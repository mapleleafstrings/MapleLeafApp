package mapleleafstrings.mapleleafapp;

import android.os.AsyncTask;
import android.util.Log;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

// Another email test junk class
class EmailSender3 extends AsyncTask<Void, Void, Boolean> {
    Mail m = new Mail("from@gmail.com", "my password");

    public EmailSender3() {
        if (BuildConfig.DEBUG) Log.v(EmailSender3.class.getName(), "SendEmailAsyncTask()");
        String[] toArr = {"to mail@gmail.com"};
        m.setTo(toArr);
        m.setFrom("from mail@gmail.com");
        m.setSubject("Email from Android");
        m.setBody("body.");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(EmailSender3.class.getName(), "doInBackground()");
        try {
            m.send();
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(EmailSender3.class.getName(), "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            //Log.e(EmailSender3.class.getName(), m.getTo(null) + "failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
