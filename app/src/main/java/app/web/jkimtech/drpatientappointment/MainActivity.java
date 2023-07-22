package app.web.jkimtech.drpatientappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import app.web.jkimtech.drpatientappointment.controller.CalenderDoctorActivity;
import app.web.jkimtech.drpatientappointment.controller.ConfirmedRequestsActivity;
import app.web.jkimtech.drpatientappointment.controller.DoctorAppointmentActivity;
import app.web.jkimtech.drpatientappointment.controller.DoctorHomeActivity;
import app.web.jkimtech.drpatientappointment.controller.DoctorProfileActivity;
import app.web.jkimtech.drpatientappointment.controller.FirstTimeActivity;
import app.web.jkimtech.drpatientappointment.controller.HomeActivity;
import app.web.jkimtech.drpatientappointment.controller.MedicalFolderActivity;
import app.web.jkimtech.drpatientappointment.controller.MyDoctorsActivity;
import app.web.jkimtech.drpatientappointment.controller.MyPatientsActivity;
import app.web.jkimtech.drpatientappointment.controller.PatientAppointmentActivity;
import app.web.jkimtech.drpatientappointment.controller.PatientProfileActivity;
import app.web.jkimtech.drpatientappointment.controller.SearchActivity;
import app.web.jkimtech.drpatientappointment.controller.TermsAndConditionsActivity;
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
    private Button ForgotPass;

    private TextView txtConditions;

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

    public static void goToListPatients(DoctorHomeActivity doctorHomeActivity) {
        // this method will be called when the user clicks the list patients button
        // it will take the user to MyPatientsActivity
        Intent intent = new Intent(doctorHomeActivity, MyPatientsActivity.class);
        doctorHomeActivity.startActivity(intent);
    }

    public static void goToAppointment(DoctorHomeActivity doctorHomeActivity) {
        // this method will be called when the user clicks the appointment button
        // it will take the user to the ConfirmedRequests activity
        Intent intent = new Intent(doctorHomeActivity, ConfirmedRequestsActivity.class);
        doctorHomeActivity.startActivity(intent);
    }

    public static void signOut(DoctorHomeActivity doctorHomeActivity) {
        // this method will be called when the user clicks the sign out button
        // it will sign out the user and take them back to the main activity
        // it will also clear the activity stack so that the user cannot go back to the previous activity
        // without signing in again
        // it will create a dialog to ask if the user wants to sign out and if yes then the user will be signed out
        // if no then the dialog will be dismissed
        // a progress dialog will be shown while the user is being signed out
        ProgressDialog progressDialog = new ProgressDialog(doctorHomeActivity);
        progressDialog.setMessage("Signing Out...");
        progressDialog.show();
        new android.app.AlertDialog.Builder(doctorHomeActivity)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        progressDialog.dismiss();
                        Intent intent = new Intent(doctorHomeActivity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        doctorHomeActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
        progressDialog.dismiss();
    }

    public static void exitPatientApp(HomeActivity homeActivity) {
        // this method will be called when the user clicks the exit button
        // it will create a dialog to ask if the user wants to exit the app
        // if yes then the app will exit
        // if no then the dialog will be dismissed
        new android.app.AlertDialog.Builder(homeActivity)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        homeActivity.finishAffinity();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public static void goToSearch(HomeActivity homeActivity) {
        // this method will be called when the user clicks the search button
        // it will take the user to the SearchActivity activity
        Intent intent = new Intent(homeActivity, SearchActivity.class);
        homeActivity.startActivity(intent);
    }

    public static void goToMyDoctors(HomeActivity homeActivity) {
        // this method will be called when the user clicks the my doctors button
        // it will take the user to the MyDoctorsActivity activity
        Intent intent = new Intent(homeActivity, MyDoctorsActivity.class);
        homeActivity.startActivity(intent);
    }

    public static void goToMedicalFolder(HomeActivity homeActivity) {
        // this method will be called when the user clicks the medical folder button
        // it will take the user to the MedicalFolderActivity activity
        Intent intent = new Intent(homeActivity, MedicalFolderActivity.class);
        intent.putExtra("patient_email", FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        homeActivity.startActivity(intent);
    }

    public static void goToPatientProfile(HomeActivity homeActivity) {
        // this method will be called when the user clicks the profile button
        // it will take the user to the PatientProfileActivity activity
        Intent intent = new Intent(homeActivity, PatientProfileActivity.class);
        homeActivity.startActivity(intent);
    }

    public static void goToPatientAppointment(HomeActivity homeActivity) {
        // this method will be called when the user clicks the appointment button
        // it will take the user to the PatientAppointmentActivity activity
        Intent intent = new Intent(homeActivity, PatientAppointmentActivity.class);
        homeActivity.startActivity(intent);
    }

    public static void signOutPatient(HomeActivity homeActivity) {
        // this method will be called when the user clicks the sign out button
        // it will sign out the user and take them back to the main activity
        // it will also clear the activity stack so that the user cannot go back to the previous activity
        // it will create a dialog to ask if the user wants to sign out and if yes then the user will be signed out
        // if no then the dialog will be dismissed
        // a progress dialog will be shown while the user is being signed out

        ProgressDialog progressDialog = new ProgressDialog(homeActivity);
        progressDialog.setMessage("Signing Out...");
        progressDialog.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(homeActivity)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        progressDialog.dismiss();
                        Intent intent = new Intent(homeActivity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        homeActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("No", null);

        // set the "Yes" button color to red
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });

        // set the "No" button color to green
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
            }
        });

        dialog.show();
        progressDialog.dismiss();
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
        ForgotPass = findViewById(R.id.ForgotPass);
