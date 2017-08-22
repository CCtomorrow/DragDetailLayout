package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * <b>Project:</b> almanac <br>
 * <b>Create Date:</b> 2017/8/21 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 今日五行TextView，最上面一行是标题，下面是内容 <br>
 * <b>Description:</b> 标题和内容 <br>
 */
public class TitleContentView extends View {

    private static final int defcolor = 0xFF151515;

    ColorStateList textColor = null;
    int textSize = 15;
    int textPadding = 0;
    boolean changeColor;
    TextPaint textPaint;

    String title = "标题";
    String content[] = new String[]{
            "你是傻 ",
            "瞅你 咋地",
            "再问 打死你"
    };

    public TitleContentView(Context context) {
        this(context, null);
    }

    public TitleContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HLTitleContentView);
        textColor = a.getColorStateList(R.styleable.HLTitleContentView_android_textColor);
        textSize = a.getDimensionPixelSize(R.styleable.HLTitleContentView_android_textSize, textSize);
        textPadding = a.getDimensionPixelSize(R.styleable.HLTitleContentView_twotextPadding, textPadding);
        a.recycle();
        if (textColor == null) {
            textColor = ColorStateList.valueOf(0xFF151515);
        } else {
            changeColor = true;
        }
        initPaint();
    }

    private void initPaint() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setColor(defcolor);
        textPaint.setTextSize(textSize);
    }

    /**
     * 设置标题和内容，内容是一行一个数组的
     *
     * @param title
     * @param content
     */
    public void setText(String title, String... content) {
        this.title = title;
        this.content = content;
        requestLayout();
        invalidate();
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        changeColor = true;
        textColor = ColorStateList.valueOf(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (TextUtils.isEmpty(title) || content == null || TextUtils.isEmpty(content[0])) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = getMeasureWidth(widthMeasureSpec);
        int height = getMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 获取最大的宽度，拿标题的和内容的最长的那行的宽度对比即可
     *
     * @return width
     */
    private float getMaxWidth() {
        textPaint.setTypeface(Typeface.DEFAULT);
        float cwith = textPaint.measureText(content[0]);
        if (content.length > 1) {
            for (int i = 1; i < content.length; i++) {
                float w = textPaint.measureText(content[i]);
                if (w > cwith) {
                    cwith = w;
                }
            }
        }
        String twidthStr = title;
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float twidth = textPaint.measureText(twidthStr);
        return Math.max(twidth, cwith);
    }

    /**
     * 获取最大的高度，拿标题的和内容的的高度相加即可
     *
     * @return width
     */
    private float getMaxHeight() {
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        // ascent为负数在baseline上面，安卓系统坐标，向右边和下边为正
        float theight = textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent;
        textPaint.setTypeface(Typeface.DEFAULT);
        float cheight = textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent;
        return theight + cheight * content.length + textPadding * content.length;
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
            result = (int) (padding + getMaxWidth());
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
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            // ascent为负数在baseline上面，安卓系统坐标，向右边和下边为正
            result = (int) (padding + getMaxHeight());
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(title) || content == null || TextUtils.isEmpty(content[0])) {
            super.onDraw(canvas);
            return;
        }
        canvas.save();
        textPaint.setColor(textColor.getColorForState(getDrawableState(), 0));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float ty = getPaddingTop() - textPaint.getFontMetrics().ascent;
        canvas.drawText(title, getWidth() / 2 - textPaint.measureText(title) / 2, ty, textPaint);
        textPaint.setTypeface(Typeface.DEFAULT);
        for (int i = 0; i < content.length; i++) {
            String item = content[i];
            float begin = getWidth() / 2 - textPaint.measureText(item) / 2;
            if (changeColor) {
                int index = item.lastIndexOf(" ");
                if (index < 0 || index + 1 >= item.length()) {
                    textPaint.setColor(defcolor);
                    drawNosplit(canvas, item, begin, i, ty);
                } else {
                    String left = item.substring(0, index + 1);
                    textPaint.setColor(defcolor);
                    drawNosplit(canvas, left, begin, i, ty);

                    String right = item.substring(index + 1, item.length());
                    textPaint.setColor(textColor.getColorForState(getDrawableState(), 0));
                    drawNosplit(canvas, right, begin + textPaint.measureText(left), i, ty);
                }
            } else {
                textPaint.setColor(defcolor);
                drawNosplit(canvas, item, begin, i, ty);
            }
        }
        canvas.restore();
    }

    private void drawNosplit(Canvas canvas, String des, float begin, int index, float ty) {
        canvas.drawText(des, begin, ty + (textPaint.getFontSpacing() + textPadding) * (index + 1), textPaint);
    }

}
