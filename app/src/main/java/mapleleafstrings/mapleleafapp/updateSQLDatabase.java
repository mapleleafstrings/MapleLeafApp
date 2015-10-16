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
            String[] trackingNumbers = new String[2];
            trackingNumbers[0] = "111";
            trackingNumbers[1] = "222";

            String link = "http://steveseifried.com/mls/returnApp/manualReturnSubmit.php";

            // Gather the variables to send to the php file listed above
            String data = URLEncoder.encode("recievedDate", "UTF-8")
                    + "=" + URLEncoder.encode("1/11/1111");
            data += URLEncoder.encode("recievedBy", "UTF-8")
                    + "=" + URLEncoder.encode("John Doe", "UTF-8");
            data += URLEncoder.encode("recievedFrom", "UTF-8")
                    + "=" + URLEncoder.encode("Sample Customers Inc", "UTF-8");
            data += URLEncoder.encode("carrierName", "UTF-8")
                    + "=" + URLEncoder.encode("Generic Carrier", "UTF-8");
            data += URLEncoder.encode("boxNumber", "UTF-8")
                    + "=" + URLEncoder.encode("9", "UTF-8");
            data += URLEncoder.encode("trackingNumbers", "UTF-8")
                    + "=" + URLEncoder.encode("fixTrackingNumbers", "UTF-8");
            data += URLEncoder.encode("boxDimensions", "UTF-8")
                    + "=" + URLEncoder.encode("fixBoxDimensions", "UTF-8");
            data += URLEncoder.encode("isDamaged", "UTF-8")
                    + "=" + URLEncoder.encode("false", "UTF-8");
            data += URLEncoder.encode("damageDescription", "UTF-8")
                    + "=" + URLEncoder.encode("Sample Description", "UTF-8");
            data += URLEncoder.encode("privateLabels", "UTF-8")
                    + "=" + URLEncoder.encode("Fix Private Labels", "UTF-8");
            data += URLEncoder.encode("returnedItems", "UTF-8")
                    + "=" + URLEncoder.encode("Fix Returned Items", "UTF-8");
            data += URLEncoder.encode("serialNumbers", "UTF-8")
                    + "=" + URLEncoder.encode("Fix Serial Numbers", "UTF-8");
            data += URLEncoder.encode("returnReason", "UTF-8")
                    + "=" + URLEncoder.encode("Sample Return Reason", "UTF-8");
//            String data = URLEncoder.encode("username", "UTF-8")
//                    + "=" + URLEncoder.encode(username, "UTF-8");
//            data += "&" + URLEncoder.encode("password", "UTF-8")
//                    + "=" + URLEncoder.encode(password, "UTF-8");

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
