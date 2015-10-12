package mapleleafstrings.mapleleafapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * ========================= RetrieveBackgroundTask.java ===============================
 *  Creates a seperate thread to run a task in its own process (basically creates a
 *  new thread); mainly done because android disallows network functions on the main
 *  thread.
 * ==================== Created by Christian Boler on 10/5/2015. =======================
 */
public class RetrieveBackgroundTask extends AsyncTask<String, Integer, Long> {

    private Exception exception;

    @Override
    protected Long doInBackground(String... urls){

        Mail m = new Mail("christian@steveseifried.com", "stevewrote98");

        String[] toArr = {"christian@steveseifried.com"};
        m.setTo(toArr);
        m.setFrom("returns@mapleleafstrings.com");
        m.setSubject("Sample Return Email");
        m.setBody("Email body.");

        try {
            if(m.send()) {
                Log.e("MailApp", "Mail Sent Successfully");
            } else {
                Log.e("MailApp", "Email was not sent.");
            }
        } catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("MailApp", "Could not send email; ", e);
        }

        return null;
    }

    protected Boolean sendMailInBackground(){
        Mail m = new Mail("christian@steveseifried.com", "stevewrote98");

        String[] toArr = {"christian@steveseifried.com"};
        m.setTo(toArr);
        m.setFrom("returns@mapleleafstrings.com");
        m.setSubject("Sample Return Email");
        m.setBody("Email body.");

        try {
            if(m.send()) {
                Log.e("MailApp", "Mail Sent Successfully");
                return true;
            } else {
                Log.e("MailApp", "Email was not sent.");
                return false;
            }
        } catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("MailApp", "Could not send email; ", e);
            return false;
        }
    }

    protected void onProgressUpdate(Integer... progress) {
        // Placeholder comment
    }

    protected void onPostExecute(Long result) {
        // Placeholder comment
    }
}
