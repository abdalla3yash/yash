package com.example.a3yashspc.yash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView allUserList;
    private DatabaseReference allDatabaseUsersRefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);


        mToolbar = (Toolbar)findViewById(R.id.all_user_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ALL USERS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUserList = (RecyclerView)findViewById(R.id.all_user_list);
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(this));
        allDatabaseUsersRefrence = FirebaseDatabase.getInstance().getReference().child("Users");
        allDatabaseUsersRefrence.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<AllUsers, AllUserViewHolder> firebaseRecyclerAdapter
            = new FirebaseRecyclerAdapter<AllUsers, AllUserViewHolder>
                (AllUsers.class,
                        R.layout.all_users_display_layout,
                        AllUserViewHolder.class,
                        allDatabaseUsersRefrence)
        {
            @Override
            protected void populateViewHolder(AllUserViewHolder viewHolder, AllUsers model, final int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_thumb_image(getApplicationContext(),model.getUser_thumb_image());

                viewHolder.mview.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id = getRef(position).getKey();
                        Intent i = new Intent(AllUserActivity.this,ProfileActivity.class);
                        i.putExtra("visit_user_id",visit_user_id);
                        startActivity(i);
                    }
                });
            }

        };
        allUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public AllUserViewHolder(View itemView)
        {
            super(itemView);
            mview = itemView;
        }

        public void setUser_name(String user_name)
        {
            TextView name =(TextView) mview.findViewById(R.id.all_user_username);
            name.setText(user_name);
        }

        public void setUser_status(String user_status)
        {
            TextView status = (TextView)mview.findViewById(R.id.all_user_status);
            status.setText(user_status);

        }

        public void setUser_thumb_image(final Context ctx ,final String user_thumb_image)
        {
            final CircleImageView thumb_image= (CircleImageView) mview.findViewById(R.id.all_users_profile_image);




            Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_profile_image).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.ic_profile_image).into(thumb_image);
                }
            });

        }
    }
}
