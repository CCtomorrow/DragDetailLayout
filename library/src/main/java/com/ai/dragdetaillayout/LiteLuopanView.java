package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * <b>Project:</b> DragDetailLayout <br>
 * <b>Create Date:</b> 2017/3/21 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 简版罗盘View <br>
 */
public class LiteLuopanView extends View {

    // 注意这个View是专门放置罗盘和元宝的，所以，默认这两个View都不会为null
    private Bitmap mLuopan = null; // 罗盘
    private Bitmap mGold = null; // 元宝

    private TextPaint
            mTopTextPaint = null,
            mBottomTextPaint = null;

    private Paint mBitmapPaint = null;

    private int mTopTextColor = Color.BLACK;
    private int mTopTextSize = 15;
    private boolean mTopTextBold = false; // 是否加粗
    private String mTopText = "";

    private int mBottomTextColor = Color.BLACK;
    private int mBottomTextSize = 15;
    private boolean mBottomTextBold = false; // 是否加粗
    private String mBottomText = "";

    private int mTextPadding = 0; // 两行文字之间的距离
    private int mDrawablePadding = 0; // drawable和文字之间的距离

    private static int DEF_COLOR = Color.parseColor("#ECDED0");

    private int mBorderLineWidth = 0;
    private int mBorderLineColor = DEF_COLOR;

    private int mLeftBorderLineWidth = mBorderLineWidth;
    private int mLeftBorderLineColor = mBorderLineColor;
    private int mTopBorderLineWidth = mBorderLineWidth;
    private int mTopBorderLineColor = mBorderLineColor;
    private int mRightBorderLineWidth = mBorderLineWidth;
    private int mRightBorderLineColor = mBorderLineColor;
    private int mBottomBorderLineWidth = mBorderLineWidth;
    private int mBottomBorderLineColor = mBorderLineColor;

    private Paint
            mLeftLinePaint = null,
            mTopLinePaint = null,
            mRightLinePaint = null,
            mBottomLinePaint = null;

    /**
     * 方向，从0开始，7结束，南0，西南1，东南7
     */
    private int mDirection = 1;

    public LiteLuopanView(Context context) {
        this(context, null);
    }

