package com.cardapp.utils.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public class HandWrite extends android.support.v7.widget.AppCompatImageView {
    private final Paint mBitmapPaint;
    private final Paint mPaint;

    private Paint paint = null;
    private Bitmap originalBitmap = null;
    private Bitmap new1Bitmap = null;
    private Bitmap new2Bitmap = null;
    private float clickX = 0, clickY = 0;
    private float startX = 0, startY = 0;
    private boolean isMove = true;
    private boolean isClear = false;
    //画笔颜色
    private String mPaintColor = "#ff0000";
    //画笔粗细
    private float strokeWidth = 3.0f;
    private Bitmap mBitmap;
    private Path mPath;
    private float mX;
    private float mY;
    private static final float TOUCH_TOLERANCE = 4;
    private Canvas mCanvas;
    // 保存Path路径的集合,用List集合来模拟栈
    private static List<DrawPath> savePath;
    // 保存Path路径的集合,用List集合来模拟栈,用于下一步
    private final ArrayList<DrawPath> saveNextPath;
    // 保存新Path路径的集合，重繪新的路徑
//    private final ArrayList<DrawPath> mNewSaveNextPath = new ArrayList<>();

    // 记录Path路径的对象
    private DrawPath dp;

    private Bitmap mBitmap1;

    private boolean isEnable;

    public void setPicture(Bitmap bitmap) {
        mBitmap1 = bitmap;
//        mBitmap1 = getResizedBitmap(bitmap, getHeight(), getWidth());
    }

    public void setEditEnable(boolean enable) {
        isEnable = enable;
    }

    public HandWrite(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * 出现Immutable bitmap passed to Canvas constructor错误的原因是如果不用copy的方法，
         * 直接引用会对资源文件进行修改，而android是不允许在代码里修改res文件里的图片
         * BitmapFactory.decodeResource(getResources(), R.drawable.xiao).copy(Bitmap.Config.ARGB_8888, true);
         * 替换
         * BitmapFactory.decodeResource(getResources(), R.drawable.xiao);
         */
//        originalBitmap = BitmapFactory.decodeResource(getResources(),
//                R.drawable.crop1).copy(Bitmap.Config.ARGB_8888, false);
//        new1Bitmap = Bitmap.createBitmap(originalBitmap);
//        mBitmap=new1Bitmap;
        // 保存一次一次绘制出来的图形
//        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor(mPaintColor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
        mPaint.setStrokeWidth(5);// 画笔宽度

        savePath = new ArrayList<DrawPath>();
        saveNextPath = new ArrayList<DrawPath>();

//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.handwrite);
//        isEnable = ta.getBoolean(R.styleable.handwrite_set_enable, false);
    }

    public void next_do() {
        if (savePath.size() < saveNextPath.size()) {
            savePath.add(saveNextPath.get(savePath.size()));
            invalidate();
        }
    }

    private class DrawPath {
        // 路径
        public Path path;
        // 画笔
        public Paint paint;
        public String paintColor;
    }

    /**
     * 清空涂鸦
     */
    public void clear() {
        isClear = true;
        savePath.clear();// 重新设置画布，相当于清空画布
        saveNextPath.clear();// 重新设置画布，相当于清空画布
        invalidate();
    }

    /**
     * 设置粗细
     *
     * @param strokeWidth
     */
    public void setstyle(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Bitmap getBitmap() {
//        originalBitmap = BitmapFactory.decodeResource(getResources(),
//                mBitmap1).copy(Bitmap.Config.ARGB_8888, true);
//        new1Bitmap = Bitmap.createBitmap(originalBitmap);
//        Bitmap lBitmap = new1Bitmap;
        originalBitmap = Bitmap.createBitmap(mBitmap1).copy(Bitmap.Config.ARGB_8888, true);
        new1Bitmap = Bitmap.createBitmap(originalBitmap);
        Bitmap lBitmap = new1Bitmap;
        // 保存一次一次绘制出来的图形
        mCanvas = new Canvas(lBitmap);
        if (savePath != null && savePath.size() > 0) {
            Iterator<DrawPath> iter = savePath.iterator();
            while (iter.hasNext()) {
                DrawPath drawPath = iter.next();
                DrawPath lDrawPath = new DrawPath();
                lDrawPath.paint = drawPath.paint;
                lDrawPath.path = drawPath.path;
                lDrawPath.paintColor = drawPath.paintColor;
                lDrawPath.paint.setColor(Color.parseColor(drawPath.paintColor));
                lDrawPath.path.offset(-getLeftPoint(), -(getTopPoint()));
                mCanvas.drawPath(lDrawPath.path, lDrawPath.paint);
            }
        }
        return lBitmap;
    }

    private int getLeftPoint() {
        int lWidth = getWidth();
        int lWidth1 = mBitmap1.getWidth();
        return lWidth / 2 - lWidth1 / 2;

    }


    private int getTopPoint() {
        int lHeight = getHeight();
        int lHeight1 = mBitmap1.getHeight();
        return lHeight / 2 - lHeight1 / 2;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scale;
        if (width > height) {
            scale = ((float) newWidth) / width;
        } else {
            scale = ((float) newHeight) / height;
        }
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scale, scale);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap1 = getResizedBitmap(mBitmap1, getHeight(), getWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 将前面已经画过得显示出来
        int lLeftPoint = getLeftPoint();
        int lTopPoint = getTopPoint();
        canvas.drawBitmap(mBitmap1, lLeftPoint, lTopPoint, mBitmapPaint);
        if (mPath != null) {
            // 实时的显示
            mPaint.setColor(Color.parseColor(mPaintColor));
            canvas.drawPath(mPath, mPaint);
        }
        if (savePath != null && savePath.size() > 0) {
            for (DrawPath drawPath : savePath) {
                drawPath.paint.setColor(Color.parseColor(drawPath.paintColor));
                canvas.drawPath(drawPath.path, drawPath.paint);
            }
        } else if (isClear) {
            isClear = false;
            //  2016/8/22 by woode 修复《》No.:  原图需要处理，脱离项目
            originalBitmap = BitmapFactory.decodeResource(getResources(), me.nereo.multi_image_selector.R.drawable.default_error).copy
                    (Bitmap
                    .Config
                    .ARGB_8888, true);
            new2Bitmap = Bitmap.createBitmap(originalBitmap);
//            mCanvas.setBitmap(new2Bitmap);

//            mBitmap=new2Bitmap;
            invalidate();
        }
    }

//    public Bitmap HandWriting(Bitmap originalBitmap){
//        Canvas canvas = null;
//
//        if(isClear){
//            canvas = new Canvas(new2Bitmap);
//        }
//        else{
//            canvas = new Canvas(originalBitmap);
//        }
//        paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setAntiAlias(true);
//        paint.setColor(mPaintColor);
//        paint.setStrokeWidth(strokeWidth);
//        if(isMove){
//            canvas.drawLine(startX, startY, clickX, clickY, paint);
//        }
//
//        startX = clickX;
//        startY = clickY;
//
//        if(isClear){
//            return new2Bitmap;
//        }
//        return originalBitmap;
//    }

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
//        触摸间隔大于阈值才绘制路径
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也是可以的)
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
//        mCanvas.drawPath(mPath, mPaint);
        //将一条完整的路径保存下来(相当于入栈操作)
        savePath.add(dp);
        saveNextPath.add(dp);
        mPath = null;// 重新置空
    }

    /**
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
//        mCanvas.drawBitmap(new1Bitmap, 0, 0, mBitmapPaint);
//        mCanvas.setBitmap(originalBitmap);// 重新设置画布，相当于清空画布
        // 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉...
        if (savePath != null && savePath.size() > 0) {
            // 移除最后一个path,相当于出栈操作
            savePath.remove(savePath.size() - 1);
//            Iterator<DrawPath> iter = savePath.iterator();
//            while (iter.hasNext()) {
//                DrawPath drawPath = iter.next();
//                mCanvas.drawPath(drawPath.path, drawPath.paint);
//            }
            invalidate();// 刷新

   /*在这里保存图片纯粹是为了方便,保存图片进行验证*/
