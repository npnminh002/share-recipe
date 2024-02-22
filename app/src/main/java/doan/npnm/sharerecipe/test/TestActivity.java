package doan.npnm.sharerecipe.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.databinding.ActivityTestBinding;
import doan.npnm.sharerecipe.dialog.BottomManagerRecipe;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}