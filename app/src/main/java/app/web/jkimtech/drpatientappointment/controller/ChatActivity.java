package app.web.jkimtech.drpatientappointment.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Common.Common;
import app.web.jkimtech.drpatientappointment.model.Message;
import app.web.jkimtech.drpatientappointment.model.adapter.MessageAdapter;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Bundle extras;
    private CollectionReference MessageRef1 ;
    private CollectionReference MessageRef2 ;
    private MessageAdapter adapter;
    private TextInputEditText send;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        extras = getIntent().getExtras();

        // Get the references of the messages from the database
        MessageRef1 = FirebaseFirestore.getInstance().collection("chat").document(extras.getString("key1")).collection("message");
        MessageRef2 = FirebaseFirestore.getInstance().collection("chat").document(extras.getString("key2")).collection("message");
        send = (TextInputEditText)findViewById(R.id.activity_mentor_chat_message_edit_text);
        btnSend = (Button)findViewById(R.id.activity_mentor_chat_send_button);
        setUpRecyclerView();
        btnSend.setOnClickListener(v -> {
            // Send the message to the database
            Message msg = new Message(send.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
            MessageRef1.document().set(msg);
            MessageRef2.document().set(msg);
            send.setText("");
        });

        // change the text input field based on the current user
        //if (Common.CurrentUserType.equals("Doctor")) {
        //    send.setHint("Send a message to your patient");
        //} else {
        //    send.setHint("Send a message to your doctor");
        //}
    }

    // Set up the RecyclerView
    private void setUpRecyclerView() {
        // Get the messages from the database
        Query query = MessageRef1.orderBy("dateCreated");
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
        adapter = new MessageAdapter(options);

        // Display the messages in a RecyclerView
        RecyclerView recyclerView = findViewById(R.id.activity_mentor_chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    // Start listening when the user enters the app
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // Stop listening when the user gets out of the app
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}