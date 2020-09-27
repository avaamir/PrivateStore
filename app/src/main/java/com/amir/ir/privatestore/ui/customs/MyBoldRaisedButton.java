package com.amir.ir.privatestore.ui.customs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.amir.ir.privatestore.R;


public class MyBoldRaisedButton extends MyRaisedButton {

    public MyBoldRaisedButton(Context context) {
        super(context);
        init();
    }

    public MyBoldRaisedButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBoldRaisedButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.my_raised_button, this);


        TextView tvCaption = view.findViewById(R.id.tv_caption);

        tvCaption.setTypeface(ResourcesCompat.getFont(getContext(), R.font.iransans_bold));

    }

}
