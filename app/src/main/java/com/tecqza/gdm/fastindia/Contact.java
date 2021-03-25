package com.tecqza.gdm.fastindia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Button call, whatsapp, email;
        call = findViewById(R.id.button_upload1);
        whatsapp = findViewById(R.id.button_upload2);
        email = findViewById(R.id.button_upload3);
        call.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:18001030770"));
            startActivity(intent);
        });
        whatsapp.setOnClickListener(v -> {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918564999333"));
            startActivity(browserIntent);

        });
        email.setOnClickListener(v -> {
            sendEmail();
        });

    }


    void sendEmail() {
        String[] TO = {"info@tecqza.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.setType("message/rfc822");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Contact.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}

