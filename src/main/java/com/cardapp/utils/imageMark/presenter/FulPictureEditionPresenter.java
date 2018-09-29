package com.cardapp.utils.imageMark.presenter;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.cardapp.utils.imageMark.view.inter.ImageMarkingView;
import com.cardapp.utils.mvp.BasePresenter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * [Description]
 * <p/>16.5.圖片編輯
 * [How to use]
 * <p/>1、對圖片的編輯步驟，進行撤銷、恢復撤銷。
 * <p>
 * 2、圖片區域，圖片根據實際比例，在此區域最大化顯示。兩點同時觸摸可放大縮小圖片（此功能有待商量）。
 * 單點觸摸時，可在圖片上畫不規則線條，顏色默認為紅色。
 * <p>
 * 3、畫圖筆提示語。圖片區域開始畫線後，該提示語消失。畫線撤銷完時，提示語也不出現。返回，重新進入該頁面時，提示語顯示。
 * <p>
 * 4、圖片編輯完成後，點擊確定保存修改後的圖片，保存編輯內容，並返回進事項內容列表。
 * [Tips]
 *
 * @author Created by Woode.Wang on 2016/1/13.
 * @since 1.0.0
 */
@SuppressWarnings("ConstantConditions")
public class FulPictureEditionPresenter extends BasePresenter<ImageMarkingView> {
    private static final String TAG = FulPictureEditionPresenter.class.getSimpleName();
    private final String mImagePath;

    public FulPictureEditionPresenter(String imagePath) {
        mImagePath = imagePath;
        mEditingBitmap = createImageThumbnail(imagePath);
    }

    /*///////////////////////////////////////////////////////////////////////////
            // 创建参数及业务对象
            ///////////////////////////////////////////////////////////////////////////*/
    private Bitmap mEditingBitmap;

    public void updateUi() {
        getView().showImageEditorUi(mEditingBitmap);
    }

    public Bitmap getEditingPieceBitmap() {
        return mEditingBitmap;
    }

    private byte[] getByte4Bitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        try {

            bitmap = ImageLoader.getInstance().loadImageSync("file://" + filePath, new ImageSize(520, 520));
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

    public void submitPicture(Bitmap bitmap) {
        mEditingBitmap = bitmap;
        //保存图片到本地
        // FIXME: 2016/6/29 by woode 修复《》No.: 这里应该考虑保存新图，防止到自己定义的文件夹
        // TODO: 2016/6/29 by woode: 完成需求《》：并应清除图片加载控件改图的缓存
        saveBitmap2Local(mImagePath, mEditingBitmap);
        getView().afterConfirm(mImagePath);
    }

    private void saveBitmap2Local(String path, Bitmap editingBitmap) {
        Log.e(TAG, "保存图片");
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            editingBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
