package doan.npnm.sharerecipe.dialog;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//
//import doan.npnm.sharerecipe.R;
//import doan.npnm.sharerecipe.databinding.BottomSheetShareBinding;
//
//public class BottomSheetShare extends BottomSheetDialogFragment {
//
//    private BottomSheetShareBinding binding;
//    private final OnBottomSheetEvent event;
//
//    public BottomSheetShare(OnBottomSheetEvent event) {
//        this.event = event;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NO_FRAME, R.style.SheetDialog);
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        binding = BottomSheetShareBinding.inflate(inflater, container, false);
//        binding.shareFacebook.setOnClickListener(v -> {
//            event.onFaceBook();
//            Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
//        });
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//    }
//
//
//    public interface OnBottomSheetEvent {
//        void onFaceBook();
//    }
//}


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.databinding.BottomSheetShareBinding;

public class BottomSheetShare extends BottomSheetDialogFragment {

    private BottomSheetShareBinding binding;

    public BottomSheetShare(OnBottomSheetEvent event) {
        this.event = event;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = BottomSheetShareBinding.inflate(inflater, container, false);
        View view= inflater.inflate(R.layout.bottom_sheet_share,container);
        setStyle(BottomSheetDialogFragment.STYLE_NO_FRAME, R.style.SheetDialog);

        view.findViewById(R.id.shareFacebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppContext.getContext(), "Helpp", Toast.LENGTH_SHORT).show();
           event.onFaceBook();
            }
        });

        return view;
    }
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        binding.shareFacebook.setOnClickListener(v -> {
//            Toast.makeText(AppContext.getContext(), "Helpp", Toast.LENGTH_SHORT).show();
//           event.onFaceBook();
//        });
//    }
    final OnBottomSheetEvent event;

    public interface OnBottomSheetEvent {
        void onFaceBook();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
