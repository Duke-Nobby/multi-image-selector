package com.cardapp.utils.widget.multiImageView.model.bean;

import java.io.Serializable;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Michael.Mu on 4/4/2016.
 * @version 1.0.0
 */
public class ImageUploadedBean implements Serializable {

    public static final int UPLOAD_STATUS_NOT_START = 0;
    public static final int UPLOAD_STATUS_UPLOADING = 1;
    public static final int UPLOAD_STATUS_SUCCESS = 2;

    public ImageUploadedBean(String imgUrlStr, int uploadStatus) {
        this.imgUrlStr = imgUrlStr;
        this.uploadStatus = uploadStatus;
    }

    String imgUrlStr;
    int uploadStatus = 0;

    public String getImgUrlStr() {
        return imgUrlStr;
    }

    public void setImgUrlStr(String imgUrlStr) {
        this.imgUrlStr = imgUrlStr;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
}
