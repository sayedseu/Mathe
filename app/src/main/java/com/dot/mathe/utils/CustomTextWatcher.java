package com.dot.mathe.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class CustomTextWatcher implements TextWatcher {
    private final View view;
    private final TextChangeListener listener;

    public CustomTextWatcher(View view, TextChangeListener listener) {
        this.view = view;
        this.listener = listener;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        listener.onTextChange(view.getId(), editable.toString());
    }

    public interface TextChangeListener {
        void onTextChange(int id, String text);
    }
}
