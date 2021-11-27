package com.dot.mathe.app;

import android.content.Context;

import com.dot.mathe.data.AppDao;
import com.dot.mathe.data.AppDatabase;

public class Injection {
    private static AppDao provideAppDao(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return database.getAppDaoInstance();
    }

    public static ViewModelProviderFactory provideViewModelProviderFactory(Context context) {
        AppDao appDao = provideAppDao(context);
        return new ViewModelProviderFactory(appDao);
    }
}
