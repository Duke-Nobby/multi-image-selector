package com.cardapp.utils.imageMark.view.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cardapp.utils.imageMark.view.inter.ImageMarkTitleBarView;
import com.cardapp.utils.view.OnDebouncedClickListener;

import me.nereo.multi_image_selector.R;

import static android.R.color.white;

/**
 * [Description]
 * <p/> 【ImageMark】ImageMarkActivity的专用顶部栏，供ImageMarkBaseFragment子类重复调用实现页面的顶部栏需求
 * [How to use]
 * <p>
 * [Tips]
 * 【imagePick】
 *
 * @author Created by JJ.Lin on 2015/12/25.
 * @since 1.0.0
 */
public class ImageMarkTitleBarManager implements ImageMarkTitleBarView {
    private Context mContext;
    private Toolbar mToolbar;
    TextView mTitleTv;
    TextView mSave;
    private final TextView mConfirm;

    public ImageMarkTitleBarManager(Context context, Toolbar toolbar) {
        mContext = context;
        mToolbar = toolbar;
        mTitleTv = (TextView) toolbar.findViewById(R.id.titleTv_imagePick_title_bar);
        mSave = (TextView) toolbar.findViewById(R.id.saveTv_imagePick_title_bar);
        mConfirm = (TextView) toolbar.findViewById(R.id.confirmTv_imagePick_title_bar);
    }

    @Override
    public ImageMarkTitleBarManager init() {
        //设置toolbar
        mToolbar.setTitleTextColor(mContext.getResources().getColor(white));
        renew();
        return this;
    }

    @Override
    public void renew() {
        //设置返回按钮
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        showSave(false);
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public TextView getTitleView() {
        return mTitleTv;
    }

    @Override
    public void showToolBarView() {
        mToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissToolBarView() {
        mToolbar.setVisibility(View.GONE);
    }

    @Override
    public ImageMarkTitleBarManager setTitle(int titleResId) {
        return setTitle(mContext.getString(titleResId));
    }

    @Override
    public ImageMarkTitleBarManager setTitle(@Nullable String title) {
        if (title == null) {
            mTitleTv.setVisibility(View.GONE);
        } else {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(title);
        }
        return this;
    }

    @Override
    public ImageMarkTitleBarManager clickTitle(final View.OnClickListener onClickListener) {
        mTitleTv.setOnClickListener(new OnDebouncedClickListener() {
            @Override
            public void onDebouncedClick(View view) {
                onClickListener.onClick(view);
            }
        });
        return this;
    }

    @Override
    public ImageMarkTitleBarManager showSave(boolean show) {
        mSave.setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    @Override
    public ImageMarkTitleBarManager clickSave(final View.OnClickListener onClickListener) {
        mSave.setOnClickListener(new OnDebouncedClickListener(1000) {
            @Override
            public void onDebouncedClick(View view) {
                onClickListener.onClick(view);
            }
        });
        return this;
    }

    @Override
    public ImageMarkTitleBarManager showConfirmTv(boolean isShow) {
        mConfirm.setVisibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    @Override
    public ImageMarkTitleBarManager clickConfirmTv(final View.OnClickListener onClickListener) {
        mConfirm.setOnClickListener(new OnDebouncedClickListener(1000) {
            @Override
            public void onDebouncedClick(View view) {
                onClickListener.onClick(view);
            }
        });
        return this;
    }

}
