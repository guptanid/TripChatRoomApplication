package edu.uncc.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rujut on 4/27/2017.
 */

public class RecyclerviewListAdapter extends RecyclerView.Adapter<RecyclerviewListAdapter.ViewHolder> {
    ArrayList<Trip> m_PList = null;
    Context m_Context = null;

    public RecyclerviewListAdapter(ArrayList<Trip> list, Context context) {
        m_PList = list;
        m_Context = context;
    }

    @Override
    public RecyclerviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_trip_view, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerviewListAdapter.ViewHolder holder, final int position) {
        final Trip details = m_PList.get(position);

        View view = holder.mView;
        TextView tripTitleTextText = (TextView) view.findViewById(R.id.textViewTripTitleRecycler);
        ImageView tripImage = (ImageView) view.findViewById(R.id.imageViewTripView);

        tripTitleTextText.setText(details.getTripName());
        if (details.getImageURL() != null) {
            Picasso.with(m_Context)
                    .load(details.getImageURL())
                    .resize(100, 100)
                    .centerCrop()
                    .into(tripImage);
        }
        view.setFocusable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_Context, TripDetailsActivity.class);
                intent.putExtra("Trip ID", details.getTripID());
                m_Context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_PList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }


}
