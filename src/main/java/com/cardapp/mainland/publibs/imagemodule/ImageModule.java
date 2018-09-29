package com.cardapp.mainland.publibs.imagemodule;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.cardapp.Module.BaseModule;
import com.cardapp.Module.OnModuleFragmentListener;
import com.cardapp.mainland.publibs.imagemodule.image_viewpager.ZoomPictureActivityMIS;
import com.cardapp.mainland.publibs.imagemodule.image_viewpager.ZoomPictureTransparentActivity;
import com.cardapp.mainland.publibs.imagemodule.uploadImgs.UploadMultiImageCallback;
import com.cardapp.utils.helper.PermissionHelper;
import com.cardapp.utils.resource.L;
import com.cardapp.utils.serverrequest.ServerOption;
import com.cardapp.utils.widget.multiImageView.model.MultiImageDataManager;
import com.cardapp.utils.widget.multiImageView.model.MultiImageInterfaces;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Jim.Huang on 2015/11/19. updated by Michael.Mu on 2016/04/04
 * @since 1.0.0
 */

public class ImageModule implements BaseModule {

    public static final String URL_TITLE_drawable = "drawable://";
    public static final String URL_ImageDemo = "http://imgsrc.baidu" +
            ".com/forum/w%3D580/sign=2e07200d8301a18bf0eb1247ae2e0761" +
            "/3dc0912397dda14408e7ca93b6b7d0a20cf48638.jpg";
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //多图回调接口
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private MultiPagerPicker mMultiPagerPicker;

    private UploadImgListener mUploadImgListener;

    /*单张图回调接口*/
    @Deprecated
    private SingleImgPicker mSingleImgPicker;
    private SingleImgPicker1 mSingleImgPicker1;

    /**
     * 獲取包內drawable圖片資源url，ImageModule庫可以識別，用於顯示包內drawable圖片
     *
     * @param addImgResId
     * @return
     */
    @NonNull
    public static String getDrawablePathUrl(@DrawableRes int addImgResId) {
        return URL_TITLE_drawable + addImgResId;
    }

    public void destroyMultiPagerPicker() {
        mMultiPagerPicker = null;
    }

    public void destroySingleImgPicker() {
        mSingleImgPicker = null;
    }

    public void destroyImgTitleList() {
        mImgTitleList = new ArrayList<>();
    }

    private int ImageCount = 9;

    public interface MultiPagerPicker {

        void onImagePickSucc(ArrayList<String> resultList);
    }

    public interface SingleImgPicker {
        void onSingleImgPickSucc(Uri uri);
    }

    public interface SingleImgPicker1 {
        void onSingleImgPickSucc(Uri uri);

        void onSingleImgPickFail();
    }

    public interface UploadImgListener {
        void onUploadSucc();

