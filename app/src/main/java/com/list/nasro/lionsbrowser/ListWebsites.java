/*
Code for SQL Database Storage modified from Android SQLite Database tutorial, "CRUD Operation in SQLite"
Created by  Belal Khan on 9/26/17.
https://www.simplifiedcoding.net/android-sqlite-database-example/
*/

package com.list.nasro.lionsbrowser;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ListWebsites extends AppCompatActivity {

    Button main, browser;
    String sendUAS;

    List<Website> websiteList;
    SQLiteDatabase mDatabase;
    ListView listViewEmployees;
    WebsiteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_websites);

        Intent i = getIntent();
        String UASfromOP = i.getStringExtra("UAS");

        if (TextUtils.isEmpty(UASfromOP)) {
            //Toast.makeText(this,"UAS Not Selected, Using Default (Android/Chrome)",Toast.LENGTH_LONG).show();
            sendUAS = "Chrome on Android";
        }else{
            //Toast.makeText(this,"Selected UAS: " + UASfromOP,Toast.LENGTH_LONG).show();
            sendUAS = UASfromOP;
        }

        listViewEmployees = (ListView) findViewById(R.id.list_view);
        websiteList = new ArrayList<>();

        //opening the database
        mDatabase = openOrCreateDatabase(Options.DATABASE_NAME, MODE_PRIVATE, null);

        //this method will display the list
        showEmployeesFromDatabase();


        main = (Button) findViewById(R.id.main_btn);
        browser = (Button) findViewById(R.id.browser_btn);


        main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCUtoAU = new Intent(ListWebsites.this, Options.class);
                iCUtoAU.putExtra("uasCUtoAU",sendUAS);
                startActivity(iCUtoAU);
                //startActivity(new Intent(Customize.this, AboutUs.class));
            }
        });

        browser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCUtoMA = new Intent(ListWebsites.this, MainActivity.class);
                iCUtoMA.putExtra("uasCUtoMA",sendUAS);
                startActivity(iCUtoMA);
                //startActivity(new Intent(Customize.this, MainActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu strmenu) {
        getMenuInflater().inflate(R.menu.main_menu, strmenu);
        return super.onCreateOptionsMenu(strmenu);
    }

    private void showEmployeesFromDatabase() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor cursorEmployees = mDatabase.rawQuery("SELECT * FROM employees", null);

        //if the cursor has some data
        if (cursorEmployees.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                websiteList.add(new Website(
                        cursorEmployees.getInt(0),
                        cursorEmployees.getString(1),
                        cursorEmployees.getString(2),
                        cursorEmployees.getString(3),
                        cursorEmployees.getDouble(4)
                ));
            } while (cursorEmployees.moveToNext());
        }
        //closing the cursor
        cursorEmployees.close();

        //creating the adapter object
        adapter = new WebsiteAdapter(this, R.layout.list_layout_website, websiteList, mDatabase);

        //adding the adapter to listview
        listViewEmployees.setAdapter(adapter);
    }
}