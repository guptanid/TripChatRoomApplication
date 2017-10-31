package edu.uncc.chatapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatroomActivity extends AppCompatActivity {

    ImageView sendMessageButton, addPhotosbutton;
    private DatabaseReference mDatabase;
    String currentID, imageURL, tripID;
    private FirebaseAuth mAuth;
    EditText messageText;
    public static int REQUEST_IMAGE = 11;
    public static int RESULT_OK = 1;
    ScrollView scrollViewMessages;
    LinearLayout scrollLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

//        currentID = mAuth.getCurrentUser().getUid();
        currentID = "1222";

        messageText = (EditText) findViewById(R.id.editTextMessageInput);
        scrollViewMessages = (ScrollView) findViewById(R.id.scrollViewMessages);
        scrollLinearLayout = (LinearLayout) findViewById(R.id.scrollViewLinearLayout);

        if (getIntent().getExtras() != null){
            tripID = getIntent().getExtras().getString(CreateTripActivity.TRIP_ID);
        }

        sendMessageButton = (ImageView) findViewById(R.id.imageViewSendButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
//                String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String uuid = UUID.randomUUID().toString();
                ChatMessage newMessage = new ChatMessage(messageText.getText().toString(), imageURL, currentID, null,uuid);
                mDatabase.child("Messages").child(tripID).push().setValue(newMessage);
                messageText.setText("");
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = (View) inflater.inflate(R.layout.sent_message, null);
                TextView userName = (TextView) view.findViewById(R.id.sentUser);
                userName.setText(mAuth.getCurrentUser().getDisplayName());
                TextView textMessage = (TextView) view.findViewById(R.id.textViewSent);
                textMessage.setText(newMessage.getMessageText() + "\n" + newMessage.getMessageTime());
                scrollLinearLayout.addView(view);
            }
        });

        addPhotosbutton = (ImageView) findViewById(R.id.imageViewAddPhotos);
        addPhotosbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), REQUEST_IMAGE);
            }
        });

        mDatabase.child("Messages").child(tripID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                if (message != null) {
                    if (message.getMessageUser() == currentID) {
///////////////////////////sent messages////////////////////////
                        if (message.getImageURL() == null) {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            View view = (View) inflater.inflate(R.layout.sent_message, null);
                            TextView userName = (TextView) view.findViewById(R.id.sentUser);
                            userName.setText(mAuth.getCurrentUser().getDisplayName());
                            TextView textMessage = (TextView) view.findViewById(R.id.textViewSent);
                            textMessage.setText(message.getMessageText() + "\n" + message.getMessageTime());
                            scrollLinearLayout.addView(view);
                        } else {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            View view = (View) inflater.inflate(R.layout.sent_image, null);
                            TextView userName = (TextView) view.findViewById(R.id.sentUser);
                            userName.setText(mAuth.getCurrentUser().getDisplayName());
                            ImageView img = (ImageView) findViewById(R.id.sentImage);
                            Uri uri = Uri.parse(message.getImageURL());
                            img.setImageURI(uri);
                            scrollLinearLayout.addView(view);
                        }
                    } else {

                        if (message.getImageURL() == null) {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            View view = (View) inflater.inflate(R.layout.received_image, null);
                            TextView userName = (TextView) view.findViewById(R.id.receivedUser);
                            userName.setText("");
                            TextView textMessage = (TextView) view.findViewById(R.id.textViewReceived);
                            textMessage.setText(message.getMessageText() + "\n" + message.getMessageTime());
                            scrollLinearLayout.addView(view);
                        } else {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            View view = (View) inflater.inflate(R.layout.received_message, null);
                            TextView userName = (TextView) view.findViewById(R.id.receivedUser);
                            userName.setText("");
                            ImageView img = (ImageView) findViewById(R.id.receivedImage);
                            Uri uri = Uri.parse(message.getImageURL());
                            img.setImageURI(uri);
                            scrollLinearLayout.addView(view);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE){
            if (resultCode == RESULT_OK){
                if (data != null){
                    final Uri uri = data.getData();
                    imageURL = uri.toString();
                    String key = mDatabase.child("Messages").child(tripID).push().getKey();
//                    String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    ChatMessage message = new ChatMessage(null, currentID, imageURL, null, key);
                    Map<String, Object> postValues = message.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/Messages/"+ message.getMessageID(), postValues);
                    mDatabase.updateChildren(childUpdates);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://tripapplication-6cab7.appspot.com/");

                    StorageReference imagesRef=storageRef.child("Images/"+uri.getLastPathSegment());
                    UploadTask uploadTask= imagesRef.putFile(uri);

                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


                    View view = (View) inflater.inflate(R.layout.sent_image, null);
                    TextView userName = (TextView) view.findViewById(R.id.sentUser);
                    userName.setText(mAuth.getCurrentUser().getDisplayName());
                    ImageView img = (ImageView) findViewById(R.id.sentImage);

                    img.setImageURI(uri);
                    scrollLinearLayout.addView(view);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
                }
            }
        }
    }


}
