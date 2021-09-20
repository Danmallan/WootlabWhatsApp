package com.example.wootlabwhatsapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.wootlabwhatsapp.MainActivity;
import com.example.wootlabwhatsapp.R;
import com.example.wootlabwhatsapp.databinding.ActivitySetUserInfoBinding;
import com.example.wootlabwhatsapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetUserInfoActivity extends AppCompatActivity {

    ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_info);

        progressDialog = new ProgressDialog(this);

        initButtonClick();
    }

    private void initButtonClick() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.etName.getText().toString())){
                    Toast.makeText(SetUserInfoActivity.this, "Please input username", Toast.LENGTH_SHORT).show();
                }else {
                    doUpdate();
                }
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    private void pickImage() {
    }

    private void doUpdate() {
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null ){
            User user = new User(
                    firebaseUser.getUid(),
                    binding.etName.getText().toString(),
                    firebaseUser.getPhoneNumber(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
            );
            firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(SetUserInfoActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SetUserInfoActivity.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SetUserInfoActivity.this, "Error"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "You need to login first!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}