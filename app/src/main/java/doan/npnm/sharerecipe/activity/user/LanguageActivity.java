
package doan.npnm.sharerecipe.activity.user;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import java.util.ArrayList;
import java.util.Locale;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.LanguageAdapter;
import doan.npnm.sharerecipe.activity.admin.AdminMainActivity;
import doan.npnm.sharerecipe.app.lang.Language;
import doan.npnm.sharerecipe.app.lang.LanguageUtil;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityLanguageBinding;

public class LanguageActivity extends BaseActivity<ActivityLanguageBinding> {

    @Override
    protected ActivityLanguageBinding getViewBinding() {
        return ActivityLanguageBinding.inflate(getLayoutInflater());
    }

    ArrayList<Language> listLanguages = new ArrayList<>();
    String languageCode = "en";
    LanguageAdapter languageAdapter;


    @Override
    protected void createView() {

        initData();
        getDataLanguage();

    }

    @Override
    public void OnClick() {
        binding.icSaveIcon.setOnClickListener(v -> changeLanguage());
    }

    private void changeLanguage() {
        // Thiết lập mã ngôn ngữ mới cho ứng dụng
        LanguageUtil.setLanguageCode(languageCode);

        // Thay đổi ngôn ngữ của ứng dụng dựa trên mã ngôn ngữ mới
        LanguageUtil.changeLang(LanguageUtil.getLanguageCode(), this);

        // Đánh dấu rằng ứng dụng không còn là lần mở đầu tiên nữa
        LanguageUtil.setFirstOpenApp(false);

        // Nếu người dùng đã đăng nhập
        if(userViewModel.auth.getCurrentUser() != null) {
            // Chuyển đến AdminMainActivity nếu người dùng là quản trị viên, ngược lại chuyển đến MainActivity
            startActivity(new Intent(this, userViewModel.users.getValue().AccountType == 1 ? AdminMainActivity.class : MainActivity.class));
            finish(); // Kết thúc SplashActivity
        } else {
            // Nếu không có người dùng đăng nhập, chuyển đến MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Kết thúc SplashActivity
        }
    }

    private void getDataLanguage() {
        // Khởi tạo dữ liệu ngôn ngữ
        initData();

        Locale locale;
        // Xác định ngôn ngữ hệ thống
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }

        Language languageSystem = null;
        int position = 0;

        // Lặp qua danh sách ngôn ngữ
        for (Language language : listLanguages) {
            if (language.getLocale().equals(locale.getLanguage())) {
                // Nếu ngôn ngữ trong danh sách trùng với ngôn ngữ hệ thống
                languageSystem = language;
                languageCode = locale.getLanguage();
            }

            if (LanguageUtil.getLanguageCode().equals(language.getLocale())) {
                // Nếu ngôn ngữ trong danh sách trùng với ngôn ngữ đã chọn trước đó
                languageSystem = language;
                languageCode = languageSystem.getLocale();
            }
            position = listLanguages.indexOf(languageSystem);
        }

        // Đánh dấu ngôn ngữ đã chọn
        if (languageSystem != null) {
            listLanguages.get(position).setChoose(true);
        }

        // Khởi tạo adapter cho RecyclerView
        initAdapter();
    }

    private void initAdapter() {
        // Khởi tạo adapter cho RecyclerView
        languageAdapter = new LanguageAdapter((language, position) -> {
            languageCode = language.getLocale();
            languageAdapter.setCurrentPos(position);
        });
        // Đặt dữ liệu cho adapter
        languageAdapter.setItems(listLanguages);
        // Đặt adapter cho RecyclerView
        binding.rcvListLang.setAdapter(languageAdapter);
    }

    private void initData() {
        // Khởi tạo dữ liệu mẫu cho danh sách ngôn ngữ
        listLanguages = new ArrayList<>();
        listLanguages.add(new Language(R.drawable.flag_en, getString(R.string.language_english), "en"));
        listLanguages.add(new Language(R.drawable.flag_vn, "Vietnamese", "vi"));
        listLanguages.add(new Language(R.drawable.flag_es_spain, "Spanish", "es"));
        listLanguages.add(new Language(R.drawable.flag_it, "Italian", "it"));
        listLanguages.add(new Language(R.drawable.flag_in_hindi, "Hindi", "in"));
        listLanguages.add(new Language(R.drawable.flag_pt_portugal, getString(R.string.language_portugal), "pt"));
    }

}