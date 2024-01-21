package doan.npnm.sharerecipe.base;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import doan.npnm.sharerecipe.lib.shared_preference.SharedPreference;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageReference = storage.getReference();
    protected T binding;
    public OnBackPressedCallback callback;
    public SharedPreference preference = new SharedPreference();

    public void handlerBackPressed() {
    }

    public void showToast(String mess) {
        Toast.makeText(this.requireContext(), mess, Toast.LENGTH_LONG).show();
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
    }

    protected abstract T getBinding(LayoutInflater inflater, ViewGroup container);

    protected abstract void initView();

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
}
