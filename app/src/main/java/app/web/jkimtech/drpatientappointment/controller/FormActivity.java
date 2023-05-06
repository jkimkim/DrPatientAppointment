package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Form;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText disease;
    private EditText description;
    private EditText treatment;
    private Spinner formType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        disease = findViewById(R.id.disease);
        description = findViewById(R.id.form_description);
        treatment = findViewById(R.id.treatment);
        formType = findViewById(R.id.form_type);
        //Spinner to choose fiche type
        Spinner spinner = findViewById(R.id.form_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.form_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //Add fiche
        Button addFicheButton = findViewById(R.id.add_form_btn);
        addFicheButton.setOnClickListener(view -> addFiche());
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String SeltectedFicheType = adapterView.getItemAtPosition(i).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void addFiche(){
        String disease = this.disease.getText().toString();
        String description =  this.description.getText().toString();
        String treatment = this.treatment.getText().toString();
        String type = formType.getSelectedItem().toString();

        String patient_name = getIntent().getStringExtra("patient_name");
        String patient_email = getIntent().getStringExtra("patient_email");
        CollectionReference formRef = FirebaseFirestore.getInstance().collection("Patient").document(""+patient_email+"")
                .collection("MyMedicalFolder");
        formRef.document().set(new Form(disease, description, treatment, type, FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()));
        //ficheRef.add(new Fiche(disease, description, treatment, type, FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()));
        Toast.makeText(this, "Form added."+patient_name, Toast.LENGTH_LONG).show();
        finish();
    }
}