package com.freddieptf.shush.calendar.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by fred on 12/8/15.
 */
public class AutoFitRecyclerView extends RecyclerView {

    StaggeredGridLayoutManager gridLayoutManager;
    int columnWidth = -1;

    public AutoFitRecyclerView(Context context) {
        this(context, null);
    }

    public AutoFitRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        gridLayoutManager = new StaggeredGridLayoutManager(getContext(), attrs, 0, 0);
        setLayoutManager(gridLayoutManager);

    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            Log.d("AutoFitRecycler", "span: " + spanCount);
            gridLayoutManager.setSpanCount(spanCount);
        }
    }
}
