package com.cardapp.utils.widget.multiImageView.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.BuildConfig;

import com.cardapp.utils.mvp.model.ModelSource;
import com.cardapp.utils.mvp.model.ServerResultErrorCodePreHandler;
import com.cardapp.utils.serverrequest.HttpRequestable;
import com.cardapp.utils.serverrequest.ServerOption;
import com.cardapp.utils.widget.multiImageView.model.bean.ImageUploadedBean;
import com.cardapp.utils.widget.multiImageView.model.bean.MultiImageBean;

import java.util.ArrayList;
import java.util.LinkedList;

import rx.Observable;
import rx.functions.Func1;


/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Michael.Mu on 3/24/2016.
 * @version 1.0.0
 */
public class MultiImageDataManager {

    /*///////////////////////////////////////////////////////////////////////////
    // 楷林物业接口地址
    ///////////////////////////////////////////////////////////////////////////*/
    //楷林物业测试版接口地址：
    public static final String WEBSERVICE_URL_TEST = "http://kineermobile.test.cn-cic.com/KineerService.svc";
    //楷林物业正式版接口地址：
    public static final String WEBSERVICE_URL_OFFICIAL = "http://kineermobile.cn-cic.com/KineerService.svc";
    public static final String REMOTE_INTERFACE = "IKineerService";

    public static ServerOption mServerOption;

    public static HttpRequestable mRequestable;

    private static volatile MultiImageCache sMultiImageCache = new MultiImageCache();

    public static MultiImageCache getMultiImageCache() {
        return sMultiImageCache;
    }

    public static void destroyMultiImageCache(){
        sMultiImageCache = new MultiImageCache();
    }

    public static class MultiImageCache{

        public MultiImageCache() {
            mUploadedImgStrs = new ArrayList<>();
            mImageUploadedBeen = new ArrayList<>();
        }

        public LinkedList<MultiImageBean> mMultiImageBeen;

        /**
         * 【上传成功的图片】列表缓存
         */
        public ArrayList<String> mUploadedImgStrs;

        public ArrayList<ImageUploadedBean> mImageUploadedBeen;

        public int mImgStrListSize;

        public int mUploadedImgCnt;

        public int getUploadedImgCnt() {
            return mUploadedImgCnt;
        }

        public void setUploadedImgCnt(int uploadedImgCnt) {
            mUploadedImgCnt = uploadedImgCnt;
        }

        public void addUploadedImgcnt(){
            mUploadedImgCnt++;
        }

        public ArrayList<ImageUploadedBean> getImageUploadedBeen() {
            return mImageUploadedBeen;
        }

        public void addImageUploadedBeen(int finalI, String uploadedImgStr) {
            ImageUploadedBean lImageUploadedBean = new ImageUploadedBean(uploadedImgStr, ImageUploadedBean.UPLOAD_STATUS_SUCCESS);
            mImageUploadedBeen.add(finalI, lImageUploadedBean);
        }

        public void addSuccImageUrl(int finalI, String uploadedImgStr) {
            mUploadedImgStrs.add(finalI, uploadedImgStr);
        }



        public LinkedList<MultiImageBean> getMultiImageBeen() {
            return mMultiImageBeen;
        }

        public void setMultiImageBeen(LinkedList<MultiImageBean> multiImageBeen) {
            mMultiImageBeen = multiImageBeen;
        }

        public ArrayList<String> getUploadedImgStrs() {
            return mUploadedImgStrs;
        }

        public void setUploadedImgStrs(ArrayList<String> uploadedImgStrs) {
            mUploadedImgStrs = uploadedImgStrs;
        }

        public void addUploadedImgStr(int ImgStrListSize, int finalI, String uploadedImgStr) {
            mImgStrListSize = ImgStrListSize;
//            mUploadedImgStrs.add(finalI, uploadedImgStr);
            addImageUploadedBeen(finalI, uploadedImgStr);
        }

        public void removeUploadedImgStr(int position) {

            mUploadedImgStrs.remove(position);
        }
    }

    public static ServerOption getServerOption() {
        return mServerOption;
    }

    public MultiImageDataManager setServerOption(ServerOption mServerOption) {
        MultiImageDataManager.mServerOption = mServerOption;
        return this;
    }

    public static HttpRequestable getRequestable() {
        return mRequestable;
    }

    public MultiImageDataManager setRequestable(HttpRequestable mRequestable) {
        MultiImageDataManager.mRequestable = mRequestable;
        return this;
    }

    @NonNull
    public static ServerOption getBgServerOption() {
        String webserviceUrl = BuildConfig.DEBUG ? WEBSERVICE_URL_TEST : WEBSERVICE_URL_OFFICIAL;
        String remoteInterface = REMOTE_INTERFACE;
        return new ServerOption(webserviceUrl, remoteInterface);
    }

    public static Observable<String> UploadImage(Context context, ServerOption lServerOption,
                                                 HttpRequestable requestable, int timeout) {

        final ModelSource<String, HttpRequestable> lModelSource =
                new ModelSource<>(context, lServerOption, requestable,
                        new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                return s;
                            }
                        });

        return lModelSource
                .setIsDataValidFun(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s != null;
                    }
                })
                /*设置忽略网络请求失败*/
                .setIfThrowOutNetworkException(false)
                /*//设置请求结果的预处理*/
                .setPreHandleErrorcodeFunc(ServerResultErrorCodePreHandler.createPreHandleErrorcodeFunc())
                .setLogMode(true, true)
                //本地缓存优先
                .setMode(ModelSource.MODE_N)
                .setTimeout(timeout)
                .Observable();
    }

}
