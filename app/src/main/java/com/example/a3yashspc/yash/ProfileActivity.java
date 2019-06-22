package com.example.a3yashspc.yash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button sendFriendRequest , removeFriendRequest ;
    private TextView profileName, profileStatus;
    private ImageView profileImage;
    private DatabaseReference UserReference , FriendRequestReference ,FriendsReference , NotificationsReference;
    private String CURRENT_STATE;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;
    private Toolbar pToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FriendRequestReference =FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FriendRequestReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        sender_user_id =mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReference.keepSynced(true);

        NotificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationsReference.keepSynced(true);

        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();


        sendFriendRequest = (Button)findViewById(R.id.profile_visit_add_friend);
        removeFriendRequest = (Button)findViewById(R.id.profile_friend_remove_friend);
        profileName = (TextView)findViewById(R.id.profile_visit_username);
        profileStatus = (TextView) findViewById(R.id.profile_visit_userstatus);
        profileImage = (ImageView)findViewById(R.id.profile_visit_user_image);

        CURRENT_STATE = "not_friends";

        pToolbar = (Toolbar)findViewById(R.id.profile_layout);
        setSupportActionBar(pToolbar);
        getSupportActionBar().setTitle("profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        UserReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.ic_profile_image).into(profileImage);

                FriendRequestReference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                    if (dataSnapshot.hasChild(receiver_user_id))
                                    {
                                        String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                                        if (req_type.equals("sent"))
                                        {
                                            CURRENT_STATE = "request_sent";
                                            sendFriendRequest.setText("Cancel Friend Request");


                                            removeFriendRequest.setVisibility(View.INVISIBLE);
                                            removeFriendRequest.setEnabled(false);

                                        }
                                        else if (req_type.equals("received"))
                                        {
                                            CURRENT_STATE = "request_received";
                                            sendFriendRequest.setText("Accept Friend Request");


                                            removeFriendRequest.setVisibility(View.VISIBLE);
                                            removeFriendRequest.setEnabled(true);

                                            removeFriendRequest.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    removeRequestFriend();
                                                }
                                            });

                                        }
                                    }
                                    else
                                    {
                                        FriendsReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(receiver_user_id))
                                                {
                                                    CURRENT_STATE = "friends";
                                                    sendFriendRequest.setText("UnFriend This Person");


                                                    removeFriendRequest.setVisibility(View.INVISIBLE);
                                                    removeFriendRequest.setEnabled(false);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        removeFriendRequest.setVisibility(View.INVISIBLE);
        removeFriendRequest.setEnabled(false);


        if (!sender_user_id.equals(receiver_user_id))
        {
            sendFriendRequest.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequest.setEnabled(false);


                    if (CURRENT_STATE.equals("not_friends"))
                    {
                        sendFriendRequestToFriend();
                    }

                    if (CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }

                    if (CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends"))
                    {
                        UnFriendsFriend();
                    }

                }
            });
        }
        else
        {
            removeFriendRequest.setVisibility(View.INVISIBLE);
            sendFriendRequest.setVisibility(View.INVISIBLE);


        }
    }

    private void removeRequestFriend() {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequest.setText("Send Friend Request");

                                                removeFriendRequest.setVisibility(View.INVISIBLE);
                                                removeFriendRequest.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void UnFriendsFriend()
    {

    FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            sendFriendRequest.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            sendFriendRequest.setText("send Friend Request");


                                            removeFriendRequest.setVisibility(View.INVISIBLE);
                                            removeFriendRequest.setEnabled(false);

                                        }
                                    }
                                });
                    }
                }
            });

    }


    private void AcceptFriendRequest() {
        android.icu.util.Calendar CalForDate = android.icu.util.Calendar.getInstance();
        android.icu.text.SimpleDateFormat currentDate = new android.icu.text.SimpleDateFormat("dd-MMMM-YYYY");
        final String saveCurrentDate = currentDate.format(CalForDate.getTime());
        FriendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        FriendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                sendFriendRequest.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                sendFriendRequest.setText("Unfriend this person");

                                                                                removeFriendRequest.setVisibility(View.INVISIBLE);
                                                                                removeFriendRequest.setEnabled(false);

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

    private void CancelFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequest.setText("Send Friend Request");

                                                removeFriendRequest.setVisibility(View.INVISIBLE);
                                                removeFriendRequest.setEnabled(false);

                                            }
                                       }
                                   });
                       }
                    }
                });
    }

    private void sendFriendRequestToFriend() {

        FriendRequestReference.child(sender_user_id).child(receiver_user_id).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    FriendRequestReference.child(receiver_user_id).child(sender_user_id).child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {

                                        HashMap<String,String> notaficationData = new HashMap<String, String>();
                                        notaficationData.put("from",sender_user_id);
                                        notaficationData.put("type","request");

                                        NotificationsReference.child(receiver_user_id).push().setValue(notaficationData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            sendFriendRequest.setEnabled(true);
                                                            CURRENT_STATE = "request_sent";
                                                            sendFriendRequest.setText("cancel Friend Request");


                                                            removeFriendRequest.setVisibility(View.INVISIBLE);
                                                            removeFriendRequest.setEnabled(false);
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                }
            }
        });

    }
}
