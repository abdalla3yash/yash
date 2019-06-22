package com.example.a3yashspc.yash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Info extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView  tumbler , github , twitter , facebook , gmail , instagram , linkedIn , whatsApp ,messenger , soundCloud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mToolbar = (Toolbar)findViewById(R.id.info_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("about");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        twitter = (CircleImageView)findViewById(R.id.twitter);
        facebook= (CircleImageView)findViewById(R.id.facebook);
        gmail = (CircleImageView)findViewById(R.id.gmail);
        instagram = (CircleImageView)findViewById(R.id.instagram);
        linkedIn = (CircleImageView)findViewById(R.id.linkedin);
        whatsApp = (CircleImageView)findViewById(R.id.whatsapp);
        messenger = (CircleImageView)findViewById(R.id.messenger);
        soundCloud = (CircleImageView)findViewById(R.id.sound_cloud);
        github = (CircleImageView)findViewById(R.id.github);
        tumbler = (CircleImageView)findViewById(R.id.tunmler);

        tumbler.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.tumblr.com/blog/i-3yash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        github.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://github.com/abdalla3yash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        twitter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://twitter.com/a_3yash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        linkedIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.linkedin.com/in/abdalla-ayash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        whatsApp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String toNumber = "+201091032414"; // contains spaces.
                toNumber = toNumber.replace("+", "").replace(" ", "");

                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                sendIntent.putExtra(Intent.EXTRA_TEXT,"hi developer .. your app is awesome!!");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        messenger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.messenger.com/t/abdalla.3yash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        soundCloud.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://soundcloud.com/a_3yash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.facebook.com/abdalla.3yash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        gmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://plus.google.com/u/1/113805469695497342556?pageId=none";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        instagram.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.instagram.com/a_3yash";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


    }
}
