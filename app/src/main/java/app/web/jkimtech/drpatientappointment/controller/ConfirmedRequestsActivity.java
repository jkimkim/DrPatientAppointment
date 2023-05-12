package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.adapter.ConfirmedAppointmentsAdapter;

public class ConfirmedRequestsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myDoctorsRef = db.collection("Doctor");
    private ConfirmedAppointmentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_requests);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = myDoctorsRef.document(""+doctorID+"")
                .collection("calendar").orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<app.web.jkimtech.drpatientappointment.model.ApointementInformation> options = new FirestoreRecyclerOptions.Builder<app.web.jkimtech.drpatientappointment.model.ApointementInformation>()
                .setQuery(query, app.web.jkimtech.drpatientappointment.model.ApointementInformation.class)
                .build();
        adapter = new ConfirmedAppointmentsAdapter(options);
        //List current appointments
        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.confirmed_appointments_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    //Start listening for changes
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    //Stop listening for changes
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}