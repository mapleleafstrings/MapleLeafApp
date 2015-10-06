package mapleleafstrings.mapleleafapp;

import android.os.AsyncTask;
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
        try{
            EmailSender2Test sender = new EmailSender2Test();
            sender.sendEmail();
        } catch (Exception e) {
            //TODO: Populate this exception
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        //TODO: Write Progress Update Code
    }

    protected void onPostExecute(Long result) {
        //TODO: Write Post Execute Code
    }
}
