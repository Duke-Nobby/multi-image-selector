package com.cardapp.utils.widget.multiImageView.model.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;
import java.io.Serializable;

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
public class MultiImageBean implements Serializable {
    public static final int TYPE_ADD = 1;
    public static final int TYPE_NORMAL = 2;

    public static final int FLAG_LOCAL = 3;
    public static final int FLAG_NETWORK = 4;
    public static final int FLAG_DRAWBLE = 5;

    public static final int UPLOAD_STATUS_NOT_START = 6;
    public static final int UPLOAD_STATUS_UPLOADING = 7;
    public static final int UPLOAD_STATUS_FINISH = 8;

    private int mType;
    private String mString;
    private String mPath;
    private int mFlag;

    public int mUploadedStatus; //上传状态

    public MultiImageBean(int type, String string) {
        mType = type;
        mString = string;
        mUploadedStatus = UPLOAD_STATUS_NOT_START;
    }

    public String getString() {
        return mString;
    }

    public MultiImageBean(int type, int flag, String path) {
        mType = type;
        mFlag = flag;
        mPath = path;
        mUploadedStatus = UPLOAD_STATUS_NOT_START;
    }

    public int getType() {
        return mType;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public int getFlag() {
        return mFlag;
    }

    public boolean isAdd() {
        return mType == TYPE_ADD;
    }

    public int getUploadedStatus() {
        return mUploadedStatus;
    }

    public void setUploadedStatus(int uploadedStatus) {
        mUploadedStatus = uploadedStatus;
    }

    public Bitmap getBitmap() {
        return createImageThumbnail(mPath);
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
        } catch (Exception ignored) {
        }
        return bitmap;
    }

}
