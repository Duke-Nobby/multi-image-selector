package com.cardapp.mainland.publibs.compoundview.multiImageWidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.mainland.publibs.imagemodule.image_viewpager.BottomSelectorPopDialog;
import com.cardapp.utils.widget.multiImageView.view.MultiImageLy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import me.nereo.multi_image_selector.R;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2015/11/5.
 * @since 1.0.0
 * @deprecated please use {@link MultiImageLy} instead
 */
public class MultiImageCv extends LinearLayout {

    private RecyclerView mOrderListRv;
    private Listener mListener;
    private LinkedList<ImageBean> mImageBeans;
    private BottomSelectorPopDialog mBottomSelectorPopDialog;
    private int maxImageCount;
    private ArrayList<String> mNetworkUrlList;

    public MultiImageCv(Context context) {
        super(context);
        initializeViews(context);
    }

    public MultiImageCv(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }


    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.imagemodule_multi_images, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findView();
    }

    private void findView() {
        mOrderListRv = (RecyclerView) findViewById(R.id.imageList_rv_cv_multi_images);
    }

    public void initView(int spanCount, int maxCount) {
        if (spanCount > 5) {
            spanCount = 5;
        }
        mImageBeans = new LinkedList<>();
        maxImageCount = maxCount;

        mImageBeans.add(new ImageBean(ImageBean.TYPE_ADD, ImageBean.FLAG_DRAWBLE, "drawable://" + R.drawable.imagemodule_ic_add_to));
        if (mNetworkUrlList != null) {
            for (int i = 0; i < mNetworkUrlList.size(); i++) {
                mImageBeans.add(i, new ImageBean(ImageBean.TYPE_NORMAL, ImageBean.FLAG_NETWORK, mNetworkUrlList.get(i)));
            }
        }

        mOrderListRv.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        updateRv();
    }

    private void updateRv() {
        MultiImageAdapter.Listener lListener = new MultiImageAdapter.Listener() {
            @Override
            public void onImageBtnClick(int position, int count) {
                if (position == mImageBeans.size() - 1) {
                    //选取图片
                    go2pickActivity(count);
                } else {
                    //显示图片
                }
            }

            @Override
            public void onDeleteBtnClick(int position) {
                //监听图片数量的变化
                if (mListener != null) {
                    mListener.onImageCntChange(getImageCnt());
                }
            }
        };

        MultiImageAdapter lMultiImageAdapter = new MultiImageAdapter(getContext(), mImageBeans, maxImageCount);
        lMultiImageAdapter.setListener(lListener);
        mOrderListRv.setAdapter(lMultiImageAdapter);
    }

    private void go2pickActivity(int count) {
        ImageModule.MultiPagerPicker lMultiPagerPicker = new ImageModule.MultiPagerPicker() {
            @Override
            public void onImagePickSucc(ArrayList<String> resultList) {
                for (String path : resultList) {
                // 在最后一个之前插入addString(path);
                    addBitMap(path);
                }
            }
        };
        ImageModule.getInstance().pickMultiImages(getContext(), lMultiPagerPicker, maxImageCount - count);
    }



    private void showPublishActionSelector() {
        if (mBottomSelectorPopDialog == null) {
            String choose_photolibrary = getContext().getString(R.string.util_dialog_choose_photolibrary);
            String choose_camera = getContext().getString(R.string.util_dialog_choose_camera);
            CharSequence[] actionTexts = new CharSequence[]{choose_photolibrary, choose_camera};
            mBottomSelectorPopDialog = new BottomSelectorPopDialog(getContext(), actionTexts);
            mBottomSelectorPopDialog.setListener(new BottomSelectorPopDialog.Listener() {
                @Override
                public void onBtnItemClick(int position) {
                    switch (position) {
                        case 0://
                            break;
                        case 1://
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onCancelBtnClick() {

                }
            });
        }
        mBottomSelectorPopDialog.show();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void addBitMap(String path) {
        //int size=mImageBeans.size()-1;
        mImageBeans.add(mImageBeans.size() - 1, new ImageBean(ImageBean.TYPE_NORMAL, ImageBean.FLAG_LOCAL, path));
        // mOrderListRv.getAdapter().notifyItemRangeInserted(size,3);
        updateRv();
        //监听图片数量的变化
        if (mListener != null) {
            mListener.onImageCntChange(getImageCnt());
        }
    }

    private int getImageCnt() {
        try {
            return mImageBeans.size() - 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ArrayList<String> getLocalBitmapPathList() {
        final ArrayList<String> lStrings = new ArrayList<>();
        for (int i = 0; i < mImageBeans.size(); i++) {
            if (i != mImageBeans.size() - 1) {
                ImageBean lImageBean = mImageBeans.get(i);
                if (lImageBean.getFlag() == ImageBean.FLAG_LOCAL) {
                    lStrings.add(lImageBean.getPath());
                }
            }
        }
        return lStrings;
    }


    public ArrayList<String> getNetworkBitmapPathList() {
        final ArrayList<String> lStrings = new ArrayList<>();
        for (int i = 0; i < mImageBeans.size(); i++) {
            if (i != mImageBeans.size() - 1) {
                ImageBean lImageBean = mImageBeans.get(i);
                if (lImageBean.getFlag() == ImageBean.FLAG_NETWORK) {
                    lStrings.add(lImageBean.getPath());
                }
            }
        }
        return lStrings;
    }

    public void setNetworkUrlList(ArrayList<String> urlList) {
        mNetworkUrlList = urlList;
    }

    public void addLocalUrlList(ArrayList<String> urlList) {
        if (mImageBeans != null) {
            if (urlList != null) {
                for (int i = 0; i < urlList.size(); i++) {
                    mImageBeans.add(i, new ImageBean(ImageBean.TYPE_NORMAL, ImageBean.FLAG_LOCAL, urlList.get(i)));
                }
            }
        }
        updateRv();
    }

    public interface Callback {

        void onStartGalleyBtnClick();

        void onStartCameraBtnClick();

    }

    public interface Listener {

        void onImageCntChange(int imageCnt);

    }

    public class ImageBean {

        public static final int TYPE_ADD = 1;
        public static final int TYPE_NORMAL = 2;

        public static final int FLAG_LOCAL = 3;
        public static final int FLAG_NETWORK = 4;
        public static final int FLAG_DRAWBLE = 5;
        private int mType;
        private String mString;
        private String mPath;
        private int mFlag;

        public ImageBean(int type, String string) {
            mType = type;
            mString = string;
        }

        public String getString() {
            return mString;
        }

        public ImageBean(int type, int flag, String path) {
            mType = type;
            mPath = path;
            mFlag = flag;
        }

        public int getType() {
            return mType;
        }

        public String getPath() {
            return mPath;
        }

        public int getFlag() {
            return mFlag;
        }

        public boolean isAdd() {
            return mType == MultiImageCv.ImageBean.TYPE_ADD;
        }

        public Bitmap getBitmap() {
            return createImageThumbnail(mPath);
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inJustDecodeBounds = false;
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), m, true);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }


}
