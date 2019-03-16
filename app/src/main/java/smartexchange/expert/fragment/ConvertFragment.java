package smartexchange.expert.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import smartexchange.expert.R;
import smartexchange.expert.activity.SelectDialog;
import smartexchange.expert.adapter.ConvertAdapter;
import smartexchange.expert.model.Region;
import smartexchange.expert.sql.SqlService;
import smartexchange.expert.sql.SqlStructure;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.SimpleItemTouchHelperCallback;
import smartexchange.expert.util.Utils;

public class ConvertFragment extends Fragment {

    private ConvertAdapter convertAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.convert_fragment, container, false);
        setHasOptionsMenu(true);
        Objects.requireNonNull(getActivity()).setTitle(R.string.convertor);
        TextView dateUpdated = view.findViewById(R.id.date_updated);
        String date = Utils.getFromSharedPreferences(Constants.DATE_PREF,
                Objects.requireNonNull(getActivity()), Constants.date_key);
        String format = Utils.getFromSharedPreferences(Constants.PREFERED_DATE_FORMAT, getActivity(), "dateformat");
        try {
            date = Utils.dateToString(Utils.stringToDate(date, Constants.ANOTHER_DATE_FORMAT), format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateUpdated.setText(String.format(getString(R.string.rate_update), date));
        List<Region> regionList = SqlService.getExchangeList(getActivity(),
                SqlStructure.SqlData.convertor_favorite + " = ?", new String[] {"1"});
        RecyclerView convertRecycler = view.findViewById(R.id.convert_recycler);
        convertAdapter = new ConvertAdapter(getActivity(), regionList, ConvertFragment.this);
        convertRecycler.setAdapter(convertAdapter);
        convertRecycler.setItemAnimator(new DefaultItemAnimator());
        convertRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(convertAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(convertRecycler);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REGION_CALCULATOR_REQUEST_CODE) {
            if (resultCode == Constants.REGION_CALCULATOR_RESULT_CODE) {
                if (data != null) {
                    Region region = data.getParcelableExtra(Constants.REGION);
                    convertAdapter.modifyItems(region);
                }
            }
        }
        else if (requestCode == Constants.REGION_CHOOSE_REQUEST_CODE) {
            if (resultCode == Constants.REGION_CHOOSE_RESULT_CODE) {
                if (data != null) {
                    List<Region> regionList = data.getParcelableArrayListExtra(Constants.REGION);
                    convertAdapter.removeAll();
                    for (Region region : regionList) {
                        if (region.getExchange().getConvertorFavorite().equals("1")) {
                            convertAdapter.addItem(region);
                        }
                        SqlService.updateExchange(getActivity(), region.getExchange());
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_currency, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_currency:
                Intent intent = new Intent(getActivity(), SelectDialog.class);
                intent.putExtra(Constants.INTENT_TYPE, Constants.CONVERTER);
                startActivityForResult(intent, Constants.REGION_CHOOSE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
