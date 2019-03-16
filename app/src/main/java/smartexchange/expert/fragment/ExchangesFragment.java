package smartexchange.expert.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import smartexchange.expert.R;
import smartexchange.expert.activity.SelectDialog;
import smartexchange.expert.activity.SplashActivity;
import smartexchange.expert.adapter.ExchangeAdapter;
import smartexchange.expert.model.Region;
import smartexchange.expert.sql.SqlService;
import smartexchange.expert.sql.SqlStructure;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.ServerHelper;
import smartexchange.expert.util.Utils;

public class ExchangesFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, ServerHelper.OnRequestCompleted {

    private ExchangeAdapter exchangeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exchange_fragment, container, false);
        Context context = getActivity();
        setHasOptionsMenu(true);
        Objects.requireNonNull(getActivity()).setTitle(R.string.calculator);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            swipeRefreshLayout.setColorSchemeColors(getActivity().getColor(R.color.colorPrimary),
                    getActivity().getColor(R.color.colorPrimaryLight),
                    getActivity().getColor(R.color.colorPrimaryDark));
        }
        else {
            swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimary),
                    getActivity().getResources().getColor(R.color.colorPrimaryLight),
                    getActivity().getResources().getColor(R.color.colorPrimaryDark));
        }
        TextView dateUpdated = view.findViewById(R.id.date_updated);
        String date = Utils.getFromSharedPreferences(Constants.DATE_PREF,
                Objects.requireNonNull(context), Constants.date_key);
        String format = Utils.getFromSharedPreferences(Constants.PREFERED_DATE_FORMAT, getActivity(), "dateformat");
        try {
            date = Utils.dateToString(Utils.stringToDate(date, Constants.ANOTHER_DATE_FORMAT), format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateUpdated.setText(String.format(getString(R.string.rate_update), date));
        exchangeAdapter = new ExchangeAdapter(context, new ArrayList<>(),
                ExchangesFragment.this);
        RecyclerView exchangeRecycler = view.findViewById(R.id.exchange_recycler);
        exchangeRecycler.setAdapter(exchangeAdapter);
        exchangeRecycler.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        exchangeRecycler.setItemAnimator(new DefaultItemAnimator());
        List<Region> regionList = SqlService.getExchangeList(context,
                SqlStructure.SqlData.calculator_favorite + " = ?", new String[]{"1"});
        exchangeAdapter.addAllItems(regionList);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REGION_CALCULATOR_REQUEST_CODE) {
            if (resultCode == Constants.REGION_CALCULATOR_RESULT_CODE) {
                if (data != null) {
                    Region region = data.getParcelableExtra(Constants.REGION);
                    exchangeAdapter.modifyItems(region);
                }
            }
        }
        else if (requestCode == Constants.REGION_CHOOSE_REQUEST_CODE) {
            if (resultCode == Constants.REGION_CHOOSE_RESULT_CODE) {
                if (data != null) {
                    List<Region> regionList = data.getParcelableArrayListExtra(Constants.REGION);
                    exchangeAdapter.removeAll();
                    for (Region region : regionList) {
                        if (region.getExchange().getCalculatorFavorite().equals("1")) {
                            exchangeAdapter.addItem(region);
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
                intent.putExtra(Constants.INTENT_TYPE, Constants.CALCULATOR);
                startActivityForResult(intent, Constants.REGION_CHOOSE_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        ServerHelper serverHelper = new ServerHelper(this, getActivity());
        serverHelper.updateRates(Constants.BNR_URL);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onSuccess() {
        exchangeAdapter.removeAll();
        List<Region> regionList = SqlService.getExchangeList(getActivity(),
                SqlStructure.SqlData.calculator_favorite + " = ?", new String[]{"1"});
        exchangeAdapter.addAllItems(regionList);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(getActivity(),
                String.format("A aparut o eroare! Info: %s", message), Toast.LENGTH_LONG).show();
    }
}
