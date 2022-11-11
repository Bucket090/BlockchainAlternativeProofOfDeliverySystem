package com.example.blocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class DeliveryListAdapter extends RecyclerView.Adapter<DeliveryListAdapter.ViewHolder> {

    private List<Notification> nofitication_list;
    public DeliveryListAdapter(List<Notification> nlist){
        nofitication_list = nlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View PersonRowView = inflater.inflate(R.layout.notifications_list_view,parent,false);
        ViewHolder holder = new ViewHolder(PersonRowView);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification n = nofitication_list.get(position);
        holder.txt_delivery_device.setText(n.getDelivery_device());
        holder.txt_delivery_msg.setText(n.getDelivery_msg());
        holder.txt_delivery_timestamp.setText(n.getDelivery_timestamp());
    }

    @Override
    public int getItemCount() {
        return nofitication_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_delivery_device;
        TextView txt_delivery_msg;
        TextView txt_delivery_timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_delivery_device = itemView.findViewById(R.id.deviceregistrationIDTXT);
            txt_delivery_msg = itemView.findViewById(R.id.delivery_msgTXT);
            txt_delivery_timestamp = itemView.findViewById(R.id.deliveryTimestamptxt);
        }
    }
}
