package com.boge.graffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author boge
 * @version 1.0
 * @date 2016/11/24
 */

public class GraffitiView extends View {

    /***画笔*/
    private Paint mPaint;
    /***路径*/
    private Path mPath;

    private Bitmap mBitmap;
    /***画板*/
    private Canvas mCanvas;

    /***宽高*/
    private int widht, height;

    /***记录点的位置*/
    private float currentX, currentY;

    /***保存Path路径集合*/
    private List<DrawPath> savePath;

    /***撤销Path路径集合*/
    private List<DrawPath> deletePath;

    // 记录Path路径的对象
    private DrawPath dp;

    /***画笔颜色*/
    private int paintColor = Color.BLACK;

    /***背景颜色*/
    private int backColor = Color.WHITE;

    /***画笔大小*/
    private int size = 5;

    /***是否是橡皮擦*/
    private boolean isEraser = false;

    public GraffitiView(Context context,  int screenWidth, int screenHeight) {
        super(context);
        widht = screenWidth;
        height = screenHeight;
        init();
        setPaint();
        savePath = new ArrayList<DrawPath>( );
        deletePath = new ArrayList<DrawPath>();
    }

    private void init() {
        mBitmap = Bitmap.createBitmap(widht, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(backColor);
    }

    /**
     * 设置画板
     */
    private void setPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 去除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        if(isEraser){
            mPaint.setAlpha(0);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(50);
        } else {
            mPaint.setStrokeWidth(size);
            mPaint.setColor(paintColor);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        if(mPath != null){
            canvas.drawPath(mPath, mPaint);
        }
    }

    /**
     * 触摸屏幕是操作
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://按下
                mPath = new Path();
                dp = new DrawPath();
                dp.path = mPath;
                dp.paint = mPaint;
                currentX = x;
                currentY = y;
                mPath.moveTo(currentX, currentY);
                break;
            case MotionEvent.ACTION_MOVE://移动
                //画线
                mPath.quadTo(currentX, currentY, (currentX + x)/2, (currentY + y)/2);
                currentX = x;
                currentY = y;
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(dp.path, dp.paint);
                savePath.add(dp);
                dp = null;
                mPath = null;
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 设置颜色
     * @param paintColor
     */
    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
        setPaint();
    }

    /**
     * 设置大小
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
        setPaint();
    }

    /**
     * 撤销
     */
    public void undo(){
        if(savePath != null && savePath.size() > 0){
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(0,drawPath);
            savePath.remove(savePath.size()-1);
        }
        reDraw();
    }

    /***
     * 恢复上一步
     */
    public void recover(){
        if(deletePath != null && deletePath.size() > 0){
            DrawPath drawPath = deletePath.get(0);
            savePath.add(drawPath);
            deletePath.remove(0);
        }
        reDraw();
    }
    /**
     * 清除画板
     */
    public void clear(){
        if(savePath != null && savePath.size() > 0){
            savePath.clear();
        }
        if(deletePath != null && deletePath.size() > 0){
            deletePath.clear();
        }
        reDraw();
    }

    /**
     * 重新加载画板
     */
    private void reDraw(){
        init();
        for (DrawPath drawPath : savePath){
            mCanvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();
    }

    /****
     * 是否是橡皮擦
     * @param isEraser   true是
     */
    public void setEraser(boolean isEraser){
        this.isEraser = isEraser;
        setPaint();
    }

    /***
     * 保存图片到本地
     */
    public void saveBitmap(){

        String path = Environment.getExternalStorageDirectory() + File.separator + "graffiti/";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".png";
        path += date;
        File file1 = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file1);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class DrawPath{
        public Path path;
        public Paint paint;
    }
}
