package com.cardapp.utils.imageMark.view.inter;

import android.view.View;

import com.cardapp.Module.moduleImpl.view.inter.CaBaseTitleBarView;
import com.cardapp.utils.imageMark.view.base.ImageMarkTitleBarManager;

/**
 * [Description]
 * <p> 所有fragment共同所处的activity中共同使用的titleBar抽象基类，用于解耦activity与titleBar的强关联
 * <p> [How to use]
 * <p> 继承{@link ImageMarkContainerView} 的activity需返回继承{@link ImageMarkTitleBarView}的titleBar实例
 * <p> [Tips]
 * 【ImageMark】
 *
 * @author Created by Woode.Wang on 2016/11/7.
 * @since 1.0.0
 */
public interface ImageMarkTitleBarView extends CaBaseTitleBarView<ImageMarkTitleBarView> {

    /**
     * 显示或隐藏【保存】
     *
     * @param show 显示或隐藏
     * @return 本身
     */
    ImageMarkTitleBarView showSave(boolean show);

    /**
     * 点击【保存】
     *
     * @param clickListener 点击事件
     * @return 本身
     */
    ImageMarkTitleBarView clickSave(View.OnClickListener clickListener);

    ImageMarkTitleBarManager showConfirmTv(boolean isShow);

    ImageMarkTitleBarManager clickConfirmTv(View.OnClickListener onClickListener);
}
