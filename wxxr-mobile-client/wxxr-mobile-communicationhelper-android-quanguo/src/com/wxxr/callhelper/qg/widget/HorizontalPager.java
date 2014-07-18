/*
 * @(#)HorizontalPager.java	 2011-12-21
 *
 * Copyright 2004-2011 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.wxxr.callhelper.qg.R;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

/**
 * @class desc A HorizontalPager.
 * 
 * @author liuzhongnan
 * @version $Revision: 1.2 $
 * @created time 2011-12-21  下午3:56:16
 */
public class HorizontalPager extends ViewGroup
{
    public static final String TAG = "DeezApps.Widget.HorizontalPager";

    private static final int INVALID_SCREEN = -1; //无效屏
    public static final int SPEC_UNDEFINED = -1;

    /**
     * The velocity at which a fling gesture will cause us to snap to the next screen
     */
    private static final int SNAP_VELOCITY = 1000;  //速率

    private int pageWidthSpec, pageWidth;

    private boolean mFirstLayout = true;

    private int mCurrentPage;
    private int mNextPage = INVALID_SCREEN;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker; //速率追踪

    private int mTouchSlop; //溢出
    private int mMaximumVelocity; //最大速率

    private float mLastMotionX;
    private float mLastMotionY;

    private final static int TOUCH_STATE_REST = 0;  // 手势状态 休息
    private final static int TOUCH_STATE_SCROLLING = 1; //滚动

    private int mTouchState = TOUCH_STATE_REST;

    private boolean mAllowLongPress;

