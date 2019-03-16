package smartexchange.expert.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.List;

import smartexchange.expert.R;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.Utils;

public class DateFormatAdapter extends RecyclerView.Adapter<DateFormatAdapter.DateFormatViewHolder> {

    private Context context;
    private List<String> dateFormats;

    public DateFormatAdapter(Context context, List<String> dateFormats) {
        this.context = context;
        this.dateFormats = dateFormats;
    }

    public void addAll(List<String> dateFormats) {
        this.dateFormats.addAll(dateFormats);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateFormatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.language_view, parent, false);
        return new DateFormatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateFormatViewHolder holder, int position) {
        holder.radioButton.setText(dateFormats.get(position));
        String preferedFormat = Utils.getFromSharedPreferences(Constants.PREFERED_DATE_FORMAT, context, "dateformat");
        holder.radioButton.setChecked(preferedFormat.equals(dateFormats.get(position)));
        holder.radioButton.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.putExtra(Constants.PREFERED_DATE_FORMAT, holder.radioButton.getText().toString());
            ((AppCompatActivity) context).setResult(Constants.CHANGE_DATE_FORMAT_RESULT_CODE, intent);
            ((AppCompatActivity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return dateFormats.size();
    }

    class DateFormatViewHolder extends RecyclerView.ViewHolder {

        private RadioButton radioButton;

        DateFormatViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio);
        }
    }
}
