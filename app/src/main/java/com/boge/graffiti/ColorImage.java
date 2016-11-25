package com.boge.graffiti;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author boge
 * @version 1.0
 * @date 2016/11/24
 */

public class ColorImage extends View {

    private int backColor;
    private Paint paint;
    private Paint arcPaint;
    private boolean hasArc;

    private int mRadius;

    public ColorImage(Context context) {
        this(context, null);
    }

    public ColorImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorImage);
        hasArc = typedArray.getBoolean(R.styleable.ColorImage_hasArc, false);
        backColor = typedArray.getColor(R.styleable.ColorImage_color, Color.BLACK);
        mRadius = (int) typedArray.getDimension(R.styleable.ColorImage_radius, 30);

        init();

    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true); // 去除锯齿
        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true); // 去除锯齿
        arcPaint.setColor(backColor);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2, getHeight()/2, mRadius,paint);
        if(hasArc)
        canvas.drawCircle(getWidth()/2, getHeight()/2, mRadius + 10,arcPaint);
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
        paint.setColor(this.backColor);
        arcPaint.setColor(this.backColor);
        invalidate();
    }

    public void setHasArc(boolean hasArc){
        this.hasArc = hasArc;
        invalidate();
    }
}
