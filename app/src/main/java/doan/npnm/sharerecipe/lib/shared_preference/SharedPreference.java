package doan.npnm.sharerecipe.lib.shared_preference;

import android.content.Context;
import android.content.SharedPreferences;

import doan.npnm.sharerecipe.app.context.AppContext;

public class SharedPreference {
    private SharedPreferences sharedPreferences;
    public SharedPreference() {
        Context context = AppContext.getContext();
        sharedPreferences = context.getSharedPreferences("SharePrfe", Context.MODE_PRIVATE);
    }

    public void edit(EditorBlock block) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        block.invoke(editor);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        edit(editor -> editor.putBoolean(key, value));
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void putString(String key, String value) {
        edit(editor -> editor.putString(key, value));
    }

    public void clear() {
        edit(SharedPreferences.Editor::clear);
    }
}