ForgotPass.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                showRecoverPasswordDialog();
            }
        });

        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText(R.string.google_signin_txt);
        etEmail = findViewById(R.id.editText2);
        etPassword = findViewById(R.id.editText);
        confirmPassword = findViewById(R.id.editText3);
        confirmPassword.setVisibility(confirmPassword.INVISIBLE);
        btnSignUp = findViewById(R.id.SignUpBtn);
        btnLogin = findViewById(R.id.loginBtn);
        createAccount = findViewById(R.id.CreateAccount);

        txtConditions = findViewById(R.id.txtConditions);
        txtConditions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TermsAndConditionsActivity.class);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPass = confirmPassword.getText().toString();
            if (!email.isEmpty() && !password.isEmpty() && !confirmPass.isEmpty()) {
                if (password.equals(confirmPass)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    progressDialog.dismiss();
                                    Log.d("TAG", "createUserWithEmail:success");
                                    android.widget.Toast.makeText(MainActivity.this, "Account Created", android.widget.Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressDialog.dismiss();
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    android.widget.Toast.makeText(MainActivity.this, "Authentication failed.",
                                            android.widget.Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            });
                }else {
                    progressDialog.dismiss();
                    android.widget.Toast.makeText(MainActivity.this, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show();
                }
            }else {
                progressDialog.dismiss();
                android.widget.Toast.makeText(MainActivity.this, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        // login button
        btnLogin.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Logging In...");
                progressDialog.show();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    android.util.Log.d("TAG", "signInWithEmail:success");
                                    progressDialog.dismiss();
                                    android.widget.Toast.makeText(MainActivity.this, "Login Successful", android.widget.Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressDialog.dismiss();
                                    android.util.Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    android.widget.Toast.makeText(MainActivity.this, "Authentication failed.",
                                            android.widget.Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            });
                }else {
                    progressDialog.dismiss();
                    android.widget.Toast.makeText(MainActivity.this, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
        //create account button
        createAccount.setOnClickListener(v -> {
            etEmail.setText("");
            etPassword.setText("");
            if (confirm.getVisibility() == confirm.INVISIBLE){
                confirmPassword.setVisibility(confirmPassword.VISIBLE);
                btnSignUp.setVisibility(btnSignUp.VISIBLE);
                btnLogin.setVisibility(btnLogin.INVISIBLE);
                createAccount.setText(R.string.signin_btn_clicked);
                confirm.setVisibility(confirm.VISIBLE);
                ForgotPass.setVisibility(ForgotPass.INVISIBLE);
            }else {
                confirmPassword.setVisibility(confirmPassword.INVISIBLE);
                btnSignUp.setVisibility(btnSignUp.INVISIBLE);
                btnLogin.setVisibility(btnLogin.VISIBLE);
                createAccount.setText(R.string.create_account_unclicked);
                confirm.setVisibility(confirm.INVISIBLE);
                ForgotPass.setVisibility(ForgotPass.VISIBLE);
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

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                progressDialog.dismiss();
                android.widget.Toast.makeText(MainActivity.this, "Email sent", android.widget.Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.dismiss();
                android.widget.Toast.makeText(MainActivity.this, "Failed...", android.widget.Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            android.widget.Toast.makeText(MainActivity.this, ""+e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
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
        // log in with google
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        // show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        // get google id token
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // sign in with credential
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // sign in success, dismiss dialog and start register activity
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        // if user is signing in for the first time then get and show user info from google account
                        if (task.getResult().getAdditionalUserInfo().isNewUser()){
                            // get user email and uid from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            // when user is registered store user info in firebase realtime database too
                            // using hashmap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            // put info in hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", ""); // will add later (e.g. edit profile)
                            hashMap.put("phone", "");
                            hashMap.put("image", "");
                            hashMap.put("cover", "");
                            hashMap.put("type", "User");
                            // firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            // path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            // put data within hashmap in database
                            reference.child(uid).setValue(hashMap);
                        }
                        // show user email in toast
                        android.widget.Toast.makeText(MainActivity.this, ""+user.getEmail(), android.widget.Toast.LENGTH_SHORT).show();
                        // go to profile activity after logged in
                        updateUI(user);
                    } else {
                        // if sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        // show error message
                        android.widget.Toast.makeText(MainActivity.this, "Authentication failed...", android.widget.Toast.LENGTH_SHORT).show();
                        // update UI
                        updateUI(null);
                    }

                    // ...
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // exit app on back pressed
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}

// Path: app\src\main\java\app\web\jkimtech\drpatientappointment\controller\EditProfilePatientActivity.java
// NOTE: this project uses Deprecated Gradle features it is therefore incompatible with Gradle 8.0!
// NOTE: this file uses the same methods as ProfileDoctorActivity.java to edit the profile and upload the image because the process is the same
// NOTE: this file uses the same xml file as ProfileDoctorActivity.java to display the patient profile.
// path: app\src\main\res\layout\activity_doctor_profile.xml
// path: app\src\main\res\layout\activity_edit_profile_patient.xml
// NOTE: this is a modified version of ProfilePatientActivity.java from DaktarLagbe project with some to a lot of modification and so are most of the files in this project
// NOTE: however the design of the app is mostly the same!
// NOTE: this file and others are modified to fit the purpose of this project
// NOTE: this file is used to edit the patient profile
// NOTE: before making any changes to this project, please connect with your firebase account and change the google-services.json file in the app folder
// NOTE: this project is connected to firebase firestore and firebase storage
// NOTE: this project is connected to firebase authentication with email and password
// NOTE: when editing any file, please make sure to change the package name to your own package name.

// TODO: Add terms and conditions