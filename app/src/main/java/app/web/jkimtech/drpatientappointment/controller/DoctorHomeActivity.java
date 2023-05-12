package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.MainActivity;
import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Common.Common;

public class DoctorHomeActivity extends AppCompatActivity {

    Button profile;
    Button btnRequest;
    Button myCalendarBtn;
    Button listPatients;
    Button appointment;
    Button signOutBtn;
    TextView welcomeText;
    final String patID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Doctor").document("" + patID + "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        profile = findViewById(R.id.profile);
        btnRequest = findViewById(R.id.btnRequest);
        myCalendarBtn = findViewById(R.id.myCalendarBtn);
        listPatients = findViewById(R.id.listPatients);
        appointment = findViewById(R.id.appointment);
        signOutBtn = findViewById(R.id.signOutBtn);
        Common.CurrentDoctor = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Common.CurrentUserType = "Doctor";
        welcomeText = findViewById(R.id.doctorName);
        // set welcome message
        documentReference.addSnapshotListener(this,(documentSnapshot, e) -> {
            assert documentSnapshot != null;
            welcomeText.setText(String.format("Welcome Dr. %s", documentSnapshot.getString("name")));
        });
        // on click listener for profile button
        profile.setOnClickListener(v -> {
            MainActivity.goToProfile(this);
        });
        // on click listener for request button
        btnRequest.setOnClickListener(v -> {
            MainActivity.goToAppointment(this);
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
            MainActivity.goToRequest(this);
        });
        // on click listener for signOutBtn button
        signOutBtn.setOnClickListener(v -> {
            MainActivity.signOut(this);
        });
    }
    // on back pressed create a dialog to ask if user wants to exit
    // if yes exit the app
    @Override
    public void onBackPressed() {
        MainActivity.exitApp(this);
    }
}