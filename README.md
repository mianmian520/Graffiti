项目地址：https://github.com/mianmian520/Graffiti.git
# Graffiti 涂鸦

###主要功能
* 设置画笔的颜色
目前有黑色，绿色，蓝色，红色和黄色这几种选择
* 设置画笔大小
可以随意的调节画笔的大小
* 撤销，恢复上一步，清除所有
* 保存到本地

###使用
    /**
     * 触摸屏幕时操作
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
