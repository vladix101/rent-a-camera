package com.projekat.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.projekat.backend.dto.EmployeeDashboardStatsDto;
import com.projekat.backend.dto.IznajmljivanjeDto;
import com.projekat.backend.dto.IznajmljivanjeRequestDto;
import com.projekat.backend.dto.RentalStatsDto;
import com.projekat.backend.entity.Fotoaparat;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.entity.Specifikacija;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.FotoaparatRepository;
import com.projekat.backend.repository.IznajmljivanjeRepository;
import com.projekat.backend.repository.KlijentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IznajmljivanjeService {

    private final IznajmljivanjeRepository iznajmljivanjeRepository;
    private final FotoaparatRepository fotoaparatRepository;
    private final KlijentRepository klijentRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String mailSenderAddress;

    @Value("${app.name}")
    private String appName;

    private static final DateTimeFormatter PDF_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

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

    @Transactional(readOnly = true)
    public EmployeeDashboardStatsDto getStatistika(LocalDate datumOd, LocalDate datumDo) {
        List<Iznajmljivanje> sveIznajmljivanja = iznajmljivanjeRepository.findAll();

        Map<Long, Long> brojPoFotoaparatId = new LinkedHashMap<>();
        Map<Long, Fotoaparat> fotoaparatPoId = new LinkedHashMap<>();
        for (Iznajmljivanje iznajmljivanje : sveIznajmljivanja) {
            Fotoaparat fotoaparat = iznajmljivanje.getFotoaparat();
            brojPoFotoaparatId.merge(fotoaparat.getId(), 1L, Long::sum);
            fotoaparatPoId.putIfAbsent(fotoaparat.getId(), fotoaparat);
        }

        long ukupnoIznajmljivanja = sveIznajmljivanja.size();
        List<RentalStatsDto> statistikaPoFotoaparatu = brojPoFotoaparatId.entrySet()
                .stream()
                .map(entry -> new RentalStatsDto(
                        nazivZaFotoaparat(fotoaparatPoId.get(entry.getKey())),
                        entry.getValue(),
                        ukupnoIznajmljivanja == 0 ? 0.0 : Math.round(entry.getValue() * 10000.0 / ukupnoIznajmljivanja) / 100.0
                ))
                .sorted(Comparator.comparing(RentalStatsDto::getBrojIznajmljivanja).reversed())
                .toList();

        String najpopularniji = statistikaPoFotoaparatu.isEmpty() ? null : statistikaPoFotoaparatu.get(0).getNazivFotoaparata();

        long brojDostupnih;
        long brojNedostupnih;
        if (datumOd != null && datumDo != null) {
            Set<Long> zauzetiIds = iznajmljivanjeRepository
                    .findByDatumPocetkaLessThanEqualAndDatumKrajaGreaterThanEqual(datumDo, datumOd)
                    .stream()
                    .map(iznajmljivanje -> iznajmljivanje.getFotoaparat().getId())
                    .collect(Collectors.toSet());
            long ukupnoDostupnihUPonudi = fotoaparatRepository.countByDostupanTrue();
            brojDostupnih = fotoaparatRepository.findAll().stream()
                    .filter(fotoaparat -> Boolean.TRUE.equals(fotoaparat.getDostupan()) && !zauzetiIds.contains(fotoaparat.getId()))
                    .count();
            brojNedostupnih = fotoaparatRepository.count() - brojDostupnih;
            if (brojDostupnih > ukupnoDostupnihUPonudi) {
                brojDostupnih = ukupnoDostupnihUPonudi;
            }
        } else {
            brojDostupnih = fotoaparatRepository.countByDostupanTrue();
            brojNedostupnih = fotoaparatRepository.countByDostupanFalse();
        }

        return new EmployeeDashboardStatsDto(
                ukupnoIznajmljivanja,
                klijentRepository.count(),
                fotoaparatRepository.count(),
                brojDostupnih,
                brojNedostupnih,
                najpopularniji,
                statistikaPoFotoaparatu
        );
    }

    private String nazivZaFotoaparat(Fotoaparat fotoaparat) {
        if (fotoaparat == null) {
            return "Nepoznat fotoaparat";
        }
        Specifikacija specifikacija = fotoaparat.getSpecifikacija();
        String proizvodjacNaziv = fotoaparat.getProizvodjac() == null ? "Fotoaparat" : fotoaparat.getProizvodjac().getName();
        return specifikacija != null && specifikacija.getRezolucija() != null
                ? proizvodjacNaziv + " · " + specifikacija.getRezolucija()
                : proizvodjacNaziv;
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
            byte[] pdf = generateRentalConfirmationPdf(klijent, fotoaparat, iznajmljivanje);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailSenderAddress);
            helper.setTo(klijent.getEmail());
            helper.setSubject("Potvrda iznajmljivanja");
            helper.setText("""
                    <div style="font-family: Arial, sans-serif; color: #1e1b4b; line-height: 1.6; padding: 20px;">
                        <h2 style="margin: 0 0 12px;">Iznajmljivanje je uspešno potvrđeno</h2>
                        <p style="margin: 0;">PDF potvrda vašeg iznajmljivanja je u prilogu ovog email-a.</p>
                    </div>
                    """, true);
            helper.addAttachment("potvrda-iznajmljivanja.pdf", () -> new ByteArrayInputStream(pdf), "application/pdf");
            javaMailSender.send(message);
        } catch (MessagingException | MailException | DocumentException exception) {
            // Iznajmljivanje je već uspešno kreirano; neuspeh slanja email potvrde ne sme da poništi rezervaciju.
        }
    }

    private byte[] generateRentalConfirmationPdf(Klijent klijent, Fotoaparat fotoaparat, Iznajmljivanje iznajmljivanje) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 48, 48, 54, 48);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Specifikacija specifikacija = fotoaparat.getSpecifikacija();
        String proizvodjacNaziv = fotoaparat.getProizvodjac() == null ? "Fotoaparat" : fotoaparat.getProizvodjac().getName();
        String modelNaziv = proizvodjacNaziv + (specifikacija != null && specifikacija.getRezolucija() != null ? " · " + specifikacija.getRezolucija() : "");

        Paragraph title = new Paragraph(appName, titleFont);
        title.setSpacingAfter(4);
        document.add(title);

        Paragraph subtitle = new Paragraph("Potvrda iznajmljivanja fotoaparata", sectionFont);
        subtitle.setSpacingAfter(18);
        document.add(subtitle);

        document.add(detailLine("Klijent", klijent.getIme() + " " + klijent.getPrezime(), textFont));
        document.add(detailLine("Email", klijent.getEmail(), textFont));
        document.add(detailLine("Fotoaparat", modelNaziv, textFont));
        document.add(detailLine("Period iznajmljivanja", formatPdfDate(iznajmljivanje.getDatumPocetka()) + " - " + formatPdfDate(iznajmljivanje.getDatumKraja()), textFont));
        if (iznajmljivanje.getCena() != null) {
            document.add(detailLine("Cena", iznajmljivanje.getCena() + " EUR", textFont));
        }
        document.add(detailLine("Datum kreiranja potvrde", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")), textFont));

        Paragraph footer = new Paragraph("Vaše iznajmljivanje je uspešno potvrđeno. Hvala što ste izabrali " + appName + "!", textFont);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();
        return outputStream.toByteArray();
    }

    private Paragraph detailLine(String label, String value, Font textFont) {
        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingBefore(7);
        paragraph.add(new Phrase(label + ": ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        paragraph.add(new Phrase(value == null || value.isBlank() ? "-" : value, textFont));
        return paragraph;
    }

    private String formatPdfDate(LocalDate date) {
        return date == null ? "-" : date.format(PDF_DATE_FORMATTER);
    }

    private void throwValidationError(String field, String message) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        fieldErrors.put(field, message);
        throw new ValidationException(fieldErrors);
    }

    private IznajmljivanjeDto toDto(Iznajmljivanje iznajmljivanje, Fotoaparat fotoaparat) {
        Specifikacija specifikacija = fotoaparat.getSpecifikacija();

        return new IznajmljivanjeDto(
                iznajmljivanje.getId(),
                iznajmljivanje.getDatumPocetka(),
                iznajmljivanje.getDatumKraja(),
                iznajmljivanje.getCena(),
                fotoaparat.getId(),
                fotoaparat.getProizvodjac() == null ? null : fotoaparat.getProizvodjac().getName(),
                fotoaparat.getKategorija() == null ? null : fotoaparat.getKategorija().getNaziv(),
                specifikacija == null ? null : specifikacija.getRezolucija(),
                specifikacija == null ? null : specifikacija.getOpis()
        );
    }
}
