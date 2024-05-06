package com.example.loginapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private Button generatePdfButton;
    private Button logoutButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generatePdfButton = findViewById(R.id.generate_pdf_button);
        logoutButton = findViewById(R.id.logout);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        // Request external storage permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call method to generate and display PDF with updated data
                generateAndDisplayPdf();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, activity_login.class));
                finish();
            }
        });
    }

    private void generateAndDisplayPdf() {
        // Generate PDF with the latest data from the database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder userData = new StringBuilder();

                // Loop through all users and append their data to StringBuilder
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String email = userSnapshot.child("email").getValue(String.class);
                    String mobileNumber = userSnapshot.child("mobile").getValue(String.class);
                    String name = userSnapshot.child("name").getValue(String.class);

                    userData.append("User ID: ").append(userId).append("\n");
                    userData.append("Email: ").append(email).append("\n");
                    userData.append("Mobile Number: ").append(mobileNumber).append("\n");
                    userData.append("Name: ").append(name).append("\n\n");
                }

                // Generate PDF with the accumulated user data
                generatePdf(userData.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void generatePdf(String userData) {
        // Create PDF file
        File pdfFile = new File(getExternalFilesDir(null), "userData.pdf");
        try {
            if (!pdfFile.exists()) {
                pdfFile.createNewFile();
            }

            // Write user data to PDF document
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile, false));
            document.open();
            document.add(new Paragraph(userData));
            document.close();

            // Open PDF file with PDF viewer
            openPdfFile(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPdfFile(File pdfFile) {
        // Create intent to view PDF file
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(MainActivity.this,
                MainActivity.this.getPackageName() + ".provider", pdfFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Start activity to view PDF file
        startActivity(intent);
    }
}
