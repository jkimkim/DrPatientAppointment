package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.MainActivity;
import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Common.Common;

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
        Common.CurrentUserid= FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        FirebaseFirestore.getInstance().collection("User").document(Common.CurrentUserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Common.CurrentUserName = documentSnapshot.getString("name");
            }
        });
    }
    // on back pressed create a dialog to ask if user wants to exit
    // if yes exit the app
    @Override
    public void onBackPressed() {
        MainActivity.exitPatientApp(this);
    }
}