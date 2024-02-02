package doan.npnm.sharerecipe.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}