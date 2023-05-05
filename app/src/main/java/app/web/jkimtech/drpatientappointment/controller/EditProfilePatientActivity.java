package app.web.jkimtech.drpatientappointment.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class EditProfilePatientActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditProfileDoctorActivity";
    private ImageView profileImage;
    private ImageButton selectImage;
    private Button updateProfile;
    private TextInputEditText doctorName;
    private TextInputEditText doctorEmail;
    private TextInputEditText doctorPhone;
    private TextInputEditText doctorAddress;
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
        setContentView(R.layout.activity_edit_profile_patient);
        doctorRef = FirebaseFirestore.getInstance();
        profileImage = findViewById(R.id.image_profile);
        selectImage = findViewById(R.id.select_image);
        updateProfile = findViewById(R.id.update);
        doctorName = findViewById(R.id.nameText);
        doctorPhone = findViewById(R.id.phoneText);
        doctorAddress = findViewById(R.id.addressText);
        pStorageRef = FirebaseStorage.getInstance().getReference("DoctorProfile");
        pDatabaseRef = FirebaseDatabase.getInstance().getReference("DoctorProfile");
        //get the default doctor's information from ProfileDoctorActivity
        Intent intent = getIntent(); //get the current intent
        String current_name = intent.getStringExtra("CURRENT_NAME");
        String current_phone = intent.getStringExtra("CURRENT_PHONE");
        String current_address = intent.getStringExtra("CURRENT_ADDRESS");
        //Set the default information in the text fields
        doctorName.setText(current_name);
        doctorPhone.setText(current_phone);
        doctorAddress.setText(current_address);
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
                Toast.makeText(EditProfilePatientActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //Select image from gallery
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updateAddress = doctorAddress.getText().toString();
                String updateName = doctorName.getText().toString();
                //String updateEmail = doctorEmail.getText().toString();
                String updatePhone = doctorPhone.getText().toString();
                uploadProfileImage();
                updateDoctorInfo(updateName, updateAddress, updatePhone);
            }
        });
    }
    // update the doctor's information in the database
    private void updateDoctorInfo(String name, String address, String phone)
    {
        DocumentReference doctorReference = doctorRef.collection("Patient").document("" + doctorID + "");
        doctorReference.update("name", name);
        doctorReference.update("address", address);
        doctorReference.update("phone", phone)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfilePatientActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditProfilePatientActivity.this, PatientProfileActivity.class);
                                startActivity(intent);
                            }
                        }
                ).addOnFailureListener(
                        e -> Toast.makeText(EditProfilePatientActivity.this, "Profile Update Failed", Toast.LENGTH_LONG).show()
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
    private void uploadProfileImage() {
        // Check if the selected image is not null
        if (uriImage != null) {
            // Create a reference to the storage location where the image will be uploaded
            // The location is specified using the currentDoctorUID and the file extension of the image
            StorageReference storageReference = pStorageRef.child(currentDoctorUID + "." + getFileExtension(uriImage));

            // Upload the image to the storage location
            // Use the 'continueWithTask' method to chain a continuation task that retrieves the download URL
            storageReference.putFile(uriImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        // If the upload task was not successful, throw an exception to stop the continuation
                        throw task.getException();
                    }
                    // If the upload task was successful, return a task that retrieves the download URL
                    return pStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // If the download URL retrieval task was successful, get the download URL from the result
                        Uri downloadUri = task.getResult();
                        Log.e(TAG, "then: " + downloadUri.toString());

                        // Create a new UploadImage object with the currentDoctorUID and the download URL
                        UploadImage upload = new UploadImage(currentDoctorUID, downloadUri.toString());

                        // Upload the UploadImage object to the database
                        pDatabaseRef.push().setValue(upload);

                        // TODO: Add code here to update the user's profile with the new image
                        // For now we'll just display the image in the ImageView
                    } else {
                        // If the download URL retrieval task was not successful, show an error message
                        Toast.makeText(EditProfilePatientActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}