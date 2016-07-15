package com.freddieptf.shush.calNextras.ui.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.freddieptf.shush.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fred on 12/10/15.
 */
public class InputDialog extends DialogFragment {


    public InputDialog(){
        stringList = new ArrayList<>();
    }

    OnOptionClick onOptionClick;
    @Bind(R.id.dialog_group_title) EditText text;
    @Bind(R.id.dialog_textInputLayout) TextInputLayout textInputLayout;
    @Bind(R.id.dialog_title) TextView title;
    @Bind(R.id.dialog_negative) TextView negative;
    @Bind(R.id.dialog_positive) TextView positive;

    @OnClick(R.id.dialog_negative) void onCancel(){
        onOptionClick.onCancel();
    }
    @OnClick(R.id.dialog_positive) void onCreate(){
        onOptionClick.onCreate(text.getText().toString());
    }

    List<String> stringList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_text_input, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        // must follow the same exact order as the methods
        title.setText(stringList.get(0));
        textInputLayout.setHint(stringList.get(1));
        negative.setText(stringList.get(2));
        positive.setText(stringList.get(3));
    }

    public InputDialog setTitle(String resId){
        stringList.add(resId);
        return this;
    }

    public InputDialog setTextInputLayoutHint(String c){
        stringList.add(c);
        return this;
    }

    public InputDialog setNegativeText(String resId){
        stringList.add(resId);
        return this;
    }

    public InputDialog setPositiveText(String resId){
        stringList.add(resId);
        return this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setClickListener(OnOptionClick onOptionClick){
        this.onOptionClick = onOptionClick;
    }

    public interface OnOptionClick{
        void onCancel();
        void onCreate(String groupName);
    }
}
