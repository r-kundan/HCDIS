package com.app.harcdis.utils;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypeWriter extends androidx.appcompat.widget.AppCompatTextView {
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 500; //Default 500ms delay

    private Handler mHandler = new Handler();


    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };


    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }



}