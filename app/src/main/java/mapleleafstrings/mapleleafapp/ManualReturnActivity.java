package mapleleafstrings.mapleleafapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    EditText cre;
    ListView tl;

    String recievedDate, recievedBy, recievedFrom, otherCarrier, boxNumber,
            damageDescription, returnReason, carrierName;
    int carrierRadioState = 0;
    Boolean isDamaged = false;
    List<String> trackingNumbers = new ArrayList<>(),
            privateLabels = new ArrayList<>(),
            itemsAndSerials = new ArrayList<>(),
            boxDimensions = new ArrayList<>();

    // List selection stuff
    View selectedTrackingObject;
    int selectedTrackingPosition;
    View selectedDimensionObject;
    int selectedDimensionPosition;
    View selectedLabelObject;
    int selectedLabelPosition;
    View selectedItemObject;
    int selectedItemPosition;

    // List of ID values to assign to entry fields for saving
    HashMap<String, Integer> fieldIDList = new HashMap<String, Integer>(){{
        put("byField", 1000);
        put("fromField", 1001);
        put("dateField", 1002);
        put("carrierRadio", 1003);
        put("otherField", 1004);
        put("numberField", 1005);
        put("trackingTable", 1006);
        put("dimTable", 1007);
        put("damageField", 1008);
        put("labelTable", 1009);
        put("itemTable", 1010);
        put("reasonField", 1011);
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
    public void generateFields(final int currentEntryIndex){
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
            rb.setText(recievedBy);
            linearLayout.addView(rb);

            // Received From label
            TextView ft = new TextView(this);
            ft.setText("Who sent this return? (name of sender)");
            linearLayout.addView(ft);

            // Received From field
            EditText rf = new EditText(this);
            setSingleLineRules(rf);
            rf.setId(fieldIDList.get("fromField"));
            rf.setText(recievedFrom);
            linearLayout.addView(rf);

        } else if(field == "Carrier, Boxes, and Tracking"){
            // Carrier label
            TextView t = new TextView(this);
            t.setText("Shipping Carrier for Returned Package:");
            linearLayout.addView(t);

            // Layout for radio buttons and text field
            LinearLayout radioLayout = new LinearLayout(this);
            //radioLayout.setClickable(true);
            //radioLayout.setFocusableInTouchMode(true);

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

            crb[carrierRadioState].setChecked(true);

            // The 'other' edit text
            cre = new EditText(this);
            setSingleLineRules(cre);
            cre.setId(fieldIDList.get("otherField"));
            cre.setText(otherCarrier);
            cre.setWidth(150);

            // Disable/Enable the Other field and clear it depending on the
            // current selection
            crb[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cre.setInputType(InputType.TYPE_NULL);
                    cre.setText("");
                    cre.clearFocus();
                    carrierRadioState = 0;
                    carrierName = "UPS";
                }
            });

            crb[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cre.setInputType(InputType.TYPE_NULL);
                    cre.setText("");
                    cre.clearFocus();
                    carrierRadioState = 1;
                    carrierName = "Conway";
                }
            });

            crb[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cre.setInputType(InputType.TYPE_CLASS_TEXT);
                    carrierRadioState = 2;
                }
            });

            radioLayout.addView(crg);
            radioLayout.addView(cre);

            linearLayout.addView(radioLayout);

            // Box Number label
            TextView nt = new TextView(this);
            nt.setText("Number of boxes returned:");
            linearLayout.addView(nt);

            // Box Number field
            EditText nf = new EditText(this);
            nf.setInputType(InputType.TYPE_CLASS_NUMBER);
            setSingleLineRules(nf);
            nf.setId(fieldIDList.get("numberField"));
            nf.setText(boxNumber);
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

            // Tracking Table
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    R.layout.simplerow, trackingNumbers);

            tl = new ListView(this);
            tl.setAdapter(listAdapter);
            tl.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            tl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Deselect the current object
                    if (selectedTrackingObject != null &&
                            selectedTrackingObject != view){
                        selectedTrackingObject.setBackgroundResource(0);
                    }

                    selectedTrackingObject = view;
                    selectedTrackingPosition = position;
                    selectedTrackingObject.setBackgroundResource(R.drawable.highlighted_row);
                }
            });
            linearLayout.addView(tl);

            // Add button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addTableEntry(listAdapter, "Input Tracking Number");
                }
            });

            // Remove button
            removeButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (selectedTrackingObject != null){
                        // Clear the highlight
                        selectedTrackingObject.setBackgroundResource(0);

                        // Remove the selected entry and clear the tracking info
                        trackingNumbers.remove(selectedTrackingPosition);
                        selectedTrackingObject = null;
                        selectedTrackingPosition = -1;
                        listAdapter.notifyDataSetChanged();
                    } else {
                        displayToastMessage("No Item Selected");
                    }
                }
            });

        } else if(field == "BoxDimensions"){
            // Dimensions label
            TextView t = new TextView(this);
            t.setText("(Optional) Specify the dimensions of the returned boxes:");
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

            // Box Dimension table
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    R.layout.simplerow, boxDimensions);

            tl = new ListView(this);
            tl.setAdapter(listAdapter);
            tl.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            tl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Deselect the current object
                    if (selectedDimensionObject != null &&
                            selectedDimensionObject != view) {
                        selectedDimensionObject.setBackgroundResource(0);
                    }

                    selectedDimensionObject = view;
                    selectedDimensionPosition = position;
                    selectedDimensionObject.setBackgroundResource(R.drawable.highlighted_row);
                }
            });
            linearLayout.addView(tl);

            // Add button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addTableEntry(listAdapter, "Input Box Dimensions");
                }
            });

            // Remove button
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedDimensionObject != null) {
                        // Clear the highlight
                        selectedDimensionObject.setBackgroundResource(0);

                        // Remove the selected entry and clear the tracking info
                        boxDimensions.remove(selectedDimensionPosition);
                        selectedDimensionObject = null;
                        selectedDimensionPosition = -1;
                        listAdapter.notifyDataSetChanged();
                    } else {
                        displayToastMessage("No Item Selected");
                    }
                }
            });

            // Save for formatting the table dialog
