package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import app.web.jkimtech.drpatientappointment.R;
import io.alterac.blurkit.BlurLayout;

public class DoctorProfileActivity extends AppCompatActivity {
    //blurkit library
    BlurLayout blurLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
    }
}