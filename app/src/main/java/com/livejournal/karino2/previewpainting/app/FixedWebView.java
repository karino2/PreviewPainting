package com.livejournal.karino2.previewpainting.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by karino on 3/28/14.
 */
public class FixedWebView extends WebView {
    public FixedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    View.OnTouchListener touchListener;

    public void setOnTouchListener2(OnTouchListener onTouchListener) {
        this.touchListener = onTouchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(touchListener != null) {
            boolean consumed = touchListener.onTouch(this, event);
            if(consumed)
                return true;
        }
        return super.onTouchEvent(event);
    }
}
