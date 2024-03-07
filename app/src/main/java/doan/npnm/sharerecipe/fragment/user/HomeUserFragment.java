package doan.npnm.sharerecipe.fragment.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.CategoryAdapter;
import doan.npnm.sharerecipe.adapter.users.RecipeAdapter;
import doan.npnm.sharerecipe.adapter.users.TopChefAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.lib.widget.TextValue;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.databinding.FragmentHomeUserBinding;

public class HomeUserFragment extends BaseFragment<FragmentHomeUserBinding> {

    public UserViewModel homeviewModel;

    public HomeUserFragment(UserViewModel userViewModel) {
        this.homeviewModel = userViewModel;
    }

    @Override
    public void OnClick() {
        binding.icSearch.setOnClickListener(v -> {
            if (searchKey.value().isEmpty()){
                searchKey.onError();
            }else {
                homeviewModel.searchKey.postValue(searchKey.value());
            }
        });
    }
    @Override
    protected FragmentHomeUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeUserBinding.inflate(getLayoutInflater());
    }

    private TextValue searchKey;

    private CategoryAdapter categoryAdapter;
    private TopChefAdapter topChefAdapter;

    private RecipeAdapter recipeAdapter;


    @Override
    protected void initView() {

        searchKey= new TextValue(binding.txtSearch);



        homeviewModel.recipeLiveData.observe(this, arr -> {
            recipeAdapter.setItems(arr);
        });

        homeviewModel.categoriesArr.observe(this,data->{
            categoryAdapter.setItems(data);
        });

        topChefAdapter = new TopChefAdapter(us -> {
            addFragment(new DetailAuthFragment(homeviewModel, us), android.R.id.content, true);
        });
        homeviewModel.recipeAuth.observe(this, arr -> {
            topChefAdapter.setItems(arr);
        });
        binding.rcvTopChef.setAdapter(topChefAdapter);

        categoryAdapter = new CategoryAdapter(category -> {
            homeviewModel.searchKey.postValue(category.Id);
        });

        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {
                if (homeviewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                    homeviewModel.database.recentViewDao().removeRecent(rcp.Id);
                }
                homeviewModel.database.recentViewDao().addRecentView(new RecentView() {{
                    AuthID = rcp.RecipeAuth;
                    RecipeID = rcp.Id;
                    ViewTime = getTimeNow();
                    Recipe = rcp.toJson();
                }});

                addFragment(new DetailRecipeFragment(rcp, homeviewModel), android.R.id.content, true);
            }

            @Override
            public void onLove(Recipe rcp, boolean isLove) {
                if (homeviewModel.auth.getCurrentUser() == null) {
                    showToast(getString(R.string.no_us));
                } else {
                    if (!isLove) {
                        homeviewModel.onLoveRecipe(rcp);
                    } else {
                        homeviewModel.onUnlove(rcp);
                    }
                }
            }

        }, homeviewModel.database);
//        binding.imgUsers.setOnClickListener(v -> {
//            startActivity(new Intent(this.getContext(), AddDataActivity.class));
//
//        });

        homeviewModel.onChangeLove.observe(this,data->{
            categoryAdapter.setCurrentPos(homeviewModel.recipeLiveData.getValue().indexOf(data));
        });
        binding.rcvItemCategory.setAdapter(categoryAdapter);
        homeviewModel.categoriesArr.observe(this,data->{
            categoryAdapter.setItems(data);
        });
        binding.rcvRecipe.setAdapter(recipeAdapter);
    }
}