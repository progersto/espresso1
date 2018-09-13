package com.miracas.espresso.layouts;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.miracas.R;

public class CustomLinearLayoutManager extends LinearLayoutManager {

    private static final float mShrinkAmount = 0.15f;
    private static final float mShrinkDistance = 1.7f;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("probe", "meet a IOOBE in RecyclerView");
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == VERTICAL) {
            int scrolled = super.scrollVerticallyBy(dy, recycler, state);
            float midpoint = getHeight() / 2.f + 100;
            float d0 = 0.f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1.f;
            float s1 = 1.f - mShrinkAmount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                View link = child.findViewById(R.id.link);
                if (link == null) {
                    int position = getPosition(child);
                    if (position != 0) {
                        float childMidpoint =
                                (getDecoratedBottom(child) + getDecoratedTop(child)) / 2.f;
                        float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                        float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                        if (midpoint - childMidpoint > 0)
                            scale = 1.0F;

                        float diffScale = 1.0F - scale;
                        float scaleZoom = scale - diffScale * 1.05F;

                        child.setScaleX(scaleZoom);
                        child.setScaleY(scaleZoom);

                    } else {
                        child.setScaleX(1.0F);
                        child.setScaleY(1.0F);
                    }
                }
            }

            return scrolled;
        } else {
            return 0;
        }
    }

}
