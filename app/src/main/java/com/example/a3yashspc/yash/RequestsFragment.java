package com.example.a3yashspc.yash;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView myRequestsList;
    private View myMainView;
    private DatabaseReference FriendsReference;
    private FirebaseAuth mAuth;
    String online_user_id;
    private DatabaseReference UsersReference , FriendsDatabaseRef ,FriendsReqDatabaseRef;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView =inflater.inflate(R.layout.fragment_requests, container, false);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        myRequestsList = (RecyclerView)myMainView.findViewById(R.id.requests_list);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef =FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        myRequestsList.setHasFixedSize(true);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Request ,RequestViewHolder> firebaseRecyclerAdapter
            = new FirebaseRecyclerAdapter<Request ,RequestViewHolder>
            (
                    Request.class,
                    R.layout.friend_request_all_users_layout,
                    RequestsFragment.RequestViewHolder.class,
                    FriendsReference
            )

        {


            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Request model, int position) {
                final String list_users_id =getRef(position).getKey();

                DatabaseReference get_type_req = getRef(position).child("request_type").getRef();

                get_type_req.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received"))
                            {
                                UsersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        String userStatus =dataSnapshot.child("user_status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumbImage(thumbImage,getContext());
                                        viewHolder.setUserStatus(userStatus);

                                        viewHolder.mview.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] =new CharSequence[]
                                                        {
                                                                "Accept Friend Request",
                                                                "Cancel Friend Request"
                                                        };
                                                AlertDialog.Builder builder = new Builder(getContext());
                                                builder.setTitle("Friend Req Options");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {
                                                        if (position ==0)
                                                        {
                                                            android.icu.util.Calendar CalForDate = android.icu.util.Calendar.getInstance();
                                                            android.icu.text.SimpleDateFormat currentDate = new android.icu.text.SimpleDateFormat("dd-MMMM-YYYY");
                                                            final String saveCurrentDate = currentDate.format(CalForDate.getTime());

                                                            FriendsDatabaseRef.child(online_user_id).child(list_users_id).child("date").setValue(saveCurrentDate)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid)
                                                                        {
                                                                            FriendsDatabaseRef.child(list_users_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    Toast.makeText(getContext(), "Friend Request Accepted Successfully", Toast.LENGTH_LONG).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }

                                                                                                    });

                                                                                        }
                                                                                    });
                                                                        }
                                                                    });

                                                        }
                                                        if (position ==1) {

                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(getContext(), "Cancel Friend Successfully", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });

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
                            else if (request_type.equals("sent"))
                            {

                                Button req_sent_btn = viewHolder.mview.findViewById(R.id.request_accept_btn);
                                req_sent_btn.setText("Req Sent");

                                viewHolder.mview.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);
                                UsersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        String userStatus =dataSnapshot.child("user_status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumbImage(thumbImage,getContext());
                                        viewHolder.setUserStatus(userStatus);

                                        viewHolder.mview.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[] =new CharSequence[]
                                                        {
                                                                "Cancel Friend Request",

                                                        };
                                                AlertDialog.Builder builder = new Builder(getContext());
                                                builder.setTitle("Friend Request sent");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {

                                                        if (position ==0) {

                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(getContext(), "Cancel Friend Successfully", Toast.LENGTH_LONG).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });

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
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

             }
        };

        myRequestsList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mview =itemView;
        }

        public void setUserName(String userName)
        {
            TextView userNameDisplay = (TextView)mview.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(userName);

        }


        public void setThumbImage(final String thumbImage ,final Context ctx) {

            final CircleImageView thumb_image= (CircleImageView) mview.findViewById(R.id.request_profile_image);




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

        public void setUserStatus(String userStatus) {
            TextView user_status= (TextView)mview.findViewById(R.id.request_profile_status);
            user_status.setText(userStatus);
        }


    }
}
