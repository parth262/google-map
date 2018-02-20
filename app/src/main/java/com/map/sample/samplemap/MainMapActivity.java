
package com.map.sample.samplemap;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;

import java.net.MalformedURLException;

public class MainMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    LatLng origin, destination;
    Button button;

    //added for Get Place
    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MainMapActivity.class.getSimpleName();
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    /*private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private CameraPosition mCameraPosition;

    //added for place picker
    private int PLACE_PICKER_REQUEST = 1;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        //added for Get Place
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this,null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button = findViewById(R.id.get_direction);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                GetDirections getDirections = new GetDirections(origin, destination);
                getDirections.execute(mMap);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*LatLng sydney = new LatLng(-33.852,151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        //added for Get Place
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_content, null);
                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });

        /*mMap.setOnPoiClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationClickListener(this);*/

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();



        PlaceAutocompleteFragment placeAutocompleteFragmentOrigin =  (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_origin);
        placeAutocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                origin = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title((String) place.getName()));
                Toast.makeText(MainMapActivity.this, place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MainMapActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        PlaceAutocompleteFragment placeAutocompleteFragmentDestination =  (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);
        placeAutocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title((String) place.getName()));
                Toast.makeText(MainMapActivity.this, place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MainMapActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //added for place picker


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST) {
            if(resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Toast.makeText(this, place.getName(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(resultCode == RESULT_OK) {
            }
        }
    }*/

    //added for Get Place
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    //added for Get Place
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }*/

    //added for Get Place
    private void getLocationPermission() {
        /*
        * Request location permission, so that we can get the location of the
        * device. The result of the permission request is handled by a callback,
        * onRequestPermissionsResult.
        */
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //added for Get Place
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    //added for Get Place
    private void updateLocationUI() {
        if(mMap == null) {
            return;
        }
        try {
            if(mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
               // mMap.getUiSettings().setMyLocationButtonEnabled(true);

            }
            else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //added for Get Place
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if(mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                        }
                        else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //added for Get Place
    /*private void showCurrentPlace() {
        if(mMap == null) {
            return;
        }
        if(mLocationPermissionGranted) {
            @SuppressLint("MissingPermission")
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    if(task.isSuccessful() && task.getResult() != null) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                        int count;
                        if(likelyPlaces.getCount() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getCount();
                        }
                        else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        mLikelyPlaceNames = new String[count];
                        mLikelyPlaceAddresses = new String[count];
                        mLikelyPlaceAttributions = new String[count];
                        mLikelyPlaceLatLngs = new LatLng[count];

                        for(PlaceLikelihood placeLikelihood : likelyPlaces) {
                            mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                            mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
                            mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace().getAttributions();
                            mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if(i > (count - 1))
                                break;
                        }
                        likelyPlaces.release();

                        openPlacesDialog();
                    }
                    else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        }
        else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            getLocationPermission();
        }
    }*/

   /* private void openPlacesDialog() {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if(mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,DEFAULT_ZOOM));
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }*/

   /* @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {

        mMap.addMarker(new MarkerOptions()
        .title(pointOfInterest.name)
        .position(pointOfInterest.latLng));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointOfInterest.latLng,DEFAULT_ZOOM));
        Toast.makeText(getApplicationContext(),"Clicked: " +
                pointOfInterest.name + "\nPlace ID:" + pointOfInterest.placeId
                + "\nLatitude: " + pointOfInterest.latLng.latitude
                + " Longitude: " + pointOfInterest.latLng.longitude,Toast.LENGTH_LONG).show();
    }*/

   /* @Override
    public void onMapClick(LatLng latLng) {

        Toast.makeText(this, "map clicked", Toast.LENGTH_SHORT).show();
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Undefined")
                .snippet("Lat: " + latLng.latitude + "\nLong: " + latLng.longitude)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }*/

    /*@Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "my location clicked", Toast.LENGTH_SHORT).show();
        //added for place picker
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }*/
}
