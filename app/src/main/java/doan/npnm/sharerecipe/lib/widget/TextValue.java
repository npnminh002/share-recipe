package doan.npnm.sharerecipe.lib.widget;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.function.Consumer;

import doan.npnm.sharerecipe.R;

public class TextValue {
    private EditText editText;

    public TextValue(EditText editText) {
        this.editText = editText;
        onFocus();

    }

    public void onError() {
        editText.setBackgroundResource(R.drawable.bg_input_start_error);
    }

    public void onFocus() {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            editText.setBackgroundResource(hasFocus ? R.drawable.bg_input_start_enable : R.drawable.bg_input_start_disable);
        });
    }

    public String value() {
        return editText.getText().toString();
    }

    public TextValue onValueChange(Consumer<String> value) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    value.accept(s.toString());
                }
            }
        });
        return this;
    }

}
