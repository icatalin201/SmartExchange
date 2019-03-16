package smartexchange.expert.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import smartexchange.expert.R;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.ServerHelper;
import smartexchange.expert.util.Utils;

public class SplashActivity extends AppCompatActivity implements ServerHelper.OnRequestCompleted {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String first = Utils.getFromSharedPreferences(Constants.FIRST_TIME, this, "first");
        if (first.equals("no_value") || !first.equals("no")) {
            Utils.putInSharedPreferences(Constants.PREFERED_DATE_FORMAT, this,
                    "dateformat", Constants.DATE_FORMAT);
            Utils.putInSharedPreferences(Constants.NOTIFICATION, this, "notify", "1");
        }
        updateRates();
    }

    private void updateRates() {
        ServerHelper serverHelper = new ServerHelper(this ,getApplicationContext());
        serverHelper.updateRates(Constants.BNR_URL);
    }

    @Override
    public void onBackPressed() { }

    @Override
    public void onSuccess() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, Constants.SPLASH_SCREEN_DURATION);
    }

    @Override
    public void onFailure(String message) {

    }
}
