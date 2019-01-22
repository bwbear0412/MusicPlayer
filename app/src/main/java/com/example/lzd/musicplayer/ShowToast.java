package com.example.lzd.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ShowToast {
    @SuppressLint("ShowToast")
    public static void ShowShortToast(Context context, String string) {
        Toast toast = null;
        toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(string);
        toast.show();
    }
}
