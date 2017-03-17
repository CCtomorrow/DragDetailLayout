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

    private static int DEF_COLOR = Color.parseColor("#ECDED0");

    private int mLeftBorderLineWidth;
    private int mLeftBorderLineColor;
    private int mTopBorderLineWidth;
    private int mTopBorderLineColor;
    private int mRightBorderLineWidth;
    private int mRightBorderLineColor;
    private int mBottomBorderLineWidth;
    private int mBottomBorderLineColor;

    private Paint
            mLeftLinePaint = null,
            mTopLinePaint = null,
            mRightLinePaint = null,
            mBottomLinePaint = null;

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

        mTextPadding = a.getDimensionPixelSize(R.styleable.PicsurroundView_twotextPadding, 0);
        mDrawablePadding = a.getDimensionPixelSize(R.styleable.PicsurroundView_android_drawablePadding, 0);

        mTopTextColor = a.getColor(R.styleable.PicsurroundView_topTextColor, Color.BLACK);
        mTopTextSize = a.getDimensionPixelSize(R.styleable.PicsurroundView_topTextSize, 15);
        mTopTextBold = a.getBoolean(R.styleable.PicsurroundView_topTextBold, false);
        mTopText = (String) a.getText(R.styleable.PicsurroundView_topText);

        mBottomTextColor = a.getColor(R.styleable.PicsurroundView_bottomTextColor, Color.BLACK);
        mBottomTextSize = a.getDimensionPixelSize(R.styleable.PicsurroundView_bottomTextSize, 15);
        mBottomTextBold = a.getBoolean(R.styleable.PicsurroundView_bottomTextBold, false);
        mBottomText = (String) a.getText(R.styleable.PicsurroundView_bottomText);

        int borderLineWidth = a.getDimensionPixelSize(R.styleable.PicsurroundView_borderLineWidth, 0);
        int borderLineColor = a.getColor(R.styleable.PicsurroundView_borderLineColor, DEF_COLOR);

        mLeftBorderLineWidth = a.getDimensionPixelSize(R.styleable.PicsurroundView_leftBorderLineWidth, borderLineWidth);
        mLeftBorderLineColor = a.getColor(R.styleable.PicsurroundView_leftBorderLineColor, borderLineColor);
        mTopBorderLineWidth = a.getDimensionPixelSize(R.styleable.PicsurroundView_topBorderLineWidth, borderLineWidth);
        mTopBorderLineColor = a.getColor(R.styleable.PicsurroundView_topBorderLineColor, borderLineColor);
        mRightBorderLineWidth = a.getDimensionPixelSize(R.styleable.PicsurroundView_rightBorderLineWidth, borderLineWidth);
        mRightBorderLineColor = a.getColor(R.styleable.PicsurroundView_rightBorderLineColor, borderLineColor);
        mBottomBorderLineWidth = a.getDimensionPixelSize(R.styleable.PicsurroundView_bottomBorderLineWidth, borderLineWidth);
        mBottomBorderLineColor = a.getColor(R.styleable.PicsurroundView_bottomBorderLineColor, borderLineColor);

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

        initLinePaint();
    }

    private void initLinePaint() {
        if (mLeftBorderLineWidth > 0) {
            mLeftLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLeftLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mLeftLinePaint.setColor(mLeftBorderLineColor);
            mLeftLinePaint.setStrokeWidth(mLeftBorderLineWidth);
        }
        if (mTopBorderLineWidth > 0) {
            mTopLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTopLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mTopLinePaint.setColor(mTopBorderLineColor);
            mTopLinePaint.setStrokeWidth(mTopBorderLineWidth);
        }
        if (mRightBorderLineWidth > 0) {
            mRightLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRightLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mRightLinePaint.setColor(mRightBorderLineColor);
            mRightLinePaint.setStrokeWidth(mRightBorderLineWidth);
        }
        if (mBottomBorderLineWidth > 0) {
            mBottomLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBottomLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mBottomLinePaint.setColor(mBottomBorderLineColor);
            mBottomLinePaint.setStrokeWidth(mBottomBorderLineWidth);
        }
    }

    public void setTopText(String topText) {
        mTopText = topText;
        re();
    }

    public void setTopText(int res) {
        mTopText = getContext().getString(res);
        re();
    }

    public void setBottomText(String bottomText) {
        mBottomText = bottomText;
        re();
    }

    public void setBottomText(int res) {
        mBottomText = getContext().getString(res);
        re();
    }

    public void setDrawableLeft(Drawable drawableLeft) {
        mDrawableLeft = drawableLeft;
        re();
    }

    public void setDrawableLeft(int res) {
        mDrawableLeft = ViewUtil.getDrawable(getContext(), res);
        re();
    }

    public void setDrawableTop(Drawable drawableTop) {
        mDrawableTop = drawableTop;
        re();
    }

    public void setDrawableTop(int res) {
        mDrawableTop = ViewUtil.getDrawable(getContext(), res);
        re();
    }

    public void setDrawableRight(Drawable drawableRight) {
        mDrawableRight = drawableRight;
        re();
    }

    public void setDrawableRight(int res) {
        mDrawableRight = ViewUtil.getDrawable(getContext(), res);
        re();
    }

    public void setDrawableBottom(Drawable drawableBottom) {
        mDrawableBottom = drawableBottom;
        re();
    }

    public void setDrawableBottom(int res) {
        mDrawableBottom = ViewUtil.getDrawable(getContext(), res);
        re();
    }

    private void re() {
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
            result = padding + getMiddleWidth() + getDrawableWidth(0) + getDrawableWidth(2);
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
            result = padding + getMiddleHeight() + getDrawableHeight(1) + getDrawableHeight(3);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 返回中间的宽度
     */
    private int getMiddleWidth() {
        int textWidth = (int) Math.max(getTopTextWidth(), getBottomTextWidth());
        int drawableWidth = Math.max(getDrawableWidth(1), getDrawableWidth(3));
        int middleWidth = Math.max(textWidth, drawableWidth);
        return middleWidth;
    }

    /**
     * 返回中间的高度
     */
    private int getMiddleHeight() {
        int textHeight = (int) (getTopTextHeight() + mTextPadding + getBottomTextHeight());
        int middleHeight = Math.max(Math.max(textHeight, getDrawableHeight(0)), getDrawableHeight(2));
        return middleHeight;
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
     * 获取drawable的宽度
     *
     * @param direction left:0 top:1 right:2 bottom:3
     * @return
     */
    private int getDrawableWidth(int direction) {
        switch (direction) {
            case 0:
                int leftdrawableWidth = mDrawableLeft != null ? mDrawableLeft.getIntrinsicWidth() + mDrawablePadding : 0;
                return leftdrawableWidth;
            case 1:
                int topdrawableWidth = mDrawableTop != null ? mDrawableTop.getIntrinsicWidth() : 0;
                return topdrawableWidth;
            case 2:
                int rightdrawableWidth = mDrawableRight != null ? mDrawableRight.getIntrinsicWidth() + mDrawablePadding : 0;
                return rightdrawableWidth;
            case 3:
                int bottomdrawableWidth = mDrawableBottom != null ? mDrawableBottom.getIntrinsicWidth() : 0;
                return bottomdrawableWidth;
        }
        return 0;
    }

    /**
     * 获取drawable的高度
     *
     * @param direction left:0 top:1 right:2 bottom:3
     * @return
     */
    private int getDrawableHeight(int direction) {
        switch (direction) {
            case 0:
                int leftdrawableHeight = mDrawableLeft != null ? mDrawableLeft.getIntrinsicHeight() : 0;
                return leftdrawableHeight;
            case 1:
                int topdrawableHeight = mDrawableTop != null ? mDrawableTop.getIntrinsicHeight() + mDrawablePadding : 0;
                return topdrawableHeight;
            case 2:
                int rightdrawableHeight = mDrawableRight != null ? mDrawableRight.getIntrinsicHeight() : 0;
                return rightdrawableHeight;
            case 3:
                int bottomdrawableHeight = mDrawableBottom != null ? mDrawableBottom.getIntrinsicHeight() + mDrawablePadding : 0;
                return bottomdrawableHeight;
        }
        return 0;
    }

    /**
     * 最左边的开始的x坐标
     *
     * @return x坐标
     */
    private int getLeftStart() {
        int width = getMiddleWidth() + getDrawableWidth(0) + getDrawableWidth(2);
        int left = getWidth() / 2 - width / 2;
        return left;
    }

    /**
     * 最上边开始的y坐标
     *
     * @return y坐标
     */
    private int getTopStart() {
        int height = getMiddleHeight() + getDrawableHeight(1) + getDrawableHeight(3);
        int top = getHeight() / 2 - height / 2;
        return top;
    }

    /**
     * 左侧Drawable的位置
     *
     * @return rect
     */
    private Rect getLeftBounds() {
        int left = getLeftStart();
        int top = getHeight() / 2 - getDrawableHeight(0) / 2;
        int right = left + mDrawableLeft.getIntrinsicWidth();
        int bottom = top + getDrawableHeight(0);
        return new Rect(left, top, right, bottom);
    }

    /**
     * 顶部Drawable的位置
     *
     * @return rect
     */
    private Rect getTopBounds() {
        int left = getWidth() / 2 - getDrawableWidth(1) / 2;
        int top = getTopStart();
        int right = left + getDrawableWidth(1);
        int bottom = top + mDrawableTop.getIntrinsicHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * 右边Drawable的位置
     *
     * @return rect
     */
    private Rect getRightBounds() {
        int left = getLeftStart() + getDrawableWidth(0) + getMiddleWidth() + mDrawablePadding;
        int top = getHeight() / 2 - getDrawableHeight(2) / 2;
        int right = left + mDrawableRight.getIntrinsicWidth();
        int bottom = top + getDrawableHeight(2);
        return new Rect(left, top, right, bottom);
    }

    /**
     * 底部Drawable的位置
     *
     * @return rect
     */
    private Rect getBottomBounds() {
        int left = getWidth() / 2 - getDrawableWidth(3) / 2;
        int top = getTopStart() + getDrawableHeight(1) + getMiddleHeight() + mDrawablePadding;
        int right = left + getDrawableWidth(3);
        int bottom = top + mDrawableBottom.getIntrinsicHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * 两行文字的位置
     *
     * @return rect
     */
    private Rect getTextRect() {
        int left = getLeftStart() + getDrawableWidth(0);
        int top = getTopStart() + getDrawableHeight(1);
        int right = left + getMiddleWidth();
        int bottom = top + getMiddleHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * 第一行文字的位置
     *
     * @return x，y
     */
    private Point getTopTextPoint(Rect rect) {
        int x = (int) (rect.centerX() - getTopTextWidth() / 2);
        int y = (int) (rect.centerY() - (getTopTextHeight() + getBottomTextHeight() + mTextPadding) / 2
                - mTopTextPaint.ascent());
        return new Point(x, y);
    }

    /**
     * 第二行文字的位置
     *
     * @return x，y
     */
    private Point getBottomTextPoint(Rect rect) {
        int x = (int) (rect.centerX() - getBottomTextWidth() / 2);
        int y = (int) (rect.centerY() - (getTopTextHeight() + getBottomTextHeight() + mTextPadding) / 2
                + getTopTextHeight() + mTextPadding - mBottomTextPaint.ascent());
        return new Point(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawableLeft != null) {
            mDrawableLeft.setBounds(getLeftBounds());
            mDrawableLeft.draw(canvas);
        }
        if (mDrawableTop != null) {
            mDrawableTop.setBounds(getTopBounds());
            mDrawableTop.draw(canvas);
        }
        if (mDrawableRight != null) {
            mDrawableRight.setBounds(getRightBounds());
            mDrawableRight.draw(canvas);
        }
        if (mDrawableBottom != null) {
            mDrawableBottom.setBounds(getBottomBounds());
            mDrawableBottom.draw(canvas);
        }
        Rect rect = getTextRect();
        canvas.drawText(mTopText, getTopTextPoint(rect).x, getTopTextPoint(rect).y, mTopTextPaint);
        canvas.drawText(mBottomText, getBottomTextPoint(rect).x, getBottomTextPoint(rect).y, mBottomTextPaint);
        drawLine(canvas);
    }

    /**
     * 画线
     */
    private void drawLine(Canvas canvas) {
        if (mLeftBorderLineWidth > 0) {
            canvas.drawRect(new Rect(0, 0, mLeftBorderLineWidth, canvas.getHeight()),
                    mLeftLinePaint);
        }
        if (mTopBorderLineWidth > 0) {
            canvas.drawRect(new Rect(0, 0, canvas.getWidth(), mTopBorderLineWidth),
                    mTopLinePaint);
        }
        if (mRightBorderLineWidth > 0) {
            canvas.drawRect(new Rect(canvas.getWidth() - mRightBorderLineWidth, 0, canvas.getWidth(), canvas.getHeight()),
                    mRightLinePaint);
        }
        if (mBottomBorderLineWidth > 0) {
            canvas.drawRect(new Rect(0, canvas.getHeight() - mBottomBorderLineWidth, canvas.getWidth(), canvas.getHeight()),
                    mBottomLinePaint);
        }
    }

}
