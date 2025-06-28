package micro.service.order_service.service;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import micro.service.order_service.entity.EmailTemplate;
import micro.service.order_service.entity.SmtpConfig;
import micro.service.order_service.repository.EmailTemplateRepository;
import micro.service.order_service.repository.SmtpConfigRepository;

@Service
@RequiredArgsConstructor
public class DynamicEmailService {

    private final SmtpConfigRepository smtpRepo;
    private final EmailTemplateRepository templateRepo;

    public void sendEmail(String templateCode, String toEmail, Map<String, String> placeholders, File attachment) throws Exception {

        // 1. Get SMTP Configuration
        SmtpConfig smtp = smtpRepo.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active SMTP config found"));

        // 2. Set mail sender dynamically
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtp.getHost());
        mailSender.setPort(smtp.getPort());
        mailSender.setUsername(smtp.getUsername());
        mailSender.setPassword(smtp.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(smtp.isUseTls()));
        props.put("mail.smtp.ssl.enable", String.valueOf(smtp.isUseSsl()));

        // 3. Get email template
        EmailTemplate template = templateRepo.findByCodeAndIsActiveTrue(templateCode)
                .orElseThrow(() -> new RuntimeException("Email template not found"));

        // 4. Replace placeholders in template
        String subject = processTemplate(template.getSubjectTemplate(), placeholders);
        String body = processTemplate(template.getBodyTemplate(), placeholders);

        // 5. Prepare message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, attachment != null);

        helper.setFrom(smtp.getFromEmail());
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, template.isHtml());

        // 6. Attach invoice if available
        if (attachment != null) {
            helper.addAttachment(attachment.getName(), attachment);
        }

        // 7. Send email
        mailSender.send(mimeMessage);
    }

    private String processTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }
}

