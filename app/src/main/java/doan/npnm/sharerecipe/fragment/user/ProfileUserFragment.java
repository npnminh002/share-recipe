package doan.npnm.sharerecipe.fragment.user;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.adapter.RecipeRecentViewAdapter;
import doan.npnm.sharerecipe.adapter.RecipeSaveViewAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.databinding.FragmentProfileUserBinding;
import doan.npnm.sharerecipe.interfaces.OnRecipeEvent;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class ProfileUserFragment extends BaseFragment<FragmentProfileUserBinding> {
    private AppViewModel viewModel;

    public ProfileUserFragment(AppViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void OnClick() {

        binding.icSetting.setOnClickListener(v -> {
            addFragment(new SettingFragment(viewModel), android.R.id.content, true);
        });
    }

    @Override
    protected FragmentProfileUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileUserBinding.inflate(getLayoutInflater());
    }

    ArrayList<RecentView> recentView = new ArrayList<>();
    private RecipeRecentViewAdapter recentViewAdapter;
    private RecipeSaveViewAdapter saveViewAdapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {



        viewModel.users.observe(this, users -> {
            if (!Objects.equals(users.UrlImg, "")) {
                loadImage(users.UrlImg, binding.ImgUser);
            }
            binding.txtEmail.setText(users.Email);
            binding.userName.setText(users.UserName);
            binding.txtRecipe.setText("" + users.Recipe);
            binding.txtFollow.setText("" + users.Follow);
            binding.txtFollower.setText("" + users.Follower);

        });
        recentViewAdapter = new RecipeRecentViewAdapter(new OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {

            }

            @Override
            public void onSave(Recipe recipe) {

            }
        });
        saveViewAdapter = new RecipeSaveViewAdapter(new RecipeSaveViewAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {

            }

            @Override
            public void onRemove(Recipe recipe,int pos) {
                viewModel.database.saveRecipeDao().removeRecent(recipe.Id);
                
                saveViewAdapter.removeItem(pos);
            }
        });

        binding.llFollow.setOnClickListener(v -> {
            addFragment(new FollowerFragment(viewModel, FollowerFragment.FOLLOW.FOLLOW),android.R.id.content,true);
        });

        binding.llFollower.setOnClickListener(v -> {
            addFragment(new FollowerFragment(viewModel, FollowerFragment.FOLLOW.FOLLOWER),android.R.id.content,true);
        });

        binding.rcvRecentView.setAdapter(recentViewAdapter);
        recentViewAdapter.setItems((ArrayList<RecentView>) viewModel.database.recentViewDao().getListRecentView());
        binding.rcvSaveRecipe.setAdapter(saveViewAdapter);
        saveViewAdapter.setItems((ArrayList<SaveRecipe>) viewModel.database.saveRecipeDao().getListRecentView());

    }
}






























