package com.fransis.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * Created by francisco on 03/10/2016.
 */
public class Imagen {
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String rotateCss(int orientation) {

        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return "transform: rotate(0deg);";
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return "transform: scale(-1, 1);";
            case ExifInterface.ORIENTATION_ROTATE_180:
                return "transform: rotate(180deg);";
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                //matrix.setRotate(180);
                //matrix.postScale(-1, 1);
                return "transform: rotate(180deg) scale(-1, 1);";
            case ExifInterface.ORIENTATION_TRANSPOSE:
                return "transform: rotate(90deg) scale(-1, 1);";
                //matrix.setRotate(90);
                //matrix.postScale(-1, 1);
            case ExifInterface.ORIENTATION_ROTATE_90:
                //matrix.setRotate(90);
                return "transform: rotate(90deg);";
            case ExifInterface.ORIENTATION_TRANSVERSE:
                return "transform: rotate(270deg) scale(-1, 1);";
                //matrix.setRotate(-90);
                //matrix.postScale(-1, 1);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return "transform: rotate(270deg);";
                //matrix.setRotate(-90);
            default:
                return "";
        }
    }

    public static String rotateCssPHtml(int orientation, int width, int height) {

        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return "width:"+width+"px; height: "+height+"px;";
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return "width:"+width+"px; height: "+height+"px;";
            case ExifInterface.ORIENTATION_ROTATE_180:
                return "width:"+width+"px; height: "+height+"px;";
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return "width:"+width+"px; height: "+height+"px;";
            case ExifInterface.ORIENTATION_TRANSPOSE:
                return "height:"+width+"px; width: "+height+"px;";
            case ExifInterface.ORIENTATION_ROTATE_90:
                return "height:"+width+"px; width: "+height+"px;";
            case ExifInterface.ORIENTATION_TRANSVERSE:
                return "height:"+width+"px; width: "+height+"px;";
            case ExifInterface.ORIENTATION_ROTATE_270:
                return "height:"+width+"px; width: "+height+"px;";
            default:
                return "";
        }
    }


}
