package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cardapp.Module.moduleImpl.view.dialog.BottomSelectorPopDialog;
import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.mainland.publibs.imagemodule.view.TouchImageView;
import com.cardapp.utils.imageUtils.ImageBuilder;
import com.cardapp.utils.resource.L;
import com.cardapp.utils.resource.SysRes;
import com.cardapp.utils.resource.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.nereo.multi_image_selector.R;

import static android.R.attr.description;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Jim.Huang on 2015/12/21.
 * @since 1.0.0
 */

public class ZoomImageFragmentV2 extends Fragment {
    public static final String ARGS_IMAGE_URL = "fragment.image_url";
    public static final String ARGS_IMAGE_TITLE = "fragment.image_title";
    public static final String ARGS_LONG_CLICK_ENABLE = "fragment.enable_long_click";
    private int position;
    TouchImageView mImage;
    TextView mImgTitleTv;
    View mProgressView;
    private Bitmap mBitmap;

    public ZoomImageFragmentV2() {
    }

    @SuppressLint("ValidFragment")
    public ZoomImageFragmentV2(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.imagemodule_item_zoom_picture, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        View lView = getView();
        findView(lView);

        final Bundle args = getArguments();

        if (args == null) {
            return;
        }
        String imageUrl = args.getString(ARGS_IMAGE_URL);
        String lImgTitle = args.getString(ARGS_IMAGE_TITLE);
        boolean lLongClickEnable = args.getBoolean(ARGS_LONG_CLICK_ENABLE);
        if (imageUrl == null) {
            return;
        }

        mImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

            /* 设置长按处理事件 */
        if (lLongClickEnable) {
            mImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showBottomSelectorPopDialog();
                    return false;
                }
            });
        }

        if (lImgTitle != null && !TextUtils.isEmpty(lImgTitle)) {
            mImgTitleTv.setText(lImgTitle);
        }

        // 多图添加时网络图片预览

        if (imageUrl.startsWith("http") || imageUrl.startsWith("Http")) {
            boolean lShowOriginalImage = ImageModule.getInstance().isShowOriginalImage();
            if (lShowOriginalImage) {
                SysRes.setVisibleOrGone(mProgressView, true);
                SysRes.setVisibleOrGone(mImage, false);
            }
            new ImageBuilder()
                    .setShowOriginalImage(lShowOriginalImage)
                    .setOnLoadingListener(new ImageBuilder.OnLoadingListener() {
                        @Override
                        public void loadSucc() {
                            SysRes.setVisibleOrGone(mProgressView, false);
                            SysRes.setVisibleOrGone(mImage, true);
                            L.e("test", "loadSucc");
                        }

                        @Override
                        public void loadFail() {
                            SysRes.setVisibleOrGone(mProgressView, false);
                            SysRes.setVisibleOrGone(mImage, true);
                            L.e("test", "loadFail");
                        }

                        @Override
                        public void loading(int i) {

                        }
                    })
                    .display(mImage, imageUrl);
//                    Helper_Image.displayImage_network(mImage, imageUrl);
        } else if (imageUrl.startsWith(ImageModule.URL_TITLE_drawable)) {
            // 包內图片预览
            try {
                int lImageResId = Integer.parseInt(imageUrl.replace(ImageModule.URL_TITLE_drawable, ""));
                mImage.setImageResource(lImageResId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
//                    Helper_Image.displayImage_network(mImage, imageUrl);
        } else {
            //多图添加时本地图片预览
//                    Helper_Image.displayImage_local_string(imageUrl, mImage);
            Bitmap lBitmap = createImageThumbnail(imageUrl);
            lBitmap = getOriBitmap(imageUrl, lBitmap);
            mImage.setImageBitmap(lBitmap);

        }
    }

    private void findView(View view) {
        mImage = (TouchImageView) view.findViewById(R.id.multi_image_selectorLib_display_picture);
        mProgressView = view.findViewById(R.id.progress_image_module_item_zoom_picture);
        mImgTitleTv = (TextView) view.findViewById(R.id.multi_image_selectorLib_img_title);
    }

    private Bitmap getOriBitmap(String imageUrl, Bitmap bitmap) {
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        try {

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

    ImageSize lImageSize = new ImageSize(512, 512);

    private void showBottomSelectorPopDialog() {
        String saveStr = getContext().getString(R.string.save_to_phone);
        CharSequence[] actionTexts = new CharSequence[]{saveStr};
        BottomSelectorPopDialog mBottomSelectorPopDialog = new BottomSelectorPopDialog(getActivity(),
                actionTexts, R.color.colorAccent);
        mBottomSelectorPopDialog.setListener(new BottomSelectorPopDialog.Listener() {
            @Override
            public void onBtnItemClick(int position) {
                switch (position) {
                    case 0:
                        saveImageToGallery();
                    default:
                        break;
                }
            }

            @Override
            public void onCancelBtnClick() {
            }
        });
        mBottomSelectorPopDialog.show();
    }

    private void saveImageToGallery() {
        String imageUrl = getArguments().getString(ARGS_IMAGE_URL);
        if (imageUrl.startsWith("http")) {
            Glide.with(this).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mBitmap = resource;
                    saveImageToGallery(getActivity(), mBitmap);
                    Toast.Short(getActivity(), getContext().getString(R.string.save_success));
                }
            });
//            mBitmap = ImageLoader.getInstance().loadImageSync(imageUrl);
        } else {
            String lUri = "file://" + imageUrl;
            mBitmap = ImageLoader.getInstance().loadImageSync(lUri, lImageSize);
            saveImageToGallery(getActivity(), mBitmap);
            Toast.Short(getActivity(), getContext().getString(R.string.save_success));
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), SysRes.getApplicationName(context));
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()); // DATE HERE
        values.put(MediaStore.Images.Media.MIME_TYPE, "mImage/jpeg");
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" +
                fileName)));
    }


    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        opts.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }
}
