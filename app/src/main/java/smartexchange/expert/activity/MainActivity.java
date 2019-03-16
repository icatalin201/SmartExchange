package smartexchange.expert.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.Objects;

import smartexchange.expert.R;
import smartexchange.expert.fragment.BanknotesFragment;
import smartexchange.expert.fragment.ConvertFragment;
import smartexchange.expert.fragment.ExchangesFragment;
import smartexchange.expert.notification.NotificationManager;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.Utils;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.loadLocale(this);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, new ExchangesFragment())
                .commit();
        navigationView.getMenu().getItem(0).setChecked(true);
        String notify = Utils.getFromSharedPreferences(Constants.NOTIFICATION, this, "notify");
        Utils.stopJob(this);
        if (notify.equals("1")) {
            Utils.launchJob(this, NotificationManager.class);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.calculator:
                fragment = new ExchangesFragment();
                break;
            case R.id.convertor:
                fragment = new ConvertFragment();
                break;
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class),
                        Constants.CHANGE_LANGUAGE_REQUEST_CODE);
                break;
            case R.id.share:
                share();
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.rating:
                rate();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        if (fragment != null) {
            Utils.changeFragment(this, fragment);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void share() {
        String packageName = getPackageName();
        Intent appShareIntent = new Intent(Intent.ACTION_SEND);
        appShareIntent.setType("text/plain");
        String extraText = String.format("Salut! Incearca noua aplicatie %s. \n", getResources().getString(R.string.app_name));
        extraText += "https://play.google.com/store/apps/details?id=" + packageName;
        appShareIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        startActivity(appShareIntent);
    }

    private void rate() {
        String packageName = getPackageName();
        String playStoreLink = "https://play.google.com/store/apps/details?id=" + packageName;
        Intent app = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink));
        startActivity(app);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CHANGE_LANGUAGE_REQUEST_CODE) {
            if (resultCode == Constants.CHANGE_LANGUAGE_RESULT_CODE) {
                recreate();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
