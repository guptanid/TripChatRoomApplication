package edu.uncc.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends MenuBaseActivity {
    User loggedInUser;
    ImageView imgMyProfilePic;
    TextView txtMyProfileName, txtMyProfileGender, lblFriendsList, lblTripsList;
    Button btnEditProfile;
    FirebaseStorage storage;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    ImageButton btnSearchUserName;
    EditText txtSearchUserName;
    ImageView addNewTripImageButton;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView tripRecyclerView, gridMyFriendsList;
    ArrayList<Trip> userTriplist = new ArrayList<Trip>();
    public static String TRIP_ID = "Trip ID";
    ValueEventListener tripVlaueEventListner;

    private RecyclerView.Adapter mAdapterFriends;
    private RecyclerView.LayoutManager mLayoutManagerFriends;
    ArrayList<User> friendsList = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        this.setTitle("Travelogged");

        tripRecyclerView = (RecyclerView) findViewById(R.id.gridMyTrips);
        tripRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tripRecyclerView.setLayoutManager(mLayoutManager);

        gridMyFriendsList = (RecyclerView) findViewById(R.id.gridMyFriendsList);
        gridMyFriendsList.setHasFixedSize(false);
        mLayoutManagerFriends = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        gridMyFriendsList.setLayoutManager(mLayoutManagerFriends);

        lblFriendsList = (TextView) findViewById(R.id.lblFriendsList);
        lblFriendsList.setTextColor(Color.BLUE);
        lblFriendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, AllFriendsListActivity.class);
                startActivity(intent);
            }
        });

        lblTripsList = (TextView) findViewById(R.id.lblMyTrips);
        lblTripsList.setTextColor(Color.BLUE);
        lblTripsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, AllTripsListActivity.class);
                startActivity(intent);
            }
        });


        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = auth.getCurrentUser().getUid();
                loggedInUser = dataSnapshot.child(uid).getValue(User.class);
                if (loggedInUser != null) {
                    imgMyProfilePic = (ImageView) findViewById(R.id.imgMyProfilePic);
                    txtMyProfileName = (TextView) findViewById(R.id.txtMyProfileName);
                    txtMyProfileGender = (TextView) findViewById(R.id.txtMyProfileGender);

                    txtMyProfileName.setText(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
                    txtMyProfileGender.setText(loggedInUser.getGender());
                    storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.storage_ref_url));

                    StorageReference imagesRef = storageRef.child("ProfileImages/" + loggedInUser.getImageUrl());
                    Glide.with(MyProfileActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(imagesRef)
                            .into(imgMyProfilePic);


                    //Set Friends List Recycler View
                    friendsList.clear();
                    List<User> usersList = new ArrayList<User>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        usersList.add(user);
                    }
                    for (DataSnapshot friendSnapshot : dataSnapshot.child(uid).child("FriendsList").getChildren()) {
                        String friendId = friendSnapshot.getValue(String.class);
                        for (User user : usersList) {
                            if (friendId.equals(user.getUserId()))
                                friendsList.add(user);
                        }
                    }
                    mAdapterFriends = new FriendsListRecyclerViewAdapter(MyProfileActivity.this, friendsList);
                    gridMyFriendsList.setAdapter(mAdapterFriends);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        tripVlaueEventListner = dbRef.child("Trip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTriplist.clear();
                String uid = auth.getCurrentUser().getUid();
                List<Trip> tripList = new ArrayList<Trip>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    tripList.add(child.getValue(Trip.class));
                }
                Log.d("profile", "Trip list size: " + tripList.size());
                for (int i = 0; i < tripList.size(); i++) {
                    Trip trip = tripList.get(i);
                    if (trip.getCreatedByID() != null && trip.getCreatedByID().equals(uid)) {
                        userTriplist.add(trip);
                    } else {
                        ArrayList<String> members = trip.getMemberList();
                        for (int j = 0; j < members.size(); j++) {
                            if (members.get(j).equals(uid)) {
                                userTriplist.add(trip);
                            }
                        }
                    }
                }
                mAdapter = new RecyclerviewListAdapter(userTriplist, MyProfileActivity.this);
                tripRecyclerView.setAdapter(mAdapter);
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
                finish();
            }
        });
        btnEditProfile=(Button) findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("LoggedInUser",loggedInUser);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //dbRef.child("Users").removeEventListener(dbRefEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // dbRef.child("Users").addListenerForSingleValueEvent(dbRefEventListener);
    }
}
