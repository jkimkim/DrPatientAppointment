package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.adapter.MyPatientsAdapter;

public class MyPatientsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myPatientsRef = db.collection("Doctor");
    private MyPatientsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        Query query = myPatientsRef.document(""+doctorID+"")
                .collection("MyPatients").orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<app.web.jkimtech.drpatientappointment.model.Patient> options = new FirestoreRecyclerOptions.Builder<app.web.jkimtech.drpatientappointment.model.Patient>()
                .setQuery(query, app.web.jkimtech.drpatientappointment.model.Patient.class)
                .build();
        adapter = new MyPatientsAdapter(options);
        //ListMyPatients
        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.ListMyPatients);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    // onStart
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    // onStop
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}