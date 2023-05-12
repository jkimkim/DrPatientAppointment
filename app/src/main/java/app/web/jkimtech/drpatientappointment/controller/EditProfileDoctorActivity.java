package app.web.jkimtech.drpatientappointment.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import app.web.jkimtech.drpatientappointment.R;
import app.web.jkimtech.drpatientappointment.model.UploadImage;

public class EditProfileDoctorActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditProfileDoctorActivity";
    private ImageView profileImage;
    private ImageButton selectImage;
    private Button updateProfile;
    private TextInputEditText doctorName;
    private TextInputEditText doctorEmail;
    private TextInputEditText doctorPhone;
    private TextInputEditText doctorAddress;
    private TextInputEditText doctorAbout;
    final String currentDoctorUID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
    private Uri uriImage;
    private StorageReference pStorageRef;
    private DatabaseReference pDatabaseRef;
    private FirebaseFirestore doctorRef;
    private StorageReference pathReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private DatabaseReference currentUserImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_doctor);
        doctorRef = FirebaseFirestore.getInstance();
        profileImage = findViewById(R.id.image_profile);
        selectImage = findViewById(R.id.select_image);
        updateProfile = findViewById(R.id.update);
        doctorName = findViewById(R.id.nameText);
        doctorPhone = findViewById(R.id.phoneText);
        doctorAddress = findViewById(R.id.addressText);
        pStorageRef = FirebaseStorage.getInstance().getReference("DoctorProfile");
        pDatabaseRef = FirebaseDatabase.getInstance().getReference("DoctorProfile");
        doctorAbout = findViewById(R.id.about_meText);
        //get the default doctor's information from ProfileDoctorActivity
        Intent intent = getIntent(); //get the current intent
        String current_name = intent.getStringExtra("CURRENT_NAME");
        String current_phone = intent.getStringExtra("CURRENT_PHONE");
        String current_address = intent.getStringExtra("CURRENT_ADDRESS");
        String current_about = intent.getStringExtra("CURRENT_ABOUT");
        //Set the default information in the text fields
        doctorName.setText(current_name);
        doctorPhone.setText(current_phone);
        doctorAddress.setText(current_address);
        doctorAbout.setText(current_about);
        //Set the default image
        String userPhotoPath = currentDoctorUID + ".jpg";
        pathReference = storageRef.child("DoctorProfile/" + userPhotoPath); //Doctor photo in database
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.doctor)
                        .fit()
                        .centerCrop()
                        .into(profileImage);//Store here the imageView
                // profileImage.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        //Select image from gallery
        selectImage.setOnClickListener(view -> openFileChooser());
        updateProfile.setOnClickListener(view -> {
            String updateAddress = doctorAddress.getText().toString();
            String updateName = doctorName.getText().toString();
            //String updateEmail = doctorEmail.getText().toString();
            String updatePhone = doctorPhone.getText().toString();
            String updateAbout = doctorAbout.getText().toString();
            uploadProfileImage();
            updateDoctorInfo(updateName, updateAddress, updatePhone, updateAbout);
        });
    }
    // update the doctor's information in the database
    private void updateDoctorInfo(String name, String address, String phone, String updateAbout)
    {
        DocumentReference doctorReference = doctorRef.collection("Doctor").document("" + doctorID + "");
        doctorReference.update("name", name);
        doctorReference.update("address", address);
        doctorReference.update("about", updateAbout);
        doctorReference.update("phone", phone)
                .addOnSuccessListener(
                        aVoid -> {
                            Toast.makeText(EditProfileDoctorActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                            // wait for all the information and image to be updated
                            // show the updated information in the ProfileDoctorActivity
                            Intent intent = new Intent(EditProfileDoctorActivity.this, DoctorProfileActivity.class);
                            intent.putExtra("UPDATED_NAME", name);
                            intent.putExtra("UPDATED_ADDRESS", address);
                            intent.putExtra("UPDATED_PHONE", phone);
                            intent.putExtra("UPDATED_ABOUT", updateAbout);
                            startActivity(intent);
                        }
                ).addOnFailureListener(
                        e -> Toast.makeText(EditProfileDoctorActivity.this, "Profile Update Failed", Toast.LENGTH_LONG).show()
                );
    }
    // open the file chooser to select the image
    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                intent,
                PICK_IMAGE_REQUEST
        );
    }
    // get the image from the file chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //check if the request code is the same as the one we passed
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            uriImage = data.getData();
            Picasso.get()
                    .load(uriImage)
                    .placeholder(R.drawable.doctor)
                    .fit()
                    .centerCrop()
                    .into(profileImage);//Store here the imageView
            //profileImage.setImageURI(uriImage);
        }
    }
    // get the image extension
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(
                contentResolver.getType(uri)
        );
    }
    // upload the image to the database
    // show a progress bar while the image is uploading to the database
    // go to the ProfileDoctorActivity after the image is uploaded
    // show the updated information in the ProfileDoctorActivity
    private void uploadProfileImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        // Set the progress dialog box title and message
        // and show the progress dialog box and show the percentage of the image upload progress
        // Check if the selected image is not null
        if (uriImage != null) {
            // Create a reference to the storage location where the image will be uploaded
            // The location is specified using the currentDoctorUID and the file extension of the image
            StorageReference storageReference = pStorageRef.child(currentDoctorUID + "." + getFileExtension(uriImage));

            // Upload the image to the storage location
            // Use the 'continueWithTask' method to chain a continuation task that retrieves the download URL
            storageReference.putFile(uriImage).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    // If the upload task was not successful, throw an exception to stop the continuation
                    throw task.getException();
                }
                // If the upload task was successful, return a task that retrieves the download URL
                return pStorageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // If the download URL retrieval task was successful, get the download URL from the result
                    Uri downloadUri = task.getResult();
                    Log.e(TAG, "then: " + downloadUri.toString());

                    // Create a new UploadImage object with the currentDoctorUID and the download URL
                    UploadImage upload = new UploadImage(currentDoctorUID, downloadUri.toString());

                    // Upload the UploadImage object to the database
                    pDatabaseRef.push().setValue(upload);
                    // wait for all the information and image to be updated
                    // show the updated information in the ProfileDoctorActivity
                    Intent intent = new Intent(EditProfileDoctorActivity.this, DoctorProfileActivity.class);
                    intent.putExtra("UPDATED_NAME", doctorName.getText().toString());
                    intent.putExtra("UPDATED_ADDRESS", doctorAddress.getText().toString());
                    intent.putExtra("UPDATED_PHONE", doctorPhone.getText().toString());
                    intent.putExtra("UPDATED_ABOUT", doctorAbout.getText().toString());
                    startActivity(intent);
                    // Dismiss the progress dialog box
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    // If the download URL retrieval task was not successful, show an error message
                }
            });
        }
    }
}