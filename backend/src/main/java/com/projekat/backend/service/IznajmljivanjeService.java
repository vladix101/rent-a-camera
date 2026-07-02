package com.projekat.backend.service;

import com.projekat.backend.dto.IznajmljivanjeDto;
import com.projekat.backend.dto.IznajmljivanjeRequestDto;
import com.projekat.backend.entity.Fotoaparat;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.entity.Specifikcija;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.FotoaparatRepository;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import com.projekat.backend.repository.KlijentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IznajmljivanjeService {

    private final IznajmljivanjeRepository iznajmljivanjeRepository;
    private final FotoaparatRepository fotoaparatRepository;
    private final KlijentRepository klijentRepository;
    private final EmailService emailService;
    private final PdfService pdfService;

    @Transactional
    public IznajmljivanjeDto createIznajmljivanje(Long klijentId, IznajmljivanjeRequestDto requestDto) {
        validatePayment(requestDto);

        Fotoaparat fotoaparat = fotoaparatRepository.findById(requestDto.getFotoaparatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fotoaparat nije pronađen"));

        if (!Boolean.TRUE.equals(fotoaparat.getDostupan())) {
            throwValidationError("fotoaparatId", "Fotoaparat trenutno nije u ponudi");
        }

        boolean zauzet = iznajmljivanjeRepository.existsByFotoaparatIdAndDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(
                fotoaparat.getId(), requestDto.getDatumDo(), requestDto.getDatumOd());
        if (zauzet) {
            throwValidationError("datumOd", "Izabrani period više nije slobodan za ovaj fotoaparat");
        }

        Klijent klijent = klijentRepository.findById(klijentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Klijent nije pronađen"));

        Iznajmljivanje iznajmljivanje = new Iznajmljivanje();
        iznajmljivanje.setDatumPocetka(requestDto.getDatumOd());
        iznajmljivanje.setDatumKraja(requestDto.getDatumDo());
        iznajmljivanje.setFotoaparat(fotoaparat);
        iznajmljivanje.setKlijent(klijent);
        iznajmljivanje = iznajmljivanjeRepository.save(iznajmljivanje);

        sendConfirmation(klijent, fotoaparat, iznajmljivanje);

        return toDto(iznajmljivanje, fotoaparat);
    }

    @Transactional(readOnly = true)
    public List<IznajmljivanjeDto> getMojaIznajmljivanja(Long klijentId) {
        return iznajmljivanjeRepository.findByKlijentId(klijentId)
                .stream()
                .map(iznajmljivanje -> toDto(iznajmljivanje, iznajmljivanje.getFotoaparat()))
                .toList();
    }

    private void validatePayment(IznajmljivanjeRequestDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getFotoaparatId() == null) {
            fieldErrors.put("fotoaparatId", "Fotoaparat je obavezan");
        }
        if (requestDto.getDatumOd() == null || requestDto.getDatumDo() == null) {
            fieldErrors.put("datumOd", "Period iznajmljivanja je obavezan");
        } else if (!requestDto.getDatumDo().isAfter(requestDto.getDatumOd())) {
            fieldErrors.put("datumDo", "Datum završetka mora biti posle datuma početka");
        }

        String brojKartice = requestDto.getBrojKartice() == null ? "" : requestDto.getBrojKartice().replaceAll("\\s+", "");
        if (brojKartice.isBlank() || !brojKartice.matches("\\d{12,19}")) {
            fieldErrors.put("brojKartice", "Broj kartice nije validan");
        }

        if (requestDto.getDatumIstekaKartice() == null || requestDto.getDatumIstekaKartice().isBlank()) {
            fieldErrors.put("datumIstekaKartice", "Datum isteka kartice je obavezan");
        }

        String cvc = requestDto.getCvc() == null ? "" : requestDto.getCvc().trim();
        if (!cvc.matches("\\d{3,4}")) {
            fieldErrors.put("cvc", "CVC nije validan");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private void sendConfirmation(Klijent klijent, Fotoaparat fotoaparat, Iznajmljivanje iznajmljivanje) {
        if (klijent.getEmail() == null || klijent.getEmail().isBlank()) {
            return;
        }

        try {
            byte[] pdf = pdfService.generateRentalConfirmationPdf(klijent, fotoaparat, iznajmljivanje);
            String htmlBody = """
                    <div style="font-family: Arial, sans-serif; color: #1e1b4b; line-height: 1.6; padding: 20px;">
                        <h2 style="margin: 0 0 12px;">Iznajmljivanje je uspešno potvrđeno</h2>
                        <p style="margin: 0;">PDF potvrda vašeg iznajmljivanja je u prilogu ovog email-a.</p>
                    </div>
                    """;
            emailService.sendRentalConfirmationEmail(klijent.getEmail(), "Potvrda iznajmljivanja", htmlBody, pdf, "potvrda-iznajmljivanja.pdf");
        } catch (ResponseStatusException | MailException exception) {
            // Iznajmljivanje je već uspešno kreirano; neuspeh slanja email potvrde ne sme da poništi rezervaciju.
        }
    }

    private void throwValidationError(String field, String message) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        fieldErrors.put(field, message);
        throw new ValidationException(fieldErrors);
    }

    private IznajmljivanjeDto toDto(Iznajmljivanje iznajmljivanje, Fotoaparat fotoaparat) {
        Specifikcija specifikcija = fotoaparat.getSpecifikcije().isEmpty() ? null : fotoaparat.getSpecifikcije().get(0);

        return new IznajmljivanjeDto(
                iznajmljivanje.getId(),
                iznajmljivanje.getDatumPocetka(),
                iznajmljivanje.getDatumKraja(),
                iznajmljivanje.getCena(),
                fotoaparat.getId(),
                fotoaparat.getProizvodjac() == null ? null : fotoaparat.getProizvodjac().getName(),
                specifikcija == null || specifikcija.getKategorija() == null ? null : specifikcija.getKategorija().getNaziv(),
                specifikcija == null ? null : specifikcija.getRezolucija(),
                specifikcija == null ? null : specifikcija.getOpis()
        );
    }
}
