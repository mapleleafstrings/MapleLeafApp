package mapleleafstrings.mapleleafapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Put declarations here
    AlertDialog mdialog;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            FloatingActionButton addImage = (FloatingActionButton) findViewById(R.id.fab);
            public void onClick(View view) {
                Mail m = new Mail("gmailusername@gmail.com", "password");

                String[] toArr = {"bla@bla.com", "lala@lala.com"};
                m.setTo(toArr);
                m.setFrom("wooo@wooo.com");
                m.setSubject("This is an email sent using my Mail JavaMail wrapper from an Android device.");
                m.setBody("Email body.");

                try {
                    //m.addAttachment("/sdcard/filelocation");

                    if(m.send()) {
                        Toast.makeText(MainActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e) {
                    //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }
            }
        });
    }
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createYesNoDialogue("Send Test Email?", "Send", "Cancel");
            }
        });
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Prototype of a simple dialogue window
    // TODO: Update comment once fully finished
    private void createYesNoDialogue(String displayText, String confirmText, String cancelText){
        mdialog = new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.app_name))
            // The message shown in the main body of the window
            .setMessage(displayText)
            //TODO: Implement a basic alert image
            //.setIcon(R.drawable.alert_icon)

            // Sets text for the confirm and cancel button, and what method to call on execution;
            // null closes the window.
            .setPositiveButton(confirmText, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id) {
                    sendTestEmail("", "", "");
                }
            })
            .setNegativeButton(cancelText, null)
            .show();

        // Hackwork to force the confirm/cancel button order
        //TODO: Test on other devices, make sure this isn't broken. Can be removed
        //TODO:    it becomes problematic, buttons just won't look as good.
        Button positiveButton = (Button) mdialog.findViewById(android.R.id.button1);
        Button negativeButton = (Button) mdialog.findViewById(android.R.id.button2);
        // Get the parent ViewGroup
        ViewGroup buttonPanelContainer = (ViewGroup) positiveButton.getParent();
        int positiveButtonIndex = buttonPanelContainer.indexOfChild(positiveButton);
        int negativeButtonIndex = buttonPanelContainer.indexOfChild(negativeButton);
        if (positiveButtonIndex > negativeButtonIndex) {
            // prepare exchange their index in ViewGroup
            buttonPanelContainer.removeView(positiveButton);
            buttonPanelContainer.removeView(negativeButton);
            // Indexes seem to be wrong, shift them down by 1 to correct them
            buttonPanelContainer.addView(negativeButton, positiveButtonIndex -1);
            buttonPanelContainer.addView(positiveButton, negativeButtonIndex -1);
        }

        // Additional menu display formatting
        WindowManager.LayoutParams layoutParams = mdialog.getWindow().getAttributes();
        layoutParams.dimAmount = 0.9f;
        mdialog.getWindow().setAttributes(layoutParams);
        mdialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    }

    // Debug method for testing emails sent from apps
    private void sendTestEmail(String recipients, String subject, String body){
        // Test command to make sure things are working
        TextView text = (TextView)findViewById(R.id.debugText);
        text.setText("Send Button Was Pressed");

        // Email Send Attempt
        try {
            new RetrieveBackgroundTask().execute();
        } catch (Exception e) {
            Log.e("SendEmail", e.getMessage(), e);
        }
    }
}
