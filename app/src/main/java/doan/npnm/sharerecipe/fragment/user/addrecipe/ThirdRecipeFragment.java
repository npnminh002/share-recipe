package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.IngridentsAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentThirdRecipeBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class ThirdRecipeFragment extends BaseFragment<FragmentThirdRecipeBinding> {
    public UserViewModel viewModel;
    public RecipeViewModel recipeViewModel;

    public ThirdRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
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

        listDefautIngrident = new ArrayList<>(Arrays.asList(
                new Ingredients(1, getString(R.string.ingredients) + " 1", 0),
                new Ingredients(2, getString(R.string.ingredients) + " 2", 0),
                new Ingredients(3, getString(R.string.ingredients) + " 3", 0)
        ));


        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(ThirdRecipeFragment.this);
        });
        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.EDIT, new IngridentsAdapter.OnIngridentEvent() {
            @Override
            public void onNameChange(Ingredients ingredients) {
                for (Ingredients id : recipe.Ingredients) {
                    if (id.Id == ingredients.Id) {
                        id.Name = ingredients.Name;
                        break;  }
                }
            }


            @Override
            public void onQuantitiveChange(Ingredients ingredients) {
                for (Ingredients id : recipe.Ingredients) {
                    if (id.Id == ingredients.Id) {
                        id.Quantitative = ingredients.Quantitative;
                        break;  }
                }
            }

            @Override
            public void onRemove(Ingredients id, int pos) {
                removeIngridents(id, pos);
            }
        });

        binding.rcvIngrident.setAdapter(ingridentsAdapter);

        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            if (data == null || data.Ingredients == null || data.Ingredients.isEmpty()) {
                recipe.Ingredients = listDefautIngrident;
            } else {
                listDefautIngrident = data.Ingredients;
            }
            ingridentsAdapter.setItems(recipe.Ingredients);
        });

    }

    private void removeIngridents(Ingredients id, int pos) {
        new ConfirmDialog(ThirdRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            recipe.Ingredients.remove(pos);
            ingridentsAdapter.setItems(recipe.Ingredients);
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
            addNewIngridents();
        });
        binding.btnPrev.setOnClickListener(v -> {
            closeFragment(ThirdRecipeFragment.this);
            postValue();

        });

    }

    private void addNewIngridents() {
        int index = recipe.Ingredients.size() + 1;
        Ingredients newIngredient = new Ingredients(index, getString(R.string.ingredients) + " " + index, 0f);
        recipe.Ingredients.add(newIngredient);
        Collections.sort(recipe.Ingredients, (o1, o2) -> String.valueOf(o2.Id).compareTo(String.valueOf(o1.Id)));
        recipeViewModel.recipeLiveData.postValue(recipe);
    }


    private void postValue() {
        recipe.Ingredients = listDefautIngrident;
        recipeViewModel.recipeLiveData.postValue(recipe);
        hideKeyboard();

    }

}
