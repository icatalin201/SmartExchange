package smartexchange.expert.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import smartexchange.expert.R;
import smartexchange.expert.activity.CalculatorActivity;
import smartexchange.expert.model.Region;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.Utils;

public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.ExchangeViewHolder> {

    private Context context;
    private List<Region> regionList;
    private Fragment fragment;

    public ExchangeAdapter(Context context, List<Region> regionList, Fragment fragment) {
        this.context = context;
        this.regionList = regionList;
        this.fragment = fragment;
    }

    public void modifyItems(Region sourceRegion) {
        for (Region region : regionList) {
            if (region.getName().equals(sourceRegion.getName())) {
                region.setValue(sourceRegion.getValue());
            }
            else {
                float computedValue = Utils.computeValueCurrency(sourceRegion, region);
                region.setValue(computedValue);
            }
        }
        notifyDataSetChanged();
    }

    public void addItem(Region region) {
        this.regionList.add(region);
        notifyDataSetChanged();
    }

    public void addAllItems(List<Region> regionList) {
        this.regionList.addAll(regionList);
        notifyDataSetChanged();
    }

    public void removeItem(Region region) {
        this.regionList.remove(region);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.regionList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExchangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                                .inflate(R.layout.exchange_card, parent, false);
        return new ExchangeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeViewHolder holder, int position) {
        holder.regionFlag.setBackground(context.getDrawable(regionList.get(position).getFlag()));
        holder.regionName.setText(regionList.get(position).getName());
        holder.regionValue.setText(String.format(Locale.ENGLISH, "%s %.2f",
                regionList.get(position).getSymbol(),
                regionList.get(position).getValue()));
        holder.regionRate.setText(String.format(Locale.ENGLISH,
                "%d %s = %f RON",
                regionList.get(position).getExchange().getMultiplier(),
                regionList.get(position).getExchange().getName(),
                regionList.get(position).getExchange().getValue()));
        holder.exchangeCard.setOnClickListener(new CardOnClickListener(regionList.get(position), holder));
    }

    @Override
    public int getItemCount() {
        return regionList.size();
    }

    class CardOnClickListener implements View.OnClickListener {

        private Region region;
        private ExchangeViewHolder holder;

        CardOnClickListener(Region region, ExchangeViewHolder holder) {
            this.holder = holder;
            this.region = region;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, CalculatorActivity.class);
            intent.putExtra(Constants.REGION, region);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation((FragmentActivity) context, holder.regionFlag, "regionFlag");
            ((FragmentActivity) context).startActivityFromFragment(fragment,
                    intent, Constants.REGION_CALCULATOR_REQUEST_CODE, optionsCompat.toBundle());
        }
    }

    class ExchangeViewHolder extends RecyclerView.ViewHolder {

        private CardView exchangeCard;
        private TextView regionName;
        private TextView regionRate;
        private TextView regionValue;
        private ImageView regionFlag;

        ExchangeViewHolder(View itemView) {
            super(itemView);
            exchangeCard = itemView.findViewById(R.id.region_card);
            regionFlag = itemView.findViewById(R.id.region_flag);
            regionRate = itemView.findViewById(R.id.region_rate);
            regionValue = itemView.findViewById(R.id.region_value);
            regionName = itemView.findViewById(R.id.region_name);
            regionName.setSelected(true);
            regionValue.setSelected(true);
        }
    }
}
