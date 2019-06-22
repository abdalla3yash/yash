package com.example.a3yashspc.yash;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    private RecyclerView myFriendsList;

    private DatabaseReference FriendsReference , UsersReference;

    private FirebaseAuth mAuth;

    String online_user_id;
    private View mymainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mymainView = inflater.inflate(R.layout.fragment_friends, container, false);

        myFriendsList = (RecyclerView)mymainView.findViewById(R.id.friends_list);

        mAuth =FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        FriendsReference.keepSynced(true);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference.keepSynced(true);
        myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mymainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>
                (
                        Friends.class,
                        R.layout.all_users_display_layout,
                        FriendsViewHolder.class,
                        FriendsReference
                )

        {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                viewHolder.setDate(model.getDate());

                final String list_user_id  = getRef(position).getKey();

                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online"))
                        {
                            String online_status = (String)dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online_status);
                        }

                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage,getContext());

                        viewHolder.mview.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] =new CharSequence[]
                                        {
                                                userName +"'s profile ",
                                                "send message"
                                        };
                                AlertDialog.Builder builder = new Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                             if (position ==0)
                                             {
                                                 Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                                 profileIntent.putExtra("visit_user_id",list_user_id);
                                                 startActivity(profileIntent);
                                             }
                                        if (position ==1) {
                                            if (dataSnapshot.child("online").exists()) {
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                chatIntent.putExtra("visit_user_id", list_user_id);
                                                chatIntent.putExtra("user_name", userName);
                                                startActivity(chatIntent);
                                            }
                                            else
                                            {
                                                UsersReference.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                        chatIntent.putExtra("visit_user_id", list_user_id);
                                                        chatIntent.putExtra("user_name", userName);
                                                        startActivity(chatIntent);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        myFriendsList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public FriendsViewHolder(View itemView){
            super(itemView);

            mview = itemView;

        }

        public void setDate(String date)
        {
            TextView sinceFriendDate = (TextView)mview.findViewById(R.id.all_user_status);
            sinceFriendDate.setText("Friends Since: \n" +date);
        }


        public void setUserName(String userName)
        {
            TextView userNameDisplay = (TextView)mview.findViewById(R.id.all_user_username);
            userNameDisplay.setText(userName);
        }

        public void setThumbImage(final String thumbImage ,final Context ctx) {

            final CircleImageView thumb_image= (CircleImageView) mview.findViewById(R.id.all_users_profile_image);




            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_profile_image).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.ic_profile_image).into(thumb_image);
                }
            });
        }

        public void setUserOnline(String online_status)
        {
            ImageView onlineStatusView = (ImageView)mview.findViewById(R.id.online_status);
            if (online_status.equals("true"))
            {
                onlineStatusView.setVisibility(View.VISIBLE);
            }
            else
            {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }

    }
}
