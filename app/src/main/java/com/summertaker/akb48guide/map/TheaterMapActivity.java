package com.summertaker.akb48guide.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.summertaker.akb48guide.R;
//import com.google.maps.android.ui.IconGenerator;

import java.security.KeyStore;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TheaterMapActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap mMap;
    private HashMap mMarkersHashMap;

    private LocationManager mLocationManager;
    private static final long MIN_TIME = 5 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
    private static final float MIN_DISTANCE = 5; // Minimum distance change for update in meters, i.e. 10 meters.

    private Marker mMyLocationMarker;
    private LatLng mMyLocationLatLng;
    private int mMyLocationZoom = 15;

    private TextView mLog1;
    private TextView mLog2;
    private int mCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theater_map_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        mLog1 = (TextView) findViewById(R.id.log1);
        mLog2 = (TextView) findViewById(R.id.log2);

        try {
            initilizeMap();
            initializeUiSettings();
            initializeMapLocationSettings();
            initializeCustomControl();
            //initializeMapTraffic();
            //initializeMapType();
            //initializeMapViewSettings();
            initializeLocationManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initilizeMap() {
        //mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
        // check if map is created successfully or not
        //if (mMap == null) {
        //    Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        //}

        (findViewById(R.id.mapFragment)).getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    (findViewById(R.id.mapFragment)).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    (findViewById(R.id.mapFragment)).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                //mMap.addMarker(new MarkerOptions().position(new LatLng(10, 10)).title("Hello world"));

                addMarkerToHashMap("markerOne", new MarkerOptions().position(new LatLng(41.0102747, -73.9945297))
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapMarker("1,234", R.drawable.ic_marker_16)))); //.anchor(0.5f, 1));

                /*
                IconGenerator tc = new IconGenerator(TheaterMapActivity.this);
                tc.setContentPadding(10, 0, 10, 0);
                tc.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                tc.setTextAppearance(R.style.TextColorWhite);
                Bitmap bmp1 = tc.makeIcon("무궁화"); // pass the text you want.

                addMarkerToHashMap("markerOne", new MarkerOptions().position(new LatLng(40.7102747, -73.9945297))
                        .title("Hello world").snippet("Population: 4,137,400")
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp1)));

                tc.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                Bitmap bmp2 = tc.makeIcon("우리나라"); // pass the text you want.
                addMarkerToHashMap("markerTwo", new MarkerOptions()
                        .position(new LatLng(43.7297251, -74.0675716))
                        .title("Hello world")
                        .snippet("Population: 4,137,400")
                                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp2)));

                //marker.showInfoWindow();
                */
                zoomToMarkers();
            }
        });
    }

    public void initializeUiSettings() {
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);

        //mMap.setPadding(0, 0, 0, 300);
    }

    public void initializeMapLocationSettings() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //mMap.setMyLocationEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void initializeCustomControl() {
        Button myLocationButton = (Button) findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMyLocation();
            }
        });

        Button zoomInButton = (Button) findViewById(R.id.zoomInButton);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        Button zoomOutButton = (Button) findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    public Bitmap getBitmapMarker(String title, int resId) {
        Drawable drawable = ContextCompat.getDrawable(TheaterMapActivity.this, resId);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), conf);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setTextSize(28);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_16), 0, 0, paint);
        int xPos = (canvas.getWidth() / 2);
        int yPos = 105; //(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(title, xPos, yPos, paint);

        return bitmap;
    }

    public void addMarkerToHashMap(String id, MarkerOptions markerOptions) {
        if (mMarkersHashMap == null) {
            mMarkersHashMap = new HashMap();
        }
        Marker marker = mMap.addMarker(markerOptions);
        mMarkersHashMap.put(id, marker);
    }

    public void zoomToMarkers() {
        zoomAnimateLevelToFitMarkers(120);
    }

    public void zoomAnimateLevelToFitMarkers(int padding) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        Iterator<KeyStore.Entry> iter = mMarkersHashMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry mEntry = (Map.Entry) iter.next();
            String key = (String) mEntry.getKey();
            Marker marker = (Marker) mEntry.getValue();
            LatLng ll = marker.getPosition();
            b.include(ll);
        }
        LatLngBounds bounds = b.build();

        // Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    public void moveToMyLocation() {
        if (mMyLocationLatLng != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMyLocationLatLng, mMyLocationZoom);
            mMap.animateCamera(cameraUpdate);
        }
    }

    /*
    사용자의 위치 찾기 (1) - 간단히 현재 위치만 찾는다.
    http://stackoverflow.com/questions/23104089/googlemap-getmylocation-cannot-get-current-location
     */
    public void findMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//        mMap.animateCamera(cameraUpdate);
//        locationManager.removeUpdates(this);
    }

    /*
    사용자 위치 찾기 (2) - LocationListener를 implements 해서 현재 위치를 지속적으로 업데이트한다.
    http://stackoverflow.com/questions/13756758/how-to-directly-move-camera-to-current-location-in-google-maps-android-api-v2
     */
    public void initializeLocationManager() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLog1.setText(location.getLatitude() + ", " + location.getLongitude());
        mLog2.setText(DateFormat.getTimeInstance().format(new Date()));

        mMyLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMyLocationMarker == null) {
            mMyLocationMarker = mMap.addMarker(new MarkerOptions().position(mMyLocationLatLng));
        } else {
            mMyLocationMarker.setPosition(mMyLocationLatLng);
        }
        moveToMyLocation();

        // Stop listening to location updates, also stops providers.
        //mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
