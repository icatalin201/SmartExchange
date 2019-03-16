package smartexchange.expert.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Objects;

import smartexchange.expert.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void getSpace(View view) {
        String packageName = "space.infinity.app";
        String playStoreLink = "https://play.google.com/store/apps/details?id=" + packageName;
        Intent spaceApp = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
        startActivity(spaceApp);
    }

    public void getMovies(View view) {
        String packageName = "app.mov.movieteca";
        String playStoreLink = "https://play.google.com/store/apps/details?id=" + packageName;
        Intent movieApp = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
        startActivity(movieApp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
