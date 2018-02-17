package com.example.matthew.openice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Double.parseDouble;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, OnInfoWindowClickListener {

    private GoogleMap mMap;
    /** Provides the entry point to Google Play services */
    protected GoogleApiClient mGoogleApiClient;
    /** Stores parameters for request to the FusedLocationProviderApi */
    protected LocationRequest mLocationRequest;
    /** Represents a geographical location */
    protected Location mCurrentLocation;
    /** Represents two minutes in milliseconds */
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    /** Stores the information for each rink parsed from csv file */
    List<String[]> rinks;
    /** Static variables **/
    private static final int ID_IDX = 0;
    private static final int ID_NAME = 2;
    private static final int ID_SIZE = 4;
    private static final int ID_LIT = 5;
    private static final int ID_PADS = 6;
    private static final int ID_DISTRICT = 7;
    private static final int ID_WARD = 8;
    private static final int ID_ADDRESS = 9;
    private static final int ID_LONG = 10;
    private static final int ID_LAT = 11;
    private static final int ID_WASHROOM = 12;
    private static final int ID_CHANGEROOM = 13;
    private static final int ID_TRAIL = 14;
    private static final int ID_RENTAL = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Kick off the process of building the GoogleApiClient and LocationRequest
        buildGoogleApiClient();
        createLocationRequest();

        rinks = parseCsv("rinks.csv");

    }

    /** Parse data from CSV file */
    List<String[]> parseCsv(String filename) {
        String next[];
        List<String[]> list = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open(filename)));
            // skip the first line, since it's just column headers
            reader.readNext();
            // parse each line into a string array and add it to the list
            while((next = reader.readNext()) != null) {
                list.add(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
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

        // Add a marker in Toronto, Ontario and move the camera
        LatLng toronto = new LatLng(43.7, -79.4);
        // Zoom ranges from 2.0 to 21.0
        float zoomLevel = 12;
        mMap.addMarker(new MarkerOptions().position(toronto).title("Toronto Marker"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(toronto, zoomLevel));

        // Add rink location markers to the map
        addMarkers();

        int permissionState = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    updateLocation(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // Enable the 'My Location' button, zoom controls, compass, and map toolbar
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12345);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    /** Adds a marker for each rink location to the map */
    public void addMarkers() {
        for(String rink[] : rinks) {
            Double lng = 0.0;
            Double lat = 0.0;
            LatLng pos = null;
            String rinkName = rink[ID_NAME];
            String address = rink[ID_ADDRESS];
            // if coordinates already exist, get the location
            if (!rink[ID_LONG].isEmpty() && !rink[ID_LAT].isEmpty()) {
                lng = parseDouble(rink[ID_LONG]);
                lat = parseDouble(rink[ID_LAT]);
                pos = new LatLng(lat, lng);
            }
            else {
                // attempt to get coordinates from the address using Geocoder
                try {
                    Geocoder geocoder = new Geocoder(this);
                    String addressPlus = rink[ID_ADDRESS] + ", Toronto";
                    List<Address> location = geocoder.getFromLocationName(addressPlus, 1);
                    lng = location.get(0).getLongitude();
                    lat = location.get(0).getLatitude();
                    pos = new LatLng(lat, lng);
                    System.out.println("ADDED MARKER WITH GEOCODER FOR ADDRESS: " + addressPlus + "@ LAT: " + lat + " LONG: " + lng);
                }
                catch (IOException ioException) {
                    // catch network or other I/O problems
                    String errorMessage = "Unable to retrieve location data";
                    Log.e("Geocoder: ", errorMessage);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // catch invalid location name value
                    String errorMessage = "Invalid location name";
                    Log.e("Geocoder: ", errorMessage);
                }
            }

            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(rinkName)
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.skate)));
        }

        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // get the proper marker information
                for (String rink[] : rinks) {
                    if(marker.getTitle().equals(rink[ID_NAME])) {
                        Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);

                        intent.putExtra("rinkId", rink[ID_IDX]);
                        intent.putExtra("rinkName", rink[ID_NAME]);
                        intent.putExtra("rinkSize", rink[ID_SIZE]);
                        intent.putExtra("litArea", rink[ID_LIT]);
                        intent.putExtra("numOfPads", rink[ID_PADS]);
                        intent.putExtra("district", rink[ID_DISTRICT]);
                        intent.putExtra("ward", rink[ID_WARD]);
                        intent.putExtra("address", rink[ID_ADDRESS]);
                        intent.putExtra("washroom", rink[ID_WASHROOM]);
                        intent.putExtra("changeroom", rink[ID_CHANGEROOM]);
                        intent.putExtra("skateTrail", rink[ID_TRAIL]);
                        intent.putExtra("skateRental", rink[ID_RENTAL]);

                        startActivity(intent);
                    }
                }
            }
        });
    }

    /** Builds a GoogleApiClient. */
    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /** Creates a location request */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /** Updates the location displayed on the map */
    private void updateLocation(Location location) {
        System.out.println("Received location update " + location.getLatitude() + " " + location.getLongitude());
        // If the new location is better, update current location
        if(isBetterLocation(location, mCurrentLocation)) {
            mCurrentLocation = location;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionState = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            if (mCurrentLocation == null) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                LatLng latLngLoc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngLoc, 12);
                mMap.animateCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
