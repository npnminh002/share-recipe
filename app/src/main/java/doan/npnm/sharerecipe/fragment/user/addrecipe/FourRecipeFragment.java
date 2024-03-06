package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.DirectionsAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.databinding.FragmentFourRecipeBinding;

public class FourRecipeFragment extends BaseFragment<FragmentFourRecipeBinding> {
    public UserViewModel viewModel;
    public RecipeViewModel recipeViewModel;
    private Recipe recipe;
    public ArrayList<Directions> listDefDirection = new ArrayList<>();

    public FourRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
    }

    @Override
    protected FragmentFourRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFourRecipeBinding.inflate(getLayoutInflater());
    }

    private DirectionsAdapter directionsAdapter;

    @Override
    protected void initView() {

        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(FourRecipeFragment.this);
        });
        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.EDIT, new DirectionsAdapter.OnDirectionEvent() {
            @Override
            public void onNameChange(Directions directions, String value, int postion) {
                int indexOf = listDefDirection.indexOf(directions);
                if (indexOf != -1) {
                    listDefDirection.get(indexOf).Name = value;
                }
            }

            @Override
            public void onRemove(Directions id, int pos) {
                removeDirection(id, pos);
            }
        });

        binding.rcvDirection.setAdapter(directionsAdapter);
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            if (data == null || data.Directions == null || data.Directions.size() == 0) {
                listDefDirection = new ArrayList<>(Arrays.asList(
                        new Directions(1, getContext().getString(R.string.directions) + " 1"),
                        new Directions(2, getContext().getString(R.string.directions) + " 2"),
                        new Directions(3, getContext().getString(R.string.directions) + " 3")
                ));
            } else {
                listDefDirection = data.Directions;
            }
            directionsAdapter.setItems(listDefDirection);
        });

    }

    private void removeDirection(Directions id, int pos) {
        new ConfirmDialog(FourRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            listDefDirection.remove(pos);
            directionsAdapter.setItems(listDefDirection);
        }).show();
    }

    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(FourRecipeFragment.this);
            postValue(false);
        });
        binding.btnNext.setOnClickListener(v -> {
            replaceFragment(new FiveRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
            postValue(true);
        });
        binding.icAddIngrident.setOnClickListener(v -> {
            int index = listDefDirection.size() + 1;
            listDefDirection.add(0, new Directions(index, getString(R.string.ingredients) + " " + index));
            directionsAdapter.setItems(listDefDirection);
        });

        binding.btnPrev.setOnClickListener(v -> {
            closeFragment(FourRecipeFragment.this);
            postValue(true);
        });
    }

    private void postValue(boolean b) {
        recipe.Directions = b ? listDefDirection : new ArrayList<>();
        recipeViewModel.recipeLiveData.postValue(recipe);
        hideKeyboard();
    }

}
