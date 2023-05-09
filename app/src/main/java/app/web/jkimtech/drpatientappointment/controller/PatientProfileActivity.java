package app.web.jkimtech.drpatientappointment.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import app.web.jkimtech.drpatientappointment.R;

public class PatientProfileActivity extends AppCompatActivity {
    private MaterialTextView doctorName;
    private MaterialTextView doctorSpe;
    private MaterialTextView doctorPhone;
    private MaterialTextView doctorEmail;
    private MaterialTextView doctorAddress;
    private MaterialTextView doctorAbout;
    private ImageView doctorImage;
    StorageReference pathReference ;
    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("Patient").document("" + doctorID + "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        doctorName = findViewById(R.id.doctor_name);
        doctorSpe = findViewById(R.id.doctor_specialty);
        doctorPhone = findViewById(R.id.doctor_phone);
        doctorEmail = findViewById(R.id.doctor_email);
        doctorAddress = findViewById(R.id.doctor_address);
        doctorAbout = findViewById(R.id.doctor_about);
        doctorImage = findViewById(R.id.imageView3);
        Drawable defaultImage = getResources().getDrawable(R.drawable.ic_anon_user_48dp); //default user image
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.show();
        // display profile image
        String imageId = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        pathReference = FirebaseStorage.getInstance().getReference().child("PatientProfile/"+ imageId+".jpg");
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri)
                    .placeholder(R.drawable.logo)
                    .fit()
                    .centerCrop()
                    .into(doctorImage);
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            doctorImage.setImageDrawable(defaultImage);
            dialog.dismiss();
        });
        // display profile info
        docRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            doctorName.setText(documentSnapshot.getString("name"));
            doctorPhone.setText(documentSnapshot.getString("tel"));
            doctorEmail.setText(documentSnapshot.getString("email"));
            doctorAddress.setText(documentSnapshot.getString("address"));
            doctorImage.setImageDrawable(defaultImage);
        });
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
    }
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }
    //Handling Action Bar button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Back button
            case R.id.back:
                //If this activity started from other activity
                finish();
                startHomeActivity();
                return true;
            case R.id.edit:
                //If the edit button is clicked.
                startEditActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void startEditActivity() {
        Intent intent = new Intent(this, EditProfilePatientActivity.class);
        intent.putExtra("CURRENT_NAME", doctorName.getText().toString());
        intent.putExtra("CURRENT_PHONE", doctorPhone.getText().toString());
        intent.putExtra("CURRENT_ADDRESS", doctorAddress.getText().toString());
        startActivity(intent);
        finish();
    }
}

// Path: app\src\main\java\app\web\jkimtech\drpatientappointment\controller\EditProfilePatientActivity.java
// NOTE: this project uses Deprecated Gradle features it is therefore incompatible with Gradle 8.0!
// NOTE: this file uses the same methods as ProfileDoctorActivity.java to edit the profile and upload the image because the process is the same
// NOTE: this file uses the same xml file as ProfileDoctorActivity.java to display the patient profile.
// path: app\src\main\res\layout\activity_doctor_profile.xml
// path: app\src\main\res\layout\activity_edit_profile_patient.xml
// NOTE: this is a modified version of ProfilePatientActivity.java from DaktarLagbe project with some to a lot of modification and so are most of the files in this project
// NOTE: however the design of the app is mostly the same!
// NOTE: this file and others are modified to fit the purpose of this project
// NOTE: this file is used to edit the patient profile
// NOTE: before making any changes to this project, please connect with your firebase account and change the google-services.json file in the app folder
// NOTE: this project is connected to firebase firestore and firebase storage
// NOTE: this project is connected to firebase authentication with email and password
// NOTE: when editing any file, please make sure to change the package name to your own package name.