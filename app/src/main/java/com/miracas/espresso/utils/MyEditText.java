package com.miracas.espresso.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.miracas.espresso.widget.DEditText;

public class MyEditText extends DEditText{

    OnCursorChangedInterface onCursorChangedInterface;

    public MyEditText(Context context, AttributeSet attrs,
                      int defStyle) {
        super(context,attrs);

    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MyEditText(Context context, OnCursorChangedInterface onCursorChangedInterface) {
        super(context);
        this.onCursorChangedInterface = onCursorChangedInterface;
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if(onCursorChangedInterface != null) {
            onCursorChangedInterface.onCursorPositionChanged(selStart);
        }
    }

    public interface OnCursorChangedInterface{
        void onCursorPositionChanged(int position);
    }

    public void setListener(OnCursorChangedInterface onCursorChangedInterface){
        this.onCursorChangedInterface = onCursorChangedInterface;
    }

}
