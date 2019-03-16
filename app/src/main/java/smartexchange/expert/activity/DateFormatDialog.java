package smartexchange.expert.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.adapter.DateFormatAdapter;
import smartexchange.expert.util.Constants;

public class DateFormatDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_format_dialog);
        setTitle(R.string.dateformat);
        RecyclerView dateformatRecycler = findViewById(R.id.dateformat_recycler);
        DateFormatAdapter dateFormatAdapter = new DateFormatAdapter(this, new ArrayList<>());
        dateformatRecycler.setItemAnimator(new DefaultItemAnimator());
        dateformatRecycler.setAdapter(dateFormatAdapter);
        dateformatRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        dateFormatAdapter.addAll(getFormatList());
    }

    private List<String> getFormatList() {
        List<String> formatList = new ArrayList<>();
        formatList.add(Constants.DATE_FORMAT);
        formatList.add(Constants.DATE_FORMAT_V2);
        formatList.add(Constants.DATE_FORMAT_V3);
        formatList.add(Constants.ANOTHER_DATE_FORMAT);
        formatList.add(Constants.DATE_FORMAT_V4);
        formatList.add(Constants.DATE_FORMAT_V5);
        return formatList;
    }
}
