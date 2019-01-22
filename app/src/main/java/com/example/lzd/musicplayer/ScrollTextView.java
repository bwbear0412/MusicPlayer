package com.example.lzd.musicplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

public class ScrollTextView extends AppCompatTextView {

    public ScrollTextView(Context context) {
        super(context);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
//        return super.isFocused();
        return true;
    }
}