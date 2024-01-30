package doan.npnm.sharerecipe.fragment.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import doan.npnm.sharerecipe.activity.AddDataActivity;
import doan.npnm.sharerecipe.adapter.CategoryAdapter;
import doan.npnm.sharerecipe.adapter.RecipeAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentHomeUserBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class HomeUserFragment extends BaseFragment<FragmentHomeUserBinding> {

    public AppViewModel homeviewModel;

    public HomeUserFragment(AppViewModel appViewModel) {
        this.homeviewModel = appViewModel;
    }

    @Override
    public void OnClick() {

    }


    @Override
    protected FragmentHomeUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeUserBinding.inflate(getLayoutInflater());
    }

    private CategoryAdapter categoryAdapter;

    private RecipeAdapter recipeAdapter;


    @Override
    protected void initView() {

//        homeviewModel.recipeLiveData.observe(this,arr->{
//            recipeAdapter.setItems(arr);
//
//            loadImage(arr.get(0).ImgUrl,binding.imgUsers);
//
//        });

        homeviewModel.getData(new AppViewModel.OnGetSucces() {
            @Override
            public void onData(ArrayList<Recipe> arr) {
                recipeAdapter.setItems(arr);

                loadImage(arr.get(0).ImgUrl, binding.imgUsers);
            }
        });
        categoryAdapter = new CategoryAdapter(category -> {

        });

        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {

            }

            @Override
            public void onSave(Recipe recipe) {

            }
        });
        binding.imgUsers.setOnClickListener(v -> {
            startActivity(new Intent(this.getContext(), AddDataActivity.class));

        });


        binding.rcvItemCategory.setAdapter(categoryAdapter);
        categoryAdapter.setItems(homeviewModel.getListCategory());
        binding.rcvRecipe.setAdapter(recipeAdapter);
    }
}