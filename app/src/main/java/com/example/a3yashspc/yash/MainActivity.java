package com.example.a3yashspc.yash;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import static com.example.a3yashspc.yash.R.menu.main_menu;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;

    private FirebaseAuth mAuth;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;
    FirebaseUser currentUser;
    private DatabaseReference UsersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            String online_user_id = mAuth.getCurrentUser().getUid();
            UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        }


        //tabs for mainActivity
        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);


        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("yash");

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            LogOutUser();
        }
        else if (currentUser != null)
        {
            UsersReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null)
        {
            UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void LogOutUser() {
        Intent i = new Intent(MainActivity.this,StartPageActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){

            case R.id.main_lagout_menu:
                if (currentUser != null)
                {
                    UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
                }

                mAuth.signOut();
                LogOutUser();
                break;

            case R.id.main_setting_menu:
                    Intent i = new Intent(MainActivity.this,SettingActivity.class);
                    startActivity(i);
                break;

            case R.id.main_about_menu :
                Intent y = new Intent(MainActivity.this,Info.class);
                startActivity(y);
                break;

            case R.id.main_all_user_menu :
                Intent a = new Intent(MainActivity.this,AllUserActivity.class);
                startActivity(a);
                break;

            default:
                break;

        }
        return true;
    }
}
