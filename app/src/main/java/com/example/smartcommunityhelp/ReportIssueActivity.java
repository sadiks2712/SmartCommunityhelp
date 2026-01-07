package com.example.smartcommunityhelp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.HashMap;
import java.util.Map;

public class ReportIssueActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 101;
    private static final int LOCATION_PERMISSION_CODE = 201;

    ImageView imgPreview;
    EditText edtType;
    TextView txtLocation;
    Uri imageUri;

    double latitude = 0, longitude = 0;

    FirebaseFirestore db;
    FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        imgPreview = findViewById(R.id.imgPreview);
        edtType = findViewById(R.id.edtType);
        txtLocation = findViewById(R.id.txtLocation);

        Button btnPickImage = findViewById(R.id.btnPickImage);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        db = FirebaseFirestore.getInstance();
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        btnPickImage.setOnClickListener(v -> pickImage());
        btnGetLocation.setOnClickListener(v -> fetchLocationSafely());
        btnSubmit.setOnClickListener(v -> saveIssue());
    }

    /* ---------------- IMAGE PICKER ---------------- */

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
            detectImage(imageUri);
        }
    }

    /* ---------------- ML KIT IMAGE DETECTION ---------------- */

    private void detectImage(Uri uri) {
        try {
            InputImage image = InputImage.fromFilePath(this, uri);
            ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    .process(image)
                    .addOnSuccessListener(labels -> {
                        if (!labels.isEmpty()) {
                            edtType.setText(labels.get(0).getText());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- LOCATION (SAFE & PROFESSIONAL) ---------------- */

    private void fetchLocationSafely() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );
            return;
        }

        try {
            locationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            txtLocation.setText(
                                    "Lat: " + latitude + ", Lng: " + longitude
                            );
                        } else {
                            Toast.makeText(this,
                                    "Turn ON GPS and try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (SecurityException e) {
            Toast.makeText(this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            fetchLocationSafely();
        }
    }

    /* ---------------- FIRESTORE ---------------- */

    private void saveIssue() {

        if (latitude == 0 || longitude == 0) {
            Toast.makeText(this,
                    "Please get location first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> issue = new HashMap<>();
        issue.put("type", edtType.getText().toString());
        issue.put("lat", latitude);
        issue.put("lng", longitude);
        issue.put("status", "Pending");

        db.collection("issues")
                .add(issue)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this,
                            "Issue Reported Successfully",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MapsActivity.class));
                });
    }
}
