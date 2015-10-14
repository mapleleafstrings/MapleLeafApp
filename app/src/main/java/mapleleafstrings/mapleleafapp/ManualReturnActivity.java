package mapleleafstrings.mapleleafapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;
import java.util.*;

public class ManualReturnActivity extends AppCompatActivity {

    // Declarations
    Calendar dateCalendar = Calendar.getInstance();
    boolean keyboardNextIsPressed;
    String currentDate;
    LinearLayout linearLayout;

    String recievedDate, recievedBy, recievedFrom, carrier, boxNumber,
            boxDimensions, damageDescription, returnReason;
    String[] trackingNumbers, privateLabels, returnedItems, serialNumbers;

    // List of ID values to assign to entry fields for saving
    HashMap<String, Integer> fieldIDList = new HashMap<String, Integer>(){{
        put("byField", 1000);
        put("fromField", 1001);
        put("dateField", 1002);
    }};

    // A list of data field pages to make
    List<String> dataEntryFields = Arrays.asList(
            "Date, Name, and From",
            "Carrier, Boxes, and Tracking",
            "BoxDimensions",

            // Yes or no prompt that will bring up photography is selected
            "IsDamaged",

            "Damage Description",
            "Private Labels",
            "Items Returned and Serials",
            "Reason For Return",
            "Return Summary"
    );

