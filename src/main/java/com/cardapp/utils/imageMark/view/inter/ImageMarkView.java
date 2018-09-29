package com.cardapp.utils.imageMark.view.inter;

import com.cardapp.utils.imageMark.model.bean.ImageMarkResultObj;

import rx.Observable;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2016/12/31.
 * @since 1.0.0
 */
public interface ImageMarkView {
    void startImageMark(String imagePath);

    Observable<ImageMarkResultObj> createImageMarkResultObservable();
}
