package doan.npnm.sharerecipe.fragment.admin;

import android.content.Intent;
import android.os.Message;
import android.se.omapi.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.RecipeAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.databinding.FragmentDetailAuthAdminBinding;
import doan.npnm.sharerecipe.databinding.FragmentDetailAuthBinding;
import doan.npnm.sharerecipe.dialog.NoUserDialog;
import doan.npnm.sharerecipe.fragment.user.DetailRecipeFragment;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class DetailAuthAdminFragment extends BaseFragment<FragmentDetailAuthAdminBinding> {

    private AdminViewModel viewModel;
    private Users users;


    public DetailAuthAdminFragment(AdminViewModel viewModel, Users idAuth) {
        this.viewModel = viewModel;
        this.users = idAuth;
    }

    @Override
    protected FragmentDetailAuthAdminBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailAuthAdminBinding.inflate(getLayoutInflater());
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

                addFragment(new DetailAdminRecipeFragment(rcp, viewModel), android.R.id.content, true);
            }

            @Override
            public void onLove(Recipe rcp, boolean isLove) {
            }
        }, viewModel.database);
        binding.rcvRecipe.setAdapter(recipeAdapter);

        if (!Objects.equals(users.UrlImg, "")) {
            loadImage(users.UrlImg, binding.ImgUser);
        }
        binding.userId.setText("Id: "+users.UserID);
        binding.txtEmail.setText("Email: "+users.Email);
        binding.txtNickname.setText("#"+users.NickName);
        binding.userName.setText("Name: "+users.UserName);
        binding.txtRecipe.setText("" + users.Recipe);
        binding.txtFollow.setText("" + users.Follow);
        binding.txtFollower.setText("" + users.Follower);
        binding.txtimeLastLog.setText(""+formatDateString(users.TimeLog));
        binding.txtAddress.setText("Address: "+users.Address);
        binding.txtGender.setText("Gender: "+users.Gender);
        String hts= "";
        for (String s:users.History){
            hts+=s+"\n";
        }
        binding.txtHistory.setText(hts);

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
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(DetailAuthAdminFragment.this);
        });

        binding.btnDelete.setOnClickListener(v -> {


        });
    }
}
