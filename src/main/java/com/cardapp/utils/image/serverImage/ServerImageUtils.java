package com.cardapp.fun.startupads.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.cardapp.utils.image.serverImage.ServerImage;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**
 * Created by wanghaobin on 04/06/2018.
 */

public class ServerImageUtils {
    /**
     * 异步发起多个请求，獲取多个图片
     *
     * @param context           上下文
     * @param reportOwnerImages bitmap格式图片实例合集
     * @param isCopyCacheFile   true則拷貝緩存圖片出新文件并返回，false則返回原緩存圖片
     * @return 多图上传结果实例观察者
     */
    @SuppressWarnings("unchecked")
    public static <T extends ServerImage> Observable<ArrayList<T>> createMultiLocalImageFromServerObservable(
            Context context, @NonNull List<T> reportOwnerImages, boolean isCopyCacheFile) {
        final ArrayList<Observable<T>> lObservables = new ArrayList<>();
        for (T lReportOwnerImage : reportOwnerImages) {
            final Observable<T> lImageUrlBeanObservable =
                    createLocalImageFromServerObservable(context, lReportOwnerImage, isCopyCacheFile);
            lObservables.add(lImageUrlBeanObservable);
        }
        final FuncN<ArrayList<T>> lZipFunction = new
                FuncN<ArrayList<T>>() {
                    @Override
                    public ArrayList<T> call(Object... args) {
                        ArrayList<T> images = new ArrayList<>();
                        for (Object lO : args) {
                            images.add((T) lO);
//                            if (lO instanceof T) {
//                            }
                        }
                        return images;
                    }
                };
        //使用zip联结多个请求并异步发起，使用案例参见：http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0325/4080.html
        return Observable.zip(lObservables, lZipFunction);
    }

    /**
     * 异步发起多个请求，獲取多个图片
     *
     * @param <T>              網絡圖片對象接口
     * @param context          上下文
     * @param reportOwnerImage 單個bitmap格式图片实例
     * @param isCopyCacheFile  true則拷貝緩存圖片出新文件并返回，false則返回原緩存圖片
     * @return 單個圖片上传结果实例观察者
     */
    public static <T extends ServerImage> Observable<T> createLocalImageFromServerObservable(
            final Context context, final T reportOwnerImage, final boolean isCopyCacheFile) {

        Observable<T> lJust = Observable.just(reportOwnerImage);
        if (!TextUtils.isEmpty(reportOwnerImage.getLocalImagePath())) {
            //非空則返回原來結果
            return lJust;
        }
        return lJust
                .observeOn(Schedulers.newThread())
                .map(new Func1<T, T>() {
                    @Override
                    public T call(T reportOwnerImage) {
                        try {
                            File lFile = Glide.with(context)
                                    .load(reportOwnerImage.getServerImage())
                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get();
                            if (lFile == null) {
                                return null;
                            }
                            //此path就是对应文件的缓存路径
                            String path;
                            if (isCopyCacheFile) {
                                path = getCopyFilePath(lFile);
                            } else {
                                path = lFile.getPath();
                            }
                            reportOwnerImage.setLocalImagePath(path);
                            return reportOwnerImage;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return reportOwnerImage;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            return reportOwnerImage;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return reportOwnerImage;
                        }
                    }
                });
    }

    private static String getCopyFilePath(File file) throws IOException {
        String lName = file.getName();
        String lCopyFileName = lName + "-temp-" + DateTime.now().getMillis();
        String lCopyFilePath = file.getPath().replace(lName, lCopyFileName);
        copy(file, new File(lCopyFilePath));
        return lCopyFilePath;
    }

    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
