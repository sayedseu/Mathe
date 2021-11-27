package com.dot.mathe.app;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dot.mathe.data.AppDao;
import com.dot.mathe.view.MainActivityViewModel;

public class ViewModelProviderFactory implements ViewModelProvider.Factory {
    private final AppDao appDao;

    public ViewModelProviderFactory(AppDao appDao) {
        this.appDao = appDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(appDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
