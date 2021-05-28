package com.yin.driver.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class AppTextView_SubTitle extends android.support.v7.widget.AppCompatTextView {
    public AppTextView_SubTitle(Context context) {
        super(context);
        init();
    }

    public AppTextView_SubTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppTextView_SubTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "medium.otf");
        setTypeface(tf);
    }
}
