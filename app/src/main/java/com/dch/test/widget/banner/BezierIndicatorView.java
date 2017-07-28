package com.dch.test.widget.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrCoder on 2017/7/27 17:15
 * 描述：自定义贝塞尔效果指示器indicator
 * 邮箱：daichuanhao@caijinquan.com
 */
public class BezierIndicatorView extends View {
    private final String TAG = "BezierIndicatorView";
    private int mDefaultIndicatorColor = Color.BLUE;
    private int mMoveIndicatorColor = Color.RED;
    private int mIndicatorCount = 5;
    private float mIndicatorRadius = 10;
    private float mPreIndicatorRadius = 10;
    private float mIndicatorInterval = 10;
    private int mWidth;
    private int mHeight;
    private int mCurrentIndicatorPosition = 0;
    private float[] mIndicators;
    private float mDeltDistanceBetweenIndicator = 0;
    private Indicator mCurrentIndicator;
    private List<Indicator> mIndicatorList = new ArrayList<>();
    private Path mBezierPath;
    private Paint mDefaultIndicatorPaint;
    private Paint mMoveIndicatorPaint;
    private Paint mCircleLinePaint;
    private Indicator indicatorFromList;

    public BezierIndicatorView(Context context) {
        this(context, null);
    }

    public BezierIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDefaultIndicatorPaint = new Paint();
        mDefaultIndicatorPaint.setAntiAlias(true);
        mDefaultIndicatorPaint.setColor(mDefaultIndicatorColor);
        mDefaultIndicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mMoveIndicatorPaint = new Paint();
        mMoveIndicatorPaint.setAntiAlias(true);
        mMoveIndicatorPaint.setColor(mMoveIndicatorColor);
        mMoveIndicatorPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mCircleLinePaint = new Paint();
        mCircleLinePaint.setAntiAlias(true);
        mCircleLinePaint.setColor(mDefaultIndicatorColor);
        mCircleLinePaint.setStyle(Paint.Style.STROKE);
        mCircleLinePaint.setStrokeWidth(mIndicatorRadius*2);



        mBezierPath = new Path();

    }

    public void setIndicatorList(int count) {
        this.mIndicatorCount = count;
        mIndicators = new float[10];
        for (int i = 0; i < mIndicatorCount; i++) {
            Indicator indicator = new Indicator(mIndicatorRadius * (i * 2 + 1) + (i + 1) * mIndicatorInterval, mHeight / 2);
            mIndicatorList.add(indicator);
            mIndicators[2*i] = indicator.x;
            mIndicators[2*i+1] = indicator.y;
            if (i == mCurrentIndicatorPosition) {
                mCurrentIndicator = new Indicator(indicator.x, indicator.y);
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = measuredWidth;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = measuredWidth + getPaddingLeft() + getPaddingRight();
        } else {
            mWidth = (int) (getPaddingLeft() + getPaddingRight() + (mIndicatorCount + 1) * mIndicatorInterval + mIndicatorCount * 2 * mIndicatorRadius);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = measuredHeight;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            mHeight = measuredHeight + getPaddingTop() + getPaddingBottom();
        } else {
            mHeight = getPaddingTop() + getPaddingBottom() + 2 * (int) mIndicatorRadius + 10;
        }

        setMeasuredDimension(mWidth, mHeight);
        mIndicatorInterval = (mWidth - 2 * mIndicatorCount * mIndicatorRadius) / (1 + mIndicatorCount);
        mDeltDistanceBetweenIndicator = mIndicatorRadius * 2 + mIndicatorInterval;
    }

    Indicator indicator;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        for (int i = 0; i < mIndicatorList.size(); i++) {
//            if (i == mCurrentIndicatorPosition) {
//                continue;
//            } else {
//                indicator = mIndicatorList.get(i);
//                canvas.drawCircle(indicator.x, indicator.y, mIndicatorRadius, mDefaultIndicatorPaint);
//            }
//        }
        canvas.drawPoints(mIndicators,mCircleLinePaint);
        if (null != mMoveIndicator) {
            canvas.drawCircle(mMoveIndicator.x, mMoveIndicator.y, mIndicatorRadius, mMoveIndicatorPaint);
        }

        if (!flag)
        canvas.drawCircle(mCurrentIndicator.x, mCurrentIndicator.y, mPreIndicatorRadius, mMoveIndicatorPaint);


        canvas.drawPath(mBezierPath, mMoveIndicatorPaint);
    }

    private Indicator mMoveIndicator;
    private int mLastPosition = -1;
    private boolean flag = true;

    /**
     * @param xOffset xOffSet是一个介于0~1之间的映射两个indicator距离的值
     */
    public void indicatorMove(float xOffset) {
        if (xOffset < 0 && xOffset > 1) {
            throw new RuntimeException("offset偏移量必须介于0~1之间");
        }
        if (xOffset < 0.1) {
            mLastPosition = mCurrentIndicatorPosition;
            flag = false;
        }
        mPreIndicatorRadius = mIndicatorRadius * (1 - xOffset);
        if (mPreIndicatorRadius < 2) {
            mPreIndicatorRadius = 2;
        }
        Log.d(TAG, "xOffset: " + String.valueOf(xOffset));
        Log.d(TAG, "mPreIndicatorRadius: " + String.valueOf(mPreIndicatorRadius));
        if (mIndicatorList.size() > 0) {
            indicatorFromList = mIndicatorList.get(mLastPosition);
            float moveX = indicatorFromList.x + xOffset * mDeltDistanceBetweenIndicator;
            if (mMoveIndicator == null) {
                mMoveIndicator = new Indicator(moveX, indicatorFromList.y);
            } else {
                mMoveIndicator.x = moveX;
            }

            float middleX = (mMoveIndicator.x - indicatorFromList.x) / 2 + indicatorFromList.x;
            mBezierPath.reset();
            mBezierPath.moveTo(indicatorFromList.x, indicatorFromList.y + mPreIndicatorRadius);
            mBezierPath.quadTo(middleX, indicatorFromList.y, mMoveIndicator.x, mMoveIndicator.y + mIndicatorRadius);
            mBezierPath.lineTo(mMoveIndicator.x, mMoveIndicator.y - mIndicatorRadius);
            mBezierPath.quadTo(middleX, indicatorFromList.y, indicatorFromList.x, indicatorFromList.y - mPreIndicatorRadius);
            mBezierPath.close();

            invalidate();
        }
    }


    public void setCurrentPosition(final int position) {
        flag = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mBezierPath.reset();
                mCurrentIndicatorPosition = position;
                mPreIndicatorRadius = mIndicatorRadius;
                mCurrentIndicator.x = mIndicatorList.get(position).x;
                invalidate();
            }
        },10);


    }

    private class Indicator {
        public float x = -1;
        public float y = -1;

        public Indicator(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}