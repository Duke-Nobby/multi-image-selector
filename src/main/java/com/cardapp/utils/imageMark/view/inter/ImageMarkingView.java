package com.cardapp.utils.imageMark.view.inter;


import android.graphics.Bitmap;

import com.cardapp.utils.mvp.BaseView;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2016/6/23.
 * @since 1.0.0
 */
public interface ImageMarkingView extends BaseView {
    void showImageEditorUi(Bitmap bitmap);

    void afterConfirm(String imagePath);
}
