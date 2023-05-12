package app.web.jkimtech.drpatientappointment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.controller.DoctorHomeActivity;
import app.web.jkimtech.drpatientappointment.controller.FirstTimeActivity;
import app.web.jkimtech.drpatientappointment.controller.HomeActivity;
import app.web.jkimtech.drpatientappointment.model.User;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 5000; // 5 seconds
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Delay for the specified duration and check the login status
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginStatus();
            }
        }, SPLASH_DURATION);
    }
    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser!=null){
            try {
                userRef.document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user=documentSnapshot.toObject(User.class);
                            if(user.getType().equals("Patient")){
                                Intent p = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(p);
                            }else{
                                Intent d = new Intent(SplashActivity.this, DoctorHomeActivity.class);
                                startActivity(d);
                                //Snackbar.make(findViewById(R.id.main_layout), "Doctor interface entraint de realisation", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent y = new Intent(SplashActivity.this, FirstTimeActivity.class);
                            startActivity(y);
                        }
                    }
                });
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
        else{
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    private void checkLoginStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // update the UI accordingly
        // use updateUI() method to update the UI
        updateUI(user);
    }
}