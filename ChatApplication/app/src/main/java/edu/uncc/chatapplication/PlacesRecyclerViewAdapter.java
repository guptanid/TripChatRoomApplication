package edu.uncc.chatapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rujut on 4/27/2017.
 */

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder> {
    ArrayList<MyPlaces> m_PList = null;
    Context m_Context = null;
    private static PlacesRecyclerViewAdapter.MyClickListener myClickListener;

    @Override
    public PlacesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PlacesRecyclerViewAdapter.ViewHolder vh = null;
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_places, parent, false);
        vh = new PlacesRecyclerViewAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(PlacesRecyclerViewAdapter.ViewHolder holder, final int position) {
        MyPlaces details = m_PList.get(position);

        View view = holder.mView;
        TextView tripTitleTextText = (TextView) view.findViewById(R.id.textViewPlaceName);

        tripTitleTextText.setText(details.getName());

        view.setFocusable(true);
    }

    @Override
    public int getItemCount() {
        return m_PList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener  {

        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {

            super(v);
            mView = v;
//            Log.i("demo", "Adding Listener");
            mView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Log.d("demo", "OnClick");
            myClickListener.onItemClick(getAdapterPosition());
        }
    }


    public PlacesRecyclerViewAdapter(ArrayList<MyPlaces> list, Context context){
        m_PList = list;
        m_Context = context;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position);
    }
}
