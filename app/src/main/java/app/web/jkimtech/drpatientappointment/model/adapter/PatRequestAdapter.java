package app.web.jkimtech.drpatientappointment.model.adapter;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.Doctor;
import app.web.jkimtech.drpatientappointment.model.Patient;
import app.web.jkimtech.drpatientappointment.model.Request;

// Define a class named PatRequestAdapter that extends FirestoreRecyclerAdapter
// and takes Request objects as input
public class PatRequestAdapter extends FirestoreRecyclerAdapter<Request, PatRequestAdapter.PatRequesteHolder> {

    // Create a new instance of FirebaseFirestore
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Create a CollectionReference to the "Request" collection in the database
    static CollectionReference addRequest = db.collection("Request");

    // Constructor for PatRequestAdapter that takes in FirestoreRecyclerOptions<Request> options
    public PatRequestAdapter(@NonNull FirestoreRecyclerOptions<Request> options) {
        // Call the constructor of the superclass and pass in the options
        super(options);
    }

    // Override the onBindViewHolder method to set the data for each item in the RecyclerView
    @Override
    protected void onBindViewHolder(@NonNull final PatRequesteHolder RequestHolder, final int i, @NonNull final Request request) {

        // Get the TextView and other data for the current Request
        final TextView t = RequestHolder.title;
        final String idPat = request.getId_patient();
        final String idDoc = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        final String HourPath = request.getHour_path();

        // Retrieve the Doctor object associated with the current Request from the database
        db.collection("Doctor").document(idDoc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                // Convert the retrieved document to a Doctor object
                final Doctor onlineDoc = documentSnapshot.toObject(Doctor.class);

                // Retrieve the Patient object associated with the current Request from the database
                db.collection("Patient").document(idPat).get().addOnSuccessListener(documentSnapshot12 -> {

                    // Convert the retrieved document to a Patient object
                    final Patient pat= documentSnapshot12.toObject(Patient.class);

                    // Set the title and description of the current Request item in the RecyclerView
                    RequestHolder.title.setText(pat.getName());
                    RequestHolder.specialite.setText(R.string.want_to_be_your_patient);

                    // Set an onClickListener for the "Add" button to add the current Patient to the current Doctor's list of patients
                    RequestHolder.addDoc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                            // Add the current Doctor to the current Patient's list of doctors in the database
                            db.collection("Patient").document(idPat).collection("MyDoctors").document(idDoc).set(onlineDoc);

                            // Add the current Patient to the current Doctor's list of patients in the database
                            db.collection("Doctor").document(idDoc+"").collection("MyPatients").document(idPat).set(pat);

                            // Delete the current Request from the "Request" collection in the database
                            addRequest.whereEqualTo("id_doctor",idDoc+"").whereEqualTo("id_patient",idPat+"").get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots){
                                    addRequest.document(documentSnapshot1.getId()).delete();
                                }
                            });

                            // Update the "choosen" field of the current appointment in the database to indicate that it has been chosen
                            db.document(HourPath).update("choosen","true");

                            // Display a message indicating that the patient has been added and hide the "Add" button
                            Snackbar.make(t, "Patient added", Snackbar.LENGTH_SHORT).show();
                            RequestHolder.addDoc.setVisibility(View.INVISIBLE);
                        }
                    });
                });
            }
        });
    }

    // Override the deleteItem method to delete the current Request from the database
    public void deleteItem(int position) {
        // Get the hour_path of the current Request
        String hour =getSnapshots().getSnapshot(position).getString("hour_path");
        // Delete the current Request from the "Request" collection in the database
        db.document(hour).delete();
        // Delete the current Request from the RecyclerView
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    // Override the onCreateViewHolder method to inflate the layout for each item in the RecyclerView
    @NonNull
    @Override
    public PatRequesteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pat_request_item,
                parent, false);
        return new PatRequesteHolder(v);
    }

    // Define a class named PatRequesteHolder that extends RecyclerView.ViewHolder
    class PatRequesteHolder extends RecyclerView.ViewHolder {

        // Define the TextView and ImageView for the current Request item in the RecyclerView
        TextView title;
        TextView specialite;
        ImageView image;
        Button addDoc;
        // Define the constructor for PatRequesteHolder that takes in a View called itemView
        public PatRequesteHolder(@NonNull View itemView) {
            super(itemView);
            addDoc = itemView.findViewById(R.id.pat_request_accept_btn);
            title= itemView.findViewById(R.id.pat_request_title);
            specialite=itemView.findViewById(R.id.pat_request_description);
            image=itemView.findViewById(R.id.pat_request_item_image);

        }
    }
}
