package doan.npnm.sharerecipe.fragment.user;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import java.util.Collections;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.users.DiscussionAdapter;
import doan.npnm.sharerecipe.adapter.users.ImageStringAdapter;
import doan.npnm.sharerecipe.adapter.users.IngridentsAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.databinding.FragmentDetailRecipeBinding;
import doan.npnm.sharerecipe.databinding.PopupReportRecipeBinding;
import doan.npnm.sharerecipe.dialog.NoUserDialog;
import doan.npnm.sharerecipe.dialog.WarningDialog;
import doan.npnm.sharerecipe.interfaces.DataEventListener;
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

public class DetailRecipeFragment extends BaseFragment<FragmentDetailRecipeBinding> {
    private Recipe data;
    private UserViewModel viewModel;

    public DetailRecipeFragment(Recipe rcp, UserViewModel vm) {
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
        // Khởi tạo các thành phần liên quan đến Facebook
        AppEventsLogger.activateApp(this.requireActivity().getApplication()); // Kích hoạt logger sự kiện Facebook
        callbackManager = CallbackManager.Factory.create(); // Tạo callback manager cho chia sẻ Facebook
        shareDialog = new ShareDialog(this); // Tạo dialog chia sẻ Facebook
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

        // Bắt đầu lắng nghe các sự kiện thảo luận trong một luồng riêng
        new Thread(this::listenerDiscussion).start();

        // Lấy dữ liệu người dùng dựa trên ID công thức
        viewModel.getDataFromUserId(data.RecipeAuth, new FetchByID<Users>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Users users) {
                if (data != null) {
                    // Cập nhật lượt xem công thức trên Firestore
                    viewModel.firestore.collection(Constant.RECIPE).document(data.Id).update("View", data.View + 1);
                    binding.llAnErr.setVisibility(View.GONE); // Ẩn LinearLayout báo lỗi

                    // Hiển thị thông tin đầu bếp
                    binding.chefName.setText(users.UserName);
                    binding.recipeCount.setText(users.Recipe + " " + getString(R.string.recipe));
                    binding.circleImageView.loadImage(users.UrlImg == "" ? R.drawable.img_demo_user : users.UrlImg);

                    // Thiết lập các bộ chuyển đổi RecyclerView
                    directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null); // Cho hướng dẫn
                    binding.rcvDirection.setAdapter(directionsAdapter);
                    adapter = new ImageStringAdapter(url -> { /* Xử lý click vào ảnh */ });
                    ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null); // Cho thành phần
                    binding.rcvIngrident.setAdapter(ingridentsAdapter);
                    binding.rcvGallery.setAdapter(adapter); // Cho ảnh

                    // Hiển thị ảnh và tên công thức
                    loadImage(data.ImgUrl, binding.imgProduct);
                    binding.foodName.setText(data.Name == null ? "" : data.Name);

                    // Hiển thị thời gian nấu
                    binding.timeCook.setText(data.CookTime.Time + " " + (data.CookTime.TimeType.equals("s") ? "giây" : Objects.equals(data.CookTime.TimeType, "m") ? "phút" : "giờ"));

                    // Hiển thị số lượng thành phần
                    binding.txtIngrident.setText("" + data.Ingredients.size() + " " + getString(R.string.ingredients));

                    // Sắp xếp và đưa dữ liệu vào các bộ chuyển đổi RecyclerView
                    Collections.sort(data.Directions, ((o1, o2) -> String.valueOf(o1.Id).compareTo(String.valueOf(o2.Id))));
                    Collections.sort(data.Ingredients, ((o1, o2) -> String.valueOf(o1.Id).compareTo(String.valueOf(o2.Id))));
                    directionsAdapter.setItems(data.Directions);
                    ingridentsAdapter.setItems(data.Ingredients);
                    adapter.setItems(data.ImagePreview);

                    // Thiết lập và kết nối bộ chuyển đổi cho thảo luận
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
                        // Xử lý sự kiện trả lời và thay đổi icon trong thảo luận
                    });
                    binding.rcvDiscussion.setAdapter(discussionAdapter);
                }
            }

            @Override
            public void onErr(Object err) {
                // Hiển thị LinearLayout báo lỗi
                binding.llAnErr.setVisibility(View.VISIBLE);
            }
        });
    }


    Discussion discussionReply = null;
    private boolean isReply = false;

    private ArrayList<Discussion> discussions = new ArrayList<>();

    private void listenerDiscussion() {
        // Lắng nghe sự kiện từ Firebase Database
        viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION).child(data.Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Kiểm tra xem dữ liệu có tồn tại không
                if (snapshot.exists()) {
                    // Khởi tạo một ArrayList để lưu trữ các thảo luận
                    ArrayList<Discussion> discussions = new ArrayList<>();
                    // Duyệt qua từng nút con của snapshot
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        // Lấy dữ liệu của mỗi thảo luận và thêm vào ArrayList
                        Discussion dcs = childSnapshot.getValue(Discussion.class);
                        discussions.add(dcs);
                    }
                    // Kiểm tra xem adapter của thảo luận có tồn tại không
                    if (discussionAdapter != null) {
                        // Cập nhật dữ liệu mới vào adapter
                        discussionAdapter.setItem(discussions);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong việc truy xuất dữ liệu từ Firebase Database
            }
        });
    }


    @Override
    public void OnClick() {

        Users us = viewModel.users.getValue();

// Thay đổi màu của biểu tượng "yêu thích" dựa trên việc kiểm tra xem công thức này đã được yêu thích hay chưa
        binding.llLove.setColorFilter(viewModel.database.loveRecipeDao().checkExist(data.Id) ? Color.parseColor("#FF0000") : Color.parseColor("#ffffff"));
        binding.llLove.setOnClickListener(v -> {
            // Kiểm tra xem người dùng đã đăng nhập chưa
            if (viewModel.auth.getCurrentUser() == null) {
                showToast(getString(R.string.no_us));
            } else {
                // Nếu công thức chưa được yêu thích, thực hiện thao tác yêu thích
                if (!viewModel.database.loveRecipeDao().checkExist(data.Id)) {
                    viewModel.onLoveRecipe(data);
                } else { // Ngược lại, thực hiện thao tác bỏ yêu thích
                    viewModel.onUnlove(data);
                }
            }
            // Cập nhật lại màu của biểu tượng "yêu thích" sau khi thực hiện thao tác
            binding.llLove.setColorFilter(viewModel.database.loveRecipeDao().checkExist(data.Id) ? Color.parseColor("#FF0000") : Color.parseColor("#ffffff"));
        });

// Bắt sự kiện khi người dùng bấm nút "quay lại"
        binding.backIcon2.setOnClickListener(v -> closeFragment(DetailRecipeFragment.this));

// Bắt sự kiện khi người dùng bấm nút "gửi phản hồi"
        binding.icSendDiscuss.setOnClickListener(v -> {
            // Kiểm tra xem người dùng đã đăng nhập hay chưa
            if (us != null) {
                sendDisscuss();
            } else {
                // Hiển thị thông báo khi người dùng chưa đăng nhập
                new NoUserDialog(requireContext(), () -> {
                    viewModel.isSingApp.postValue(true);
                }).show();
            }
        });

// Bắt sự kiện khi người dùng bấm nút "lưu công thức"
        binding.llSaveRecipe.setOnClickListener(v -> {
            // Kiểm tra xem người dùng đã đăng nhập hay chưa
            if (us != null) {
                // Nếu công thức đã tồn tại trong danh sách lưu trữ, xóa nó đi
                if (viewModel.database.saveRecipeDao().checkExistence(data.Id)) {
                    viewModel.database.saveRecipeDao().removeRecent(data.Id);
                }
                // Thêm công thức vào danh sách lưu trữ và hiển thị thông báo lưu thành công
                viewModel.database.saveRecipeDao().addRecentView(new SaveRecipe() {{
                    AuthID = data.RecipeAuth;
                    RecipeID = data.Id;
                    SaveTime = getTimeNow();
                    Recipe = data.toJson();
                }});
                showToast(getString(R.string.save));
            } else {
                // Hiển thị thông báo khi người dùng chưa đăng nhập
                new NoUserDialog(requireContext(), () -> {
                    viewModel.isSingApp.postValue(true);
                }).show();
            }
        });

// Bắt sự kiện khi người dùng bấm nút "báo cáo"
        binding.llReport.setOnClickListener(v -> {
            // Kiểm tra phiên bản Android hiện tại
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                int locationY = Utils.getHeightPercent(5);
                int locationX = v.getRight() - Utils.getWidthPercent(30);

                // Hiển thị menu popup cho việc báo cáo công thức
                PopUpDialog.showPopupMenu(v, PopupReportRecipeBinding::inflate, Utils.getWidthPercent(35), ViewGroup.LayoutParams.WRAP_CONTENT, locationX, locationY, (binding, popup) -> {
                    if (us != null) {
                        // Nếu người dùng là chủ sở hữu của công thức, hiển thị tùy chọn khóa công thức
                        if (data.RecipeAuth.equals(us.UserID)) {
                            binding.llLock.setVisibility(View.VISIBLE);
                        }
                    }
                    // Bắt sự kiện khi người dùng bấm nút "chia sẻ"
                    binding.llShare.setOnClickListener(v2 -> {
                        shareWithFacebook();
                        popup.dismiss();
                    });
                    // Bắt sự kiện khi người dùng bấm nút "báo cáo"
                    binding.llReport.setOnClickListener(v1 -> {
                        if (us != null) {
                            reportRecipe(new DataEventListener<String>() {
                                @Override
                                public void onSuccess(String data) {
                                    // Xử lý khi báo cáo thành công
                                }

                                @Override
                                public void onErr(Object err) {
                                    // Xử lý khi có lỗi xảy ra trong quá trình báo cáo
                                }
                            });
                        } else {
                            // Hiển thị thông báo khi người dùng chưa đăng nhập
                            new NoUserDialog(requireContext(), () -> {
                                viewModel.isSingApp.postValue(true);
                            }).show();
                        }
                    });

                    // Bắt sự kiện khi người dùng bấm nút "khóa"
                    binding.llLock.setOnClickListener(v12 -> {
                        // Xử lý khi người dùng bấm nút "khóa"
                    });
                });
            }
        });
    }

    private void reportRecipe(DataEventListener<String> dataEventListener) {
        new WarningDialog(getContext(), getString(R.string.cf_report), (s) -> {

        }).show();
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
                viewModel.firestore.collection(Constant.RECIPE).document(this.data.Id).update("Share", this.data.Share + 1);
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
        // Lấy thông tin người dùng hiện tại
        Users us = viewModel.getUsers().getValue();
        // Lấy nội dung tin nhắn từ trường nhập liệu
        String message = binding.txtDiscuss.getText().toString();
        // Tạo khóa mới cho cuộc thảo luận
        String id = viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION).child(data.Id).push().getKey();
        // Tạo đối tượng thảo luận mới
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

        // Nếu không phải là phản hồi
        if (!isReply) {
            // Lưu dữ liệu của cuộc thảo luận vào Firebase Database
            viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION).child(data.Id).child(id).setValue(discussion).addOnSuccessListener(unused -> {
                showToast("Success");
                binding.txtDiscuss.setText("");
            }).addOnFailureListener(e -> {
                showToast(e.getMessage());
            });
        } else { // Nếu là phản hồi
            // Thêm cuộc thảo luận vào mảng cuộc thảo luận của cuộc trả lời
            discussionReply.DiscussionArray.add(discussion);
            // Lưu dữ liệu của cuộc thảo luận vào Firebase Database
            viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION).child(data.Id).child(discussionReply.Id).child("DiscussionArray").setValue(discussionReply.DiscussionArray).addOnSuccessListener(unused -> {
                showToast("Success");
                binding.txtDiscuss.setText("");
                binding.llReply.setVisibility(View.GONE);
            }).addOnFailureListener(e -> {
                showToast(e.getMessage());
            });
        }
    }

}