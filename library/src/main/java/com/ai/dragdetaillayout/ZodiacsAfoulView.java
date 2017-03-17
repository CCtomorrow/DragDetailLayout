package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * <b>Project:</b> DragDetailLayout <br>
 * <b>Create Date:</b> 2017/3/16 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 生肖相冲View，上面三张图片，下面是一行文字 <br>
 */
public class ZodiacsAfoulView extends View {

    private Drawable
            mDrawableLeft = null,
            mDrawableMiddle = null,
            mDrawableRight = null;
    private int mDrawablePadding;

    private ColorStateList mTextColor = null;
    private int mCurrentColor;
    private String mText = "";
    private int mPictextPadding = 0;
    private int mTextSize = 15;
    private boolean mTextBold;

    private TextPaint mTextPaint;

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

    public ZodiacsAfoulView(Context context) {
        this(context, null);
    }

    public ZodiacsAfoulView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZodiacsAfoulView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZodiacsAfoulView);
        mDrawableLeft = a.getDrawable(R.styleable.ZodiacsAfoulView_android_drawableLeft);
        mDrawableMiddle = a.getDrawable(R.styleable.ZodiacsAfoulView_drawableMiddle);
        mDrawableRight = a.getDrawable(R.styleable.ZodiacsAfoulView_android_drawableRight);
        mDrawablePadding = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_android_drawablePadding, 0);
        mText = (String) a.getText(R.styleable.ZodiacsAfoulView_android_text);
        mTextSize = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_android_textSize, 15);
        mTextColor = a.getColorStateList(R.styleable.ZodiacsAfoulView_android_textColor);
        mPictextPadding = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_pictextPadding, 0);
        mTextBold = a.getBoolean(R.styleable.ZodiacsAfoulView_textBold, false);

        int borderLineWidth = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_borderLineWidth, 0);
        int borderLineColor = a.getColor(R.styleable.ZodiacsAfoulView_borderLineColor, DEF_COLOR);

        mLeftBorderLineWidth = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_leftBorderLineWidth, borderLineWidth);
        mLeftBorderLineColor = a.getColor(R.styleable.ZodiacsAfoulView_leftBorderLineColor, borderLineColor);
        mTopBorderLineWidth = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_topBorderLineWidth, borderLineWidth);
        mTopBorderLineColor = a.getColor(R.styleable.ZodiacsAfoulView_topBorderLineColor, borderLineColor);
        mRightBorderLineWidth = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_rightBorderLineWidth, borderLineWidth);
        mRightBorderLineColor = a.getColor(R.styleable.ZodiacsAfoulView_rightBorderLineColor, borderLineColor);
        mBottomBorderLineWidth = a.getDimensionPixelSize(R.styleable.ZodiacsAfoulView_bottomBorderLineWidth, borderLineWidth);
        mBottomBorderLineColor = a.getColor(R.styleable.ZodiacsAfoulView_bottomBorderLineColor, borderLineColor);

        a.recycle();
        initPaint();
    }

    private void initPaint() {
        // 初始化画笔并设置参数
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        if (mTextBold) {
            mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if (mTextColor == null) {
            mTextColor = ColorStateList.valueOf(0xFF000000);
        }
        int color = mTextColor.getColorForState(getDrawableState(), 0);
        if (color != mCurrentColor) {
            mCurrentColor = color;
        }
        // mTextPaint.setTypeface(Typeface.DEFAULT_BOLD); // 粗体
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

    /**
     * 设置字符串
     *
     * @param text 需要设置的字符串
     */
    public void setText(String text) {
        mText = text;
        re();
    }

    /**
     * 设置字符串
     *
     * @param res 需要设置的字符串资源文件
     */
    public void setText(int res) {
        mText = getContext().getString(res);
        re();
    }

    /**
     * 设置左边的图片
     */
    public void setDrawableLeft(Drawable drawableLeft) {
        mDrawableLeft = drawableLeft;
        re();
    }

    /**
     * 设置左边的图片
     */
    public void setDrawableLeft(int res) {
        mDrawableLeft = ViewUtil.getDrawable(getContext(), res);
        re();
    }

    /**
     * 设置中间的图片
     */
    public void setDrawableMiddle(Drawable drawableMiddle) {
        mDrawableMiddle = drawableMiddle;
        re();
    }

    /**
     * 设置中间的图片
     */
    public void setDrawableMiddle(int res) {
        mDrawableMiddle = ViewUtil.getDrawable(getContext(), res);
        re();
    }

    /**
     * 设置右边的图片
     */
    public void setDrawableRight(Drawable drawableRight) {
        mDrawableRight = drawableRight;
        re();
    }

    /**
     * 设置右边的图片
     */
    public void setDrawableRight(int res) {
        mDrawableRight = ViewUtil.getDrawable(getContext(), res);
        re();
    }

    private void re() {
        requestLayout();
        invalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mTextColor != null && mTextColor.isStateful()) {
            updateTextColors();
        }
    }

    /**
     * 更新颜色
     */
    private void updateTextColors() {
        boolean inval = false;
        int color = mTextColor.getColorForState(getDrawableState(), 0);
        if (color != mCurrentColor) {
            mCurrentColor = color;
            inval = true;
        }
        if (inval) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureWidth(widthMeasureSpec);
        int height = getMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 获取drawable的宽度
     */
    private float getDrawableWidth() {
        float drawableWidth = mDrawableLeft.getIntrinsicWidth() + mDrawablePadding
                + mDrawableMiddle.getIntrinsicWidth() + mDrawablePadding
                + mDrawableRight.getIntrinsicWidth();
        return drawableWidth;
    }

    /**
     * 获取Text的宽度
     */
    private float getTextWidth() {
        return mTextPaint.measureText(mText);
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
            // 这里，如果文字宽度不确定的话，最大的文字宽度为图片宽度的3倍
            // 文字较多
            if (getTextWidth() > getDrawableWidth()) {
                result = (int) (padding + getTextWidth());
            } else {
                result = (int) (padding + getDrawableWidth());
            }
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 获取drawable的高度
     */
    private float getDrawableHeight() {
        int leftDrawableHeight = mDrawableLeft.getIntrinsicHeight();
        int middleDrawableHeight = mDrawableMiddle.getIntrinsicHeight();
        int rightDrawableHeight = mDrawableRight.getIntrinsicHeight();
        float drawableHeight = Math.max(leftDrawableHeight, middleDrawableHeight);
        drawableHeight = Math.max(drawableHeight, rightDrawableHeight);
        return drawableHeight;
    }

    /**
     * 获取文字的高度
     */
    private float getTextHeight() {
        return mTextPaint.descent() - mTextPaint.ascent();
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
            result = (int) (padding + getDrawableHeight() + mPictextPadding + getTextHeight());
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 获取三个drawable的rect
     *
     * @return rect
     */
    private Rect getAllDrawableRect() {
        int left = (int) (getWidth() / 2 - getDrawableWidth() / 2);
        int top = (int) (getHeight() / 2 - (getDrawableHeight() + mPictextPadding + getTextHeight()) / 2);
        int right = (int) (left + getDrawableWidth());
        int bottom = (int) (top + getDrawableHeight());
        return new Rect(left, top, right, bottom);
    }

    private Rect getMiddleRect(Rect allDrawableRect) {
        int left = allDrawableRect.centerX() - mDrawableMiddle.getIntrinsicWidth() / 2;
        int right = allDrawableRect.centerX() + mDrawableMiddle.getIntrinsicWidth() / 2;
        int top = allDrawableRect.centerY() - mDrawableMiddle.getIntrinsicHeight() / 2;
        int bottom = allDrawableRect.centerY() + mDrawableMiddle.getIntrinsicHeight() / 2;
        return new Rect(left, top, right, bottom);
    }

    private Rect getLeftRect(Rect middle) {
        int left = middle.left - mDrawablePadding - mDrawableLeft.getIntrinsicWidth();
        int right = middle.left - mDrawablePadding;
        int top = middle.centerY() - mDrawableLeft.getIntrinsicHeight() / 2;
        int bottom = middle.centerY() + mDrawableLeft.getIntrinsicHeight() / 2;
        return new Rect(left, top, right, bottom);
    }

    private Rect getRightRect(Rect middle) {
        int left = middle.right + mDrawablePadding;
        int right = middle.right - mDrawablePadding + mDrawableRight.getIntrinsicWidth();
        int top = middle.centerY() - mDrawableRight.getIntrinsicHeight() / 2;
        int bottom = middle.centerY() + mDrawableRight.getIntrinsicHeight() / 2;
        return new Rect(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect allDrawableRect = getAllDrawableRect();
        int textX = getWidth() / 2;
        int textY = (int) (allDrawableRect.bottom + mPictextPadding - mTextPaint.ascent());
        canvas.drawText(mText, textX, textY, mTextPaint);
        Rect middle = getMiddleRect(allDrawableRect);
        mDrawableMiddle.setBounds(middle);
        mDrawableMiddle.draw(canvas);
        mDrawableLeft.setBounds(getLeftRect(middle));
        mDrawableLeft.draw(canvas);
        mDrawableRight.setBounds(getRightRect(middle));
        mDrawableRight.draw(canvas);
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
