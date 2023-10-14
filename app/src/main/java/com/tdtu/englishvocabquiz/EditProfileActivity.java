package com.tdtu.englishvocabquiz;

import static kotlinx.coroutines.DelayKt.delay;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tdtu.englishvocabquiz.databinding.ActivityEditProfileBinding;
import com.tdtu.englishvocabquiz.databinding.ActivityProfileBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alert;
    StorageReference storageRef ;
    Uri image = null;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //handle show activity to choose imgs
            if(result.getResultCode()==RESULT_OK){
                if(result.getData()!=null){
                    image = result.getData().getData();
                    Glide.with(getApplicationContext()).load(image).into(binding.uploadImgView);
                }
            }else{
                Toast.makeText(EditProfileActivity.this, "Bạn chưa chọn ảnh nào !", Toast.LENGTH_SHORT).show();
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //user database
        db = FirebaseDatabase.getInstance();

        //alert dialog
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Thông Báo");
        alertDialogBuilder.setMessage("Đang cập nhật thông tin...");

        //load data from firebase db
         showDataFromProfileUser();

        //review profile
        binding.reviewProfileBtn.setEnabled(false);
        binding.reviewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //back home
        binding.backBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        //upload img
        binding.uploadImgView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //fibaseapp init, storage image
                FirebaseApp.initializeApp(EditProfileActivity.this);
                storageRef = FirebaseStorage.getInstance().getReference();
                //show folder to choose img
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                activityResultLauncher.launch(i); //choose img and show on ImageView
            }




        });

        //submit edit profile
        binding.submitBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String gender="Chưa có";
                String name = binding.newNameEdt.getText().toString().trim();
                String mobile=binding.newMobileEdt.getText().toString().trim();
                if(binding.maleCb.isChecked()){
                    gender="Nam";
                }
                if(binding.femaleCb.isChecked()){
                    gender="Nữ";
                }

                if(name.isEmpty()){
                    Toast.makeText(EditProfileActivity.this, "Vui lòng điền tên !", Toast.LENGTH_SHORT).show();
                }
                if(mobile.isEmpty()){
                    Toast.makeText(EditProfileActivity.this, "Vui lòng điền số điệnt thoại.", Toast.LENGTH_SHORT).show();
                }


                    //handle update - if having image will insert but wont
                    updateDataUser(name,mobile,gender);






            }
        });
    }

    private void updateDataUser(String name, String mobile, String gender) {
        alert = alertDialogBuilder.create();
        alert.show();
        //if user want to upload img then upload on firebase db
        String avt = null;
        if(image != null){
            avt = uploadImg(image);//
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        ref = db.getReference("users").child(uid);

        Map<String,Object> newInfo = new HashMap<>();
        newInfo.put("name",name);
        newInfo.put("mobile",mobile);
        newInfo.put("gender",gender);
        if(avt != null){
            newInfo.put("avt",avt);
        }

        ref.updateChildren(newInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                alert.dismiss();
                Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                binding.reviewProfileBtn.setEnabled(true);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alert.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thất bại !.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showDataFromProfileUser() {
        Intent i = getIntent();
        Bundle  bundle = i.getExtras();

       binding.newNameEdt.setText((String)bundle.get("userName"));
       binding.newMobileEdt.setText((String)bundle.get("mobile"));
       String gender = (String) bundle.get("gender");
       if(gender.equals("Name")){
           binding.maleCb.isChecked();
       }
        if(gender.equals("Nữ")){
            binding.femaleCb.isChecked();
        }
    }

    private String uploadImg(Uri image) {
        //set progress alert
//        alertDialogBuilder.setMessage("Đang tải ảnh lên hệ thống...");
//        AlertDialog imgAlert = alertDialogBuilder.create();
//        imgAlert.show();

        //set submit prevent
        binding.submitBtn.setEnabled(false);

        String imgName = UUID.randomUUID().toString();
        StorageReference Sref = storageRef.child("avatarImages/"+ imgName);
        Sref.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                binding.submitBtn.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Cập nhật ảnh thành công !", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.submitBtn.setEnabled(true);
                        Toast.makeText(EditProfileActivity.this, "Cập nhật ảnh thất bại !", Toast.LENGTH_SHORT).show();
                    }
                });
        return imgName;

    }
}