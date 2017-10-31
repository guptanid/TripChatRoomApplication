package edu.uncc.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlacesActivity extends AppCompatActivity {
    Button addPlacesButton, displayTripButton;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    ArrayList<MyPlaces> placesList = new ArrayList<MyPlaces>();
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView placesRecyclerView;
    public String TRIP_ID = null;
    public static String TRIP_ID_CODE = "TRIP_ID";
    public static String PLACES_LIST_CODE = "PLACES_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null){
            TRIP_ID = getIntent().getExtras().getString(TripDetailsActivity.TRIP_ID);
        }

        addPlacesButton = (Button) findViewById(R.id.buttonAddPlaces);
        addPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace();
            }
        });

        placesRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewPlaces);

        displayTripButton = (Button) findViewById(R.id.buttonDisplayTrip);
        displayTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapI = new Intent(PlacesActivity.this, MapsActivity.class);
                mapI.putExtra(TRIP_ID_CODE, TRIP_ID);
                mapI.putExtra(PLACES_LIST_CODE, placesList);
                startActivity(mapI);
            }
        });

        mDatabase.child("Places").child(TRIP_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MyPlaces> pList = new ArrayList<MyPlaces>();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    pList.add(child.getValue(MyPlaces.class));
                }
                placesList.clear();
                for (int i = 0 ; i < pList.size(); i++){
                    placesList.add(pList.get(i));
                }
                createRecyclerViewDisplay();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void findPlace() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                MyPlaces myPlaces = new MyPlaces(place.getName().toString(), place.getAddress().toString(), place.getId(), place.getLatLng().latitude, place.getLatLng().longitude);
                placesList.add(myPlaces);
                createRecyclerViewDisplay();
                String key = mDatabase.child("Places").child(TRIP_ID).push().getKey();
                myPlaces.setID(key);
                mDatabase.child("Places").child(TRIP_ID).child(key).setValue(myPlaces);
                Log.i("Place", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Place", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void createRecyclerViewDisplay() {

        //List view style
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        placesRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PlacesRecyclerViewAdapter(placesList, this.getBaseContext());
        ((PlacesRecyclerViewAdapter) mAdapter).setOnItemClickListener(new PlacesRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("demo", "Click: " + position);
                recyclerViewOnClick(position);
            }
        });
        placesRecyclerView.setAdapter(mAdapter);

    }

    public void recyclerViewOnClick(int position){
        MyPlaces place = placesList.get(position);
        Log.d("Placesactivity", "Delete " + TRIP_ID + " " + place.getID());
        mDatabase.child("Places").child(TRIP_ID).child(place.getID()).removeValue();
        mDatabase.child("Places").child(TRIP_ID).child(place.getID()).setValue(null);
    }
}
