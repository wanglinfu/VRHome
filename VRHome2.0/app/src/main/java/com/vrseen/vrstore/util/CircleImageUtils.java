package com.vrseen.vrstore.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 项目名称：VRHome2.0
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/6/15 16:03
 * 修改人：Administrator
 * 修改时间：2016/6/15 16:03
 * 修改备注：
 */
public class CircleImageUtils {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();

        //保证是方形，并且从中心画

        int width = bitmap.getWidth();

        int height = bitmap.getHeight();

        int w;

        int deltaX = 0;

        int deltaY = 0;

        if (width <= height) {

            w = width;

            deltaY = height - w;

        } else {

            w = height;

            deltaX = width - w;

        }

        final Rect rect = new Rect(deltaX, deltaY, w, w);

        final RectF rectF = new RectF(rect);


        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        //圆形，所有只用一个

        int radius = (int) (Math.sqrt(w * w * 2.0d) / 2);

        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
