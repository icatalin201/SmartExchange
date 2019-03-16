package smartexchange.expert.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import smartexchange.expert.R;
import smartexchange.expert.activity.SelectRegionDialog;
import smartexchange.expert.adapter.BanknotesAdapter;
import smartexchange.expert.model.Region;
import smartexchange.expert.sql.SqlService;
import smartexchange.expert.sql.SqlStructure;
import smartexchange.expert.util.Constants;

public class BanknotesFragment extends Fragment implements View.OnClickListener {

    private ImageView regionFlag;
    private TextView regionName;
    private TextView regionExchangeName;
    private BanknotesAdapter banknotesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.banknotes_fragment, container, false);
        Objects.requireNonNull(getActivity()).setTitle(R.string.banknotes);
        LinearLayout regionLayout = view.findViewById(R.id.region_info_layout);
        regionFlag = view.findViewById(R.id.region_flag);
        regionName = view.findViewById(R.id.region_name);
        regionExchangeName = view.findViewById(R.id.region_exchange_name);
        regionLayout.setOnClickListener(this);
        Region region = SqlService.getExchangeList(getActivity(),
                SqlStructure.SqlData.exchange_name + " = ?",
                new String[] {Constants.RON}).get(0);
        regionFlag.setBackground(Objects.requireNonNull(getActivity()).getDrawable(region.getFlag()));
        regionName.setText(region.getName());
        regionExchangeName.setText(region.getExchange().getName());
        RecyclerView banknotesRecycler = view.findViewById(R.id.banknotes_recycler);
        banknotesAdapter = new BanknotesAdapter(getActivity(), new ArrayList<>());
        banknotesRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        banknotesRecycler.setAdapter(banknotesAdapter);
        banknotesRecycler.setItemAnimator(new DefaultItemAnimator());
        banknotesAdapter.addAll(SqlService.getBanknoteList(getActivity(), region.getExchange().getName()));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REGION_CHOOSE_REQUEST_CODE) {
            if (resultCode == Constants.REGION_CHOOSE_RESULT_CODE) {
                if (data != null) {
                    Region region = data.getParcelableExtra(Constants.REGION);
                    regionExchangeName.setText(region.getExchange().getName());
                    regionName.setText(region.getName());
                    regionFlag.setBackground(Objects.requireNonNull(getActivity()).getDrawable(region.getFlag()));
                    banknotesAdapter.removeAll();
                    banknotesAdapter.addAll(SqlService.getBanknoteList(getActivity(), region.getExchange().getName()));
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(getActivity(), SelectRegionDialog.class),
                Constants.REGION_CHOOSE_REQUEST_CODE);
    }
}
