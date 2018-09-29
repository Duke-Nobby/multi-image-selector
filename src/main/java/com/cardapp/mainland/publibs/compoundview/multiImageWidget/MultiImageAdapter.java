package com.cardapp.mainland.publibs.compoundview.multiImageWidget;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.mainland.publibs.imagemodule.image_viewpager.Helper_Image;
import com.cardapp.utils.helper.PermissionHelper;
import com.cardapp.utils.imageUtils.ImageBuilder;
import com.cardapp.utils.view.OnDebouncedClickListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.LinkedList;

import me.nereo.multi_image_selector.R;
import rx.functions.Action1;


/**
 * {Description}
 *
 * @author Created by Administrator on 2015/10/22.
 * @deprecated please use MultiImageAdapterV2 instead
 */
public class MultiImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private LinkedList<MultiImageCv.ImageBean> mImageBeans;
    private Listener mListener;
    private ArrayList<String> imageList;
    private int mMaxImageCount = 9;

    public MultiImageAdapter(Context context, LinkedList<MultiImageCv.ImageBean> imageBeans, int maxCount) {
        mContext = context;
        mImageBeans = imageBeans;
        imageList = new ArrayList<>();
        for (MultiImageCv.ImageBean imageBean :
                imageBeans) {
            imageList.add(imageBean.getPath());
        }
        imageList.remove(imageList.size() - 1);
        if (maxCount != 0) {
            mMaxImageCount = maxCount;
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View lView = LayoutInflater.from(mContext).inflate(R.layout.imagemodule_item_imageview_normal,
                parent, false);
        return new ChildViewHold(lView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindHolder((ChildViewHold) holder, position);
    }

    private void bindHolder(final ChildViewHold holder, final int position) {
        final MultiImageCv.ImageBean lImageBean = mImageBeans.get(position);
        switch (lImageBean.getFlag()) {
            case MultiImageCv.ImageBean.FLAG_LOCAL:
                Bitmap lBitmap = lImageBean.getBitmap();
                holder.mImageView.setImageBitmap(lBitmap);
                break;
            default:
            case MultiImageCv.ImageBean.FLAG_DRAWBLE:
                Helper_Image.displayImage_network(holder.mImageView, lImageBean.getPath());
            case MultiImageCv.ImageBean.FLAG_NETWORK:
                new ImageBuilder()
                        .setDefaultImageRes(R.drawable.imagemodule_ic_add_to)
                        .display(holder.mImageView, lImageBean.getPath());
                break;
        }
//        if (lImageBean.getFlag() == MultiImageCv.ImageBean.FLAG_LOCAL) {
//            Bitmap lBitmap = lImageBean.getBitmap();
//            holder.mImageView.setImageBitmap(lBitmap);
//        }
//        if (lImageBean.getFlag() == MultiImageCv.ImageBean.FLAG_NETWORK
//                || lImageBean.getFlag() == MultiImageCv.ImageBean.FLAG_DRAWBLE) {
//            Helper_Image.displayImage_network(holder.mImageView, lImageBean.getPath());
//        }
        holder.mImageView.setOnClickListener(new OnDebouncedClickListener() {
            @Override
            public void onDebouncedClick(View view) {
                RxPermissions.getInstance(mContext).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest
                                .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).subscribe
                        (new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) { // 用户赋予使用存储空间和相机的权限
                                    int p = holder.getAdapterPosition();
                                    if (mListener != null) {
                                        if (p == mImageBeans.size() - 1) {
                                            mListener.onImageBtnClick(p, mImageBeans.size() - 1);
                                        } else {
                                            ImageModule.getInstance().showMultiImagePreviewPage(mContext,
                                                    imageList, p);
                                        }
                                    }
                                } else {
                                    PermissionHelper.showMissingPermissionDialog(mContext);
                                }
                            }
                        });
            }
        });
        boolean b = lImageBean.isAdd();
//        holder.mDeleteTv.setVisibility(b ? View.GONE : View.VISIBLE);
        if (b) {
            holder.mDeleteTv.setVisibility(View.GONE);
        } else {
            holder.mDeleteTv.setVisibility(View.VISIBLE);
            holder.mDeleteTv.setOnClickListener(new OnDebouncedClickListener() {
                @Override
                public void onDebouncedClick(View view) {

                    try {
                        mImageBeans.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mImageBeans.size() - position);
                /*imageList.clear();
                for (MultiImageCv.ImageBean imageBean :
                        mImageBeans) {
                    imageList.add(imageBean.getPath());
                }
                imageList.remove(imageList.size()-1);*/
                        imageList.remove(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mListener != null) {
                        mListener.onDeleteBtnClick(position);
                    }
                }
            });
        }

    }

    private boolean isAdd(MultiImageCv.ImageBean orderBean) {
        return orderBean.getType() == MultiImageCv.ImageBean.TYPE_ADD;
    }

    @Override
    public int getItemCount() {
        if (mImageBeans.size() > mMaxImageCount) {
            return mMaxImageCount;
        } else {
            return mImageBeans.size();
        }
    }

    class ChildViewHold extends RecyclerView.ViewHolder {
        private final ImageView mDeleteTv;
        ImageView mImageView;

        public ChildViewHold(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_mainlandlib_item_imageview_normal);
            mDeleteTv = (ImageView) itemView.findViewById(R.id.delete_tv_mainlandlib_item_imageview_normal);
        }
    }

    public interface Listener {
        void onImageBtnClick(int position, int count);

        void onDeleteBtnClick(int position);
    }

}
