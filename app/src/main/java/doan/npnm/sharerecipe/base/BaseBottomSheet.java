package doan.npnm.sharerecipe.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;


public abstract class BaseBottomSheet<V extends ViewBinding> extends BottomSheetDialogFragment {
    public static final String TAG = "BaseBottomSheet";
    private final FragmentActivity activity;

    protected BaseBottomSheet(FragmentActivity activity) {
        this.activity = activity;
    }


    protected abstract V initView(LayoutInflater inflater, ViewGroup container);

    private V binding;
    private boolean isAttached = false;

    protected abstract void onBind(V binding);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = initView(inflater, container);
        onBind(binding);
        return binding.getRoot();
    }

    @Override
    public int getTheme() {
        return  R.style.BottomSheetDialogTheme;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }
    public void show(){
        FragmentTransaction transaction= activity.getSupportFragmentManager().beginTransaction();
        this.show(transaction,TAG);
    }
}