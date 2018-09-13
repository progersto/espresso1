package com.miracas.espresso.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public class DTextView extends AppCompatTextView {

    public DTextView(Context context) {
        super(context);
    }

    public DTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextViewHelper.setTypeface(context, this, attrs);
    }
}

