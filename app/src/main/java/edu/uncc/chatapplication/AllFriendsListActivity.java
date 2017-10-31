package edu.uncc.chatapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AllFriendsListActivity extends MenuBaseActivity {
    RecyclerView recyclerViewAllFriends;
    LinearLayoutManager layoutManager;
    ArrayList<User> friendsList;
    ArrayList<String> friendsIdList;
    FriendsListRecyclerViewAdapter adapter;
    ImageButton btnSearchFriend;
    TextView txtSearchedPerson;
    FirebaseAuth auth;
    DatabaseReference dbRef;

    @Override
    protected void onStart() {
        super.onStart();
        friendsList = new ArrayList<User>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends_list);

        //Create Recycler View Adapter to bind the recycler view.
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        recyclerViewAllFriends = (RecyclerView) findViewById(R.id.recyclerViewAllFriends);
        recyclerViewAllFriends.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAllFriends.setLayoutManager(layoutManager);
        GetFriendsOfLoggedInUser();

        btnSearchFriend = (ImageButton) findViewById(R.id.btnSearchFriend);
        txtSearchedPerson = (TextView) findViewById(R.id.txtSearchedPerson);
        final String userId = auth.getCurrentUser().getUid();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> friendsIdList = (ArrayList<User>) dataSnapshot.child("Users").child(userId).child("FriendsList").getValue();
                ArrayList<User> allUsers = (ArrayList<User>) dataSnapshot.child("Users").getValue();
                for (User user : allUsers) {
                    if (friendsIdList != null && friendsIdList.size() > 0 && friendsIdList.contains(user.getUserId()))
                        friendsList.add(user);
                }

                adapter = new FriendsListRecyclerViewAdapter(AllFriendsListActivity.this, friendsList);
                recyclerViewAllFriends.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (User friend : friendsList) {

                }
            }
        });


    }

    private void GetFriendsOfLoggedInUser() {

    }
}
