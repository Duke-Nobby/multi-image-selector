package com.cardapp.utils.image.serverImage;

import android.content.Context;

/**
 * 調用{@link com.cardapp.fun.startupads.presenter.ServerImageUtils#createLocalImageFromServerObservable(Context, ServerImage)}
 *
 * @author wanghaobin
 * @date 04/06/2018
 */
public interface ServerImage {
    /**
     * 下載到本地的圖片文件路徑
     *
     * @return 下載到本地的圖片文件路徑
     */
    String getLocalImagePath();

    /**
     * 下載到本地的圖片文件路徑。
     *
     * @param localImagePath 下載到本地的圖片文件路徑
     */
    void setLocalImagePath(String localImagePath);

    /**
     * 獲取圖片服務器文件地址
     *
     * @return 獲取圖片服務器文件地址
     */
    String getServerImage();
}
