package smartexchange.expert.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.model.Region;
import smartexchange.expert.util.Constants;

public class BanknotesRegionAdapter
        extends RecyclerView.Adapter<BanknotesRegionAdapter.BanknotesViewHolder> {

    private Context context;
    private List<Region> regionList;

    public BanknotesRegionAdapter(Context context, List<Region> regionList) {
        this.context = context;
        this.regionList = regionList;
    }

    @NonNull
    @Override
    public BanknotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banknote_region_choose, parent, false);
        return new BanknotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BanknotesViewHolder holder, int position) {
        holder.regionName.setText(regionList.get(position).getName());
        holder.regionFlag.setBackground(context.getDrawable(regionList.get(position).getFlag()));
        holder.regionCard.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.putExtra(Constants.REGION, regionList.get(position));
            ((AppCompatActivity) context).setResult(Constants.REGION_CHOOSE_RESULT_CODE, intent);
            ((AppCompatActivity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return regionList.size();
    }

    class BanknotesViewHolder extends RecyclerView.ViewHolder {

        private TextView regionName;
        private ImageView regionFlag;
        private CardView regionCard;

        BanknotesViewHolder(View itemView) {
            super(itemView);
            regionCard = itemView.findViewById(R.id.region_card);
            regionName = itemView.findViewById(R.id.text);
            regionFlag = itemView.findViewById(R.id.flag);
        }
    }
}
