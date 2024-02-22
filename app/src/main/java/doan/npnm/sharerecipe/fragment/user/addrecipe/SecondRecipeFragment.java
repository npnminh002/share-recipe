package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentSecondRecipeBinding;
import doan.npnm.sharerecipe.interfaces.OnAddedSuccess;
import doan.npnm.sharerecipe.model.recipe.CookTime;
import doan.npnm.sharerecipe.model.recipe.PrepareTime;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class SecondRecipeFragment extends BaseFragment<FragmentSecondRecipeBinding> {
    public UserViewModel viewModel;

    public RecipeViewModel recipeViewModel;
    private OnAddedSuccess onAddedSuccess;

    public SecondRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel, OnAddedSuccess onAddedSuccess) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
        this.onAddedSuccess = onAddedSuccess;
    }

    private Recipe recipe;

    @Override
    protected FragmentSecondRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSecondRecipeBinding.inflate(getLayoutInflater());
    }

    private CookTime cookTime;
    public PrepareTime prepareTime;
    public ArrayList<String> listTimeType;
    public ArrayList<String> listLever;

    @Override
    protected void initView() {
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(SecondRecipeFragment.this);
        });

        listLever = new ArrayList<>(
                Arrays.asList(getString(R.string.easy), getString(R.string.normal), getString(R.string.difficul)));
        listTimeType = new ArrayList<>(Arrays.asList("s", "m", "h"));

        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;

            if (data.PrepareTime == null) {
                prepareTime = new PrepareTime();
            } else {
                prepareTime = data.PrepareTime;
            }
            if (data.CookTime == null) {
                cookTime = new CookTime();
            } else {
                cookTime = data.CookTime;
            }

            binding.timePrepare.setText(prepareTime.Time == null ? "0" : prepareTime.Time);
            binding.selectMinutePP.setText(prepareTime.TimeType == null ? "s" : prepareTime.TimeType);
            binding.timeCook.setText(cookTime.Time == null ? "0" : cookTime.Time);
            binding.selectMinutePP.setText(cookTime.TimeType == null ? "s" : cookTime.TimeType);
            binding.txtLever.setText(data.Level == null ? "" : data.Level);


        });
    }

    private int indexPpTime = 0;
    private int indexCookTime = 0;
    private int indexLevel = 0;

    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(SecondRecipeFragment.this);
            recipe.Level = "";
            prepareTime = new PrepareTime();
            cookTime = new CookTime();
            recipe.PrepareTime = prepareTime;
            recipe.CookTime = cookTime;
            recipeViewModel.recipeLiveData.postValue(recipe);
            hideKeyboard();

        });
        binding.btnNext.setOnClickListener(v -> {

            addFragment(new ThirdRecipeFragment(viewModel, recipeViewModel, onAddedSuccess), android.R.id.content, true);
            postValue();
        });

        binding.changePPtime.setOnClickListener(v -> changePpTime());
        binding.changeCookTime.setOnClickListener(v -> changeCookTime());
        binding.changeLevel.setOnClickListener(v -> changeLevel());
        binding.btnPrev.setOnClickListener(v -> {
            closeFragment(SecondRecipeFragment.this);
            postValue();
        });
    }

    private void postValue() {
        recipe.Level = binding.txtLever.getText().toString();
        prepareTime = new PrepareTime(binding.timePrepare.getText().toString(), binding.selectMinutePP.getText().toString());
        cookTime = new CookTime(binding.timeCook.getText().toString(), binding.selectMinuteCook.getText().toString());
        recipe.PrepareTime = prepareTime;
        recipe.CookTime = cookTime;
        recipeViewModel.recipeLiveData.postValue(recipe);
        hideKeyboard();
    }

    private void changeLevel() {
        indexLevel++;
        binding.txtLever.setText(listLever.get(indexLevel));
        if (indexLevel >= listLever.size() - 1) {
            indexLevel = -1;
        }
    }

    private void changePpTime() {
        indexPpTime++;
        binding.selectMinutePP.setText(listTimeType.get(indexPpTime));
        if (indexPpTime >= listTimeType.size() - 1) {
            indexPpTime = -1;
        }
    }


    public boolean checkNumber(String value) {
        String numberPattern = "-?\\d*\\.?\\d+";
        return value.matches(numberPattern);
    }


    private void changeCookTime() {
        indexCookTime++;
        binding.selectMinuteCook.setText(listTimeType.get(indexCookTime));
        if (indexCookTime >= listTimeType.size() - 1) {
            indexCookTime = -1;
        }

    }
}
