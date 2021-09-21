package com.example.wootlabwhatsapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wootlabwhatsapp.R;
import com.example.wootlabwhatsapp.databinding.ActivityPhoneLoginBinding;
import com.example.wootlabwhatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    ActivityPhoneLoginBinding binding;

    private static String TAG = "PhoneLoginActivity";
    private String mVerificationId;
    private FirebaseAuth mAuth;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            startActivity(new Intent(this, SetUserInfoActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnNext.getText().toString().equals("Next")) {
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    String phone = "+" + binding.edCountryCode.getText().toString() + binding.edPhone.getText().toString();
                    startPhoneVerification(phone);
                } else {
                    progressDialog.setMessage("Verifying...");
                    progressDialog.show();
                    verifyPhoneWithCode(mVerificationId, binding.edCode.getText().toString());
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted complete" );
                progressDialog.dismiss();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "onVerificationCompleted failed"+e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.e(TAG, "onCodeSent:"+ mVerificationId);
                mVerificationId = verificationID;
                mResendToken = token;

                binding.btnNext.setText("Confirm");
                progressDialog.dismiss();
            }
        };
    }

    private void startPhoneVerification(String phone) {
        progressDialog.setMessage("Send code to : " +phone);
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    private void verifyPhoneWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Log.d(TAG, "signInWithPhoneAuthCredential : success");

                    FirebaseUser fbuser = task.getResult().getUser();


                    if (fbuser != null) {
                        String userId = fbuser.getUid();
                        User user = new User(
                                userId,
                                "",
                                fbuser.getPhoneNumber(),
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                        );

                        firestore.collection("Users").document().collection(userId)
                                .add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull  Task<DocumentReference> task) {
                                startActivity(new Intent(PhoneLoginActivity.this, SetUserInfoActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure"+e.getMessage());
                            }
                        });
                    }else {
                        Log.e(TAG, "signInWithCredential failure"+task.getException());

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure "+e.getMessage());

            }
        });
    }
}