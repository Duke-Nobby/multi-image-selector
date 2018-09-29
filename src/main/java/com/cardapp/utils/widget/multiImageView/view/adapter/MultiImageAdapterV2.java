package com.cardapp.utils.widget.multiImageView.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.utils.imageUtils.ImageBuilder;
import com.cardapp.utils.widget.multiImageView.model.bean.MultiImageBean;
import com.cardapp.utils.widget.multiImageView.presenter.MultiImagePresenter;


/**
 * {Description}
 *
 * @author Created by Michael.Mu on 2015/10/22.
 */
public class MultiImageAdapterV2 extends RecyclerView.Adapter {

    private Context mContext;
    private Listener mListener;
    private MultiImagePresenter mPresenter;
    private int mAddImgResId;

    public MultiImageAdapterV2(Context context, MultiImagePresenter presenter, @DrawableRes int addImgResId) {
        mContext = context;
        mPresenter = presenter;
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
        View lView = LayoutInflater.from(mContext).inflate(me.nereo.multi_image_selector.R.layout.imagemodule_item_imageview_normal,
                parent, false);
        return new ChildViewHold(lView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindHolder((ChildViewHold) holder, position);
    }

    private void bindHolder(final ChildViewHold holder, final int position) {
        final MultiImageBean lImageBean = mPresenter.getMultiImageBeen().get(position);
        ImageView.ScaleType lScaleType = lImageBean.getType() == MultiImageBean.TYPE_ADD &&
                lImageBean.getFlag() == MultiImageBean.FLAG_DRAWBLE ?
                ImageView.ScaleType.CENTER_INSIDE : ImageView.ScaleType.CENTER_CROP;
        holder.mImageView.setScaleType(lScaleType);
        switch (lImageBean.getFlag()) {
            case MultiImageBean.FLAG_LOCAL:
                Bitmap lBitmap = lImageBean.getBitmap();
                holder.mImageView.setImageBitmap(lBitmap);
                break;
            default:
            case MultiImageBean.FLAG_DRAWBLE:
                // 包內图片预览
                try {
                    String lReplace =
                            lImageBean.getPath().replace(ImageModule.URL_TITLE_drawable, "");
                    int lImageResId = Integer.parseInt(lReplace);
                    holder.mImageView.setImageResource(lImageResId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case MultiImageBean.FLAG_NETWORK:
                new ImageBuilder()
                        .animate()
                        .display(holder.mImageView, lImageBean.getPath());
                break;
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lPosition = holder.getAdapterPosition();
                int lCount = mPresenter.getImageBeanSize() - 1;
                if (lPosition == lCount) {
                    if (mListener != null) {
                        mListener.onAddBtnClick(lPosition, lCount);
                    }
                } else {

                    if (mListener != null) {
                        mListener.onImageBtnClick(lPosition, lCount);
                    }
                }
            }
        });

        boolean b = lImageBean.isAdd();
//        holder.mDeleteTv.setVisibility(b ? View.GONE : View.VISIBLE);
        if (b) {
            holder.mDeleteTv.setVisibility(View.GONE);
        } else {
            holder.mDeleteTv.setVisibility(View.VISIBLE);
        }
        holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // TODO: 2017/10/31 by Michael.Mu: 动画会导致崩溃，先去掉动画
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position, mPresenter.getImageBeanSize() - position);
                    mPresenter.getMultiImageBeen().remove(position);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mListener != null) {
                    mListener.onDeleteBtnClick(position);
                }
            }
        });
    }

    private boolean isAdd(MultiImageBean orderBean) {
        return MultiImageBean.TYPE_ADD == orderBean.getType();
    }

    @Override
    public int getItemCount() {
        if (mPresenter.getImageBeanSize() > mPresenter.getMaxImageCount()) {
            return mPresenter.getMaxImageCount();
        } else {
            return mPresenter.getImageBeanSize();
        }
    }

    class ChildViewHold extends RecyclerView.ViewHolder {
        private final ImageView mDeleteTv;
        ImageView mImageView;

        public ChildViewHold(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(me.nereo.multi_image_selector.R.id.iv_mainlandlib_item_imageview_normal);
            mDeleteTv = (ImageView) itemView.findViewById(me.nereo.multi_image_selector.R.id.delete_tv_mainlandlib_item_imageview_normal);
        }
    }

    public interface Listener {
        void onAddBtnClick(int position, int count);

        void onImageBtnClick(int position, int count);

        void onDeleteBtnClick(int position);
    }


}
