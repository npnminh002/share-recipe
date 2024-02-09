package doan.npnm.sharerecipe.fragment.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.DiscussionAdapter;
import doan.npnm.sharerecipe.adapter.ImageStringAdapter;
import doan.npnm.sharerecipe.adapter.IngridentsAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentDetailRecipeBinding;
import doan.npnm.sharerecipe.dialog.BottomSheetShare;
import doan.npnm.sharerecipe.dialog.BottomSheetShare.OnBottomSheetEvent;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.lib.ImageDownloader;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.disscus.DiscussType;
import doan.npnm.sharerecipe.model.disscus.Discussion;
import doan.npnm.sharerecipe.model.disscus.DisscusAuth;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class DetailRecipeFragment extends BaseFragment<FragmentDetailRecipeBinding> implements OnBottomSheetEvent {
    private Recipe data;
    private AppViewModel viewModel;

    public DetailRecipeFragment(Recipe rcp, AppViewModel vm) {
        super();
        this.viewModel = vm;
        this.data = rcp;
    }

    CallbackManager callbackManager;
    ShareDialog shareDialog;


    @Override
    protected FragmentDetailRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailRecipeBinding.inflate(inflater);
    }

    private DirectionsAdapter directionsAdapter;

    private IngridentsAdapter ingridentsAdapter;

    private ImageStringAdapter adapter;

    private DiscussionAdapter discussionAdapter;

    @Override
    protected void initView() {

        AppEventsLogger.activateApp(this.requireActivity().getApplication());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("Share", "onSuccess: " + result.getPostId());
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                showToast(e.getMessage());
            }

            @Override
            public void onCancel() {

            }
        });


        new Thread(this::listenerDiscussion).start();

        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null);
        binding.rcvDirection.setAdapter(directionsAdapter);
        adapter = new ImageStringAdapter(url -> {

        });

        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null);
        binding.rcvIngrident.setAdapter(ingridentsAdapter);
        binding.rcvGallery.setAdapter(adapter);

        loadImage(data.ImgUrl, binding.imgProduct);
        new Thread(() -> {
            viewModel.getDataFromUserId(data.RecipeAuth.AuthId, new FetchByID<Users>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(Users data) {
                    binding.chefName.setText(data.UserName);
                    binding.recipeCount.setText(data.Recipe + " " + getString(R.string.recipe));
                    binding.circleImageView.loadImage(data.UrlImg);
                }

                @Override
                public void onErr(Object err) {

                }
            });
        }).start();


        binding.foodName.setText(data.Name == null ? "" : data.Name);
        directionsAdapter.setItems(data.Directions);
        ingridentsAdapter.setItems(data.Ingredients);
        adapter.setItems(data.ImagePreview);
        discussionAdapter = new DiscussionAdapter(DiscussType.DISSCUS, new DiscussionAdapter.OnDiscussionEvent() {
            @Override
            public void onReply(Discussion dcs) {
                DetailRecipeFragment.this.discussionReply = dcs;
                DetailRecipeFragment.this.isReply = true;
                binding.llReply.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChangeIcon(Discussion dcs) {

            }
        });
        binding.rcvDiscussion.setAdapter(discussionAdapter);
    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    Discussion discussionReply = null;
    private boolean isReply = false;

    private ArrayList<Discussion> discussions = new ArrayList<>();

    private void listenerDiscussion() {
        viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION)
                .child(data.Id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ArrayList<Discussion> discussions = new ArrayList<>();
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                Discussion dcs = childSnapshot.getValue(Discussion.class);
                                discussions.add(dcs);
                            }
                            discussionAdapter.setItem(discussions);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(DetailRecipeFragment.this));
        binding.icSendDiscuss.setOnClickListener(v -> sendDisscuss());
        binding.llShareRecipe.setOnClickListener(v -> {
            
            BottomSheetShare bottomSheetShare = new BottomSheetShare(this);
            bottomSheetShare.show(requireFragmentManager(), bottomSheetShare.getTag());

        });
    }

    private void shareWithFacebook() {
        loaddingDialog.show();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ArrayList<SharePhoto> sharePhotos = new ArrayList<>();

        ImageDownloader imageDownloaderMain = new ImageDownloader(bitmap -> {
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        });

        imageDownloaderMain.execute(data.ImgUrl);

        for (String url : data.ImagePreview) {
            ImageDownloader imageDownloaderPreview = new ImageDownloader(bitmap -> {
                if (bitmap != null) {
                    bitmaps.add(bitmap);
                    if (bitmaps.size() == data.ImagePreview.size() + 1) {
                        loaddingDialog.dismiss();
                        for (Bitmap bm : bitmaps) {
                            SharePhoto sharePhoto = new SharePhoto.Builder()
                                    .setBitmap(bm)
                                    .build();
                            sharePhotos.add(sharePhoto);
                        }
                        ShareMediaContent.Builder contentBuilder = new ShareMediaContent.Builder();

                        contentBuilder.addMedia(sharePhotos);
                        String content = getContentMedia(data);
                        contentBuilder.setShareHashtag(new ShareHashtag.Builder()
                                .setHashtag(content)
                                .build());
                        ShareContent<ShareMediaContent, ShareMediaContent.Builder> shareContent = contentBuilder.build();

                        shareDialog.show(shareContent);
                    }
                }
            });

            imageDownloaderPreview.execute(url);
        }
    }

    private String getContentMedia(Recipe data) {
        String content = "";

        content += data.Name;

        content += "\n@ " + getString(R.string.ingredients) + "\n";
        for (Ingredients gd : data.Ingredients) {
            content += "   -" + gd.Name + " --" + gd.Quantitative + " g\n";
        }
        content += "\n@ " + getString(R.string.directions) + "\n";
        int i = 0;
        for (Directions gd : data.Directions) {
            i++;
            content += "   -" + getString(R.string.step) + "" + i + ": " + gd.Name + "\n";
        }
        content += "\n#test_do_an";
        content += "\n#recipe_app";
        return content;
    }


    private void sendDisscuss() {
        Users us = viewModel.getUsers().getValue();
        String message = binding.txtDiscuss.getText().toString();
        String id = viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION).
                child(data.Id).push().getKey();
        Discussion discussion = new Discussion() {{
            Id = id;
            DisscusAuth = new DisscusAuth() {{
                AuthId = us.UserID;
                AuthName = us.UserName;
                Address = us.Address;
                Image = us.UrlImg;

            }};
            Time = getTimeNow();
            Content = message;
            DiscussIcon = doan.npnm.sharerecipe.model.disscus.DiscussIcon.NONE;
            DiscussType = doan.npnm.sharerecipe.model.disscus.DiscussType.DISSCUS;
            DiscussionArray = null;
        }};

        if (!isReply) {
            viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION)
                    .child(data.Id).child(id)
                    .setValue(discussion)
                    .addOnSuccessListener(unused -> {
                        showToast("Success");
                        binding.txtDiscuss.setText("");
                    })
                    .addOnFailureListener(e -> {
                        showToast(e.getMessage());
                    });
        } else {
            discussionReply.DiscussionArray.add(discussion);
            viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION)
                    .child(data.Id).child(discussionReply.Id)
                    .child("DiscussionArray")
                    .setValue(discussionReply.DiscussionArray)
                    .addOnSuccessListener(unused -> {
                        showToast("Success");
                        binding.txtDiscuss.setText("");
                        binding.llReply.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        showToast(e.getMessage());
                    });
        }


    }

    @Override
    public void onFaceBook() {
        showToast("Ge;;;");
    }
}