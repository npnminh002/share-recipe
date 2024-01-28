package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.IngridentsAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentThirdRecipeBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.interfaces.OnAddedSuccess;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class ThirdRecipeFragment extends BaseFragment<FragmentThirdRecipeBinding> {
    public AppViewModel viewModel;
    public RecipeViewModel recipeViewModel;
    private OnAddedSuccess onAddedSuccess;

    public ThirdRecipeFragment(AppViewModel viewModel, RecipeViewModel recipeViewModel, OnAddedSuccess onAddedSuccess) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
        this.onAddedSuccess = onAddedSuccess;
    }

    private Recipe recipe;
    public ArrayList<Ingredients> listDefautIngrident = new ArrayList<>();

    @Override
    protected FragmentThirdRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentThirdRecipeBinding.inflate(getLayoutInflater());
    }

    private IngridentsAdapter ingridentsAdapter;

    @Override
    protected void initView() {
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(ThirdRecipeFragment.this);
        });
        ingridentsAdapter = new IngridentsAdapter(new IngridentsAdapter.OnIngridetEvent() {
            @Override
            public void onNameChange(Ingredients ingredients, String value, int po) {
                listDefautIngrident.get(po).Name = value;
            }

            @Override
            public void onQuantitiveChange(Ingredients ingredients, String value, int pos) {
                listDefautIngrident.get(pos).Quantitative = Float.parseFloat(value);
            }

            @Override
            public void onRemove(Ingredients id, int pos) {
                removeIngridents(id, pos);
            }
        });

        binding.rcvIngrident.setAdapter(ingridentsAdapter);

        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            if (data == null || data.Ingredients == null || data.Ingredients.size() == 0) {
                listDefautIngrident = new ArrayList<>(Arrays.asList(
                        new Ingredients(1, getString(R.string.ingredients) + " 1", 0),
                        new Ingredients(2, getString(R.string.ingredients) + " 2", 0),
                        new Ingredients(3, getString(R.string.ingredients) + " 3", 0)
                ));
            } else {
                listDefautIngrident = data.Ingredients;
            }
            ingridentsAdapter.setItems(listDefautIngrident);
        });

    }

    private void removeIngridents(Ingredients id, int pos) {
        new ConfirmDialog(ThirdRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            listDefautIngrident.remove(pos);
            ingridentsAdapter.setItems(listDefautIngrident);
        }).show();
    }

    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(ThirdRecipeFragment.this);
            recipe.Ingredients = new ArrayList<>();
            recipeViewModel.recipeLiveData.postValue(recipe);
            hideKeyboard();
        });
        binding.btnNext.setOnClickListener(v ->
                {
                    replaceFragment(new FourRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
                    postValue();
                }
        );
        binding.icAddIngrident.setOnClickListener(v -> {
            int index = listDefautIngrident.size() + 1;
            listDefautIngrident.add(0, new Ingredients(index, getString(R.string.ingredients) + " " + index, 0f));
            ingridentsAdapter.setItems(listDefautIngrident);
        });
        binding.btnPrev.setOnClickListener(v -> {
            closeFragment(ThirdRecipeFragment.this);
            postValue();

        });
    }

    private void postValue() {
        recipe.Ingredients = listDefautIngrident;
        recipeViewModel.recipeLiveData.postValue(recipe);
        hideKeyboard();

    }

}
