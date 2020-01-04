package com.daohang.trainapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

import com.daohang.trainapp.R;

public class IconButton extends Button {
    private Bitmap mIcon;
    private Paint mPaint;
    private Rect mSrcRect;
    private int mIconPadding;
    private int mIconSize;

    public IconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IconButton(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int shift = (mIconSize + mIconPadding) / 2;

        canvas.save();
        canvas.translate(shift, 0);

        super.onDraw(canvas);

        if (mIcon != null) {
            float textWidth = getPaint().measureText((String)getText());
            int left = (int)((getWidth() / 2f) - (textWidth / 2f) - mIconSize - mIconPadding);
            int top = getHeight()/2 - mIconSize/2;

            Rect destRect = new Rect(left, top, left + mIconSize, top + mIconSize);
            canvas.drawBitmap(mIcon, mSrcRect, destRect, mPaint);
        }

        canvas.restore();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IconButton);

        for (int i = 0; i < array.getIndexCount(); ++i) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.IconButton_iconSrc:
                    mIcon = drawableToBitmap(array.getDrawable(attr));
                    break;
                case R.styleable.IconButton_iconPadding:
                    mIconPadding = array.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.IconButton_iconSize:
                    mIconSize = array.getDimensionPixelSize(attr, 0);
                    break;
                default:
                    break;
            }
        }

        array.recycle();

        //If we didn't supply an icon in the XML
        if(mIcon != null){
            mPaint = new Paint();
            mSrcRect = new Rect(0, 0, mIcon.getWidth(), mIcon.getHeight());
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
