package app.web.jkimtech.drpatientappointment.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.controller.ChatActivity;
import app.web.jkimtech.drpatientappointment.model.Doctor;

public class MyDoctorsAdapter extends FirestoreRecyclerAdapter<Doctor, MyDoctorsAdapter.MyDoctorAppointementHolder> {
    // Reference to the Firebase storage for images
    StorageReference pathReference ;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.
     * @param options the FirestoreRecyclerOptions<Doctor> object containing the query to be listened to
     */
    public MyDoctorsAdapter(@NonNull FirestoreRecyclerOptions<Doctor> options) {
        super(options);
    }

    /**
     * Binds the data from the Doctor object to the layout views of the RecyclerView item
     * @param myDoctorsHolder the ViewHolder containing the layout views to bind the data to
     * @param position the position of the item in the RecyclerView
     * @param doctor the Doctor object containing the data to bind to the layout views
     */

    @Override
    protected void onBindViewHolder(@NonNull MyDoctorAppointementHolder myDoctorsHolder, int position, @NonNull final Doctor doctor) {
        // Set the text of the title and description views to the name and speciality of the doctor
        myDoctorsHolder.textViewTitle.setText(doctor.getName());
        myDoctorsHolder.textViewDescription.setText(String.format("Speciality : %s", doctor.getSpeciality()));
        // Set the onClickListener for the send message button to open the ChatActivity with the corresponding doctor
        myDoctorsHolder.sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(v.getContext(),doctor);
            }
        });
        // Set the onClickListener for the call button to open the phone app with the corresponding phone number
        myDoctorsHolder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage(myDoctorsHolder.sendMessageButton.getContext(),doctor.getTel());
            }
        });
        // Load the profile image for the doctor from Firebase Storage and display it in the ImageView
        String imageId = doctor.getEmail()+".jpg"; //add a title image
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId); //storage the image
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.logo)
                    .fit()
                    .centerCrop()
                    .into(myDoctorsHolder.imageViewDoctor);//Image location

            // profileImage.setImageURI(uri);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

/**
 * Opens the phone app with the specified phone number
 * @param wf the context of the current activity
 * @param phoneNumber the phone number to call
 * */
    private void openPage(Context wf, String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        wf.startActivity(intent);
    }

    // Opens the ChatActivity with the specified doctor
    private void openPage(Context wf, Doctor d){
        Intent i = new Intent(wf, ChatActivity.class);
        i.putExtra("key1",d.getEmail()+"_"+ FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        i.putExtra("key2",FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()+"_"+d.getEmail());
        wf.startActivity(i);
    }

    // Creates a new ViewHolder for the RecyclerView items
    @NonNull
    @Override
    public MyDoctorAppointementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_doctor_item, parent, false);
        return new MyDoctorAppointementHolder(v);
    }

    // Holds the layout views for the RecyclerView items
    class MyDoctorAppointementHolder extends RecyclerView.ViewHolder{
        //Here we hold the MyDoctorItems
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewStatus;
        ImageView imageViewDoctor;
        Button sendMessageButton;
        Button callBtn;
        Button contactButton;
        // Constructor for the MyDoctorAppointementHolder
        public MyDoctorAppointementHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.doctor_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewStatus = itemView.findViewById(R.id.onlineStatus);
            imageViewDoctor = itemView.findViewById(R.id.doctor_item_image);
            sendMessageButton = itemView.findViewById(R.id.see_form_button);
            callBtn = itemView.findViewById(R.id.callBtn);
            contactButton = itemView.findViewById(R.id.contact);
        }
    }
}