    // Initialize the date picker dialog globally for access in other methods
    EditText dp;
    DatePickerDialog.OnDateSetListener getDate = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            dateCalendar.set(Calendar.YEAR, year);
            dateCalendar.set(Calendar.MONTH, monthOfYear);
            dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateField();
        }
    };

    // Index of current field(s). Used for saving form progress. Initialize this
    // with the first data entry field.
    int currentEntryState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_return);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // This is where all the data fields will get populated
        linearLayout = (LinearLayout)findViewById(R.id.manualReturnLayout);
        linearLayout.setClickable(true);
        linearLayout.setFocusableInTouchMode(true);

        // Generate the initial fields, and hide the Back button
        generateFields(currentEntryState);

        // Attach the 'next' button to a function
        Button NextButton = (Button) findViewById(R.id.manualNextButton);
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextFields();
            }
        });

        // Attach the 'back' button to a function
        Button BackButton = (Button) findViewById(R.id.manualBackButton);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                goToLastFields();
            }
        });

        // Hide the back button on startup
        BackButton.setVisibility(View.INVISIBLE);
    }

    // Generates the next data entry field(s) and saves information
    // from the current fields.
    //TODO: Research how to do this with less spaghetti code
    public void generateFields(int currentEntryIndex){
        // Empty the layout view for fresh populating
        clearAllFields();

        // Grab the string from the current field position
        String field = (String)dataEntryFields.get(currentEntryIndex);

        // Messy code to add and remove fields based on the currentEntryIndex
        if (field == "Date, Name, and From"){
            // Date label
            TextView t = new TextView(this);
            t.setText("Date Package Was Received:");
            linearLayout.addView(t);

            // Date field; calls a date picker dialog when selected
            dp = new EditText(this);
            dp.setInputType(InputType.TYPE_NULL);
            dp.setId(fieldIDList.get("dateField"));

            // Listen for selection to bring up a date select menu
            dp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        new DatePickerDialog(ManualReturnActivity.this, getDate,
                            dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                            dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
            });
            // Listen for selection (while focused only, for some reason)
            dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(ManualReturnActivity.this, getDate,
                            dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                            dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            linearLayout.addView(dp);
            // Pre-populate with the current date
            updateDateField();

            // Received By label
            TextView rt = new TextView(this);
            rt.setText("Who is filing this return? (enter your name)");
            linearLayout.addView(rt);

            // Received By field
            EditText rb = new EditText(this);
            setSingleLineRules(rb);
            rb.setId(fieldIDList.get("byField"));
            linearLayout.addView(rb);

            // Received From label
            TextView ft = new TextView(this);
            ft.setText("Who sent this return? (name of sender)");
            linearLayout.addView(ft);

            // Received From field
            EditText rf = new EditText(this);
            setSingleLineRules(rf);
            rf.setId(fieldIDList.get("fromField"));
            linearLayout.addView(rf);

        } else if(field == "Carrier, Boxes, and Tracking"){
            // Carrier label
            TextView t = new TextView(this);
            t.setText("Shipping Carrier for Returned Package:");
            linearLayout.addView(t);

            // Carrier radio button
            final RadioButton[] crb = new RadioButton[3];
            RadioGroup crg = new RadioGroup(this);
            crg.setOrientation(RadioGroup.HORIZONTAL);

            crb[0] = new RadioButton(this);
            crg.addView(crb[0]);
            crb[0].setText("UPS");

            crb[1] = new RadioButton(this);
            crg.addView(crb[1]);
            crb[1].setText("Conway");

            crb[2] = new RadioButton(this);
            crg.addView(crb[2]);
            crb[2].setText("Other");

            linearLayout.addView(crg);

            // Box Number label
            TextView nt = new TextView(this);
            nt.setText("Number of boxes returned:");
            linearLayout.addView(nt);

            // Box Number field
            EditText nf = new EditText(this);
            nf.setInputType(InputType.TYPE_CLASS_NUMBER);
            linearLayout.addView(nf);

            // Tracking Number label
            TextView tt = new TextView(this);
            tt.setText("Input Tracking Numbers:");
            linearLayout.addView(tt);

            // Add a sub-layout with two buttons for adding and removing from the table
            LinearLayout buttonSubLayout = new LinearLayout(this);
            buttonSubLayout.setClickable(true);
            buttonSubLayout.setFocusableInTouchMode(true);

            Button addButton = new Button(this);
            addButton.setText("Add");
            buttonSubLayout.addView(addButton);

            Button removeButton = new Button(this);
            removeButton.setText("Remove");
            buttonSubLayout.addView(removeButton);

            linearLayout.addView(buttonSubLayout);

            // Tracking Number table
            EditText tl = new EditText(this);
            setTextTableRules(tl);
            linearLayout.addView(tl);

        } else if(field == "BoxDimensions"){
            // Dimensions label
            TextView t = new TextView(this);
            t.setText("(Optional) Specify the dimensions of the returned boxes:");
            linearLayout.addView(t);

            // New linear layout for dimensions
            LinearLayout dimensionLayout = new LinearLayout(this);
            dimensionLayout.setOrientation(LinearLayout.HORIZONTAL);
            dimensionLayout.setClickable(true);
            dimensionLayout.setFocusable(true);
            dimensionLayout.setGravity(Gravity.CENTER);

            // Field 1
            EditText e1 = new EditText(this);
            e1.setWidth(100);
            setSingleLineRules(e1);
            dimensionLayout.addView(e1);

            // Label 1
            TextView l1 = new TextView(this);
            l1.setText("x");
            dimensionLayout.addView(l1);

            // Field 2
            EditText e2 = new EditText(this);
            e2.setWidth(100);
            setSingleLineRules(e2);
            dimensionLayout.addView(e2);

            // Label 2
            TextView l2 = new TextView(this);
            l2.setText("x");
            dimensionLayout.addView(l2);

            // Field 3
            EditText e3 = new EditText(this);
            e3.setWidth(100);
            setSingleLineRules(e3);
            dimensionLayout.addView(e3);

            linearLayout.addView(dimensionLayout);

        } else if(field == "IsDamaged"){
            // Damage Confirmation label
            TextView t = new TextView(this);
            t.setText("Is the return damaged in any way?");
            linearLayout.addView(t);

            // Damage Confirmation buttons
            LinearLayout damageLayout = new LinearLayout(this);
            damageLayout.setOrientation(LinearLayout.HORIZONTAL);
            damageLayout.setClickable(true);
            damageLayout.setFocusable(true);
            damageLayout.setGravity(Gravity.CENTER);

            // Increment the index by an additional amount if "no" is selected

        } else if(field == "Damage Description"){
            // Damage Description label
            TextView t = new TextView(this);
            t.setText("Describe the damage in detail:");
            linearLayout.addView(t);

        } else if(field == "Private Labels"){
            // Private Labels label
            TextView t = new TextView(this);
            t.setText("List any custom or private labels, if applicable");
            linearLayout.addView(t);

        } else if(field == "Items Returned and Serials"){
            // Items Returned and Serials label
            TextView t = new TextView(this);
            t.setText("List the items that are being returned below, and any applicable serial numbers:");
            linearLayout.addView(t);

            // Add a sub-layout with two buttons for adding and removing from the table
            LinearLayout buttonSubLayout = new LinearLayout(this);
            buttonSubLayout.setClickable(true);
            buttonSubLayout.setFocusableInTouchMode(true);

            Button addButton = new Button(this);
            addButton.setText("Add");
            buttonSubLayout.addView(addButton);

            Button removeButton = new Button(this);
            removeButton.setText("Remove");
            buttonSubLayout.addView(removeButton);

            linearLayout.addView(buttonSubLayout);

            // Items Returned and Serials table
            // List serial number below each returned item, even if empty
            EditText ise = new EditText(this);
            setTextTableRules(ise);
            linearLayout.addView(ise);

        } else if(field == "Reason For Return") {
            // Reason Returned label
            TextView t = new TextView(this);
            t.setText("Describe the reason for the return, if known (otherwise just put 'unknown'):");
            linearLayout.addView(t);

            // Reason Returned text edit
            EditText re = new EditText(this);
            re.setMaxLines(10);
            re.setLines(10);

            // Hacky way of making sure the keyboard isn't hidden when Next is pressed.
            // Can probably be done in a better way
            re.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        keyboardNextIsPressed = true;
                    } else {
                        keyboardNextIsPressed = false;
                    }
                    return false;
                }
            });

            // Listen for when the area outside of the keyboard is pressed
            re.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && !keyboardNextIsPressed) {
                        hideKeyboard(v);
                    } else {
                        keyboardNextIsPressed = false;
                    }
                }
            });

        } else if(field == "Return Summary"){
            // Summary label
            TextView t = new TextView(this);
            t.setText("Summary");
            linearLayout.addView(t);

        } else {
            //TODO: Error handling
        }
    }

    // Asserts that no data entry fields are left empty
    public Boolean allDataFieldsPopulated(){
        int layoutChildren = linearLayout.getChildCount();

        // Make sure the relativeLayout field is not empty
        if(layoutChildren > 0){
            // Iterate through all elements for any data entry
            // fields
            for(int i = 0; i < layoutChildren; i++){
                // Grabs the currently iterated element
                View v = linearLayout.getChildAt(i);

                // Check to make sure that if the element is a data
                // entry field that it is not empty.
                if (v instanceof EditText){
                    if (((EditText) v).getText() == null ||
                            ((EditText) v).getText().toString().trim() == ""){
                        displayToastMessage("Missing Required Information Field");
                        return false;
                    } else {
                        String fieldString = "";

                        // Find the fields unique name based on its assigned ID
                        for (Map.Entry<String, Integer> e : fieldIDList.entrySet()){
                            // Integer ID of the iterated hashmap segment
                            int testI = e.getValue();

                            // Compare the IDs to see if there is a match in the list
                            if (((EditText) v).getId() == e.getValue()){
                                fieldString = e.getKey();
                            }
                        }

                        if (fieldString != "") {
                            saveDataFields(fieldString);
                        } else {
                            //TODO: Error Handling
                        }
                    }
                }
            }
        }
        return true;
    }

    // Looks for the value in an EditText box and saves it to its variable
    // Overly complicated, an be simplified later. This is what design is for
    public void saveDataFields(String fieldIDString){
        int fieldIDNumber = fieldIDList.get(fieldIDString);
        EditText etObject = (EditText)findViewById(fieldIDNumber);

        switch (fieldIDString){
            case "byField":     recievedBy = etObject.getText().toString().trim();
                                displayToastMessage("byField saved as " + recievedBy);
                                break;
            case "fromField":   recievedFrom = etObject.getText().toString().trim();
                                displayToastMessage("fromField saved as " + recievedFrom);
                                break;
            case "dateField":   recievedDate = etObject.getText().toString().trim();
                                displayToastMessage("dateField saved as " + recievedDate);
                                break;
            default:            //TODO: Error Handling
                                break;
        }
    }

    // Called when the "Next" button is pressed.
    // Does exactly what it says on the tin.
    public void goToNextFields(){
        // Don't generate fields if it is the last field
        if (currentEntryState != dataEntryFields.size() - 1){
            if (allDataFieldsPopulated()) {
                currentEntryState += 1;
                // Show the back button, since advancement has occurred
                findViewById(R.id.manualBackButton).setVisibility(View.VISIBLE);

                generateFields(currentEntryState);

                // Test Usage of Toast
                //displayToastMessage("Index is " + currentEntryState + ", Size is " +dataEntryFields.size());
            }
        } else {
            // TODO: Put the manual return submission code here
        }
    }

    // Called when the "Back" button is pressed.
    // Does exactly what it says on the tin.
    public void goToLastFields(){
        if (currentEntryState != 0){
            currentEntryState -= 1;
            generateFields(currentEntryState);

            // If index is 0 after subtracting, hide the back button
            if (currentEntryState == 0) {
                findViewById(R.id.manualBackButton).setVisibility(View.INVISIBLE);
            }
        } else{
            // TODO: Error handling for if the back button is somehow not hidden at 0
        }
    }

    // Method to clear all fields from a layout
    // Also responsible for saving data
    public void clearAllFields(){
        if(((LinearLayout) linearLayout).getChildCount() > 0)
            ((LinearLayout) linearLayout).removeAllViews();
    }

    // Display a short lasting message using the Toast API
    public void displayToastMessage(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // Updates the received date field
    public void updateDateField(){
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dp.setText(sdf.format(dateCalendar.getTime()));
    }

    // Sets various rules on a passed in EditText object to make the keypad exitable
    // and convert the enter key from a new line button to a confirmation button
    public void setSingleLineRules(EditText textfield){
        // Prevents suggestions while typing
        textfield.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // Replaces enter key with next/done buttons
        textfield.setSingleLine(true);
        textfield.setMaxLines(1);
        textfield.setLines(1);

        // Hacky way of making sure the keyboard isn't hidden when Next is pressed.
        // Can probably be done in a better way
        textfield.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    keyboardNextIsPressed = true;
                } else {
                    keyboardNextIsPressed = false;
                }
                return false;
            }
        });

        // Listen for when the area outside of the keyboard is pressed
        textfield.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !keyboardNextIsPressed) {
                    hideKeyboard(v);
                } else {
                    keyboardNextIsPressed = false;
                }
            }
        });

    }

    // Set rules for EditText objects set up to behave as text display tables
    public void setTextTableRules(EditText textfield){
        // Prevent entering data
        textfield.setInputType(InputType.TYPE_NULL);

        // Set the tables size
        textfield.setMaxLines(9);
        textfield.setLines(9);

        // Change the style of the field, to make it look more like a table
        textfield.setBackgroundResource(R.drawable.table_stylesheet);
    }

    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
