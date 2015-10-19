package mapleleafstrings.mapleleafapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    // Declarations
    AlertDialog mdialog;
    LinearLayout linearLayout;

    // List of menu buttons and sub menus. Adding to any of these lists adds a button to
    // that menu; to assign a function assign a function to the button in the
    // assignButtonFunction() method.
    String[] MainMenuButtons = {"Check-In Return"};
    String[] CheckInButtons = {"Pending Return", "Manual Return", "Main Menu"};

    // Menu state: used for tracking the back feature
    String[] CurrentButtonList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Grabs where to put the buttons from content_main.xml
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout2);

        // Create the initial menu on the activity start
        populateButtons(MainMenuButtons);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createYesNoDialogue("Send Test Email?", "Send", "Cancel");
            }
        });

        // PHP SQL server test button
        FloatingActionButton phpTest = (FloatingActionButton)findViewById(R.id.phpTest);
        phpTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                phpSQLTest();
            }
        });
    }

    // Creates buttons on the menu, based on a passed-in array of buttons
    public void populateButtons(String[] buttonList){
        // Update the current button state
        CurrentButtonList = buttonList;

        // Loops through all buttons declared in MainMenuButtons[] and
        // creates them.
        for (int i = 0; i < buttonList.length; i++){
            Button b = new Button(this);
            b.setLayoutParams(new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            b.setId(i);
            b.setText(buttonList[i]);
            assignButtonFunction(b);
            linearLayout.addView(b);
        }
    }

    // Method that assigns functions to dynamically generated buttons, based
    // on the text written on the button.
    // Can probably be handled in a cleaner way than an if/else block, but
    // suits the needs of the program for now
    public void assignButtonFunction(Button b){
        if (b.getText() == "Check-In Return") {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeButtons(CurrentButtonList);
                    populateButtons(CheckInButtons);
                }
            });
        } else if(b.getText() == "Main Menu") {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeButtons(CurrentButtonList);
                    populateButtons(MainMenuButtons);
                }
            });
        } else if(b.getText() == "Pending Return") {
            //TODO: Write things here
        }else if(b.getText() == "Manual Return") {
            final Intent ManualReturnIntent = new Intent(this, ManualReturnActivity.class);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeButtons(CurrentButtonList);
                    populateButtons(MainMenuButtons);
                    startActivity(ManualReturnIntent);
                }
            });
        } else {
            //TODO: Write what to do if there is no function assigned for the button
        }
    }

    // Simple method for removing buttons from view
    // Call this whenever transitioning between new menus
    public void removeButtons(String[] buttonList){
        for (int i = 0; i < buttonList.length; i++){
            View v = linearLayout.findViewById(i);
            linearLayout.removeView(v);
        }
    }

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
        removeButtons(MainMenuButtons);
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
        //TODO: if it becomes problematic, buttons just won't look as good.

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

    // Update the php server with arbitrary test values
    private void phpSQLTest(){
        //TODO: Get this securely
        String username = "elecban_steve";
        String password = "D4t4b4se";

        new updateSQLDatabase(this).execute(username,password);
    }

    // Programatically create the initial activity menu
    private void populateStartMenu(){
        // TODO: Write code here
    }

    // Clear any buttons that might be in the start menu;
    // mainly for the purpose of traversing menus
    private void wipeStartMenu(){
        // TODO: Write code here
    }

    // Adds and removes buttons in the main menu based on user interaction
    private void menuHandler(){

    }

    // Debug method for testing emails sent from apps
    private void sendTestEmail(String recipients, String subject, String body){
        // Test command to make sure things are working
        TextView text = (TextView)findViewById(R.id.debugText);
        text.setText("Send Button Was Pressed");

        // Email Send Attempt
        try {
            // Creates the thread that sends the email
            // TODO: Make the name make more sense and add error handling
            // Possible error handling solution at:
            //   http://stackoverflow.com/questions/1739515/asynctask-and-error-handling-on-android
            new RetrieveBackgroundTask("This is a test email").execute(); // Working but has no GUI error feedback
        } catch (Exception e) {
            Log.e("SendEmail", e.getMessage(), e);
        }
    }
}
