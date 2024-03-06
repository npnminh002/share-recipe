package doan.npnm.sharerecipe.fragment.user;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.RecipeAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.databinding.FragmentDetailAuthBinding;
import doan.npnm.sharerecipe.dialog.NoUserDialog;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class DetailAuthFragment extends BaseFragment<FragmentDetailAuthBinding> {

    private UserViewModel viewModel;
    private Users users;


    public DetailAuthFragment(UserViewModel viewModel, Users idAuth) {
        this.viewModel = viewModel;
        this.users = idAuth;
    }

    @Override
    protected FragmentDetailAuthBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailAuthBinding.inflate(getLayoutInflater());
    }


    RecipeAdapter recipeAdapter;

    @Override
    protected void initView() {
        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
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
            public void onLove(Recipe rcp, boolean isLove) {
                if (viewModel.auth.getCurrentUser() == null) {
                    showToast(getString(R.string.no_us));
                } else {
                    if (!isLove) {
                        viewModel.onLoveRecipe(rcp);
                    } else {
                        viewModel.onUnlove(rcp);
                    }
                }
            }
        }, viewModel.database);
        binding.rcvRecipe.setAdapter(recipeAdapter);

        if (!Objects.equals(users.UrlImg, "")) {
            loadImage(users.UrlImg, binding.ImgUser);
        }
        binding.txtEmail.setText(users.Email);
        binding.userName.setText(users.UserName);
        binding.txtRecipe.setText("" + users.Recipe);
        binding.txtFollow.setText("" + users.Follow);
        binding.txtFollower.setText("" + users.Follower);

        if (viewModel.auth.getCurrentUser() != null) {
            viewModel.checkFollowByUid(users.UserID);
            viewModel.isFollow.observe(this, isFollow -> {
                binding.btnFollow.setText(getString(isFollow ? R.string.un_follow : R.string.follow));
            });
        }

        onFetchRecipeyUs(new FetchByID<ArrayList<Recipe>>() {
            @Override
            public void onSuccess(ArrayList<Recipe> data) {
                recipeAdapter.setItems(data);
            }

            @Override
            public void onErr(Object err) {
                showToast(err);
            }
        });
    }

    private void onFetchRecipeyUs(FetchByID<ArrayList<Recipe>> arrayListFetchByID) {
        binding.prLoadData.setVisibility(View.VISIBLE);
        ArrayList<Recipe> recipes = new ArrayList<>();
        viewModel.firestore.collection(Constant.RECIPE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        if (Objects.equals(recipe.RecipeAuth, users.UserID)) {
                            recipes.add(recipe);
                        }

                        Log.d("TESTRCP", "onFetchRecipeyUs: " + "USID:" + users.UserID + "  RECIPE: " + recipe.RecipeAuth);
                    }
                    if (!recipes.isEmpty()) {
                        arrayListFetchByID.onSuccess(recipes);
                    }
                    binding.prLoadData.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    arrayListFetchByID.onErr(e.getMessage());
                    binding.prLoadData.setVisibility(View.GONE);
                });
    }

    @Override
    public void OnClick() {
        binding.backIcon2.setOnClickListener(v -> {
            closeFragment(DetailAuthFragment.this);
        });
        binding.btnFollow.setOnClickListener(v -> {
            if (viewModel.auth.getCurrentUser() == null) {
                new NoUserDialog(requireContext(), () -> {
                    viewModel.isSingApp.postValue(true);
                }).show();
            } else {
                if (viewModel.isFollow.getValue()) {
                    binding.txtFollower.setText("" + (users.Follower - 1));
                    viewModel.onUnFollow(users);

                } else {
                    viewModel.onFollow(users);
                    binding.txtFollower.setText("" + (users.Follower + 1));
                }
                viewModel.checkFollowByUid(users.UserID);
            }
        });


    }
}
