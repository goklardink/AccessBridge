package com.example.accessbridgeproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accessbridgeproject.R;
import com.example.accessbridgeproject.models.InfoItem;
import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private Context context;
    private List<InfoItem> itemList;
    private OnItemClickListener listener;
    private com.example.accessbridgeproject.utils.TTSManager ttsManager;
    private com.example.accessbridgeproject.utils.SettingsManager settings;

    public interface OnItemClickListener {
        void onItemClick(InfoItem item);
    }

    public InfoAdapter(Context context, List<InfoItem> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
        this.ttsManager = new com.example.accessbridgeproject.utils.TTSManager(context);
        this.settings = new com.example.accessbridgeproject.utils.SettingsManager(context);
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_info, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItem item = itemList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(item);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (settings.isVoiceReaderEnabled()) {
                ttsManager.speak(item.getTitle() + ". " + item.getDescription());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}