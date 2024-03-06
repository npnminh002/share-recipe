package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.users.ImageRecipeAdapter;
import doan.npnm.sharerecipe.adapter.users.IngridentsAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.interfaces.DataEventListener;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.notification.Notification;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;
import doan.npnm.sharerecipe.databinding.FragmentPreviewRecipeBinding;
import doan.npnm.sharerecipe.firebase.FCMNotificationSender;

public class PreviewRecipeFragment extends BaseFragment<FragmentPreviewRecipeBinding> {

    private UserViewModel viewModel;
    private RecipeViewModel recipeViewModel;

    public PreviewRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
    }

    private Recipe recipe;

    @Override
    protected FragmentPreviewRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPreviewRecipeBinding.inflate(getLayoutInflater());
    }

    private DirectionsAdapter directionsAdapter;

    private IngridentsAdapter ingridentsAdapter;

    private ImageRecipeAdapter adapter;

    @Override
    protected void initView() {
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(PreviewRecipeFragment.this);
        });

        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null);
        binding.rcvDirection.setAdapter(directionsAdapter);
        adapter = new ImageRecipeAdapter(null);

        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null);
        binding.rcvIngrident.setAdapter(ingridentsAdapter);
        binding.listImg.setAdapter(adapter);

        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            if (recipeViewModel.imgUri != null) {
                loadImage(recipeViewModel.imgUri, binding.imgProduct);
            } else {
                loadImage(data.ImgUrl, binding.imgProduct);
            }
            binding.nameOfRecipe.setText(data.Name == null ? "" : data.Name);
            binding.description.setText(data.Description == null ? "" : data.Description);
            binding.timePrepare.setText(data.PrepareTime.Time);
            binding.selectMinutePP.setText(data.PrepareTime.TimeType);
            binding.timeCook.setText(data.CookTime.Time);
            binding.selectMinutePP.setText(data.CookTime.TimeType);
            binding.txtLever.setText(data.Level);

            directionsAdapter.setItems(data.Directions);
            ingridentsAdapter.setItems(data.Ingredients);
        });
        recipeViewModel.listSelect.observe(this, data -> {
            int indexNull = data.indexOf(null);
            if (indexNull >= 0) {
                data.remove(indexNull);
            }
            adapter.setItems(data);
        });

    }


    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(PreviewRecipeFragment.this));
        binding.btnCancel.setOnClickListener(v -> closeFragment(PreviewRecipeFragment.this));
        binding.btnNext.setOnClickListener(v -> {
            loaddingDialog.show();
            startUploadData(new OnAddImageSuccess() {
                @Override
                public void onAddSuccess(String docID, String img, ArrayList<String> listUrl) {
                    recipe.ImagePreview = listUrl;
                    recipe.Id = docID;
                    recipe.RecipeAuth = viewModel.auth.getCurrentUser().getUid();
                    recipeViewModel.recipeLiveData.postValue(recipe);
                    putDataRecipe(recipeViewModel.recipeLiveData.getValue());
                }
            });
        });
    }

    private interface OnAddImageSuccess {
        void onAddSuccess(String docID, String img, ArrayList<String> listUrl);
    }

    private void startUploadData(OnAddImageSuccess onAddImageSuccess) {
        String firestoreID = firestore.collection(Constant.RECIPE).document().getId();
        StorageReference reference = storage.getReference(Constant.RECIPE).child(firestoreID);
        ArrayList<String> listUrl = new ArrayList<>();

        viewModel.putImgToStorage(reference, recipeViewModel.imgUri, new UserViewModel.OnPutImageListener() {
            @Override
            public void onComplete(String urlApp) {
                recipe.ImgUrl = urlApp;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    recipeViewModel.listSelect.getValue().forEach(uri -> {
                        viewModel.putImgToStorage(reference, uri, new UserViewModel.OnPutImageListener() {
                            @Override
                            public void onComplete(String url) {
                                listUrl.add(url);
                                if (listUrl.size() == recipeViewModel.listSelect.getValue().size()) {
                                    onAddImageSuccess.onAddSuccess(firestoreID, urlApp, listUrl);
                                }
                            }

                            @Override
                            public void onFailure(String mess) {
                                showToast(mess);
                                loaddingDialog.dismiss();
                            }
                        });
                    });
                }
            }

            @Override
            public void onFailure(String mess) {
                showToast(mess);
                loaddingDialog.dismiss();
            }
        });

    }

    private void putDataRecipe(Recipe value) {
        recipe.History = new ArrayList<String>() {{
            add("Time Add" + getTimeNow());
        }};
        firestore.collection(Constant.RECIPE)
                .document(value.Id)
                .set(recipe).addOnSuccessListener(task -> {
                    showToast("Add data success full");
                    loaddingDialog.dismiss();
                    viewModel.isAddRecipe.postValue(true);
                    postNotification(recipe);

                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                    loaddingDialog.dismiss();
                });
    }

    private void postNotification(Recipe recipe) {
        DatabaseReference youfirebaseDatabase = viewModel.fbDatabase.getReference(Constant.NOTIFICATION)
                .child(recipe.RecipeAuth).push();


        String id = youfirebaseDatabase.getKey();
        Notification yourNotification = new Notification() {{
            Id = id;
            Content = getString(R.string.add_success);
            Time = getTimeNow();
            AuthID = recipe.RecipeAuth;
            IsView = false;
            Value = recipe.Id;
            NotyType = doan.npnm.sharerecipe.model.notification.NotyType.USER_ADD;
        }};

        sendNotiById(viewModel.users.getValue().Token,true);
        Notification followNotification = new Notification() {{
            Id = id;
            Content = viewModel.users.getValue().UserName + " " + getString(R.string.friend_add_recipe);
            Time = getTimeNow();
            AuthID = recipe.RecipeAuth;
            IsView = false;
            Value = recipe.Id;
            NotyType = doan.npnm.sharerecipe.model.notification.NotyType.USER_ADD;
        }};
        youfirebaseDatabase.setValue(yourNotification);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            viewModel.database.userFollowerDao().getDataList().forEach(userFollower -> {
                DatabaseReference otherfirebaseDatabase = viewModel.fbDatabase.getReference(Constant.NOTIFICATION)
                        .child(userFollower.AuthID).push();
                followNotification.Id = otherfirebaseDatabase.getKey();
                otherfirebaseDatabase.setValue(followNotification);

                firestore.collection(Constant.KEY_USER)
                        .document(userFollower.AuthID)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Users us= documentSnapshot.toObject(Users.class);
                            sendNotiById(us.Token,false);
                        }).addOnFailureListener(e -> {

                        });


            });
        }

    }

    private void sendNotiById(String id,boolean isUser) {
        FCMNotificationSender.sendNotiAddRecipe(id,isUser?null :viewModel.users.getValue(),recipe, new DataEventListener<String>() {
            @Override
            public void onSuccess(String data) {
                Log.d("TAG", "onSuccess" + data);
            }

            @Override
            public void onErr(Object err) {
                showToast("Error:" + err);

            }
        });
    }
}



















