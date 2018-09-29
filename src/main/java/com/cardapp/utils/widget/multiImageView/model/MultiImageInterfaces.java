package com.cardapp.utils.widget.multiImageView.model;

import com.cardapp.utils.serverrequest.HttpRequestable;
import com.cardapp.utils.serverrequest.RequestArg;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Michael.Mu on 3/28/2016.
 * @version 1.0.0
 */
public class MultiImageInterfaces {
    /**
     * [Description]
     * <p>图片上传请求对象抽象类
     * 继承自{@link HttpRequestable},包含参数{@link #ImageFileBytes}及{@link #ImageFileName}，专用于图片上传库
     * [How to use]
     * <p>
     * 子类在实现{@link HttpRequestable#getHttpRequestArgs()}方法时，要根据具体的图片上传接口的参数，
     * <br> 传入的图片名用{@link #getImageFileName()}获取，
     * <br> 传入的图片字节流用{@link #getImageFileBytes()}获取，
     */
    public static abstract class UploadImageRequestable implements HttpRequestable {
        /**
         * 图片名
         */
        private String ImageFileName;
        /**
         *图片字节流
         */
        private byte[] ImageFileBytes;

        public UploadImageRequestable(String imageFileName, byte[] imageFileBytes) {
            ImageFileName = imageFileName;
            ImageFileBytes = imageFileBytes;
        }
        public byte[] getImageFileBytes() {
            return ImageFileBytes;
        }

        public String getImageFileName() {
            return ImageFileName;
        }

        public void setImageFileName(String imageFileName) {
            ImageFileName = imageFileName;
        }

        public void setImageFileBytes(byte[] imageFileBytes) {
            ImageFileBytes = imageFileBytes;
        }
    }

    /**
     *图片上传请求对象抽象类的实现类，作为案例参考
     */
    public static class UploadImageImpl extends UploadImageRequestable {

        private String UserToken;
        private String EstateId;

        public static UploadImageImpl newInstance(String userToken, byte[] imageFileBytes, String imageFileName, String
                estateId) {

//            String estateId = "01458FF465889F52";
            return new UploadImageImpl(userToken, imageFileBytes, imageFileName, estateId);
        }

        public UploadImageImpl(String userToken, byte[] imageFileBytes, String
                imageFileName, String estateId) {
            super(imageFileName,imageFileBytes);
            UserToken = userToken;
            EstateId = estateId;
        }

        @Override
        public String getMethodName() {
            return "UploadImage";
        }

        @Override
        public RequestArg[] getHttpRequestArgs() {
            return new RequestArg[]{
                    new RequestArg("UserToken", UserToken),
                    new RequestArg("ImageFileBytes", getImageFileBytes()),
                    new RequestArg("ImageFileName", getImageFileName()),
                    new RequestArg("EstateId", EstateId),
            };
        }

        @Override
        public String getTypeTag() {
            return UploadImageImpl.class.getSimpleName();
        }
    }
}
