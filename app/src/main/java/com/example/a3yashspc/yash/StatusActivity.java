package com.example.a3yashspc.yash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private Button saveStatus;
    private TextView statusInput;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        saveStatus = (Button)findViewById(R.id.status_button);
        statusInput = (EditText)findViewById(R.id.status_input);
        String old_status = getIntent().getExtras().get("user_status").toString();
        statusInput.setText(old_status);

        saveStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_status = statusInput.getText().toString();

                ChangeProfileStatus(new_status);
            }
        });


    }

    private void ChangeProfileStatus(String new_status)
    {

        if (TextUtils.isEmpty(new_status))
        {
            Toast.makeText(StatusActivity.this, "ماتكتب حالتك ياعم انت هعصبنا عليك ليه", Toast.LENGTH_LONG).show();
        }
        else
        {
            progressDialog.setTitle("Change Profile status");
            progressDialog.setMessage("please wait, واترمى ف اى حته على مانخلص");
            progressDialog.show();
            databaseReference.child("user_status").setValue(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Intent i = new Intent(StatusActivity.this,SettingActivity.class);
                        startActivity(i);

                        Toast.makeText(StatusActivity.this, " it's done! ", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(StatusActivity.this, " ERROR ", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

    }


}
