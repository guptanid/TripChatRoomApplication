package edu.uncc.chatapplication;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendDetailsActivity extends AppCompatActivity {

    ImageView imgFriendDetail;
    TextView txtFriendDetailsName, txtGenderFriendDetail;
    Button btnAddFriend;
    TextView lblFriendsSince;
    DatabaseReference dbRef;
    String userId = "";
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);
        imgFriendDetail=(ImageView) findViewById(R.id.imgFriendDetail);
        btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
        txtFriendDetailsName = (TextView) findViewById(R.id.txtFriendDetailsName);
        txtGenderFriendDetail = (TextView) findViewById(R.id.txtGenderFriendDetail);
        lblFriendsSince = (TextView) findViewById(R.id.lblFriendsSince);
        lblFriendsSince.setVisibility(View.GONE);

        storage = FirebaseStorage.getInstance();
        storageRef= storage.getReferenceFromUrl("gs://tripapplication-6cab7.appspot.com/");


        if (getIntent().getExtras() != null && getIntent().getExtras().get("FriendUserId") != null) {
            userId = getIntent().getExtras().get("FriendUserId").toString();
            dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.child("Users").child(userId).getValue(User.class);
                    txtFriendDetailsName.setText(user.getFirstName() + " " + user.getLastName());
                    txtGenderFriendDetail.setText(user.getGender());
                    StorageReference imagesRef = storageRef.child("ProfileImages/" + user.getImageUrl());
                    Glide.with(FriendDetailsActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(imagesRef)
                            .into(imgFriendDetail);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ArrayList<String> addFriends = new ArrayList<String>();
                    addFriends.add(userId);
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/Users/" + loggedInUserId + "/RequestSentList/", addFriends);
                    dbRef.updateChildren(childUpdates);
                }
            });
        }

    }
}
