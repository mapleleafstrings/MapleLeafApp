package mapleleafstrings.mapleleafapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Boler on 10/14/2015.
 */
public class updateSQLDatabase extends AsyncTask<String, Void, String> {

    private Context context;

    public updateSQLDatabase(Context context) {
        this.context = context;
    }

    protected void onPreExecute(){
    }

    @Override
    protected String doInBackground(String... args){
        try {
            //String username = "elecban_steve";
            //String password = "D4t4b4se";
            String username = (String)args[0];
            String password = (String)args[1];

            String link = "http://steveseifried.com/mls/returnApp/manualReturnSubmit.php";
            String data = URLEncoder.encode("username", "UTF-8")
                    + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8")
                    + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null){
                sb.append(line);
                break;
            }
            reader.close();

            String message = sb.toString();

            return sb.toString();

        } catch (IOException e) {
            String error = "Error: " + e.getMessage();
            e.printStackTrace();

            return error;
        }
    }

    @Override
    protected void onPostExecute(String result){

        // Display Server Response
        CharSequence text = "Login Successful";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        //toast.show();
    }
}
