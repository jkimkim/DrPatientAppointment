package app.web.jkimtech.drpatientappointment.model.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.AppointmentInformation;

public class PatientAppointmentsAdapter extends FirestoreRecyclerAdapter<AppointmentInformation, PatientAppointmentsAdapter.patientAppointmentsHolder> {
    StorageReference pathReference; // Reference to the storage path for the profile image
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Reference to the Firestore database
    DocumentReference docRef; // Reference to a document in Firestore
    DocumentSnapshot documentSnapshot; // A snapshot of a document in Firestore
    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(); // Get the current user's email as a string

    // Constructor
    public PatientAppointmentsAdapter(@NonNull FirestoreRecyclerOptions<AppointmentInformation> options) {
        super(options);
    }

    // Called by RecyclerView to display the data at the specified position
    @Override
    protected void onBindViewHolder(@NonNull patientAppointmentsHolder patientAppointmentsHolder, int position, @NonNull final AppointmentInformation apointementInformation) {
        // Set the appointment date and time
        patientAppointmentsHolder.dateAppointment.setText(apointementInformation.getTime());
        // Set the doctor's name
        patientAppointmentsHolder.patientName.setText(apointementInformation.getDoctorName());
        // Set the appointment type
        patientAppointmentsHolder.appointementType.setText(apointementInformation.getAppointmentType());
        // Set the appointment status
        patientAppointmentsHolder.type.setText(apointementInformation.getType());
        String doctorEmail = apointementInformation.getDoctorId();
        Log.d("doctor email", doctorEmail);
        // Get the doctor's phone number from Firestore
        docRef = db.collection("Doctor").document("" + doctorEmail + "");
        docRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            // Set the phone number in the TextView
            patientAppointmentsHolder.phone.setText(document.getString("tel"));
            Log.d("telephone num", document.getString("tel"));
        });

        // Load the doctor's profile image from Firebase Storage
        String imageId = apointementInformation.getDoctorId();
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/" + imageId + ".jpg");
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image into the ImageView using the Picasso library
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.logo)
                    .fit()
                    .centerCrop()
                    .into(patientAppointmentsHolder.image);
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });

        String appointmentType = apointementInformation.getAppointmentType();
        if (appointmentType != null && appointmentType.equals("Consultation")) {
            // Set the background color of the appointment type TextView to the primary color
            patientAppointmentsHolder.appointementType.setBackground(patientAppointmentsHolder.appointementType.getContext().getResources().getDrawable(R.drawable.button_radius_primary_color));
        }
        // Set the text color of the appointment status TextView based on the status
        if (apointementInformation.getType().equals("Accepted")) {
            patientAppointmentsHolder.type.setTextColor(Color.parseColor("#20bf6b"));
        } else if (apointementInformation.getType().equals("Checked")) {
            patientAppointmentsHolder.type.setTextColor(Color.parseColor("#8854d0"));
        } else {
            patientAppointmentsHolder.type.setTextColor(Color.parseColor("#eb3b5a"));
        }
    }

    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item
    @NonNull
    @Override
    public patientAppointmentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_item, parent, false);
        return new patientAppointmentsHolder(v);
    }

    // Define a ViewHolder class to hold references to the views in each item of the RecyclerView
    class patientAppointmentsHolder extends RecyclerView.ViewHolder{
        TextView dateAppointment;
        TextView patientName;
        TextView appointementType;
        TextView type;
        TextView phone;
        ImageView image;

        public patientAppointmentsHolder(@NonNull View itemView) {
            super(itemView);
            dateAppointment = itemView.findViewById(R.id.appointement_date);
            patientName = itemView.findViewById(R.id.patient_name);
            appointementType = itemView.findViewById(R.id.appointement_type);
            type = itemView.findViewById(R.id.type);
            phone = itemView.findViewById(R.id.patient_phone);
            image = itemView.findViewById(R.id.patient_image);
        }
    }
}
