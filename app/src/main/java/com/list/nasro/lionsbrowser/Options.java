/*
Code for SQL Database Storage modified from Android SQLite Database tutorial, "CRUD Operation in SQLite"
Created by  Belal Khan on 9/26/17.
https://www.simplifiedcoding.net/android-sqlite-database-example/
*/

package com.list.nasro.lionsbrowser;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Options extends AppCompatActivity implements View.OnClickListener {

    public static final String DATABASE_NAME = "myemployeedatabase";

    EditText site_ent, prob_ent;
    Spinner spinnerFreq, spinnerUAS;
    String UASfromMA, spinnerdisplay, UASfromCU;
    SQLiteDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        spinnerUAS = (Spinner) findViewById(R.id.spinnerUAS);

        Intent i = getIntent();
        UASfromMA = i.getStringExtra("uasMAtoOP");
        UASfromCU = i.getStringExtra( "uasCUtoAU");

        if (TextUtils.isEmpty(UASfromMA) && TextUtils.isEmpty(UASfromCU)) {
            spinnerdisplay = "Chrome on Android";
        }   else if (TextUtils.isEmpty(UASfromMA)) {
            spinnerdisplay =  UASfromCU;
        }   else {
            spinnerdisplay =  UASfromMA;
        }

        if (TextUtils.isEmpty(spinnerdisplay)) {
            //Toast.makeText(this,"UAS Not Selected, Using Default (Android/Chrome)",Toast.LENGTH_LONG).show();
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.strings, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUAS.setAdapter(adapter);
            int spinnerPosition = adapter.getPosition("Chrome on Android");
            spinnerUAS.setSelection(spinnerPosition);
        }else{
            //Toast.makeText(this,"Selected UAS: " + spinnerdisplay ,Toast.LENGTH_LONG).show();
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.strings, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUAS.setAdapter(adapter2);
            int spinnerPosition2 = adapter2.getPosition(spinnerdisplay);
            spinnerUAS.setSelection(spinnerPosition2);
        }

        site_ent = (EditText) findViewById(R.id.site_ent);
        prob_ent = (EditText) findViewById(R.id.prob_ent);
        spinnerFreq = (Spinner) findViewById(R.id.spinnerFreq);

        findViewById(R.id.btnAddData).setOnClickListener(this);
        findViewById(R.id.viewall_btn).setOnClickListener(this);
        findViewById(R.id.browser_btn2).setOnClickListener(this);
        //findViewById(R.id.uas_mi).setOnClickListener(this);




        //creating a database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        createEmployeeTable();
    }

    private void createEmployeeTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS employees (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name varchar(200) NOT NULL,\n" +
                        "    department varchar(200) NOT NULL,\n" +
                        "    joiningdate datetime NOT NULL,\n" +
                        "    salary double NOT NULL\n" +
                        ");"
        );
    }

    private boolean inputsAreCorrect(String name, String salary) {
        if (name.isEmpty()) {
            site_ent.setError("Please Enter Website");
            site_ent.requestFocus();
            return false;
        }

        if (salary.isEmpty() || Integer.parseInt(salary) <= 0) {
            prob_ent.setError("Please Enter Probability");
            prob_ent.requestFocus();
            return false;
        }
        return true;
    }

    private void addEmployee() {

        String name = site_ent.getText().toString().trim();
        String salary = prob_ent.getText().toString().trim();
        String dept = spinnerFreq.getSelectedItem().toString();

        //getting the current time for joining date
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String joiningDate = sdf.format(cal.getTime());

        //validating the inptus
        if (inputsAreCorrect(name, salary)) {

            String insertSQL = "INSERT INTO employees \n" +
                    "(name, department, joiningdate, salary)\n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?);";

            //using the same method execsql for inserting values
            //this time it has two parameters
            //first is the sql string and second is the parameters that is to be binded with the query
            mDatabase.execSQL(insertSQL, new String[]{name, dept, joiningDate, salary});

            Toast.makeText(this, "Website Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddData:
                addEmployee();
                break;
            case R.id.viewall_btn:
                String currentUAS = spinnerUAS.getSelectedItem().toString();
                Intent returnIntentVA = new Intent(Options.this, ListWebsites.class);
                returnIntentVA.putExtra("UAS",currentUAS);
                startActivity(returnIntentVA);
                //startActivity(new Intent(this, Customize.class));
                break;
            case R.id.browser_btn2:
                String currentUAS2 = spinnerUAS.getSelectedItem().toString();
                Intent returnIntentBR = new Intent(Options.this, MainActivity.class);
                returnIntentBR.putExtra("UAS",currentUAS2);
                startActivity(returnIntentBR);
                //startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}