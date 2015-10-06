package mapleleafstrings.mapleleafapp;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.Multipart;
import javax.activation.DataHandler;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import java.util.Properties;

import android.service.textservice.SpellCheckerService;
import android.util.Log;

// Another attempt at sending automated emails. Move this to EmailSender if successful
public class EmailSender2Test {
    public void sendEmail() throws AddressException, MessagingException {
        String host = "mail.steveseifried.com";
        String address = "christian@steveseifried.com";

        String from = "christian@steveseifried.com";
        String pass = "stevewrote98";
        String to = "christian@steveseifried.com";

        Multipart multipart;
        String finalString = "";

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", address);
        props.put("mail.smtp.connectiontimeout", "30");
        props.put("mail.smtp.timeout", "30");
        props.put("mail.smtp.writetimeout", "30");
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        Log.i("Check", "done pops");
        Session session = Session.getDefaultInstance(props, null);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(
                finalString.getBytes(), "text/plain"));
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));
        message.setDataHandler(handler);
        Log.i("Check", "done sessions");

        multipart = new MimeMultipart();

        InternetAddress toAddress;
        toAddress = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        Log.i("Check", "added recipient");
        message.setSubject("Send Auto-Mail");
        message.setContent(multipart);
        message.setText("Demo For Sending Mail in Android Automatically");

        Log.i("check", "transport");
        Transport transport = session.getTransport("smtp");
        Log.i("check", "connecting");
        transport.connect(host, address, pass);
        Log.i("check", "attempting send");
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();

        Log.i("check", "send successfully attempted");
    }
}
