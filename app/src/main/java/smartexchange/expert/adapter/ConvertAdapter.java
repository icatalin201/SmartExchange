package smartexchange.expert.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
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
import smartexchange.expert.sql.SqlService;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.ItemTouchHelperAdapter;
import smartexchange.expert.util.Utils;

public class ConvertAdapter extends RecyclerView.Adapter<ConvertAdapter.ConvertViewHolder>
        implements ItemTouchHelperAdapter{

    private Context context;
    private List<Region> regionList;
    private Fragment fragment;

    public ConvertAdapter(Context context, List<Region> regionList, Fragment fragment) {
        this.context = context;
        this.regionList = regionList;
        this.fragment = fragment;
    }

    public void addItem(Region region) {
        this.regionList.add(region);
        notifyItemInserted(0);
    }

    public void removeAll() {
        this.regionList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.convert_exchange_card, parent, false);
        return new ConvertViewHolder(view);
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

    @Override
    public void onBindViewHolder(@NonNull ConvertViewHolder holder, int position) {
        holder.regionFlagImage.setBackground(context.getDrawable(regionList.get(position).getFlag()));
        holder.currencyRateView.setText(String.format(Locale.ENGLISH,
                "%d %s = %f RON",
                regionList.get(position).getExchange().getMultiplier(),
                regionList.get(position).getExchange().getName(),
                regionList.get(position).getExchange().getValue()));
        holder.currencyInputValue.setText(String.valueOf(regionList.get(position).getValue()));
        holder.regionName.setText(regionList.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return regionList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        Region region = regionList.get(position);
        region.getExchange().setConvertorFavorite("0");
        regionList.remove(position);
        notifyItemRemoved(position);
        SqlService.updateExchange(context, region.getExchange());
    }

    class ConvertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView regionFlagImage;
        private TextView currencyRateView;
        private TextView currencyInputValue;
        private TextView regionName;
        private CardView regionCard;

        ConvertViewHolder(View itemView) {
            super(itemView);
            regionFlagImage = itemView.findViewById(R.id.region_flag);
            currencyInputValue = itemView.findViewById(R.id.input_currency);
            currencyRateView = itemView.findViewById(R.id.currency_rate);
            regionName = itemView.findViewById(R.id.region_name);
            regionName.setSelected(true);
            regionCard = itemView.findViewById(R.id.currency);
            regionCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof CardView) {
                Intent intent = new Intent(context, CalculatorActivity.class);
                intent.putExtra(Constants.REGION, regionList.get(getAdapterPosition()));
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation((AppCompatActivity) context, regionFlagImage,
                                "regionFlag");
                ((FragmentActivity) context).startActivityFromFragment(fragment, intent,
                        Constants.REGION_CALCULATOR_REQUEST_CODE, activityOptionsCompat.toBundle());
            }
        }
    }
}
