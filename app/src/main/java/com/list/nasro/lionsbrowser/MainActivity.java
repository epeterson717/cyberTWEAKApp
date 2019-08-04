/*
 Code for Browser Activity modified from "simple_web_browser_app"
 Created by Red Ayoub (redayoub47) on 5/24/18.
 https://github.com/redayoub47/simple_web_browser_app
 ----------------------------------------------------------------
 Code for SQL Database Storage modified from Android SQLite Database tutorial, "CRUD Operation in SQLite"
 Created by  Belal Khan on 9/26/17.
 https://www.simplifiedcoding.net/android-sqlite-database-example/
 */

package com.list.nasro.lionsbrowser;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    WebsiteAdapter adapter;

    String showUAS, siteUAS;

    private WebView webView;
    private AutoCompleteTextView urlInput;
    private ProgressBar pageLodingProgressBar;

    private DBConnection dbConnection;
    private ArrayAdapter<String> webPageArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent i = getIntent();
        String UASfromAU = i.getStringExtra("UAS");
        String UASfromCU = i.getStringExtra("uasCUtoMA");


        if (TextUtils.isEmpty(UASfromAU) && TextUtils.isEmpty(UASfromCU)) {
            //Toast.makeText(this,"UAS Not Selected"+ "\n" + "Using Default (Android/Chrome)",Toast.LENGTH_LONG).show();
            showUAS = "Chrome on Android";
        } else if(TextUtils.isEmpty(UASfromAU)) {
            showUAS =  UASfromCU;
            //Toast.makeText(this,"Selected UAS: " + UASfromCU,Toast.LENGTH_LONG).show();
        }
        else{
            //Toast.makeText(this,"Selected UAS: " + UASfromAU,Toast.LENGTH_LONG).show();
            showUAS = UASfromAU;
        }

        // Set User Agent String

        if (showUAS.equals("Chrome on Android")) {
            // Android - Google Chrome
            siteUAS = ("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        } else if (showUAS.equals("Safari on iPhone")) {
            // iPhone - Safari
            siteUAS = ("Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1");
        } else if (showUAS.equals("Chrome on Windows")) {
            // Windows - Chrome
            siteUAS = ("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        } else if (showUAS.equals("Internet Explorer on Windows")) {
            // Windows - IE
            siteUAS = ("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        } else if (showUAS.equals("Safari on Mac")) {
            // Mac - Safari
            siteUAS = ("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9");
        } else if (showUAS.equals("Firefox on Linux")) {
            // Linux - Firefox
            siteUAS = ("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:57.0) Gecko/20100101 Firefox/57.0");
        } else {
            // Android - Google Chrome (DEFAULT)
            siteUAS = ("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        }

        //opening the database
        mDatabase = openOrCreateDatabase(Options.DATABASE_NAME, MODE_PRIVATE, null);


        Button options = (Button) findViewById(R.id.options_btn);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iMAtoOP = new Intent(MainActivity.this, Options.class);
                iMAtoOP.putExtra("uasMAtoOP",showUAS);
                startActivity(iMAtoOP);
                //startActivity(new Intent(MainActivity.this, AboutUs.class));

            }
        });

        urlInput = (AutoCompleteTextView) findViewById(R.id.edtWebUrl);
        pageLodingProgressBar = findViewById(R.id.progress);

        webView = (WebView) findViewById(R.id.myWebView);
        WebSettings settings = webView.getSettings();

        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);

        // init webView with google
        pageLodingProgressBar.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new Callback());

        webView.loadUrl("https://www.whatsmybrowser.org");      //Set home to WhatsMyBrowswer.org
        urlInput.setText("https://www.whatsmybrowser.org");

        hideKeyboared();

        dbConnection = new DBConnection(this);

        webPageArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        urlInput.setAdapter(webPageArrayAdapter);

        urlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    showPage(null);
                    return true;
                }

                return false;
            }
        });

        finalcheck();
    }



    private String urlForCompare(String currentURL) {
        if (currentURL.startsWith("http://www.")) {
            return currentURL.replace("http://www.", "");
        } else if (currentURL.startsWith("https://www.")) {
            return currentURL.replace("https://www.", "");
        } else if (currentURL.startsWith("www.")) {
            return currentURL.replace("www.", "");
        } else {
            return currentURL;
        }
    }
    private String ridOfEnd(String displayURL) {
        if (displayURL.endsWith("/")) {
            return displayURL.replace("/", "");
        } else {
            return displayURL;
        }
    }

    private void finalcheck() {
        String currentURL = webView.getUrl();
        String displayURL = urlForCompare(currentURL);
        String finalDislayURL = ridOfEnd(displayURL);
        //Toast.makeText(this, "Current URL:" + finalDislayURL, Toast.LENGTH_LONG).show();

        if (queryIsExist(finalDislayURL)) {
            Toast.makeText(this, "This Website is in Your List", Toast.LENGTH_LONG).show();
            webView.getSettings().setUserAgentString(siteUAS);

        } else {
            Toast.makeText(this, "This Website is NOT in Your List", Toast.LENGTH_LONG).show();
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");

        }
    }

    public boolean queryIsExist(String finalDislayURL) {
        Cursor cursor = mDatabase.query("employees", new String[]{"name"}, null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private void hideKeyboared() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // init webPageAdapter
        ArrayList<String> urls = new ArrayList<>();
        for (WebPage webPage : dbConnection.getData()) {
            if (!urls.contains(webPage.getUrl()))
                urls.add(webPage.getUrl());
        }
        webPageArrayAdapter.clear();
        webPageArrayAdapter.addAll(urls);
        webPageArrayAdapter.notifyDataSetChanged();

        // get url for intent data
        Uri uriFromIntent = getIntent().getData();
        if (uriFromIntent != null) {
            urlInput.setText(uriFromIntent.toString());
            webView.loadUrl(uriFromIntent.toString());
        }
    }

    public void showPage(View v) {
        hideKeyboared();
        webView.setWebViewClient(new Callback());
        webView.loadUrl(userInputToUrl(urlInput.getText().toString()));
        WebPage webPage = dbConnection.dataInsert(urlForDB(urlInput.getText().toString()));
        webPageArrayAdapter.add(webPage.getUrl());
        pageLodingProgressBar.setVisibility(View.VISIBLE);
    }

    private String urlForDB(String urlInput) {
        if (urlInput.startsWith("http://")) {
            return urlInput.replace("http://", "");
        } else if (urlInput.startsWith("https://")) {
            return urlInput.replace("https://", "");
        } else {
            return urlInput;
        }
    }

    private String userInputToUrl(String urlInput) {
        if (urlInput.startsWith("http://") || urlInput.startsWith("https://")) {
            return urlInput;
        } else {
            return "http://" + urlInput;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    class Callback extends WebViewClient {
        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            pageLodingProgressBar.setVisibility(View.GONE);
        }
    }
}