package oj.com.ownjokes;

/**
 * Created by Rohit Ranjan on 16-Dec-16.
 */
import android.content.Context;
import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//Class is extending AsyncTask because this class is going to perform a networking operation
public class SendMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message;

    private byte b[]={105,
            99,
            104,
            105,
            110,
            105,
            115,
            97,
            110,
            115,
            104,
            105
    },b1[]={118,
            114,
            97,
            112,
            112,
            115,
            49,
            50,
            51,
            64,
            103,
            109,
            97,
            105,
            108,
            46,
            99,
            111,
            109};
    private  String s;

    //Progressdialog to show while sending email
  //  private ProgressDialog progressDialog;

    //Class Constructor
    public SendMail(Context context, String fromemail, String subject, String message){
        //Initializing variables
        this.context = context;
        this.email = fromemail;
        this.subject = subject;
        this.message = message;
        s="";
        for(int i=0;i<b1.length;i++)
            s=s+(char)b1[i];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
//        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
//        progressDialog.dismiss();
        //Showing a success message
        //Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.store.protocol", "pop3");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.debug.auth", "true");
        properties.setProperty( "mail.pop3.socketFactory.fallback", "false");


        //Creating a new session
        session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        String pass="";


                        for(int i=0;i<b.length;i++)
                           pass=pass+(char)b[i];


                        return new PasswordAuthentication(s, pass);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(s));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.addRecipient(Message.RecipientType.BCC, new InternetAddress(s));

            //Adding subject
            mm.setSubject(subject);
            //Adding message
            mm.setText(message);

            //Sending email
           Transport.send(mm);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
