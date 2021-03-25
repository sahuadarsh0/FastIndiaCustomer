package com.tecqza.gdm.fastindia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OrderPlaced extends AppCompatActivity {

    Button okk_btn;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        context=this;
        okk_btn=findViewById(R.id.okk_btn);

        okk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(context, OrderType.class);
//                startActivity(i);
//                finish();
            }
        });
    }
}
