package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * <b>Project:</b> DragDetailLayout <br>
 * <b>Create Date:</b> 2017/2/28 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 包含两个View的Layout，可以滑动显示后面的Layout <br>
 */
public class SlidingBehindLayout extends ViewGroup {

    private static final String TAG = SlidingBehindLayout.class.getSimpleName();

    /**
     * 滑动的时间
     */
    private int mDuration = 200;
    /**
     * 到一个View的百分之多少可以滑动到下一个View上去的
     */
    private float mPercent = 0.3F;
    /**
     * 获取Fling速度的最大值
     */
    private int mMaxFlingVelocity;
    /**
     * 获取Fling速度的最小值
     */
    private int mMiniFlingVelocity;
    /**
     * 速度跟踪器
     */
    private VelocityTracker mVelocityTracker;
    /**
     * 获取touchSlop,该值表示系统所能识别出的被认为是滑动的最小距离
     */
    private float mTouchSlop;

    private float mDownMotionY;
    private float mDownMotionX;
    private float mInitialInterceptY;

    private View mBehindView;
    private View mFrontView;

    /**
     * 默认展示top
     */
    private int mCurrentIndex = SlidingBehindLayoutPageState.FRONT;

    /**
     * 是不是允许拦截事件，默认允许
     */
    private boolean mInterceptEvent = true;

    @IntDef(value = {SlidingBehindLayoutPageState.FRONT, SlidingBehindLayoutPageState.BEHIND})
    public @interface IndexMask {

    }

    public class SlidingBehindLayoutPageState {
        public static final int FRONT = 1;
        public static final int BEHIND = 2;

        private int custate = 0;

        public SlidingBehindLayoutPageState() {
        }

        public SlidingBehindLayoutPageState(int custate) {
            this.custate = custate;
        }

        public int getCustate() {
            return custate;
        }

        public void setCustate(int custate) {
            this.custate = custate;
        }
    }

    /**
     * 内部View滑动完成的回调
     */
    private SlidingBehindLayoutOnChangeListener mChangeListener;

    public interface SlidingBehindLayoutOnChangeListener {
        void onStatueChanged(SlidingBehindLayoutPageState state);
    }

