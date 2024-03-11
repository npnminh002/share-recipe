package doan.npnm.sharerecipe.fragment.publics;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.Serializable;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.lib.BitmapUtils;
import doan.npnm.sharerecipe.lib.ImageDownloader;
import doan.npnm.sharerecipe.databinding.FragmentImagePreviewBinding;
public class ImagePreviewFragment extends BaseFragment<FragmentImagePreviewBinding> {

    // Biến lưu trữ URL hoặc đối tượng hình ảnh
    private Object imageSource;

    // Hàm khởi tạo mặc định không tham số
    public ImagePreviewFragment() {
    }

    // Phương thức tạo mới một instance của ImagePreviewFragment với đối số là URL hoặc đối tượng hình ảnh
    public static ImagePreviewFragment newInstance(Serializable url) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("Source", url);
        fragment.setArguments(args);
        return fragment;
    }

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentImagePreviewBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentImagePreviewBinding.inflate(inflater);
    }

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Kiểm tra xem có tham số truyền vào không
        assert getArguments() != null;
        // Lấy đối tượng hình ảnh từ tham số
        imageSource = getArguments().getSerializable("Source");
        // Hiển thị hình ảnh nếu có
        if (imageSource != null) {
            binding.imgPreivew.loadImage(imageSource);
        }
    }

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.icBack.setOnClickListener(v -> closeFragment(ImagePreviewFragment.this));
        // Xử lý sự kiện khi nhấn nút tải về
        binding.icDownload.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận tải về
            new ConfirmDialog(getContext(), getString(R.string.cf_download), () -> {
                // Tạo mới một đối tượng ImageDownloader để tải hình ảnh về
                ImageDownloader imageDownloaderMain = new ImageDownloader(bitmap -> {
                    // Nếu hình ảnh được tải thành công, thực hiện tải về
                    if (bitmap != null) {
                        BitmapUtils.downloadImagePhoto(bitmap);
                    }
                });
                // Thực hiện tải về hình ảnh
                imageDownloaderMain.execute(imageSource.toString());
            }).show();
        });
    }
}


































