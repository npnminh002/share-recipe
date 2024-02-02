package doan.npnm.sharerecipe.app.lang;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Locale;

import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.lib.shared_preference.SharedPreference;

public class LanguageUtil  {

    private static void saveLocale(String lang) {
        new SharedPreference().putString("lang", lang);
    }

    public static void setupLanguage(Context context) {
        String languageCode = new SharedPreference().getString("lang", "en");
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = Locale.getDefault().getLanguage();
        }

        Configuration config = new Configuration();
        Locale locale = (languageCode != null) ? new Locale(languageCode) : null;
        if (locale != null) {
            Locale.setDefault(locale);
        }
        config.locale = locale;
        context.getResources().updateConfiguration(config, null);
    }

    public static void changeLang(String lang, Context context) {
        if (TextUtils.isEmpty(lang)) return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    private static final String FIRST_OPEN_APP = "FIRST_OPEN_APP";
    private static final String LANGUAGE = "LANGUAGE";

    public static boolean isFirstOpenApp() {
        return new SharedPreference().getBoolean(FIRST_OPEN_APP, true);
    }

    public static void setFirstOpenApp(boolean value) {
        new SharedPreference().putBoolean(FIRST_OPEN_APP, value);
    }

    public static String getLanguageCode() {
        return new SharedPreference().getString(LANGUAGE, "");
    }

    public static void setLanguageCode(String value) {
        new SharedPreference().putString(LANGUAGE, value);
        setFirstOpenApp(false);
    }
}
