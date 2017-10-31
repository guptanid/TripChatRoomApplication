package edu.uncc.chatapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AllUsersListActivity extends MenuBaseActivity {
    RecyclerView recyclerViewAllUsers;
    DatabaseReference dbRef;
    ArrayList<User> allUsers;
    ArrayList<User> matchedUsers;
    String searchedString = "";
    ImageButton btnSearchUser;
    EditText txtSearchUser;
    RecyclerView.LayoutManager layoutManager;
    AllUsersListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_list);
        this.setTitle(R.string.app_name);
        allUsers = new ArrayList<>();
        if (getIntent().getExtras() != null && getIntent().getExtras().get("SearchedString") != null) {
            searchedString = getIntent().getExtras().get("SearchedString").toString().toLowerCase();
        }
        recyclerViewAllUsers = (RecyclerView) findViewById(R.id.recyclerViewAllUsers);
        recyclerViewAllUsers.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAllUsers.setLayoutManager(layoutManager);
        final String loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.child("Users").getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getUserId().equals(loggedInUserId))
                        allUsers.add(user);
                }
                matchedUsers = new ArrayList<User>();
                if (searchedString.equals("")) {
                    matchedUsers = allUsers;
                } else {
                    for (User user : allUsers) {
                        String fName=user.getFirstName().toLowerCase();
                        String lName=user.getLastName().toLowerCase();
                        //String userName = user.getFirstName() + " " + user.getLastName();

                        if (fName.startsWith(searchedString)
                                || lName.startsWith(searchedString)) {
                            matchedUsers.add(user);
                        }
                    }
                }

                adapter = new AllUsersListRecyclerViewAdapter(AllUsersListActivity.this, matchedUsers);
                recyclerViewAllUsers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        txtSearchUser = (EditText) findViewById(R.id.txtSearchUser);
        txtSearchUser.setText(searchedString);
        btnSearchUser = (ImageButton) findViewById(R.id.btnSearchUser);
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedString = txtSearchUser.getText().toString().toLowerCase();
                if (allUsers != null) {
                    if (searchedString.equals("")) {
                        matchedUsers = allUsers;
                    } else {
                        matchedUsers = new ArrayList<User>();
                        for (User user : allUsers) {
                            String fName=user.getFirstName().toLowerCase();
                            String lName=user.getLastName().toLowerCase();
                            //String userName = user.getFirstName() + " " + user.getLastName();

                            if (fName.startsWith(searchedString)
                                    || lName.startsWith(searchedString)) {
                                matchedUsers.add(user);
                            }
                        }
                    }

                    adapter = new AllUsersListRecyclerViewAdapter(AllUsersListActivity.this, matchedUsers);
                    recyclerViewAllUsers.setAdapter(adapter);
                }
            }
        });
    }
}
