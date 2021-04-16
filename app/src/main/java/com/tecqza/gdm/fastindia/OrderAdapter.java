package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VendorViewHolder> {

    private ArrayList<OrderItemsModel> orders;
    private OrderItemsModel orderItemsModel;
    private final Context context;
    private final ProcessDialog processDialog;
    private final SharedPrefs userSharedPrefs;

    public OrderAdapter(ArrayList<OrderItemsModel> orders, Context context) {
        this.orders = orders;
        this.context = context;
        this.processDialog = new ProcessDialog(context, "Loading..");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_orders, parent, false);

        return new OrderAdapter.VendorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final VendorViewHolder holder, int position) {
        orderItemsModel = orders.get(position);

        holder.customer_name.setText(orderItemsModel.customer_name);
        holder.state.setText(orderItemsModel.status);
        holder.id.setText(orderItemsModel.id);


        String pattern = "dd MMM, hh:mm aaa";

        DateFormat df = new SimpleDateFormat(pattern);
        Date dt = orderItemsModel.cDT;
        String todayAsString = df.format(dt);
        holder.date.setText(todayAsString);


        holder.state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new OrderDetailsFragment(context, holder.id.getText().toString());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
            }
        });

        holder.view_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment = new OrderDetailsFragment(context, holder.id.getText().toString());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
            }
        });

        if (holder.state.getText().toString().equals("PENDING")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.state.setBackgroundColor(context.getResources().getColor(R.color.yellow, null));
            }
        } else if (holder.state.getText().toString().equals("REJECTED")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.state.setBackgroundColor(context.getResources().getColor(R.color.red, null));
            }
        } else if (holder.state.getText().toString().equals("ACCEPTED")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.state.setBackgroundColor(context.getResources().getColor(R.color.glo_green, null));
            }
        } else if (holder.state.getText().toString().equals("DELIVERED")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.state.setBackgroundColor(context.getResources().getColor(R.color.blue, null));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.state.setBackgroundColor(context.getResources().getColor(R.color.blue, null));
            }
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    public class VendorViewHolder extends RecyclerView.ViewHolder {

        TextView customer_name, date, view_details, id;
        Button accept, reject, state;

        public VendorViewHolder(@NonNull final View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            customer_name = itemView.findViewById(R.id.customer_name);
            date = itemView.findViewById(R.id.date);
            view_details = itemView.findViewById(R.id.view_details);
            reject = itemView.findViewById(R.id.reject);
            accept = itemView.findViewById(R.id.accept);
            state = itemView.findViewById(R.id.state);

        }
    }

    public void updateList(ArrayList<OrderItemsModel> newList) {
        orders = new ArrayList<>();
        orders.addAll(newList);
        notifyDataSetChanged();

    }
}
