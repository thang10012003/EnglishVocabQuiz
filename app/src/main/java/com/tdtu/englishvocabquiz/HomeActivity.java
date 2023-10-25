package com.tdtu.englishvocabquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.tdtu.englishvocabquiz.databinding.ActivityHomeBinding;
import com.tdtu.englishvocabquiz.databinding.ActivityMainBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.navigationBar.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.navigationHome){
                replaceFragment(new HomeFragment());
            }else if(item.getItemId() == R.id.navigationSolutions){
                replaceFragment(new SolutionsFragment());
            }else if(item.getItemId() == R.id.navigationAdd){

            }else if(item.getItemId() == R.id.navigationLibrary){
                replaceFragment(new LibraryFragment());
            }else if(item.getItemId() == R.id.navigationUser) {
                replaceFragment(new UserFragment());
            }

            return  true;
        });

    }
    private void  replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }
}