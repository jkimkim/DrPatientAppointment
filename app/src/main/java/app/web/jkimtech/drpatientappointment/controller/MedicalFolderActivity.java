package app.web.jkimtech.drpatientappointment.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
import app.web.jkimtech.drpatientappointment.model.Common.Common;
import app.web.jkimtech.drpatientappointment.model.Form;
import app.web.jkimtech.drpatientappointment.model.adapter.ConsultationFragmentAdapter;

public class MedicalFolderActivity extends AppCompatActivity {
    private FloatingActionButton createNewFormButton;
    private String patient_email;
    private Button infobtn;
    private String patient_name;
    private String patient_phone;
    private final static String TAG = "MedicalFolderActivity";

    // firebase
    final String patientID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference patRef = db.collection("Patient").document("" + patientID + "");
    StorageReference pathReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_folder);
        ImageView image = findViewById(R.id.imageView2);
        patient_email = getIntent().getStringExtra("patient_email");
        this.configureViewPager();
        Log.d(TAG, "onCreate medical folder activity: started");
        getIncomingIntent();

        createNewFormButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        createNewFormButton.setOnClickListener(view -> openPatientForm());
        if (Common.CurrentUserType.equals("Patient")) {
            createNewFormButton.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: patient");
        }
        infobtn = findViewById(R.id.infobtn);
        infobtn.setOnClickListener(v -> openPatientInfo());

        String imageId = patient_email+".jpg";
        pathReference = FirebaseStorage.getInstance().getReference().child("PatientProfile/"+imageId);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(image);//Image location
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void openPatientInfo() {
        Intent intent = new Intent(this, PatientInfoActivity.class);
        String patient_name = getIntent().getStringExtra("patient_name");
        String patient_email = getIntent().getStringExtra("patient_email");
        intent.putExtra("patient_email", patient_email);
        intent.putExtra("patient_name", patient_name);
        startActivity(intent);
    }

    private void openPatientForm() {
        Log.d(TAG, "openPatientForm: opening patient form");
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra("patient_name", patient_name);
        intent.putExtra("patient_phone", patient_phone);
        intent.putExtra("patient_email", patient_email);
        startActivity(intent);
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");
        if (getIntent().hasExtra("patient_name") && getIntent().hasExtra("patient_email")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            patient_name = getIntent().getStringExtra("patient_name");
            patient_email = getIntent().getStringExtra("patient_email");
            patient_phone = getIntent().getStringExtra("patient_phone");

            setPatient(patient_name, patient_phone, patient_email);
        } else {
            Log.d(TAG, "getIncomingIntent: no intent extras found");
            patRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Log.d(TAG, "Current data: " + documentSnapshot.getData());
                        patient_name = documentSnapshot.getString("name");
                        patient_phone = documentSnapshot.getString("tel");
                        patient_email = documentSnapshot.getString("email");

                        //set patient name, email, phone number
                        setPatient(patient_name, patient_email, patient_phone);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }
    }


    private void setPatient(String patient_name, String patient_phone, String patient_email) {
        Log.d(TAG, "setPatient: put patient info's");

        TextView name = findViewById(R.id.patient_name);
        name.setText(patient_name);

        TextView email = findViewById(R.id.patient_address);
        email.setText(patient_email);

        TextView number = findViewById(R.id.phone_number);
        number.setText(patient_phone);
    }

    private void configureViewPager() {
        // 1 - Get ViewPager from layout
        ViewPager pager = findViewById(R.id.ViewPagerDossier);
        // 2 - Set Adapter PageAdapter and glue it together
        pager.setAdapter(new ConsultationFragmentAdapter(getSupportFragmentManager()));
        // 3 - Design purpose. Tabs have the same width
        TabLayout tabs = (TabLayout) findViewById(R.id.activity_main_tabs);
        // 4 - Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager);
        // 5 - Design purpose. Tabs have the same width
        tabs.setTabMode(TabLayout.MODE_FIXED);
        TextView text = new TextView(this);
    }
}