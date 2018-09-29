package com.cardapp.utils.imageMark.view.inter;

import android.app.Activity;
import android.content.Intent;

import com.cardapp.Module.moduleImpl.view.inter.CaBaseContainerView;
import com.cardapp.utils.imageMark.view.base.ImageMarkBaseFragment;

/**
 * [Description]
 * <p>所有继承{@link ImageMarkBaseFragment}的fragment类所在activity的抽象基类，用于解耦activity与fragment的强关联
 * 主要功能是提供{@link ImageMarkTitleBarView}和传递来自activity
 * 的事件如{@link Activity#onBackPressed()}、{@link Activity#onActivityResult(int, int, Intent)}
 * <p>[How to use]
 * <p>调用改包fragment代码所处的activity必须继承自FragmentActivity，且实现ImageMarkContainerView接口
 * ，否则会报{@link #WARM_NEED_TO_IN_FRAGMENT_ACTIVITY}错误
 * <p>[Tips]
 * 【ImageMark】
 *
 * @author Created by Woode.Wang on 2016/11/7.
 * @since 1.0.0
 */
public interface ImageMarkContainerView extends CaBaseContainerView {
    String WARM_NEED_TO_IN_FRAGMENT_ACTIVITY =
            "该fragment所在的activity必须继承自FragmentActivity，且实现ImageMarkContainerView接口";

    /**
     * 保存当前显示的fragment实例
     *
     * @param currentFragment 当前保存的fragment实例
     */
    void setCurrentFragment(ImageMarkBaseFragment currentFragment);

    /**
     * 获取titleBar实例
     *
     * @return titleBar实例
     */
    ImageMarkTitleBarView getImageMarkToolBarView();

}
