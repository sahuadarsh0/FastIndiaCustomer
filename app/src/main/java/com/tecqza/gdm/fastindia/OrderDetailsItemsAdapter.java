package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class OrderDetailsItemsAdapter extends RecyclerView.Adapter<OrderDetailsItemsAdapter.VendorViewHolder> {

    private final ArrayList<OrderedItemModel> items;
    private OrderedItemModel item;
    private final Context context;
    private final ProcessDialog processDialog;
    private final SharedPrefs userSharedPrefs;

    public OrderDetailsItemsAdapter(ArrayList<OrderedItemModel> items, Context context) {
        this.items = items;
        this.context = context;
        this.processDialog = new ProcessDialog(context, "PROCESSING");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_list_items, parent, false);

        return new VendorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final VendorViewHolder holder, int position) {
        item = items.get(position);

        holder.item_name.setText(item.product);
        holder.id.setText(item.order_id);
        holder.qty.setText(item.qty);

        float amount = Float.parseFloat(item.amount) * Float.parseFloat(item.qty);
        holder.amount.setText(amount + "");

        if(item.image.equals("1")) {
            holder.item_name.setVisibility(View.GONE);
            holder.qty.setVisibility(View.GONE);
            String image_path = context.getString(R.string.file_base_url) + "list/" + item.product;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, qty, id, amount;
        public VendorViewHolder(@NonNull final View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            item_name = itemView.findViewById(R.id.item_name);
            qty = itemView.findViewById(R.id.qty);
            amount = itemView.findViewById(R.id.amount);


        }
    }
}
