package smartexchange.expert.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.adapter.CheckboxAdapter;
import smartexchange.expert.model.Region;
import smartexchange.expert.sql.SqlService;
import smartexchange.expert.util.Constants;

public class SelectDialog extends AppCompatActivity {

    private CheckboxAdapter checkboxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dialog);
        setTitle(R.string.favorites);
        List<Region> regionList = SqlService.getExchangeList(this, null, null);
        RecyclerView selectRecycler = findViewById(R.id.select_recycler);
        String intentType = getIntent().getStringExtra(Constants.INTENT_TYPE);
        checkboxAdapter = new CheckboxAdapter(this, regionList, intentType);
        selectRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        selectRecycler.setItemAnimator(new DefaultItemAnimator());
        selectRecycler.setAdapter(checkboxAdapter);
    }

    public void addItems(View view) {
        List<Region> toAdd = checkboxAdapter.getRegionList();
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constants.REGION, (ArrayList<? extends Parcelable>) toAdd);
        setResult(Constants.REGION_CHOOSE_RESULT_CODE, intent);
        finish();
    }
}
