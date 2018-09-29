package com.cardapp.mainland.publibs.imagemodule.uploadImgs;

import com.cardapp.utils.widget.multiImageView.model.MultiImageInterfaces;

/**
 * [Description]
 * <p> 上传多个图片回调方法，用于提供图片上传过程所需钩子方法
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2016/4/6.
 * @since 1.0.0
 */
public interface UploadMultiImageCallback {
    /**
     * 构造并返回【图片上传请求对象实例】
     * @param imgName 图片名
     * @param imgBytes 图片字节流对象
     * @return 【图片上传请求对象实例】
     */
    MultiImageInterfaces.UploadImageRequestable createUploadImageRequestable(String imgName, byte[]
            imgBytes);
}
