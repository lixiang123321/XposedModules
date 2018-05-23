package com.nosuchserver.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * base test activity
 *
 * easy to get button, textView, editText
 *
 * Created by rere on 2017/1/20.
 */
public abstract class TestBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSimpleLinearLayout();
    }

    protected void getSimpleLinearLayout() {
        ScrollView view = new ScrollView(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(view);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        view.addView(layout, MATCH_PARENT, MATCH_PARENT);
        addViews(layout);
    }

    protected void addViews(LinearLayout layout) {

    }

    protected Button getButton(ViewGroup group, String text, View.OnClickListener onClickListener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setOnClickListener(onClickListener);
        group.addView(btn);
        return btn;
    }

    protected TextView getTextview(ViewGroup group, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        group.addView(textView);
        return textView;
    }

    protected ImageView getImageView(ViewGroup group) {
        ImageView imageView = new ImageView(this);
        group.addView(imageView);
        return imageView;
    }

    protected EditText getEditText(ViewGroup group, String hint) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setTextColor(Color.BLACK);
        group.addView(editText);
        return editText;
    }


}
