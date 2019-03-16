package smartexchange.expert.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.adapter.BanknotesRegionAdapter;
import smartexchange.expert.model.Region;
import smartexchange.expert.sql.SqlService;

public class SelectRegionDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region_dialog);
        setTitle(R.string.select_region);
        RecyclerView recyclerView = findViewById(R.id.region_recycler);
        List<Region> regionList = SqlService.getExchangeList(this, null, null);
        BanknotesRegionAdapter banknotesRegionAdapter = new BanknotesRegionAdapter(this, regionList);
        recyclerView.setAdapter(banknotesRegionAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
