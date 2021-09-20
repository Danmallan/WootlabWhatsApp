package com.example.wootlabwhatsapp.view;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.wootlabwhatsapp.R;
import com.example.wootlabwhatsapp.SplashScreenActivity;
import com.example.wootlabwhatsapp.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bottomSheetDialog, bottomSheetDialogEditName;
    private ProgressDialog dialog;
    private String userName;

    private int IMAGE_GALLERY_REQUEST = 115512;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);

        if (firebaseUser != null) {
            getInfo();
        }

        initActionClick();

    }

    private void initActionClick() {
        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoPickBottomSheet();
            }
        });

        binding.editNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetEditName();
            }
        });

        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Do you want to sign out ?");
        alertBuilder.setPositiveButton(getResources().getString(R.string.sign_out), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, SplashScreenActivity.class));
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    private void showPhotoPickBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick, null);
        view.findViewById(R.id.image_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setOnDismissListener(dialog1 -> bottomSheetDialog = null);
        bottomSheetDialog.show();
    }

    private void showBottomSheetEditName() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_edit, null);
        EditText editTextUserName = view.findViewById(R.id.ed_username);
        if (userName != null)
            editTextUserName.setText(userName);
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextUserName.getText().toString())) {
                    Toast.makeText(ProfileActivity.this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    updateEditName(editTextUserName.getText().toString());
                    bottomSheetDialogEditName.dismiss();
                    getInfo();
                }
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialogEditName.dismiss();

            }
        });

        bottomSheetDialogEditName = new BottomSheetDialog(this);
        bottomSheetDialogEditName.setContentView(view);

        bottomSheetDialogEditName.setOnDismissListener(dialog1 -> bottomSheetDialog = null);
        bottomSheetDialogEditName.show();
    }

    private void updateEditName(String newName) {
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName", newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.e("Update name error", e.getMessage());
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            upLoadToFirebase();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.profileImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.e("Image Error : ", e.getMessage());
            }
        }
    }

    private void upLoadToFirebase() {
        if (imageUri != null) {
            dialog.setMessage("Uploading image, please wait..");
            dialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImageProfile/" + System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadUri = uriTask.getResult();

                            final String sdownload_url = String.valueOf(downloadUri);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("imageProfile", sdownload_url);

                            firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Upload Successfull!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(e -> {
                                dialog.dismiss();
                                Log.e("Image Profile error", e.getMessage());
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.e("Image Profile", e.getMessage());

                }
            });

        }
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        userName = documentSnapshot.getString("userName");

                        binding.tvName.setText(userName);
                        binding.tvPhone.setText(documentSnapshot.getString("userPhone"));
                        Glide.with(ProfileActivity.this)
                                .load(imageProfile)
                                .placeholder(R.drawable.place_holder)
                                .error(R.drawable.place_holder)
                                .into(binding.profileImage);
                    }
                }).addOnFailureListener(e -> {
            Log.e("Profile Activity", e.getMessage());
        });
    }
}