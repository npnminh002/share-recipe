package doan.npnm.sharerecipe.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.dialog.LoaddingDialog;
import doan.npnm.sharerecipe.lib.shared_preference.SharedPreference;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageReference = storage.getReference();
    protected T binding;
    public OnBackPressedCallback callback;

    public BaseFragment() {
    }

    public SharedPreference preference = new SharedPreference();

    public void handlerBackPressed() {
    }

    public BaseFragment<T> newInstance(HashMap<String, Serializable> data) {
        BaseFragment<T> fragment = this;
        Bundle args = new Bundle();
        if (data != null) {
            for (Map.Entry<String, Serializable> entry : data.entrySet()) {
                String key = entry.getKey();
                Serializable value = entry.getValue();
                args.putSerializable(key, value);
            }
        }

        fragment.setArguments(args);
        return fragment;
    }

    public Serializable getData(String key) {
        Bundle args = getArguments();
        if (args != null && args.containsKey(key)) {
            return args.getSerializable(key);
        }
        return null;
    }


    public LoaddingDialog loaddingDialog;

    public void loadImage(String imageLink, final ImageView imageView) {
        Glide.with(this)
                .load(imageLink)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public void loadImage(Object imageLink, final ImageView imageView) {
        Glide.with(this)
                .load(imageLink)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public void showToast(String mess) {
        Toast.makeText(this.requireContext(), mess, Toast.LENGTH_LONG).show();
    }

    public void showToast(Object mess) {
        Toast.makeText(this.requireContext(), mess.toString(), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handlerBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        loaddingDialog = new LoaddingDialog(this.requireContext());
    }


    public String formatToCurrency(float value) {
        Locale locale = new Locale("vi", "VN"); // Set the Vietnamese locale
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(value);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        callback.remove();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = getBinding(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        OnClick();

    }

    protected abstract T getBinding(LayoutInflater inflater, ViewGroup container);

    protected abstract void initView();

    public abstract void OnClick();

    public void addFragment(Fragment fragment, int viewId, boolean addtoBackStack) {
        if (viewId == 0) {
            viewId = android.R.id.content;
        }
        ((BaseActivity<?>) requireActivity()).addFragment(fragment, viewId, addtoBackStack);
    }

    public void replaceFullViewFragment(Fragment fragment, int viewId, boolean addToBackStack) {
        if (viewId == 0) {
            viewId = android.R.id.content;
        }
        ((BaseActivity<?>) requireActivity()).replaceFragment(fragment, viewId, addToBackStack);
    }

    public String getTimeNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String formatDateString(String inputDate) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
            Date date = inputDateFormat.parse(inputDate);

            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return outputDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void replaceFragment(Fragment fragment, int viewId, boolean addtoBackStack) {
        if (viewId == 0) {
            viewId = android.R.id.content;
        }
        ((BaseActivity<?>) requireActivity()).replaceFragment(fragment, viewId, addtoBackStack);
    }

    public void closeFragment(Fragment fragment) {
        ((BaseActivity<?>) requireActivity()).handleBackpress();
    }

    public void addAndRemoveCurrentFragment(Fragment currentFragment, Fragment newFragment, boolean addToBackStack) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.remove(currentFragment);
        transaction.add(android.R.id.content, newFragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    protected void hideKeyboard() {
        if (getActivity() != null) {
            ((BaseActivity<?>) getActivity()).hideKeyboard();
        }
    }

    protected void showKeyboard(View view) {
        ((BaseActivity<?>) requireActivity()).showKeyboard(view);
    }

    protected void setColorStatusBar(int idColor) {
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), idColor));

            }
        }
    }

    protected void setColorStatusDark() {
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.black));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            }
        }
    }

    protected void getResultListener(String requestKey, ResultListener callback) {
        getParentFragmentManager().setFragmentResultListener(requestKey, this, (key, result) -> callback.onResult(key, result));
    }

    protected void setFragmentResult(String requestKey, Bundle resultBundle) {
        requireActivity().getSupportFragmentManager().setFragmentResult(requestKey, resultBundle);
    }

    public static boolean isGoToSetting = false;
    public static boolean isAdsRewardShowing = false;

    public static <F extends Fragment> F newInstance(Class<F> fragment, Bundle args) throws IllegalAccessException, java.lang.InstantiationException {
        F f = fragment.newInstance();
        if (args != null) {
            f.setArguments(args);
        }
        return f;
    }

    interface ResultListener {
        void onResult(String requestKey, Bundle bundle);
    }

    public final String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            isAPI33OrHigher() ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS
    };

    public final String[] permissionsForUsedApp = new String[]{
            Manifest.permission.CAMERA,
            isAPI33OrHigher() ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allPermissionGranted = true;
                for (String permission : permissionsForUsedApp) {
                    if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        break;
                    }
                }
            });

    public boolean allPermissionsGranted() {
        for (String permission : permissionsForUsedApp) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void goToSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
        BaseFragment.isGoToSetting = true;
    }

    private boolean isAPI33OrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }


}
