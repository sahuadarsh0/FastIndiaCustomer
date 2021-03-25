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

public class OrderedItemsAdapter extends RecyclerView.Adapter<OrderedItemsAdapter.OrderedItemViewHolder> {

    ArrayList<OrderedItemModel> items;
    Context context;
    OrderedItemModel item;
    ProcessDialog processDialog;
    String status;
    public OrderedItemsAdapter(ArrayList<OrderedItemModel> items, Context context, String status){
        this.context=context;
        this.items=items;
        this.processDialog=new ProcessDialog(context,"PROCESSING...");
        this.status=status;
    }

    @NonNull
    @Override
    public OrderedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ordered_item_list_item, parent, false);
        return new OrderedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderedItemViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        item=items.get(position);

        holder.id.setText(item.id);
        if(item.image.equals("1")){
            holder.list_image.setVisibility(View.VISIBLE);
            holder.product.setVisibility(View.GONE);
            holder.qty.setVisibility(View.GONE);
            String image_path=context.getString(R.string.file_base_url)+"vendors/"+item.product;
            Picasso.get().load(image_path).into(holder.list_image);
        }else {
            holder.product.setText(item.product);
            holder.qty.setText(item.qty);
            holder.list_image.setVisibility(View.GONE);
        }

        holder.amt.setText(item.amount);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OrderedItemViewHolder extends RecyclerView.ViewHolder {
        TextView product, qty, id, amt;
        ImageView list_image;
        public OrderedItemViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image=itemView.findViewById(R.id.list_image);
            id=itemView.findViewById(R.id.id);
            product=itemView.findViewById(R.id.product);
            qty=itemView.findViewById(R.id.qty);
            amt=itemView.findViewById(R.id.amt);
        }
    }
}
