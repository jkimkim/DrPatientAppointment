package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.adapter.DoctorAppointmentAdapter;

public class DoctorAppointmentActivity extends AppCompatActivity {
    // TODO: Rename parameter arguments, choose names that match
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorAppointementRef = db.collection("Doctor");
    private DoctorAppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        // Get the doctors by patient id
        final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = doctorAppointementRef.document("" + doctorID + "")
                .collection("apointementrequest")
                .orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<app.web.jkimtech.drpatientappointment.model.ApointementInformation> options = new FirestoreRecyclerOptions.Builder<app.web.jkimtech.drpatientappointment.model.ApointementInformation>()
                .setQuery(query, app.web.jkimtech.drpatientappointment.model.ApointementInformation.class)
                .build();
        adapter = new DoctorAppointmentAdapter(options);
        // List current appointments
        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.DoctorAppointment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    // Start listening for changes
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // Stop listening for changes
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}