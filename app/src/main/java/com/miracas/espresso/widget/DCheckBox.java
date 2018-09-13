package com.miracas.espresso.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;


public class DCheckBox extends AppCompatCheckBox {

    public DCheckBox(Context context) {
        super(context);
    }

    public DCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextViewHelper.setTypeface(context, this, attrs);
    }
}
