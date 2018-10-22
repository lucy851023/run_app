package andbas.ui3_0628;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;
import android.location.LocationManager;


import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    LocationRequest locationRequest;
    LocationCallback locationCallback;


    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 16;
    private static final int BOX_RANGE = 50;
    private static final int REQUEST_INTERVAL = 5000;
    private static final int REQUEST_FAST_INTERVAL = 3000;
    private static final int REQUEST_SMALLEST_DISPLACEMENT = 10;
    private static final int BOX_NUMBER = 35;
    private FloatingActionButton fab_left;
    private FloatingActionButton fab_right;

    private TextView tvBoxNum;
    private TextView tvRunningLength;
    private Location start_location;
    private Location end_location;
    private float runningLength = 0;
    private ArrayList<LatLng> runningPoints = new ArrayList<>();
    private boolean isStart = false;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Chronometer mChronometer;
    private long lastPause;
    private static final String FILE_NAME = "MyFile";

    private SoundPool soundPool;
    private int sound1;

    private ArrayList<Marker> markerList = new ArrayList<>();

    private int boxNum;

    private BitmapDescriptor box_bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        box_bitmap = BitmapDescriptorFactory.fromResource(R.drawable.box_icon);

        tvBoxNum = findViewById(R.id.tvBoxNum);
        tvRunningLength = findViewById(R.id.tvRunningLength);
        fab_left = findViewById(R.id.fab_left);
        fab_right = findViewById(R.id.fab_right);
        mChronometer = findViewById(R.id.chronometer);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        sound1 = soundPool.load(this,R.raw.audio,1);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(REQUEST_INTERVAL);
        locationRequest.setFastestInterval(REQUEST_FAST_INTERVAL);
        locationRequest.setSmallestDisplacement(REQUEST_SMALLEST_DISPLACEMENT);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {

                    end_location = location;
                    LatLng latLng_start = new LatLng(start_location.getLatitude(), start_location.getLongitude());
                    LatLng latLng_end = new LatLng(end_location.getLatitude(), end_location.getLongitude());
                    //runningPoints.add(latLng_end);
                    float result[] = new float[10];
                    Location.distanceBetween(start_location.getLatitude(), start_location.getLongitude(), end_location.getLatitude(), end_location.getLongitude(), result);

                    runningLength = runningLength + result[0];
                    Float kmLength = MtoKM(runningLength);
                    tvRunningLength.setText(kmLength + "KM");
                    mMap.addPolyline(new PolylineOptions().clickable(true).add(latLng_start, latLng_end));
                    moveCamera(latLng_end, DEFAULT_ZOOM);

                    for(int i=0; i<markerList.size() ; i++){
                        float distance[] = new float[1];
                        Location.distanceBetween(markerList.get(i).getPosition().latitude,
                                                 markerList.get(i).getPosition().longitude,
                                                 end_location.getLatitude(),
                                                 end_location.getLongitude(),
                                                 distance);
                        if(distance[0] < BOX_RANGE){
                            // box number ++
                            boxNum = boxNum + 1;
                            tvBoxNum.setText(boxNum + "個");
                            soundPool.play(sound1,1,1,0,0,1);
                            markerList.get(i).remove();
                            markerList.remove(i);
                            i = i-1;

                        }
                    }

                    start_location = end_location;
                }
            }
        };

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @NonNull
    private LatLng getPointCenter(ArrayList<LatLng> mPoints) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng ll : mPoints)
            boundsBuilder.include(ll);

        return boundsBuilder.build().getCenter();
    }

    private static void saveBitmap(Context context, Bitmap b, String picName) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }

    }

    private void setFab_left() {
        fab_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LatLng centerPoint = getPointCenter(runningPoints);
                //moveCamera(centerPoint, DEFAULT_ZOOM);

                /*GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 550, 550, true);
                        if (smallBitmap == null) {
                            Log.d(TAG, "bitmap is null");
                        } else {
                            saveBitmap(MapsActivity.this, smallBitmap, FILE_NAME);
                        }
                    }
                };
                mMap.snapshot(callback);*/

                Intent intent = new Intent(MapsActivity.this, FinishActivity.class);
                intent.putExtra("run_length", MtoKM(runningLength));
                intent.putExtra("run_time", mChronometer.getText().toString());
                intent.putExtra("box_num",boxNum);
                startActivity(intent);
                finish();


            }
        });
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            start_location = mLastKnownLocation;
                            runningPoints.add(new LatLng(start_location.getLatitude(), start_location.getLongitude()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            boxCurrentLocation(mLastKnownLocation);
                            showBox(mLastKnownLocation);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, int zoom) {
        //Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        // Prompt the user for permission.
        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        } else {
            buildLocationRequest();
            buildLocationCallback();

            fab_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isStart = !isStart;
                    if (isStart) {
                        fab_right.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause_black_48dp));

                        if (lastPause != 0) {
                            mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                        } else {
                            mChronometer.setBase(SystemClock.elapsedRealtime());
                        }

                        mChronometer.start();

                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                        , 10);
                            }

                            return;
                        }
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        Toast.makeText(MapsActivity.this,"Has Request Location Update",Toast.LENGTH_SHORT).show();

                    } else {
                        fab_right.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_arrow_black_48dp));
                        lastPause = SystemClock.elapsedRealtime();
                        mChronometer.stop();
                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);

                    }

                }
            });
            setFab_left();

        }

    }


    private float MtoKM(float f){
        DecimalFormat df = new DecimalFormat("##.00");
        Float km = f/1000;
        return Float.parseFloat(df.format(km));
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void boxCurrentLocation(Location location){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(),location.getLongitude()));
        markerOptions.icon(box_bitmap);
        Marker marker = mMap.addMarker(markerOptions);
        markerList.add(marker);

    }

    private void showBox(Location location){


        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double interval = 0.005;
        double lat_max = lat + interval;
        double lat_min = lat - interval;
        double lng_max = lng + interval;
        double lng_min = lng - interval;



        for(int i=0;i<BOX_NUMBER;i++) {
            double lat_r = ThreadLocalRandom.current().nextDouble(lat_min, lat_max);
            double lng_r = ThreadLocalRandom.current().nextDouble(lng_min, lng_max);
            //mMap.addMarker()
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(lat_r,lng_r));
            markerOptions.icon(box_bitmap);
            Marker marker = mMap.addMarker(markerOptions);
            markerList.add(marker);
            //
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

}
