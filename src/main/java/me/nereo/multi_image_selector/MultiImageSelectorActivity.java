package me.nereo.multi_image_selector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.utils.resource.L;
import com.cardapp.utils.resource.Toast;
import com.yalantis.ucrop.UCrop;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//import com.yalantis.ucrop.UCrop;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorActivity extends FragmentActivity implements MultiImageSelectorFragment
        .Callback {

    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_CUT = "CUT";

    /* 剪裁图片比例为1:1 */
    public static final String EXTRA_AspectRatio_X = "EXTRA_AspectRatio_X";

    /* 剪裁图片比例为1:1 */
    public static final String EXTRA_AspectRatio_Y = "EXTRA_AspectRatio_Y";


    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /*目标剪切图片文件名*/
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage%s.jpeg";

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;

    /*uCrop destination uri*/
    private Uri mDestinationUri;

    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;
    private boolean mIsCut;
    private boolean mIsSquare;/* 是否以1:1的比例剪裁图片 */
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagemodule_activity_default);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        mIsCut = intent.getBooleanExtra(EXTRA_CUT, false);
        if (mMode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mMode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this,
                        MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFailListener();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // 完成按钮
        mSubmitButton = (Button) findViewById(R.id.commit);
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText(this.getString(R.string.done));
            mSubmitButton.setEnabled(false);
        } else {
            mSubmitButton.setText(this.getString(R.string.done) + "(" + resultList.size() + "/" +
                    mDefaultCount + ")");
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSelect();
            }
        });

        long lMillis = DateTime.now().getMillis();
        String lFormat = String.format(SAMPLE_CROPPED_IMAGE_NAME, lMillis);
        mDestinationUri = Uri.fromFile(new File(getCacheDir(), lFormat));

    }

    @Override
    public void onSingleImageSelected(String path) {
        onImgBack(path);
    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }

        /* 旋转图片 */
        doRotateImageAndSave(path);

        // 有图片之后，改变按钮状态
        if (resultList.size() > 0) {
            mSubmitButton.setText(this.getString(R.string.done) + "(" + resultList.size() + "/" +
                    mDefaultCount + ")");
            if (!mSubmitButton.isEnabled()) {
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
            mSubmitButton.setText(this.getString(R.string.done) + "(" + resultList.size() + "/" +
                    mDefaultCount + ")");
        } else {
            mSubmitButton.setText(this.getString(R.string.done) + "(" + resultList.size() + "/" +
                    mDefaultCount + ")");
        }
        // 当为选择图片时候的状态
        if (resultList.size() == 0) {
            mSubmitButton.setText(this.getString(R.string.done));
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile == null) {
            return;
        }
        final String lAbsolutePath = imageFile.getAbsolutePath();
        doRotateImageAndSave(lAbsolutePath);
        onImgBack(lAbsolutePath);
    }

    private void onImgBack(String absolutePath) {
        resultList.add(absolutePath);
        if (mIsCut) {
//                Intent mIntent = new Intent(this, CropImageUI.class);
//                mIntent.putExtra("bitmap", imageFile.getAbsolutePath());
//                startActivity(mIntent);
//                finish();

            Uri lUri = Uri.parse("file://" + absolutePath);
            UCrop lUCrop = UCrop.of(lUri, mDestinationUri);
            int lX = getIntent().getIntExtra(EXTRA_AspectRatio_X, 1);
            int lY = getIntent().getIntExtra(EXTRA_AspectRatio_Y, 1);
            lUCrop.withAspectRatio(lX, lY);
            lUCrop.start(MultiImageSelectorActivity.this);

        } else {
            confirmSelect();
        }
    }

    private void confirmSelect() {
        if (resultList == null || resultList.size() <= 0) {
            return;
        }

        final ImageModule lInstance = ImageModule.getInstance();
        if (mMode == MODE_SINGLE) {
            try {
                final ImageModule.SingleImgPicker lSingleImgPicker = ImageModule.getInstance()
                        .getSingleImgPicker();
                final ImageModule.SingleImgPicker1 lSingleImgPicker1 = ImageModule.getInstance()
                        .getSingleImgPicker1();
                final String lPath = resultList.get(0);
                final Uri lUri = new Uri.Builder().path(lPath).build();
                if (lSingleImgPicker != null) {
                    lSingleImgPicker.onSingleImgPickSucc(lUri);
                    ImageModule.getInstance().destroySingleImgPicker();
                }
                if (lSingleImgPicker1 != null) {
                    lSingleImgPicker1.onSingleImgPickSucc(lUri);
                    ImageModule.getInstance().destroySingleImgPicker();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            ImageModule.MultiPagerPicker lMultiPagerPicker = lInstance.getMultiPagerPicker();
            if (lMultiPagerPicker != null) {
                lMultiPagerPicker.onImagePickSucc(resultList);
                lInstance.destroyMultiPagerPicker();
            }
        }
        if (!MultiImageSelectorActivity.this.isFinishing()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
//                resultList.add(resultUri.toString());
                final ImageModule.SingleImgPicker lSingleImgPicker = ImageModule.getInstance()
                        .getSingleImgPicker();
                final ImageModule.SingleImgPicker1 lSingleImgPicker1 = ImageModule.getInstance()
                        .getSingleImgPicker1();
                if (lSingleImgPicker == null && lSingleImgPicker1 == null) {
                    L.e(this, "没有实现ImageModule.SingleImgPicker");
                    return;
                }
                if (lSingleImgPicker != null) {
                    lSingleImgPicker.onSingleImgPickSucc(resultUri);
                }
                if (lSingleImgPicker1 != null) {
                    lSingleImgPicker1.onSingleImgPickSucc(resultUri);
                }
                ImageModule.getInstance().destroySingleImgPicker();
                if (!MultiImageSelectorActivity.this.isFinishing()) {
                    finish();
                }
            } else {
                Toast.Short(MultiImageSelectorActivity.this, "Can't retrieve cropped image");
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callFailListener();
    }

    private void callFailListener() {
        final ImageModule.SingleImgPicker1 lSingleImgPicker1 = ImageModule.getInstance()
                .getSingleImgPicker1();
        if (lSingleImgPicker1 != null) {
            lSingleImgPicker1.onSingleImgPickFail();
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    //旋转图片，并保存
    //////////////////////////////////////////////////////////////////////////*/

    private void doRotateImageAndSave(String filePath) {
        doRotateImageAndSaveStrategy2(filePath);
    }

    //通过img得到旋转rotate角度后的bitmap
    public static Bitmap rotateImage(Bitmap img, int rotate) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, false);
        return img;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException ex) {
        }
        if (exifInterface == null)
            return degree;
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface
                .ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    //加载filePath下的图片，旋转并保存到filePath
    private void doRotateImageAndSaveStrategy1(String filePath) {
        int rotate = readPictureDegree(filePath);//获取需要加载图片的旋转角度
        if (rotate == 0) {
            return;
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap destBitmap = rotateImage(bitmap, rotate);
            bitmap.recycle();

            //save to file
            saveImageToSDCard(destBitmap, 100, filePath);
            destBitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private int outWidth = 0;//输出bitmap的宽
    private int outHeight = 0;//输出bitmap的高

    //计算sampleSize
    private int caculateSampleSize(String imgFilePath, int rotate) {
        outWidth = 0;
        outHeight = 0;
        int imgWidth = 0;//原始图片的宽
        int imgHeight = 0;//原始图片的高
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(imgFilePath);
            BitmapFactory.decodeStream(inputStream, null, options);//由于options
            // .inJustDecodeBounds位true，所以这里并没有在内存中解码图片，只是为了得到原始图片的大小
            imgWidth = options.outWidth;
            imgHeight = options.outHeight;
            //初始化
            outWidth = imgWidth;
            outHeight = imgHeight;
            //如果旋转的角度是90的奇数倍,则输出的宽和高和原始宽高调换
            if ((rotate / 90) % 2 != 0) {
                outWidth = imgHeight;
                outHeight = imgWidth;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        //计算输出bitmap的sampleSize
        while (imgWidth / sampleSize > outWidth || imgHeight / sampleSize > outHeight) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    // 加载filePath下的图片，旋转并保存到filePath, 同时对图片进行等比例压缩
    private void doRotateImageAndSaveStrategy2(String filePath) {
        int rotate = readPictureDegree(filePath);
        if (rotate == 0)
            return;
        //得到sampleSize
        int sampleSize = caculateSampleSize(filePath, rotate);
        if (outWidth == 0 || outHeight == 0)
            return;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        //适当调整颜色深度
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            Bitmap srcBitmap = BitmapFactory.decodeStream(inputStream, null, options);//加载原图
            //test
            Bitmap.Config srcConfig = srcBitmap.getConfig();
            int srcMem = srcBitmap.getRowBytes() * srcBitmap.getHeight();//计算bitmap占用的内存大小

            Bitmap destBitmap = rotateImage(srcBitmap, rotate);
            int destMem = srcBitmap.getRowBytes() * srcBitmap.getHeight();
            srcBitmap.recycle();

            //保存bitmap到文件（覆盖原始图片）
            saveImageToSDCard(destBitmap, 100, filePath);
            destBitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 保存方法
     */
    public void saveImageToSDCard(Bitmap bitmap, int compress, String filePath) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            boolean lMkdir = dir.mkdir();
        }
//        String lCurrentDateTimeMills = String.valueOf(System.currentTimeMillis());
//        File file = new File(filePath, lCurrentDateTimeMills + ".png");
        try {
            FileOutputStream fOut = new FileOutputStream(dir);
            bitmap.compress(Bitmap.CompressFormat.PNG, compress, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
