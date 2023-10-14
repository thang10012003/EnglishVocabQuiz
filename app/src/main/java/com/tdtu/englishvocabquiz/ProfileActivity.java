package com.tdtu.englishvocabquiz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tdtu.englishvocabquiz.databinding.ActivityProfileBinding;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private String uid;
    private UserModel userCurrModel;

    private FirebaseUser currUser;
    StorageReference storageRef ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //firebase init
        auth = FirebaseAuth.getInstance();
        //database
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");

        //get uid
        Intent intent = getIntent();
        uid  = intent.getStringExtra("uid");

        //show data of user
        showUserDataByUid();

        //get current user
        currUser = auth.getCurrentUser();

        //fibaseapp init, storage image
        FirebaseApp.initializeApp(ProfileActivity.this);

        //back home
        binding.homeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
            }
        });
        //logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                auth.signOut();
                Toast.makeText(ProfileActivity.this,"Đăng xuất thành công.",Toast.LENGTH_LONG).show();
                 Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //edit profile
        binding.editProfileBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                //send class user with line of string
                intent.putExtra("userName",  userCurrModel.getName());
                intent.putExtra("mobile",  userCurrModel.getMobile());
                intent.putExtra("gender",  userCurrModel.getGender());

                startActivity(intent);
            }
        });
        //change password
        binding.changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,UpdatePasswordActivity.class);
                startActivity(intent);
            }
        });



    }
    public void showUserDataByUid(){
       ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel data = snapshot.getValue(UserModel.class);
                //clone value to putExtra for edit profile
                userCurrModel = new UserModel(data);

                //set avt
                renderAvt(data.getAvt());

                //set posts number
                binding.topicCount.setText(String.valueOf(data.getPosts()));
                //set name
                binding.name.setText(data.getName());
                //set age's account
                LocalDate nowDate = LocalDate.now();
                LocalDate userDate = LocalDate.parse(data.getCreateDate());
                long ageAccount = ChronoUnit.DAYS.between( userDate ,nowDate);
                binding.textView12.setText(String.valueOf(ageAccount+1));
                //set gender
               binding.gender.setText(data.getGender());
               //set mobile
               binding.mobile.setText(data.getMobile());


           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private void renderAvt(String avt) {
        //set image if null will show default avatar
        if(!avt.equals("Chưa có")){
            storageRef = FirebaseStorage.getInstance().getReference("avatarImages").child(avt);
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Glide.with(getApplicationContext()).load(imageUrl).into(binding.uploadImgView);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Không thể tải ảnh đại diện !", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(ProfileActivity.this, "chưa có avatar", Toast.LENGTH_SHORT).show();
        }
    }
}