package mapleleafstrings.mapleleafapp;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

/**
 * ============================== EmailSender.java =====================================
 *  Contains all the technical setup work required to send an email without any user
 *  input from the app. Lots of confusing stuff here that works just because the Apache
 *  Javamail API says it does.
 * ==================== Created by Christian Boler on 10/5/2015. =======================
 */
public class EmailSender {
    private static final String SMTP_HOST_NAME = "mail.steveseifried.com";
    private static final String SMTP_AUTH_USER = "christian@steveseifried.com";
    private static final String SMTP_AUTH_PWD  = "stevewrote98";

    public static void main(String[] args) throws Exception{
        new EmailSender().sendEmail();
    }

    public void sendEmail() throws Exception{
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");

        Authenticator auth = new SMTPAuthenticator();
        Session mailSession = Session.getDefaultInstance(props, auth);
        // uncomment for debugging infos to stdout
        // mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);

        Multipart multipart = new MimeMultipart("alternative");

        BodyPart part1 = new MimeBodyPart();
        part1.setText("Checking to see what box this mail goes in ?");

        BodyPart part2 = new MimeBodyPart();
        part2.setContent("Checking to see what box this mail goes in ?", "text/html");

        multipart.addBodyPart(part1);
        multipart.addBodyPart(part2);

        message.setContent(multipart);
        message.setFrom(new InternetAddress("christian@steveseifried.com"));
        message.setSubject("Can you see this mail ?");
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress("christian@steveseifried.com"));

        transport.connect();
        transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }
}
