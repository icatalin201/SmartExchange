package smartexchange.expert.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.model.Banknote;

public class BanknotesAdapter extends RecyclerView.Adapter<BanknotesAdapter.BanknotesViewHolder> {

    private Context context;
    private List<Banknote> banknoteList;

    public BanknotesAdapter(Context context, List<Banknote> banknoteList) {
        this.context = context;
        this.banknoteList = banknoteList;
    }

    public void addAll(List<Banknote> banknoteList) {
        this.banknoteList.addAll(banknoteList);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.banknoteList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BanknotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banknote_view, parent, false);
        return new BanknotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BanknotesViewHolder holder, int position) {
        holder.banknoteName.setText(banknoteList.get(position).getName());
        Glide.with(context)
                .load(context.getDrawable(banknoteList.get(position).getImage()))
                .into(holder.banknoteView);
    }

    @Override
    public int getItemCount() {
        return banknoteList.size();
    }

    class BanknotesViewHolder extends RecyclerView.ViewHolder {

        private ImageView banknoteView;
        private TextView banknoteName;

        BanknotesViewHolder(View itemView) {
            super(itemView);
            banknoteView = itemView.findViewById(R.id.banknote);
            banknoteName = itemView.findViewById(R.id.banknote_name);
        }
    }
}
