package com.oppenablers.mariatoggle.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.oppenablers.mariatoggle.R;
import com.oppenablers.mariatoggle.model.ToggleableView;

public class LabeledSwitch extends ToggleableView {

    private int padding;

    private int colorOn;
    private int colorOff;
    private int colorBorder;
    private int colorDisabled;

    private int textSize;

    private int outerRadii;
    private int thumbRadii;

    private Paint paint;

    private long startTime;

    private String labelOn;
    private String labelOff;

    private RectF thumbBounds;

    private RectF leftBgArc;
    private RectF rightBgArc;

    private RectF leftFgArc;
    private RectF rightFgArc;

    private Typeface typeface;

    private float thumbOnCenterX;
    private float thumbOffCenterX;

    public LabeledSwitch(Context context) {
        super(context);
        initView();
    }

    public LabeledSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initProperties(attrs);
    }

    public LabeledSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initProperties(attrs);
    }

    private void initView() {
        this.isOn = false;
        this.labelOn = "ON";
        this.labelOff = "OFF";

        this.isEnabled = true;
        this.textSize = (int) (12f * getResources().getDisplayMetrics().scaledDensity);

        colorBorder = colorOn = getResources().getColor(R.color.toggle_default_accent, getContext().getTheme());

        paint = new Paint();
        paint.setAntiAlias(true);

        leftBgArc = new RectF();
        rightBgArc = new RectF();

        leftFgArc = new RectF();
        rightFgArc = new RectF();
        thumbBounds = new RectF();

        this.colorOff = Color.parseColor("#FFFFFF");
        this.colorDisabled = Color.parseColor("#D3D3D3");
    }

    private void initProperties(AttributeSet attrs) {
        try (TypedArray tarr = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ToggleableView, 0, 0)) {
            final int N = tarr.getIndexCount();
            for (int i = 0; i < N; ++i) {
                int attr = tarr.getIndex(i);
                if (attr == R.styleable.ToggleableView_isOn) {
                    isOn = tarr.getBoolean(R.styleable.ToggleableView_isOn, false);
                } else if (attr == R.styleable.ToggleableView_colorOff) {
                    colorOff = tarr.getColor(R.styleable.ToggleableView_colorOff, Color.parseColor("#FFFFFF"));
                } else if (attr == R.styleable.ToggleableView_colorBorder) {
                    int accentColor;
                    accentColor = getResources().getColor(R.color.toggle_default_accent, getContext().getTheme());
                    colorBorder = tarr.getColor(R.styleable.ToggleableView_colorBorder, accentColor);
                } else if (attr == R.styleable.ToggleableView_colorOn) {
                    int accentColor;
                    accentColor = getResources().getColor(R.color.toggle_default_accent, getContext().getTheme());
                    colorOn = tarr.getColor(R.styleable.ToggleableView_colorOn, accentColor);
                } else if (attr == R.styleable.ToggleableView_colorDisabled) {
                    colorDisabled = tarr.getColor(R.styleable.ToggleableView_colorOff, Color.parseColor("#D3D3D3"));
                } else if (attr == R.styleable.ToggleableView_textOff) {
                    labelOff = tarr.getString(R.styleable.ToggleableView_textOff);
                } else if (attr == R.styleable.ToggleableView_textOn) {
                    labelOn = tarr.getString(R.styleable.ToggleableView_textOn);
                } else if (attr == R.styleable.ToggleableView_android_textSize) {
                    int defaultTextSize = (int) (12f * getResources().getDisplayMetrics().scaledDensity);
                    textSize = tarr.getDimensionPixelSize(R.styleable.ToggleableView_android_textSize, defaultTextSize);
                } else if (attr == R.styleable.ToggleableView_android_enabled) {
                    isEnabled = tarr.getBoolean(R.styleable.ToggleableView_android_enabled, false);
                } else if (attr == R.styleable.ToggleableView_android_fontFamily) {
                    int fontResourceId = tarr.getResourceId(R.styleable.ToggleableView_android_fontFamily, 0);
                    if (fontResourceId > 0 && !isInEditMode()) {
                        setTypeface(ResourcesCompat.getFont(getContext(), fontResourceId));
                    }
                }
            }
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        paint.setTextSize(textSize);

//      Drawing Switch background here
        {
            if (isEnabled()) {
                paint.setColor(colorBorder);
            } else {
                paint.setColor(colorDisabled);
            }
            canvas.drawArc(leftBgArc, 90, 180, false, paint);
            canvas.drawArc(rightBgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, 0, (width - outerRadii), height, paint);

            paint.setColor(colorOff);

            canvas.drawArc(leftFgArc, 90, 180, false, paint);
            canvas.drawArc(rightFgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, (float) padding / 10, (width - outerRadii), height - ((float) padding / 10), paint);

            int alpha = (int) (((thumbBounds.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int onColor;

            if (isEnabled()) {
                onColor = Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn));
            } else {
                onColor = Color.argb(alpha, Color.red(colorDisabled), Color.green(colorDisabled), Color.blue(colorDisabled));
            }
            paint.setColor(onColor);

            canvas.drawArc(leftBgArc, 90, 180, false, paint);
            canvas.drawArc(rightBgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, 0, (width - outerRadii), height, paint);

            alpha = (int) (((thumbOnCenterX - thumbBounds.centerX()) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int offColor = Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff));
            paint.setColor(offColor);

            canvas.drawArc(leftFgArc, 90, 180, false, paint);
            canvas.drawArc(rightFgArc, 90, -180, false, paint);
            canvas.drawRect(outerRadii, (float) padding / 10, (width - outerRadii), height - ((float) padding / 10), paint);
        }

//      Drawing Switch Labels here
        String MAX_CHAR = "N";
        float textCenter = paint.measureText(MAX_CHAR) / 2;
        if (isOn) {
            int alpha = (int) ((((width >>> 1) - thumbBounds.centerX()) / ((width >>> 1) - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int onColor = Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn));
            paint.setColor(onColor);

            float centerX = (width - padding - ((padding + (padding >>> 1)) + (thumbRadii << 1))) >>> 1;
            canvas.drawText(labelOff, (padding + (padding >>> 1)) + (thumbRadii << 1) + centerX - (paint.measureText(labelOff) / 2), (height >>> 1) + textCenter, paint);

            alpha = (int) (((thumbBounds.centerX() - (width >>> 1)) / (thumbOnCenterX - (width >>> 1))) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int offColor = Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff));
            paint.setColor(offColor);

            int maxSize = width - (padding << 1) - (thumbRadii << 1);

            centerX = (((padding >>> 1) + maxSize) - padding) >>> 1;
            canvas.drawText(labelOn, padding + centerX - (paint.measureText(labelOn) / 2), (height >>> 1) + textCenter, paint);
        } else {
            int alpha = (int) (((thumbBounds.centerX() - (width >>> 1)) / (thumbOnCenterX - (width >>> 1))) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int offColor = Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff));
            paint.setColor(offColor);

            int maxSize = width - (padding << 1) - (thumbRadii << 1);
            float centerX = (((padding >>> 1) + maxSize) - padding) >>> 1;
            canvas.drawText(labelOn, padding + centerX - (paint.measureText(labelOn) / 2), (height >>> 1) + textCenter, paint);

            alpha = (int) ((((width >>> 1) - thumbBounds.centerX()) / ((width >>> 1) - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int onColor;
            if (isEnabled()) {
                onColor = Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn));
            } else {
                onColor = Color.argb(alpha, Color.red(colorDisabled), Color.green(colorDisabled), Color.blue(colorDisabled));
            }
            paint.setColor(onColor);

            centerX = (width - padding - ((padding + (padding >>> 1)) + (thumbRadii << 1))) >>> 1;
            canvas.drawText(labelOff, (padding + (padding >>> 1)) + (thumbRadii << 1) + centerX - (paint.measureText(labelOff) / 2), (height >>> 1) + textCenter, paint);
        }

//      Drawing Switch Thumb here
        {
            int alpha = (int) (((thumbBounds.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int offColor = Color.argb(alpha, Color.red(colorOff), Color.green(colorOff), Color.blue(colorOff));
            paint.setColor(offColor);

            canvas.drawCircle(thumbBounds.centerX(), thumbBounds.centerY(), thumbRadii, paint);
            alpha = (int) (((thumbOnCenterX - thumbBounds.centerX()) / (thumbOnCenterX - thumbOffCenterX)) * 255);
            alpha = (alpha < 0 ? 0 : (Math.min(alpha, 255)));
            int onColor;
            if (isEnabled()) {
                onColor = Color.argb(alpha, Color.red(colorOn), Color.green(colorOn), Color.blue(colorOn));
            } else {
                onColor = Color.argb(alpha, Color.red(colorDisabled), Color.green(colorDisabled), Color.blue(colorDisabled));
            }
            paint.setColor(onColor);
            canvas.drawCircle(thumbBounds.centerX(), thumbBounds.centerY(), thumbRadii, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getResources().getDimensionPixelSize(R.dimen.labeled_default_width);
        int desiredHeight = getResources().getDimensionPixelSize(R.dimen.labeled_default_height);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);

        outerRadii = Math.min(width, height) >>> 1;
        thumbRadii = (int) (Math.min(width, height) / (2.88f));
        padding = (height - thumbRadii) >>> 1;

        thumbBounds.set(width - padding - thumbRadii, padding, width - padding, height - padding);
        thumbOnCenterX = thumbBounds.centerX();

        thumbBounds.set(padding, padding, padding + thumbRadii, height - padding);
        thumbOffCenterX = thumbBounds.centerX();

        if (isOn) {
            thumbBounds.set(width - padding - thumbRadii, padding, width - padding, height - padding);
        } else {
            thumbBounds.set(padding, padding, padding + thumbRadii, height - padding);
        }

        leftBgArc.set(0, 0, outerRadii << 1, height);
        rightBgArc.set(width - (outerRadii << 1), 0, width, height);

        leftFgArc.set((float) padding / 10, (float) padding / 10, (outerRadii << 1) - (float) padding / 10, height - ((float) padding / 10));
        rightFgArc.set(width - (outerRadii << 1) + ((float) padding / 10), (float) padding / 10, width - ((float) padding / 10), height - ((float) padding / 10));
    }

    @Override
    public final boolean performClick() {
        super.performClick();
        if (isOn) {
            ValueAnimator switchColor = ValueAnimator.ofFloat(width - padding - thumbRadii, padding);
            switchColor.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                invalidate();
            });
            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
            switchColor.setDuration(250);
            switchColor.start();
        } else {
            ValueAnimator switchColor = ValueAnimator.ofFloat(padding, width - padding - thumbRadii);
            switchColor.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                invalidate();
            });
            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
            switchColor.setDuration(250);
            switchColor.start();
        }
        isOn = !isOn;
        if (onToggledListener != null) {
            onToggledListener.onToggled(this, isOn);
        }
        return true;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            float x = event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    startTime = System.currentTimeMillis();
                    return true;
                }
// BROKEN!!!!!!!! fix someday i hope

                case MotionEvent.ACTION_MOVE: {
                    if (x - (thumbRadii >>> 1) > padding && x + (thumbRadii >>> 1) < width - padding) {
                        thumbBounds.set(x - (thumbRadii >>> 1), thumbBounds.top, x + (thumbRadii >>> 1), thumbBounds.bottom);
                        invalidate();
                    }
                    return true;
                }

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    long endTime = System.currentTimeMillis();
                    long span = endTime - startTime;
                    if (span < 200) {
//                        performClick();
                    } else {
                        if (x >= width >>> 1) {
                            ValueAnimator switchColor = ValueAnimator.ofFloat((x > (width - padding - thumbRadii) ? (width - padding - thumbRadii) : x), width - padding - thumbRadii);
                            switchColor.addUpdateListener(animation -> {
                                float value = (float) animation.getAnimatedValue();
                                thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                                invalidate();
                            });
                            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
                            switchColor.setDuration(250);
                            switchColor.start();
                            isOn = true;
                        } else {
                            ValueAnimator switchColor = ValueAnimator.ofFloat((x < padding ? padding : x), padding);
                            switchColor.addUpdateListener(animation -> {
                                float value = (float) animation.getAnimatedValue();
                                thumbBounds.set(value, thumbBounds.top, value + thumbRadii, thumbBounds.bottom);
                                invalidate();
                            });
                            switchColor.setInterpolator(new AccelerateDecelerateInterpolator());
                            switchColor.setDuration(250);
                            switchColor.start();
                            isOn = false;
                        }
                        if (onToggledListener != null) {
                            onToggledListener.onToggled(this, isOn);
                        }
                    }
                    invalidate();
                    return true;
                }

                default: {
                    return super.onTouchEvent(event);
                }
            }
        } else {
            return false;
        }
    }

    public int getColorOn() {
        return colorOn;
    }

    public void setColorOn(int colorOn) {
        this.colorOn = colorOn;
        invalidate();
    }

    public int getColorOff() {
        return colorOff;
    }

    public void setColorOff(int colorOff) {
        this.colorOff = colorOff;
        invalidate();
    }

    public String getLabelOn() {
        return labelOn;
    }

    public void setLabelOn(String labelOn) {
        this.labelOn = labelOn;
        invalidate();
    }

    public String getLabelOff() {
        return labelOff;
    }

    public void setLabelOff(String labelOff) {
        this.labelOff = labelOff;
        invalidate();
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        if (typeface != null) {
            this.typeface = typeface;
            paint.setTypeface(typeface);
            invalidate();
        }
    }

    @Override
    public void setOn(boolean on) {
        super.setOn(on);
        if (isOn) {
            thumbBounds.set(width - padding - thumbRadii, padding, width - padding, height - padding);
        } else {
            thumbBounds.set(padding, padding, padding + thumbRadii, height - padding);
        }
        invalidate();
    }

    public int getColorDisabled() {
        return colorDisabled;
    }

    public void setColorDisabled(int colorDisabled) {
        this.colorDisabled = colorDisabled;
        invalidate();
    }

    public int getColorBorder() {
        return colorBorder;
    }

    public void setColorBorder(int colorBorder) {
        this.colorBorder = colorBorder;
        invalidate();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = (int) (textSize * getResources().getDisplayMetrics().scaledDensity);
        invalidate();
    }
}
