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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    User loggedInUser;
    TextView txtUpdateFirstName, txtUpdateLastName;
    RadioGroup rgUpdateGender;
    ImageView imgUpdateProfileImage;
    Button btnUpdateProfileImage, btnUpdateProfile;
    int SELECT_IMAGE = 1234;
    String imgUrl = "";
    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("LoggedInUser") != null) {
            loggedInUser = (User) getIntent().getExtras().get("LoggedInUser");
            txtUpdateFirstName = (TextView) findViewById(R.id.txtUpdateFirstName);
            txtUpdateLastName = (TextView) findViewById(R.id.txtUpdateLastName);
            txtUpdateFirstName.setText(loggedInUser.getFirstName());
            txtUpdateLastName.setText(loggedInUser.getLastName());
            rgUpdateGender = (RadioGroup) findViewById(R.id.rgUpdateGender);
            if (loggedInUser.getGender() == "Male") {
                rgUpdateGender.check(R.id.rbUpdateMale);
            } else {
                rgUpdateGender.check(R.id.rbUpdateFemale);
            }
            imgUpdateProfileImage = (ImageView) findViewById(R.id.imgUpdateProfileImage);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.storage_ref_url));
            StorageReference imagesRef = storageRef.child("ProfileImages/" + loggedInUser.getImageUrl());
            Glide.with(EditProfileActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(imagesRef)
                    .into(imgUpdateProfileImage);

            btnUpdateProfileImage = (Button) findViewById(R.id.btnUpdateProfileImage);
            btnUpdateProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Choose Picture"), SELECT_IMAGE);
                }
            });
            btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
            btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loggedInUser.setFirstName(txtUpdateFirstName.getText().toString());
                    loggedInUser.setLastName(txtUpdateLastName.getText().toString());

//                    String fName = ((TextView) findViewById(R.id.txtFirstName)).getText().toString();
//                    String lName = ((TextView) findViewById(R.id.txtLastName)).getText().toString();
//                    User newUser = new User();
//                    newUser.setUserId(loggedInUserId);
//                    String gender = "Male";
//                    if (rgGender.getCheckedRadioButtonId() == R.id.rbFemale) {
//                        gender = "Female";
//                    }
//                    newUser.setGender(gender);
//                    newUser.setEmail(firebaseUser.getEmail());
//                    newUser.setFirstName(fName);
//                    newUser.setLastName(lName);
//                    newUser.setImageUrl(imgUrl);
//                    newUser.setFriendsList(null);
//                    newUser.setRequestReceivedList(null);
//                    newUser.setRequestSentList(null);
//                    Map<String, Object> values = newUser.toMap();
//                    Map<String, Object> childUpdates = new HashMap<>();
//                    childUpdates.put("/Users/" + loggedInUserId, values);
//                    mDatabaseRef.updateChildren(childUpdates);

                    Intent intent=new Intent(EditProfileActivity.this,MyProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
           selectedImage = data.getData();
            imgUrl = selectedImage.getLastPathSegment();
            imgUpdateProfileImage.setImageURI(selectedImage);
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
