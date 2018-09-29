package com.cardapp.utils.widget.multiImageView.presenter;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.utils.widget.multiImageView.model.bean.MultiImageBean;
import com.cardapp.utils.widget.multiImageView.view.MultiImageView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Michael.Mu on 3/24/2016.
 * @version 1.0.0
 */
@SuppressWarnings("ALL")
public class MultiImagePresenter extends MvpBasePresenter<MultiImageView> {

    private LinkedList<MultiImageBean> mMultiImageBeen;
    private int mMaxImageCount = 9;


    public void initData(LinkedList<MultiImageBean> multiImageBeen, int maxImageCount) {
        mMaxImageCount = maxImageCount;
        mMultiImageBeen = multiImageBeen;
        if (isViewAttached()) {
            getView().updateRv();
        }
    }

    public void initData(int maxImageCount, int addImgResId) {
        mMaxImageCount = maxImageCount;
        mMultiImageBeen = new LinkedList<>();
        mMultiImageBeen.add(new MultiImageBean(MultiImageBean.TYPE_ADD, MultiImageBean.FLAG_DRAWBLE,
                ImageModule.getDrawablePathUrl(addImgResId)));

        if (isViewAttached()) {
            getView().updateRv();
        }
    }

    public LinkedList<MultiImageBean> getMultiImageBeen() {
        return mMultiImageBeen;
    }

    public int getImageBeanSize() {
        return mMultiImageBeen.size();
    }

    public int getMaxImageCount() {
        return mMaxImageCount;
    }

    public void setMaxImageCount(int maxImageCount) {
        mMaxImageCount = maxImageCount;
    }

    public int getImageCnt() {
        try {
            return mMultiImageBeen.size() - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public void removeBitmap(int position) {
        mMultiImageBeen.remove(position);
        update();
    }

    public void addBitMap(ArrayList<String> imgPathList) {
        for (String path : imgPathList) {
            mMultiImageBeen.add(mMultiImageBeen.size() - 1, new MultiImageBean(MultiImageBean.TYPE_NORMAL,
                    MultiImageBean
                            .FLAG_LOCAL, path));
        }
        update();
    }

    public void addBitMap(String path) {
        //int size=mImageBeans.size()-1;
        mMultiImageBeen.add(mMultiImageBeen.size() - 1, new MultiImageBean(MultiImageBean.TYPE_NORMAL,
                MultiImageBean
                        .FLAG_LOCAL, path));
        update();
    }

    public void addNetworkBitMap(String path) {
        //int size=mImageBeans.size()-1;
        mMultiImageBeen.add(mMultiImageBeen.size() - 1, new MultiImageBean(MultiImageBean.TYPE_NORMAL,
                MultiImageBean
                        .FLAG_NETWORK, path));
        update();
    }

    public void changeBitMapPath(String oldImagePath, String newImagePath) {
        for (MultiImageBean lMultiImageBean : mMultiImageBeen) {
            if (lMultiImageBean.getPath().equals(oldImagePath)) {
                lMultiImageBean.setPath(newImagePath);
                update();
            }
        }
    }

    public void update() {
        if (isViewAttached()) {
            getView().updateRv();
            //监听图片数量的变化
            getView().updateImgCnt(getImageCnt());
        }
    }

    public ArrayList<String> getLocalBitmapPathList() {
        final ArrayList<String> lStrings = new ArrayList<>();
        for (int i = 0; i < mMultiImageBeen.size(); i++) {
            if (i != mMultiImageBeen.size() - 1) {
                MultiImageBean lImageBean = mMultiImageBeen.get(i);
                if (lImageBean.getFlag() == MultiImageBean.FLAG_LOCAL) {
                    lStrings.add(lImageBean.getPath());
                }
            }
        }
        return lStrings;
    }

    public ArrayList<String> getNetworkBitmapPathList() {
        final ArrayList<String> lStrings = new ArrayList<>();
        for (int i = 0; i < mMultiImageBeen.size(); i++) {
            if (i != mMultiImageBeen.size() - 1) {
                MultiImageBean lImageBean = mMultiImageBeen.get(i);
                if (lImageBean.getFlag() == MultiImageBean.FLAG_NETWORK) {
                    lStrings.add(lImageBean.getPath());
                }
            }
        }
        return lStrings;
    }

    public ArrayList<String> getImagePathList() {
        ArrayList<String> mImagePathList = new ArrayList<>();
        for (MultiImageBean imageBean : mMultiImageBeen) {
            mImagePathList.add(imageBean.getPath());
        }
        mImagePathList.remove(mImagePathList.size() - 1);
        return mImagePathList;
    }
}
