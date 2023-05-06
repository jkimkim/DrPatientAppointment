package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.adapter.MessageAdapter;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Bundle extras;
    private CollectionReference MessageRef1 ;
    private CollectionReference MessageRef2 ;
    private MessageAdapter adapter;
    private TextInputEditText envoyer;
    private Button btnEnvoyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
}