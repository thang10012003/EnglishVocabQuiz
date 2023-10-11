package com.tdtu.englishvocabquiz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tdtu.englishvocabquiz.databinding.ActivityMainBinding;
import com.tdtu.englishvocabquiz.databinding.ActivitySignUpBinding;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //firebase init
        auth = FirebaseAuth.getInstance();
        //database
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");

        //submit sign up btn
        binding.signUpBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = binding.emailEdt.getText().toString().trim();
                String pass = binding.passEdt.getText().toString().trim();
                String conPass = binding.confPassEdt.getText().toString().trim();
                String name = binding.nameEdt.getText().toString().trim();


                //validate
                if(email.isEmpty()){
                    binding.emailEdt.setError("Email không được để trống");
                }
                else if(pass.isEmpty()){
                    binding.passEdt.setError("Mật khẩu không được để trống");
                }
                else if(conPass.isEmpty()){
                    binding.confPassEdt.setError("Mật khẩu xác nhận không được để trống");
                }
                else if (!conPass.equals(pass)){
                    binding.confPassEdt.setError("Mật khẩu xác nhận không chính xác");

                }else if (name.isEmpty()){
                    binding.confPassEdt.setError("Vui lòng nhập họ tên !");

                }
                else{
                    //create acc
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //get uid
                                FirebaseUser user = auth.getCurrentUser();
                                String uid = user.getUid();


                                //get now date
                                LocalDate myObj = LocalDate.now();
                                String createDate = String.valueOf(myObj);

                                //create user model
                                String gender ="Chưa có";
                                String avt ="Chưa có";
                                String mobile ="Chưa có ";
                                int posts = 0;

                                UserModel newUser = new UserModel(name,gender,createDate,posts,avt,uid,mobile);

                                //add child to db
                                ref.child(uid).setValue(newUser);

                                Toast.makeText(SignUpActivity.this,"Đăng ký thành công",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUpActivity.this,ProfileActivity.class);
                                //send uid
                                intent.putExtra("uid",uid);

                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(SignUpActivity.this,"Đăng ký thất bại !!!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
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