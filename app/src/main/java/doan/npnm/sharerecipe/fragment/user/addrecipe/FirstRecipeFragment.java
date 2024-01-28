package doan.npnm.sharerecipe.fragment.user.addrecipe;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentFirstRecipeBinding;
import doan.npnm.sharerecipe.interfaces.OnAddedSuccess;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class FirstRecipeFragment extends BaseFragment<FragmentFirstRecipeBinding> {
    public AppViewModel viewModel;
    private RecipeViewModel recipeViewModel;
    private Recipe recipe;
    private OnAddedSuccess onAddedSuccess;

    public FirstRecipeFragment(AppViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected FragmentFirstRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFirstRecipeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        onAddedSuccess = () -> {

            recipeViewModel = new ViewModelProvider(FirstRecipeFragment.this).get(RecipeViewModel.class);
            showToast("Add Success");

        };
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;


            if (recipeViewModel.imgUri != null) {
                loadImage(recipeViewModel.imgUri, binding.imgProduct);
                binding.txtChooseImage.setVisibility(View.GONE);
            }
            binding.nameOfRecipe.setText(data.Name == null ? "" : data.Name);
            binding.description.setText(data.Description == null ? "" : data.Description);
        });

    }

    private int REQUEST_CODE_IMAGE = 1;

    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(FirstRecipeFragment.this));
        binding.btnNext.setOnClickListener(v -> {
            String name = binding.nameOfRecipe.getText().toString();
            String descitption = binding.description.getText().toString();
            if (name.isEmpty() || descitption.isEmpty()) {
                showToast("Enter all value");
            } else {
                if (recipe == null) {
                    recipe = new Recipe();
                }
                recipe.Name = name;
                recipe.Description = descitption;
                recipeViewModel.recipeLiveData.postValue(recipe);
                addFragment(new SecondRecipeFragment(viewModel, recipeViewModel, onAddedSuccess), android.R.id.content, true);
                hideKeyboard();
            }

        });

        binding.llChoseImage.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                permissionLauncher.launch(permissions);
            } else {
                openImagePicker();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    private Uri uriImg;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                uriImg = data.getData();
                loadImage(String.valueOf(uriImg), binding.imgProduct);
                recipeViewModel.imgUri = uriImg;
                binding.txtChooseImage.setVisibility(View.GONE);
            }
        }
    }


}


