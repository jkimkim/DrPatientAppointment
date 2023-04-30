package app.web.jkimtech.drpatientappointment.model.firestoreApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.model.Doctor;

public class DoctorHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference DoctorRef = db.collection("Doctor");

    public static void addDoctor(String name, String address, String tel,String speciality){
        Doctor doctor = new Doctor(name,address,tel, FirebaseAuth.getInstance().getCurrentUser().getEmail(),speciality);

        DoctorRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(doctor);

    }
}
