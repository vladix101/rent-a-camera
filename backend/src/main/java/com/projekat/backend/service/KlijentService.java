package com.projekat.backend.service;

import com.projekat.backend.dto.KlijentDto;
import com.projekat.backend.dto.KlijentRegistrationDto;
import com.projekat.backend.dto.KlijentUpdateDto;
import com.projekat.backend.dto.KlijentVerificationDto;
import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.MessageResponseDto;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import com.projekat.backend.repository.KlijentRepository;
import com.projekat.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class KlijentService {

    private final KlijentRepository klijentRepository;
    private final IznajmljivanjeRepository iznajmljivanjeRepository;
    private final JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    private final KlijentService self;

    @Value("${spring.mail.username:}")
    private String mailSenderAddress;

    @Value("${app.name}")
    private String appName;

    public KlijentService(KlijentRepository klijentRepository, IznajmljivanjeRepository iznajmljivanjeRepository,
                           JavaMailSender javaMailSender, JwtUtil jwtUtil, @Lazy KlijentService self) {
        this.klijentRepository = klijentRepository;
        this.iznajmljivanjeRepository = iznajmljivanjeRepository;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;
        this.self = self;
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final Map<String, VerificationCodeData> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, KlijentRegistrationDto> pendingRegistrations = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public List<KlijentDto> getKlijenti() {
        return klijentRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Klijent klijent = klijentRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka"));

        if (!klijent.getPassword().equals(loginRequestDto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Pogrešno korisničko ime ili lozinka");
        }

        String token = jwtUtil.generateToken(klijent.getId(), klijent.getUsername(), klijent.getIme(), klijent.getPrezime(), "KLIJENT");
        return new LoginResponseDto(klijent.getId(), klijent.getIme(), klijent.getPrezime(), klijent.getUsername(), "KLIJENT", token);
    }

    public MessageResponseDto startRegistration(KlijentRegistrationDto registrationDto) {
        if (registrationDto == null) {
            throwValidationError("form", "Podaci za registraciju nedostaju");
        }
        validateRegistration(registrationDto.getUsername(), registrationDto.getPassword(), registrationDto.getEmail());
        sendVerificationCode(registrationDto.getEmail());
        pendingRegistrations.put(normalizeEmail(registrationDto.getEmail()), registrationDto);
        return new MessageResponseDto("Verifikacioni kod je uspešno poslat", registrationDto.getEmail());
    }

    @Transactional
    public KlijentDto verifyRegistration(KlijentVerificationDto verificationDto) {
        if (verificationDto == null || verificationDto.getKlijent() == null) {
            throwValidationError("form", "Podaci za registraciju nedostaju");
        }

        String verificationEmail = verificationDto.getEmail() != null && !verificationDto.getEmail().isBlank()
                ? verificationDto.getEmail()
                : verificationDto.getKlijent().getEmail();

        KlijentRegistrationDto registrationDto = pendingRegistrations.getOrDefault(
                normalizeEmail(verificationEmail),
                verificationDto.getKlijent()
        );
        registrationDto.setEmail(verificationEmail);
        validateVerificationCode(verificationEmail, verificationDto.getCode());
        validateRegistration(registrationDto.getUsername(), registrationDto.getPassword(), registrationDto.getEmail());

        Klijent klijent = new Klijent();
        klijent.setIme(registrationDto.getIme());
        klijent.setPrezime(registrationDto.getPrezime());
        klijent.setStarost(registrationDto.getStarost());
        klijent.setUsername(registrationDto.getUsername());
        klijent.setPassword(registrationDto.getPassword());
        klijent.setEmail(registrationDto.getEmail());
        klijent = klijentRepository.save(klijent);

        removeVerificationCode(verificationEmail);
        pendingRegistrations.remove(normalizeEmail(verificationEmail));

        return toDto(klijent);
    }

    @Transactional
    public KlijentDto updateKlijent(Long id, KlijentUpdateDto updateDto) {
        Klijent klijent = klijentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent nije pronađen"));

        validateUpdate(klijent, updateDto);

        klijent.setIme(updateDto.getIme());
        klijent.setPrezime(updateDto.getPrezime());
        klijent.setStarost(updateDto.getStarost());
        klijent.setUsername(updateDto.getUsername());
        klijent.setEmail(updateDto.getEmail());
        klijent = klijentRepository.save(klijent);

        return toDto(klijent);
    }

    @Transactional
    public void deleteKlijent(Long id) {
        Klijent klijent = klijentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent nije pronađen"));

        if (!iznajmljivanjeRepository.findByKlijentId(id).isEmpty()) {
            throwValidationError("form", "Klijent ima postojeća iznajmljivanja i ne može biti obrisan");
        }

        klijentRepository.delete(klijent);
    }

    private void validateUpdate(Klijent existing, KlijentUpdateDto updateDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (updateDto.getUsername() == null || updateDto.getUsername().isBlank()) {
            fieldErrors.put("username", "Korisničko ime je obavezno");
        } else if (!updateDto.getUsername().equals(existing.getUsername())
                && klijentRepository.existsByUsername(updateDto.getUsername())) {
            fieldErrors.put("username", "Korisničko ime već postoji");
        }

        if (updateDto.getEmail() == null || updateDto.getEmail().isBlank()) {
            fieldErrors.put("email", "Email je obavezan");
        } else if (!EMAIL_PATTERN.matcher(updateDto.getEmail()).matches()) {
            fieldErrors.put("email", "Email nije validan");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private void validateRegistration(String username, String password, String email) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (username == null || username.isBlank()) {
            fieldErrors.put("username", "Korisničko ime je obavezno");
        } else if (klijentRepository.existsByUsername(username)) {
            fieldErrors.put("username", "Korisničko ime već postoji");
        }

        if (password == null || password.length() <= 6) {
            fieldErrors.put("password", "Lozinka mora imati više od 6 karaktera");
        }

        if (email == null || email.isBlank()) {
            fieldErrors.put("email", "Email je obavezan");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            fieldErrors.put("email", "Email nije validan");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private synchronized void sendVerificationCode(String email) {
        String normalizedEmail = normalizeEmail(email);
        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        verificationCodes.put(normalizedEmail, new VerificationCodeData(code, expiresAt));
        self.sendVerificationEmailAsync(email, code);
    }

    @Async
    public void sendVerificationEmailAsync(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailSenderAddress);
        message.setTo(email);
        message.setSubject(appName + " - verifikacioni kod");
        message.setText("Vaš verifikacioni kod je: " + code + "\n\nKod ističe za 10 minuta.");

        try {
            javaMailSender.send(message);
        } catch (MailException exception) {
            // Slanje mejla je asinhrono i ne sme da poništi već zapisani verifikacioni kod.
        }
    }

    private void validateVerificationCode(String email, String code) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String enteredCode = code == null ? "" : code.trim();

        if (!enteredCode.matches("\\d{6}")) {
            fieldErrors.put("code", "Verifikacioni kod mora imati tačno 6 cifara");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }

        String normalizedEmail = normalizeEmail(email);
        VerificationCodeData codeData = verificationCodes.get(normalizedEmail);

        if (codeData == null) {
            throwValidationError("code", "Verifikacioni kod nije zatražen ili je istekao");
        }

        if (LocalDateTime.now().isAfter(codeData.expiresAt())) {
            removeVerificationCode(normalizedEmail);
            throwValidationError("code", "Verifikacioni kod je istekao");
        }

        if (!codeData.code().equals(enteredCode)) {
            throwValidationError("code", "Verifikacioni kod nije tačan");
        }
    }

    private void removeVerificationCode(String email) {
        verificationCodes.remove(normalizeEmail(email));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private void throwValidationError(String field, String message) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        fieldErrors.put(field, message);
        throw new ValidationException(fieldErrors);
    }

    private KlijentDto toDto(Klijent klijent) {
        return new KlijentDto(klijent.getId(), klijent.getIme(), klijent.getPrezime(),
                klijent.getStarost(), klijent.getUsername(), klijent.getEmail());
    }

    private record VerificationCodeData(String code, LocalDateTime expiresAt) {
    }
}
