package fi.muni.courtreservation.registration.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Radim Stejskal 514102
 */
@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private final static Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    /**
     *
     * @param to Recipient's email address
     * @param email Message to be sent to the recipient
     */
    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Email confirmation");
            helper.setFrom("myjavatenniscourts@gmail.com");
            mailSender.send(mimeMessage);
        }catch (MessagingException e){
            logger.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