    public LiteLuopanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiteLuopanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LiteLuopanView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.LiteLuopanView_lite_img_src) {
                // 这样可以得到只是图片的宽高，然后缩放加载
                // BitmapFactory.Options options = new BitmapFactory.Options();
                // options.inJustDecodeBounds = true;
                mLuopan = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
            } else if (attr == R.styleable.LiteLuopanView_lite_compass_gold_src) {
                mGold = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
            } else if (attr == R.styleable.LiteLuopanView_twotextPadding) {
                mTextPadding = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.LiteLuopanView_android_drawablePadding) {
                mDrawablePadding = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.LiteLuopanView_topText) {
                mTopText = (String) a.getText(attr);
            } else if (attr == R.styleable.LiteLuopanView_topTextSize) {
                mTopTextSize = a.getDimensionPixelSize(attr, 15);
            } else if (attr == R.styleable.LiteLuopanView_topTextBold) {
                mTopTextBold = a.getBoolean(attr, false);
            } else if (attr == R.styleable.LiteLuopanView_topTextColor) {
                mTopTextColor = a.getColor(attr, Color.BLACK);
            } else if (attr == R.styleable.LiteLuopanView_bottomText) {
                mBottomText = (String) a.getText(attr);
            } else if (attr == R.styleable.LiteLuopanView_bottomTextSize) {
                mBottomTextSize = a.getDimensionPixelSize(attr, 15);
            } else if (attr == R.styleable.LiteLuopanView_bottomTextBold) {
                mBottomTextBold = a.getBoolean(attr, false);
            } else if (attr == R.styleable.LiteLuopanView_bottomTextColor) {
                mBottomTextColor = a.getColor(attr, Color.BLACK);
            } else if (attr == R.styleable.LiteLuopanView_borderLineWidth) {
                mBorderLineWidth = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.LiteLuopanView_borderLineColor) {
                mBorderLineColor = a.getColor(attr, DEF_COLOR);
            } else if (attr == R.styleable.LiteLuopanView_leftBorderLineWidth) {
                mLeftBorderLineWidth = a.getDimensionPixelSize(attr, mBorderLineWidth);
            } else if (attr == R.styleable.LiteLuopanView_leftBorderLineColor) {
                mLeftBorderLineColor = a.getColor(attr, mBorderLineColor);
            } else if (attr == R.styleable.LiteLuopanView_topBorderLineWidth) {
                mTopBorderLineWidth = a.getDimensionPixelSize(attr, mBorderLineWidth);
            } else if (attr == R.styleable.LiteLuopanView_topBorderLineColor) {
                mTopBorderLineColor = a.getColor(attr, mBorderLineColor);
            } else if (attr == R.styleable.LiteLuopanView_rightBorderLineWidth) {
                mRightBorderLineWidth = a.getDimensionPixelSize(attr, mBorderLineWidth);
            } else if (attr == R.styleable.LiteLuopanView_rightBorderLineColor) {
                mRightBorderLineColor = a.getColor(attr, mBorderLineColor);
            } else if (attr == R.styleable.LiteLuopanView_bottomBorderLineWidth) {
                mBottomBorderLineWidth = a.getDimensionPixelSize(attr, mBorderLineWidth);
            } else if (attr == R.styleable.LiteLuopanView_bottomBorderLineColor) {
                mBottomBorderLineColor = a.getColor(attr, mBorderLineColor);
            }
        }
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

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

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

    public void setDirection(int direction) {
        if (direction != mDirection) {
            mDirection = direction;
            invalidate();
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

    private void re() {
        requestLayout();
        invalidate();
    }

    public void setTopTextColor(int topTextColor) {
        if (topTextColor != mTopTextColor) {
            mTopTextColor = topTextColor;
            invalidate();
        }
    }

    public void setBottomTextColor(int bottomTextColor) {
        if (bottomTextColor != mBottomTextColor) {
            mBottomTextColor = bottomTextColor;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureWidth(widthMeasureSpec);
        int height = getMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private float getLuoPanWidth() {
        float width = mLuopan == null ? 0 : mLuopan.getWidth();
        return width;
    }

    private float getGoldWidth() {
        float width = (mGold == null || mLuopan == null) ? 0 : mGold.getWidth();
        return width;
    }

    private float getTopTextWidth() {
        float topTextWidth = mTopTextPaint.measureText(mTopText);
        return topTextWidth;
    }

    private float getBottomTextWidth() {
        float bottomTextWidth = mBottomTextPaint.measureText(mBottomText);
        return bottomTextWidth;
    }

    private int getMeasureWidth(int widthMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int padding = getPaddingLeft() + getPaddingRight();
            int textWidth = (int) (getTopTextWidth() + getBottomTextWidth() + mTextPadding);
            int bitmapWidth = (int) (getLuoPanWidth() + (getGoldWidth() > 0 ? getGoldWidth() / 2 : 0));
            result = padding + Math.max(textWidth, bitmapWidth);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private float getLuoPanHeight() {
        float height = mLuopan == null ? 0 : mLuopan.getHeight();
        return height;
    }

    private float getGoldHeight() {
        float height = (mGold == null || mLuopan == null) ? 0 : mGold.getHeight();
        return height;
    }

    private float getTopTextHeight() {
        float topTextHeight = mTopTextPaint.descent() - mTopTextPaint.ascent();
        return topTextHeight;
    }

    private float getBottomTextHeight() {
        float bottomTextHeight = mBottomTextPaint.descent() - mBottomTextPaint.ascent();
        return bottomTextHeight;
    }

    private int getMeasureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int padding = getPaddingTop() + getPaddingBottom();
            int textHeight = (int) Math.max(getTopTextHeight(), getBottomTextHeight());
            int bitmapHeight = (int) (getLuoPanHeight() + (getGoldHeight() > 0 ? getGoldHeight() / 2 : 0));
            result = padding + textHeight + mDrawablePadding + bitmapHeight;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 画的左边和上边的起始位置
     */
    private Point getStart() {
        int textWidth = (int) (getTopTextWidth() + getBottomTextWidth() + mTextPadding);
        int bitmapWidth = (int) (getLuoPanWidth() + (getGoldWidth() > 0 ? getGoldWidth() / 2 : 0));
        int textHeight = (int) Math.max(getTopTextHeight(), getBottomTextHeight());
        int bitmapHeight = (int) (getLuoPanHeight() + (getGoldHeight() > 0 ? getGoldHeight() / 2 : 0));
        int x = getWidth() / 2 - (Math.max(textWidth, bitmapWidth)) / 2;
        int y = getHeight() / 2 - (textHeight + mDrawablePadding + bitmapHeight) / 2;
        return new Point(x, y);
    }

    private Rect getBitmapRect(Point point) {
        int bitmapWidth = (int) (getLuoPanWidth() + (getGoldWidth() > 0 ? getGoldWidth() / 2 : 0));
        int left = getWidth() / 2 - bitmapWidth / 2;
        int top = point.y;
        int right = left + bitmapWidth;
        int bitmapHeight = (int) (getLuoPanHeight() + (getGoldHeight() > 0 ? getGoldHeight() / 2 : 0));
        int bottom = top + bitmapHeight;
        return new Rect(left, top, right, bottom);
    }

    private Rect getTextRect(Rect rect) {
        int textWidth = (int) (getTopTextWidth() + getBottomTextWidth() + mTextPadding);
        int textHeight = (int) Math.max(getTopTextHeight(), getBottomTextHeight());
        int left = getWidth() / 2 - textWidth / 2;
        int top = rect.bottom + mDrawablePadding;
        int right = left + textWidth;
        int bottom = top + textHeight;
        return new Rect(left, top, right, bottom);
    }

    private Point getTopTextPoint(Rect rect) {
        int x = rect.left;
        int y = (int) (rect.centerY() - (mTopTextPaint.ascent() + mTopTextPaint.descent()) / 2);
        return new Point(x, y);
    }

    private Point getBottomTextPoint(Rect rect) {
        int x = (int) (rect.right - getBottomTextWidth());
        int y = (int) (rect.centerY() - (mBottomTextPaint.ascent() + mBottomTextPaint.descent()) / 2);
        return new Point(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);
        Point start = getStart();
        Rect bitmapRect = getBitmapRect(start);
        Rect textRect = getTextRect(bitmapRect);

        Paint p = new Paint();
        p.setColor(Color.GREEN);
        canvas.drawRect(bitmapRect, p);

        // 方向，从0开始，7结束，南0，西南1，东南7
        switch (mDirection) {
            case 0:
                canvas.drawBitmap(
                        mLuopan,
                        bitmapRect.left + getGoldWidth() / 4,
                        bitmapRect.top,
                        mBitmapPaint);
                canvas.drawBitmap(
                        mGold,
                        bitmapRect.centerX() - getGoldWidth() / 2,
                        bitmapRect.bottom - getGoldHeight(),
                        mBitmapPaint);
                break;
            case 1:
                canvas.drawBitmap(
                        mLuopan,
                        bitmapRect.left + getGoldWidth() / 4,
                        bitmapRect.top + getGoldHeight() / 4,
                        mBitmapPaint);
                canvas.drawBitmap(mGold, getCirclePoint(bitmapRect, 1).x + getGoldWidth() / 2,
                        getCirclePoint(bitmapRect, 1).y + getGoldHeight() / 2, mBitmapPaint);
                break;
            case 2:
                canvas.drawBitmap(
                        mLuopan,
                        bitmapRect.left + getGoldWidth() / 2,
                        bitmapRect.top + getGoldHeight() / 4,
                        mBitmapPaint);
                canvas.drawBitmap(
                        mGold,
                        bitmapRect.left,
                        bitmapRect.centerY() - getGoldHeight() / 2,
                        mBitmapPaint);
                break;
            case 3:
                canvas.drawBitmap(
                        mLuopan,
                        bitmapRect.left + getGoldWidth() / 4,
                        bitmapRect.top + getGoldHeight() / 4,
                        mBitmapPaint);
                break;
            case 4:
                canvas.drawBitmap(
                        mLuopan,
                        (int) (bitmapRect.left + getGoldWidth() / 4),
                        bitmapRect.top + getGoldHeight() / 2,
                        mBitmapPaint);
                canvas.drawBitmap(
                        mGold,
                        bitmapRect.centerX() - getGoldWidth() / 2,
                        bitmapRect.top,
                        mBitmapPaint);
                break;
            case 5:
                canvas.drawBitmap(
                        mLuopan,
                        bitmapRect.left + getGoldWidth() / 4,
                        bitmapRect.top + getGoldHeight() / 4,
                        mBitmapPaint);
                break;
            case 6:
                canvas.drawBitmap(
                        mLuopan,
                        bitmapRect.left,
                        bitmapRect.top + getGoldHeight() / 4,
                        mBitmapPaint);
                canvas.drawBitmap(
                        mGold,
                        bitmapRect.right - getGoldWidth(),
                        bitmapRect.centerY() - getGoldHeight() / 2,
                        mBitmapPaint);
                break;
            case 7:
                canvas.drawBitmap(
                        mLuopan,
                        bitmapRect.left + getGoldWidth() / 4,
                        bitmapRect.top + getGoldHeight() / 4,
                        mBitmapPaint);
                break;
        }
        Point leftPoint = getTopTextPoint(textRect);
        Point rightPoint = getBottomTextPoint(textRect);
        canvas.drawText(mTopText, leftPoint.x, leftPoint.y, mTopTextPaint);
        canvas.drawText(mBottomText, rightPoint.x, rightPoint.y, mBottomTextPaint);
        drawLine(canvas);
    }

    /**
     * x1=x0+r*cos(角度*3.14/180)
     * y1=y0+r*sin(角度*3.14/180)
     *
     * @param direction
     * @return
     */
    private Point getCirclePoint(Rect bitmapRect, int direction) {
        if (direction == 1) {
            int x = (int) (bitmapRect.centerX() - getLuoPanWidth() / 2 * (225 * Math.PI / 180));
            int y = (int) (bitmapRect.centerY() - getLuoPanHeight() / 2 * (225 * Math.PI / 180));
            return new Point(x, y);
        }
        return null;
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
