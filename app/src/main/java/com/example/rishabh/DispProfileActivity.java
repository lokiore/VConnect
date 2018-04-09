package com.example.rishabh;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UpdateAppearance;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by lokiore on 8/4/18.
 */

public class DispProfileActivity extends AppCompatActivity {
    public static final int PICK_IMAGE_REQUEST = 1;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        ImageView imageView = findViewById(R.id.disp_profile_picture);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            TextView emailView = findViewById(R.id.profile_email);
            emailView.setText(personEmail);
            TextView nameView = findViewById(R.id.profile_name);
            nameView.setText(personGivenName);
            LinearLayout mobileView = findViewById(R.id.mobile_layout);
            mobileView.setVisibility(View.GONE);
            LinearLayout passwordView = findViewById(R.id.password_layout);
            passwordView.setVisibility(View.GONE);
            LinearLayout updateView = findViewById(R.id.update_profile);
            updateView.setVisibility(View.GONE);
        } else {
            if (mAuth != null) {
                String new_email = mAuth.getEmail();
                new_email = new_email.replaceAll("\\.", "_dot_");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mDatabase = database.getReference();
                DatabaseReference myRef = mDatabase.child("users");
                DatabaseReference mUser = myRef.child(new_email);
                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> user;
                        user = (HashMap<String, String>) dataSnapshot.getValue();
                        String personName = user.get("Name");
                        String personEmail = user.get("Email");
                        String personMobile = user.get("Mobile");
                        String personPassword = user.get("Password");
                        TextView username = findViewById(R.id.profile_name);
                        TextView email = findViewById(R.id.profile_email);
                        TextView mobile = findViewById(R.id.profile_mobile);
                        TextView password = findViewById(R.id.profile_pass);
                        username.setText(personName);
                        email.setText(personEmail);
                        mobile.setText(personMobile);
                        personPassword = personPassword.replaceAll(".", "*");
                        password.setText(personPassword);
                        LinearLayout updateView = findViewById(R.id.update_profile);
                        updateView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DispProfileActivity.this, UpdateProfileActivity.class);


                                startActivity(intent);

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
    //ImageView mImageView = findViewById(R.id.disp_profile_picture);



}
