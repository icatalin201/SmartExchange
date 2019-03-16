package smartexchange.expert.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.model.Region;
import smartexchange.expert.util.Constants;

public class CheckboxAdapter extends RecyclerView.Adapter<CheckboxAdapter.CheckboxViewHolder> {

    private Context context;
    private List<Region> regionList;
    private String intentType;

    public CheckboxAdapter(Context context, List<Region> regionList, String intentType) {
        this.context = context;
        this.regionList = regionList;
        this.intentType = intentType;
    }

    public List<Region> getRegionList() {
        return this.regionList;
    }

    @NonNull
    @Override
    public CheckboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_card, parent,
                false);
        return new CheckboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxViewHolder holder, int position) {
        holder.checkBox.setText(regionList.get(position).getName());
        holder.flag.setBackground(context.getDrawable(regionList.get(position).getFlag()));
        if (intentType.equals(Constants.CONVERTER)) {
            if (regionList.get(position).getExchange().getConvertorFavorite().equals("1")) {
                holder.checkBox.setChecked(true);
            }
            else {
                holder.checkBox.setChecked(false);
            }
        }
        else if (intentType.equals(Constants.CALCULATOR)) {
            if (regionList.get(position).getExchange().getCalculatorFavorite().equals("1")) {
                holder.checkBox.setChecked(true);
            }
            else {
                holder.checkBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return regionList.size();
    }

    class CheckboxViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView flag;

        CheckboxViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            flag = itemView.findViewById(R.id.flag);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (intentType.equals(Constants.CALCULATOR)) {
                        regionList.get(getAdapterPosition()).getExchange().setCalculatorFavorite("1");
                    }
                    else {
                        regionList.get(getAdapterPosition()).getExchange().setConvertorFavorite("1");
                    }
                }
                else {
                    if (intentType.equals(Constants.CALCULATOR)) {
                        regionList.get(getAdapterPosition()).getExchange().setCalculatorFavorite("0");
                    }
                    else {
                        regionList.get(getAdapterPosition()).getExchange().setConvertorFavorite("0");
                    }
                }
            });
        }
    }
}
