package edu.uncc.chatapplication;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendDetailsActivity extends MenuBaseActivity {
    String friendUserId = "", loggedInUserId = "";

    ImageView imgFriendDetail;
    TextView txtFriendDetailsName, txtGenderFriendDetail, lblTripsCreated;
    Button btnAddFriend;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerViewFriendAllTrips;

    ArrayList<Trip> userTriplist = new ArrayList<Trip>();
    public static String TRIP_ID = "Trip ID";

    ValueEventListener tripValueEventListner;
    DatabaseReference dbRef;
    FirebaseStorage storage;
    StorageReference storageRef;
    //0 - Add Friend
    //1 - Accept/Deny Request
    //2 - Request Sent Already
    //3 - Already friends
    int setCase = 0;

    ArrayList<String> requestReceivedList = new ArrayList<>();
    ArrayList<String> requestSentList = new ArrayList<>();
    ArrayList<String> existingFriendsList = new ArrayList<>();

    ArrayList<String> userRequestReceivedList = new ArrayList<>();
    ArrayList<String> userRequestSentList = new ArrayList<>();
    ArrayList<String> userExistingFriendsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);
        this.setTitle("Friends Detail");
        loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        imgFriendDetail = (ImageView) findViewById(R.id.imgFriendDetail);
        btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
        txtFriendDetailsName = (TextView) findViewById(R.id.txtFriendDetailsName);
        txtGenderFriendDetail = (TextView) findViewById(R.id.txtGenderFriendDetail);

        lblTripsCreated = (TextView) findViewById(R.id.lblTripsCreated);
        lblTripsCreated.setVisibility(View.GONE);

        recyclerViewFriendAllTrips = (RecyclerView) findViewById(R.id.recyclerViewFriendAllTrips);
        recyclerViewFriendAllTrips.setHasFixedSize(false);
        mLayoutManager = new GridLayoutManager(this,3);
        recyclerViewFriendAllTrips.setLayoutManager(mLayoutManager);
        recyclerViewFriendAllTrips.setVisibility(View.GONE);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(getResources().getString(R.string.storage_ref_url));

        if (getIntent().getExtras() != null && getIntent().getExtras().get("FriendUserId") != null) {
            friendUserId = getIntent().getExtras().get("FriendUserId").toString();
            dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.child(friendUserId).getValue(User.class);
                    txtFriendDetailsName.setText(user.getFirstName() + " " + user.getLastName());
                    txtGenderFriendDetail.setText(user.getGender());
                    StorageReference imagesRef = storageRef.child("ProfileImages/" + user.getImageUrl());
                    Glide.with(FriendDetailsActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(imagesRef)
                            .into(imgFriendDetail);

                    if (dataSnapshot.child(loggedInUserId).child("RequestReceivedList").getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.child(loggedInUserId).child("RequestReceivedList").getChildren()) {
                            String requestUserId = snapshot.getValue(String.class);
                            requestReceivedList.add(requestUserId);
                        }
                    }

                    if (dataSnapshot.child(loggedInUserId).child("RequestSentList").getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.child(loggedInUserId).child("RequestSentList").getChildren()) {
                            String sentUserId = snapshot.getValue(String.class);
                            requestSentList.add(sentUserId);
                        }
                    }
                    if (dataSnapshot.child(loggedInUserId).child("FriendsList").getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.child(loggedInUserId).child("FriendsList").getChildren()) {
                            String friendId = snapshot.getValue(String.class);
                            existingFriendsList.add(friendId);
                        }
                    }
                    if (dataSnapshot.child(friendUserId).child("RequestReceivedList").getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.child(friendUserId).child("RequestReceivedList").getChildren()) {
                            String requestUserId = snapshot.getValue(String.class);
                            userRequestReceivedList.add(requestUserId);
                        }
                    }
                    if (dataSnapshot.child(friendUserId).child("RequestSentList").getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.child(friendUserId).child("RequestSentList").getChildren()) {
                            String sentUserId = snapshot.getValue(String.class);
                            userRequestSentList.add(sentUserId);
                        }
                    }
                    if (dataSnapshot.child(friendUserId).child("FriendsList").getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.child(friendUserId).child("FriendsList").getChildren()) {
                            String friendId = snapshot.getValue(String.class);
                            userExistingFriendsList.add(friendId);
                        }
                    }

                    if (requestReceivedList.contains(friendUserId)) {
                        //show accept request button
                        btnAddFriend.setText("Accept Request");
                        btnAddFriend.setEnabled(true);
                        lblTripsCreated.setVisibility(View.GONE);
                        recyclerViewFriendAllTrips.setVisibility(View.GONE);
                        setCase = 1;
                    } else if (requestSentList.contains(friendUserId)) {
                        //show disabled request sent button
                        btnAddFriend.setText("Request Sent");
                        btnAddFriend.setEnabled(false);
                        lblTripsCreated.setVisibility(View.GONE);
                        recyclerViewFriendAllTrips.setVisibility(View.GONE);
                        setCase = 2;
                    } else if (existingFriendsList.contains(friendUserId)) {
                        //show disabled friends button
                        btnAddFriend.setText("Friends");
                        btnAddFriend.setEnabled(false);
                        lblTripsCreated.setVisibility(View.VISIBLE);
                        recyclerViewFriendAllTrips.setVisibility(View.VISIBLE);
                        ShowTripsLayout();
                        setCase = 3;
                    } else {
                        btnAddFriend.setText("Add Friend");
                        btnAddFriend.setEnabled(true);
                        lblTripsCreated.setVisibility(View.GONE);
                        recyclerViewFriendAllTrips.setVisibility(View.GONE);
                        setCase = 0;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (setCase) {
                    case 0:
                        Log.d("demo", "inside switch");
                        AddFriend();
                        break;
                    case 1:
                        AcceptRequest();
                        break;
                    case 2:
                        RequestAlreadySent();
                        break;
                    case 3:
                        ShowAlreadyFriends();
                        break;
                }
                Log.d("demo", "click end");
            }


        });

    }


    private void RequestAlreadySent() {
        btnAddFriend.setText("Request Sent");
        btnAddFriend.setEnabled(false);
    }

    private void ShowAlreadyFriends() {
        btnAddFriend.setText("Friends");
        btnAddFriend.setEnabled(false);
        //Show trips layout if already friends
        ShowTripsLayout();
    }

    private void AddFriend() {
        Log.d("demo", "ADD friend");
        requestSentList.add(friendUserId);
        Map<String, Object> childUpdates1 = new HashMap<>();
        childUpdates1.put("/Users/" + loggedInUserId + "/RequestSentList/", requestSentList);
        dbRef.updateChildren(childUpdates1);

        userRequestReceivedList.add(loggedInUserId);
        Map<String, Object> childUpdates2 = new HashMap<>();
        childUpdates2.put("/Users/" + friendUserId + "/RequestReceivedList/", userRequestReceivedList);
        dbRef.updateChildren(childUpdates2);

        btnAddFriend.setText("Request Sent");
        btnAddFriend.setEnabled(false);
        lblTripsCreated.setVisibility(View.GONE);
        recyclerViewFriendAllTrips.setVisibility(View.GONE);
        Log.d("demo", "after add");
    }

    private void AcceptRequest() {

        existingFriendsList.add(friendUserId);
        Map<String, Object> childUpdates3 = new HashMap<>();
        childUpdates3.put("/Users/" + loggedInUserId + "/FriendsList/", existingFriendsList);
        dbRef.updateChildren(childUpdates3);

        userExistingFriendsList.add(loggedInUserId);
        Map<String, Object> childUpdates4 = new HashMap<>();
        childUpdates4.put("/Users/" + friendUserId + "/FriendsList/", userExistingFriendsList);
        dbRef.updateChildren(childUpdates4);

        requestReceivedList.remove(friendUserId);
        Map<String, Object> childUpdates1 = new HashMap<>();
        childUpdates1.put("/Users/" + loggedInUserId + "/RequestReceivedList/", requestReceivedList);
        dbRef.updateChildren(childUpdates1);

        userRequestSentList.remove(loggedInUserId);
        Map<String, Object> childUpdates2 = new HashMap<>();
        childUpdates2.put("/Users/" + friendUserId + "/RequestSentList/", userRequestSentList);
        dbRef.updateChildren(childUpdates2);

        btnAddFriend.setText("Friends");
        btnAddFriend.setEnabled(false);

        ShowTripsLayout();
    }

    private void ShowTripsLayout() {
        lblTripsCreated.setVisibility(View.VISIBLE);
        recyclerViewFriendAllTrips.setVisibility(View.VISIBLE);

        tripValueEventListner = dbRef.child("Trip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTriplist.clear();
                List<Trip> tripList = new ArrayList<Trip>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    tripList.add(child.getValue(Trip.class));
                }
                for (int i = 0; i < tripList.size(); i++) {
                    Trip trip = tripList.get(i);
                    if (trip.getCreatedByID() != null && trip.getCreatedByID().equals(friendUserId)) {
                        userTriplist.add(trip);
                    } else {
                        ArrayList<String> members = trip.getMemberList();
                        for (int j = 0; j < members.size(); j++) {
                            if (members.get(j).equals(friendUserId)) {
                                userTriplist.add(trip);
                            }
                        }
                    }
                }
                mAdapter = new RecyclerviewListAdapter(userTriplist, FriendDetailsActivity.this);
                recyclerViewFriendAllTrips.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("demo", "on Stop");
    }
}
