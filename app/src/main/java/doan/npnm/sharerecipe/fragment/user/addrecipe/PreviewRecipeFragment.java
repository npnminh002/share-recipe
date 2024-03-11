package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

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

    // Constructor nhận ViewModels
    public PreviewRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
    }

    // Đối tượng công thức
    private Recipe recipe;

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentPreviewRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPreviewRecipeBinding.inflate(getLayoutInflater());
    }

    // Adapter cho danh sách hướng dẫn và nguyên liệu
    private DirectionsAdapter directionsAdapter;
    private IngridentsAdapter ingridentsAdapter;
    private ImageRecipeAdapter adapter;

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Quan sát sự kiện thêm công thức mới
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(PreviewRecipeFragment.this);
        });

        // Khởi tạo adapter cho danh sách hướng dẫn
        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null);
        binding.rcvDirection.setAdapter(directionsAdapter);

        // Khởi tạo adapter cho danh sách nguyên liệu
        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null);
        binding.rcvIngrident.setAdapter(ingridentsAdapter);

        // Khởi tạo adapter cho danh sách hình ảnh
        adapter = new ImageRecipeAdapter(null);
        binding.listImg.setAdapter(adapter);

        // Quan sát dữ liệu công thức từ ViewModel
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            // Hiển thị hình ảnh của công thức
            if (recipeViewModel.imgUri != null) {
                loadImage(recipeViewModel.imgUri, binding.imgProduct);
            } else {
                loadImage(data.ImgUrl, binding.imgProduct);
            }
            // Hiển thị thông tin công thức
            binding.nameOfRecipe.setText(data.Name == null ? "" : data.Name);
            binding.description.setText(data.Description == null ? "" : data.Description);
            binding.timePrepare.setText(data.PrepareTime.Time);
            binding.selectMinutePP.setText(data.PrepareTime.TimeType);
            binding.timeCook.setText(data.CookTime.Time);
            binding.selectMinutePP.setText(data.CookTime.TimeType);
            binding.txtLever.setText(data.Level);

            // Sắp xếp danh sách hướng dẫn và nguyên liệu
            Collections.sort(data.Directions, ((o1, o2) -> String.valueOf(o1.Id).compareTo(String.valueOf(o2.Id))));
            Collections.sort(data.Ingredients, ((o1, o2) -> String.valueOf(o1.Id).compareTo(String.valueOf(o2.Id))));

            // Hiển thị danh sách hướng dẫn và nguyên liệu
            directionsAdapter.setItems(data.Directions);
            ingridentsAdapter.setItems(data.Ingredients);
        });

        // Quan sát danh sách hình ảnh từ ViewModel
        recipeViewModel.listSelect.observe(this, data -> {
            int indexNull = data.indexOf(null);
            if (indexNull >= 0) {
                data.remove(indexNull);
            }
            adapter.setItems(data);
        });
    }

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.backIcon.setOnClickListener(v -> closeFragment(PreviewRecipeFragment.this));
        // Xử lý sự kiện khi nhấn nút hủy
        binding.btnCancel.setOnClickListener(v -> closeFragment(PreviewRecipeFragment.this));
        // Xử lý sự kiện khi nhấn nút tiếp theo
        binding.btnNext.setOnClickListener(v -> {
            // Hiển thị hộp thoại tiến trình
            loaddingDialog.show();
            // Bắt đầu tải lên dữ liệu
            startUploadData(new OnAddImageSuccess() {
                @Override
                public void onAddSuccess(String docID, String img, ArrayList<String> listUrl) {
                    // Cập nhật dữ liệu cho công thức
                    recipe.ImagePreview = listUrl;
                    recipe.Id = docID;
                    recipe.RecipeAuth = viewModel.auth.getCurrentUser().getUid();
                    recipeViewModel.recipeLiveData.postValue(recipe);
                    // Đặt dữ liệu công thức
                    putDataRecipe(recipeViewModel.recipeLiveData.getValue());
                }
            });
        });
    }

    // Giao diện thông báo thành công khi tải lên hình ảnh
    private interface OnAddImageSuccess {
        void onAddSuccess(String docID, String img, ArrayList<String> listUrl);
    }

    // Bắt đầu tải lên dữ liệu
    private void startUploadData(OnAddImageSuccess onAddImageSuccess) {
        // Tạo ID mới cho tài liệu Firestore
        String firestoreID = firestore.collection(Constant.RECIPE).document().getId();
        StorageReference reference = storage.getReference(Constant.RECIPE).child(firestoreID);
        ArrayList<String> listUrl = new ArrayList<>();

        // Tải lên hình ảnh chính của công thức
        viewModel.putImgToStorage(reference, recipeViewModel.imgUri, new UserViewModel.OnPutImageListener() {
            @Override
            public void onComplete(String urlApp) {
                recipe.ImgUrl = urlApp;
                // Nếu có hình ảnh phụ
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    recipeViewModel.listSelect.getValue().forEach(uri -> {
                        viewModel.putImgToStorage(reference, uri, new UserViewModel.OnPutImageListener() {
                            @Override
                            public void onComplete(String url) {
                                listUrl.add(url);
                                // Nếu đã tải lên đủ hình ảnh phụ
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

    // Đặt dữ liệu công thức lên Firestore
    private void putDataRecipe(Recipe value) {
        // Tạo lịch sử thay đổi cho công thức
        recipe.History = new ArrayList<String>() {{
            add("Time Add" + getTimeNow());
        }};
        // Đặt dữ liệu công thức lên Firestore
        firestore.collection(Constant.RECIPE)
                .document(value.Id)
                .set(recipe).addOnSuccessListener(task -> {
                    showToast("Add data success full");
                    loaddingDialog.dismiss();
                    viewModel.isAddRecipe.postValue(true);
                    ArrayList<String> arrAdd= viewModel.myRecipeArr.getValue();
                    arrAdd.add(value.toJson());
                    viewModel.myRecipeArr.postValue(arrAdd);
                    postNotification(recipe);
                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                    loaddingDialog.dismiss();
                });
    }

    // Gửi thông báo về việc thêm mới công thức
    private void postNotification(Recipe recipe) {
        // Tạo tham chiếu đến Firebase Database
        DatabaseReference youfirebaseDatabase = viewModel.fbDatabase.getReference(Constant.NOTIFICATION)
                .child(recipe.RecipeAuth).push();

        // Tạo ID cho thông báo
        String id = youfirebaseDatabase.getKey();
        // Tạo thông báo
        Notification yourNotification = new Notification() {{
            Id = id;
            Content = getString(R.string.add_success);
            Time = getTimeNow();
            AuthID = recipe.RecipeAuth;
            IsView = false;
            Value = recipe.Id;
            NotyType = doan.npnm.sharerecipe.model.notification.NotyType.USER_ADD;
        }};

        // Gửi thông báo cho người dùng
        sendNotiById(viewModel.users.getValue().Token,true);
        // Tạo thông báo cho người theo dõi
        Notification followNotification = new Notification() {{
            Id = id;
            Content = viewModel.users.getValue().UserName + " " + getString(R.string.friend_add_recipe);
            Time = getTimeNow();
            AuthID = recipe.RecipeAuth;
            IsView = false;
            Value = recipe.Id;
            NotyType = doan.npnm.sharerecipe.model.notification.NotyType.USER_ADD;
        }};
        // Đặt thông báo vào Firebase Database
        youfirebaseDatabase.setValue(yourNotification);
        // Gửi thông báo cho từng người theo dõi
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

    // Gửi thông báo đến người dùng qua token FCM
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
