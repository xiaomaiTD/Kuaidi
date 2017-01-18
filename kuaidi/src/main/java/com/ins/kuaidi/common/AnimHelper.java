package com.ins.kuaidi.common;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/1/18.
 */

public class AnimHelper {

    private float alpha;
    private ValueAnimator animator;

    public void turnLight(final View shadowView) {
        if (shadowView != null) {
            if (animator!=null){
                animator.cancel();
            }
            shadowView.setVisibility(View.VISIBLE);
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(250).start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float alpha = (Float) animation.getAnimatedValue();
                    shadowView.setAlpha(alpha);
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
//                    shadowView.setVisibility(View.VISIBLE);
                    shadowView.setEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void turnDark(final View shadowView) {
        if (shadowView != null) {
            if (animator!=null){
                animator.cancel();
            }
            animator = ValueAnimator.ofFloat(1f, 0f);
            animator.setDuration(250).start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float alpha = (Float) animation.getAnimatedValue();
                    shadowView.setAlpha(alpha);
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
//                    shadowView.setVisibility(View.GONE);
                    shadowView.setEnabled(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
}
