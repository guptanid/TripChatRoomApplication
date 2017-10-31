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

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatroomActivity extends AppCompatActivity {

    ImageView sendMessageButton, addPhotosbutton;
    private DatabaseReference mDatabase;
    String currentID, imageURL, tripID;
    private FirebaseAuth mAuth;
    EditText messageText;
    public static int REQUEST_IMAGE = 11;
    LinearLayout scrollLinearLayout;
    final ArrayList<String> deletedMsgIds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        this.setTitle("ChatRoom");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentID = mAuth.getCurrentUser().getUid();

        messageText = (EditText) findViewById(R.id.editTextMessageInput);
        //scrollViewMessages = (ScrollView) findViewById(R.id.scrollViewMessages);
        scrollLinearLayout = (LinearLayout) findViewById(R.id.scrollViewLinearLayout);

        if (getIntent().getExtras() != null) {
            tripID = getIntent().getExtras().getString(CreateTripActivity.TRIP_ID);
        }

        sendMessageButton = (ImageView) findViewById(R.id.imageViewSendButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String key = mDatabase.child("Messages").child(tripID).push().getKey();
                ChatMessage newMessage = new ChatMessage(messageText.getText().toString(), null, currentID, format, key);
                //mDatabase.child("Messages").child(tripID).child(key).setValue(newMessage);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Messages/" + tripID + "/" + key + "/", newMessage);
                mDatabase.updateChildren(childUpdates);

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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), REQUEST_IMAGE);
            }
        });

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.child("Messages").child(tripID);
                List<ChatMessage> messageList = new ArrayList<ChatMessage>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    messageList.add(child.getValue(ChatMessage.class));
                }

                DataSnapshot deletedMsgs = dataSnapshot.child("Users").child(currentID).child("DeletedMessages");
                if (deletedMsgs != null && deletedMsgs.getChildren() != null) {
                    for (DataSnapshot deletedMsgSnapshot : deletedMsgs.getChildren()) {
                        deletedMsgIds.add(deletedMsgSnapshot.getValue(String.class));
                    }
                }
                scrollLinearLayout.removeAllViews();
                Collections.sort(messageList, new Comparator<ChatMessage>() {
                    public int compare(ChatMessage o1, ChatMessage o2) {
                        return o1.getMessageTime().compareTo(o2.getMessageTime());
                    }
                });
                for (int i = 0; i < messageList.size(); i++) {
                    final ChatMessage message = messageList.get(i);
                    int count = messageList.size();
                    if (message != null) {
                        TextView userName;
                        TextView textMessage;
                        ImageView imgMessage;
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        User msgUser = dataSnapshot.child("Users").child(message.getMessageUser()).getValue(User.class);
                        String msgUserName = msgUser.getFirstName() + " " + msgUser.getLastName();
                        if (deletedMsgIds.size() == 0 || !deletedMsgIds.contains(message.getMessageID())) {
                            if (message.getMessageUser().equals(currentID)) {
                                if (message.getImageURL() == null) {
                                    final View view = (View) inflater.inflate(R.layout.sent_message, null);
                                    userName = (TextView) view.findViewById(R.id.sentUser);
                                    userName.setText(msgUserName);
                                    textMessage = (TextView) view.findViewById(R.id.textViewSent);
                                    textMessage.setText(message.getMessageText() + "\n");
                                    scrollLinearLayout.addView(view);
                                    view.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            scrollLinearLayout.removeView(v);
                                            DeleteMsgFromDatabase(message);
                                            return true;
                                        }
                                    });
                                } else {
                                    final View view = (View) inflater.inflate(R.layout.sent_image, null);
                                    userName = (TextView) view.findViewById(R.id.sentUser);
                                    userName.setText(msgUserName);
                                    imgMessage = (ImageView) view.findViewById(R.id.sentImage);
                                    Picasso.with(getApplicationContext())
                                            .load(message.getImageURL())
                                            .resize(120, 120)
                                            .centerCrop()
                                            .into(imgMessage);
                                    scrollLinearLayout.addView(view);
                                    view.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            scrollLinearLayout.removeView(v);
                                            DeleteMsgFromDatabase(message);
                                            return true;
                                        }
                                    });
                                }


                            } else {
                                if (message.getImageURL() == null) {
                                    final View view = (View) inflater.inflate(R.layout.received_message, null);
                                    userName = (TextView) view.findViewById(R.id.receivedUser);
                                    userName.setText(msgUserName);
                                    textMessage = (TextView) view.findViewById(R.id.textViewReceived);
                                    textMessage.setText(message.getMessageText() + "\n");
                                    scrollLinearLayout.addView(view);
                                    view.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            scrollLinearLayout.removeView(v);
                                            DeleteMsgFromDatabase(message);
                                            return true;
                                        }
                                    });
                                } else {
                                    final View view = (View) inflater.inflate(R.layout.received_image, null);
                                    userName = (TextView) view.findViewById(R.id.receivedUser);
                                    userName.setText(msgUserName);
                                    imgMessage = (ImageView) view.findViewById(R.id.receivedImage);
                                    Picasso.with(getApplicationContext())
                                            .load(message.getImageURL())
                                            .resize(120, 120)
                                            .centerCrop()
                                            .into(imgMessage);
                                    scrollLinearLayout.addView(view);
                                    view.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            scrollLinearLayout.removeView(v);
                                            DeleteMsgFromDatabase(message);
                                            return true;
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void DeleteMsgFromDatabase(ChatMessage message) {
        ChatMessage msg = message;
        String deletedMsgId = msg.getMessageID();
        deletedMsgIds.add(deletedMsgId);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + currentID + "/DeletedMessages/", deletedMsgIds);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Chatroom", "On activity result");
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Log.d("Chatroom", "Not null");
                    final Uri selectedImage = data.getData();
                    imageURL = selectedImage.toString();

                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.storage_ref_url));
                    StorageReference imagesRef = storageRef.child("MessageImages/" + selectedImage.getLastPathSegment());
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
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("image", downloadUrl.toString());
                            String key = mDatabase.child("Messages").child(tripID).push().getKey();
                            String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                            imageURL = downloadUrl.toString();
                            ChatMessage message = new ChatMessage(null, imageURL, currentID, format, key);
                            //mDatabase.child("Messages").child(tripID).child(key).setValue(message);

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/Messages/" + tripID + "/" + key + "/", message);
                            mDatabase.updateChildren(childUpdates);

                        }
                    });
                }
            }
        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE){
//            if (resultCode == RESULT_OK){
//                if (data != null){
//                    final Uri selectedImage = data.getData();
//                    imageURL = selectedImage.toString();
//
//                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//
//
//                    View view = (View) inflater.inflate(R.layout.sent_image, null);
//                    TextView userName = (TextView) view.findViewById(R.id.sentUser);
//                    userName.setText(mAuth.getCurrentUser().getDisplayName());
//                    ImageView img = (ImageView) findViewById(R.id.sentImage);
//
//
//
////                    img.setImageURI(uri);
////                    Picasso.with(getApplicationContext())
////                            .load(imageURL.toString())
////                            .resize(80, 80)
////                            .centerCrop()
////                            .into(img);
//
//
//
////                    coverImage.setImageURI(selectedImage);
//                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.storage_ref_url));
//                    StorageReference imagesRef = storageRef.child("MessageImages/" + selectedImage.getLastPathSegment());
//
//                    Glide.with(ChatroomActivity.this)
//                            .using(new FirebaseImageLoader())
//                            .load(imagesRef)
//                            .into(img);
//                    scrollLinearLayout.addView(view);
//
//
//
//                    UploadTask uploadTask = imagesRef.putFile(selectedImage);
//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            Toast.makeText(getApplicationContext(), "Failure on image", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @SuppressWarnings("VisibleForTests")
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                            Log.d("image", downloadUrl.toString());
//                            String key = mDatabase.child("Messages").child(tripID).push().getKey();
//                            ChatMessage message = new ChatMessage(null, currentID, imageURL, null, key);
//                            mDatabase.child("Messages").child(tripID).child(key).setValue(message);
//                            imageURL = downloadUrl.toString();
//                        }
//                    });
//
//
//                }
//            }
//        }
//    }


}
