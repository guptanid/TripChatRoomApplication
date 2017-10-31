package edu.uncc.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CreateNewUserActivity extends AppCompatActivity{
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    int SELECT_IMAGE = 1234;
    String loggedInUserId = "";
    private StorageReference mStorageRef;
    Uri selectedImage;
    String imgUrl = "";
    TextView txtFirstName, txtLastName;
    RadioGroup rgGender;
    Button btnUploadProfileImage, btnRegister;
    ImageView imgUploadProfileImage;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        this.setTitle("Create New User");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        loggedInUserId = firebaseUser.getUid();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (firebaseUser != null && dataSnapshot.child("Users").child(firebaseUser.getUid()).getValue(User.class) != null) {
                    Intent intent = new Intent(CreateNewUserActivity.this, MyProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // [START_EXCLUDE]
                Toast.makeText(CreateNewUserActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
        txtFirstName = (TextView) findViewById(R.id.txtFirstName);
        txtLastName = (TextView) findViewById(R.id.txtLastName);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        btnUploadProfileImage = (Button) findViewById(R.id.btnUploadProfileImage);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        if (firebaseUser.getDisplayName() != null) {
            String[] name = firebaseUser.getDisplayName().split(" ");
            txtFirstName.setText(name[0]);
            txtLastName.setText(name[1]);
        }

        btnUploadProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), SELECT_IMAGE);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName = ((TextView) findViewById(R.id.txtFirstName)).getText().toString();
                String lName = ((TextView) findViewById(R.id.txtLastName)).getText().toString();
                User newUser = new User();
                newUser.setUserId(loggedInUserId);
                String gender = "Male";
                if (rgGender.getCheckedRadioButtonId() == R.id.rbFemale) {
                    gender = "Female";
                }
                newUser.setGender(gender);
                newUser.setEmail(firebaseUser.getEmail());
                newUser.setFirstName(fName);
                newUser.setLastName(lName);
                newUser.setImageUrl(imgUrl);
                newUser.setFriendsList(null);
                newUser.setRequestReceivedList(null);
                newUser.setRequestSentList(null);
                Map<String, Object> values = newUser.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Users/" + loggedInUserId, values);
                mDatabaseRef.updateChildren(childUpdates);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            imgUrl = selectedImage.getLastPathSegment();
            imgUploadProfileImage = (ImageView) findViewById(R.id.imgUploadProfileImage);
            imgUploadProfileImage.setImageURI(selectedImage);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.storage_ref_url));
            StorageReference imagesRef = storageRef.child("ProfileImages/" + selectedImage.getLastPathSegment());
            UploadTask uploadTask = imagesRef.putFile(selectedImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgUrl = selectedImage.getLastPathSegment();
                }
            });
        }
    }
}


