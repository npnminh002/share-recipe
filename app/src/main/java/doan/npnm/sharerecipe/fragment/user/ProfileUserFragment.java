package doan.npnm.sharerecipe.fragment.user;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.MyRecipeViewAdapter;
import doan.npnm.sharerecipe.adapter.users.RecipeRecentViewAdapter;
import doan.npnm.sharerecipe.adapter.users.RecipeSaveViewAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.LoveRecipe;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.databinding.FragmentProfileUserBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.fragment.user.addrecipe.FirstRecipeFragment;
import doan.npnm.sharerecipe.fragment.user.addrecipe.ThirdRecipeFragment;
import doan.npnm.sharerecipe.interfaces.OnRecipeEvent;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class ProfileUserFragment extends BaseFragment<FragmentProfileUserBinding> {
    private UserViewModel viewModel;
    private RecipeViewModel recipeViewModel;

    public ProfileUserFragment(UserViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void OnClick() {

        binding.icSetting.setOnClickListener(v -> {
            addFragment(new SettingFragment(viewModel), android.R.id.content, true);
        });

        binding.signInApp.setOnClickListener(v -> {
            viewModel.isSingApp.postValue(true);
        });

        binding.icAddRecipe.setOnClickListener(v -> {
            recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
            recipeViewModel.recipeLiveData.postValue(new Recipe());
            addFragment(new FirstRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
        });
    }

    @Override
    protected FragmentProfileUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileUserBinding.inflate(getLayoutInflater());
    }

    private RecipeRecentViewAdapter recentViewAdapter;
    private RecipeSaveViewAdapter saveViewAdapter;

    private MyRecipeViewAdapter myViewAdapter;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {

        if (viewModel.users.getValue() == null) {
            binding.llProfile.setVisibility(View.GONE);
            binding.llNoProfile.setVisibility(View.VISIBLE);

        } else {

            binding.llProfile.setVisibility(View.VISIBLE);
            binding.llNoProfile.setVisibility(View.GONE);
            viewModel.users.observe(this, users -> {
                if (!Objects.equals(users.UrlImg, "")) {
                    loadImage(users.UrlImg, binding.ImgUser);
                }
                binding.txtEmail.setText(users.Email);
                binding.userName.setText(users.UserName);
                binding.txtRecipe.setText("" + users.Recipe);
                binding.txtFollow.setText("" + users.Follow);
                binding.txtFollower.setText("" + users.Follower);
                binding.txtNickName.setText("#" + users.NickName);

            });


            recentViewAdapter = new RecipeRecentViewAdapter(new OnRecipeEvent() {
                @Override
                public void onView(Recipe rcp) {
                    if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                        viewModel.database.recentViewDao().removeRecent(rcp.Id);
                    }
                    viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                        AuthID = rcp.RecipeAuth;
                        RecipeID = rcp.Id;
                        ViewTime = getTimeNow();
                        Recipe = rcp.toJson();
                    }});

                    addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
                }

                @Override
                public void onSave(Recipe rcp) {
                    if (viewModel.database.saveRecipeDao().checkExistence(rcp.Id)) {
                        viewModel.database.saveRecipeDao().removeRecent(rcp.Id);
                    }
                    viewModel.database.saveRecipeDao().addRecentView(new SaveRecipe() {{
                        AuthID = rcp.RecipeAuth;
                        RecipeID = rcp.Id;
                        SaveTime = getTimeNow();
                        Recipe = rcp.toJson();
                    }});
                }
            });
            saveViewAdapter = new RecipeSaveViewAdapter(new RecipeSaveViewAdapter.OnRecipeEvent() {
                @Override
                public void onView(Recipe rcp) {
                    if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                        viewModel.database.recentViewDao().removeRecent(rcp.Id);
                    }
                    viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                        AuthID = rcp.RecipeAuth;
                        RecipeID = rcp.Id;
                        ViewTime = getTimeNow();
                        Recipe = rcp.toJson();
                    }});

                    addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
                }

                @Override
                public void onRemove(Recipe recipe, int pos) {
                    viewModel.database.saveRecipeDao().removeRecent(recipe.Id);

                    saveViewAdapter.removeItem(pos);
                }
            });


            myViewAdapter = new MyRecipeViewAdapter(new MyRecipeViewAdapter.OnRecipeEvent() {
                @Override
                public void onView(Recipe rcp) {
                    addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
                }

                @Override
                public void onRemove(Recipe recipe, int pos) {
                    new ConfirmDialog(ProfileUserFragment.this.requireContext(), getString(R.string.cf_delete), () -> {

                    }).show();
                }

                @Override
                public void onEdit(Recipe recipe) {
                    replaceFullViewFragment(new EditRecipeFragment(viewModel, recipe), android.R.id.content, true);
                }
            });


            binding.llFollow.setOnClickListener(v -> {
                addFragment(new FollowerFragment(viewModel, FollowerFragment.FOLLOW.FOLLOW), android.R.id.content, true);
            });

            binding.llFollower.setOnClickListener(v -> {
                addFragment(new FollowerFragment(viewModel, FollowerFragment.FOLLOW.FOLLOWER), android.R.id.content, true);
            });

            binding.rcvRecentView.setAdapter(recentViewAdapter);
            ArrayList<RecentView> recentViews = (ArrayList<RecentView>) viewModel.database.recentViewDao().getListRecentView();
            if (recentViews.isEmpty()) {
                binding.txtNo1.setVisibility(View.VISIBLE);
            }
            recentViewAdapter.setItems(recentViews);

            ArrayList<LoveRecipe> loveRecipes = (ArrayList<LoveRecipe>) viewModel.database.loveRecipeDao().getLoveArr();
            if (loveRecipes.isEmpty()) {
                binding.txtNo2.setVisibility(View.VISIBLE);
            }
            binding.rcvSaveRecipe.setAdapter(saveViewAdapter);
            saveViewAdapter.setItems(loveRecipes);

            viewModel.myRecipeArr.observe(this,data->{
                if(data.isEmpty()){
                    binding.txtNo3.setVisibility(View.VISIBLE);
                }
                else {
                    binding.txtNo3.setVisibility(View.GONE);
                    binding.txtRecipe.setText(""+data.size());
                    myViewAdapter.setItems(data);
                }
            });

            showToast(viewModel.database.loveRecipeDao().getLoveArr().size());
            binding.rcvMyRecipe.setAdapter(myViewAdapter);

        }
    }
}






























