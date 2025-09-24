package com.app.harcdis.screens;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.harcdis.R;
import com.app.harcdis.utils.Connection_Detector;

public class WebViewForImage extends AppCompatActivity {
    WebView webView;
    LinearLayout no_internet_layout;
    String pdfUrl;
    Connection_Detector connection_detector;
    private ProgressDialog progressDialog;

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view_for_image);
        progressDialog = new ProgressDialog(WebViewForImage.this);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        webView = findViewById(R.id.webView);
        no_internet_layout = findViewById(R.id.no_internet_layout);
        connection_detector = new Connection_Detector(WebViewForImage.this);

        pdfUrl = getIntent().getStringExtra("image_url");

        if (!connection_detector.isConnected()) {
            no_internet_layout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            progressDialog.dismiss();
        } else {
            no_internet_layout.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setDomStorageEnabled(true);
            webView.requestFocus();


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //    no_internet_layout.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    Toast.makeText(WebViewForImage.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            });

            webView.loadUrl(pdfUrl);


        }
    }

}

