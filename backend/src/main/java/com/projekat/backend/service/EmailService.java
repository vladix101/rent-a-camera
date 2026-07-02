package com.projekat.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String mailSenderAddress;

    @Value("${app.name}")
    private String appName;

    public void sendVerificationEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailSenderAddress);
        message.setTo(email);
        message.setSubject(appName + " - verifikacioni kod");
        message.setText("Vaš verifikacioni kod je: " + code + "\n\nKod ističe za 10 minuta.");

        try {
            javaMailSender.send(message);
        } catch (MailException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Verifikacioni email nije mogao biti poslat. Proverite mail konfiguraciju.");
        }
    }

    public void sendRentalConfirmationEmail(String email, String subject, String htmlBody, byte[] pdfAttachment, String attachmentFileName) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailSenderAddress);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.addAttachment(attachmentFileName, () -> new ByteArrayInputStream(pdfAttachment), "application/pdf");
            javaMailSender.send(message);
        } catch (MessagingException | MailException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Potvrda iznajmljivanja nije mogla biti poslata na email");
        }
    }
}
