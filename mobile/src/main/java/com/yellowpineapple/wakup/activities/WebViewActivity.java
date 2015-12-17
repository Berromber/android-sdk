package com.yellowpineapple.wakup.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by agutierrez on 13/02/15.
 */
@EActivity
public class WebViewActivity extends ParentActivity {

    @Extra int titleId = 0;
    @Extra String url = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup view
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient() {

            boolean firstLoad = true;

            @Override
            public void onPageFinished(WebView view, String url) {
                setLoading(false);
                firstLoad = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!firstLoad) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            }

        });
        // Get notification
        setLoading(true);
        if (url != null) webView.loadUrl(url);
        if (titleId != 0) setTitle(titleId);
        setContentView(webView);
    }

}