//            String fileUrl = Environment.getExternalStorageDirectory()
//                    .toString() + "/android/data/test.png";
//            try {
//                FileOutputStream fos = new FileOutputStream(new File(fileUrl));
//                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.flush();
//                fos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    /**
     * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)，
     * 然后从redo的集合里面取出最顶端对象，
     * 画在画布上面即可。
     */
    public void redo() {
        //如果撤销你懂了的话，那就试试重做吧。
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnable) {
            clickX = event.getX();
            clickY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 每次down下去重新new一个Path
                    mPath = new Path();
                    //每一次记录的路径对象是不一样的
                    dp = new DrawPath();
                    dp.path = mPath;
                    dp.paint = mPaint;
                    dp.paintColor = mPaintColor;
                    touch_start(clickX, clickY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(clickX, clickY);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    break;
                default:
                    break;

            }
            invalidate();
        }
        return true;
    }

    public String getPaintColor() {
        return mPaintColor;
    }

    public void setPaintColorAsYellow() {
        setPaintColor("#FFFF00");
    }

    public void setPaintColorAsGreen() {
        setPaintColor("#00FF00");

    }

    public void setPaintColorAsRed() {
        setPaintColor("#FF0000");

    }

    public void setPaintColor(String paintColor) {
        mPaintColor = paintColor;
        mPaint.setColor(Color.parseColor(mPaintColor));
    }
}
