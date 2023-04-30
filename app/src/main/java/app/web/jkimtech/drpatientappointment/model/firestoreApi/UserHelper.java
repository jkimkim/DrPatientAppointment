package app.web.jkimtech.drpatientappointment.model.firestoreApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.model.User;

public class UserHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference UsersRef = db.collection("User");

    public static void addUser(String name, String address, String tel,String type){
        User user = new User(name,address,tel, FirebaseAuth.getInstance().getCurrentUser().getEmail(),type);
        UsersRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(user);

    }
}
