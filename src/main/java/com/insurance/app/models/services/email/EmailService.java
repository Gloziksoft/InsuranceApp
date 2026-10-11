package com.insurance.app.models.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String mailFrom;

    public void sendPasswordResetEmail(
            String email,
            String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(mailFrom);
        message.setTo(email);
        message.setSubject("Obnovenie hesla");
        message.setText(
                "Dobrý deň,\n\n"
                        + "požiadali ste o obnovenie hesla.\n\n"
                        + "Pre nastavenie nového hesla kliknite na nasledujúci odkaz:\n"
                        + resetLink
                        + "\n\n"
                        + "Odkaz je platný 15 minút.\n\n"
                        + "Ak ste o obnovenie hesla nepožiadali, tento e-mail môžete ignorovať.\n\n"
                        + "Insurance App"
        );

        mailSender.send(message);
    }
}