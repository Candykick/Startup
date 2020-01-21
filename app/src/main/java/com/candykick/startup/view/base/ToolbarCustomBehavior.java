package com.candykick.startup.view.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.candykick.startup.R;

/**
 * Created by candykick on 2019. 8. 30..
 */

public class ToolbarCustomBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private final static float MIN_AVATAR_PERCENTAGE_SIZE = 0.3f;
    private final static int EXTRA_FINAL_AVATER_PADDING = 80;

    private final Context context;
    private float mAvatarMaxSize;

    private float mFinalLeftAvatarPadding;
    private float mStartPosiiton;
    private int mStartXPosition, mStartYPosition;
    private int mFinalYPosition, finalHeight, mStartHeight, mFinalXPosition;
    private float mStartToolbarPosition;

    public ToolbarCustomBehavior(Context context, AttributeSet attrs) {
        this.context = context;
        mAvatarMaxSize = context.getResources().getDimension(R.dimen.image_width);

        mFinalLeftAvatarPadding = context.getResources().getDimension(R.dimen.activity_horizontal_margin);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        maybeInitProperties(child, dependency);

        final int maxScrollDistance = (int) (mStartToolbarPosition - getStatusBarHeight());
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        float distanceYToSubtract = ((mStartYPosition - mFinalYPosition) * (1f - expandedPercentageFactor)) + (child.getHeight()/2);
        float distanceXToSubtract = ((mStartXPosition - mFinalXPosition) * (1f - expandedPercentageFactor)) + (child.getWidth()/2);

        float heightToSubtract = ((mStartHeight - finalHeight) * (1f - expandedPercentageFactor));

        child.setY(mStartYPosition - distanceYToSubtract);
        child.setX(mStartXPosition - distanceXToSubtract);

        int proportionalAvatarSize = (int) (mAvatarMaxSize * (expandedPercentageFactor));

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)child.getLayoutParams();
        layoutParams.width = (int) (mStartHeight - heightToSubtract);
        layoutParams.height = (int) (mStartHeight - heightToSubtract);
        child.setLayoutParams(layoutParams);

        return true;
    }

    @SuppressLint("PrivateResource")
    private void maybeInitProperties(ImageView child, View dependency) {
        if(mStartYPosition == 0)
            mStartYPosition = (int) (dependency.getY());

        if(mFinalYPosition == 0)
            mFinalYPosition = (dependency.getHeight()/2);

        if(mStartHeight == 0)
            mStartHeight = child.getHeight();

        if(finalHeight == 0)
            finalHeight = context.getResources().getDimensionPixelOffset(R.dimen.image_small_width);

        if(mStartXPosition == 0)
            mStartXPosition = (int) (child.getX() + (child.getWidth()/2));

        if(mFinalXPosition == 0)
            mFinalXPosition = context.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + (finalHeight / 2);

        if(mStartToolbarPosition == 0)
            mStartToolbarPosition = dependency.getY() + (dependency.getHeight()/2);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height","dimen","android");

        if(resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }
}
