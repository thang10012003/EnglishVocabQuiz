package com.tdtu.englishvocabquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tdtu.englishvocabquiz.databinding.ActivityLoginBinding;
import com.tdtu.englishvocabquiz.databinding.ActivitySignUpBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //firebase init
        auth = FirebaseAuth.getInstance();
        //database
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        //submit sign up btn
        binding.loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = binding.emailEdt.getText().toString().trim();
                String pass = binding.passEdt.getText().toString().trim();
                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(email,pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        //get uid
                                        FirebaseUser user = auth.getCurrentUser();
                                        String uid = user.getUid();

                                        Toast.makeText(LoginActivity.this,"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
                                        //send uid
                                        intent.putExtra("uid",uid);

                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this,"Tài khoản hoặc mật khẩu không đúng.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        binding.passEdt.setError("Mật khẩu bị trống !");
                    }
                }else if(email.isEmpty()){
                    binding.emailEdt.setError("Email bị trống !");
                }else{
                    binding.emailEdt.setError("Email sai!");
                }

            }
        });

        //back tbn
        binding.backBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}