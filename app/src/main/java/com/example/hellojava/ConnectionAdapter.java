package com.example.hellojava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {
    private List<String> connections;
    private Context context;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String connection);
    }

    public ConnectionAdapter(Context ctx, List<String> connections, OnDeleteClickListener listener) {
        this.context = ctx;
        this.connections = connections;
        this.deleteClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_connection, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String connection = connections.get(position);
        holder.textView.setText(connection);
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(connection));
    }

    @Override
    public int getItemCount() {
        return connections.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.connectionTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
