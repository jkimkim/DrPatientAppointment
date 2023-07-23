package app.web.jkimtech.drpatientappointment.controller;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import app.web.jkimtech.drpatientappointment.R;

public class DoctorProfileActivity extends AppCompatActivity {
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
    DocumentReference docRef = db.collection("Doctor").document("" + doctorID + "");

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
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId+".jpg");
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
            doctorAbout.setText(documentSnapshot.getString("about"));
            doctorSpe.setText(documentSnapshot.getString("speciality"));
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
    //Start Home Activity
    private void startHomeActivity() {
        Intent intent = new Intent(this, DoctorHomeActivity.class);
        startActivity(intent);
        finish();
    }
    //Start Edit Activity
    private void startEditActivity() {
        Intent intent = new Intent(this, EditProfileDoctorActivity.class);
        intent.putExtra("CURRENT_NAME", doctorName.getText().toString());
        intent.putExtra("CURRENT_PHONE", doctorPhone.getText().toString());
        intent.putExtra("CURRENT_ADDRESS", doctorAddress.getText().toString());
        intent.putExtra("CURRENT_ABOUT", doctorAbout.getText().toString());
        startActivity(intent);
        finish();
    }
}