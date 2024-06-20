package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {


    EditText mnewusername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    FirebaseFirestore firebaseFirestore;
    ImageView mgetnewuserimageinimageview;
    StorageReference storageReference;

    String imageURIaccesstoken;
    androidx.appcompat.widget.Toolbar mtoolbarofupdateprofile;
    ImageButton mbackbuttonofupdateprofile;
    FirebaseStorage firebaseStorage;
    ProgressBar mprogressbarofupdateprofile;

    Uri imagepath;
    Intent intent;

    android.widget.Button mupdateprofilebutton;
    private static  int PICK_IMAGE=123;

    String newname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mtoolbarofupdateprofile=findViewById(R.id.toolbarofupdateprofile);
        mbackbuttonofupdateprofile=findViewById(R.id.backbuttonofupdateprofile);
        mgetnewuserimageinimageview=findViewById(R.id.getnewuserimageinimageview);
        mprogressbarofupdateprofile=findViewById(R.id.progressbarofupdateprofile);
        mnewusername=findViewById(R.id.getnewusername);
        mupdateprofilebutton=findViewById(R.id.updateprofilebutton);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        intent = getIntent();
        setSupportActionBar(mtoolbarofupdateprofile);

        mbackbuttonofupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mnewusername.setText(intent.getStringExtra("nameofuser"));

        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        mupdateprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newname=mnewusername.getText().toString();
                if(newname.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name is empty",Toast.LENGTH_SHORT).show();
                }else if(imagepath!=null){
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userprofile muserprofile = new userprofile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);
                    updateimagetostorage();
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent =new Intent(UpdateProfile.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userprofile muserprofile = new userprofile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);
                    updatenameonclouldfirestore();
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent =new Intent(UpdateProfile.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        mgetnewuserimageinimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageURIaccesstoken=uri.toString();
                Picasso.get().load(uri).into(mgetnewuserimageinimageview);
            }
        });

    }

    private void updatenameonclouldfirestore() {

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("name", newname);
        userdata.put("image", imageURIaccesstoken);
        userdata.put("uid", firebaseAuth.getUid());
        userdata.put("status", "Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data sent to Cloud Firestore", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateimagetostorage() {
        StorageReference imageref= storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");
        Bitmap bitmap=null;
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }catch (IOException e){
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        UploadTask uploadTask=imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURIaccesstoken=uri.toString();
                        Toast.makeText(getApplicationContext(),"Uri success",Toast.LENGTH_LONG).show();
                        updatenameonclouldfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Uri failed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image Not Uploaded",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            imagepath=data.getData();
            mgetnewuserimageinimageview.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online");
    }
}