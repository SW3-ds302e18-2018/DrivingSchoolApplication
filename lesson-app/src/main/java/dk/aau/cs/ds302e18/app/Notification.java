package dk.aau.cs.ds302e18.app;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class Notification
{

    private static String ACCESS_KEY;
    private static String SECRET_KEY;
    private static String emailUsername;
    private static String emailPassword;

    static // Creating a static constructor for one time initialize authconfig.properties att.
    {
        ResourceBundle reader = ResourceBundle.getBundle("authconfig");
        ACCESS_KEY = reader.getString("aws.accesskey");
        SECRET_KEY = reader.getString("aws.secretkey");
        emailUsername = reader.getString("mail.username");
        emailPassword = reader.getString("mail.password");
    }

    //Overloaded constructor to send a message to both a phone number and an email address.
    public Notification(String message, String phoneNumber, String emailAddress)
    {
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("DSchool") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));
        // AWS Send SMS
        AmazonSNSClient snsClient = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
        SMSMessage(snsClient, message, phoneNumber, smsAttributes);

        // Send Email
        EmailMessage(message, emailAddress);
    }

    //Overloaded constructor to send a message to only an email address.
    public Notification(String message, String emailAddress)
    {

        // Send Email
        EmailMessageToAdmin(message, emailAddress);
    }

    // Send SMS to a Phone Number
    private void SMSMessage(AmazonSNSClient snsClient,
                            String message, String phoneNumber, Map<String, MessageAttributeValue> smsAttributes)
    {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
    }

    // Send Email message to the senders email address
    private void EmailMessage(String message, String emailAddress)
    {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(prop, new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });
        try
        {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("gregardo@live.dk"));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
            mimeMessage.setSubject("Notification from Driving School");
            mimeMessage.setText("" + message);
            Transport.send(mimeMessage);
        } catch (MessagingException mex)
        {
            mex.printStackTrace();
        }
    }

    // Send Email message to the senders email address
    private void EmailMessageToAdmin(String message, String emailAddress)
    {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(prop, new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });
        try
        {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(emailAddress));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("gregardo@live.dk"));
            mimeMessage.setSubject("Notification from Driving School");
            mimeMessage.setText("" + message );
            Transport.send(mimeMessage);
        } catch (MessagingException mex)
        {
            mex.printStackTrace();
        }
    }
}