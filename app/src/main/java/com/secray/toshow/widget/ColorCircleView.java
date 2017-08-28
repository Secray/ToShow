package com.secray.toshow.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.secray.toshow.R;

/**
 * Created by xiekui on 17-8-28.
 */

public class ColorCircleView extends View {
    private int mFillColor;
    private int mBorderColor;
    private Paint mPaint;
    private boolean mIsDrawBorder;
    public ColorCircleView(Context context) {
        this(context, null);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorCircleView);
        mFillColor = a.getColor(R.styleable.ColorCircleView_borderColor, Color.RED);
        mBorderColor = a.getColor(R.styleable.ColorCircleView_fillColor, Color.GREEN);
        mIsDrawBorder = a.getBoolean(R.styleable.ColorCircleView_drawBorder, false);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsDrawBorder) {
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(4);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
                    Math.min(getMeasuredHeight() / 2 - 4, getMeasuredWidth() / 2 - 4), mPaint);
        }

        mPaint.setColor(mFillColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
                Math.min(getMeasuredHeight() / 2 - 8, getMeasuredWidth() / 2 - 8), mPaint);
    }

    public void setFillColor(int color) {
        mFillColor = color;
        invalidate();
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
        mIsDrawBorder = true;
        invalidate();
    }

    public void drawBorder(boolean isDrawBorder) {
        mIsDrawBorder = isDrawBorder;
        invalidate();
    }

    public int getColor() {
        return mFillColor;
    }
}
