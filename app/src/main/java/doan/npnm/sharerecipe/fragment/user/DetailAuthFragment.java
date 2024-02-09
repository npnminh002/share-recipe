package doan.npnm.sharerecipe.fragment.user;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.RecipeAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.Follower;
import doan.npnm.sharerecipe.databinding.FragmentDetailAuthBinding;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class DetailAuthFragment extends BaseFragment<FragmentDetailAuthBinding> {

    private AppViewModel viewModel;
    private Users users;
    private Users authLogin;

    public DetailAuthFragment(AppViewModel viewModel, Users idAuth) {
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
        authLogin = viewModel.users.getValue();
        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {

            }

            @Override
            public void onSave(Recipe recipe) {

            }
        });
        binding.rcvRecipe.setAdapter(recipeAdapter);

        if (!Objects.equals(users.UrlImg, "")) {
            loadImage(users.UrlImg, binding.ImgUser);
        }
        binding.txtEmail.setText(users.Email);
        binding.userName.setText(users.UserName);
        binding.txtRecipe.setText("" + users.Recipe);
        binding.txtFollow.setText("" + users.Follow);
        binding.txtFollower.setText("" + users.Follower);

        viewModel.checkFollowByUid(users.UserID);
        viewModel.isFollow.observe(this,isFollow->{
            binding.btnFollow.setText(getString(isFollow ? R.string.un_follow : R.string.follow));
        });


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
                        if (Objects.equals(recipe.RecipeAuth.AuthId, users.UserID)) {
                            recipes.add(recipe);
                        }

                        Log.d("TESTRCP", "onFetchRecipeyUs: " + "USID:" + users.UserID + "  RECIPE: " + recipe.RecipeAuth.toJson());
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
            if (viewModel.isFollow.getValue()) {
                viewModel.database.followerDao().removeRecent(users.UserID);
                new Thread(() -> {
                    viewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                            .child(authLogin.UserID)
                            .child("YouFollow")
                            .child(users.UserID)
                            .removeValue();
                    viewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                            .child(users.UserID)
                            .child("OtherFollow")
                            .child(authLogin.UserID)
                            .removeValue();
                }).start();


            } else {
                viewModel.database.followerDao().addRecentView(new Follower() {{
                    AuthID = users.UserID;
                }});
                new Thread(() -> {
                    viewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                            .child(authLogin.UserID)
                            .child("YouFollow")
                            .child(users.UserID)
                            .setValue(users.toJson());
                    viewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                            .child(users.UserID)
                            .child("OtherFollow")
                            .child(authLogin.UserID)
                            .setValue(authLogin.toJson());
                }).start();
            }
            viewModel.checkFollowByUid(users.UserID);

        });
    }
}
