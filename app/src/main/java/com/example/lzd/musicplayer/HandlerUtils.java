package com.example.lzd.musicplayer;

import android.os.Handler;
import android.os.Looper;

public class HandlerUtils {
    private HandlerUtils() {

    }

    public static final Handler handler = new Handler(Looper.getMainLooper());
}
