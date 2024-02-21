package doan.npnm.sharerecipe.fragment.user;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
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
import doan.npnm.sharerecipe.database.models.Follower;
import doan.npnm.sharerecipe.databinding.FragmentDetailRecipeBinding;
import doan.npnm.sharerecipe.databinding.PopupReportRecipeBinding;
import doan.npnm.sharerecipe.dialog.BottomSheetShare.OnBottomSheetEvent;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.lib.BitmapUtils;
import doan.npnm.sharerecipe.lib.ImageDownloader;
import doan.npnm.sharerecipe.lib.PopUpDialog;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.disscus.DiscussType;
import doan.npnm.sharerecipe.model.disscus.Discussion;
import doan.npnm.sharerecipe.model.disscus.DisscusAuth;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;
import doan.npnm.sharerecipe.utility.Utils;

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


        viewModel.getDataFromUserId(data.RecipeAuth, new FetchByID<Users>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Users users) {
                if (data != null) {
                    viewModel.firestore.collection(Constant.RECIPE).document(data.Id)
                            .update("View",data.View+1);
                    binding.llAnErr.setVisibility(View.GONE);
                    binding.chefName.setText(users.UserName);
                    binding.recipeCount.setText(users.Recipe + " " + getString(R.string.recipe));
                    binding.circleImageView.loadImage(users.UrlImg);

                    directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null);
                    binding.rcvDirection.setAdapter(directionsAdapter);
                    adapter = new ImageStringAdapter(url -> {

                    });

                    ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null);
                    binding.rcvIngrident.setAdapter(ingridentsAdapter);
                    binding.rcvGallery.setAdapter(adapter);

                    loadImage(data.ImgUrl, binding.imgProduct);


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

            }

            @Override
            public void onErr(Object err) {
                binding.llAnErr.setVisibility(View.VISIBLE);
            }
        });


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
        binding.backIcon2.setOnClickListener(v -> closeFragment(DetailRecipeFragment.this));
        binding.icSendDiscuss.setOnClickListener(v -> sendDisscuss());

        binding.llSaveRecipe.setOnClickListener(v -> viewModel.database.followerDao().addRecentView(new Follower() {{
            AuthID = data.Id;
        }}));

        binding.llReport.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                int locationY = Utils.getHeightPercent(5);
                int locationX = v.getRight() - Utils.getWidthPercent(30);
                PopUpDialog.showPopupMenu(v, PopupReportRecipeBinding::inflate, Utils.getWidthPercent(35), ViewGroup.LayoutParams.WRAP_CONTENT, locationX, locationY, (binding, popup) -> {
                    binding.llShare.setOnClickListener(v2 -> {
                        shareWithFacebook();
                    });
                });
            }
        });
    }

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("LOG", "Permission granted");

            } else {
                Log.v("LOG", "Permission not granted, requesting permission...");
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }
        } else {
            Log.v("LOG", "Permission is granted");

        }
    }


    private void shareWithFacebook() {
        loaddingDialog.show();
        ArrayList<Uri> sharePhotos = new ArrayList<>();

        ImageDownloader imageDownloaderMain = new ImageDownloader(bitmap -> {
            if (bitmap != null) {
                BitmapUtils.getUriFromBitmap(bitmap, sharePhotos::add);
            }
        });

        imageDownloaderMain.execute(data.ImgUrl);

        for (String url : data.ImagePreview) {
            ImageDownloader imageDownloaderPreview = new ImageDownloader(bitmap -> {
                if (bitmap != null) {
                    BitmapUtils.getUriFromBitmap(bitmap, sharePhotos::add);
                    if (sharePhotos.size() == data.ImagePreview.size() + 1) {
                        shareImages(sharePhotos);
                    }
                }
            });

            imageDownloaderPreview.execute(url);
        }
    }

    private static final int SHARE_REQUEST_CODE = 101;
    private void shareImages(ArrayList<Uri> imageUris) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getContentMedia(data));
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);

        try {
            startActivityForResult(Intent.createChooser(shareIntent, "Share Images"), SHARE_REQUEST_CODE);
            loaddingDialog.dismiss();
            viewModel.listDeleted.addAll(imageUris);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "No apps can perform this action", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHARE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(requireContext(), "Sharing attempted", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    for (Uri uri : viewModel.listDeleted) {
                        deleteImage(uri, requireContext());
                    }
                }, 600000);
                viewModel.firestore.collection(Constant.RECIPE).document(this.data.Id)
                        .update("Share",this.data.Share+1);
            } else {
                Toast.makeText(requireContext(), "Sharing cancelled or failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deleteImage(Uri imageUri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(imageUri, null, null);
    }

    private boolean checkAppInstall(String uri) {
        PackageManager pm = requireActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);

        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    private String getContentMedia(Recipe data) {
        String content = "";
        content += data.Name;

        content += "\n@" + getString(R.string.ingredients) + "\n";
        for (Ingredients gd : data.Ingredients) {
            content += "   -" + gd.Name + ": " + gd.Quantitative + " g\n";
        }
        content += "\n@" + getString(R.string.directions) + "\n";
        int i = 0;
        for (Directions gd : data.Directions) {
            i++;
            content += "   -" + getString(R.string.step) + " " + i + ": " + gd.Name + "\n";
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