package com.ai.dragdetaillayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * <b>Project:</b> DragDetailLayout <br>
 * <b>Create Date:</b> 2016/12/18 <br>
 * <b>Author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 工具类 <br>
 */
public class ViewUtil {

    /**
     * 为view设置背景
     *
     * @param view     view
     * @param drawable 背景
     */
    public static void setViewBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 从指定的id获取drawable
     *
     * @param context context
     * @param id      id
     * @return Drawable
     */
    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    /**
     * 从指定的id获取drawable
     *
     * @param context context
     * @param id      id
     * @return Drawable
     */
    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

}
