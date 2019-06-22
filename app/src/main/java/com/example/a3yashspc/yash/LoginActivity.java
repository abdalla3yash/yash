package com.example.a3yashspc.yash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView userEmail,userPassword;
    private Button login;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail=(TextView) findViewById(R.id.login_email);
        userPassword=(TextView) findViewById(R.id.login_password);
        login = (Button)findViewById(R.id.login_signin);
        loading = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("sign in");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();

                LoginUserAccount(email,password);
            }
        });
    }
    private void LoginUserAccount(String email, String password) {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this, "ياعم دخل ام الاكونت بتاعك ", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this, "الباسورد ونبي عشان هتشل ", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loading.setTitle("Login Account");
            loading.setMessage("بص العصفورة");
            loading.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    String online_user_id = mAuth.getCurrentUser().getUid();
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                    usersReference.child(online_user_id).child("device_token").setValue(deviceToken)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            });

                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "يو هاف ايرور امسح الاكونت والباسورد واكتبهم تانى", Toast.LENGTH_SHORT).show();
                                }
                                loading.dismiss();
                        }
                    });
        }

    }
}
