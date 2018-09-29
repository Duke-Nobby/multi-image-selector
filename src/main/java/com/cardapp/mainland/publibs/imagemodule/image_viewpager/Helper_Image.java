package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Jim.Huang on 2015/11/26.
 * @since 1.0.0
 * @deprecated 改用ImageBuilder().display的方式加载图片
 */
@Deprecated
public class Helper_Image {
    static int FADEINTIME = 200;
    /**
     * 使用ImageLoader加载图片
     *
     * @param image
     * @param imageUrl
     */
    public static void displayImage_network(ImageView image, String imageUrl) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
//                .showImageOnLoading(R.drawable.loading_bg_cn_2_1)
//                .showImageOnFail(R.drawable.loading_bg_cn_2_1)
//                .showImageForEmptyUri(R.drawable.loading_bg_cn_2_1)
                .cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(FADEINTIME))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, image, options);
    }

    public static void previewBigPictures_local(int position, ArrayList<String> imageList, Context context){
        Intent picIntent = new Intent(context, ZoomPictureActivityMIS.class);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES,
                imageList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_CURRENT_PIC, imageList.get(position));
        context.startActivity(picIntent);
    }

    public static void previewBigPictures_local(int position, LinkedList<String> imageList, Context context){
        Intent picIntent = new Intent(context, ZoomPictureActivityMIS.class);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_PICTURES,
                imageList);
        picIntent.putExtra(ZoomPictureActivityMIS.INTENT_CURRENT_PIC, imageList.get(position));
        context.startActivity(picIntent);
    }

    public static void displayImage_local_string(String string,ImageView image){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
//                .showImageOnLoading(R.drawable.loading_bg_cn_2_1)
//                .showImageOnFail(R.drawable.loading_bg_cn_2_1)
//                .showImageForEmptyUri(R.drawable.loading_bg_cn_2_1)
                .cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(FADEINTIME)).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        String imagePath = "/mnt/sdcard/mImage.png";
        String imageUrl = ImageDownloader.Scheme.FILE.wrap(imagePath);
        ImageLoader.getInstance().displayImage(string, image, options);
    }
}
