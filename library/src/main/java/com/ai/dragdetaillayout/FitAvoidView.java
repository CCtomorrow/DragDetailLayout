package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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
 * <b>Description:</b> 宜忌View <br>
 * {R.styleable.FitAvoidView_android_src 图片}
 * {R.styleable.FitAvoidView_android_text 文字，也可以调用代码设置}
 * {R.styleable.FitAvoidView_android_textColor 文字颜色}
 * {R.styleable.FitAvoidView_android_textSize 文字大小}
 * {R.styleable.FitAvoidView_pictextPadding 文字和图片之间的间距}
 */
public class FitAvoidView extends View {

    private BitmapDrawable mDrawable = null;
    private ColorStateList mTextColor = null;
    private int mCurrentColor;
    private String mText = "";
    private int mPictextPadding = 0;
    private int mTextSize = 15;

    private TextPaint mTextPaint;
    private StaticLayout mStaticLayout;

    public FitAvoidView(Context context) {
        this(context, null);
    }

    public FitAvoidView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitAvoidView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FitAvoidView);
        mDrawable = (BitmapDrawable) a.getDrawable(R.styleable.FitAvoidView_android_src);
        mTextSize = a.getDimensionPixelSize(R.styleable.FitAvoidView_android_textSize, 15);
        mTextColor = a.getColorStateList(R.styleable.FitAvoidView_android_textColor);
        mPictextPadding = a.getDimensionPixelSize(R.styleable.FitAvoidView_pictextPadding, 0);
        mText = (String) a.getText(R.styleable.FitAvoidView_android_text);
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
    }

    /**
     * 设置字符串
     *
     * @param string 需要设置的字符串
     */
    public void setText(String string) {
        mText = string;
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
        }
    }
}