        void onUploadFail();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //多图选择器创建者
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class MultiImagesPickerBuilder {

        private Context mContext1;
        private boolean mShowCamera = true;
        private int mSelectCount = 9;
        private boolean isSingleChoose = false;
        private boolean isCut = false;
        private int mCutAspectRatioX = 1;
        private int mCutAspectRatioY = 1;

        public MultiImagesPickerBuilder(Context context1) {
            mContext1 = context1;
        }

        public MultiImagesPickerBuilder setShowCamera(boolean showCamera) {
            mShowCamera = showCamera;
            return this;
        }

        public MultiImagesPickerBuilder setSelectCount(int selectCount) {
            mSelectCount = selectCount;
            return this;
        }

        public MultiImagesPickerBuilder setSingleChooseMode() {
            this.isSingleChoose = true;
            return this;
        }

        /**
         * 设置1:1剪裁
         *
         * @return
         */
        public MultiImagesPickerBuilder setCutSquare() {
            setCutAspectRatio(1, 1);
            return this;
        }

        public MultiImagesPickerBuilder setCutAspectRatio(int cutAspectRatioX, int cutAspectRatioY) {
            mCutAspectRatioX = cutAspectRatioX;
            mCutAspectRatioY = cutAspectRatioY;
            return this;
        }

        public MultiImagesPickerBuilder setCut(boolean cut) {
            isCut = cut;
            return this;
        }

        public MultiImagesPickerBuilder setSingleChooseAndCutMode() {
            this.isSingleChoose = true;
            this.isCut = true;
            return this;
        }

        public void startPick() {

            RxPermissions.getInstance(mContext1).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(
                            new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {

                                        Intent mIntent = new Intent(mContext1, MultiImageSelectorActivity
                                                .class);
// 是否显示调用相机拍照
                                        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA,
                                                mShowCamera);
// 最大图片选择数量
                                        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT,
                                                mSelectCount);
// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
                                        final int lModeMulti = !isSingleChoose ? MultiImageSelectorActivity
                                                .MODE_MULTI :
                                                MultiImageSelectorActivity.MODE_SINGLE;
                                        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
                                                lModeMulti);
                                        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_CUT, isCut);

                                        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_AspectRatio_X,
                                                mCutAspectRatioX);
                                        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_AspectRatio_Y,
                                                mCutAspectRatioY);
                                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext1.startActivity(mIntent);
                                    } else {
                                        PermissionHelper.showMissingPermissionDialog(mContext1);
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                    );
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //单例
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static ImageModule sImageModule;


    private Context mContext;

    public static ImageModule getInstance() {
        if (sImageModule == null) {
            synchronized (ImageModule.class) {
                if (sImageModule == null) {
                    sImageModule = new ImageModule();
                }
            }
        }
        return sImageModule;
    }

    public ImageModule init(Context context) {
        this.mContext = context.getApplicationContext();
        return this;
    }

    /**
     * @deprecated 使用 {@link #showMultiImagePreviewPage(Context, ArrayList, int)}替代
     */
    @Deprecated
    public void previewBigPictures_network(int position, ArrayList<String> imageList, Context context) {
        showMultiImagePreviewPage(context, imageList, position);
    }

    /**
     * 显示多图浏览页面，可放大缩小
     *
     * @param context      上下文
     * @param imageListStr 图片集合
     */
    public void showMultiImagePreviewPage(Context context, String imageListStr) {
        ArrayList<String> lImageList = new ArrayList<>();
        String[] lSplit = imageListStr.split(",");
        lImageList.addAll(Arrays.asList(lSplit));
        showMultiImagePreviewPage(context, lImageList, 0);
    }

    /**
     * 显示多图浏览页面，可放大缩小
     *
     * @param context   上下文
     * @param imageList 图片集合
     * @param position  首先显示的图片的位置
     */
    public void showMultiImagePreviewPage(Context context, ArrayList<String> imageList, int position) {
        Intent picIntent = new Intent(context, ZoomPictureActivityMIS.class);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES, imageList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES_TITLE, mImgTitleList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_LONG_CLICK_ENABLE, mShowLongClickMenu);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_CURRENT_PIC, imageList.get(position));
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_SHOW_INDICATOR, mShowIndicator); /* 默认不显示
        ViewPager的Indicator */
        picIntent.putExtra(ZoomPictureActivityMIS.TYPE_NETWORK, 2);
        context.startActivity(picIntent);
    }

    /**
     * 显示多图浏览页面，可放大缩小
     *
     * @param context   上下文
     * @param imageList 图片集合
     * @param position  首先显示的图片的位置
     */
    public void showMultiImagePreviewPageWithToolbar(Context context, ArrayList<String> imageList, int
            position) {
        Intent picIntent = new Intent(context, ZoomPictureActivityMIS.class);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES, imageList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES_TITLE, mImgTitleList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_LONG_CLICK_ENABLE, mShowLongClickMenu);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_CURRENT_PIC, imageList.get(position));
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_SHOW_INDICATOR, mShowIndicator);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_TOOL_BAR_TITLE, mImgPagerTitle);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_SHOW_TOOLBAR, mShowToolBar);
        picIntent.putExtra(ZoomPictureActivityMIS.TYPE_NETWORK, 2);
        context.startActivity(picIntent);
    }

    /**
     * 显示多图浏览页面，可放大缩小
     *
     * @param context   上下文
     * @param imageList 图片集合
     * @param position  首先显示的图片的位置
     */
    public void showMultiImagePreviewPageWithTransparentBackground(Context context, ArrayList<String>
            imageList, int
                                                                           position) {
        Intent picIntent = new Intent(context, ZoomPictureTransparentActivity.class);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES, imageList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES_TITLE, mImgTitleList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_LONG_CLICK_ENABLE, mShowLongClickMenu);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_CURRENT_PIC, imageList.get(position));
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_SHOW_INDICATOR, mShowIndicator);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_TOOL_BAR_TITLE, mImgPagerTitle);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_SHOW_TOOLBAR, mShowToolBar);
        picIntent.putExtra(ZoomPictureActivityMIS.TYPE_NETWORK, 2);
        context.startActivity(picIntent);
    }

    public void test4you(Context context, ArrayList<String>
            imageList, int position) {

    }

    private String mImgPagerTitle;
    private boolean mShowIndicator;
    private boolean mShowToolBar;
    private ArrayList<String> mImgTitleList = new ArrayList<>();
    /**
     * 是否启用长按保存图片
     */
    private boolean mShowLongClickMenu = true;

    /**
     * 是否下載顯示原圖
     */
    private boolean mShowOriginalImage;

    public boolean isShowOriginalImage() {
        return mShowOriginalImage;
    }

    /**
     * @param showOriginalImage 是否下載顯示原圖
     * @return this
     */
    public ImageModule setShowOriginalImage(boolean showOriginalImage) {
        mShowOriginalImage = showOriginalImage;
        return this;
    }

    /**
     * @param showLongClickMenu 是否启用长按保存图片
     * @return this
     */
    public ImageModule setShowLongClickMenu(boolean showLongClickMenu) {
        mShowLongClickMenu = showLongClickMenu;
        return this;
    }

    @Deprecated
    public ImageModule setLongClickEnable(boolean longClickEnable) {
        mShowLongClickMenu = longClickEnable;
        return this;
    }

    public ImageModule setImgPagerTitle(String imgPagerTitle) {
        mImgPagerTitle = imgPagerTitle;
        return this;
    }

    public ImageModule showImgPagerIndicator(boolean showIndicator) {
        mShowIndicator = showIndicator;
        return this;
    }

    public ImageModule showImgPagerToolBar(boolean showToolBar) {
        mShowToolBar = showToolBar;
        return this;
    }

    public ImageModule setImgTitleList(ArrayList<String> imgTitleList) {
        mImgTitleList.clear();
        mImgTitleList.addAll(imgTitleList);
        return this;
    }

    private ImageBackListener mImageBack;

    public void setImageBack(ImageBackListener imageBack) {
        mImageBack = imageBack;
    }

    public ImageBackListener getImageBack() {
        return mImageBack;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void displayAsActivity() {

    }

    public MultiPagerPicker getMultiPagerPicker() {
        return mMultiPagerPicker;
    }

    @Deprecated
    public SingleImgPicker getSingleImgPicker() {
        return mSingleImgPicker;
    }

    public SingleImgPicker1 getSingleImgPicker1() {
        return mSingleImgPicker1;
    }

    public MultiImagesPickerBuilder createMultiImagesPickerBuilder(Context context, MultiPagerPicker
            multiPagerPicker) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        mMultiPagerPicker = multiPagerPicker;
        return new MultiImagesPickerBuilder(mContext);
    }

    @Deprecated
    public MultiImagesPickerBuilder createSingleImgPickerBuilder(Context context, SingleImgPicker
            singleImgPicker) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        mSingleImgPicker = singleImgPicker;
        return new MultiImagesPickerBuilder(mContext).setSingleChooseMode();
    }

    public MultiImagesPickerBuilder createSingleImgPickerBuilder(Context context, SingleImgPicker1
            singleImgPicker) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        mSingleImgPicker1 = singleImgPicker;
        return new MultiImagesPickerBuilder(mContext).setSingleChooseMode();
    }

    public void pickMultiImages(Context context, MultiPagerPicker multiPagerPicker, int maxImageCount) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        if (maxImageCount != 0) {
            ImageCount = maxImageCount;
        }
        mMultiPagerPicker = multiPagerPicker;
        Intent mIntent = new Intent(mContext, MultiImageSelectorActivity.class);

