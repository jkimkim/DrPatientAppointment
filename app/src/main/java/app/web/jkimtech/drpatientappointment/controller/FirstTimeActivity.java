package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import app.web.jkimtech.drpatientappointment.MainActivity;
import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.firestoreApi.DoctorHelper;
import app.web.jkimtech.drpatientappointment.model.firestoreApi.PatientHelper;
import app.web.jkimtech.drpatientappointment.model.firestoreApi.UserHelper;

public class FirstTimeActivity extends AppCompatActivity {
// this is the first time activity that will be shown to the user when they first install the app and clicks on the sign up button
    // this activity will ask the user for their credentials and will be stored in the database i.e. name, email, password, phone number, etc.
    private static final String TAG = "FirstTimeActivity";

    private EditText fullName;
    private EditText birthday;
    private EditText teL;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        btn = (Button) findViewById(R.id.confirmBtn);
        fullName = (EditText) findViewById(R.id.firstSignFullName);
        birthday = (EditText) findViewById(R.id.firstSignBirthDay);
        teL = (EditText) findViewById(R.id.firstSignTel);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);// this is the array that will be shown in the spinner
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// this is the layout that will be shown when the user clicks on the spinner
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        final Spinner specialtyList = (Spinner) findViewById(R.id.specialty_spinner);
        ArrayAdapter<CharSequence> adapterSpecialtyList = ArrayAdapter.createFromResource(this,
                R.array.specialty_spinner, android.R.layout.simple_spinner_item);
        adapterSpecialtyList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialtyList.setAdapter(adapterSpecialtyList);
        String newAccountType = spinner.getSelectedItem().toString();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
// this is the method that will be called when the user clicks on the spinner
                String selected = spinner.getSelectedItem().toString();
                if (selected.equals("Doctor")) {
                    specialtyList.setVisibility(View.VISIBLE);
                } else {
                    specialtyList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                specialtyList.setVisibility(View.GONE);
            }
        });
        // this is the method that will be called when the user clicks on the confirm button
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String fullname, birtDay, tel, type, specialty;
                fullname = fullName.getText().toString();
                birtDay = birthday.getText().toString();
                tel = teL.getText().toString();
                type = spinner.getSelectedItem().toString();
                specialty = specialtyList.getSelectedItem().toString();
                UserHelper.addUser(fullname, birtDay, tel, type);
                if (type.equals("Patient")){
                    PatientHelper.addPatient(fullname, "address", tel);
                } else if (type.equals("Doctor")) {
                    DoctorHelper.addDoctor(fullname, "address", tel, specialty);
                }
                Intent intent = new Intent(FirstTimeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}