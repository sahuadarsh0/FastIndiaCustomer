package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VendorProductsAdapter extends RecyclerView.Adapter<VendorProductsAdapter.VendorProductViewHolder> {

    ArrayList<ProductModel> product_list;
    ProductModel product;
    Context context;
    DatabaseHelper myDb;
    SharedPrefs userSharePrefs;
    TextView total, items_selected;

    public VendorProductsAdapter(ArrayList<ProductModel> product_list, Context context, TextView total, TextView items_selected) {
        this.product_list = product_list;
        this.context = context;
        myDb = new DatabaseHelper(context);
        this.userSharePrefs = new SharedPrefs(context, "USER");
        this.total = total;
        this.items_selected = items_selected;
    }

    @NonNull
    @Override
    public VendorProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vendors_product_list_item, parent, false);
        return new VendorProductsAdapter.VendorProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorProductViewHolder holder, int position) {

        product = product_list.get(position);

        holder.name.setText(product.name);
        holder.qty.setText(product.qty + " " + product.unit);
        holder.pamount.setText(product.pamount);
        holder.amount.setText(product.amount);
        holder.id.setText(product.vpid);


        holder.pamount.setPaintFlags(holder.pamount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String image_path = context.getString(R.string.file_base_url) + "products/" + product.image;
        Picasso.get().load(image_path).into(holder.image);

        if (Integer.parseInt(product.cqty) > 0) {
            holder.item_add_linearLayout.setVisibility(View.GONE);
            holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);
            holder.item_count.setText(product.cqty);
        }

        holder.item_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item_count = Integer.parseInt(holder.item_count.getText().toString());
                holder.item_count.setText(++item_count + "");
                myDb.updateQty(holder.id.getText().toString(), item_count + "");

                float added_amt = Float.parseFloat(holder.amount.getText().toString());
                total.setText((Float.parseFloat(total.getText().toString()) + added_amt) + "");

            }
        });

        holder.item_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item_count = Integer.parseInt(holder.item_count.getText().toString());
                if (--item_count > 0) {
                    holder.item_count.setText(item_count + "");
                    myDb.updateQty(holder.id.getText().toString(), item_count + "");
                } else {
                    myDb.deleteWhereVPID(holder.id.getText().toString());
                    holder.item_add_linearLayout.setVisibility(View.VISIBLE);
                    holder.item_add_sub_linearLayout.setVisibility(View.GONE);

                    int items = Integer.parseInt(items_selected.getText().toString());
                    items--;
                    items_selected.setText(items + "");
                }

                float removed_amt = Float.parseFloat(holder.amount.getText().toString());
                total.setText((Float.parseFloat(total.getText().toString()) - removed_amt) + "");
            }
        });

        holder.item_add_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.insertData(
                        holder.id.getText().toString(),
                        holder.name.getText().toString() + " " + holder.qty.getText().toString(),
                        "1",
                        "0",
                        userSharePrefs.getSharedPrefs("vendor_id"),
                        holder.amount.getText().toString()
                );
                holder.item_add_linearLayout.setVisibility(View.GONE);
                holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);

                float added_amt = Float.parseFloat(holder.amount.getText().toString());
                total.setText((Float.parseFloat(total.getText().toString()) + added_amt) + "");

                int items = Integer.parseInt(items_selected.getText().toString());
                items++;
                items_selected.setText(items + "");
            }
        });


    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }


    public class VendorProductViewHolder extends RecyclerView.ViewHolder {

        TextView name, qty, pamount, amount, id, item_count;
        ImageView image, item_minus, item_plus;
        LinearLayout item_add_linearLayout, item_add_sub_linearLayout;

        public VendorProductViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
            pamount = itemView.findViewById(R.id.pamount);
            amount = itemView.findViewById(R.id.amount);
            id = itemView.findViewById(R.id.id);
            image = itemView.findViewById(R.id.image);
            item_add_linearLayout = itemView.findViewById(R.id.item_add_linearLayout);
            item_add_sub_linearLayout = itemView.findViewById(R.id.item_add_sub_linearLayout);
            item_count = itemView.findViewById(R.id.item_count);

            item_plus = itemView.findViewById(R.id.item_plus);
            item_minus = itemView.findViewById(R.id.item_minus);


        }
    }


    public void updateList(ArrayList<ProductModel> newList) {
        product_list = new ArrayList<>();
        product_list.addAll(newList);
        notifyDataSetChanged();
    }


}
