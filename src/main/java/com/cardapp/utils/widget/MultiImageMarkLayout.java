package com.cardapp.utils.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.cardapp.utils.imageMark.model.bean.ImageMarkResultObj;
import com.cardapp.utils.widget.multiImageView.model.bean.MultiImageBean;
import com.cardapp.utils.widget.multiImageView.view.MultiImageLy;

import java.util.ArrayList;

import rx.Observable;
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

public class MultiImageMarkLayout extends MultiImageLy {
    ContainerView mContainerView;

    public MultiImageMarkLayout(Context context) {
        super(context);
    }

    public MultiImageMarkLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiImageMarkLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiImageMarkLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setContainerView(ContainerView containerView) {
        mContainerView = containerView;
    }

    @Override
    protected void onImageBtnClick(int position, MultiImageBean multiImageBean) {
        String lImagePath = multiImageBean.getPath();
        startImageMark(lImagePath);

    }

    private void startImageMark(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        //打开图片标记页面
        if (mContainerView != null) {
            mContainerView.createImageMarkResultObservable(imagePath)
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
    }

    @Override
    protected void onGetImageSucc(ArrayList<String> resultList) {
        if (resultList.size() == 1) {
            String lImagePath = resultList.get(0);
            startImageMark(lImagePath);
        }
    }

    public interface ContainerView {
        Observable<ImageMarkResultObj> createImageMarkResultObservable(String imagePath);
    }
}
