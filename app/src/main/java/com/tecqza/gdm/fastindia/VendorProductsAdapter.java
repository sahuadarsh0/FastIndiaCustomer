package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

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
        setHasStableIds(true);
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

        holder.grocery.setVisibility(View.GONE);
        holder.customizable.setVisibility(View.GONE);
        holder.stock_out.setVisibility(View.GONE);


        Cursor res = myDb.getItemOfVPId(product_list.get(position).vpid);

        int qty = 0;
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                qty = Integer.parseInt(res.getString(3));
            }
        }

        if (qty > 0) {
            holder.item_add_linearLayout.setVisibility(View.GONE);
            holder.qty.setText(qty + "");
            holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);
        } else {
            holder.item_add_linearLayout.setVisibility(View.VISIBLE);
            holder.qty.setText("1");
            holder.item_add_sub_linearLayout.setVisibility(View.GONE);
        }


        ArrayList<VarietiesModel> varieties_list = new ArrayList<>();

        try {
            if (product_list.get(position).varieties.equals("NA")) {
                holder.grocery.setVisibility(View.GONE);
            } else {
                holder.grocery.setVisibility(View.VISIBLE);
                JSONArray varietiesArray = new JSONArray(product_list.get(position).varieties);
                varieties_list = VarietiesModel.fromJson(varietiesArray);
            }

        } catch (Exception ignored) {

        }

        ArrayList<VarietiesModel> finalVarieties_list = varieties_list;


        try {
            holder.name.setText(product.name);
            holder.qty.setText(varieties_list.get(0).qty);
            holder.pamount.setText(varieties_list.get(0).mrp);
            holder.amount.setText(varieties_list.get(0).selling_price);
            holder.id.setText(product.vpid);
            holder.desc.setText(product.product_description);

            if (varieties_list.get(0).mrp.equals(varieties_list.get(0).selling_price)){
                holder.pamount.setVisibility(View.INVISIBLE);
                holder.pamount.setText("");
            }

        } catch (Exception ignored) {

        }


        holder.pamount.setPaintFlags(holder.pamount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String image_path = context.getString(R.string.file_base_url) + "products/" + product.image;
        Picasso.get().load(image_path).into(holder.image);

        if (Integer.parseInt(product.cqty) > 0) {
            holder.item_add_linearLayout.setVisibility(View.GONE);
            holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);
            holder.item_count.setText(product.cqty);
        }
        if (finalVarieties_list.size() > 1) {
            holder.customizable.setVisibility(View.VISIBLE);
        } else {
            holder.customizable.setVisibility(View.GONE);
        }
        holder.item_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalVarieties_list.size() > 1) {

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();

                    VarietyBottom varietyBottom = new VarietyBottom(context, holder.name.getText().toString(), holder.desc.getText().toString(), product_list.get(position), holder.item_count, total);
                    varietyBottom.show(activity.getSupportFragmentManager(),
                            "add_photo_dialog_fragment");

                } else {

                    int item_count = Integer.parseInt(holder.item_count.getText().toString());
                    holder.item_count.setText(++item_count + "");
                    myDb.updateQty(holder.id.getText().toString(), item_count + "");


                    Utility utility = new Utility(context);
                    total.setText(utility.getCartItemsTotalAmount() + "");
                }
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

                Utility utility = new Utility(context);
                total.setText(utility.getCartItemsTotalAmount() + "");
            }
        });

        holder.item_add_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.item_add_linearLayout.setVisibility(View.GONE);
                holder.item_add_sub_linearLayout.setVisibility(View.VISIBLE);

                Utility utility = new Utility(context);
                total.setText(utility.getCartItemsTotalAmount() + "");


                if (finalVarieties_list.size() > 1) {

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();

                    VarietyBottom varietyBottom = new VarietyBottom(context, holder.name.getText().toString(), holder.desc.getText().toString(), product_list.get(position), holder.item_count, total);
                    varietyBottom.show(activity.getSupportFragmentManager(),
                            "add_photo_dialog_fragment");


                } else {


                    myDb.insertData(
                            holder.id.getText().toString(),
                            holder.name.getText().toString() + " " + holder.qty.getText().toString(),
                            "1",
                            "0",
                            userSharePrefs.getSharedPrefs("vendor_id"),
                            holder.amount.getText().toString(),
                            finalVarieties_list.get(0).id

                    );

                    float added_amt = Float.parseFloat(holder.amount.getText().toString());
                    total.setText((Float.parseFloat(total.getText().toString()) + added_amt) + "");

                }

                int items = Integer.parseInt(items_selected.getText().toString());
                items++;
                items_selected.setText(items + "");


            }
        });



        if (product_list.get(position).in_stock.equals("0")){
            holder.stock_out.setVisibility(View.VISIBLE);
            holder.item_minus.setClickable(false);
            holder.item_plus.setClickable(false);
            holder.item_add_linearLayout.setClickable(false);
        }else{
            holder.stock_out.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }



    public class VendorProductViewHolder extends RecyclerView.ViewHolder {

        TextView name, qty, pamount, amount, id, desc, item_count, customizable;
        ImageView image, item_minus, item_plus;
        LinearLayout item_add_linearLayout, item_add_sub_linearLayout;
        ConstraintLayout grocery,stock_out;
        CardView cardView ;

        public VendorProductViewHolder(@NonNull View itemView) {
            super(itemView);
            grocery = itemView.findViewById(R.id.grocery);
            cardView = itemView.findViewById(R.id.cardView);
            stock_out = itemView.findViewById(R.id.stock_out);
            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
            pamount = itemView.findViewById(R.id.pamount);
            amount = itemView.findViewById(R.id.amount);
            id = itemView.findViewById(R.id.id);
            desc = itemView.findViewById(R.id.desc);
            image = itemView.findViewById(R.id.image);
            item_add_linearLayout = itemView.findViewById(R.id.item_add_linearLayout);
            item_add_sub_linearLayout = itemView.findViewById(R.id.item_add_sub_linearLayout);
            item_count = itemView.findViewById(R.id.item_count);

            item_plus = itemView.findViewById(R.id.item_plus);
            item_minus = itemView.findViewById(R.id.item_minus);
            customizable = itemView.findViewById(R.id.customizable);


        }
    }


    public void updateList(ArrayList<ProductModel> newList) {
        product_list = new ArrayList<>();
        product_list.addAll(newList);
        notifyDataSetChanged();
    }


}
