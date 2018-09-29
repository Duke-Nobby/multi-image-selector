package com.cardapp.utils.imageMark.view.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cardapp.Module.moduleImpl.view.base.CaBaseMvpFragment;
import com.cardapp.utils.imageMark.view.inter.ImageMarkContainerView;
import com.cardapp.utils.imageMark.view.inter.ImageMarkTitleBarView;
import com.cardapp.utils.mvp.BasePresenter;
import com.cardapp.utils.mvp.BaseView;


/**
 * [Description]
 * <p/> 【ImageMark】基于mvp实现的fragment基类，供子类继承，提供ImageMarkActivity的调用及后退监听等
 * [How to use]
 * <p/> 继承该类
 * [Tips]
 * 与ImageMarkActivity绑定，必须display在ImageMarkActivity上
 *
 * @author Created by Jim.Huang on 2015/12/5.
 * @since 1.0.0
 */
public abstract class ImageMarkBaseFragment<V extends BaseView, P extends BasePresenter<V>>
        extends CaBaseMvpFragment<V, P> {
    protected ImageMarkContainerView mContainerView;
    protected ImageMarkTitleBarView mToolBarManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ImageMarkContainerView) {
            mContainerView = (ImageMarkContainerView) activity;
            mContainerView.setCurrentFragment(this);
            mFragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        } else {
            throw new RuntimeException(ImageMarkContainerView.WARM_NEED_TO_IN_FRAGMENT_ACTIVITY);
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //  2015/12/21 by woode 修复《组内buglist》No.2:后台返回当前activity造成mToolBarManager空指针问题
        //1，原因在于系统从后台恢复fragment的时候，执行的生命周期为：
        //Activity.onCreate() → Fragment.onAttach() → Activity.setContentView() → Activity.AfterViews()
        // → Fragment.onCreateView() → Fragment.onCreate()
        //2，注意Activity.mPcTitleBarView 在 Activity.AfterViews()才真正创建实例
        //3，而错误的做法是在Fragment.onAttach()中赋值Fragment.mPcTitleBarView = Activity.mPcTitleBarView，
        // 此时Activity.mPcTitleBarView = null
        //因此当Fragment.onCreate()调用Fragment.mToolBarManager便会报空指针
        //4，故正确的做法是在Fragment.onCreateView()中赋值Fragment.mPcTitleBarView = Activity.mPcTitleBarView
        mToolBarManager = mContainerView.getImageMarkToolBarView();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mToolBarManager != null) {
            mToolBarManager.renew();
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    public ImageMarkContainerView getContainerView() {
        return mContainerView;
    }

    public ImageMarkTitleBarView getToolBarManager() {
        return mToolBarManager;
    }
}
