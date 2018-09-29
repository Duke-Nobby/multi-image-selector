package com.cardapp.utils.imageMark.view.base;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.cardapp.utils.fragment.FragmentBuilder;

/**
 * [Description]
 * <p/> fragment的builder基类，供子类继承，快速实现在ImageMarkActivity的调用显示
 * [How to use]
 * <p/> 在{@link ImageMarkBaseFragment}的子类中继承该类实现内部类
 * [Tips]
 *
 * @author Created by Jim.Huang on 2015/12/5.
 * @since 1.0.0
 */
public abstract class ImageMarkFragmentBuilder<T extends Fragment> extends FragmentBuilder<T> {
    public ImageMarkFragmentBuilder(Context context) {
        super(context);
    }

    protected ImageMarkFragmentBuilder() {
    }

    @Override
    protected int getContainerResID() {
        return ImageMarkActivity.mContainerResID;
    }
}
