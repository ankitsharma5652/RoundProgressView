package android.practice.g0ku.roundeprogress;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class RoundProgressView extends View {

    private final float DEFAULT_WIDTH = 10f;
    private RectF path;
    private int progress = 0;
    private int maxProgress = 100;
    private boolean mIndeterminate = true;
    private Paint mPaint;
    private float mWidth = DEFAULT_WIDTH;
    private float outerCircleRadius = 200f;

    private AnimatorSet set;


    @ColorInt
    private int backgroundColor = Color.TRANSPARENT;

    @ColorInt
    private int progressColor;
    private float startAngle = 270f;
    private float maxAngle = 20f;

    public RoundProgressView(Context context) {
        this(context, null);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RoundProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);


        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.RoundProgressView);


        progressColor = array.getColor(R.styleable.RoundProgressView_rp_progressColor, Color.parseColor("#039BE5"));
        backgroundColor = array.getColor(R.styleable.RoundProgressView_rp_progressColor, Color.parseColor("#e3e3e3"));
        outerCircleRadius = array.getFloat(R.styleable.RoundProgressView_rp_radius, 200f);
        mWidth = array.getDimension(R.styleable.RoundProgressView_rp_progressWidth, DEFAULT_WIDTH);
        mIndeterminate = array.getBoolean(R.styleable.RoundProgressView_rp_indeterminate,true);
        progress = array.getInt(R.styleable.RoundProgressView_rp_progress,0);
        maxProgress = array.getInt(R.styleable.RoundProgressView_rp_maxProgress,100);

        array.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new RectF();


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        checkProgressAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(resolveSize((int) (outerCircleRadius), widthMeasureSpec), resolveSize((int) (outerCircleRadius), heightMeasureSpec));

    }


    public void setIndeterminsate(boolean indeterminate) {

        if (indeterminate == mIndeterminate) return;

        this.mIndeterminate = indeterminate;


        checkProgressAnimation();

        invalidate();
    }

    private void checkProgressAnimation() {

        if (mIndeterminate) {
            animationSet().start();
            startAngle = 20f;
        } else
            animationSet().cancel();
    }


    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    public void setMaxProgress(int maxProgress) {
        if (maxProgress == this.maxProgress) return;

        this.maxProgress = maxProgress;
        invalidate();
    }


    public void setProgress(int progress) {
        if (this.progress == progress || progress > maxProgress) return;

        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(backgroundColor);

        path.set(mWidth + getPaddingStart(), mWidth + getPaddingTop(), getWidth() - mWidth - getPaddingRight(), getHeight() - mWidth - getPaddingBottom());


        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mWidth);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - (mWidth - getPaddingStart() * 2), mPaint);


        mPaint.setColor(progressColor);

        if (!mIndeterminate) {
            if (progress > 0)
                maxAngle = (360f * progress) / 100;
            else
                maxAngle = 0f;

        }

        canvas.drawArc(path, startAngle, maxAngle, false, mPaint);


    }

    private Animator rotateAnimation() {

        ValueAnimator animator;

        animator = ValueAnimator.ofFloat(0, 360f);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle = (float) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });

        animator.setDuration(900);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        return animator;
    }


    private AnimatorSet animationSet() {

        if (set != null)
            return set;

        set = new AnimatorSet();


        set.playTogether(rotateAnimation(), scaleAnimation());


        return set;
    }

    private Animator scaleAnimation() {


        ValueAnimator animator = ValueAnimator.ofFloat(20, 180);

        animator.setDuration(900);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                maxAngle = (float) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });


        return animator;
    }


    @Override
    protected void onDetachedFromWindow() {

        if (mIndeterminate)
            animationSet().cancel();

        super.onDetachedFromWindow();

    }
}
