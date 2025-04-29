
package com.example.hellojava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ConnectionAdapter
        extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {

    private final List<Connection> items;
    private final OnClickListener clickListener;

    public interface OnClickListener {
        void onItemClick(Connection conn);
    }

    public ConnectionAdapter(List<Connection> items, OnClickListener lc) {
        this.items = items;
        this.clickListener = lc;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_connection, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder vh, int position) {
        Connection c = items.get(position);
        vh.ipText.setText(c.getIp());
        vh.portText.setText(String.valueOf(c.getPort()));
        vh.itemView.setOnClickListener(v -> clickListener.onItemClick(c));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView ipText, portText;
        ViewHolder(View itemView) {
            super(itemView);
            ipText   = itemView.findViewById(R.id.tv_ip);
            portText = itemView.findViewById(R.id.tv_port);
        }
    }
}
