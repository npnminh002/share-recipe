package doan.npnm.sharerecipe.lib.shared_preference;

import android.content.SharedPreferences;

public interface EditorBlock {
    void invoke(SharedPreferences.Editor editor);
}
