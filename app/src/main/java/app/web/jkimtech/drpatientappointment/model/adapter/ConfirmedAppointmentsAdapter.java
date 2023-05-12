package app.web.jkimtech.drpatientappointment.model.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.ApointementInformation;

public class ConfirmedAppointmentsAdapter extends FirestoreRecyclerAdapter<ApointementInformation, ConfirmedAppointmentsAdapter.ConfirmedAppointmentsHolder> {
    StorageReference pathReference ;
    public ConfirmedAppointmentsAdapter(@NonNull FirestoreRecyclerOptions<ApointementInformation> options) {
        super(options);
    }

    @Override
    // Bind the data to the view
    protected void onBindViewHolder(@NonNull ConfirmedAppointmentsHolder confirmedAppointmentsHolder, int position, @NonNull final ApointementInformation apointementInformation) {
        confirmedAppointmentsHolder.dateAppointment.setText(apointementInformation.getTime());
        confirmedAppointmentsHolder.patientName.setText(apointementInformation.getPatientName());
        confirmedAppointmentsHolder.appointmentType.setText(apointementInformation.getAppointmentType());

        // Get the image from the database
        String imageId = apointementInformation.getPatientId()+".jpg"; //add a title image
        pathReference = FirebaseStorage.getInstance().getReference().child("PatientProfile/"+ imageId); //storage the image
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.logo)
                    .fit()
                    .centerCrop()
                    .into(confirmedAppointmentsHolder.patientImage);//Image location

            // profileImage.setImageURI(uri);
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });

    }

    @NonNull
    @Override
    // Create the view
    public ConfirmedAppointmentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirmed_appointments_item, parent, false);
        return new ConfirmedAppointmentsHolder(v);
    }

    // Create the holder
    class ConfirmedAppointmentsHolder extends RecyclerView.ViewHolder{
        TextView dateAppointment;
        TextView patientName;
        TextView appointmentType;
        ImageView patientImage;
        public ConfirmedAppointmentsHolder(@NonNull View itemView) {
            super(itemView);
            dateAppointment = itemView.findViewById(R.id.appointment_date);
            patientName = itemView.findViewById(R.id.patient_name);
            appointmentType = itemView.findViewById(R.id.appointment_type);
            patientImage = itemView.findViewById(R.id.patient_image);
        }
    }
}
