package edu.uncc.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class CreateTripActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    EditText tripNameText, tripLocationText, imgURLText;
    ArrayList<String> memberList = new ArrayList<String>();
    String currentID;
    private FirebaseAuth mAuth;
    public static String TRIP_ID = "Trip ID";
    public static int SELECT_IMAGE = 12;
    ImageView coverImage;
    Uri selectedImage, downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
//        currentID = mAuth.getCurrentUser().getUid();
        currentID = "1222";
        memberList.add(currentID);

        tripNameText = (EditText) findViewById(R.id.editTextTripnameCreate);
        tripLocationText = (EditText) findViewById(R.id.editTextLocationCreate);
        coverImage = (ImageView) findViewById(R.id.imageViewtripImageDetails);



        findViewById(R.id.buttonCreateTrip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorCode = validateInputs();
                if(errorCode == null){
                    String uuid = UUID.randomUUID().toString();
                    Trip newTrip = new Trip(uuid, tripNameText.getText().toString(), tripLocationText.getText().toString(), " ",currentID, memberList);
                    mDatabase.child("Trip").child(uuid).setValue(newTrip);
                    Intent tripDetailsI = new Intent(CreateTripActivity.this, TripDetailsActivity.class);
                    tripDetailsI.putExtra(TRIP_ID, uuid);
                    startActivity(tripDetailsI);
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
        else if(tripLocationText.getText().toString().isEmpty()|| tripLocationText.getText().equals(" ")){
            return "Trip Location is empty.";
        }
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == SELECT_IMAGE){
            if (resultCode == RESULT_OK) {
                Log.d("demo", "Activity on result for image");
                selectedImage = data.getData();
//                coverImage.setImageURI(selectedImage);
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://tripapplication-6cab7.appspot.com/");
                StorageReference imagesRef = storageRef.child("Images/" + selectedImage.getLastPathSegment());
                UploadTask uploadTask = imagesRef.putFile(selectedImage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });

            }
        }
    }
}
