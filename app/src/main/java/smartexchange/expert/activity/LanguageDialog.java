package smartexchange.expert.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.adapter.LanguageAdapter;
import smartexchange.expert.model.Language;

public class LanguageDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_dialog);
        setTitle(R.string.choose_language);
        RecyclerView languageRecycler = findViewById(R.id.language_recycler);
        LanguageAdapter languageAdapter = new LanguageAdapter(this, getLanguageList());
        languageRecycler.setAdapter(languageAdapter);
        languageRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        languageRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    private List<Language> getLanguageList() {
        List<Language> languageList = new ArrayList<>();
        Language romanian = new Language(getResources().getString(R.string.ro), R.drawable.ic_flag_of_romania);
        Language french = new Language(getResources().getString(R.string.fr), R.drawable.ic_flag_of_france);
        Language english = new Language(getResources().getString(R.string.en), R.drawable.ic_flag_of_the_united_kingdom);
        languageList.add(romanian);
        languageList.add(french);
        languageList.add(english);
        return languageList;
    }
}
