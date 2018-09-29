package com.cardapp.utils.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.cardapp.utils.imageMark.model.bean.ImageMarkResultObj;
import com.cardapp.utils.imageMark.view.inter.ImageMarkView;
import com.cardapp.utils.widget.multiImageView.model.bean.MultiImageBean;
import com.cardapp.utils.widget.multiImageView.view.MultiImageLy;

import java.util.ArrayList;

import rx.functions.Action1;

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

public class MultiImageMarkView extends MultiImageLy {
    ImageMarkView mImageMarkView;

    public MultiImageMarkView(Context context) {
        super(context);
    }

    public MultiImageMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiImageMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiImageMarkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setImageMarkView(ImageMarkView imageMarkView) {
        mImageMarkView = imageMarkView;
        mImageMarkView.createImageMarkResultObservable()
                .subscribe(new Action1<ImageMarkResultObj>() {
                    @Override
                    public void call(ImageMarkResultObj activityResultObj) {
                        String lNewImagePath = activityResultObj.getNewImagePath();
                        String lOldImagePath = activityResultObj.getOldImagePath();
                        replaceImage(lOldImagePath, lNewImagePath);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void onImageBtnClick(int position, MultiImageBean multiImageBean) {
        String lImagePath = multiImageBean.getPath();
        //打开图片标记页面
        if (mImageMarkView != null) {
            mImageMarkView.startImageMark(lImagePath);
        }
    }

    @Override
    protected void onGetImageSucc(ArrayList<String> resultList) {
        if (resultList.size() == 1) {
            String lToEditImagePath = resultList.get(0);
            //打开图片标记页面
            if (mImageMarkView != null) {
                mImageMarkView.startImageMark(lToEditImagePath);
            }
        }
    }
}
