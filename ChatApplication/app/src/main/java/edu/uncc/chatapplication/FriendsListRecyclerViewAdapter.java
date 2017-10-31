package edu.uncc.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by NidhiGupta on 4/20/2017.
 */

public class FriendsListRecyclerViewAdapter extends RecyclerView.Adapter<FriendsListRecyclerViewAdapter.ViewHolder> {
    ArrayList<User> friendsList = null;
    Context m_Context = null;
    MainActivity m_activity = null;
    User friendDetails;
    FirebaseAuth mAuth;
    FirebaseStorage storage;

    public FriendsListRecyclerViewAdapter(Context context, ArrayList<User> list) {
        friendsList = list;
        m_Context = context;
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public FriendsListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_friends_list, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FriendsListRecyclerViewAdapter.ViewHolder holder, int position) {
        final int pos = position;
        friendDetails = friendsList.get(position);
        View view = holder.mView;

        TextView txtFriendName = (TextView) view.findViewById(R.id.txtFriendName);
        txtFriendName.setText(friendDetails.getFirstName() + " " + friendDetails.getLastName());
        ImageView imgFriendPic = (ImageView) view.findViewById(R.id.imgFriendPic);
        StorageReference storageRef = storage.getReferenceFromUrl(m_Context.getResources().getString(R.string.storage_ref_url));
        StorageReference imagesRef = storageRef.child("ProfileImages/" + friendDetails.getImageUrl());
        Glide.with(m_Context)
                .using(new FirebaseImageLoader())
                .load(imagesRef)
                .into(imgFriendPic);
        imgFriendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsList.get(pos);
                //go to friend details page with current friend id
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendId = friendsList.get(pos).getUserId();
                Intent intent = new Intent(m_Context, FriendDetailsActivity.class);
                intent.putExtra("FriendUserId", friendId);
                m_Context.startActivity(intent);
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
