package edu.uncc.chatapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rujut on 4/27/2017.
 */

public class AllTripsListRecyclerView extends RecyclerView.Adapter<AllTripsListRecyclerView.ViewHolder> {
    ArrayList<Trip> m_PList = null;
    Context m_Context = null;
    private static AllTripsListRecyclerView.MyClickListener myClickListener;
    FirebaseAuth auth;
    String currentUserID;
    private DatabaseReference mDatabase;

    @Override
    public AllTripsListRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AllTripsListRecyclerView.ViewHolder vh = null;
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_trips_list, parent, false);
        vh = new AllTripsListRecyclerView.ViewHolder(v);
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return vh;
    }


    @Override
    public void onBindViewHolder(AllTripsListRecyclerView.ViewHolder holder, final int position) {
        final Trip details = m_PList.get(position);
        ArrayList<String> members = details.getMemberList();

        View view = holder.mView;
        TextView tripTitleTextText = (TextView) view.findViewById(R.id.txtListTripName);
        TextView tripCreatedBy = (TextView) view.findViewById(R.id.txtListCreatedByName);
        ImageView tripImage = (ImageView) view.findViewById(R.id.imgTripListPicture);

        tripTitleTextText.setText(details.getTripName());
        tripCreatedBy.setText("Created by: " + details.getCreatedByName());

        final Button joinButton = (Button) view.findViewById(R.id.btnListJoinTrip);

        if (details.createdByID.equals(currentUserID)) {
            Log.d("trips", "Delete");
            joinButton.setVisibility(View.VISIBLE);
            joinButton.setText("Delete");
        } else if (!members.contains(currentUserID)) {
            joinButton.setVisibility(View.VISIBLE);
            joinButton.setText("Join");
        } else {
            Log.d("trips", "show none");
            joinButton.setVisibility(View.INVISIBLE);
        }


        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinButton.getText().equals("Join")) {
                    Trip trip = details;
                    ArrayList<String> memberList = trip.getMemberList();
                    memberList.add(currentUserID);
                    trip.setMemberList(memberList);
                    mDatabase.child("Trip").child(trip.getTripID()).setValue(trip);
//                    mDatabase.child("Trip").child(details.getTripID()).removeValue();

                } else if (joinButton.getText().equals("Delete")) {
                    mDatabase.child("Trip").child(details.getTripID()).removeValue();
                    mDatabase.child("Places").child(details.getTripID()).removeValue();
                    mDatabase.child("Messages").child(details.getTripID()).removeValue();
                }
            }
        });


        if (details.getImageURL() != null) {
            Picasso.with(m_Context)
                    .load(details.getImageURL())
                    .resize(100, 100)
                    .centerCrop()
                    .into(tripImage);
        }


        view.setFocusable(true);
    }

    @Override
    public int getItemCount() {
        return m_PList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {

        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {

            super(v);
            mView = v;
            Log.i("demo", "Adding Listener");
            mView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Log.d("demo", "OnClick");
            myClickListener.onItemClick(getAdapterPosition());
        }
    }


    public AllTripsListRecyclerView(ArrayList<Trip> list, Context context) {
        m_PList = list;
        m_Context = context;
    }

    public void setOnItemClickListener(AllTripsListRecyclerView.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position);
    }
}
