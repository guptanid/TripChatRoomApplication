package edu.uncc.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.storage.images.FirebaseImageLoader;

public class MyProfileActivity extends MenuBaseActivity {
    User loggedInUser;
    ImageView imgMyProfilePic;
    TextView txtMyProfileName, txtMyProfileGender, lblFriendsList;
    FirebaseStorage storage;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    ImageButton btnSearchUserName;
    EditText txtSearchUserName;
    ImageView addNewTripImageButton;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = auth.getCurrentUser().getUid();
                loggedInUser = dataSnapshot.child("Users").child(uid).getValue(User.class);
                if (loggedInUser != null) {
                    imgMyProfilePic = (ImageView) findViewById(R.id.imgMyProfilePic);
                    txtMyProfileName = (TextView) findViewById(R.id.txtMyProfileName);
                    txtMyProfileGender = (TextView) findViewById(R.id.txtMyProfileGender);

                    txtMyProfileName.setText(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
                    txtMyProfileGender.setText(loggedInUser.getGender());
                    storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://tripapplication-6cab7.appspot.com/");

                    StorageReference imagesRef = storageRef.child("ProfileImages/" + loggedInUser.getImageUrl());
                    Glide.with(MyProfileActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(imagesRef)
                            .into(imgMyProfilePic);

                    lblFriendsList = (TextView) findViewById(R.id.lblFriendsList);
                    lblFriendsList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MyProfileActivity.this, AllFriendsListActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnSearchUserName = (ImageButton) findViewById(R.id.btnSearchUserName);
        txtSearchUserName = (EditText) findViewById(R.id.txtSearchUserName);
        btnSearchUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchedString = txtSearchUserName.getText().toString();
                Intent intent = new Intent(MyProfileActivity.this, AllUsersListActivity.class);
                intent.putExtra("SearchedString", searchedString);
                startActivity(intent);
            }
        });

        addNewTripImageButton = (ImageView) findViewById(R.id.imageViewAddTripProfile);
        addNewTripImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createI = new Intent(MyProfileActivity.this, CreateTripActivity.class);
                startActivity(createI);
            }
        });
    }
}
