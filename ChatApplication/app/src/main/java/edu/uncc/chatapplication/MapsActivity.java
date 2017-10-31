package edu.uncc.chatapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , RoutingListener{

    private GoogleMap mMap;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    public String TRIP_ID = null;
    ArrayList<MyPlaces> myPlacesList = new ArrayList<MyPlaces>();
//    Polyline polyline;
//    PolylineOptions rectOptions = new PolylineOptions();
    LatLng start, end;
    Routing routing;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark,R.color.colorPrimary,R.color.cardview_light_background,R.color.colorAccent,R.color.primary_dark_material_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        polylines = new ArrayList<>();

        if (getIntent().getExtras() != null){
            TRIP_ID = getIntent().getExtras().getString(PlacesActivity.TRIP_ID_CODE);
            myPlacesList = (ArrayList<MyPlaces>) getIntent().getExtras().getSerializable(PlacesActivity.PLACES_LIST_CODE);

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("Places").child(TRIP_ID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                List<MyPlaces> placesList = new ArrayList<MyPlaces>();
//                for (DataSnapshot child: dataSnapshot.getChildren()){
//                    placesList.add(child.getValue(MyPlaces.class));
//                }
//                myPlacesList.clear();
//                for (int i = 0 ; i < placesList.size(); i++){
//                    myPlacesList.add(placesList.get(i));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        Log.d("Map", myPlacesList.size()+ "");
        route();

//        for (int i = 0; i < myPlacesList.size(); i++){
//            MyPlaces myPlaces = myPlacesList.get(i);
//            LatLng latLng = new LatLng(myPlaces.getLatitude(), myPlaces.getLongitude());
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(latLng));
//            rectOptions.add(latLng);
//            polyline = mMap.addPolyline(rectOptions);
//            builder.include(marker.getPosition());
//        }
//
//
//        LatLngBounds bounds = builder.build();
//        int padding = (int) (40 * 10 + 0.5f);
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        mMap.animateCamera(cu);
//        polyline = mMap.addPolyline(rectOptions);
    }

    public void route(){
//        for (int i = 0; i < myPlacesList.size(); i++){
//            MyPlaces startPlace = myPlacesList.get(i);
//            LatLng latLng = new LatLng(myPlaces.getLatitude(), myPlaces.getLongitude());
//
//        }



        MyPlaces startPlace = myPlacesList.get(0);
        start = new LatLng(startPlace.getLatitude(), startPlace.getLongitude());

        MyPlaces endPlace = myPlacesList.get(myPlacesList.size() - 1);
        end = new LatLng(endPlace.getLatitude(), endPlace.getLongitude());
        List<LatLng> waypointsList = new ArrayList<LatLng>();
        for (int i = 0; i < myPlacesList.size(); i++){
            MyPlaces myPlaces = myPlacesList.get(i);
            LatLng latLng = new LatLng(myPlaces.getLatitude(), myPlaces.getLongitude());
            waypointsList.add(latLng);
        }
        waypointsList.add(start);

//        Routing routing = new Routing.Builder()
//                .travelMode(AbstractRouting.TravelMode.TRANSIT)
//                .withListener(this)
//                .alternativeRoutes(true)
//                .waypoints(start, end)
//                .build();
        Routing.Builder builder = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(waypointsList);
        routing = builder.build();

        routing.execute();

    }


    @Override
    protected void onStop() {
        super.onStop();
//        polyline.remove();
//        rectOptions = new PolylineOptions();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {
        Log.d("Map", "OnRouting started");

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

//        mMap.moveCamera(center);


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int j = 0; j <arrayList.size(); j++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + j * 3);
            polyOptions.addAll(arrayList.get(j).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (j+1) +": distance - "+ arrayList.get(j).getDistanceValue()+": duration - "+ arrayList.get(j).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        mMap.addMarker(options);


    }

    @Override
    public void onRoutingCancelled() {
        Log.i("Map", "Routing was cancelled.");

    }

    @Override
    protected void onDestroy() {
        if (routing != null) {
            if (!routing.isCancelled()) {
                routing.cancel(true);
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
//        if (routing != null) {
//            if (!routing.isCancelled()) {
//                routing.cancel(true);
//            }
//        }
        super.onPause();
    }
    //    @Override
//    protected void onResume() {
//        super.onResume();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("Places").child(TRIP_ID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                List<MyPlaces> placesList = new ArrayList<MyPlaces>();
//                for (DataSnapshot child: dataSnapshot.getChildren()){
//                    placesList.add(child.getValue(MyPlaces.class));
//                }
//                myPlacesList.clear();
//                for (int i = 0 ; i < placesList.size(); i++){
//                    myPlacesList.add(placesList.get(i));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}


