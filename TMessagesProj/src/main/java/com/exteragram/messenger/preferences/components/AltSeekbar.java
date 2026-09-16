package com.exteragram.messenger.preferences.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

public class AltSeekbar extends FrameLayout {

    public interface OnDrag {
        void run(float value);
    }

    private final OnDrag onDrag;
    private final int min;
    private final int max;
    private final AnimatedTextView headerValue;
    private final TextView leftTextView;
    private final TextView rightTextView;
    public SeekBarView seekBarView;

    private float currentValue;
    private int roundedValue;
    private int vibro = -1;

    public AltSeekbar(Context context, OnDrag onDrag, int min, int max, String header, String left, String right) {
        super(context);
        this.onDrag = onDrag;
        this.min = min;
        this.max = max;

        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);

        TextView title = new TextView(context);
        title.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 15);
        title.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        title.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        title.setText(header);
        headerLayout.addView(title, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));

        headerValue = new AnimatedTextView(context, false, true, true) {
            final Drawable backgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(4), Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader), 0.15f));

            @Override
            protected void onDraw(Canvas canvas) {
                backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                backgroundDrawable.draw(canvas);
                super.onDraw(canvas);
            }
        };
        headerValue.setTextSize(AndroidUtilities.dp(11));
        headerValue.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        headerValue.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        headerValue.setPadding(AndroidUtilities.dp(5), 0, AndroidUtilities.dp(5), 0);
        headerValue.setGravity(Gravity.CENTER);
        headerLayout.addView(headerValue, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 17, Gravity.CENTER_VERTICAL, 6, 1, 0, 0));
        addView(headerLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT), 21, 17, 21, 0));

        leftTextView = createLabel(context, left, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        addView(leftTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT), 21, 45, 21, 0));
        rightTextView = createLabel(context, right, LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT);
        addView(rightTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT), 21, 45, 21, 0));

        initSeekBar();
    }

    private TextView createLabel(Context context, String text, int gravity) {
        TextView textView = new TextView(context);
        textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        textView.setGravity(gravity);
        textView.setText(text);
        return textView;
    }

    private void initSeekBar() {
        seekBarView = new SeekBarView(getContext(), true, null);
        seekBarView.setReportChanges(true);
        seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public void onSeekBarDrag(boolean stop, float progress) {
                float value = min + (max - min) * progress;
                onDrag.run(value);
                if (Math.round(value) != roundedValue) {
                    setProgress(value);
                }
            }

            @Override
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        addView(seekBarView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 44, Gravity.TOP, 6, 68, 6, 0));
        setProgress(currentValue);
    }

    public void setProgress(float value) {
        currentValue = value;
        roundedValue = Math.round(value);
        if (seekBarView != null) {
            seekBarView.setProgress((value - min) / (float) (max - min));
        }
        headerValue.cancelAnimation();
        headerValue.setText(getTextForHeader(), true);
        if ((roundedValue == min || roundedValue == max) && roundedValue != vibro) {
            vibro = roundedValue;
            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        } else if (roundedValue > min && roundedValue < max) {
            vibro = -1;
        }
        updateValues();
    }

    public CharSequence getTextForHeader() {
        CharSequence text;
        if (roundedValue == min) {
            text = leftTextView.getText();
        } else if (roundedValue == max) {
            text = rightTextView.getText();
        } else {
            text = String.valueOf(roundedValue);
        }
        return text.toString().toUpperCase();
    }

    private void updateValues() {
        int gray = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
        int blue = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
        float center = ((max - min) / 2f + min) * 1.5f;
        float rightStart = center - min * 0.5f;
        float leftEnd = (min + max) * 0.5f;
        if (currentValue >= rightStart) {
            rightTextView.setTextColor(ColorUtils.blendARGB(gray, blue, (currentValue - rightStart) / (max - rightStart)));
            leftTextView.setTextColor(gray);
        } else if (currentValue <= leftEnd) {
            leftTextView.setTextColor(ColorUtils.blendARGB(gray, blue, 1f - (currentValue - min) / (leftEnd - min)));
            rightTextView.setTextColor(gray);
        } else {
            leftTextView.setTextColor(gray);
            rightTextView.setTextColor(gray);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(112), MeasureSpec.EXACTLY));
    }

    public void updateStyle() {
        if (seekBarView != null) {
            removeView(seekBarView);
            seekBarView = null;
        }
        initSeekBar();
    }
}
