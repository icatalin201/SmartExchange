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
import smartexchange.expert.model.Language;
import smartexchange.expert.util.Constants;
import smartexchange.expert.util.Utils;

public class LanguageAdapter
        extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private Context context;
    private List<Language> languageList;

    public LanguageAdapter(Context context, List<Language> languageList) {
        this.context = context;
        this.languageList = languageList;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.language_view, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        holder.radioButton.setText(languageList.get(position).getName());
        String name = languageList.get(position).getName();
        String lang = "ro";
        if (name.equals(context.getString(R.string.en))) {
            lang = "en";
        }
        else if (name.equals(context.getString(R.string.fr))) {
            lang = "fr";
        }
        String currentLang = Utils.getLanguage(context);
        if (currentLang.equals(lang)) {
            holder.radioButton.setChecked(true);
        }
        else {
            holder.radioButton.setChecked(false);
        }
        String finalLang = lang;
        holder.radioButton.setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.putExtra(Constants.LANGUAGE, finalLang);
            ((AppCompatActivity) context).setResult(Constants.CHANGE_LANGUAGE_RESULT_CODE, intent);
            ((AppCompatActivity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {

        private RadioButton radioButton;

        LanguageViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio);
        }
    }
}
