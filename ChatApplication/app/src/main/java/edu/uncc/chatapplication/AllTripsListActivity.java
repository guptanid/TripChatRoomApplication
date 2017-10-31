package edu.uncc.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllTripsListActivity extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference dbRef;
    ArrayList<Trip> userTriplist = new ArrayList<Trip>();
    public static String TRIP_ID = "Trip ID";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView tripRecyclerView;
    ImageButton addNewTripImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trips_list);
        this.setTitle("All Trips");

        auth = FirebaseAuth.getInstance();
        tripRecyclerView = (RecyclerView) findViewById(R.id.listAllTrips) ;

        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Trip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTriplist.clear();
                String uid = auth.getCurrentUser().getUid();
                List<Trip> tripList = new ArrayList<Trip>();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    tripList.add(child.getValue(Trip.class));
                }
                Log.d("profile", "Trip list size: " + tripList.size());
                for (int i = 0; i < tripList.size(); i++){
                    Trip trip = tripList.get(i);
                    userTriplist.add(trip);
//                    if (trip.getCreatedByID().equals(uid)){
//                        userTriplist.add(trip);
//                    }
//                    else {
//                        ArrayList<String> members = trip.getMemberList();
//                        for (int j = 0; j < members.size(); j++) {
//                            if (members.get(j).equals(uid)) {
//                                userTriplist.add(trip);
//                            }
//                        }
//                    }
                }

                createRecyclerViewDisplay();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addNewTripImageButton = (ImageButton) findViewById(R.id.btnAddNewTrip);
        addNewTripImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createI = new Intent(AllTripsListActivity.this, CreateTripActivity.class);
                startActivity(createI);
                finish();
            }
        });
    }
    public void createRecyclerViewDisplay() {

        //List view style
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        tripRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AllTripsListRecyclerView(userTriplist, this.getBaseContext());
        ((AllTripsListRecyclerView) mAdapter).setOnItemClickListener(new AllTripsListRecyclerView
                .MyClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("demo", "Click: " + position);
                recyclerViewOnClick(position);
            }
        });
        tripRecyclerView.setAdapter(mAdapter);

    }

    public void recyclerViewOnClick(int position){
        Trip trip = userTriplist.get(position);
        Intent tripdetailsI = new Intent(AllTripsListActivity.this, TripDetailsActivity.class);
        tripdetailsI.putExtra(TRIP_ID, trip.getTripID());
        startActivity(tripdetailsI);
    }
}
