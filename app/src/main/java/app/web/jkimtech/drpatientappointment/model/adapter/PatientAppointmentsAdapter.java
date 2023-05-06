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
    StorageReference pathReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    DocumentSnapshot documentSnapshot;
    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

    public PatientAppointmentsAdapter(@NonNull FirestoreRecyclerOptions<AppointmentInformation> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull patientAppointmentsHolder patientAppointmentsHolder, int position, @NonNull final AppointmentInformation apointementInformation) {
        patientAppointmentsHolder.dateAppointment.setText(apointementInformation.getTime());
        patientAppointmentsHolder.patientName.setText(apointementInformation.getDoctorName());
        patientAppointmentsHolder.appointementType.setText(apointementInformation.getAppointmentType());
        patientAppointmentsHolder.type.setText(apointementInformation.getType());
        String doctorEmail = apointementInformation.getDoctorId();
        Log.d("docotr email", doctorEmail);
        docRef = db.collection("Doctor").document("" + doctorEmail + "");
        /* Get the doctor's phone number */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                patientAppointmentsHolder.phone.setText(document.getString("tel"));
                Log.d("telephone num", document.getString("tel"));
            }
        });


        //display profile image
        String imageId = apointementInformation.getDoctorId();
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/" + imageId + ".jpg");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.logo)
                        .fit()
                        .centerCrop()
                        .into(patientAppointmentsHolder.image);
                // profileImage.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        if (apointementInformation.getAppointmentType().equals("Consultation")) {
            //patientAppointmentsHolder.appointementType.setBackgroundColor((patientAppointmentsHolder.type.getContext().getResources().getColor(R.color.colorPrimaryDark)));
            patientAppointmentsHolder.appointementType.setBackground(patientAppointmentsHolder.appointementType.getContext().getResources().getDrawable(R.drawable.button_radius_primary_color));
        }
        if (apointementInformation.getType().equals("Accepted")) {
            patientAppointmentsHolder.type.setTextColor(Color.parseColor("#20bf6b"));
        } else if (apointementInformation.getType().equals("Checked")) {
            patientAppointmentsHolder.type.setTextColor(Color.parseColor("#8854d0"));
        } else {
            patientAppointmentsHolder.type.setTextColor(Color.parseColor("#eb3b5a"));
        }
    }

    @NonNull
    @Override
    public patientAppointmentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_item, parent, false);
        return new patientAppointmentsHolder(v);
    }


    class patientAppointmentsHolder extends RecyclerView.ViewHolder {
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
