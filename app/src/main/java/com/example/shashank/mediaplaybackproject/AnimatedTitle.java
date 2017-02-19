package com.example.shashank.mediaplaybackproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Shashank on 27-10-2016.
 */

public class AnimatedTitle extends View {

    Paint paint;

    public AnimatedTitle(Context context) {
        super(context);
        init();

    }


    public AnimatedTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init(){
        paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        //paint.setTextSize(60.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);
        canvas.drawText("SHASHANK",getWidth()-canvas.getWidth(),30,paint);
        canvas.rotate(90,0,0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
