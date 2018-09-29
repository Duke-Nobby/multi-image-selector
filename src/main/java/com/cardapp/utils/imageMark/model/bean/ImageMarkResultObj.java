package com.cardapp.utils.imageMark.model.bean;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2016/12/31.
 * @since 1.0.0
 */
public class ImageMarkResultObj {
    private String mNewImagePath;
    private String mOldImagePath;

    public ImageMarkResultObj() {
    }

    public ImageMarkResultObj(String newImagePath, String oldImagePath) {
        mNewImagePath = newImagePath;
        mOldImagePath = oldImagePath;
    }

    public String getNewImagePath() {
        return mNewImagePath;
    }

    public String getOldImagePath() {
        return mOldImagePath;
    }
}
