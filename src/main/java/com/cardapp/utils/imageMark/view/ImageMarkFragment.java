package com.cardapp.utils.imageMark.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cardapp.utils.imageMark.presenter.FulPictureEditionPresenter;
import com.cardapp.utils.imageMark.view.base.ImageMarkActivity;
import com.cardapp.utils.imageMark.view.base.ImageMarkBaseFragment;
import com.cardapp.utils.imageMark.view.base.ImageMarkFragmentBuilder;
import com.cardapp.utils.imageMark.view.inter.ImageMarkingView;
import com.cardapp.utils.widget.HandWrite;

import butterknife.ButterKnife;
import me.nereo.multi_image_selector.R;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by JJ.Lin on 2016/3/15.
 * @since 1.0.0
 */
public class ImageMarkFragment extends ImageMarkBaseFragment<ImageMarkingView,
        FulPictureEditionPresenter> implements ImageMarkingView {
    public static final String PAGE_TAG = ImageMarkFragment.class.getSimpleName();


    public static class Builder extends ImageMarkFragmentBuilder<ImageMarkFragment> implements
            Parcelable {
        private String mImagePath;

        public Builder(Context context, String imagePath) {
            super(context);
            mImagePath = imagePath;
        }

        @Override
        public ImageMarkFragment create() {
            ImageMarkFragment lImageMarkFragment = new ImageMarkFragment();
            Bundle lBundle = new Bundle();
            lBundle.putString(ImageMarkActivity.ARG_OldImagePath, mImagePath);
            lImageMarkFragment.setArguments(lBundle);
            return lImageMarkFragment;
        }

        @Override
        public String getPageTag() {
            return PAGE_TAG;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mImagePath);
        }

        protected Builder(Parcel in) {
            this.mImagePath = in.readString();
        }

        public static final Parcelable.Creator<Builder> CREATOR = new Parcelable.Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel source) {
                return new Builder(source);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };
    }

    HandWrite mHandWrite;
    LinearLayout mPreviousStep;
    LinearLayout mNextStep;
    LinearLayout mClickEditLl;
    Button mClickEditBtn;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataAction();
    }

    private void dataAction() {
        initArgs();
    }

    private void initArgs() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_mark_fragment_edit_image, container, false);
    }

    @NonNull
    @Override
    public FulPictureEditionPresenter createPresenter() {
        final String lImagePath = getArguments().getString(ImageMarkActivity.ARG_OldImagePath);
        return new FulPictureEditionPresenter(lImagePath);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uiAction();
        presenter.updateUi();
    }

    private void uiAction() {
        findViews(getView());
        initUI();
    }

    private void findViews(View view) {
        ButterKnife.bind(this, view);
        mHandWrite = (HandWrite) view.findViewById(R.id.hand_write_imageMark_fragment_edit_image);
        mPreviousStep = (LinearLayout) view.findViewById(R.id.previous_step_imageMark_fragment_edit_image);
        mNextStep = (LinearLayout) view.findViewById(R.id.next_step_imageMark_fragment_edit_image);
        mClickEditLl = (LinearLayout) view.findViewById(R.id.click2EditLl_imageMark_fragment_edit_image);
        mClickEditBtn = (Button) view.findViewById(R.id.click2EditBtn_imageMark_fragment_edit_image);
    }

    @Override
    public void showImageEditorUi(Bitmap bitmap) {
        mHandWrite.setPicture(bitmap);
        mHandWrite.setEditEnable(false);

    }

    @Override
    public void afterConfirm(String newImagePath) {
        Intent lData = new Intent();
        final String lOldImagePath = getArguments().getString(ImageMarkActivity.ARG_OldImagePath);
        lData.putExtra(ImageMarkActivity.ARG_OldImagePath, lOldImagePath);
        lData.putExtra(ImageMarkActivity.ARG_NewImagePath, newImagePath);
        getActivity().setResult(Activity.RESULT_OK, lData);
        getContainerView().pageBack();
    }

    private void initUI() {
        getToolBarManager().setTitle(getString(R.string.imageMark_pageTitle_Images_Edit));
        getToolBarManager().showConfirmTv(true);
        getToolBarManager().clickConfirmTv(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap lBitmap = mHandWrite.getBitmap();
                presenter.submitPicture(lBitmap);
            }
        });
        View.OnClickListener lL = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickEditLl.setVisibility(View.GONE);
                mHandWrite.setEditEnable(true);
            }
        };
        mClickEditLl.setOnClickListener(lL);
        mClickEditBtn.setOnClickListener(lL);

        mPreviousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandWrite.undo();
            }
        });

        mNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandWrite.next_do();
            }
        });
    }
}
