package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CartExtraItemsAdapter extends RecyclerView.Adapter<CartExtraItemsAdapter.VendorViewHolder> {

    private final ArrayList<CartItemsModel> items;
    private CartItemsModel item;
    private final Context context;
    private final ProcessDialog processDialog;
    private final SharedPrefs userSharedPrefs;
    DatabaseHelper myDb;

    public CartExtraItemsAdapter(ArrayList<CartItemsModel> items, Context context) {
        this.items = items;
        this.context = context;
        this.processDialog = new ProcessDialog(context, "PROCESSING");
        this.userSharedPrefs = new SharedPrefs(context, "USER");

        myDb = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_list_extra_items, parent, false);

        return new VendorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final VendorViewHolder holder, int position) {
        item = items.get(position);

        holder.item_name.setText(item.name);
        holder.id.setText(item.id);
        holder.details.setText(item.extra_qty);

//        if(item.image.equals("1")) {
//            holder.item_name.setVisibility(View.GONE);
//            holder.qty.setVisibility(View.GONE);
//            String image_path = context.getString(R.string.file_base_url) + "list/" + item.product;
//        }


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDb.delete(holder.id.getText().toString());
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new AddExtraItemsFragment(context);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, details, id;
        ImageView remove;

        public VendorViewHolder(@NonNull final View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            item_name = itemView.findViewById(R.id.item_name);
            details = itemView.findViewById(R.id.details);
            remove = itemView.findViewById(R.id.remove);


        }
    }
}
