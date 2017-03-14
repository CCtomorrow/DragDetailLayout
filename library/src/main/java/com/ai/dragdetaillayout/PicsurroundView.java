package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * <b>Project:</b> DragDetailLayout <br>
 * <b>Create Date:</b> 2017/3/14 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 两行文字，四周可以环绕图片的View <br>
 */
public class PicsurroundView extends View {

    private Drawable
            mDrawableLeft = null,
            mDrawableTop = null,
            mDrawableRight = null,
            mDrawableBottom = null;

    private TextPaint
            mTopTextPaint = null,
            mBottomTextPaint = null;

    private int mTopTextColor;
    private int mTopTextSize;
    private boolean mTopTextBold; // 是否加粗
    private String mTopText;

    private int mBottomTextColor;
    private int mBottomTextSize;
    private boolean mBottomTextBold; // 是否加粗
    private String mBottomText;

    private int mTextPadding;
    private int mDrawablePadding;

    public PicsurroundView(Context context) {
        this(context, null);
    }

    public PicsurroundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PicsurroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PicsurroundView);
        mDrawableLeft = a.getDrawable(R.styleable.PicsurroundView_android_drawableLeft);
        mDrawableTop = a.getDrawable(R.styleable.PicsurroundView_android_drawableTop);
        mDrawableRight = a.getDrawable(R.styleable.PicsurroundView_android_drawableRight);
        mDrawableBottom = a.getDrawable(R.styleable.PicsurroundView_android_drawableBottom);

        mTextPadding = a.getDimensionPixelSize(R.styleable.PicsurroundView_textPadding, 0);
        mDrawablePadding = a.getDimensionPixelSize(R.styleable.PicsurroundView_android_drawablePadding, 0);

        mTopTextColor = a.getColor(R.styleable.PicsurroundView_topTextColor, Color.BLACK);
        mTopTextSize = a.getDimensionPixelSize(R.styleable.PicsurroundView_topTextSize, 15);
        mTopTextBold = a.getBoolean(R.styleable.PicsurroundView_topTextBold, false);
        mTopText = (String) a.getText(R.styleable.PicsurroundView_topText);

        mBottomTextColor = a.getColor(R.styleable.PicsurroundView_bottomTextColor, Color.BLACK);
        mBottomTextSize = a.getDimensionPixelSize(R.styleable.PicsurroundView_bottomTextSize, 15);
        mBottomTextBold = a.getBoolean(R.styleable.PicsurroundView_bottomTextBold, false);
        mBottomText = (String) a.getText(R.styleable.PicsurroundView_bottomText);

        if (TextUtils.isEmpty(mTopText)) mTopText = "";
        if (TextUtils.isEmpty(mBottomText)) mBottomText = "";

        a.recycle();
        initPaint();
    }

    private void initPaint() {
        mTopTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTopTextPaint.setColor(mTopTextColor);
        mTopTextPaint.setTextSize(mTopTextSize);
        mTopTextPaint.setTextAlign(Paint.Align.LEFT);
        mTopTextPaint.setTypeface(mTopTextBold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

        mBottomTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mBottomTextPaint.setColor(mBottomTextColor);
        mBottomTextPaint.setTextSize(mBottomTextSize);
        mBottomTextPaint.setTextAlign(Paint.Align.LEFT);
        mBottomTextPaint.setTypeface(mBottomTextBold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    public void setTopText(String topText) {
        mTopText = topText;
        requestLayout();
        invalidate();
    }

    public void setBottomText(String bottomText) {
        mBottomText = bottomText;
        requestLayout();
        invalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureWidth(widthMeasureSpec);
        int height = getMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 测量View的宽度
     *
     * @param widthMeasureSpec widthMeasureSpec
     * @return 返回View的测量宽度
     */
    private int getMeasureWidth(int widthMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int padding = getPaddingLeft() + getPaddingRight();
            float topTextWidth = mTopTextPaint.measureText(mTopText);
            float bottomTextWidth = mBottomTextPaint.measureText(mBottomText);
            int leftdrawableWidth = mDrawableLeft != null ? mDrawableLeft.getIntrinsicWidth() + mDrawablePadding : 0;
            int rightdrawableWidth = mDrawableRight != null ? mDrawableRight.getIntrinsicWidth() + mDrawablePadding : 0;
            result = (int) (padding + Math.max(topTextWidth, bottomTextWidth) + leftdrawableWidth + rightdrawableWidth);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 测量View的宽度
     *
     * @param heightMeasureSpec heightMeasureSpec
     * @return 返回View的测量高度
     */
    private int getMeasureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int padding = getPaddingTop() + getPaddingBottom();
            float topTextHeight = mTopTextPaint.descent() - mTopTextPaint.ascent();
            float bottomTextHeight = mBottomTextPaint.descent() - mBottomTextPaint.ascent();
            int topdrawableHeight = mDrawableTop != null ? mDrawableTop.getIntrinsicHeight() + mDrawablePadding : 0;
            int bottomdrawableHeight = mDrawableBottom != null ? mDrawableBottom.getIntrinsicHeight() + mDrawablePadding : 0;
            result = (int) (padding + topdrawableHeight + topTextHeight + mTextPadding + bottomTextHeight + bottomdrawableHeight);
            int leftDrawableHeight = mDrawableLeft != null ? mDrawableLeft.getIntrinsicHeight() : 0;
            int rightDrawableHeight = mDrawableRight != null ? mDrawableRight.getIntrinsicHeight() : 0;
            int drawableHeight = Math.max(leftDrawableHeight, rightDrawableHeight);
            result = Math.max(result, drawableHeight);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * topText宽度
     *
     * @return 宽度
     */
    private float getTopTextWidth() {
        float topTextWidth = mTopTextPaint.measureText(mTopText);
        return topTextWidth;
    }

    /**
     * bottomText宽度
     *
     * @return
     */
    private float getBottomTextWidth() {
        float bottomTextWidth = mBottomTextPaint.measureText(mBottomText);
        return bottomTextWidth;
    }

    /**
     * topText高度
     *
     * @return 高度
     */
    private float getTopTextHeight() {
        float topTextHeight = mTopTextPaint.descent() - mTopTextPaint.ascent();
        return topTextHeight;
    }

    /**
     * bottomText高度
     *
     * @return 高度
     */
    private float getBottomTextHeight() {
        float bottomTextHeight = mBottomTextPaint.descent() - mBottomTextPaint.ascent();
        return bottomTextHeight;
    }

    /**
     * 最左边的开始的x坐标
     *
     * @return x坐标
     */
    private int getLeftStart() {
        int leftdrawableWidth = mDrawableLeft != null ? mDrawableLeft.getIntrinsicWidth() + mDrawablePadding : 0;
        int rightdrawableWidth = mDrawableRight != null ? mDrawableRight.getIntrinsicWidth() + mDrawablePadding : 0;
        int width = (int) (Math.max(getTopTextWidth(), getBottomTextWidth()) + leftdrawableWidth + rightdrawableWidth);
        int left = getWidth() / 2 - width / 2;
        return left;
    }

    /**
     * 最上边开始的y坐标
     *
     * @return y坐标
     */
    private int getTopStart() {
        int topdrawableHeight = mDrawableTop != null ? mDrawableTop.getIntrinsicHeight() + mDrawablePadding : 0;
        int bottomdrawableHeight = mDrawableBottom != null ? mDrawableBottom.getIntrinsicHeight() + mDrawablePadding : 0;
        int height = (int) (topdrawableHeight + getTopTextHeight() + mTextPadding + getBottomTextHeight() + bottomdrawableHeight);
        int top = getHeight() / 2 - height / 2;
        return top;
    }

    /**
     * 左侧Drawable的位置
     *
     * @return rect
     */
    private Rect getLeftBrounds() {
        int left = getLeftStart();
        int top = getHeight() / 2 - mDrawableLeft.getIntrinsicHeight() / 2;
        int right = left + mDrawableLeft.getIntrinsicWidth();
        int bottom = top + mDrawableLeft.getIntrinsicHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * 顶部Drawable的位置
     *
     * @return rect
     */
    private Rect getTopBrounds() {
        int left = getWidth() / 2 - mDrawableTop.getIntrinsicHeight() / 2;
        int top = getTopStart();
        int right = left + mDrawableTop.getIntrinsicWidth();
        int bottom = top + mDrawableTop.getIntrinsicHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * 第一行文字的位置
     *
     * @return x，y
     */
    private Point getTopTextPoint() {
        int left = getLeftStart();
        if (getTopTextWidth() < getBottomTextWidth()) {
            left += (getBottomTextWidth() - getTopTextWidth()) / 2;
        }
        if (mDrawableLeft != null) {
            left += mDrawableLeft.getIntrinsicWidth() + mDrawablePadding;
        }
        int top = getTopStart();
        if (mDrawableTop != null) {
            top += mDrawableTop.getIntrinsicHeight() + mDrawablePadding;
        }
        // 画文字从baseline开始的，需要把asent部分加上，ascent值为负，所以这里是减号
        top -= mTopTextPaint.ascent();
        return new Point(left, top);
    }

    /**
     * 第二行文字的位置
     *
     * @return x，y
     */
    private Point getBottomTextPoint() {
        float bottomTextHeight = mBottomTextPaint.descent() - mBottomTextPaint.ascent();
        int left = getLeftStart();
        if (getTopTextWidth() > getBottomTextWidth()) {
            left += (getTopTextWidth() - getBottomTextWidth()) / 2;
        }
        if (mDrawableLeft != null) {
            left += mDrawableLeft.getIntrinsicWidth() + mDrawablePadding;
        }
        int top = (int) (getTopTextPoint().y + bottomTextHeight + mTextPadding);
        // int top = (int) (getTopTextPoint().y - mBottomTextPaint.ascent() + mTextPadding);
        return new Point(left, top);
    }

    /**
     * 右边Drawable的位置
     *
     * @return rect
     */
    private Rect getRightBrounds() {
        int topx = (int) (getTopTextPoint().x + getTopTextWidth() + mDrawablePadding);
        int bottomx = (int) (getBottomTextPoint().x + getBottomTextWidth() + mDrawablePadding);
        int left = Math.max(topx, bottomx);
        int top = getHeight() / 2 - mDrawableRight.getIntrinsicHeight() / 2;
        int right = left + mDrawableRight.getIntrinsicWidth();
        int bottom = top + mDrawableRight.getIntrinsicHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * 底部Drawable的位置
     *
     * @return rect
     */
    private Rect getBottomBrounds() {
        int left = getWidth() / 2 - mDrawableBottom.getIntrinsicHeight() / 2;
        int top = getBottomTextPoint().y + mDrawablePadding;
        int right = left + mDrawableBottom.getIntrinsicWidth();
        int bottom = top + mDrawableBottom.getIntrinsicHeight();
        return new Rect(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawableLeft != null) {
            mDrawableLeft.setBounds(getLeftBrounds());
            mDrawableLeft.draw(canvas);
        }
        if (mDrawableTop != null) {
            mDrawableTop.setBounds(getTopBrounds());
            mDrawableTop.draw(canvas);
        }
        canvas.drawText(mTopText, getTopTextPoint().x, getTopTextPoint().y, mTopTextPaint);
        canvas.drawText(mBottomText, getBottomTextPoint().x, getBottomTextPoint().y, mBottomTextPaint);
        if (mDrawableRight != null) {
            mDrawableRight.setBounds(getRightBrounds());
            mDrawableRight.draw(canvas);
        }
        if (mDrawableBottom != null) {
            mDrawableBottom.setBounds(getBottomBrounds());
            mDrawableBottom.draw(canvas);
        }
    }

}