    public void setChangeListener(SlidingBehindLayoutOnChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    /**
     * 子view是否可以滑动的回调，可以通过它，就不用覆盖实现{@link SlidingBehindLayout#canViewScrollVertically(View, int)}
     */
    private OnChildScrollCallback mScrollCallback;

    public interface OnChildScrollCallback {
        /**
         * @param parent    父View
         * @param child     当前操作的View
         * @param direction 负数表示是否可以下滑，正数表示是否可以上滑
         * @return 是否子某个方向上还能继续滑动
         */
        boolean canScrollVertically(SlidingBehindLayout parent, View child, int direction);
    }

    public void setScrollCallback(OnChildScrollCallback scrollCallback) {
        mScrollCallback = scrollCallback;
    }

    public SlidingBehindLayout(Context context) {
        this(context, null);
    }

    public SlidingBehindLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingBehindLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mMaxFlingVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMiniFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    /**
     * 设置显示哪个View，{@link SlidingBehindLayoutPageState#FRONT} or {@link SlidingBehindLayoutPageState#BEHIND}
     *
     * @param index
     */
    public void switchIndex(@IndexMask int index) {
        mCurrentIndex = index;
        if (mCurrentIndex == SlidingBehindLayoutPageState.FRONT) {
            smtoFront();
        } else if (mCurrentIndex == SlidingBehindLayoutPageState.BEHIND) {
            smtoBehind();
        }
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public View getFrontView() {
        return mFrontView;
    }

    public View getBehindView() {
        return mBehindView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBehindView = getChildAt(0);
        mFrontView = getChildAt(1);
        mBehindView.setAlpha(0);
        mFrontView.setAlpha(1);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 允许拦截事件
        requestDisallowInterceptTouchEvent(!mInterceptEvent);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否允许此父布局拦截拦截事件，true允许拦截，false不允许拦截
     *
     * @param intercept true允许拦截，false不允许拦截
     */
    public void interceptEvent(boolean intercept) {
        if (intercept == mInterceptEvent) return;
        mInterceptEvent = intercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetDownPosition(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                return checkShouldInterceptEvent(ev);
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 只有上面的onInterceptTouchEvent判断到一定情况把事件拦截之后才会走到这里的
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                upHandle();
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_MOVE:
                scrolltoPosition(ev);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 滑动到Front
     */
    public void smtoFront() {
        Log.e(TAG, "smtoFront===>alpha===>FrontView:" + mFrontView.getAlpha() + "===>BehindView:" + mBehindView.getAlpha());
        Log.e(TAG, "smtoFront===>translationY===>FrontView:" + mFrontView.getTranslationY() +
                "===>BehindView:" + mBehindView.getTranslationY());
        // 这下面的两个值都是mFrontView最后会达到的值
        // 第一个是mFrontView最后translationY到达0，第二个是透明度mFrontView最后到达1
        // 1230--->0 -1230需要滑动
        int needScrollDistance = 0;
        // 0.48--->1 1-0.48需要渐变
        int needScrollAlpha = 1;
        mFrontView
                .animate()
                .translationYBy(needScrollDistance - mFrontView.getTranslationY())
                .alphaBy(needScrollAlpha - mFrontView.getAlpha())
                .setDuration(mDuration)
                .start();
        // 0.52--->0 1-0.52-1需要渐变
        mBehindView
                .animate()
                .alphaBy((1 - mBehindView.getAlpha() - needScrollAlpha))
                .setDuration(mDuration)
                .start();
    }

    /**
     * 滑动到Behind
     */
    public void smtoBehind() {
        Log.e(TAG, "smtoBehind===>alpha===>FrontView:" + mFrontView.getAlpha() + "===>BehindView:" + mBehindView.getAlpha());
        Log.e(TAG, "smtoBehind===>translationY===>FrontView:" + mFrontView.getTranslationY() +
                "===>BehindView:" + mBehindView.getTranslationY());
        // 这下面的两个值都是mFrontView最后会达到的值
        // 第一个是最后translationY到达mBehindView.getHeight()，第二个是透明度mFrontView最后到达0
        // 123--->mBehindView.getHeight() mBehindView.getHeight()-123需要滑动
        int needScrollDistance = mBehindView.getHeight();
        // 0.52--->0 0-0.52需要渐变
        int needScrollAlpha = 0;
        mFrontView
                .animate()
                .translationYBy(needScrollDistance - mFrontView.getTranslationY())
                .alphaBy(needScrollAlpha - mFrontView.getAlpha())
                .setDuration(mDuration)
                .start();
        // 0.48--->1 1-0.48-0
        mBehindView
                .animate()
                .alphaBy(1 - mBehindView.getAlpha() - needScrollAlpha)
                .setDuration(mDuration)
                .start();
    }

    /**
     * up或者cancel事件的处理
     */
    private void upHandle() {
        int height = mBehindView.getHeight();
        float distance = (int) mFrontView.getTranslationY();
        int percentView = (int) (height * mPercent);
        if (mCurrentIndex == SlidingBehindLayoutPageState.FRONT) {
            boolean couldToNext = Math.abs(distance) >= percentView;
            if (couldToNext || needFlingToToggleView()) {
                Log.e(TAG, "滑动到behind");
                smtoBehind();
                mCurrentIndex = SlidingBehindLayoutPageState.BEHIND;
            } else {
                Log.e(TAG, "滑动回front");
                smtoFront();
                mCurrentIndex = SlidingBehindLayoutPageState.FRONT;
            }
        } else if (mCurrentIndex == SlidingBehindLayoutPageState.BEHIND) {
            // frontView的translationY，是正值，这里由于上拉，会小于BehindView的height
            distance = height - distance; // 已经上拉的部分，只要这个高度>=30%的height即表示可以滑动到FrontView的位置
            boolean couldToNext = Math.abs(distance) >= percentView;
            if (couldToNext || needFlingToToggleView()) {
                Log.e(TAG, "滑动到front");
                smtoFront();
                mCurrentIndex = SlidingBehindLayoutPageState.FRONT;
            } else {
                Log.e(TAG, "滑动回behind");
                smtoBehind();
                mCurrentIndex = SlidingBehindLayoutPageState.BEHIND;
            }
        }
        if (mChangeListener != null) {
            mChangeListener.onStatueChanged(new SlidingBehindLayoutPageState(mCurrentIndex));
        }
    }

    /**
     * 检测速度，如果速度到了还是需要滑动的
     *
     * @return 速度达到一定值，就算距离没达到也可以滑动到下一个Layout去
     */
    private boolean needFlingToToggleView() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
        if (mCurrentIndex == SlidingBehindLayoutPageState.BEHIND) {
            if (-mVelocityTracker.getYVelocity() > mMiniFlingVelocity * 4) {
                return true;
            }
        } else if (mCurrentIndex == SlidingBehindLayoutPageState.FRONT) {
            if (mVelocityTracker.getYVelocity() > mMiniFlingVelocity * 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否需要拦截事件
     * 1.mFrontView的底部
     * 2.mBehindView的顶部
     *
     * @param ev MotionEvent
     * @return 是否需要拦截事件
     */
    private boolean checkShouldInterceptEvent(MotionEvent ev) {
        // 获取当前在哪个界面
        final float xDiff = ev.getX() - mDownMotionX;
        final float yDiff = ev.getY() - mDownMotionY;
        // 当前的Layout
        View currentTargetView = getCurrentTargetView();
        // View在顶部检测下滑，底部检测上滑
        boolean result = canScrollVertically(currentTargetView, (int) -yDiff, ev);
        if (!result) { // 不能滑动
            mInitialInterceptY = ev.getY();
            if (yDiff == 0) return false;
            // 是在Y轴方向上滑动，即是竖直滑动的
            boolean isScrollY = Math.abs(yDiff) > mTouchSlop && Math.abs(yDiff) >= Math.abs(xDiff);

            // front拦截的情况
            boolean interceptFront = mCurrentIndex == SlidingBehindLayoutPageState.FRONT && (
                    // 顶部的时候，拦截下拉
                    (mFrontView.getScrollY() <= 0 && yDiff >= 0));

            // behind拦截上拉
            boolean interceptBehind = mCurrentIndex == SlidingBehindLayoutPageState.BEHIND &&
                    (mBehindView.getScrollY() >= 0 && yDiff <= 0);

            Log.e(TAG, "interceptFront:" + interceptFront);
            Log.e(TAG, "interceptBehind:" + interceptBehind);

            if (isScrollY && (interceptFront || interceptBehind)) {
                return true;
            }
        }
//        Log.e(TAG, "Height:" + currentTargetView.getHeight()
//                + "===>ScrollY:" + currentTargetView.getScrollY()
//                + "===>yDiff:" + yDiff);
        return false;
    }

    /**
     * View是否可以竖直滑动
     *
     * @param view   view
     * @param offSet 方向
     * @param ev     MotionEvent
     * @return 在某个方向是否可以滑动
     */
    private boolean canScrollVertically(View view, int offSet, MotionEvent ev) {
        // 如果没有Touch到当前的View上面
        if (!isTouchedView(ev, view)) {
            return false;
        }
        if (mScrollCallback != null) {
            return mScrollCallback.canScrollVertically(this, view, offSet);
        }
        // 关于这个方法的解释，官方的其实不靠谱
        return canViewScrollVertically(view, offSet);
    }

    /**
     * View在某个方向是否可以滑动
     * 子类如果要自己去实现判断的方法可以自行实现，可能需要{@link #getCurrentIndex()}拿到当前是在Front还是Behind然后进行判断
     *
     * @param direction 负数表示是否可以下滑，正数表示是否可以上滑
     * @return 是否可以滑动
     */
    public boolean canViewScrollVertically(View view, int direction) {
        return view.canScrollVertically(direction);
    }

    /**
     * 滑动
     *
     * @param event MotionEvent
     */
    private void scrolltoPosition(MotionEvent event) {
        float needScrollAlpha = 1;
        float needScrollDistance = 0;
        float distance = event.getY() - mInitialInterceptY;
        // 滑动的这个距离占整个BehindView的百分百
        float percent = Math.abs(distance) / mBehindView.getHeight();
        Log.e(TAG, "===>distance:" + distance + "===>percent:" + percent);
        if (mCurrentIndex == SlidingBehindLayoutPageState.FRONT) {
            if (distance >= 0) {
                // 下滑才操作
                needScrollAlpha = 1 - percent;
                needScrollDistance = distance;
            }
        } else {
            if (distance < 0) {
                // 上滑才有，这个时候distance是负值
                int topviewHeight = mFrontView.getHeight();
                needScrollAlpha = percent;
                needScrollDistance = topviewHeight + distance;
            }
        }
        mFrontView.setTranslationY(needScrollDistance);
        mFrontView.setAlpha(needScrollAlpha);
        mBehindView.setAlpha(1 - needScrollAlpha);
        mVelocityTracker.addMovement(event);
        Log.e(TAG, "alpha===>FrontView:" + mFrontView.getAlpha() + "===>BehindView:" + mBehindView.getAlpha());
        Log.e(TAG, "translationY===>FrontView:" + mFrontView.getTranslationY() + "===>BehindView:" + mBehindView.getTranslationY());
    }

    /**
     * 重置用户第一次按下的数据
     *
     * @param ev MotionEvent
     */
    private void resetDownPosition(MotionEvent ev) {
        mDownMotionX = ev.getX();
        mDownMotionY = ev.getY();
        mInitialInterceptY = (int) ev.getY();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.clear();
    }

    /**
     * 清理VelocityTracker
     */
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 获取当前的操作的Layout
     *
     * @return {@link SlidingBehindLayout#mFrontView} or {@link SlidingBehindLayout#mBehindView}
     */
    public View getCurrentTargetView() {
        if (mCurrentIndex == SlidingBehindLayoutPageState.FRONT) {
            return mFrontView;
        } else {
            return mBehindView;
        }
    }

    /**
     * 判断MotionEvent是否处于View上面
     *
     * @param ev   MotionEvent
     * @param view view
     * @return true or false
     */
    protected boolean isTouchedView(MotionEvent ev, View view) {
        float x = ev.getRawX();
        float y = ev.getRawY();
        int[] rect = new int[2];
        view.getLocationInWindow(rect);
        float localX = x - rect[0];
        float localY = y - rect[1];
        return localX >= 0 && localX < (view.getRight() - view.getLeft())
                && localY >= 0 && localY < (view.getBottom() - view.getTop());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 声明临时变量存储父容器的期望值，该值应该等于父容器的内边距加上所有子元素的测量宽高和外边距
        int parentDesireWidth = 0;
        int parentDesireHeight = 0;
        // 声明临时变量存储子元素的测量状态
        int childMeasureState = 0;
        // 如果父容器内有子元素
        if (getChildCount() > 0) {
            // 那么就遍历子元素
            for (int i = 0; i < getChildCount(); i++) {
                // 获取对应遍历下标的子元素
                View child = getChildAt(i);
                SlidingLayoutParams lp = (SlidingLayoutParams) child.getLayoutParams();
                // 如果该子元素没有以“不占用空间”的方式隐藏则表示其需要被测量计算
                // 只有在Front，前面的View才需要叠加高度
                if (child.getVisibility() != View.GONE && lp.defaultLocation == 0) {
                    // 测量子元素并考量其外边距
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    // 考量外边距计算子元素实际宽高
                    parentDesireWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    parentDesireHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                    // 合并子元素的测量状态
                    childMeasureState = combineMeasuredStates(childMeasureState, child.getMeasuredState());
                } else {
                    // BehindView也是需要测量的
                    // 测量子元素并考量其外边距
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                }
            }
            // 考量父容器内边距将其累加到期望值
            parentDesireWidth += getPaddingLeft() + getPaddingRight();
            parentDesireHeight += getPaddingTop() + getPaddingBottom();
            // 尝试比较父容器期望值与Android建议的最小值大小并取较大值
            parentDesireWidth = Math.max(parentDesireWidth, getSuggestedMinimumWidth());
            parentDesireHeight = Math.max(parentDesireHeight, getSuggestedMinimumHeight());
        }
        // 确定父容器的测量宽高
        setMeasuredDimension(resolveSizeAndState(parentDesireWidth, widthMeasureSpec, childMeasureState),
                resolveSizeAndState(parentDesireHeight, heightMeasureSpec, childMeasureState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 获取父容器内边距
        int parentPaddingLeft = getPaddingLeft();
        int parentPaddingTop = getPaddingTop();
        // 如果有子元素
        if (getChildCount() > 0) {
            // 声明一个临时变量存储高度倍增值
            int mutilHeight = 0;
            // 那么遍历子元素并对其进行定位布局
            for (int i = 0; i < getChildCount(); i++) {
                // 获取一个子元素
                View child = getChildAt(i);
                SlidingLayoutParams lp = (SlidingLayoutParams) child.getLayoutParams();
                int left = parentPaddingLeft + lp.leftMargin;
                int right = child.getMeasuredWidth() + parentPaddingLeft + lp.leftMargin;
                if (lp.defaultLocation == 0) {
                    // 通知子元素进行布局
                    // 此时考虑父容器内边距和子元素外边距的影响
                    int top = mutilHeight + parentPaddingTop + lp.topMargin;
                    int bottom = child.getMeasuredHeight() + mutilHeight + parentPaddingTop + lp.topMargin;
                    child.layout(left, top, right, bottom);
                    // 改变高度倍增值
                    mutilHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                } else {
                    // 通知子元素进行布局
                    // 此时考虑父容器内边距和子元素外边距的影响
                    int top = parentPaddingTop + lp.topMargin;
                    int bottom = child.getMeasuredHeight() + parentPaddingTop + lp.topMargin;
                    child.layout(left, top, right, bottom);
                }
            }
        }
    }

    /*
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int position = i;
        for (int j = 0; j < childCount; j++) {
            View child = getChildAt(i);
            HomePageLayoutParams lp = (HomePageLayoutParams) child.getLayoutParams();
            if (lp.defaultLocation != 0) {
                position = j;
            }
        }
        if (i == 0) {
            return position;
        }
        // 最先绘制底层的
        if (i == position) {
            return 0;
        }
        return super.getChildDrawingOrder(childCount, i);
    }*/

    //================LayoutParams================
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new SlidingLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new SlidingLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new SlidingLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof SlidingLayoutParams;
    }

    public class SlidingLayoutParams extends MarginLayoutParams {

        /**
         * 默认在前面，有一个View可以在其他View的后面
         */
        protected int defaultLocation;

        public SlidingLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray array = c.obtainStyledAttributes(attrs, R.styleable.DragDetailLayoutOther);
            defaultLocation = array.getInt(R.styleable.DragDetailLayoutOther_layout_location, 0);
            array.recycle();
        }

        public SlidingLayoutParams(int width, int height) {
            super(width, height);
        }

        public SlidingLayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public SlidingLayoutParams(LayoutParams source) {
            super(source);
        }
    }
    //================LayoutParams================

}
