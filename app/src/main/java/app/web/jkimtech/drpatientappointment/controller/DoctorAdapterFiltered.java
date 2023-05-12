package app.web.jkimtech.drpatientappointment.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Common.Common;
import app.web.jkimtech.drpatientappointment.model.Doctor;

public class DoctorAdapterFiltered extends RecyclerView.Adapter<DoctorAdapterFiltered.DoctorHolder2> implements Filterable {

    // Flag to indicate if a speciality search is being performed
    public static boolean specialitySearch = false;

    // String to hold the email address of a doctor
    static String doc;

    // Instance of FirebaseFirestore to interact with the Firestore database
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Collection reference for the "Request" collection in Firestore
    static CollectionReference addRequest = db.collection("Request");

    // List of Doctor objects to be displayed in the RecyclerView
    private List<Doctor> mTubeList;

    // List of Doctor objects to be filtered based on search input
    private List<Doctor> mTubeListFiltered;

    // Storage reference for images of doctors
    StorageReference pathReference ;

    // Constructor to initialize the adapter with a list of Doctor objects
    public DoctorAdapterFiltered(List<Doctor> tubeList){
        mTubeList = tubeList;
        mTubeListFiltered = tubeList;
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    @NonNull
    @Override
    public DoctorHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the layout for the item view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item, parent, false);

        // Return a new instance of DoctorHolder2 with the inflated view
        return new DoctorHolder2(v);
    }

    // Called by RecyclerView to display the data at the specified position
    @Override
    public void onBindViewHolder(@NonNull DoctorHolder2 doctoreHolder, int i) {

        // Get the Doctor object at the current position
        final Doctor doctor = mTubeListFiltered.get(i);

        // Set the title TextView to the doctor's name
        final TextView t = doctoreHolder.title ;
        doctoreHolder.title.setText(doctor.getName());

        // Set the image for the doctor using Picasso library
        String imageId = doctor.getEmail()+".jpg";
        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/"+ imageId);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.logo)
                        .fit()
                        .centerCrop()
                        .into(doctoreHolder.image);//Image location
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // Set the speciality TextView to the doctor's speciality
        doctoreHolder.speciality.setText(String.format("Speciality : %s", doctor.getSpeciality()));

        // Get the email address of the current user (patient)
        final String idPat = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

        // Get the email address of the current doctor
        final String idDoc = doctor.getEmail();

        // Set an OnClickListener for the "Add" button to add a doctor to the patient's list of doctors
        doctoreHolder.addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create a new note to store the patient and doctor email addresses
                Map<String, Object> note = new HashMap<>();
                note.put("id_patient", idPat);
                note.put("id_doctor", idDoc);

                // Add the note to the "Request" collection in Firestore
                addRequest.document().set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            // If the note is added successfully, display a Snackbar message
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(t, "Request sent!", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                // Hide the "Add" button
                doctoreHolder.addDoc.setVisibility(View.INVISIBLE);
            }
        });
        // Set an OnClickListener for the "Appointment" button to open the AppointmentActivity
        // When the button is clicked, store the doctor's email address in the Common class
        doctoreHolder.appointmentBtn.setOnClickListener(v -> {
            // Get the doctor's email address
            doc= doctor.getEmail();
            Common.CurrentDoctor = doctor.getEmail();
            Common.CurrentDoctorName = doctor.getName();
            Common.CurrentPhone = doctor.getTel();
            openPage(v.getContext());
        });
    }
    @Override
    public int getItemCount() {
        // Return the size of the list
        return mTubeListFiltered.size();
    }
    @Override
    // Returns a filter that can be used to constrain data with a filtering pattern
    public Filter getFilter() {
        // Return a new instance of the Filter class
        return new Filter() {
            // Invoked in a worker thread to filter the data according to the constraint
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Create a new List to store the filtered data
                String pattern = constraint.toString().toLowerCase();
                if(pattern.isEmpty()){
                    mTubeListFiltered = mTubeList;
                } else {
                    List<Doctor> filteredList = new ArrayList<>();
                    for(Doctor tube: mTubeList){
                        // Filter doctor names and specialities based on the search input
                        if(specialitySearch == false) {
                            if (tube.getName().toLowerCase().contains(pattern) || tube.getName().toLowerCase().contains(pattern)) {
                                filteredList.add(tube);
                            }
                        }
                        else{
                            if (tube.getSpeciality().toLowerCase().contains(pattern) || tube.getSpeciality().toLowerCase().contains(pattern)) {
                                filteredList.add(tube);
                            }
                        }
                    }
                    mTubeListFiltered = filteredList;
                }
                // Create a new FilterResults object to return the filtered data
                FilterResults filterResults = new FilterResults();
                filterResults.values = mTubeListFiltered;
                return filterResults;
            }
            // Invoked in the UI thread to publish the filtering results in the user interface
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTubeListFiltered = (ArrayList<Doctor>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    // A ViewHolder describes an item view and metadata about its place within the RecyclerView
    class DoctorHolder2 extends RecyclerView.ViewHolder {
        // Member variables for the holder data
        Button appointmentBtn;
        TextView title;
        TextView speciality;
        ImageView image;
        Button addDoc;
        Button load;
        // Constructor for the DoctorHolder2 class
        public DoctorHolder2(@NonNull View itemView) {
            // Initialize the views
            super(itemView);
            addDoc = itemView.findViewById(R.id.addDocBtn);
            title= itemView.findViewById(R.id.doctor_view_title);
            speciality =itemView.findViewById(R.id.text_view_description);
            image=itemView.findViewById(R.id.doctor_item_image);
            appointmentBtn =itemView.findViewById(R.id.appointmentBtn);
        }
    }
    // Open the TestActivity
    private void openPage(Context wf){
        // Create an Intent to start the TestActivity
        Intent i = new Intent(wf, TestActivity.class);
        wf.startActivity(i);
    }
}
