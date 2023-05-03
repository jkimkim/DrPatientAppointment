package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import app.web.jkimtech.drpatientappointment.MainActivity;
import app.web.jkimtech.drpatientappointment.R;

public class DoctorHomeActivity extends AppCompatActivity {

    Button profile;
    Button btnRequest;
    Button myCalendarBtn;
    Button listPatients;
    Button appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        profile = findViewById(R.id.profile);
        btnRequest = findViewById(R.id.btnRequest);
        myCalendarBtn = findViewById(R.id.myCalendarBtn);
        listPatients = findViewById(R.id.listPatients);
        appointment = findViewById(R.id.appointment);
        // on click listener for profile button
        profile.setOnClickListener(v -> {
            MainActivity.goToProfile(this);
        });
        // on click listener for request button
        btnRequest.setOnClickListener(v -> {
            MainActivity.goToRequest(this);
        });
        // on click listener for myCalendarBtn button
        myCalendarBtn.setOnClickListener(v -> {
            MainActivity.goToMyCalendar(this);
        });
        // on click listener for listPatients button
        listPatients.setOnClickListener(v -> {
            MainActivity.goToListPatients(this);
        });
        // on click listener for appointment button
        appointment.setOnClickListener(v -> {
            MainActivity.goToAppointment(this);
        });
    }
    // on back pressed create a dialog to ask if user wants to exit
    // if yes exit the app
    @Override
    public void onBackPressed() {
        MainActivity.exitApp(this);
    }
}