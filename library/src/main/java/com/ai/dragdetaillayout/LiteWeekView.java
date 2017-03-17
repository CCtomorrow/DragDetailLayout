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
 * <b>Create Date:</b> 2017/3/16 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 两行文字，第二行文字的右边可能存在一个假或者班的图标 <br>
 */
public class LiteWeekView extends View {

    private Drawable mDrawable = null;

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

    private int mTextPadding; // 两行文字之间的距离
    private int mDrawablePadding; // drawable和左边的文字之间的距离

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

    public LiteWeekView(Context context) {
        this(context, null);
    }

    public LiteWeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiteWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LiteWeekView);
        mDrawable = a.getDrawable(R.styleable.LiteWeekView_android_src);
        mTextPadding = a.getDimensionPixelSize(R.styleable.LiteWeekView_twotextPadding, 0);
        mDrawablePadding = a.getDimensionPixelSize(R.styleable.LiteWeekView_android_drawablePadding, 0);

        mTopTextColor = a.getColor(R.styleable.LiteWeekView_topTextColor, Color.BLACK);
        mTopTextSize = a.getDimensionPixelSize(R.styleable.LiteWeekView_topTextSize, 15);
        mTopTextBold = a.getBoolean(R.styleable.LiteWeekView_topTextBold, false);
        mTopText = (String) a.getText(R.styleable.LiteWeekView_topText);

        mBottomTextColor = a.getColor(R.styleable.LiteWeekView_bottomTextColor, Color.BLACK);
        mBottomTextSize = a.getDimensionPixelSize(R.styleable.LiteWeekView_bottomTextSize, 15);
        mBottomTextBold = a.getBoolean(R.styleable.LiteWeekView_bottomTextBold, false);
        mBottomText = (String) a.getText(R.styleable.LiteWeekView_bottomText);

        int borderLineWidth = a.getDimensionPixelSize(R.styleable.LiteWeekView_borderLineWidth, 0);
        int borderLineColor = a.getColor(R.styleable.LiteWeekView_borderLineColor, DEF_COLOR);

        mLeftBorderLineWidth = a.getDimensionPixelSize(R.styleable.LiteWeekView_leftBorderLineWidth, borderLineWidth);
        mLeftBorderLineColor = a.getColor(R.styleable.LiteWeekView_leftBorderLineColor, borderLineColor);
        mTopBorderLineWidth = a.getDimensionPixelSize(R.styleable.LiteWeekView_topBorderLineWidth, borderLineWidth);
        mTopBorderLineColor = a.getColor(R.styleable.LiteWeekView_topBorderLineColor, borderLineColor);
        mRightBorderLineWidth = a.getDimensionPixelSize(R.styleable.LiteWeekView_rightBorderLineWidth, borderLineWidth);
        mRightBorderLineColor = a.getColor(R.styleable.LiteWeekView_rightBorderLineColor, borderLineColor);
        mBottomBorderLineWidth = a.getDimensionPixelSize(R.styleable.LiteWeekView_bottomBorderLineWidth, borderLineWidth);
        mBottomBorderLineColor = a.getColor(R.styleable.LiteWeekView_bottomBorderLineColor, borderLineColor);

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

    /**
     * 设置drawable
     *
     * @param drawable 需要设置的图片
     */
    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        re();
    }

    /**
     * 设置drawable
     *
     * @param res 需要设置的图片资源，必须是图片，不然后面的计算宽高就会出问题
     */
    public void setDrawable(int res) {
        mDrawable = ViewUtil.getDrawable(getContext(), res);
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

    private int getMeasureWidth(int widthMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int padding = getPaddingLeft() + getPaddingRight();
            float bottomWidth = getBottomTextWidth() + getDrawableWidth();
            result = (int) (padding + Math.max(getTopTextWidth(), bottomWidth));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int getMeasureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int padding = getPaddingTop() + getPaddingBottom();
            float bottomHeight = Math.max(getDrawableHeight(), getBottomTextHeight());
            result = (int) (padding + getTopTextHeight() + mTextPadding + bottomHeight);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 获取drawable的宽度
     *
     * @return 宽度
     */
    private int getDrawableWidth() {
        int drawableWidth = mDrawable != null ? mDrawable.getIntrinsicWidth() + mDrawablePadding : 0;
        return drawableWidth;
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
     * @return 宽度
     */
    private float getBottomTextWidth() {
        float bottomTextWidth = mBottomTextPaint.measureText(mBottomText);
        return bottomTextWidth;
    }

    /**
     * 获取drawable的高度
     *
     * @return 高度
     */
    private int getDrawableHeight() {
        int drawableHeight = mDrawable != null ? mDrawable.getIntrinsicHeight() : 0;
        return drawableHeight;
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

    private Point getTopTextPoint() {
        int x = (int) (getWidth() / 2 - getTopTextWidth() / 2);
        float bottomHeight = Math.max(getDrawableHeight(), getBottomTextHeight());
        int height = (int) (getTopTextHeight() + mTextPadding + bottomHeight);
        int y = getHeight() / 2 - height / 2;
        return new Point(x, (int) (y - mTopTextPaint.ascent()));
    }

    /**
     * 获取底部的文字和右边图片的位置，然后细分
     */
    private Rect getBottomBounds(Point topTextPoint) {
        float bottomWidth = getBottomTextWidth() + getDrawableWidth();
        float bottomHeight = Math.max(getDrawableHeight(), getBottomTextHeight());
        int left = (int) (getWidth() / 2 - bottomWidth / 2);
        int top = (int) (topTextPoint.y + mTopTextPaint.descent() + mTextPadding);
        int right = (int) (left + bottomWidth);
        int bottom = (int) (top + bottomHeight);
        return new Rect(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Point textPoint = getTopTextPoint();
        canvas.drawText(mTopText, textPoint.x, textPoint.y, mTopTextPaint);
        Rect bottomBounds = getBottomBounds(textPoint);
        // 把TextView画在Rect的中间
        int y = (int) (bottomBounds.centerY() - (mBottomTextPaint.ascent() + mBottomTextPaint.descent()) / 2);
        canvas.drawText(mBottomText, bottomBounds.left, y, mBottomTextPaint);
        if (mDrawable != null) {
            int left = bottomBounds.right - mDrawable.getIntrinsicWidth();
            int top = bottomBounds.centerY() - mDrawable.getIntrinsicHeight() / 2;
            int right = bottomBounds.right;
            int bottom = bottomBounds.centerY() + mDrawable.getIntrinsicHeight() / 2;
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(canvas);
        }

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
