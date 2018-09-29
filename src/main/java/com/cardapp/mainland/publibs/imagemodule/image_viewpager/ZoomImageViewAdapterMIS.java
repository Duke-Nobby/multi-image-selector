package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ZoomImageViewAdapterMIS extends FragmentPagerAdapter {

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mImageTitleList = new ArrayList<>(); /* 图片标题 */
    private boolean mLongClickEnable = true; /* 是否启用长按保存图片 */


    public ZoomImageViewAdapterMIS(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment imageFragment = new ZoomImageFragmentV2(position);
        if (getCount() > 0) {
            Bundle args = new Bundle();
            args.putString(ZoomImageFragmentV2.ARGS_IMAGE_URL, mImageUrls.get(position));

            /* 图片的名称，默认为空，然后再去参数中找，看是否有传值过来 */
            String lImgTitle = "";
            if (mImageTitleList != null && mImageTitleList.size() > position) {
                lImgTitle = mImageTitleList.get(position);
            }

            args.putString(ZoomImageFragmentV2.ARGS_IMAGE_TITLE, lImgTitle);
            args.putBoolean(ZoomImageFragmentV2.ARGS_LONG_CLICK_ENABLE, mLongClickEnable);
            imageFragment.setArguments(args);
        }
        return imageFragment;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    public void addImageUrl(ArrayList<String> urls) {
        mImageUrls.addAll(urls);
    }

    public void addImageTitleList(ArrayList<String> titleList) {
        mImageTitleList.clear();
        if (titleList != null) {
            mImageTitleList.addAll(titleList);
        }
    }

    public void setLongClickEnable(boolean enable) {
        mLongClickEnable = enable;
    }

    public List<String> getUrls() {
        return mImageUrls;
    }


}
