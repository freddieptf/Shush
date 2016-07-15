package com.freddieptf.shush.calendar.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.freddieptf.shush.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fred on 12/16/15.
 */
public class SectionView extends LinearLayout {

    @Bind(R.id.section_body) LinearLayout sectionBody;

    private static final String TAG = "SectionView";

    public SectionView(Context context) {
        this(context, null);
    }

    public SectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.section_view, this);
        ButterKnife.bind(this);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(sectionBody == null) super.addView(child, index, params);
        else sectionBody.addView(child, index, params);
    }

}
