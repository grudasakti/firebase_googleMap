package com.example.tugas4;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tugas4.databinding.ActivityMapsBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private EditText latitude, longtitude;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitude = findViewById(R.id.latitude);
        longtitude = findViewById(R.id.longtitude);
        btnSave = findViewById(R.id.btn_save);

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("locations");
        mFirebaseInstance.getReference("app_title").setValue("GMaps Database");
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

        LatLng salatiga = new LatLng(-7.3305, 110.5084);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(salatiga, 15.0f));

        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .draggable(true).icon(BitmapDescriptorFactory.defaultMarker
                        (new Random().nextInt(360))).position(latLng).title("Marker"));

        getCompleteAddressString(latLng);

        if(marker != null){
            mMap.setOnMapClickListener(null);
        }
    }

    private String getCompleteAddressString(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses != null) {
                String strLat = String.valueOf(addresses.get(0).getLatitude());
                String strLng = String.valueOf(addresses.get(0).getLongitude());

                latitude.setText(strLat);
                longtitude.setText(strLng);

                createLocation(strLat, strLng);
                Toast.makeText(getApplicationContext(),"Saved Location", Toast.LENGTH_LONG).show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputLat = latitude.getText().toString();
                        String inputLng = longtitude.getText().toString();

                        LatLng inputLatLng = new LatLng(Double.parseDouble(inputLat), Double.parseDouble(inputLng));

                        mMap.addMarker(new MarkerOptions()
                                .draggable(true).icon(BitmapDescriptorFactory.defaultMarker
                                        (new Random().nextInt(360))).position(inputLatLng).title("New Marker"));

                        updateLocation(inputLat, inputLng);
                        Toast.makeText(getApplicationContext(),"Updated Location", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.w("CurrentLocation", "No addresses!");
            }
        }catch (Exception ex) {
            Log.w("CurrentLocation", "Can't get addresses");
        }
        return null;
    }

    private void createLocation(String strLat, String strLng) {
        if(TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }
        Location location = new Location(strLat, strLng);

        mFirebaseDatabase.child(userId).setValue(location);
    }

    private void updateLocation(String inputLat, String inputLng){
        if(!TextUtils.isEmpty(inputLat))
            mFirebaseDatabase.child(userId).child("latitude").setValue(inputLat);

        if(!TextUtils.isEmpty(inputLng))
            mFirebaseDatabase.child(userId).child("longtitude").setValue(inputLng);
    }
}