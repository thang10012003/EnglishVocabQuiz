package com.tdtu.englishvocabquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tdtu.englishvocabquiz.databinding.ActivityChangePasswordBinding;

public class UpdatePasswordActivity extends AppCompatActivity {
    ActivityChangePasswordBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currUser;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //firebase init
        auth = FirebaseAuth.getInstance();
        currUser = auth.getCurrentUser();
        //alert
         builder = new AlertDialog.Builder(this);

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = binding.oldPassEdt.getText().toString().trim();
                String newPass = binding.newPassEdt.getText().toString().trim();
                String confNewPass = binding.confNewPassEdt.getText().toString().trim();
                //validate
                if(oldPass.isEmpty()){
                    binding.oldPassEdt.setError("Mật khẩu cũ chưa nhập !");
                }else if(newPass.isEmpty() ){
                    binding.newPassEdt.setError("Mật khẩu mới chưa nhập !");
                }else if(confNewPass.isEmpty() ){
                    binding.confNewPassEdt.setError("Mật khẩu xác nhận chưa nhập !");
                }
                else if(!confNewPass.equals(newPass) ){
                    binding.confNewPassEdt.setError("Mật khẩu xác nhận chưa trùng khớp !");
                }else{
                    updatePassword(oldPass,newPass);
                }
            }
        });
        //back btn
        binding.backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updatePassword(String oldPass, String newPass) {
        builder.setMessage("Đang xử lý cập nhật mật khẩu...");
        AlertDialog alert = builder.create();
        alert.show();
        AuthCredential authCredential = EmailAuthProvider.getCredential(currUser.getEmail(),oldPass);
        currUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        currUser.updatePassword(newPass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        alert.dismiss();
                                        Toast.makeText(UpdatePasswordActivity.this,"Cập nhật thành công !",Toast.LENGTH_LONG).show();
                                        auth.signOut();
                                        Intent intent = new Intent(UpdatePasswordActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        alert.dismiss();
                                        Toast.makeText(UpdatePasswordActivity.this,"Không thể cập nhất mới mật khẩu !",Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alert.dismiss();
                        Toast.makeText(UpdatePasswordActivity.this,"Mật khẩu cũ không chính xác !",Toast.LENGTH_LONG).show();
                    }
                });
    }
}