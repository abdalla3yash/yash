package com.example.a3yashspc.yash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar rToolbar;
    private DatabaseReference storeUserDefaulttransferance;
    private EditText registerUserName , registerEmail , registerPassword;
    private Button createAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        variables();



    }

    private void variables() {

        registerUserName = (EditText)findViewById(R.id.register_name);
        registerEmail = (EditText)findViewById(R.id.register_email);
        registerPassword = (EditText)findViewById(R.id.register_password);
        createAccount = (Button)findViewById(R.id.register_btn);
        loadingBar = new ProgressDialog(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = registerUserName.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();

                RegisterAccount(name,email,password);
            }
        });



        rToolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(rToolbar);
        getSupportActionBar().setTitle("sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void RegisterAccount(final String name, final String email, final String password) {

        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(RegisterActivity.this, "please write your name..", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this, "please insert your email..", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this, "please insert password..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait... " );
            loadingBar.setMessage("اترمى انت دلوقتى على مانخلصلك ورق الاكونت");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String current_user_id =mAuth.getCurrentUser().getUid();
                                storeUserDefaulttransferance = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                                storeUserDefaulttransferance.child("user_name").setValue(name);
                                storeUserDefaulttransferance.child("password").setValue(password);
                                storeUserDefaulttransferance.child("email").setValue(email);
                                storeUserDefaulttransferance.child("user_status").setValue("Hey there, iam using yash");
                                storeUserDefaulttransferance.child("user_image").setValue("default_profile");
                                storeUserDefaulttransferance.child("device_token").setValue(deviceToken);
                                storeUserDefaulttransferance.child("user_thumb_image").setValue("default_image")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }
                                        });

                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "ERROR, Try Again... " , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
