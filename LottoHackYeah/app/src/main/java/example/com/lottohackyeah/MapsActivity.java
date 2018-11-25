package example.com.lottohackyeah;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int LOCATION_PERMISSION_CONSTANT = 1;

    private int amountOfTokens = 0;

    private boolean freeFlag = false;

    private Location currentLocation = null;

    private int clickCount = 2;

    private ArrayList<GeolocationRadius> areaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Model model = new Model();
        //ArrayList<GeolocationRadius> listFromServer = model.get();
        // initialize list of areas
        areaList = new ArrayList<>();
        // mock locations
        areaList.add(new GeolocationRadius(52.2856859, 20.99620413, 200)); // outside
        areaList.add(new GeolocationRadius(52.2997859, 20.98100413, 200)); // outside
        areaList.add(new GeolocationRadius(52.2957859, 20.98940413, 200)); // outside
        areaList.add(new GeolocationRadius(52.2908859, 20.99160413, 200)); // outside
        areaList.add(new GeolocationRadius(52.292945,21.007259, 200)); // inside
        // real locations
        //areaList.addAll(listFromServer);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        configureFabs();

        updateTokenView();

        startLocationListener();

        redrawAreas();

        LatLng warsaw = new LatLng(52.2946859,20.99620413);

        float zoomValue = 14; // 10 -> whole Warsaw (30km wide), 15 -> like 1km wide
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, zoomValue));
    }

    public void configureFabs() {
        // assign action to action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLocation != null) { // allow to spend only if location is ready
                    spendToken();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "wait for GPS signal", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        FloatingActionButton fabBuy = (FloatingActionButton) findViewById(R.id.fab_buy);
        fabBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "1 token bought", Toast.LENGTH_SHORT);
                toast.show();
                amountOfTokens += 1;
                updateTokenView();
            }
        });

        final FloatingActionButton fabSocial = (FloatingActionButton) findViewById(R.id.fab_social);
        fabSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLocation != null) { // allow to spend only if location is ready
                    Toast toast = Toast.makeText(getApplicationContext(), "sharing on fb", Toast.LENGTH_SHORT);
                    amountOfTokens += 1;
                    freeFlag = true;
                    updateTokenView();
                    fabSocial.hide();
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "wait for GPS signal", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void spendToken() {
        if (clickCount < 10) {
            if (amountOfTokens > 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "token spent", Toast.LENGTH_SHORT);
                toast.show();
                amountOfTokens--;
                clickCount++;
                updateTokenView();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "buy tokens first!", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            // start win activity
            Intent intent = new Intent(getApplicationContext(), WinActivity.class);
            startActivity(intent);

            // hide wasted spot
            if (currentLocation != null) {
                areaList.remove(inCircle(currentLocation));
                redrawAreas();
            }
        }
    }

    public void updateTokenView() {
        TextView tokenCount = (TextView) findViewById(R.id.tokenCount);
        tokenCount.setText("Tokens: "+amountOfTokens);
    }

    public void checkInCircle(Location location) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabSocial = (FloatingActionButton) findViewById(R.id.fab_social);
        if(inCircle(location) != null) {
            // in circle -> show action button
            fab.show();
            if(freeFlag == false) fabSocial.show();
        } else {
            // outside -> hide button
            fab.hide();
            fabSocial.hide();
        }
    }

    public GeolocationRadius inCircle(Location currentLocation) {
        for(GeolocationRadius area : areaList) {
            float distance = getDistanceInMeters(currentLocation, new LatLng(area.getLatitude(), area.getLongitude()));
            if (distance <= area.getCircleRadius()) {
                return area;
            }
        }
        return null;
    }

    public void redrawAreas() {
        for(GeolocationRadius area : areaList) {
            Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(area.getLatitude(), area.getLongitude()))
                .radius(area.getCircleRadius())
                .strokeColor(Color.GREEN)
            );
        }
    }

    public float getDistanceInMeters(Location location, LatLng latLng) {
        Location fromLatLng = new Location("");
        fromLatLng.setLongitude(latLng.longitude);
        fromLatLng.setLatitude(latLng.latitude);
        return location.distanceTo(fromLatLng);
    }

    public void startLocationListener() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("location changed", location.toString());
                currentLocation = location;
                mMap.clear();
                mMap.addCircle(new CircleOptions()
                    .center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .fillColor(Color.BLUE)
                    .radius(10));
                redrawAreas();
                checkInCircle(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        try {
            // if permission not yet given
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CONSTANT);
            }
            // you can change NETWORK_PROVIDER to GPS_PROVIDER to get GPS location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            // user didn't permit location
            e.printStackTrace();
        }
    }
}
