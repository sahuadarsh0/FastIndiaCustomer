package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.VendorViewHolder> {

    private final ArrayList<NotificationsModel> notifications;
    private NotificationsModel notification;
    private final Context context;
    private final ProcessDialog processDialog;
    private final SharedPrefs userSharedPrefs;

    public NotificationsAdapter(ArrayList<NotificationsModel> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
        this.processDialog = new ProcessDialog(context, "Loading..");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notifications, parent, false);
        return new NotificationsAdapter.VendorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final VendorViewHolder holder, int position) {
        notification = notifications.get(position);

        holder.title.setText(notification.title);
        holder.id.setText(notification.id);
        holder.message.setText(notification.message);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    public class VendorViewHolder extends RecyclerView.ViewHolder {

        TextView message, title, id;

        public VendorViewHolder(@NonNull final View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            message = itemView.findViewById(R.id.message);
            title = itemView.findViewById(R.id.title);
        }
    }


}
