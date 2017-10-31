package edu.uncc.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by NidhiGupta on 4/21/2017.
 */

public class AllUsersListRecyclerViewAdapter extends RecyclerView.Adapter<AllUsersListRecyclerViewAdapter.ViewHolder> {
    ArrayList<User> friendsList = null;
    Context m_Context = null;
    MainActivity m_activity = null;
    User userDetails;
    FirebaseAuth mAuth;
    FirebaseStorage storage;

    public AllUsersListRecyclerViewAdapter(Context context, ArrayList<User> list) {
        friendsList = list;
        m_Context = context;
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public AllUsersListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AllUsersListRecyclerViewAdapter.ViewHolder vh = null;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_searchedusers_list, parent, false);
        vh = new AllUsersListRecyclerViewAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AllUsersListRecyclerViewAdapter.ViewHolder holder, int position) {
        final int pos = position;
        userDetails = friendsList.get(position);
        View view = holder.mView;

        TextView txtSearchedUserName = (TextView) view.findViewById(R.id.txtSearchedUserName);
        txtSearchedUserName.setText(userDetails.getFirstName() + " " + userDetails.getLastName());

        ImageView imgSearchedUserPic = (ImageView) view.findViewById(R.id.imgSearchedUserPic);
        StorageReference storageRef = storage.getReferenceFromUrl("gs://tripapplication-6cab7.appspot.com/");
        StorageReference imagesRef = storageRef.child("ProfileImages/" + userDetails.getImageUrl());
        Glide.with(m_Context)
                .using(new FirebaseImageLoader())
                .load(imagesRef)
                .into(imgSearchedUserPic);
        imgSearchedUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User friend = friendsList.get(pos);
                Intent intent = new Intent(m_Context, FriendDetailsActivity.class);
                intent.putExtra("FriendUserId", friend.getUserId());
                m_Context.startActivity(intent);
                //go to friend details page with current friend id
            }
        });

        Button btnPeopleListAddFriend = (Button) view.findViewById(R.id.btnPeopleListAddFriend);
        TextView txtSearchedAlreadyFriends = (TextView) view.findViewById(R.id.txtSearchedAlreadyFriends);
        TextView txtSearchedFriendRequestSent = (TextView) view.findViewById(R.id.txtSearchedFriendRequestSent);
        Button btnSearchedAcceptRequest = (Button) view.findViewById(R.id.btnSearchedAcceptRequest);
        btnPeopleListAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }
}
