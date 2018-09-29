package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import static android.R.color.white;

/**
 * [Description]
 * <p/> 【Na】NaActivity的专用顶部栏，供NaBaseFragment子类重复调用实现页面的顶部栏需求
 * [How to use]
 * <p>
 * [Tips]
 * 【sample】
 *
 * @author Created by Michael Mu on 2015/12/25.
 * @since 1.0.0
 */
public class ZpTitleBarManager {
    private Context mContext;
    private Toolbar mToolbar;
    TextView mTitleTv;

    public ZpTitleBarManager(Context context, Toolbar toolbar) {
        mContext = context;
        mToolbar = toolbar;
    }

    public ZpTitleBarManager init() {
        //设置toolbar
        mToolbar.setTitleTextColor(mContext.getResources().getColor(white));
        renew();
        return this;
    }

    public void renew() {
        //设置返回按钮
//        mToolbar.setNavigationIcon(R.drawable.pub_back);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public ZpTitleBarManager showToolBar(boolean show) {
        mToolbar.setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    public TextView getTitleView() {
        return mTitleTv;
    }

    public void setToolBarManager() {
        mToolbar.setVisibility(View.VISIBLE);
    }

    public void removeToolBarManager() {
        mToolbar.setVisibility(View.GONE);
    }

    public ZpTitleBarManager setTitle(int titleResId) {
        return setTitle(mContext.getString(titleResId));
    }

    public ZpTitleBarManager setTitle(@Nullable String title) {
        if (title == null) {
            mTitleTv.setVisibility(View.GONE);
        } else {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(title);
        }
        return this;
    }

    public ZpTitleBarManager clickTitle(View.OnClickListener clickListener) {
        mTitleTv.setOnClickListener(clickListener);
        return this;
    }

}
