package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cardapp.Module.moduleImpl.view.base.CaBaseActivity;
import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import me.nereo.multi_image_selector.R;

public class ZoomPictureActivityMIS extends CaBaseActivity {

    public static final String INTENT_PICTURES = "intent.viewpager_pictures";
    public static final String INTENT_PICTURES_TITLE = "intent.viewpager_pictures_title";
    public static final String INTENT_LONG_CLICK_ENABLE = "intent.viewpager_long_click_enable";
    public static final String INTENT_CURRENT_PIC = "intent.current_picture";
    public static final String INTENT_SHOW_INDICATOR = "intent.show_indicator";
    public static final String INTENT_SHOW_TOOLBAR = "intent.show_toolbar";
    public static final String INTENT_TOOL_BAR_TITLE = "intent.toolbar_title";
    public static final String TYPE_NETWORK = "network";

    ViewPager mDisplayerPage;
    CirclePageIndicator mPagerIndicator;

    ZpTitleBarManager mToolManager;
    Toolbar mToolbar;

    private boolean mShowToolBar; // 是否显示ToolBar
    private boolean mShowIndicator; // 是否显示 Indicator
    private String mToolBarTitleStr; // TitleBar 的 标题

    private int mPosition;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mImageTitleList = new ArrayList<>();
    private boolean mLongClickEnable = true; /* 是否启用长按保存图片 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setContentView(R.layout.imagemodule_activity_zoom_picture);

        initArgs();

        findViews();

        initViews();

        updateImgPager();
    }

    /**
     * 初始化参数
     */
    private void initArgs() {
        Intent intent = getIntent();
        mImageUrls = intent.getStringArrayListExtra(INTENT_PICTURES);
        mImageTitleList = intent.getStringArrayListExtra(INTENT_PICTURES_TITLE);
        mLongClickEnable = intent.getBooleanExtra(INTENT_LONG_CLICK_ENABLE, true);
        String currentUrl = intent.getStringExtra(INTENT_CURRENT_PIC);
        mPosition = mImageUrls.indexOf(currentUrl);
        mShowIndicator = intent.getBooleanExtra(INTENT_SHOW_INDICATOR, false);
        mShowToolBar = intent.getBooleanExtra(INTENT_SHOW_TOOLBAR, false);
        mToolBarTitleStr = intent.getStringExtra(INTENT_TOOL_BAR_TITLE);
    }

    /**
     * View 的实例化
     */
    private void findViews() {
        mDisplayerPage = (ViewPager) findViewById(R.id.multi_image_selectorLib_displayer_pager);
        mPagerIndicator = (CirclePageIndicator) findViewById(R.id.zoom_picture_pager_indicator);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_zp_title_bar);

        mToolManager = new ZpTitleBarManager(ZoomPictureActivityMIS.this, mToolbar);
    }

    /**
     * 初始化 View 实例
     */
    private void initViews() {

        mToolManager.showToolBar(mShowToolBar);
        if (mShowToolBar) {
            mToolManager.getToolbar().setTitle(mToolBarTitleStr == null ? "" : mToolBarTitleStr);
        }
    }

    /**
     * 更新 图片 ViewPager
     */
    private void updateImgPager() {
        ZoomImageViewAdapterMIS zoomImageAdapter = new ZoomImageViewAdapterMIS(getSupportFragmentManager());
        zoomImageAdapter.addImageUrl(mImageUrls);
        zoomImageAdapter.addImageTitleList(mImageTitleList);
        zoomImageAdapter.setLongClickEnable(mLongClickEnable);
        mDisplayerPage.setAdapter(zoomImageAdapter);
        mDisplayerPage.setCurrentItem(mPosition);

        if (mShowIndicator && mImageUrls != null && mImageUrls.size() > 1) {
            mPagerIndicator.setVisibility(View.VISIBLE);
            mPagerIndicator.setViewPager(mDisplayerPage);
        } else {
            mPagerIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageModule.getInstance().destroyImgTitleList();
    }
}
