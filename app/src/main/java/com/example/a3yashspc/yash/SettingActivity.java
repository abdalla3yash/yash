package com.example.a3yashspc.yash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView settingDisplayProfileImage;
    private TextView settingName , settingStatus;
    private Button settingChangeProfileImage , settingChangeStatus;
    private DatabaseReference getUserDataReferance;
    private FirebaseAuth mAuth;

    private final static int Gallery_pick = 1 ;
    private StorageReference storeProfileImageStoreRef ,thumbImageRef;
    Bitmap thumb_bitmap = null;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initiviews();

        mToolbar = (Toolbar)findViewById(R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        getUserDataReferance.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("user_name").getValue().toString();
                    String status = dataSnapshot.child("user_status").getValue().toString();
                    final String image = dataSnapshot.child("user_image").getValue().toString();
                    String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                    settingName.setText(name);
                    settingStatus.setText(status);

                    if (!image.equals("default_profile"))
                    {

                        Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.ic_profile_image).into(settingDisplayProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(SettingActivity.this).load(image).placeholder(R.drawable.ic_profile_image).into(settingDisplayProfileImage);

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        settingChangeProfileImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i , Gallery_pick);
            }
            });

        settingChangeStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String old_status = settingStatus.getText().toString();

                Intent i = new Intent(SettingActivity.this,StatusActivity.class);
                i.putExtra("user_status", old_status);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data!= null)
        {
            Uri ImagrUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1 )
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Updating your awesome picture");
                loadingBar.setMessage("انت قمر");
                loadingBar.show();

                Uri resultUri = result.getUri();


                File thumb_filePathUri = new File(resultUri.getPath());


                String user_id = mAuth.getCurrentUser().getUid();

            try
            {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);
            } catch (IOException e)
            {
                    e.printStackTrace();
            }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(CompressFormat.JPEG , 50 ,byteArrayOutputStream);

                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();



                StorageReference filePath = storeProfileImageStoreRef.child(user_id +".jpg");

                final StorageReference thumb_filePath = thumbImageRef.child(user_id +".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(SettingActivity.this, "saving your avatar", Toast.LENGTH_SHORT).show();


                        final String downloadUrl = task.getResult().getDownloadUrl().toString();

                        UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);


                        uploadTask.addOnCompleteListener(new OnCompleteListener<TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<TaskSnapshot> thumb_task) {
                           String thumb_downloadUri = thumb_task.getResult().getDownloadUrl().toString();

                           if (task.isSuccessful())
                           {
                               Map update_user_data = new HashMap();
                               update_user_data.put("user_image",downloadUrl);
                               update_user_data.put("user_thumb_image",thumb_downloadUri);


                               getUserDataReferance.updateChildren(update_user_data)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           Toast.makeText(SettingActivity.this, "Avatar upload successfully...", Toast.LENGTH_SHORT).show();
                                           loadingBar.dismiss();
                                       }
                                   });
                               }
                           }
                        });
                    }
                    else
                    {
                        Toast.makeText(SettingActivity.this, "ERROR, while uploading your avatar", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    }
                });
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    private void initiviews() {

    mAuth = FirebaseAuth.getInstance();
    String online_user_id = mAuth.getCurrentUser().getUid();
    getUserDataReferance = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
    getUserDataReferance.keepSynced(true);
    storeProfileImageStoreRef = FirebaseStorage.getInstance().getReference().child("profile_Images");
    thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");
    settingChangeProfileImage = (Button)findViewById(R.id.setting_change_profile_image_btn);
    settingDisplayProfileImage =(CircleImageView)findViewById(R.id.setting_profile_pic);
    settingName = (TextView)findViewById(R.id.setting_user_name);
    settingStatus = (TextView)findViewById(R.id.setting_user_status);
    settingChangeStatus = (Button)findViewById(R.id.setting_change_status_btn);
    loadingBar = new ProgressDialog(this);

    }


}
