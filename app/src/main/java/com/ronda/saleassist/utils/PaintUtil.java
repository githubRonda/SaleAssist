package com.ronda.saleassist.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.ImageView;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/12/06
 * Version: v1.0
 */

public class PaintUtil {

    private PaintUtil(){}

    /**
     * 画主界面中的折扣
     * @param text
     * @param img
     */
    public static void paintText(String text, ImageView img){
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
//        canvas.scale(0.5f, 0.5f);
        Paint paint = new Paint();
        paint.setTextSize(16);
        paint.setStyle(Paint.Style.FILL);

        // 画红色的三角形
        paint.setColor(Color.RED);
        Path path = new Path();
        path.moveTo(50,0);
        path.lineTo(100, 0);
        path.lineTo(100,50);
        path.close();
        canvas.drawPath(path,paint);

        //画文字
        paint.setColor(Color.YELLOW);
        canvas.drawText(text, 68, 18, paint); //第二次参数表示text的起始点的x坐标，第三个参数表示text的baseline的y坐标

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        img.setImageBitmap(bitmap);
    }


    /**
     * 画一个圆形，用于标识这种货物是按份计算的（左上角）
     * @param img
     */
    public static void paintCircle(ImageView img){
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();//设置一个笔刷大小是3的黄色的画笔

        // 画红色的三角形（右上角）
        paint.setColor(Color.RED);
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(50, 0);
        path.lineTo(0, 50);
        path.close();
        canvas.drawPath(path, paint);

        //画一个圆形
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(15, 15, 10, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        img.setImageBitmap(bitmap);
    }
}
