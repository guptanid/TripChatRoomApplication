package edu.uncc.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class CreateTripActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    EditText tripNameText, imgURLText;
    ArrayList<String> memberList = new ArrayList<String>();
    String currentID;
    private FirebaseAuth mAuth;
    public static String TRIP_ID = "Trip ID";
    public static int SELECT_IMAGE = 12;
    ImageView coverImage;
    Uri selectedImage, downloadUrl;
    AutoCompleteTextView autoCompView;
    User currentUser = null;
//    ArrayList<MyPlaces> myPlaces = new ArrayList<MyPlaces>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        this.setTitle("Create New Trip");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentID = mAuth.getCurrentUser().getUid();
        memberList.add(currentID);

        tripNameText = (EditText) findViewById(R.id.editTextTripnameCreate);
        coverImage = (ImageView) findViewById(R.id.imageViewTripimgCreate);

        GooglePlacesAutocompleteAdapter placesAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.location_list);
        placesAdapter.setNotifyOnChange(true);
        autoCompView = (AutoCompleteTextView) findViewById(R.id.autocompleteTextLocationCreate);
        autoCompView.setThreshold(3);
        autoCompView.setAdapter(placesAdapter);
//        autoCompView.setAdapter(adapter);
        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();

            }
        });

        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uid = mAuth.getCurrentUser().getUid();
//                loggedInUser = dataSnapshot.child("Users").child(uid).getValue(User.class);
                currentUser = dataSnapshot.child(uid).getValue(User.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        findViewById(R.id.buttonCreateTrip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorCode = validateInputs();
                if(errorCode == null){
                    String uuid = UUID.randomUUID().toString();
                    StringBuilder builder = new StringBuilder();
                    builder.append(currentUser.getFirstName());
                    builder.append(" ");
                    builder.append(currentUser.getLastName());
                    Trip newTrip = new Trip(uuid, tripNameText.getText().toString(), autoCompView.getText().toString(), downloadUrl.toString(),currentID, builder.toString(), memberList);
                    Intent tripDetailsI = new Intent(CreateTripActivity.this, TripDetailsActivity.class);
                    tripDetailsI.putExtra(TRIP_ID, uuid);
                    startActivity(tripDetailsI);
                    mDatabase.child("Trip").child(uuid).setValue(newTrip);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.buttonUploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), SELECT_IMAGE);
            }
        });
    }

    String  validateInputs(){
        if(tripNameText.getText().toString().isEmpty() || tripNameText.getText().equals(" ")){
            return "Trip Name is empty.";
        }
        else if(autoCompView.getText().toString().isEmpty()|| autoCompView.getText().equals(" ")){
            return "Trip Location is empty.";
        }
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == SELECT_IMAGE){
            if (resultCode == RESULT_OK) {
                Log.d("demo", "Activity on result for image");
                selectedImage = data.getData();
                coverImage.setImageURI(selectedImage);
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.storage_ref_url));
                StorageReference imagesRef = storageRef.child("TripImages/" + selectedImage.getLastPathSegment());
                UploadTask uploadTask = imagesRef.putFile(selectedImage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Failure on image", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("image", downloadUrl.toString());
                    }
                });

            }
        }
    }
}
