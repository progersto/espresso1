package com.miracas.espresso.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;


public class DRadioButton extends AppCompatRadioButton {

    public DRadioButton(Context context) {
        super(context);
    }

    public DRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextViewHelper.setTypeface(context, this, attrs);
    }
}
