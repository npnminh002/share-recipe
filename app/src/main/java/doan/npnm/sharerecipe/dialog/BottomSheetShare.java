package doan.npnm.sharerecipe.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.databinding.BottomSheetShareBinding;
public class BottomSheetShare extends BottomSheetDialogFragment {

    private BottomSheetShareBinding binding;
    private final   OnBottomSheetEvent event;

    public BottomSheetShare(OnBottomSheetEvent event) {
        this.event = event;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NO_FRAME, R.style.SheetDialog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=BottomSheetShareBinding.inflate(inflater,container,false);
        binding.shareFacebook.setOnClickListener(v -> event.onFaceBook());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    public interface OnBottomSheetEvent {
        void onFaceBook();
    }
}
