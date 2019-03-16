package smartexchange.expert.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Objects;

import smartexchange.expert.R;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.Utils;

public class SettingsActivity extends AppCompatActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings);
        LinearLayout language = findViewById(R.id.language);
        language.setTag("language");
        LinearLayout dateFormat = findViewById(R.id.dateformat);
        dateFormat.setTag("dateformat");
        TextView currentLanguage = findViewById(R.id.current_language);
        TextView currentDateFormat = findViewById(R.id.current_dateformat);
        Switch autoUpdate = findViewById(R.id.autoupdate_switch);
        Switch notification = findViewById(R.id.notifications_switch);
        String notify = Utils.getFromSharedPreferences(Constants.NOTIFICATION, this, "notify");
        String dateformat = Utils.getFromSharedPreferences(Constants.PREFERED_DATE_FORMAT, this, "dateformat");
        currentDateFormat.setText(dateformat);
        String auto = Utils.getFromSharedPreferences(Constants.AUTO_UPDATE, this, "auto");
        autoUpdate.setChecked(auto.equals("1"));
        autoUpdate.setOnCheckedChangeListener(this);
        notification.setChecked(notify.equals("1"));
        notification.setOnCheckedChangeListener(this);
        autoUpdate.setTag("update");
        notification.setTag("notification");
        String currentL = Utils.getLanguage(this);
        if (currentL.equals("")) {
            currentL = "default";
        }
        currentLanguage.setText(currentL);
        language.setOnClickListener(this);
        dateFormat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch ((String) v.getTag()) {
            case "language":
                startActivityForResult(new Intent(this, LanguageDialog.class),
                        Constants.CHANGE_LANGUAGE_REQUEST_CODE);
                break;
            case "dateformat":
                startActivityForResult(new Intent(this, DateFormatDialog.class),
                        Constants.CHANGE_DATE_FORMAT_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CHANGE_LANGUAGE_REQUEST_CODE) {
            if (resultCode == Constants.CHANGE_LANGUAGE_RESULT_CODE) {
                if (data != null) {
                    String lang = data.getStringExtra(Constants.LANGUAGE);
                    Utils.changeLang(lang, this);
                    Utils.loadLocale(this);
                    recreate();
                }
            }
        }
        else if (requestCode == Constants.CHANGE_DATE_FORMAT_REQUEST_CODE) {
            if (resultCode == Constants.CHANGE_DATE_FORMAT_RESULT_CODE) {
                if (data != null) {
                    String format = data.getStringExtra(Constants.PREFERED_DATE_FORMAT);
                    Utils.putInSharedPreferences(Constants.PREFERED_DATE_FORMAT, this, "dateformat", format);
                    recreate();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(Constants.CHANGE_LANGUAGE_RESULT_CODE);
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        setResult(Constants.CHANGE_LANGUAGE_RESULT_CODE);
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getTag().equals("update")) {
                Utils.putInSharedPreferences(Constants.AUTO_UPDATE, this, "auto", "1");
            }
            else if (buttonView.getTag().equals("notification")) {
                Utils.putInSharedPreferences(Constants.NOTIFICATION, this, "notify", "1");
            }
        }
        else {
            if (buttonView.getTag().equals("update")) {
                Utils.putInSharedPreferences(Constants.AUTO_UPDATE, this, "auto", "0");
            }
            else if (buttonView.getTag().equals("notification")) {
                Utils.putInSharedPreferences(Constants.NOTIFICATION, this, "notify", "0");
            }
        }
    }
}
