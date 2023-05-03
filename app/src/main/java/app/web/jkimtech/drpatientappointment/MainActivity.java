package app.web.jkimtech.drpatientappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import app.web.jkimtech.drpatientappointment.controller.CalenderDoctorActivity;
import app.web.jkimtech.drpatientappointment.controller.DoctorAppointmentActivity;
import app.web.jkimtech.drpatientappointment.controller.DoctorHomeActivity;
import app.web.jkimtech.drpatientappointment.controller.DoctorProfileActivity;
import app.web.jkimtech.drpatientappointment.controller.FirstTimeActivity;
import app.web.jkimtech.drpatientappointment.controller.HomeActivity;
import app.web.jkimtech.drpatientappointment.model.User;

public class MainActivity extends AppCompatActivity {

    // this is the main activity that will be shown to the user when they first open the app
    private FirebaseAuth mAuth;
    private Button btnLogin;
    private Button btnSignUp;
    private EditText etEmail;
    private EditText etPassword;
    private EditText confirmPassword;
    private EditText etName;
    private EditText etAddress;
    private EditText etTel;
    private Button createAccount;
    private EditText confirm;
    SignInButton signInButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("User");

    // google sign in
    private static final int RC_SIGN_IN = 1;

    // google sign in client
    GoogleSignInClient googleSignInClient;

    public static void exitApp(DoctorHomeActivity doctorHomeActivity) {
        // this method will be called when the user clicks the exit button
        // it will create a dialog to ask if the user wants to exit the app
        // if yes then the app will exit
        // if no then the dialog will be dismissed
        new android.app.AlertDialog.Builder(doctorHomeActivity)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        doctorHomeActivity.finishAffinity();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public static void goToProfile(DoctorHomeActivity doctorHomeActivity) {
        // this method will be called when the user clicks the profile button
        // it will take the user to the profile activity
        Intent intent = new Intent(doctorHomeActivity, DoctorProfileActivity.class);
        doctorHomeActivity.startActivity(intent);
    }

    public static void goToRequest(DoctorHomeActivity doctorHomeActivity) {
        // this method will be called when the user clicks the request button
        // it will take the user to the DoctorAppointmentActivity activity
        Intent intent = new Intent(doctorHomeActivity, DoctorAppointmentActivity.class);
        doctorHomeActivity.startActivity(intent);
    }

    public static void goToMyCalendar(DoctorHomeActivity doctorHomeActivity) {
        // this method will be called when the user clicks the calendar button
        // it will take the user to the CalenderDoctorActivity
        Intent intent = new Intent(doctorHomeActivity, CalenderDoctorActivity.class);
        doctorHomeActivity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        confirm = findViewById(R.id.editText3);
        confirm.setVisibility(confirm.INVISIBLE);
        signInButton = findViewById(R.id.sign_in_button);

        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText(R.string.google_signin_txt);
        etEmail = findViewById(R.id.editText2);
        etPassword = findViewById(R.id.editText);
        confirmPassword = findViewById(R.id.editText3);
        confirmPassword.setVisibility(confirmPassword.INVISIBLE);
        btnSignUp = findViewById(R.id.SignUpBtn);
        btnLogin = findViewById(R.id.LoginBtn);
        createAccount = findViewById(R.id.CreateAccount);

        btnSignUp.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty() && !confirmPass.isEmpty()) {
                    if (password.equals(confirmPass)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        android.util.Log.d("TAG", "createUserWithEmail:success");
                                        android.widget.Toast.makeText(MainActivity.this, "Account Created", android.widget.Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        android.util.Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        android.widget.Toast.makeText(MainActivity.this, "Authentication failed.",
                                                android.widget.Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                });
                    }else {
                        android.widget.Toast.makeText(MainActivity.this, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }else {
                    android.widget.Toast.makeText(MainActivity.this, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
        // login button
        btnLogin.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    android.util.Log.d("TAG", "signInWithEmail:success");
                                    android.widget.Toast.makeText(MainActivity.this, "Login Successful", android.widget.Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    android.util.Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    android.widget.Toast.makeText(MainActivity.this, "Authentication failed.",
                                            android.widget.Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            });
                }else {
                    android.widget.Toast.makeText(MainActivity.this, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
        //create account button
        createAccount.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                etEmail.setText("");
                etPassword.setText("");
                if (confirm.getVisibility() == confirm.INVISIBLE){
                    confirmPassword.setVisibility(confirmPassword.VISIBLE);
                    btnSignUp.setVisibility(btnSignUp.VISIBLE);
                    btnLogin.setVisibility(btnLogin.INVISIBLE);
                    createAccount.setText(R.string.signin_btn_clicked);
                    confirm.setVisibility(confirm.VISIBLE);
                }else {
                    confirmPassword.setVisibility(confirmPassword.INVISIBLE);
                    btnSignUp.setVisibility(btnSignUp.INVISIBLE);
                    btnLogin.setVisibility(btnLogin.VISIBLE);
                    createAccount.setText(R.string.create_account_unclicked);
                    confirm.setVisibility(confirm.INVISIBLE);
                }
            }
        });
        // google sign in button
        findViewById(R.id.sign_in_button).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }
    //google sign in API
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ..
                    }
                });
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
                                Intent p = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(p);
                            }else{
                                Intent d = new Intent(MainActivity.this, DoctorHomeActivity.class);
                                startActivity(d);
                                //Snackbar.make(findViewById(R.id.main_layout), "Doctor interface entraint de realisation", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent y = new Intent(MainActivity.this, FirstTimeActivity.class);
                            startActivity(y);
                        }
                    }
                });
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    }
    //on start check if user is logged in
    //create a loading dialog while checking
    @Override
    public void onStart() {
        super.onStart();
        android.app.ProgressDialog dialog = new android.app.ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        dialog.dismiss();
    }
}