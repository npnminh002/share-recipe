
package doan.npnm.sharerecipe.activity.user;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import java.util.ArrayList;
import java.util.Locale;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.admin.AdminMainActivity;
import doan.npnm.sharerecipe.adapter.users.LanguageAdapter;
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
        LanguageUtil.setLanguageCode(languageCode);

        LanguageUtil.changeLang(LanguageUtil.getLanguageCode(), this);
        LanguageUtil.setFirstOpenApp(false);
        startActivity(new Intent(this, userViewModel.users.getValue().AccountType == 1 ? AdminMainActivity.class : MainActivity.class));
        finish();
    }


    private void getDataLanguage() {
        initData();

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }

        Language languageSystem = null;
        int position = 0;

        for (Language language : listLanguages) {
            if (language.getLocale().equals(locale.getLanguage())) {
                languageSystem = language;
                languageCode = locale.getLanguage();
            }

            if (LanguageUtil.getLanguageCode().equals(language.getLocale())) {
                languageSystem = language;
                languageCode = languageSystem.getLocale();
            }
            position = listLanguages.indexOf(languageSystem);

        }

        if (languageSystem != null) {
            listLanguages.get(position).setChoose(true);
        }

        initAdapter();
    }

    private void initAdapter() {
        languageAdapter = new LanguageAdapter((language, position) -> {
            languageCode = language.getLocale();
            languageAdapter.setCurrentPos(position);
        });
        languageAdapter.setItems(listLanguages);
        binding.rcvListLang.setAdapter(languageAdapter);
    }


    private void initData() {
        listLanguages = new ArrayList<>();
        listLanguages.add(new Language(R.drawable.flag_en, getString(R.string.language_english), "en"));
        listLanguages.add(new Language(R.drawable.flag_vn, "Vietnamese", "vi"));
        listLanguages.add(new Language(R.drawable.flag_es_spain, "Spanish", "es"));
        listLanguages.add(new Language(R.drawable.flag_it, "Italian", "it"));
        listLanguages.add(new Language(R.drawable.flag_in_hindi, "Hindi", "in"));
        listLanguages.add(new Language(R.drawable.flag_pt_portugal, getString(R.string.language_portugal), "pt"));
    }

}