//            // New linear layout for dimensions
//            LinearLayout dimensionLayout = new LinearLayout(this);
//            dimensionLayout.setOrientation(LinearLayout.HORIZONTAL);
//            dimensionLayout.setClickable(true);
//            dimensionLayout.setFocusable(true);
//            dimensionLayout.setGravity(Gravity.CENTER);
//
//            // Field 1
//            EditText e1 = new EditText(this);
//            e1.setWidth(100);
//            setSingleLineRules(e1);
//            dimensionLayout.addView(e1);
//
//            // Label 1
//            TextView l1 = new TextView(this);
//            l1.setText("x");
//            dimensionLayout.addView(l1);
//
//            // Field 2
//            EditText e2 = new EditText(this);
//            e2.setWidth(100);
//            setSingleLineRules(e2);
//            dimensionLayout.addView(e2);
//
//            // Label 2
//            TextView l2 = new TextView(this);
//            l2.setText("x");
//            dimensionLayout.addView(l2);
//
//            // Field 3
//            EditText e3 = new EditText(this);
//            e3.setWidth(100);
//            setSingleLineRules(e3);
//            dimensionLayout.addView(e3);
//
//            linearLayout.addView(dimensionLayout);

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

            // Add a sub-layout with two buttons for confirming yes/no
            LinearLayout buttonSubLayout = new LinearLayout(this);
            buttonSubLayout.setClickable(true);
            buttonSubLayout.setFocusableInTouchMode(true);

            Button yesButton = new Button(this);
            yesButton.setText("Yes");
            // If yes, bring up the photography application with prompts for taking
            // pictures of damage
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDamaged = true;

                    // Just move to the next section for now
                    goToNextFields();
                    reshowNextButton();
                }
            });
            buttonSubLayout.addView(yesButton);

            Button noButton = new Button(this);
            noButton.setText("No");
            // Increment the index by an additional amount if "no" is selected, and
            // generate the next non-damage fields
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDamaged = false;

                    // This is incrementing the global var currentEntrySTATE and not the internal
                    // final var currentEntryIndex. A dangerous way to write code that may cause
                    // unexpected side effects, but *should* work fine in this instance.
                    currentEntryState += 1;
                    goToNextFields();
                    reshowNextButton();
                }
            });
            buttonSubLayout.addView(noButton);

            linearLayout.addView(buttonSubLayout);

            // Hide the next button; a selection is required here
            findViewById(R.id.manualNextButton).setVisibility(View.INVISIBLE);

        } else if(field == "Damage Description"){
            // Damage Description label
            TextView t = new TextView(this);
            t.setText("Describe the damage in detail:");

            // Damage Description edittext
            EditText de = new EditText(this);
            de.setMaxLines(15);
            de.setLines(15);
            de.setId(fieldIDList.get("damageField"));
            de.setText(damageDescription);
            de.setGravity(Gravity.TOP);
            // Change the style of the field, to make it look more like a table
            de.setBackgroundResource(R.drawable.table_stylesheet);
            linearLayout.addView(t);

            // Listen for when the area outside of the keyboard is pressed
            de.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    hideKeyboard(v);
                }
            });

            linearLayout.addView(de);

        } else if(field == "Private Labels"){
            // Private Labels label
            TextView t = new TextView(this);
            t.setText("List any custom or private labels, if applicable");
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

            // Private Labels table
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    R.layout.simplerow, privateLabels);

            tl = new ListView(this);
            tl.setAdapter(listAdapter);
            tl.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            tl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Deselect the current object
                    if (selectedLabelObject != null &&
                            selectedLabelObject != view) {
                        selectedLabelObject.setBackgroundResource(0);
                    }

                    selectedLabelObject = view;
                    selectedLabelPosition = position;
                    selectedLabelObject.setBackgroundResource(R.drawable.highlighted_row);
                }
            });
            linearLayout.addView(tl);

            // Add button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addTableEntry(listAdapter, "Input Private Label");
                }
            });

            // Remove button
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedLabelObject != null) {
                        // Clear the highlight
                        selectedLabelObject.setBackgroundResource(0);

                        // Remove the selected entry and clear the tracking info
                        privateLabels.remove(selectedLabelPosition);
                        selectedLabelObject = null;
                        selectedLabelPosition = -1;
                        listAdapter.notifyDataSetChanged();
                    } else {
                        displayToastMessage("No Item Selected");
                    }
                }
            });

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
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    R.layout.simplerow, itemsAndSerials);

            tl = new ListView(this);
            tl.setAdapter(listAdapter);
            tl.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            tl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Deselect the current objects. Select Item tracker will always be set
                    // to even
                    if (selectedItemObject != null) {
                        selectedItemObject.setBackgroundResource(0);
                        tl.getChildAt(selectedItemPosition + 1).setBackgroundResource(0);
                    }

                    // Select this and the object below if index is even
                    // Select this and the object above if index is odd
                    // Always set the position to the even index for ease of use purposes
                    if ((position % 2) == 0) {// if even
                        selectedItemObject = view;
                        selectedItemPosition = position;
                    } else { // if odd
                        // Assign the selectedItem tracking variable to the above view
                        selectedItemObject = tl.getChildAt(position - 1);
                        selectedItemPosition = position - 1;
                    }
                    // Set view highlight
                    selectedItemObject.setBackgroundResource(R.drawable.highlighted_row);

                    // Set view below selected one
                    final View lowerItemObject = tl.getChildAt(selectedItemPosition + 1);
                    lowerItemObject.setBackgroundResource(R.drawable.highlighted_row);
                }
            });
            linearLayout.addView(tl);

            // Add button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItemSerialTableEntry(listAdapter, "Input Item Returned");
                }
            });

            // Remove button
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedItemObject != null) {
                        // Clear the highlights
                        selectedItemObject.setBackgroundResource(0);
                        tl.getChildAt(selectedItemPosition + 1).setBackgroundResource(0);

                        // Remove both selected entries
                        itemsAndSerials.remove(selectedItemPosition + 1);
                        itemsAndSerials.remove(selectedItemPosition);
                        selectedItemObject = null;
                        selectedItemPosition = -1;
                        listAdapter.notifyDataSetChanged();
                    } else {
                        displayToastMessage("No Item Selected");
                    }
                }
            });

        } else if(field == "Reason For Return") {
            // Reason Returned label
            TextView t = new TextView(this);
            t.setText("Describe the reason for the return, if known (otherwise just put 'unknown'):");
            linearLayout.addView(t);

            // Reason Returned text edit
            EditText re = new EditText(this);
            re.setMaxLines(15);
            re.setLines(15);
            re.setId(fieldIDList.get("reasonField"));
            re.setText(returnReason);
            re.setGravity(Gravity.TOP);
            // Change the style of the field, to make it look more like a table
            re.setBackgroundResource(R.drawable.table_stylesheet);

            // Listen for when the area outside of the keyboard is pressed
            re.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    hideKeyboard(v);
                }
            });

            linearLayout.addView(re);

        } else if(field == "Return Summary"){
            // Summary label
            TextView t = new TextView(this);
            t.setText("Summary\n\n");
            linearLayout.addView(t);

            // Date Recieved
            TextView date = new TextView(this);
            String dateFormat = "MM/dd/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
            currentDate = sdf.format(dateCalendar.getTime());
            t.setText("Date Recieved: " + sdf.format(dateCalendar.getTime()) + "\n\n");
            linearLayout.addView(date);

            // Recieved By
            TextView name = new TextView(this);
            name.setText("Return filed by: " + recievedBy + "\n\n");
            linearLayout.addView(name);

            // Recieved From
            TextView from = new TextView(this);
            from.setText("Package returned from: " + recievedFrom + "\n\n");
            linearLayout.addView(from);

            // Carrier
            TextView carrier = new TextView(this);
            carrier.setText("Shipping Carrier: " + carrierName + "\n\n");
            linearLayout.addView(carrier);

            // Box Number
            TextView number = new TextView(this);
            number.setText("Number of Boxes: " + boxNumber + "\n\n");
            linearLayout.addView(number);

            // Tracking Numbers
            TextView tracking = new TextView(this);
            String trackingString = "Tracking Numbers:\n";
            for (int i = 0; i < trackingNumbers.size(); i++){
                trackingString += trackingNumbers.get(i) + "\n";
            }
            trackingString += "\n";
            tracking.setText(trackingString);
            linearLayout.addView(tracking);

            // Box Dimensions
            TextView dimensions = new TextView(this);
            String dimensionsString = "Box Dimensions:\n";
            for (int i = 0; i < boxDimensions.size(); i++){
                dimensionsString += boxDimensions.get(i) + "\n";
            }
            dimensionsString += "\n";
            dimensions.setText(dimensionsString);
            linearLayout.addView(dimensions);

            // Is Damaged + Damage Description
            TextView damaged = new TextView(this);
            if (isDamaged){
                damaged.setText("This return was damaged, with the following description:\n"
                    + damageDescription + "\n\n");
            } else {
                damaged.setText("Return was not damaged\n\n");
            }
            linearLayout.addView(damaged);

            // Private Labels
            TextView labels = new TextView(this);
            String labelsString = "Private Labels:\n";
            for (int i = 0; i < privateLabels.size(); i++){
                labelsString += privateLabels.get(i) + "\n";
            }
            labelsString += "\n";
            labels.setText(labelsString);
            linearLayout.addView(labels);

            // Items and Serials
            TextView items = new TextView(this);
            String itemsString = "Items Returned and Serial Numbers:\n";
            for (int i = 0; i < itemsAndSerials.size(); i++){
                itemsString += itemsAndSerials.get(i) + "\n";
            }
            itemsString += "\n";
            items.setText(itemsString);
            linearLayout.addView(items);

            // Return Reason
            TextView reason = new TextView(this);
            reason.setText("Reason for Return: \n" + returnReason + "\n\n");
            linearLayout.addView(reason);

        } else {
            //TODO: Error handling
        }
    }

    // Called to bring up a dialog to enter an item into a table
    public void addTableEntry(final ArrayAdapter<String> tableList, final String displayText){

        // Field Text Input
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        setSingleLineRules(input);

        AlertDialog mdialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))

                .setView(input)

                // The message shown in the main body of the window
                .setMessage(displayText)

                //TODO: Implement a basic alert image
                // .setIcon(R.drawable.alert_icon)

                // Sets text for the confirm and cancel button, and what method to call on execution;
                // null closes the window.
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!input.getText().toString().matches("")){
                            tableList.add(input.getText().toString());
                            return;
                        } else {
                            displayToastMessage("Field cannot be empty");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
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

    // Special table entry method for the dual table type
    public void addItemSerialTableEntry(final ArrayAdapter<String> tableList, final String displayText){

        // Add a sub-layout with two EditTexts and a Label
        LinearLayout itemSerialLayout = new LinearLayout(this);
        itemSerialLayout.setOrientation(LinearLayout.VERTICAL);
        //itemSerialLayout.setClickable(true);
        //itemSerialLayout.setFocusableInTouchMode(true);

        // Field Text Input
        final EditText input = new EditText(this);
        //setSingleLineRules(input);
        itemSerialLayout.addView(input);

        // Label for optional serial numbers
        final TextView serialLabel = new TextView(this);
        serialLabel.setText("\tSerial Number (if applicable)");
        itemSerialLayout.addView(serialLabel);

        // EditText for optional serial numbers
        final EditText serialInput = new EditText(this);
        //setSingleLineRules(serialInput);
        itemSerialLayout.addView(serialInput);

        AlertDialog mdialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))

                .setView(itemSerialLayout)
                        // The message shown in the main body of the window
                .setMessage(displayText)

                        //TODO: Implement a basic alert image
                        // .setIcon(R.drawable.alert_icon)

                        // Sets text for the confirm and cancel button, and what method to call on execution;
                        // null closes the window.
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Don't check serial since it is optional; program will
                        // just add an empty serial number
                        if (!input.getText().toString().matches("")){
                            tableList.add(input.getText().toString());
                            tableList.add("Serial Number: " +
                                serialInput.getText().toString());
                            return;
                        } else {
                            displayToastMessage("Field cannot be empty");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
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

                // Hackwork for saving the "Other" field nested in the Radio Button layout group
                if(v instanceof LinearLayout){
                    if (carrierRadioState == 2){
                        EditText otherField =
                                (EditText)v.findViewById(fieldIDList.get("otherField"));
                        if (otherField != null) {
                            carrierName = otherField.getText().toString().trim();
                            otherCarrier = otherField.getText().toString().trim();
                        }
                    }
                }

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
    // Overly complicated, to be simplified later. This is what design is for
    public void saveDataFields(String fieldIDString){
        int fieldIDNumber = fieldIDList.get(fieldIDString);
        EditText etObject = (EditText)findViewById(fieldIDNumber);

        switch (fieldIDString){
            case "byField":     recievedBy = etObject.getText().toString().trim();
                                break;
            case "fromField":   recievedFrom = etObject.getText().toString().trim();
                                break;
            case "dateField":   recievedDate = etObject.getText().toString().trim();
                                break;
            case "numberField": boxNumber = etObject.getText().toString().trim();
                                break;
            case "damageField": damageDescription = etObject.getText().toString().trim();
                                break;
            case "reasonField": returnReason = etObject.getText().toString().trim();
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

                // Change the Next text to Submit if we're on the summary after incrementing
                if (currentEntryState == dataEntryFields.size() - 1){
                    Button nextButton = (Button)findViewById(R.id.manualNextButton);
                    nextButton.setText("Submit");
                }
            }
        } else {
            fileManualReturn();
        }
    }

    // Send emails and update the database, while giving the user a
    // progress pop-up window
    public void fileManualReturn(){
        sendEmail();
        // Todo: actually check if email is sent
        displayToastMessage("Email Sent");
        finish();
    }

    // Called when the "Back" button is pressed.
    // Does exactly what it says on the tin.
    public void goToLastFields(){
        if (currentEntryState != 0){
            currentEntryState -= 1;
            generateFields(currentEntryState);

            // Reshow the next button in case it is hidden
            reshowNextButton();

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

    // Moved to a method since the Next text is changed to Submit on the
    // last page
    public void reshowNextButton(){
        Button nextButton = (Button)findViewById(R.id.manualNextButton);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setText("Next");
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

    // Debug method for testing emails sent from apps
    private void sendEmail(){
        // Email Send Attempt
        try {
            // Creates the thread that sends the email
            // TODO: Make the name make more sense and add error handling
            // Possible error handling solution at:
            //   http://stackoverflow.com/questions/1739515/asynctask-and-error-handling-on-android

            // Assemble the email body
            String dateFormat = "MM/dd/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
            currentDate = sdf.format(dateCalendar.getTime());

            String messageBody = "";
            messageBody += "A manual return has been filed with the following contents:\n\n";
            messageBody += "Date Recieved: " + currentDate + "\n\n";
            messageBody += "Return filed by: " + recievedBy + "\n\n";
            messageBody += "Return recieved from: " + recievedFrom + "\n\n";
            messageBody += "Shipping Handler: " + carrierName + "\n\n";
            messageBody += "Number of Boxes: " + boxNumber + "\n\n";
            messageBody += "Tracking Numbers:\n";
            for (int i = 0; i < trackingNumbers.size(); i++){
                messageBody += "\t" + trackingNumbers.get(i) + "\n";
            }
            messageBody += "\n";
            messageBody += "Box Dimensions:\n";
            for (int i = 0; i < boxDimensions.size(); i++){
                messageBody += "\t" + boxDimensions.get(i) + "\n";
            }
            messageBody += "\n";
            if (isDamaged){
                messageBody += "This return was listed as damaged with the following description:\n"
                        + returnReason + "\n\n";
            } else {
                messageBody += "This return was not listed as damaged\n\n";
            }
            messageBody += "Private Labels:\n";
            for(int i = 0; i < privateLabels.size(); i++){
                messageBody += "\t" + privateLabels.get(i) + "\n";
            }
            messageBody += "\n";
            messageBody += "Items Returned and Serial Numbers:\n";
            for (int i = 0; i < itemsAndSerials.size(); i++){
                messageBody += "\t" + itemsAndSerials.get(i);
            }
            messageBody += "\n\n";
            messageBody += "Reason for return:\n\t" + returnReason;


            new RetrieveBackgroundTask(messageBody).execute(); // Working but has no GUI error feedback
        } catch (Exception e) {
            Log.e("SendEmail", e.getMessage(), e);
        }
    }
}