// 是否显示调用相机拍照
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

// 最大图片选择数量
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, ImageCount);

// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }

    /**
     * 异步发起多个请求，上传多个图片
     *
     * @param context                  上下文
     * @param serverOption             后台地址配置对象
     * @param uploadMultiImageCallback 多图上传钩子回调实例
     * @param fileImageStrList         本地图片路径合集
     * @param resultObjConvertfunc     rxjava功能实例：将后台请求结果转化为图片结果实例<T>
     * @param <T>                      图片结果实例
     * @return 多图上传结果实例观察者
     */
    public <T> Observable<ArrayList<T>> uploadMultiImages(@NonNull final Context context, ServerOption
            serverOption,
                                                          UploadMultiImageCallback uploadMultiImageCallback,
                                                          @NonNull ArrayList<String> fileImageStrList,
                                                          Func1<String, T> resultObjConvertfunc) {
        return uploadMutilImages(context, serverOption, uploadMultiImageCallback, fileImageStrList,
                resultObjConvertfunc, 60000);
    }

    /**
     * 已廢棄，使用{@link #uploadMultiImages(Context, ServerOption, UploadMultiImageCallback, ArrayList, Func1)}
     * 异步发起多个请求，上传多个图片
     *
     * @param context                  上下文
     * @param serverOption             后台地址配置对象
     * @param uploadMultiImageCallback 多图上传钩子回调实例
     * @param fileImageStrList         本地图片路径合集
     * @param resultObjConvertfunc     rxjava功能实例：将后台请求结果转化为图片结果实例<T>
     * @param <T>                      图片结果实例
     * @return 多图上传结果实例观察者
     */
    @Deprecated
    public <T> Observable<ArrayList<T>> uploadMutilImages(@NonNull final Context context, ServerOption
            serverOption,
                                                          UploadMultiImageCallback uploadMultiImageCallback,
                                                          @NonNull ArrayList<String> fileImageStrList,
                                                          Func1<String, T> resultObjConvertfunc) {
        return uploadMutilImages(context, serverOption, uploadMultiImageCallback, fileImageStrList,
                resultObjConvertfunc, 60000);
    }

    /**
     * 异步发起多个请求，上传多个图片
     *
     * @param context                  上下文
     * @param serverOption             后台地址配置对象
     * @param uploadMultiImageCallback 多图上传钩子回调实例
     * @param fileImageStrList         本地图片路径合集
     * @param resultObjConvertfunc     rxjava功能实例：将后台请求结果转化为图片结果实例<T>
     * @param timeout                  每個圖片上傳接口的超時時間
     * @param <T>                      图片结果实例
     * @return 多图上传结果实例观察者
     */
    public <T> Observable<ArrayList<T>> uploadMutilImages(@NonNull final Context context, ServerOption
            serverOption,
                                                          UploadMultiImageCallback uploadMultiImageCallback,
                                                          @NonNull ArrayList<String> fileImageStrList,
                                                          Func1<String, T> resultObjConvertfunc, int
                                                                  timeout) {
        final ArrayList<Bitmap> lBitmaps = new ArrayList<>();
        for (String lImgPath : fileImageStrList) {
            Bitmap lBitmap = ImageLoader.getInstance().loadImageSync("file://" + lImgPath);
            lBitmaps.add(lBitmap);
        }
        if (lBitmaps.size() == 1) {
            L.d("调用上传单张图片方法");
            return uploadSingleImage(context, serverOption, lBitmaps.get(0), uploadMultiImageCallback,
                    resultObjConvertfunc, timeout)
                    .map(new Func1<T, ArrayList<T>>() {
                        @Override
                        public ArrayList<T> call(T t) {
                            ArrayList<T> lArrayList = new ArrayList<>();
                            lArrayList.add(t);
                            return lArrayList;
                        }
                    });
        } else {
            L.d("调用上传多张图片方法");
            return uploadMultiImages1(context, serverOption, lBitmaps, uploadMultiImageCallback,
                    resultObjConvertfunc, timeout);
        }

    }

    /**
     * 异步发起多个请求，上传多个图片
     *
     * @param context                  上下文
     * @param serverOption             后台地址配置对象
     * @param singleBitmap             图片bitmap
     * @param uploadMultiImageCallback 多图上传钩子回调实例
     * @param resultObjConvertfunc     rxjava功能实例：将后台请求结果转化为图片结果实例<T>
     * @param <T>                      图片结果实例
     * @return 多图上传结果实例观察者
     */
    @SuppressWarnings("unchecked")
    public <T> Observable<T> uploadSingleImage(@NonNull final Context context, ServerOption
            serverOption,
                                               @NonNull Bitmap singleBitmap,
                                               UploadMultiImageCallback uploadMultiImageCallback,
                                               Func1<String, T> resultObjConvertfunc) {
        return uploadSingleImage(context, serverOption, singleBitmap, uploadMultiImageCallback,
                resultObjConvertfunc, 60000);
    }

    /**
     * 异步发起多个请求，上传多个图片
     *
     * @param context                  上下文
     * @param serverOption             后台地址配置对象
     * @param singleBitmap             图片bitmap
     * @param uploadMultiImageCallback 多图上传钩子回调实例
     * @param resultObjConvertfunc     rxjava功能实例：将后台请求结果转化为图片结果实例<T>
     * @param timeout                  每個圖片上傳接口的超時時間
     * @param <T>                      图片结果实例
     * @return 多图上传结果实例观察者
     */
    @SuppressWarnings("unchecked")
    public <T> Observable<T> uploadSingleImage(@NonNull final Context context, ServerOption
            serverOption,
                                               @NonNull Bitmap singleBitmap,
                                               UploadMultiImageCallback uploadMultiImageCallback,
                                               Func1<String, T> resultObjConvertfunc, int timeout) {
        final Context lApplicationContext = context.getApplicationContext();
        final String lImageFileName = DateTime.now().getMillis() + ".jpg";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        singleBitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        singleBitmap.recycle();
        byte[] lImgBytes = stream.toByteArray();
        final String lImgName = "#" + " " + lImageFileName;
        MultiImageInterfaces.UploadImageRequestable lRequestable =
                uploadMultiImageCallback.createUploadImageRequestable(lImgName, lImgBytes);
        final Observable<T> lImageUrlBeanObservable = MultiImageDataManager
                .UploadImage(lApplicationContext, serverOption, lRequestable, timeout)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(resultObjConvertfunc);


        return lImageUrlBeanObservable;
    }

    /**
     * 异步发起多个请求，上传多个图片
     *
     * @param context                  上下文
     * @param serverOption             后台地址配置对象
     * @param imageBitmapList          bitmap格式图片实例合集
     * @param uploadMultiImageCallback 多图上传钩子回调实例
     * @param resultObjConvertfunc     rxjava功能实例：将后台请求结果转化为图片结果实例<T>
     * @param <T>                      图片结果实例
     * @return 多图上传结果实例观察者
     */
    @SuppressWarnings("unchecked")
    public <T> Observable<ArrayList<T>> uploadMutilImages1(@NonNull final Context context, ServerOption
            serverOption,
                                                           @NonNull ArrayList<Bitmap> imageBitmapList,
                                                           UploadMultiImageCallback uploadMultiImageCallback,
                                                           Func1<String, T> resultObjConvertfunc) {
        return uploadMultiImages1(context, serverOption, imageBitmapList, uploadMultiImageCallback,
                resultObjConvertfunc, 60000);
    }

    /**
     * 异步发起多个请求，上传多个图片
     *
     * @param context                  上下文
     * @param serverOption             后台地址配置对象
     * @param imageBitmapList          bitmap格式图片实例合集
     * @param uploadMultiImageCallback 多图上传钩子回调实例
     * @param resultObjConvertfunc     rxjava功能实例：将后台请求结果转化为图片结果实例<T>
     * @param timeout                  每個圖片上傳接口的超時時間
     * @param <T>                      图片结果实例
     * @return 多图上传结果实例观察者
     */
    @SuppressWarnings("unchecked")
    public <T> Observable<ArrayList<T>> uploadMultiImages1(@NonNull final Context context, ServerOption
            serverOption,
                                                           @NonNull ArrayList<Bitmap> imageBitmapList,
                                                           UploadMultiImageCallback uploadMultiImageCallback,
                                                           Func1<String, T> resultObjConvertfunc, int
                                                                   timeout) {
        final Context lApplicationContext = context.getApplicationContext();
        final ArrayList<Observable<T>> lObservables = new ArrayList<>();
        final String lImageFileName = DateTime.now().getMillis() + ".jpg";
        final int lImgListSize = imageBitmapList.size();
        for (int i = 0; i < lImgListSize; i++) {
            Bitmap lBitmap = imageBitmapList.get(i);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            lBitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            lBitmap.recycle();
            byte[] lImgBytes = stream.toByteArray();
            final String lImgName = "#" + i + " " + lImageFileName;
            MultiImageInterfaces.UploadImageRequestable lRequestable =
                    uploadMultiImageCallback.createUploadImageRequestable(lImgName, lImgBytes);
            final Observable<T> lImageUrlBeanObservable = MultiImageDataManager
                    .UploadImage(lApplicationContext, serverOption, lRequestable, timeout)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(resultObjConvertfunc);
            lObservables.add(lImageUrlBeanObservable);
        }
        final FuncN<ArrayList<T>> lZipFunction = new
                FuncN<ArrayList<T>>() {
                    @Override
                    public ArrayList<T> call(Object... args) {
                        ArrayList<T> images = new ArrayList<>();
                        for (Object lO : args) {
                            images.add((T) lO);
//                            if (lO instanceof T) {
//                            }
                        }
                        return images;
                    }
                };
        //使用zip联结多个请求并异步发起，使用案例参见：http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0325/4080.html
        return Observable.zip(lObservables, lZipFunction);
    }

    @Override
    public void displayInFragment(int i, OnModuleFragmentListener onModuleFragmentListener) {

    }

    @Override
    public void displayInFragment(int i) {

    }

    @Override
    public void changeLanguageMode(Locale locale) {

    }

    @Override
    public void popBack() {

    }

    @Override
    public void reAttach() {

    }

}
