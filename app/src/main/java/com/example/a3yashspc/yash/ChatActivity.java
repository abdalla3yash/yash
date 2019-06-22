package com.example.a3yashspc.yash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId;
    private String messageReceiverName;
    private Toolbar chatToolbar;
    private TextView userNameTitle , userLastSeen;
    private CircleImageView userChatProfileImage;
    private DatabaseReference rootRef;
    private RecyclerView userMessageList;
    private ImageButton sendMessageButton , selectImageButton;
    private EditText inputMessageText;
    private FirebaseAuth mAuth;
    private String messageSenderId;
    private final List<Messages> messageList =new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private static int Gallery_pick = 1;
    private StorageReference MessageImageStorageRef ;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("user_name").toString();
        MessageImageStorageRef = FirebaseStorage.getInstance().getReference().child("Messages_Pictures");


        chatToolbar =(Toolbar)findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);

        loadingBar = new ProgressDialog(this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view =layoutInflater.inflate(R.layout.chat_custom_bar,null);
            actionBar.setCustomView(action_bar_view);

        userNameTitle = (TextView)findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView)findViewById(R.id.custom_profile_last_seen);
        userChatProfileImage =(CircleImageView)findViewById(R.id.custom_profile_picture);
        userMessageList = (RecyclerView)findViewById(R.id.message_list_of_users);
        messageAdapter = new MessageAdapter(messageList);
        sendMessageButton = (ImageButton)findViewById(R.id.send_message);
        selectImageButton = (ImageButton)findViewById(R.id.select_image);
        inputMessageText = (EditText) findViewById(R.id.input_message);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);


        FetchMessages();

        userNameTitle.setText(messageReceiverName);
        rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.with(ChatActivity.this).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_profile_image).into(userChatProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ChatActivity.this).load(userThumb).placeholder(R.drawable.ic_profile_image).into(userChatProfileImage);
                    }
                });

                if (online.equals("true"))
                {
                    userLastSeen.setText("Online");
                }
                else
                {
                    LastSeenTime getTime = new LastSeenTime();
                    long last_seen = Long.parseLong(online);

                    String lastSeenDisplayTime = getTime.getTimeAgo(last_seen, getApplicationContext()).toString();

                    userLastSeen.setText(lastSeenDisplayTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        selectImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data!= null)
        {
            Uri ImagrUri = data.getData();
            loadingBar.setTitle("Sending chat image");
            loadingBar.setMessage("السما اتخلقت ف سبع تيام ميجراش حاجه اما تستني نص ساعه ع الصورة اما توصل");
            loadingBar.show();

            final String message_sender_ref="Messages/"+messageSenderId+"/"+messageReceiverId;
            final String message_receiver_ref="Messages/"+messageReceiverId+"/"+messageSenderId;

            DatabaseReference user_message_key = rootRef.child("messages").child(messageSenderId)
                    .child(messageSenderId).push();
            final String message_push_id = user_message_key.getKey();

            StorageReference filepath = MessageImageStorageRef.child(message_push_id + ".jpg");
            filepath.putFile(ImagrUri).addOnCompleteListener(new OnCompleteListener<TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<TaskSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();

                        final Map messageTextBox = new HashMap();

                        messageTextBox.put("message",downloadUrl);
                        messageTextBox.put("seen",false);
                        messageTextBox.put("type","image");
                        messageTextBox.put("time", ServerValue.TIMESTAMP);
                        messageTextBox.put("from",messageSenderId);

                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBox);
                        messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBox);

                        rootRef.updateChildren(messageBodyDetails, new CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null)
                                {
                                    Log.d("Chat_Log",databaseError.getMessage().toString());
                                }
                                inputMessageText.setText("");
                                loadingBar.dismiss();
                            }
                        });

                        loadingBar.dismiss();
                    }
                    else
                    {

                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void FetchMessages() {

        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messageList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage(){

        final String messageText = inputMessageText.getText().toString();
        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ChatActivity.this, "please write your message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String message_sender_ref="Messages/"+messageSenderId+"/"+messageReceiverId;
            String message_receiver_ref="Messages/"+messageReceiverId+"/"+messageSenderId;

            DatabaseReference user_message_key = rootRef.child("messages").child(messageSenderId)
                    .child(messageSenderId).push();
            String message_push_id = user_message_key.getKey();

            final Map messageTextBox = new HashMap();

            messageTextBox.put("message",messageText);
            messageTextBox.put("seen",false);
            messageTextBox.put("type","text");
            messageTextBox.put("time", ServerValue.TIMESTAMP);
            messageTextBox.put("from",messageSenderId);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBox);
            messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBox);

            rootRef.updateChildren(messageBodyDetails, new CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError !=null)
                    {
                        Log.i("Chat_Log",databaseError.getMessage().toString());
                    }
                    inputMessageText.setText("");
                }
            });

        }
    }

}
