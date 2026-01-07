package com.example.smartcommunityhelp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.firestore.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();

        // ✅ REQUIRED: SupportMapFragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map fragment error", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadIssuesFromFirestore();
    }

    // Load issues and show markers
    private void loadIssuesFromFirestore() {
        db.collection("issues")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        Double lat = doc.getDouble("lat");
                        Double lng = doc.getDouble("lng");
                        String type = doc.getString("type");
                        String status = doc.getString("status");

                        if (lat != null && lng != null) {
                            LatLng pos = new LatLng(lat, lng);
                            mMap.addMarker(
                                    new MarkerOptions()
                                            .position(pos)
                                            .title(type)
                                            .snippet("Status: " + status)
                            );
                            mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(pos, 12)
                            );
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to load issues",
                                Toast.LENGTH_SHORT).show());
    }
}
