package com.example.a3yashspc.yash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity {

    private Button needAccountbtn,haveAccountbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        needAccountbtn = (Button)findViewById(R.id.need_account_btn);
        haveAccountbtn = (Button)findViewById(R.id.have_account_btn);


        needAccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartPageActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        haveAccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartPageActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
