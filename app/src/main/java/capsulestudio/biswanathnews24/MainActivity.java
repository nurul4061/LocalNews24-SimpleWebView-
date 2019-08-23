package capsulestudio.biswanathnews24;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity
{
    private String address="http://biswanathnews24.com/";
    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    final Context context = this;
    String refreshUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState==null)
        {
            Log.d("nurul","savedInstanceState is null");
        }
        else Log.d("nurul","savedInstanceState is not null");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.floatingShare);
        FloatingActionButton refresh = (FloatingActionButton) findViewById(R.id.floatingRefresh);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,webView.getUrl());
                startActivity(Intent.createChooser(shareIntent, "Share This!"));
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //String url=webView.getUrl();
                Log.d("nurul","Refresh");
                Log.d("nurul","url is: " + refreshUrl);
                if(refreshUrl != null)
                {
                    startWeb(refreshUrl);
                }

            }
        });

        frameLayout=(FrameLayout) findViewById(R.id.frameLayout);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webView = (WebView)findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new HelpClient());
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);


                if(newProgress==100)
                {
                    frameLayout.setVisibility(View.GONE);
                    //setTitle(view.getTitle());
                }

                super.onProgressChanged(view, newProgress);
            }
        });
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.setVerticalScrollBarEnabled(true);

        startWeb(address);
    }

    private void startWeb(String url)
    {
        try
        {
            refreshUrl=url;
            webView.loadUrl(url);
        }catch(Exception e)
        {
            Log.d("nurul",e.getMessage());
        }

    }


    private class HelpClient extends WebViewClient
    {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            refreshUrl=url;
            //Log.d("nurul:deprecated",url);
            //Log.d("nurul",String.valueOf(view.getId()));
            view.loadUrl(url);
            return true;

        }
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
        {
            refreshUrl=webView.getUrl();
            String url=request.getUrl().toString();
            refreshUrl=url;
            //Log.d("nurul:non-deprecated",url);
            //Log.d("nurul",String.valueOf(view.getId()));
            view.loadUrl(url);
            return true;
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
        {

            super.onReceivedError(view, request, error);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            try
            {
                webView.stopLoading();
            } catch (Exception e) {
            }

            if (webView.canGoBack())
            {
                webView.goBack();
            }

            webView.loadUrl("file:///android_asset/index.html");
            /*final String url=webView.getUrl();
            startWeb(url);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Check your internet connection and try again.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    startWeb(url);
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    finish();

                }
            });

            alertDialog.show(); */

            super.onReceivedError(view, errorCode, description, failingUrl);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_back)
        {
            back();
            return true;
        }

        if (id == R.id.action_forward)
        {
            forward();
            return true;
        }
        if (id == R.id.action_exit)
        {
            finish();
            System.exit(0);
            return true;
        }
        if (id == R.id.action_contact)
        {
            startActivity(new Intent(MainActivity.this,ContactUs.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
    {
        Log.d("nurul","onkeydown");
        if (paramInt == 4)
        {
            if(webView.canGoBack()) back();
            else
            {
                new AlertDialog.Builder(this).setTitle("Exit Application").setMessage("Are you really exit this application").setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                        MainActivity.this.finish();
                    }
                }).setNeutralButton("Home Page", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                        startWeb(address);
                    }
                }).setNegativeButton("No", null).show();
            }

            return true;
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    private void back()
    {
        if(webView.canGoBack())
        {
            webView.goBack();

            //refreshUrl=webView.getUrl();
            //Log.d("nurul","url is: " + refreshUrl);
        }
    }

    private void forward()
    {
        if(webView.canGoForward())
        {
            webView.goForward();
            //refreshUrl=webView.getUrl();
            //Log.d("nurul","url is: " + refreshUrl);
        }
    }
}