    private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>();

    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     */
    public HorizontalPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     * @param defStyle Unused.
     */
    public HorizontalPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_wxxr_callhelper_widget_HorizontalPager);
        pageWidthSpec = a.getDimensionPixelSize(R.styleable.com_wxxr_callhelper_widget_HorizontalPager_pageWidth, SPEC_UNDEFINED);
        Log.d("pageWidthSpec-----", pageWidthSpec+"");
        a.recycle();

        init();
    }

    /**
     * Initializes various states for this workspace.
     */
    private void init() {
        mScroller = new Scroller(getContext());
        mCurrentPage = 0;

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
      //  LLog.d("mTouchSlop-----", mTouchSlop+"");
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
      //  LLog.d("mMaximumVelocity-----", mMaximumVelocity+"");
        
    }

    /**
     * Returns the index of the currently displayed page.
     *
     * @return The index of the currently displayed page.
     */
    int getCurrentPage() {
        return mCurrentPage;
    }

    /**
     * Sets the current page.
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        mCurrentPage = Math.max(0, Math.min(currentPage, getChildCount()));
        scrollTo(getScrollXForPage(mCurrentPage), 0);
        invalidate();
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(int pageWidth) {
        this.pageWidthSpec = pageWidth;
    }

    /**
     * Gets the value that getScrollX() should return if the specified page is the current page (and no other scrolling is occurring).
     * Use this to pass a value to scrollTo(), for example.
     * @param whichPage
     * @return
     */
    private int getScrollXForPage(int whichPage) {
        return (whichPage * pageWidth) - pageWidthPadding();
    }

    /*
     * 重写了父类的computeScroll()；主要功能是计算拖动的位移量、更新背景、设置要显示的屏幕（setCurrentScreen(mCurrentScreen);）。
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {//是否移动新的位置
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mNextPage != INVALID_SCREEN) {
            mCurrentPage = mNextPage;
            mNextPage = INVALID_SCREEN;
            clearChildrenCache();
        }
    }

    /*
     * 重写了父类的dispatchDraw()；主要功能是判断抽屉是否打开、绘制指定的屏幕，可以绘制当前一屏，
     * 也可以绘制当前屏幕和下一屏幕，也可以绘制所有的屏幕，这儿的绘制指显示屏幕上的child（例如：app、folder、Wiget）。
     * 和computeScroll()中的setCurrentScreen(mCurrentScreen);方法配合使用可以实现屏幕的拖动多少显示多少的功能。
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {

        // ViewGroup.dispatchDraw() supports many features we don't need:
        // clip to padding, layout animation, animation listener, disappearing
        // children, etc. The following implementation attempts to fast-track
        // the drawing dispatch by drawing only what we know needs to be drawn.

        final long drawingTime = getDrawingTime(); //开始绘画时间
        //  be smarter about which children need drawing
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            drawChild(canvas, getChildAt(i), drawingTime);
        }

        for (OnScrollListener mListener : mListeners) {
            int adjustedScrollX = getScrollX() + pageWidthPadding();
            mListener.onScroll(adjustedScrollX);
            if (adjustedScrollX % pageWidth == 0) {
                mListener.onViewScrollFinished(adjustedScrollX / pageWidth);
            }
        }
    }

    int pageWidthPadding() {
        return ((getMeasuredWidth() - pageWidth) / 2);
    }

    /*
     * 重写了父类的onMeasure()；主要功能是设置屏幕的显示大小。由每个child的measure()方法设置。
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        pageWidth = pageWidthSpec == SPEC_UNDEFINED ? getMeasuredWidth() : pageWidthSpec;
        pageWidth = Math.min(pageWidth, getMeasuredWidth());

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        }

        if (mFirstLayout) {
            scrollTo(getScrollXForPage(mCurrentPage), 0);
            mFirstLayout = false;
        }
    }

    /*
     *重写了父类的onLayout()；主要功能是设置屏幕的显示位置。由child的layout()方法设置。
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    //当组里的某个子项需要被定位在屏幕的某个矩形范围时，调用此方法。
    /*
     * 重载此方法的ViewGroup可确认以下几点：
		· 子项目将是组里的直系子项
		· 矩形将在子项目的坐标体系中
		重载此方法的ViewGroup必须保证以下几点：
		· 若矩形已经是可见的，则没有东西会改变
		· 为使矩形区域全部可见，视图将可以被滚动显示
		参数
		child 发出请求的子项目
		rect 子项目坐标系内的矩形，即此子项目希望在屏幕上的定位
		immediate 设为true，则禁止动画和缓释移动滚动条
		返回
		这个可滚动显示的组，是否接受请求
     */
    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int screen = indexOfChild(child);
        if (screen != mCurrentPage || !mScroller.isFinished()) {
            return true;
        }
        return false;
    }

    /*
     * 当在滚动视图的子视图中查找焦点视图时，需要注意不要将焦点设置在滚动出屏幕外的控件上。此方法会比执行缺省的ViewGroup代价高，否则此行为也会设置为缺省
	　　	参数　direction 指定下列常量之一：FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT
		previouslyFocusedRect 能够给出一个较好的提示的矩形（当前视图的坐标系统）表示焦点从哪里得来。如果没有提示为null。
	　　	返回值是否取得了焦点
     */
    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int focusableScreen;
        if (mNextPage != INVALID_SCREEN) {
            focusableScreen = mNextPage;
        } else {
            focusableScreen = mCurrentPage;
        }
        if( getChildAt(focusableScreen)!=null)
        getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);
        return false;
    }

    /*
    对于获得焦点的View，这个方法是捕获箭头事件最后的机会。这就是在获取焦点的View没有在内部处理、系统在要求的方向也不能找到一个新的View让其获得焦点时调用。
	参数
	focused ????当前焦点View
	direction焦点移动的方向。其中之一:FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT和 FOCUS_RIGHT.
	返回值
	如果为true，将清除这个View未处理的事件。
	（注：从源码中可看出ZoomButton覆盖了父类的该方法，在super之前调用了一下clearFocus，如下代码：
     */
    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentPage() > 0) {
                snapToPage(getCurrentPage() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentPage() < getChildCount() - 1) {
                snapToPage(getCurrentPage() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction) {
        getChildAt(mCurrentPage).addFocusables(views, direction);
        if (direction == View.FOCUS_LEFT) {
            if (mCurrentPage > 0) {
                getChildAt(mCurrentPage - 1).addFocusables(views, direction);
            }
        } else if (direction == View.FOCUS_RIGHT){
            if (mCurrentPage < getChildCount() - 1) {
                getChildAt(mCurrentPage + 1).addFocusables(views, direction);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.d(TAG, "onInterceptTouchEvent::action=" + ev.getAction());

        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            Log.d(TAG, "onInterceptTouchEvent::shortcut=true");
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        
       // LLog.v("XXXXXXXXXXXXXXXXXXXXXXX", x+"");
        //LLog.v("YYYYYYYYYYYYYYYYYYYYY", y+"");
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */
                if (mTouchState == TOUCH_STATE_REST && ev.getY()>50) {
                    checkStartScroll(x, y);
                }

                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mAllowLongPress = true;

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Release the drag
                clearChildrenCache();
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mTouchState != TOUCH_STATE_REST;
    }

    private void checkStartScroll(float x, float y) {
        /*
         * Locally do absolute value. mLastMotionX is set to the y value
         * of the down event.
         */
        final int xDiff = (int) Math.abs(x - mLastMotionX);
        final int yDiff = (int) Math.abs(y - mLastMotionY);

        boolean xMoved = xDiff > mTouchSlop;
        boolean yMoved = yDiff > mTouchSlop;

        if (xMoved || yMoved) {

            if (xMoved) {
                // Scroll if the user moved far enough along the X axis
                mTouchState = TOUCH_STATE_SCROLLING;
                enableChildrenCache();
            }
            // Either way, cancel any pending longpress
            if (mAllowLongPress) {
                mAllowLongPress = false;
                // Try canceling the long press. It could also have been scheduled
                // by a distant descendant, so use the mAllowLongPress flag to block
                // everything
                final View currentScreen = getChildAt(mCurrentPage);
                currentScreen.cancelLongPress();
            }
        }
    }

    void enableChildrenCache() {
        setChildrenDrawingCacheEnabled(true); //缓存绘制
        setChildrenDrawnWithCacheEnabled(true);
    }

    void clearChildrenCache() {
        setChildrenDrawnWithCacheEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*
                * If being flinged and user touches, stop the fling. isFinished
                * will be false if being flinged.
                */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_REST) {
                    checkStartScroll(x, y);
                } else if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event
                    int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;

                    // Apply friction to scrolling past boundaries.
                    if (getScrollX() < 0 || getScrollX() > getChildAt(getChildCount() - 1).getLeft()) {
                        deltaX /= 2;
                    }

                    scrollBy(deltaX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY && mCurrentPage > 0) {
                        // Fling hard enough to move left
                        snapToPage(mCurrentPage - 1);
                    } else if (velocityX < -SNAP_VELOCITY && mCurrentPage < getChildCount() - 1) {
                        // Fling hard enough to move right
                        snapToPage(mCurrentPage + 1);
                    } else {
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
        }

        return true;
    }

    private void snapToDestination() {
        final int startX = getScrollXForPage(mCurrentPage);
        int whichPage = mCurrentPage;
        if (getScrollX() < startX - getWidth()/8) {
            whichPage = Math.max(0, whichPage-1);
        } else if (getScrollX() > startX + getWidth()/8) {
            whichPage = Math.min(getChildCount()-1, whichPage+1);
        }

        snapToPage(whichPage);
    }

    void snapToPage(int whichPage) {
        enableChildrenCache();

        boolean changingPages = whichPage != mCurrentPage;

        mNextPage = whichPage;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && changingPages && focusedChild == getChildAt(mCurrentPage)) {
            focusedChild.clearFocus();
        }

        final int newX = getScrollXForPage(whichPage);
        final int delta = newX - getScrollX();
        mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState state = new SavedState(super.onSaveInstanceState());
        state.currentScreen = mCurrentPage;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.currentScreen != INVALID_SCREEN) {
            mCurrentPage = savedState.currentScreen;
        }
    }

    public void scrollLeft() {
        if (mNextPage == INVALID_SCREEN && mCurrentPage > 0 && mScroller.isFinished()) {
            snapToPage(mCurrentPage - 1);
        }
    }

    public void scrollRight() {
        if (mNextPage == INVALID_SCREEN && mCurrentPage < getChildCount() - 1 && mScroller.isFinished()) {
            snapToPage(mCurrentPage + 1);
        }
    }

    public int getScreenForView(View v) {
        int result = -1;
        if (v != null) {
            ViewParent vp = v.getParent();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (vp == getChildAt(i)) {
                    return i;
                }
            }
        }
        return result;
    }

    /**
     * @return True is long presses are still allowed for the current touch
     */
    public boolean allowLongPress() {
        return mAllowLongPress;
    }

    public static class SavedState extends BaseSavedState {
        int currentScreen = -1;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentScreen = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentScreen);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
            new Parcelable.Creator<SavedState>() {
                public SavedState createFromParcel(Parcel in) {
                    return new SavedState(in);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            };
    }

    public void addOnScrollListener(OnScrollListener listener) {
        mListeners.add(listener);
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Implement to receive events on scroll position and page snaps.
     */
    public static interface OnScrollListener {
        /**
         * Receives the current scroll X value.  This value will be adjusted to assume the left edge of the first
         * page has a scroll position of 0.  Note that values less than 0 and greater than the right edge of the
         * last page are possible due to touch events scrolling beyond the edges.
         * @param scrollX Scroll X value
         */
        void onScroll(int scrollX);

        /**
         * Invoked when scrolling is finished (settled on a page, centered).
         * @param currentPage The current page
         */
        void onViewScrollFinished(int currentPage);
    }
}
