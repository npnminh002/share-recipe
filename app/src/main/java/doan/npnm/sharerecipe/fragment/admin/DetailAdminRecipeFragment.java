package doan.npnm.sharerecipe.fragment.admin;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.admin.DiscussionTableAdapter;
import doan.npnm.sharerecipe.adapter.users.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.users.ImageStringAdapter;
import doan.npnm.sharerecipe.adapter.users.IngridentsAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.Follower;
import doan.npnm.sharerecipe.databinding.FragmentDetailAdminRecipeBinding;
import doan.npnm.sharerecipe.fragment.publics.ImagePreviewFragment;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.disscus.Discussion;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class DetailAdminRecipeFragment extends BaseFragment<FragmentDetailAdminRecipeBinding> {
    private Recipe data;
    private AdminViewModel viewModel;

    public DetailAdminRecipeFragment(Recipe rcp, AdminViewModel vm) {
        super();
        this.viewModel = vm;
        this.data = rcp;
    }

    CallbackManager callbackManager;
    ShareDialog shareDialog;


    @Override
    protected FragmentDetailAdminRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailAdminRecipeBinding.inflate(inflater);
    }

    private DirectionsAdapter directionsAdapter;
    private IngridentsAdapter ingridentsAdapter;
    private ImageStringAdapter adapter;
    @Override
    protected void initView() {
        listenerDiscussion();
        loaddingDialog.show();
        viewModel.getDataFromUserId(data.RecipeAuth, new FetchByID<Users>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Users users) {
                if (data != null) {
                    viewModel.firestore.collection(Constant.RECIPE).document(data.Id)
                            .update("View", data.View + 1);
                    binding.llAnErr.setVisibility(View.GONE);
                    loadImage(data.ImgUrl, binding.imgProduct);
                    binding.userName.setText(users.UserName);
                    binding.txtRecipe.setText(users.Recipe + " " + getString(R.string.recipe));
                    binding.imgUsers.loadImage(users.UrlImg);
                    directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.ADMIN, null);
                    binding.rcvDirection.setAdapter(directionsAdapter);
                    adapter = new ImageStringAdapter(url -> {
                        binding.imgProduct.setOnClickListener(v -> replaceFragment(ImagePreviewFragment.newInstance(url), 0, true));
                    });
                    binding.txtCookTime.setText(data.CookTime.Time + " " + (data.CookTime.TimeType.equals("s") ? "second" : Objects.equals(data.CookTime.TimeType, "m") ? "minute" : "hour"));
                    binding.txtPrepareTime.setText(data.PrepareTime.Time + " " + (data.PrepareTime.TimeType.equals("s") ? "second" : Objects.equals(data.PrepareTime.TimeType, "m") ? "minute" : "hour"));
                    binding.txtDifficult.setText(data.Level);
                    ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.ADMIN, null);
                    binding.rcvIngrident.setAdapter(ingridentsAdapter);
                    binding.rcvGallery.setAdapter(adapter);

                    binding.foodName.setText(data.Name == null ? "" : data.Name);
                    directionsAdapter.setItems(data.Directions);
                    ingridentsAdapter.setItems(data.Ingredients);
                    adapter.setItems(data.ImagePreview);

                    String history="";
                    for (String s:data.History){
                        history+="\n"+s;
                    }
                    binding.txtHistory.setText(history);

                    new Handler(Looper.myLooper()).postDelayed(()->{
                        loaddingDialog.dismiss();

                    },1500);
                }


            }
            @Override
            public void onErr(Object err) {
                binding.llAnErr.setVisibility(View.VISIBLE);
                loaddingDialog.dismiss();
            }
        });
    }

    private void listenerDiscussion() {
        viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION)
                .child(data.Id)
                .get().addOnSuccessListener(dataSnapshot -> {
                    ArrayList<Discussion> discussions = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Discussion dcs = childSnapshot.getValue(Discussion.class);
                        discussions.add(dcs);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        new DiscussionTableAdapter(binding.tableLayout, disscusAuth -> {

                        }).onFinih(() -> binding.progessLoad.setVisibility(View.GONE)).setData(discussions);
                    }
                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
    }

    @Override
    public void OnClick() {
        binding.backIcon2.setOnClickListener(v -> closeFragment(DetailAdminRecipeFragment.this));
        binding.llSaveRecipe.setOnClickListener(v -> viewModel.database.followerDao().addRecentView(new Follower() {{
            AuthID = data.Id;
        }}));

        binding.imgProduct.setOnClickListener(v -> replaceFragment(ImagePreviewFragment.newInstance(data.ImgUrl), 0, true));
    }

}
