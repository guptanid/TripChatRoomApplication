package edu.uncc.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripDetailsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String currentID;
    private FirebaseAuth mAuth;
    TextView tripNametext, tripLocationText;
    Button showMembersButton, closeButton;
    String tripID;
    public static String TRIP_ID = "Trip ID";
    ImageView coverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        tripNametext = (TextView) findViewById(R.id.textViewTripNameDetails);
        tripLocationText = (TextView) findViewById(R.id.textViewLocationDetails);

        coverImage = (ImageView) findViewById(R.id.imageViewtripImageDetails);

        showMembersButton = (Button) findViewById(R.id.buttonShowMembers);
        showMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Show members in this trip..", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton = (Button) findViewById(R.id.buttonCloseDetails);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().getExtras() != null){
            tripID = (String) getIntent().getExtras().getString(CreateTripActivity.TRIP_ID);
            mDatabase.child("Trip").child(tripID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Trip trip =  dataSnapshot.getValue(Trip.class);
                    tripNametext.setText(trip.getTripName());
                    tripLocationText.setText(trip.getTripLocation());
//                    coverImage.setImageURI(Uri.parse(trip.getImageURL()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("demo", "Chat button clicked. 1");
        switch (item.getItemId()){
            case R.id.action_chat:
                Log.d("demo", "Chat button clicked. 2");
                Intent chatroomI = new Intent(TripDetailsActivity.this, ChatroomActivity.class);
                chatroomI.putExtra(TRIP_ID, tripID);
                startActivity(chatroomI);
                finish();
                Log.d("demo", "Chat button clicked. 3");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
