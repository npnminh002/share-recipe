package doan.npnm.sharerecipe.fragment.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import doan.npnm.sharerecipe.activity.AddDataActivity;
import doan.npnm.sharerecipe.adapter.CategoryAdapter;
import doan.npnm.sharerecipe.adapter.RecipeAdapter;
import doan.npnm.sharerecipe.adapter.TopChefAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
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
    private TopChefAdapter topChefAdapter;

    private RecipeAdapter recipeAdapter;


    @Override
    protected void initView() {

        homeviewModel.recipeLiveData.observe(this,arr->{
            recipeAdapter.setItems(arr);
        });
        topChefAdapter= new TopChefAdapter();
        homeviewModel.recipeAuth.observe(this,arr->{
            topChefAdapter.setItems(arr);
        });
        binding.rcvTopChef.setAdapter(topChefAdapter);

        categoryAdapter = new CategoryAdapter(category -> {

        });

        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {
                if( homeviewModel.database.recentViewDao().checkExistence(rcp.Id)){
                    homeviewModel.database.recentViewDao().removeRecent(rcp.Id);
                }
                homeviewModel.database.recentViewDao().addRecentView(new RecentView(){{
                    AuthID=rcp.RecipeAuth.AuthId;
                    RecipeID=rcp.Id;
                    ViewTime=getTimeNow();
                    Recipe= rcp.toJson();
                }});

                addFragment(new DetailRecipeFragment(rcp,homeviewModel),android.R.id.content,true);
            }

            @Override
            public void onSave(Recipe rcp) {
                if( homeviewModel.database.saveRecipeDao().checkExistence(rcp.Id)){
                    homeviewModel.database.saveRecipeDao().removeRecent(rcp.Id);
                }
                homeviewModel.database.saveRecipeDao().addRecentView(new SaveRecipe(){{
                    AuthID=rcp.RecipeAuth.AuthId;
                    RecipeID=rcp.Id;
                    SaveTime=getTimeNow();
                    Recipe= rcp.toJson();
                }});
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