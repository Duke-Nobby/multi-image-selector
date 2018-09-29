package com.cardapp.utils.widget.multiImageView.view;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.utils.helper.PermissionHelper;
import com.cardapp.utils.mvp.layout.MvpLinearLayout;
import com.cardapp.utils.widget.multiImageView.model.bean.MultiImageBean;
import com.cardapp.utils.widget.multiImageView.presenter.MultiImagePresenter;
import com.cardapp.utils.widget.multiImageView.view.adapter.MultiImageAdapterV2;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.LinkedList;

import rx.functions.Action1;

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
public class MultiImageLy extends MvpLinearLayout<MultiImageView, MultiImagePresenter> implements
        MultiImageView {

    private RecyclerView mRecyclerView;
    private onImageCntChangeListener mOnImageCntChangeListener;
    private int mMaxImageCount;
    private boolean mIsNotInitView = false;
    private MultiImageAdapterV2 mMultiImageAdapter;
    private int mAddImgResId;
    private boolean mIsInMultiPickMode = true;
    private boolean mCut;
    private int mCutAspectRatioX = 1;
    private int mCutAspectRatioY = 1;


    public MultiImageLy(Context context) {
        super(context);
        initializeViews(context);
    }

    public MultiImageLy(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MultiImageLy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public MultiImageLy(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(me.nereo.multi_image_selector.R.layout.imagemodule_multi_images_v2, this);
        mAddImgResId = me.nereo.multi_image_selector.R.drawable.imagemodule_ic_add_to;
    }

    @NonNull
    @Override
    public MultiImagePresenter createPresenter() {
        return new MultiImagePresenter();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        reset();
        updateView();
    }

    public void reset() {
        presenter.initData(mMaxImageCount, mAddImgResId);
    }

    private void findViews() {
        mRecyclerView = (RecyclerView) findViewById(me.nereo.multi_image_selector.R.id
                .imageList_rv_cv_multi_images);
    }

    private void updateView() {
//        if (!mIsNotInitView) { // 只有在初始化的时候才显示外部传过来的图片，添加和删除图片的过程中都不在更新图片
//            mIsNotInitView = true;
//
//        }
        presenter.update();
    }

    /**
     * @param spanCount     显示图片的列数
     * @param maxImageCount 最多可添加的图片数量
     */
    public void initView(int spanCount, int maxImageCount) {

        //图片最多5列
        if (spanCount > 5) {
            spanCount = 5;
        }

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mMaxImageCount = maxImageCount;
    }

    /**
     * 设置初始化显示的图片
     *
     * @param imgPathList
     */
    public MultiImageLy setInitImgs(ArrayList<String> imgPathList) {
        presenter.addBitMap(imgPathList);
        return this;
    }

    /**
     * 設置添加圖片的icon
     *
     * @param addImgResId
     * @return
     */
    public MultiImageLy setAddImgResId(int addImgResId) {
        mAddImgResId = addImgResId;
        return this;
    }

    @Override
    public void updateRv() {
        MultiImageAdapterV2.Listener lListener = new MultiImageAdapterV2.Listener() {
            @Override
            public void onAddBtnClick(int position, int count) {
                //选取图片
                go2pickActivity();
            }

            @Override
            public void onImageBtnClick(int position, int count) {
                //显示图片
                MultiImageBean lMultiImageBean = presenter.getMultiImageBeen().get(position);
                MultiImageLy.this.onImageBtnClick(position, lMultiImageBean);
            }

            @Override
            public void onDeleteBtnClick(int position) {
                // 监听图片数量的变化
                if (mOnImageCntChangeListener != null) {
                    mOnImageCntChangeListener.onImageCntChange(presenter.getImageCnt());
                }
            }
        };

        mMultiImageAdapter = new MultiImageAdapterV2(getContext(), presenter, mAddImgResId);
        mMultiImageAdapter.setListener(lListener);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mMultiImageAdapter);
    }

    protected void onImageBtnClick(int position, MultiImageBean multiImageBean) {
        ArrayList<String> mImagePathList = presenter
                .getImagePathList();
        ImageModule.getInstance()
                .showMultiImagePreviewPage(getContext(),
                        mImagePathList, position);

    }

    private void go2pickActivity() {

        RxPermissions.getInstance(getContext())
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(
                        new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) {
                                    // 用户赋予使用存储空间和相机的权限
                                    showPicker();
                                } else {
//                                    Toast.Short(getContext(), getContext().getString(R.string
//                                            .please_grant_camera_storage_first));
//                                    PermissionHelper
//                                            .startInstalledAppDetailsActivity((Activity) getContext());
                                    PermissionHelper.showMissingPermissionDialog(getContext());
                                }
                            }
                        });
    }

    private void showPicker() {

        if (mIsInMultiPickMode) {
            int lMaxImageCount = presenter
                    .getMaxImageCount() - presenter.getImageCnt();
            ImageModule.MultiPagerPicker lMultiPagerPicker = new ImageModule.MultiPagerPicker() {
                @Override
                public void onImagePickSucc(ArrayList<String> resultList) {
                    presenter.addBitMap(resultList);

                    onGetImageSucc(resultList);
                }
            };
            ImageModule.getInstance()
                    .pickMultiImages(getContext(), lMultiPagerPicker, lMaxImageCount);
        } else {
            ImageModule.getInstance()
                    .createSingleImgPickerBuilder(getContext(), new ImageModule.SingleImgPicker1() {
                        @Override
                        public void onSingleImgPickSucc(Uri uri) {
                            String lPath = uri.getPath();
                            presenter.addBitMap(lPath);
                            ArrayList<String> resultList = new ArrayList<>();
                            resultList.add(lPath);
                            onGetImageSucc(resultList);
                        }

                        @Override
                        public void onSingleImgPickFail() {

                        }
                    })
                    .setCut(mCut)
                    .setCutAspectRatio(mCutAspectRatioX, mCutAspectRatioY)
                    .startPick();
        }
    }


    /**
     * 設置裁剪及裁剪比例
     * <p>
     * //默認設置為單選模式
     *
     * @param cutAspectRatioX X 寬
     * @param cutAspectRatioY Y 高
     * @return this
     */
    public MultiImageLy setCutAspectRatio(int cutAspectRatioX, int cutAspectRatioY) {
        //默認設置為單選模式
        setSinglePickMode();
        mCut = true;
        mCutAspectRatioX = cutAspectRatioX;
        mCutAspectRatioY = cutAspectRatioY;
        return this;
    }

    public MultiImageLy setSinglePickMode() {
        mIsInMultiPickMode = false;
        return this;
    }

    protected void onGetImageSucc(ArrayList<String> resultList) {

    }

    @Override
    public void updateImgCnt(int imageCnt) {
        if (mOnImageCntChangeListener != null) {
            mOnImageCntChangeListener.onImageCntChange(presenter.getImageCnt());
        }
    }

    protected void replaceImage(String oldImagePath, String newImagePath) {
        presenter.changeBitMapPath(oldImagePath, newImagePath);
    }

    public void addNetworkImage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        for (String lS : imagePath.split(",")) {
            presenter.addNetworkBitMap(lS);
        }
    }

    public void setNetworkUrlList(ArrayList<String> urlList) {
        for (String lS : urlList) {
            presenter.addNetworkBitMap(lS);
        }
    }


    public void setOnImageCntChangeListener(onImageCntChangeListener onImageCntChangeListener) {
        mOnImageCntChangeListener = onImageCntChangeListener;
    }

    public interface onImageCntChangeListener {
        void onImageCntChange(int imageCnt);
    }


    public ArrayList<String> getLocalBitmapPathList() {
        return presenter.getLocalBitmapPathList();
    }

    public ArrayList<String> getServerBitmapPathList() {
        return presenter.getNetworkBitmapPathList();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.mMultiImageBeen = presenter.getMultiImageBeen();
        ss.mMaxImageCount = presenter.getMaxImageCount();

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end
        presenter.initData(ss.mMultiImageBeen, ss.mMaxImageCount);

    }

    static class SavedState extends BaseSavedState {
        LinkedList<MultiImageBean> mMultiImageBeen;
        private int mMaxImageCount;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.mMultiImageBeen = new LinkedList<>();
            in.readList(this.mMultiImageBeen, MultiImageBean.class.getClassLoader());
            this.mMaxImageCount = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(this.mMultiImageBeen);
            out.writeInt(this.mMaxImageCount);
        }

    }

    public void updateLv() {
        presenter.update();
    }

}
