package doan.npnm.sharerecipe.fragment.publics;

import static doan.npnm.sharerecipe.lib.BitmapUtils.downloadImage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentImagePreviewBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.lib.BitmapUtils;
import doan.npnm.sharerecipe.lib.ImageDownloader;

public class ImagePreviewFragment extends BaseFragment<FragmentImagePreviewBinding> {


    private String mParam1;
    private Object imageSource;

    public ImagePreviewFragment() {

    }
    public static ImagePreviewFragment newInstance(Serializable url ) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("Source", url);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected FragmentImagePreviewBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentImagePreviewBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        assert getArguments() != null;
        imageSource= getArguments().getSerializable("Source");
        if(imageSource!=null){
            binding.imgPreivew.loadImage(imageSource);
        }
    }

    @Override
    public void OnClick() {
        binding.icBack.setOnClickListener(v -> closeFragment(ImagePreviewFragment.this));
        binding.icDownload.setOnClickListener(v -> {
            new ConfirmDialog(getContext(), getString(R.string.cf_download), () -> {
                ImageDownloader imageDownloaderMain = new ImageDownloader(bitmap -> {
                    if (bitmap != null) {
                        BitmapUtils.downloadImagePhoto(bitmap);
                    }
                });
                imageDownloaderMain.execute(imageSource.toString());
            }).show();
        });
    }
}




































