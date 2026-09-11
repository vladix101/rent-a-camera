package com.projekat.backend.service;

import com.projekat.backend.dto.ClientDto;
import com.projekat.backend.dto.ClientRegistrationDto;
import com.projekat.backend.dto.ClientUpdateDto;
import com.projekat.backend.dto.ClientVerificationDto;
import com.projekat.backend.dto.LoginRequestDto;
import com.projekat.backend.dto.LoginResponseDto;
import com.projekat.backend.dto.MessageResponseDto;
import com.projekat.backend.entity.Client;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.RentalRepository;
import com.projekat.backend.repository.ClientRepository;
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
public class ClientService {

    private final ClientRepository clientRepository;
    private final RentalRepository rentalRepository;
    private final JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    private final ClientService self;

    @Value("${spring.mail.username:}")
    private String mailSenderAddress;

    @Value("${app.name}")
    private String appName;

    public ClientService(ClientRepository clientRepository, RentalRepository rentalRepository,
                           JavaMailSender javaMailSender, JwtUtil jwtUtil, @Lazy ClientService self) {
        this.clientRepository = clientRepository;
        this.rentalRepository = rentalRepository;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;
        this.self = self;
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final Map<String, VerificationCodeData> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, ClientRegistrationDto> pendingRegistrations = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public List<ClientDto> getClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Client client = clientRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password"));

        if (!client.getPassword().equals(loginRequestDto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }

        String token = jwtUtil.generateToken(client.getId(), client.getUsername(), client.getFirstName(), client.getLastName(), "CLIENT");
        return new LoginResponseDto(client.getId(), client.getFirstName(), client.getLastName(), client.getUsername(), "CLIENT", token);
    }

    public MessageResponseDto startRegistration(ClientRegistrationDto registrationDto) {
        if (registrationDto == null) {
            throwValidationError("form", "Registration data is missing");
        }
        validateRegistration(registrationDto.getUsername(), registrationDto.getPassword(), registrationDto.getEmail());
        sendVerificationCode(registrationDto.getEmail());
        pendingRegistrations.put(normalizeEmail(registrationDto.getEmail()), registrationDto);
        return new MessageResponseDto("Verification code sent successfully", registrationDto.getEmail());
    }

    @Transactional
    public ClientDto verifyRegistration(ClientVerificationDto verificationDto) {
        if (verificationDto == null || verificationDto.getClient() == null) {
            throwValidationError("form", "Registration data is missing");
        }

        String verificationEmail = verificationDto.getEmail() != null && !verificationDto.getEmail().isBlank()
                ? verificationDto.getEmail()
                : verificationDto.getClient().getEmail();

        ClientRegistrationDto registrationDto = pendingRegistrations.getOrDefault(
                normalizeEmail(verificationEmail),
                verificationDto.getClient()
        );
        registrationDto.setEmail(verificationEmail);
        validateVerificationCode(verificationEmail, verificationDto.getCode());
        validateRegistration(registrationDto.getUsername(), registrationDto.getPassword(), registrationDto.getEmail());

        Client client = new Client();
        client.setFirstName(registrationDto.getFirstName());
        client.setLastName(registrationDto.getLastName());
        client.setAge(registrationDto.getAge());
        client.setUsername(registrationDto.getUsername());
        client.setPassword(registrationDto.getPassword());
        client.setEmail(registrationDto.getEmail());
        client = clientRepository.save(client);

        removeVerificationCode(verificationEmail);
        pendingRegistrations.remove(normalizeEmail(verificationEmail));

        return toDto(client);
    }

    @Transactional
    public ClientDto updateClient(Long id, ClientUpdateDto updateDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        validateUpdate(client, updateDto);

        client.setFirstName(updateDto.getFirstName());
        client.setLastName(updateDto.getLastName());
        client.setAge(updateDto.getAge());
        client.setUsername(updateDto.getUsername());
        client.setEmail(updateDto.getEmail());
        client = clientRepository.save(client);

        return toDto(client);
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        if (!rentalRepository.findByClientId(id).isEmpty()) {
            throwValidationError("form", "Client has existing rentals and cannot be deleted");
        }

        clientRepository.delete(client);
    }

    private void validateUpdate(Client existing, ClientUpdateDto updateDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (updateDto.getUsername() == null || updateDto.getUsername().isBlank()) {
            fieldErrors.put("username", "Username is required");
        } else if (!updateDto.getUsername().equals(existing.getUsername())
                && clientRepository.existsByUsername(updateDto.getUsername())) {
            fieldErrors.put("username", "Username already exists");
        }

        if (updateDto.getEmail() == null || updateDto.getEmail().isBlank()) {
            fieldErrors.put("email", "Email is required");
        } else if (!EMAIL_PATTERN.matcher(updateDto.getEmail()).matches()) {
            fieldErrors.put("email", "Email is not valid");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private void validateRegistration(String username, String password, String email) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (username == null || username.isBlank()) {
            fieldErrors.put("username", "Username is required");
        } else if (clientRepository.existsByUsername(username)) {
            fieldErrors.put("username", "Username already exists");
        }

        if (password == null || password.length() <= 6) {
            fieldErrors.put("password", "Password must be longer than 6 characters");
        }

        if (email == null || email.isBlank()) {
            fieldErrors.put("email", "Email is required");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            fieldErrors.put("email", "Email is not valid");
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
        message.setSubject(appName + " - verification code");
        message.setText("Your verification code is: " + code + "\n\nThe code expires in 10 minutes.");

        try {
            javaMailSender.send(message);
        } catch (MailException exception) {
            // Mail delivery is asynchronous and must not invalidate the verification code that was already stored.
        }
    }

    private void validateVerificationCode(String email, String code) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        String enteredCode = code == null ? "" : code.trim();

        if (!enteredCode.matches("\\d{6}")) {
            fieldErrors.put("code", "Verification code must be exactly 6 digits");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }

        String normalizedEmail = normalizeEmail(email);
        VerificationCodeData codeData = verificationCodes.get(normalizedEmail);

        if (codeData == null) {
            throwValidationError("code", "Verification code was not requested or has expired");
        }

        if (LocalDateTime.now().isAfter(codeData.expiresAt())) {
            removeVerificationCode(normalizedEmail);
            throwValidationError("code", "Verification code has expired");
        }

        if (!codeData.code().equals(enteredCode)) {
            throwValidationError("code", "Verification code is incorrect");
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

    private ClientDto toDto(Client client) {
        return new ClientDto(client.getId(), client.getFirstName(), client.getLastName(),
                client.getAge(), client.getUsername(), client.getEmail());
    }

    private record VerificationCodeData(String code, LocalDateTime expiresAt) {
    }
}
