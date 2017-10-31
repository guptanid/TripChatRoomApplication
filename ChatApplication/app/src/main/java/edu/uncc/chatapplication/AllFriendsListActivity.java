package edu.uncc.chatapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.List;

public class AllFriendsListActivity extends MenuBaseActivity {
    RecyclerView recyclerViewAllFriends;
    LinearLayoutManager layoutManager;
    ArrayList<User> friendsList;
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
        this.setTitle("All Friends");

        //Create Recycler View Adapter to bind the recycler view.
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        recyclerViewAllFriends = (RecyclerView) findViewById(R.id.recyclerViewAllFriends);
        recyclerViewAllFriends.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAllFriends.setLayoutManager(layoutManager);
        btnSearchFriend = (ImageButton) findViewById(R.id.btnSearchFriend);
        txtSearchedPerson = (TextView) findViewById(R.id.txtSearchedPerson);
        final String uid = auth.getCurrentUser().getUid();
        dbRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsList.clear();
                List<User> usersList = new ArrayList<User>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    usersList.add(user);
                }
                for (DataSnapshot friendSnapshot : dataSnapshot.child(uid).child("FriendsList").getChildren()) {
                    String friendId = friendSnapshot.getValue(String.class);
                    for (User user : usersList) {
                        if (friendId.equals(user.getUserId()))
                            friendsList.add(user);
                    }
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
                ArrayList<User> searchedFriends = new ArrayList<User>();
                for (User friend : friendsList) {

                    String name = friend.getFirstName() + " " + friend.getLastName();
                    String searchedString = txtSearchedPerson.getText().toString();
                    if (name.toLowerCase().contains(searchedString.toLowerCase())) {
                        //filter code here
                        searchedFriends.add(friend);
                    }
                }
                adapter = new FriendsListRecyclerViewAdapter(AllFriendsListActivity.this, searchedFriends);
                recyclerViewAllFriends.setAdapter(adapter);
            }
        });
    }
}
