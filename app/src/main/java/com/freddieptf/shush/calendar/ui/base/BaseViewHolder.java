package com.freddieptf.shush.calendar.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fred on 3/26/17.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(T t){

    }
}
