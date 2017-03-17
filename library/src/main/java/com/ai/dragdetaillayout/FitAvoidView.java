package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * <b>Project:</b> DragDetailLayout <br>
 * <b>Create Date:</b> 2017/3/12 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 宜忌View，上面是宜忌的图片，下面是可换行的文字 <br>
 * {R.styleable.FitAvoidView_android_src 图片}
 * {R.styleable.FitAvoidView_android_text 文字，也可以调用代码设置}
 * {R.styleable.FitAvoidView_android_textColor 文字颜色}
 * {R.styleable.FitAvoidView_android_textSize 文字大小}
 * {R.styleable.FitAvoidView_pictextPadding 文字和图片之间的间距}
 */
public class FitAvoidView extends View {

    private Drawable mDrawable = null;
    private ColorStateList mTextColor = null;
    private int mCurrentColor;
    private String mText = "";
    private int mPictextPadding = 0;
    private int mTextSize = 15;

    private TextPaint mTextPaint;
    private StaticLayout mStaticLayout;

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

    public FitAvoidView(Context context) {
        this(context, null);
    }

    public FitAvoidView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitAvoidView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FitAvoidView);
        mDrawable = a.getDrawable(R.styleable.FitAvoidView_android_src);
        mTextSize = a.getDimensionPixelSize(R.styleable.FitAvoidView_android_textSize, 15);
        mTextColor = a.getColorStateList(R.styleable.FitAvoidView_android_textColor);
        mPictextPadding = a.getDimensionPixelSize(R.styleable.FitAvoidView_pictextPadding, 0);
        mText = (String) a.getText(R.styleable.FitAvoidView_android_text);

        int borderLineWidth = a.getDimensionPixelSize(R.styleable.FitAvoidView_borderLineWidth, 0);
        int borderLineColor = a.getColor(R.styleable.FitAvoidView_borderLineColor, DEF_COLOR);

        mLeftBorderLineWidth = a.getDimensionPixelSize(R.styleable.FitAvoidView_leftBorderLineWidth, borderLineWidth);
        mLeftBorderLineColor = a.getColor(R.styleable.FitAvoidView_leftBorderLineColor, borderLineColor);
        mTopBorderLineWidth = a.getDimensionPixelSize(R.styleable.FitAvoidView_topBorderLineWidth, borderLineWidth);
        mTopBorderLineColor = a.getColor(R.styleable.FitAvoidView_topBorderLineColor, borderLineColor);
        mRightBorderLineWidth = a.getDimensionPixelSize(R.styleable.FitAvoidView_rightBorderLineWidth, borderLineWidth);
        mRightBorderLineColor = a.getColor(R.styleable.FitAvoidView_rightBorderLineColor, borderLineColor);
        mBottomBorderLineWidth = a.getDimensionPixelSize(R.styleable.FitAvoidView_bottomBorderLineWidth, borderLineWidth);
        mBottomBorderLineColor = a.getColor(R.styleable.FitAvoidView_bottomBorderLineColor, borderLineColor);

        if (TextUtils.isEmpty(mText)) mText = "";
        a.recycle();
        initPaint();
    }

    private void initPaint() {
        // 初始化画笔并设置参数
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
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
     * @param string 需要设置的字符串
     */
    public void setText(String string) {
        mText = string;
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
        int height = getMeasureHeight(width, heightMeasureSpec);
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
            // 这里，如果文字宽度不确定的话，最大的文字宽度为图片宽度的3倍
            float textWidth = mTextPaint.measureText(mText);
            // 文字较多
            if (textWidth > mDrawable.getIntrinsicWidth()) {
                result = padding + mDrawable.getIntrinsicWidth() * 3;
            } else {
                result = padding + mDrawable.getIntrinsicWidth();
            }
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
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

    /**
     * 测量View的高度
     *
     * @param width             width
     * @param heightMeasureSpec heightMeasureSpec
     * @return 返回View的测量高度
     */
    private int getMeasureHeight(int width, int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
            mStaticLayout = new StaticLayout(
                    mText,
                    mTextPaint,
                    width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0F,//行间距
                    0.0F,// 在基础行距上添加多少,实际行间距等于这两者的和
                    false);
        } else {
            int padding = getPaddingTop() + getPaddingBottom();
            mStaticLayout = new StaticLayout(
                    mText,
                    mTextPaint,
                    width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0F,//行间距
                    0.0F,// 在基础行距上添加多少,实际行间距等于这两者的和
                    false);
            result = padding + mDrawable.getIntrinsicHeight() + mStaticLayout.getHeight() + mPictextPadding;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 获取最终的Drawable绘制的位置
     *
     * @return Rect
     */
    private Rect getDrawableBounds() {
        int left = getWidth() / 2 - mDrawable.getIntrinsicWidth() / 2;
        int top = getHeight() / 2 - (mDrawable.getIntrinsicHeight() + mPictextPadding + mStaticLayout.getHeight()) / 2;
        int right = left + mDrawable.getIntrinsicWidth();
        int bottom = top + mDrawable.getIntrinsicHeight();
        return new Rect(left, top, right, bottom);
    }

    /**
     * 获取文字绘制的位置
     *
     * @return PointF
     */
    private PointF getTextLocation() {
        // float x = getWidth() / 2 - mStaticLayout.getWidth() / 2;
        // 这里我们初始化限定StaticLayout的宽度就是View的宽度，所以上面的值是0
        float x = getWidth() / 2;
        float y = mDrawable.getBounds().bottom + mPictextPadding;
        return new PointF(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable != null) {
            // 图片
            mDrawable.setBounds(getDrawableBounds());
            mDrawable.draw(canvas);
            canvas.save();

            // 文字
            PointF location = getTextLocation();
            canvas.translate(location.x, location.y);
            mTextPaint.setColor(mCurrentColor);
            mTextPaint.drawableState = getDrawableState();
            mStaticLayout.draw(canvas);
            canvas.restore();
            drawLine(canvas);
        }
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
