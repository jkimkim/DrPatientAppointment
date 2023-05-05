package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import app.web.jkimtech.drpatientappointment.MainActivity;
import app.web.jkimtech.drpatientappointment.R;

public class HomeActivity extends AppCompatActivity {
    Button searchBtn;
    Button myDoctors;
    Button btnMedicalFolder;
    Button profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        searchBtn = findViewById(R.id.searchBtn);
        myDoctors = findViewById(R.id.myDoctors);
        btnMedicalFolder = findViewById(R.id.btnMedicalFolder);
        profile = findViewById(R.id.profile);
        // on click listener for profile button
        profile.setOnClickListener(v -> {
            MainActivity.goToPatientProfile(this);
        });
        // on click listener for btnMedicalFolder button
        btnMedicalFolder.setOnClickListener(v -> {
            MainActivity.goToMedicalFolder(this);
        });
        // on click listener for myDoctors button
        myDoctors.setOnClickListener(v -> {
            MainActivity.goToMyDoctors(this);
        });
        // on click listener for searchBtn button
        searchBtn.setOnClickListener(v -> {
            MainActivity.goToSearch(this);
        });
    }
    // on back pressed create a dialog to ask if user wants to exit
    // if yes exit the app
    @Override
    public void onBackPressed() {
        MainActivity.exitPatientApp(this);
    }
}