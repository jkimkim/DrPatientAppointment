package app.web.jkimtech.drpatientappointment.controller;

import static app.web.jkimtech.drpatientappointment.model.Common.Common.convertBloodToInt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Common.Common;

public class PatientInfoActivity extends AppCompatActivity {
    EditText heightBtn;
    EditText weightBtn;
    Spinner bloodtypeSpinner;
    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        updateBtn = findViewById(R.id.updateInfoBtn);
        heightBtn = findViewById(R.id.heightBtn);
        weightBtn = findViewById(R.id.weightBtn);
        final Spinner specialistList = (Spinner) findViewById(R.id.bloodType);
        ArrayAdapter<CharSequence> adapterSpecialistList = ArrayAdapter.createFromResource(this,
                R.array.blood_spinner, android.R.layout.simple_spinner_item);
        adapterSpecialistList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialistList.setAdapter(adapterSpecialistList);

        String patient_name = getIntent().getStringExtra("patient_name");
        String patient_email = getIntent().getStringExtra("patient_email");

        FirebaseFirestore.getInstance().collection("Patient").document(patient_email).collection("moreInfo")
                .document(patient_email).get().addOnSuccessListener(documentSnapshot -> {
            weightBtn.setText(String.format("%s", documentSnapshot.getString("weight")));
            heightBtn.setText(String.format("%s", documentSnapshot.getString("height")));
            if(documentSnapshot.getString("bloodType") != null)
                specialistList.setSelection(convertBloodToInt(documentSnapshot.getString("bloodType")));
        });
        updateBtn.setOnClickListener(v -> {
            // check if the collection exist or not
            // if not create it
            // if yes update it
            FirebaseFirestore.getInstance().collection("Patient").document(patient_email).collection("moreInfo")
                    .document(patient_email).get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("weight", weightBtn.getText().toString());
                    map.put("height", heightBtn.getText().toString());
                    map.put("bloodType", specialistList.getSelectedItem().toString());
                    FirebaseFirestore.getInstance().collection("Patient").document(patient_email).collection("moreInfo")
                            .document(patient_email).update(map).addOnSuccessListener(aVoid -> {
                        Toast.makeText(PatientInfoActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }else{
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("weight", weightBtn.getText().toString());
                    map.put("height", heightBtn.getText().toString());
                    map.put("bloodType", specialistList.getSelectedItem().toString());
                    FirebaseFirestore.getInstance().collection("Patient").document(patient_email).collection("moreInfo")
                            .document(patient_email).set(map).addOnSuccessListener(aVoid -> {
                        Toast.makeText(PatientInfoActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        });
        if (Common.CurrentUserType.equals("Patient")){
            updateBtn.setVisibility(View.GONE);
            heightBtn.setEnabled(false);
            weightBtn.setEnabled(false);
            specialistList.setEnabled(false);
        }
    }
}