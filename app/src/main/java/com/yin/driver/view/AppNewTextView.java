package com.yin.driver.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class AppNewTextView extends android.support.v7.widget.AppCompatTextView {
    public AppNewTextView(Context context) {
        super(context);
        init();
    }

    public AppNewTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppNewTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "regular_new.ttf");
        setTypeface(tf);
    }
}
