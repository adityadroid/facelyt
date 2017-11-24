package com.adityaadi1467.facelytx.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityaadi1467.facelytx.Utilities.Common;
import com.example.adi.facelyt.R;



public class ContentActivity extends AppCompatActivity {

    String URL;
    boolean isImage = false;
    WebView mWebView;
    TextView pageTitle;
    TextView pageURL;
    ImageView downloadImageButton, shareButton, closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        mWebView = (WebView)findViewById(R.id.contentWebView);
        pageTitle = (TextView)findViewById(R.id.pageTitleTextView);
        pageURL = (TextView)findViewById(R.id.pageURLTextView);
        downloadImageButton = (ImageView) findViewById(R.id.downloadImageButton);
        shareButton = (ImageView)findViewById(R.id.shareButton);
        closeButton = (ImageView)findViewById(R.id.closeButton);
        isImage = getIntent().getExtras().getBoolean("isImage");
        URL = getIntent().getExtras().getString("url");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new mWebClient());

        downloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.downloadImage(getApplicationContext(),downloadImageButton,URL);

            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.shareLink(getApplicationContext(),mWebView);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(isImage) {
            findViewById(R.id.toolbar).setVisibility(View.GONE);
            downloadImageButton.setVisibility(View.VISIBLE);
        }

            mWebView.loadUrl(URL);


    }
    private class mWebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            findViewById(R.id.webViewProgress).setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            URL = url;
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            pageTitle.setText(mWebView.getTitle());
            pageURL.setText(mWebView.getUrl().toString().substring(0,20)+"...");
            findViewById(R.id.webViewProgress).setVisibility(View.GONE);
            super.onPageFinished(view, url);

        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            finish();
        }
    }
}
