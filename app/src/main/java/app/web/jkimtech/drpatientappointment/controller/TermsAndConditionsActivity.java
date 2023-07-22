package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import app.web.jkimtech.drpatientappointment.R;

public class TermsAndConditionsActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        webView = findViewById(R.id.webView_tnc);
        webView.loadData(getString(R.string.privacy_policy), "text/html", "UTF-8");
    }
}