package com.example.a3yashspc.yash;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 3yash's pc on 8/4/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;



    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_user, parent, false);


        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {



        String message_sender_id =mAuth.getCurrentUser().getUid();

        Messages messages = userMessagesList.get(position);
        String fromUserId =messages.getFrom();

        if (fromUserId.equals(message_sender_id))
        {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setGravity(Gravity.RIGHT);
        }
        else
        {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setGravity(Gravity.LEFT);

        }

        holder.messageText.setText(messages.getMessage());

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public CircleImageView userProfileImage;

        public MessageViewHolder(View view) {
            super(view);
            messageText = (TextView) view.findViewById(R.id.message_text);
            userProfileImage = (CircleImageView) view.findViewById(R.id.message_profile_image);

        }


    }
}

  /*  @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TextView messageText;
        CircleImageView userProfileImage;

        String message_sender_id =mAuth.getCurrentUser().getUid();

        Messages messages = userMessagesList.get(position);
        String fromUserId =messages.getFrom();
        if (convertView == null)
        {


            LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(parent.getContext());
            if (fromUserId.equals(message_sender_id))
            {
                view = inflater.inflate(R.layout.message_layout_of_user,null);
                messageText = (TextView)view.findViewById(R.id.message_text);
                userProfileImage = (CircleImageView)view.findViewById(R.id.message_profile_image);
                messageText.setText(userMessagesList.get(position).getBody());
            }
            else
            {
                view = inflater.inflate(R.layout.message_layout_of_user,null);
                messageText = (TextView)view.findViewById(R.id.message_text);
                messageText.setText(userMessagesList.get(position).getBody());

            }
        }
        return view;
    }


*/
