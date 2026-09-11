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
import com.projekat.backend.dto.RentalDto;
import com.projekat.backend.dto.RentalRequestDto;
import com.projekat.backend.dto.RentalStatsDto;
import com.projekat.backend.entity.Camera;
import com.projekat.backend.entity.Rental;
import com.projekat.backend.entity.Client;
import com.projekat.backend.entity.Specification;
import com.projekat.backend.exception.ValidationException;
import com.projekat.backend.repository.CameraRepository;
import com.projekat.backend.repository.RentalRepository;
import com.projekat.backend.repository.ClientRepository;
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
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CameraRepository cameraRepository;
    private final ClientRepository clientRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String mailSenderAddress;

    @Value("${app.name}")
    private String appName;

    private static final DateTimeFormatter PDF_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    @Transactional
    public RentalDto createRental(Long clientId, RentalRequestDto requestDto) {
        validatePayment(requestDto);

        Camera camera = cameraRepository.findById(requestDto.getCameraId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Camera not found"));

        if (!Boolean.TRUE.equals(camera.getAvailable())) {
            throwValidationError("cameraId", "This camera is currently not available for rent");
        }

        boolean occupied = rentalRepository.existsByCameraIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                camera.getId(), requestDto.getDateTo(), requestDto.getDateFrom());
        if (occupied) {
            throwValidationError("dateFrom", "The selected period is no longer available for this camera");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Rental rental = new Rental();
        rental.setStartDate(requestDto.getDateFrom());
        rental.setEndDate(requestDto.getDateTo());
        rental.setCamera(camera);
        rental.setClient(client);
        rental = rentalRepository.save(rental);

        sendConfirmation(client, camera, rental);

        return toDto(rental, camera);
    }

    @Transactional(readOnly = true)
    public List<RentalDto> getMyRentals(Long clientId) {
        return rentalRepository.findByClientId(clientId)
                .stream()
                .map(rental -> toDto(rental, rental.getCamera()))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDashboardStatsDto getStats(LocalDate dateFrom, LocalDate dateTo) {
        List<Rental> allRentals = rentalRepository.findAll();

        Map<Long, Long> countByCameraId = new LinkedHashMap<>();
        Map<Long, Camera> cameraById = new LinkedHashMap<>();
        for (Rental rental : allRentals) {
            Camera camera = rental.getCamera();
            countByCameraId.merge(camera.getId(), 1L, Long::sum);
            cameraById.putIfAbsent(camera.getId(), camera);
        }

        long totalRentalCount = allRentals.size();
        List<RentalStatsDto> statsPerCamera = countByCameraId.entrySet()
                .stream()
                .map(entry -> new RentalStatsDto(
                        cameraDisplayName(cameraById.get(entry.getKey())),
                        entry.getValue(),
                        totalRentalCount == 0 ? 0.0 : Math.round(entry.getValue() * 10000.0 / totalRentalCount) / 100.0
                ))
                .sorted(Comparator.comparing(RentalStatsDto::getRentalCount).reversed())
                .toList();

        String mostPopular = statsPerCamera.isEmpty() ? null : statsPerCamera.get(0).getCameraName();

        long availableCount;
        long unavailableCount;
        if (dateFrom != null && dateTo != null) {
            Set<Long> occupiedIds = rentalRepository
                    .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(dateTo, dateFrom)
                    .stream()
                    .map(rental -> rental.getCamera().getId())
                    .collect(Collectors.toSet());
            long totalListedAsAvailable = cameraRepository.countByAvailableTrue();
            availableCount = cameraRepository.findAll().stream()
                    .filter(camera -> Boolean.TRUE.equals(camera.getAvailable()) && !occupiedIds.contains(camera.getId()))
                    .count();
            unavailableCount = cameraRepository.count() - availableCount;
            if (availableCount > totalListedAsAvailable) {
                availableCount = totalListedAsAvailable;
            }
        } else {
            availableCount = cameraRepository.countByAvailableTrue();
            unavailableCount = cameraRepository.countByAvailableFalse();
        }

        return new EmployeeDashboardStatsDto(
                totalRentalCount,
                clientRepository.count(),
                cameraRepository.count(),
                availableCount,
                unavailableCount,
                mostPopular,
                statsPerCamera
        );
    }

    private String cameraDisplayName(Camera camera) {
        if (camera == null) {
            return "Unknown camera";
        }
        Specification specification = camera.getSpecification();
        String manufacturerName = camera.getManufacturer() == null ? "Camera" : camera.getManufacturer().getName();
        return specification != null && specification.getResolution() != null
                ? manufacturerName + " · " + specification.getResolution()
                : manufacturerName;
    }

    private void validatePayment(RentalRequestDto requestDto) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (requestDto.getCameraId() == null) {
            fieldErrors.put("cameraId", "Camera is required");
        }
        if (requestDto.getDateFrom() == null || requestDto.getDateTo() == null) {
            fieldErrors.put("dateFrom", "Rental period is required");
        } else if (!requestDto.getDateTo().isAfter(requestDto.getDateFrom())) {
            fieldErrors.put("dateTo", "End date must be after the start date");
        }

        String cardNumber = requestDto.getCardNumber() == null ? "" : requestDto.getCardNumber().replaceAll("\\s+", "");
        if (cardNumber.isBlank() || !cardNumber.matches("\\d{12,19}")) {
            fieldErrors.put("cardNumber", "Card number is not valid");
        }

        if (requestDto.getCardExpiry() == null || requestDto.getCardExpiry().isBlank()) {
            fieldErrors.put("cardExpiry", "Card expiry date is required");
        }

        String cvc = requestDto.getCvc() == null ? "" : requestDto.getCvc().trim();
        if (!cvc.matches("\\d{3,4}")) {
            fieldErrors.put("cvc", "CVC is not valid");
        }

        if (!fieldErrors.isEmpty()) {
            throw new ValidationException(fieldErrors);
        }
    }

    private void sendConfirmation(Client client, Camera camera, Rental rental) {
        if (client.getEmail() == null || client.getEmail().isBlank()) {
            return;
        }

        try {
            byte[] pdf = generateRentalConfirmationPdf(client, camera, rental);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailSenderAddress);
            helper.setTo(client.getEmail());
            helper.setSubject("Rental confirmation");
            helper.setText("""
                    <div style="font-family: Arial, sans-serif; color: #1e1b4b; line-height: 1.6; padding: 20px;">
                        <h2 style="margin: 0 0 12px;">Your rental is confirmed</h2>
                        <p style="margin: 0;">The PDF confirmation of your rental is attached to this email.</p>
                    </div>
                    """, true);
            helper.addAttachment("potvrda-rentals.pdf", () -> new ByteArrayInputStream(pdf), "application/pdf");
            javaMailSender.send(message);
        } catch (MessagingException | MailException | DocumentException exception) {
            // The rental is already created; a failure to send the confirmation email must not void the booking.
        }
    }

    private byte[] generateRentalConfirmationPdf(Client client, Camera camera, Rental rental) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 48, 48, 54, 48);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Specification specification = camera.getSpecification();
        String manufacturerName = camera.getManufacturer() == null ? "Camera" : camera.getManufacturer().getName();
        String modelName = manufacturerName + (specification != null && specification.getResolution() != null ? " · " + specification.getResolution() : "");

        Paragraph title = new Paragraph(appName, titleFont);
        title.setSpacingAfter(4);
        document.add(title);

        Paragraph subtitle = new Paragraph("Camera rental confirmation", sectionFont);
        subtitle.setSpacingAfter(18);
        document.add(subtitle);

        document.add(detailLine("Client", client.getFirstName() + " " + client.getLastName(), textFont));
        document.add(detailLine("Email", client.getEmail(), textFont));
        document.add(detailLine("Camera", modelName, textFont));
        document.add(detailLine("Rental period", formatPdfDate(rental.getStartDate()) + " - " + formatPdfDate(rental.getEndDate()), textFont));
        if (rental.getPrice() != null) {
            document.add(detailLine("Price", rental.getPrice() + " EUR", textFont));
        }
        document.add(detailLine("Confirmation issued", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")), textFont));

        Paragraph footer = new Paragraph("Your rental has been confirmed. Thank you for choosing " + appName + "!", textFont);
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

    private RentalDto toDto(Rental rental, Camera camera) {
        Specification specification = camera.getSpecification();

        return new RentalDto(
                rental.getId(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getPrice(),
                camera.getId(),
                camera.getManufacturer() == null ? null : camera.getManufacturer().getName(),
                camera.getCategory() == null ? null : camera.getCategory().getName(),
                specification == null ? null : specification.getResolution(),
                specification == null ? null : specification.getDescription()
        );
    }
}
