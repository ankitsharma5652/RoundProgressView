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
    private Paint mPaint;
    private float mWidth = DEFAULT_WIDTH;
    private float outerCircleRadius = 200f;

    private AnimatorSet set;


    @ColorInt
    private int backgroundColor = Color.TRANSPARENT;

    @ColorInt
    private int progressColor ;
    private float startAngle;
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

        TypedArray array = context.obtainStyledAttributes(R.styleable.RoundProgressView);

        progressColor = array.getColor(R.styleable.RoundProgressView_rp_progressColor,Color.parseColor("#039BE5"));
        backgroundColor = array.getColor(R.styleable.RoundProgressView_rp_progressColor,Color.parseColor("#e3e3e3"));
        outerCircleRadius = array.getFloat(R.styleable.RoundProgressView_rp_radius,200f);
        mWidth = array.getDimension(R.styleable.RoundProgressView_rp_progressWidth,DEFAULT_WIDTH);

        array.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new RectF();


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animationSet().start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(resolveSize((int) (outerCircleRadius), widthMeasureSpec), resolveSize((int) (outerCircleRadius), heightMeasureSpec));

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(backgroundColor);

        path.set(mWidth+getPaddingStart(), mWidth+getPaddingTop(), getWidth() - mWidth - getPaddingRight(), getHeight() - mWidth-getPaddingBottom());


        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mWidth);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - (mWidth - getPaddingStart()*2), mPaint);


        mPaint.setColor(progressColor);

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


        set.playTogether(rotateAnimation(), setupAnimation());


        return set;
    }

    private Animator setupAnimation() {


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
        animationSet().cancel();
        super.onDetachedFromWindow();

    }
}
