package com.cardapp.utils.imageMark.view.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ViewAnimator;

import com.cardapp.Module.moduleImpl.view.base.CaBaseActivity;
import com.cardapp.utils.imageMark.view.ImageMarkFragment;
import com.cardapp.utils.imageMark.view.inter.ImageMarkContainerView;
import com.cardapp.utils.imageMark.view.inter.ImageMarkTitleBarView;

import java.lang.ref.WeakReference;

import me.nereo.multi_image_selector.R;
import rx.Observable;
import rx_activity_result.Result;
import rx_activity_result.RxActivityResult;

/**
 * [Description]
 * <p/> 【ImageMark】主体activity，是其他具体需求页面的容器activity
 * [How to use]
 * <p>
 * [Tips]
 * 【imagePick】
 *
 * @author Created by Jim.Huang on 2015/12/5.
 * @since 1.0.0
 */
public class ImageMarkActivity extends CaBaseActivity implements ImageMarkContainerView {
    public static final String ARG_OldImagePath = "ARG_OldImagePath";
    public static final String ARG_NewImagePath = "ARG_NewImagePath";
    public static int mContainerResID = R.id.container_fl_imagePick_activity_main;
    public static int BACKSTACKENTRYCOUNT = 1;

    /*///////////////////////////////////////////////////////////////////////////////////////////////////
    //构造方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////*/

    public static <T extends Fragment> Observable<Result<T>> start4Result(
            Context context, T fragment, String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return Observable.empty();
        }
        ImageMarkFragment.Builder lBuilder = new ImageMarkFragment.Builder(context, imagePath);
        Intent lIntent = new ABuilder(context, ImageMarkFragment.PAGE_TAG)
                .setImageMarkListFragmentBuilder(lBuilder)
                .getIntent();
        return new RxActivityResult.Builder<>(fragment)
                .startIntent(lIntent);
    }

    public static <T extends Context> Observable<Result<T>> start4Result(
            T context, String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return Observable.empty();
        }
        ImageMarkFragment.Builder lBuilder = new ImageMarkFragment.Builder(context, imagePath);
        Intent lIntent = new ABuilder(context, ImageMarkFragment.PAGE_TAG)
                .setImageMarkListFragmentBuilder(lBuilder)
                .getIntent();
        return new RxActivityResult.Builder<>(context)
                .startIntent(lIntent);
    }

    public static void start(Context context, String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        new ImageMarkActivity.ABuilder(context, ImageMarkFragment.PAGE_TAG)
                .setImageMarkListFragmentBuilder(new ImageMarkFragment.Builder(context, imagePath))
                .displayActivity();
    }

    public static class ABuilder {
        public static final String EXTRA_ImageMarkListFragmentBuilder = "EXTRA_ImageMarkListFragmentBuilder";
        public static final String EXTRA_PAGE_TAG = "EXTRA_PAGE_TAG";
        String mPageTag;
        private boolean mFlagActivityNewTask;
        private Context mContext;
        private ImageMarkFragment.Builder mImageMarkListFragmentBuilder;

        public ABuilder(Context context, String mPageTag) {
            mContext = context;
            this.mPageTag = mPageTag;
        }

        public ABuilder setFlagActivityNewTask() {
            mFlagActivityNewTask = true;
            return this;
        }

        public void displayActivity() {
            final Intent lIntent = getIntent();
            mContext.startActivity(lIntent);
        }

        @NonNull
        private Intent getIntent() {
            final Intent lIntent = new Intent(mContext, ImageMarkActivity.class);
            if (mFlagActivityNewTask || !(mContext instanceof Activity)) {
                lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            //设置PageTag参数
            lIntent.putExtra(EXTRA_PAGE_TAG, mPageTag);
            lIntent.putExtra(EXTRA_ImageMarkListFragmentBuilder, mImageMarkListFragmentBuilder);
            return lIntent;
        }

        public ABuilder setImageMarkListFragmentBuilder(ImageMarkFragment.Builder builder) {
            mImageMarkListFragmentBuilder = builder;
            return this;
        }

    }

    /*///////////////////////////////////////////////////////////////////////////
    // 参数及业务对象
    ///////////////////////////////////////////////////////////////////////////*/
    private String mPageTag;
    private ImageMarkFragmentBuilder mImageMarkFragmentBuilder;

    public Toolbar mToolbar;
    public ViewAnimator mViewAnimator;
    private ImageMarkTitleBarManager mToolBarManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Possible work around for market launches. See http://code.google
        // .com/p/android/issues/detail?id=2373
// for more details. Essentially, the market launches the main activity on top of other activities.
// we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction
                    ())) {
                Log.w("test", "Main Activity is not the root.  Finishing Main Activity instead of launching" +
                        ".");
                finish();
                return;
            }
            Log.w("test", "Main Activity is not the root. " + intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    + "  " +
                    intent.getAction());
        }
        setContentView(R.layout.image_mark_activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initData();
        initView();
        AfterViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        mPageTag = getIntent().getStringExtra(ABuilder.EXTRA_PAGE_TAG);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_imagePick_title_bar);
        mViewAnimator = (ViewAnimator) findViewById(R.id.ViewAnimator_imagePick_activity_main);
    }

    void AfterViews() {
        //初始化toolbar
        mToolBarManager = new ImageMarkTitleBarManager(this, mToolbar);
        mToolBarManager
                .init()
                .setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showFragmentUi();
    }

    public void showFragmentUi() {
        mViewAnimator.setDisplayedChild(1);
        if (ImageMarkFragment.PAGE_TAG.equals(mPageTag)) {
            mImageMarkFragmentBuilder = getIntent().getParcelableExtra(ABuilder
                    .EXTRA_ImageMarkListFragmentBuilder);
        }
        mImageMarkFragmentBuilder
                .setContext(this)
                .clearFragmentStack()
                .display();
    }

    @Override
    public void onBackPressed() {
        if (callOnBackPressed())
            return;

        closeCurrentPage();

    }

    public void closeCurrentPage() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= BACKSTACKENTRYCOUNT) {
            //销毁模块数据
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callOnActivityResult(requestCode, resultCode, data);
    }

    /*///////////////////////////////////////////////////////////////////////////
    // ImageMarkContainerView
    ///////////////////////////////////////////////////////////////////////////*/

    private WeakReference<ImageMarkBaseFragment> mCurrentFragmentWeakRef;

    public void setCurrentFragment(ImageMarkBaseFragment currentFragment) {
        mCurrentFragmentWeakRef = new WeakReference<>(currentFragment);
    }

    private void callOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentFragmentWeakRef == null) {
            return;
        }
        ImageMarkBaseFragment lImageMarkBaseFragment = mCurrentFragmentWeakRef.get();
        if (lImageMarkBaseFragment != null) {
            lImageMarkBaseFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean callOnBackPressed() {
        //执行当前frgment的后退操作
        if (mCurrentFragmentWeakRef != null) {
            ImageMarkBaseFragment lBaseFragment = mCurrentFragmentWeakRef.get();
            if (lBaseFragment != null && lBaseFragment.onBackPressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void pageBack() {
        closeCurrentPage();
    }

    @Override
    public ImageMarkTitleBarView getImageMarkToolBarView() {
        return mToolBarManager;
    }

}
