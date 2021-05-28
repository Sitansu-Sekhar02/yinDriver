package com.yin.driver.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class AppButton extends android.support.v7.widget.AppCompatButton {
    public AppButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public AppButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.get("medium.otf", context);
        setTypeface(customFont);
    }

}
