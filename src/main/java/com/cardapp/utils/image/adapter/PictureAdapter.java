package com.cardapp.utils.image.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.utils.imageUtils.ImageBuilder;

import java.util.ArrayList;

import me.nereo.multi_image_selector.R;

/**
 * Created by wanghaobin on 01/01/2018.
 */


public class PictureAdapter extends RecyclerView.Adapter {

    private final ArrayList<String> mImageUrls;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public PictureAdapter(Context context, ArrayList<String> imageUrls, LayoutInflater layoutInflater) {
        mContext = context;
        mImageUrls = imageUrls;
        mLayoutInflater = layoutInflater;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PictureVh.newInstance(mLayoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String lImageUrl = mImageUrls.get(position);
        final PictureAdapter.PictureVh lHolder = (PictureVh) holder;
        new ImageBuilder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .display(lHolder.lImageView, lImageUrl);
        lHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lAdapterPosition = lHolder.getAdapterPosition();
                ImageModule.getInstance()
                        .showMultiImagePreviewPage(mContext, mImageUrls, lAdapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public static class PictureVh extends RecyclerView.ViewHolder {
        ImageView lImageView;

        public PictureVh(View itemView) {
            super(itemView);
            lImageView = (ImageView) itemView.findViewById(R.id.iv_imagemodule_item_picture);
        }

        public static RecyclerView.ViewHolder newInstance(LayoutInflater from, ViewGroup parent) {
            View lView = from.inflate(R.layout.imagemodule_item_picture, parent, false);
            return new PictureVh(lView);
        }
    }
}
