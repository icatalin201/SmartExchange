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
import smartexchange.expert.util.Utils;

public class SplashActivity extends AppCompatActivity {

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
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, Constants.SPLASH_SCREEN_DURATION);
    }

    private void updateRates() {
        try {
            String result = new CallBNR().execute(Constants.BNR_URL).get();
            Utils.parseStringXml(getApplicationContext(), result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() { }

    public static class CallBNR extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder xmlString = new StringBuilder();
            try {
                InputStream inputStream = null;
                URL url = new URL(strings[0]);
                HttpURLConnection cc = (HttpURLConnection) url.openConnection();
                cc.setReadTimeout(5000);
                cc.setConnectTimeout(5000);
                cc.setRequestMethod("GET");
                cc.setDoInput(true);
                int response = cc.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    inputStream = cc.getInputStream();
                }
                if (inputStream != null) {
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(isr);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        xmlString.append(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return xmlString.toString();
        }
    }
}
