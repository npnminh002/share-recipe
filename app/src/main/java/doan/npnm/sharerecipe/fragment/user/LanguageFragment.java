package doan.npnm.sharerecipe.fragment.user;

import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Locale;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.LanguageAdapter;
import doan.npnm.sharerecipe.app.lang.Language;
import doan.npnm.sharerecipe.app.lang.LanguageUtil;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentLanguageBinding;

public class LanguageFragment extends BaseFragment<FragmentLanguageBinding> {
    @Override
    protected FragmentLanguageBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLanguageBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

        initData();
        getDataLanguage();
    }

    ArrayList<Language> listLanguages = new ArrayList<>();
    String languageCode = "en";
    LanguageAdapter languageAdapter;


    public void OnClick() {
        binding.icSaveIcon.setOnClickListener(v -> changeLanguage());
        binding.backIcon.setOnClickListener(v -> closeFragment(LanguageFragment.this));
    }

    private void changeLanguage() {
        LanguageUtil.setLanguageCode(languageCode);
        LanguageUtil.changeLang(LanguageUtil.getLanguageCode(), LanguageFragment.this.requireContext());
        LanguageUtil.setFirstOpenApp(false);
        closeFragment(LanguageFragment.this);
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
