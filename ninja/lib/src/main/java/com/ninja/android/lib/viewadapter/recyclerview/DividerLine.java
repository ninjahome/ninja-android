package com.ninja.android.lib.viewadapter.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class DividerLine extends RecyclerView.ItemDecoration {
    private static final String TAG = DividerLine.class.getCanonicalName();
    private static final int DEFAULT_DIVIDER_SIZE = 1;
    private static final int ATTRS[] = {android.R.attr.listDivider};
    private Drawable dividerDrawable;
    private Context mContext;
    private int dividerSize;
    private LineDrawMode mMode = null;


    public enum LineDrawMode {
        HORIZONTAL, VERTICAL, BOTH
    }

    public DividerLine(Context context) {
        mContext = context;
        TypedArray attrArray = context.obtainStyledAttributes(ATTRS);
        dividerDrawable = attrArray.getDrawable(0);
        attrArray.recycle();
    }

    public DividerLine(Context context, LineDrawMode mode) {
        this(context);
        mMode = mode;
    }

    public DividerLine(Context context, int dividerSize, LineDrawMode mode) {
        this(context, mode);
        this.dividerSize = dividerSize;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    public LineDrawMode getMode() {
        return mMode;
    }

    public void setMode(LineDrawMode mode) {
        mMode = mode;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (getMode() == null) {
            throw new IllegalStateException("assign LineDrawMode,please!");
        }
        switch (getMode()) {
            case VERTICAL:
                drawVertical(c, parent, state);
                break;
            case HORIZONTAL:
                drawHorizontal(c, parent, state);
                break;
            case BOTH:
                drawHorizontal(c, parent, state);
                drawVertical(c, parent, state);
                break;
        }
    }
    private void drawVertical(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = getDividerSize() == 0 ? left + dip2px(mContext, DEFAULT_DIVIDER_SIZE) : left + getDividerSize();
            dividerDrawable.setBounds(left, top, right, bottom);
            dividerDrawable.draw(c);
        }
    }


    private void drawHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int top = child.getBottom() + params.topMargin;
            final int right = child.getRight() - params.rightMargin;
            final int bottom = getDividerSize() == 0 ? top + dip2px(mContext, DEFAULT_DIVIDER_SIZE) : top + getDividerSize();
            dividerDrawable.setBounds(left, top, right, bottom);
            dividerDrawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        outRect.bottom = getDividerSize() == 0 ? dip2px(mContext, DEFAULT_DIVIDER_SIZE) : getDividerSize();
    }


    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
