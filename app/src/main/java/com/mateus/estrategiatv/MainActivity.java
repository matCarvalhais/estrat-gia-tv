package com.mateus.estrategiatv;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private static final String TARGET_URL = "https://med.estrategia.com/mesa-de-estudo/";

    private WebView webView;
    private ImageView loadingView;
    private LinearLayout errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mantém a tela sempre ativa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        webView   = findViewById(R.id.webview);
        loadingView = findViewById(R.id.loading_view);
        errorView   = findViewById(R.id.error_view);

        Button retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(v -> loadPage());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // Cache persistente
        String cachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(cachePath);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("estrategia.com") || url.contains("accounts.google.com")) {
                    return false;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadingView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                loadingView.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
            loadingView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        } else {
            loadPage();
        }
    }

    private void loadPage() {
        errorView.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        webView.loadUrl(TARGET_URL);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Voltar no histórico
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // F5 para recarregar
        if (keyCode == KeyEvent.KEYCODE_F5) {
            loadPage();
            return true;
        }
        // Ctrl+= para aumentar zoom
        if (keyCode == KeyEvent.KEYCODE_EQUALS &&
                event.isCtrlPressed()) {
            webView.zoomIn();
            return true;
        }
        // Ctrl+- para diminuir zoom
        if (keyCode == KeyEvent.KEYCODE_MINUS &&
                event.isCtrlPressed()) {
            webView.zoomOut();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
