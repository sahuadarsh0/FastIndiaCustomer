package com.tecqza.gdm.fastindia;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class OrderDetailsExtraItemsAdapter extends RecyclerView.Adapter<OrderDetailsExtraItemsAdapter.VendorViewHolder> {

    private ArrayList<OrderedItemModel> items;
    private OrderedItemModel item;
    private Context context;
    private ProcessDialog processDialog;
    private SharedPrefs userSharedPrefs;
    TextView total;

    public OrderDetailsExtraItemsAdapter(ArrayList<OrderedItemModel> items, Context context, TextView total) {
        this.items = items;
        this.total=total;
        this.context = context;
        this.processDialog = new ProcessDialog(context, "PROCESSING");
        this.userSharedPrefs = new SharedPrefs(context, "USER");
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

        holder.item_name.setText(item.product);
        holder.id.setText(item.id);

        holder.amount.setText(item.amount);

        if(item.image.equals("1")){
            holder.item_name.setText("List in below image");
            String image_path=context.getString(R.string.file_base_url)+"list/"+item.product;
            Picasso.get().load(image_path).into(holder.list_image);
        }

        holder.remove.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, id;
        TextView amount;
        ImageView list_image;
        ImageView remove;

        public VendorViewHolder(@NonNull final View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            item_name = itemView.findViewById(R.id.item_name);
            amount = itemView.findViewById(R.id.details);

            list_image = itemView.findViewById(R.id.list_image);
            remove = itemView.findViewById(R.id.remove);

        }
    }

}
