package com.projekat.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.projekat.backend.entity.Fotoaparat;
import com.projekat.backend.entity.Iznajmljivanje;
import com.projekat.backend.entity.Klijent;
import com.projekat.backend.entity.Specifikcija;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    @Value("${app.name}")
    private String appName;

    private static final DateTimeFormatter PDF_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    public byte[] generateRentalConfirmationPdf(Klijent klijent, Fotoaparat fotoaparat, Iznajmljivanje iznajmljivanje) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 48, 48, 54, 48);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            Specifikcija specifikcija = fotoaparat.getSpecifikcije().isEmpty() ? null : fotoaparat.getSpecifikcije().get(0);
            String proizvodjacNaziv = fotoaparat.getProizvodjac() == null ? "Fotoaparat" : fotoaparat.getProizvodjac().getName();
            String modelNaziv = proizvodjacNaziv + (specifikcija != null && specifikcija.getRezolucija() != null ? " · " + specifikcija.getRezolucija() : "");

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
        } catch (DocumentException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PDF potvrda nije mogla biti generisana");
        }
    }

    private Paragraph detailLine(String label, String value, Font textFont) {
        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingBefore(7);
        paragraph.add(new Phrase(label + ": ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        paragraph.add(new Phrase(value == null || value.isBlank() ? "-" : value, textFont));
        return paragraph;
    }

    private String formatPdfDate(java.time.LocalDate date) {
        return date == null ? "-" : date.format(PDF_DATE_FORMATTER);
    }
}
