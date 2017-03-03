package com.ai.dragdetaillayout;

import android.content.Context;
import android.content.res.TypedArray;
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
 * <b>Description:</b> 包含三个View的Layout，具体看Demo图，不好的一点是中间Layout必须是Match_parent的 <br>
 */
public class DragDetailLayout extends ViewGroup {

    private static final String TAG = DragDetailLayout.class.getSimpleName();

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
    private View mTopView;
    private View mBottomView;

    /**
     * 默认展示top
     */
    private int mCurrentIndex = DragDetailLayoutPageState.TOP;

    public class DragDetailLayoutPageState {
        public static final int TOP = 1;
        public static final int BOTTOM = 2;
        public static final int BEHIND = 3;

        private int custate = 0;

        public DragDetailLayoutPageState() {
        }

        public DragDetailLayoutPageState(int custate) {
            this.custate = custate;
        }

        public int getCustate() {
            return custate;
        }

        public void setCustate(int custate) {
            this.custate = custate;
        }
    }

    private DragDetailLayoutOnChangeListener mChangeListener;

    public interface DragDetailLayoutOnChangeListener {
        void onStatueChanged(DragDetailLayoutPageState state);
    }

    public void setChangeListener(DragDetailLayoutOnChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    public DragDetailLayout(Context context) {
        this(context, null);
    }

    public DragDetailLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mMaxFlingVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMiniFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBehindView = getChildAt(0);
        mTopView = getChildAt(1);
        mBottomView = getChildAt(2);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 允许拦截事件
        requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
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
     * up或者cancel事件的处理
     */
    private void upHandle() {
        int needScrollDistance;
        if (mCurrentIndex == DragDetailLayoutPageState.TOP) {
            int height = mTopView.getHeight();
            int distance = (int) mTopView.getTranslationY();
            // 当此次的滑动到了一个View的30%或者用户定义的值之后可以滑动到下一个View
            int percentView = (int) (height * mPercent);
            boolean couldToNext = Math.abs(distance) >= percentView;
            if (couldToNext || needFlingToToggleView()) {
                // top的话要判断当前是往哪个方向滑动的
                if (distance > 0) {
                    Log.e(TAG, "滑动到behind");
                    // 下滑的，显示BehindView
                    needScrollDistance = height;
                    mCurrentIndex = DragDetailLayoutPageState.BEHIND;
                } else {
                    Log.e(TAG, "滑动到bottom");
                    // 上滑的，显示BottomView
                    needScrollDistance = -height;
                    mCurrentIndex = DragDetailLayoutPageState.BOTTOM;
                }
            } else {
                Log.e(TAG, "滑动回top");
                // 滑回去
                needScrollDistance = 0;
                mCurrentIndex = DragDetailLayoutPageState.TOP;
            }
            mTopView.animate().translationYBy(needScrollDistance - mTopView.getTranslationY())
                    .setDuration(mDuration).start();
            mBottomView.animate().translationYBy(needScrollDistance - mBottomView.getTranslationY())
                    .setDuration(mDuration).start();
            //mTopView.setTranslationY(needScrollDistance);
            //mBottomView.setTranslationY(needScrollDistance);
        } else if (mCurrentIndex == DragDetailLayoutPageState.BOTTOM) {
            int heiht = mBottomView.getHeight();
            int distance = (int) mBottomView.getTranslationY();
            int percentView = (int) (heiht * mPercent);
            int topHeight = mTopView.getHeight();
            distance = distance + topHeight;
            boolean couldToNext = Math.abs(distance) >= percentView;
            if (couldToNext || needFlingToToggleView()) {
                Log.e(TAG, "滑动到top");
                needScrollDistance = 0;
                mCurrentIndex = DragDetailLayoutPageState.TOP;
            } else {
                Log.e(TAG, "滑动回bottom");
                // 滑回去
                needScrollDistance = -topHeight;
                mCurrentIndex = DragDetailLayoutPageState.BOTTOM;
            }
            mTopView.animate().translationYBy(needScrollDistance - mTopView.getTranslationY())
                    .setDuration(mDuration).start();
            mBottomView.animate().translationYBy(needScrollDistance - mBottomView.getTranslationY())
                    .setDuration(mDuration).start();
            //mTopView.setTranslationY(needScrollDistance);
            //mBottomView.setTranslationY(needScrollDistance);
        } else if (mCurrentIndex == DragDetailLayoutPageState.BEHIND) {
            int heiht = mBehindView.getHeight();
            // topview的translationY，是正值，这里由于上拉，会小于height
            float distance = (int) mTopView.getTranslationY();
            int percentView = (int) (heiht * mPercent);
            distance = heiht - distance; // 已经上拉的部分，只要这个高度>=30%的height即表示可以滑动到TopView的位置
            boolean couldToNext = Math.abs(distance) >= percentView;
            if (couldToNext || needFlingToToggleView()) {
                Log.e(TAG, "滑动到top");
                needScrollDistance = 0;
                mCurrentIndex = DragDetailLayoutPageState.TOP;
            } else {
                needScrollDistance = heiht;
                Log.e(TAG, "滑动回behind");
                mCurrentIndex = DragDetailLayoutPageState.BEHIND;
            }
            mTopView.animate().translationYBy(needScrollDistance - mTopView.getTranslationY())
                    .setDuration(mDuration).start();
            mBottomView.animate().translationYBy(needScrollDistance - mBottomView.getTranslationY())
                    .setDuration(mDuration).start();
            //mTopView.setTranslationY(needScrollDistance);
            //mBottomView.setTranslationY(needScrollDistance);
        }
        if (mChangeListener != null) {
            mChangeListener.onStatueChanged(new DragDetailLayoutPageState(mCurrentIndex));
        }
    }

    /**
     * 检测速度，如果速度到了还是需要滑动的
     *
     * @return
     */
    private boolean needFlingToToggleView() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
        if (mCurrentIndex == DragDetailLayoutPageState.BEHIND) {
            if (-mVelocityTracker.getYVelocity() > mMiniFlingVelocity * 4) {
                return true;
            }
        } else if (mCurrentIndex == DragDetailLayoutPageState.BOTTOM) {
            if (mVelocityTracker.getYVelocity() > mMiniFlingVelocity * 4) {
                return true;
            }
        } else {
            if (Math.abs(mVelocityTracker.getYVelocity()) > mMiniFlingVelocity * 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否需要拦截事件
     * 1.TopView的顶部
     * 2.TopView的底部
     * 3.BottomView的顶部
     *
     * @param ev
     * @return
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
            // 是在Y轴方向上滑动，即是竖直滑动的
            boolean isScrollY = Math.abs(yDiff) > mTouchSlop && Math.abs(yDiff) >= Math.abs(xDiff);

            // top拦截的情况
            boolean interceptTop = mCurrentIndex == DragDetailLayoutPageState.TOP && (
                    // 顶部的时候，拦截下拉
                    (mTopView.getScrollY() <= 0 && yDiff >= 0)
                            // 底部，拦截上拉
                            || (mTopView.getScrollY() >= 0 && yDiff <= 0));
            // behind拦截上拉
            boolean interceptBehind = mCurrentIndex == DragDetailLayoutPageState.BEHIND &&
                    (mBehindView.getScrollY() >= 0 && yDiff <= 0);
            // bottom拦截下拉
            boolean interceptBottom = mCurrentIndex == DragDetailLayoutPageState.BOTTOM &&
                    (mBottomView.getScrollY() <= 0 && yDiff >= 0);

            Log.e(TAG, "interceptTop:" + interceptTop);
            Log.e(TAG, "interceptBehind:" + interceptBehind);
            Log.e(TAG, "interceptBottom:" + interceptBottom);

            if (isScrollY && (interceptTop || interceptBehind || interceptBottom)) {
                return true;
            }
        }
        Log.e(TAG, "Height:" + currentTargetView.getHeight()
                + "===>ScrollY:" + currentTargetView.getScrollY());
        return false;
    }

    /**
     * View是否可以竖直滑动
     *
     * @param view   view
     * @param offSet 方向
     * @param ev     MotionEvent
     * @return
     */
    private boolean canScrollVertically(View view, int offSet, MotionEvent ev) {
        // 如果没有Touch到当前的View上面
        if (!isTouchedView(ev, view)) {
            return false;
        }
        // 关于这个方法的解释，官方的其实不靠谱
        if (view.canScrollVertically(offSet)) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup vGroup = (ViewGroup) view;
            for (int i = 0; i < vGroup.getChildCount(); i++) {
                if (canScrollVertically(vGroup.getChildAt(i), offSet, ev)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 滑动
     *
     * @param event MotionEvent
     */
    private void scrolltoPosition(MotionEvent event) {
        float distance = event.getY() - mInitialInterceptY;
        // 滑动，上滑为正，下滑为负
        if (mCurrentIndex == DragDetailLayoutPageState.TOP) {
            mTopView.setTranslationY(distance);
            mBottomView.setTranslationY(distance);
        } else if (mCurrentIndex == DragDetailLayoutPageState.BOTTOM) {
            if (distance >= 0) {
                // 下滑才操作
                int topviewHeight = mTopView.getHeight();
                mTopView.setTranslationY(-topviewHeight + distance);
                mBottomView.setTranslationY(-topviewHeight + distance);
            }
        } else {
            if (distance < 0) {
                // 上滑才有
                int topviewHeight = mTopView.getHeight();
                mTopView.setTranslationY(topviewHeight + distance);
                mBottomView.setTranslationY(topviewHeight + distance);
            }
        }
        mVelocityTracker.addMovement(event);
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
     * @return
     */
    public View getCurrentTargetView() {
        if (mCurrentIndex == DragDetailLayoutPageState.TOP) {
            return mTopView;
        } else if (mCurrentIndex == DragDetailLayoutPageState.BOTTOM) {
            return mBottomView;
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
                DragDetailLayoutParams lp = (DragDetailLayoutParams) child.getLayoutParams();
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
                DragDetailLayoutParams lp = (DragDetailLayoutParams) child.getLayoutParams();
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
        return new DragDetailLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new DragDetailLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new DragDetailLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof DragDetailLayoutParams;
    }

    private class DragDetailLayoutParams extends MarginLayoutParams {

        /**
         * 默认在前面，有一个View可以在其他View的后面
         */
        protected int defaultLocation;

        public DragDetailLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray array = c.obtainStyledAttributes(attrs, R.styleable.DragDetailLayoutOther);
            defaultLocation = array.getInt(R.styleable.DragDetailLayoutOther_layout_location, 0);
            array.recycle();
        }

        public DragDetailLayoutParams(int width, int height) {
            super(width, height);
        }

        public DragDetailLayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public DragDetailLayoutParams(LayoutParams source) {
            super(source);
        }
    }

    //================LayoutParams================

}
