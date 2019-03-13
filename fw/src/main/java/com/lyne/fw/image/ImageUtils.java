package com.lyne.fw.image;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.text.TextUtils;

import com.lyne.fw.file.FileUtils;
import com.lyne.fw.log.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class ImageUtils {

    public static void saveBitmap(Context context, final Bitmap bitmap, final String filename, boolean toGallery) throws Exception{
        if(TextUtils.isEmpty(filename)){
            return;
        }

        File file = new File(toGallery ? FileUtils.getStorageDir(context) : FileUtils.getCacheChildDir(context, FileUtils.DIR_IMAGES), filename);

        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        if(bitmap.compress(CompressFormat.JPEG, 90, out)){
            out.flush();
            out.close();
        }

        if(toGallery){
            //把文件插入到系统图库
            notifyScanFile(context, file);
        }
    }


    /**
     * Insert an image and create a thumbnail for it.
     *
     * @param cr The content resolver to use
     * @param source The stream to use for the image
     * @param title The name of the image
     * @param description The description of the image
     * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
     *              for any reason.
     */
    public static final String insertImage(ContentResolver cr, Bitmap source,
                                           String title, String description) {
        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DESCRIPTION, description);
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(Images.Media.MIME_TYPE, "image/jpeg");

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(CompressFormat.JPEG, 90, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id,
                        Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                StoreThumbnail(cr, miniThumb, id, 50F, 50F, Images.Thumbnails.MICRO_KIND);
            } else {
                LogUtils.print(null, "insertImage---Failed to create thumbnail, removing original");
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            LogUtils.print(null, "insertImage---Failed to insert image");
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    /**
     * 扫描数据库，将图片同步到媒体中
     * @param context
     * @param file
     */
    public static void notifyScanFile(Context context, File file){
        if (file == null){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    public static void notifyScanFile(Context context, String filePath){
        if (TextUtils.isEmpty(filePath)){
            return;
        }
        notifyScanFile(context, new File(filePath));
    }

    private static final Bitmap StoreThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width, float height,
            int kind) {
        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true);

        ContentValues values = new ContentValues(4);
        values.put(Images.Thumbnails.KIND,     kind);
        values.put(Images.Thumbnails.IMAGE_ID, (int)id);
        values.put(Images.Thumbnails.HEIGHT,   thumb.getHeight());
        values.put(Images.Thumbnails.WIDTH,    thumb.getWidth());

        Uri url = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);

            thumb.compress(CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        }
        catch (FileNotFoundException ex) {
            return null;
        }
        catch (IOException ex) {
            return null;
        }
    }

    public static byte[] toByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 90, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static void compressBitmapToFile(Bitmap srcBitmap, int quality, int maxSide, int angle, File dstFile){
        compressBitmapToFile(srcBitmap, quality, maxSide, angle, dstFile, false);
    }
    public static void compressBitmapToFile(Bitmap src, int quality, File dstFile){
        if(src == null || dstFile == null){
            return;
        }

        //质量压缩
        try {
            if(!dstFile.exists()){
                dstFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            src.compress(CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存图片到指定路径,按目标文件大小进行压缩，压缩到文件小于期望值
     * Save image with specified size
     *
     * @param filePath the image file save path 储存路径
     * @param bitmap   the image what be save   目标图片
     * @param size     the file size of image   期望大小 单位bite
     */
    public static void saveImage(String filePath, Bitmap bitmap, int quality, long size) {

        File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));

        if (!result.exists() && !result.mkdirs()) return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = quality;
        bitmap.compress(CompressFormat.JPEG, options, stream);

        while (stream.toByteArray().length > size) {
            options -= 6;
            if(options<0){
                break;
            }
            stream.reset();
            bitmap.compress(CompressFormat.JPEG, options, stream);
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * @param srcBitmap
     * @param quality
     * @param maxSide 长或宽的最大值
     * @return
     */
    public static void compressBitmapToFile(Bitmap srcBitmap, int quality, int maxSide, int angle, File dstFile, boolean needBlur){
        if(srcBitmap == null || dstFile == null){
            return;
        }
        //尺寸压缩
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        Bitmap scaledBitmap;
        if(maxSide < 0 || (srcWidth <= maxSide && srcHeight <= maxSide)){
            scaledBitmap = srcBitmap;
        }else{
            int dstWidth;
            int dstHeight;
            if(srcWidth > srcHeight){
                dstWidth = maxSide;
                dstHeight = dstWidth * srcHeight / srcWidth;
            }else{
                dstHeight = maxSide;
                dstWidth = dstHeight * srcWidth / srcHeight;
            }
            scaledBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, true);
        }
        if(needBlur){
            scaledBitmap = FastBlur.doBlur(scaledBitmap, 15, false);
        }
        //图片旋转
        Bitmap rotatedBitmap = null;
        if (scaledBitmap != null){
            rotatedBitmap = rotate(angle, scaledBitmap);
        }
        if(scaledBitmap != null && scaledBitmap != rotatedBitmap && scaledBitmap != srcBitmap  && !scaledBitmap.isRecycled()){
            scaledBitmap.recycle();
            scaledBitmap = null;
        }
        //质量压缩
        try {
            if(!dstFile.exists()){
                dstFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            rotatedBitmap.compress(CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(rotatedBitmap != null && rotatedBitmap != srcBitmap && !rotatedBitmap.isRecycled()){
            rotatedBitmap.recycle();
            rotatedBitmap = null;
        }
    }

    /**

     * @param bitmap      原图
     * @param edgeLength  希望得到的正方形部分的边长
     * @return  缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
        if(null == bitmap || edgeLength <= 0)
        {
            return  null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if(widthOrg > edgeLength && heightOrg > edgeLength)
        {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try{
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            }
            catch(Exception e){
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try{
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            }
            catch(Exception e){
                return null;
            }
        }

        return result;
    }

    /**
     *
     * @param bitmap 图片源
     * @param needRecycle 是否回收bitmap
     * @param length 图片限制大小，单位为k
     * @return
     */
    public static byte[] toByteArrayInSize(Bitmap bitmap, boolean needRecycle, int length){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length > length * 1024) {  //循环判断如果压缩后图片是否大于length kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        if (needRecycle) {
            bitmap.recycle();
        }
        return baos.toByteArray();
    }

    public static String compressBitmapToTempPath(Context context, String path, int quality, int maxSide, int angle){
        File dstFile = new File(FileUtils.getCacheChildDir(context, FileUtils.DIR_IMAGES), "temp_" + System.currentTimeMillis() + FileUtils.JPG_SUFFIX);
        if(!dstFile.exists()){
            try {
                dstFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = getBitmapFromFile(new File(path), maxSide);
        compressBitmapToFile(bitmap, quality, maxSide, angle, dstFile);
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return dstFile.getPath();
    }

    public static String compressBitmapToPath(Context context, String srcPath, int quality, int maxSide, int angle){
        File dstFile = new File(FileUtils.getCacheChildDir(context, FileUtils.DIR_IMAGES), "chat_send_" + System.currentTimeMillis() + FileUtils.JPG_SUFFIX);
        if(!dstFile.exists()){
            try {
                dstFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = getBitmapFromFile(new File(srcPath), maxSide);
        compressBitmapToFile(bitmap, quality, maxSide, angle, dstFile);
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return dstFile.getPath();
    }

    public static String compressBitmapToPath(Context context, String srcPath, String dstPath, int quality, int maxSide, int angle){
        File dstFile = new File(dstPath);
        if(!dstFile.exists()){
            try {
                dstFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = getBitmapFromFile(new File(srcPath), maxSide);
        compressBitmapToFile(bitmap, quality, maxSide, angle, dstFile);
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return dstFile.getPath();
    }

    /**
     *
     * @param dst
     * @param dstMaxSide 用于计算SampleSize，为保证图片质量，实际的图片最大边可能大于dstMaxSide
     * @return
     */
    public static Bitmap getBitmapFromFile(File dst, int dstMaxSide) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (dstMaxSide > 0) {
                opts = new BitmapFactory.Options();         //设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例

                opts.inSampleSize = Math.max(opts.outHeight, opts.outWidth) / dstMaxSide;
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
                opts.inDither = false;
                opts.inPreferredConfig = Config.ARGB_8888;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     *如果图片的长宽比大于2，且图片的长边大于2倍dstMaxSide，则认为该图是长图，不decode
     * @param dst
     * @param dstMaxSide 用于计算SampleSize，为保证图片质量，实际的图片最大边可能大于dstMaxSide
     * @return
     */
    public static Bitmap getLongBitmapFromFile(File dst, int dstMaxSide) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            int inSampleSize=1;
            if (dstMaxSide > 0) {
                opts = new BitmapFactory.Options();         //设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                int w=opts.outWidth;
                int h=opts.outHeight;
                if(w*2<h&&h>dstMaxSide*2){
                    //认为是长图
                    //当长图的短边大于dstMaxSide，要做等比缩放
                    if(w>dstMaxSide){
                        inSampleSize = w / dstMaxSide;
                        return decode(dst.getPath(),inSampleSize);
                    }else{
                        //短边小于maxSize时，不需要缩放
                        return null;
                    }
                }
                inSampleSize = Math.max(opts.outHeight, opts.outWidth) / dstMaxSide;
            }
            try {
                return decode(dst.getPath(),inSampleSize);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static Bitmap decode(String path, int inSampleSize){
        BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        opts.inDither = false;
        opts.inPreferredConfig = Config.ARGB_8888;
        try {
            return  BitmapFactory.decodeFile(path,opts);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readImageDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle 180和270时还需要进行一次水平翻转
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotate(int angle , Bitmap bitmap) {
        if (bitmap == null){
            return null;
        }

        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        if (angle > 90){
            matrix.postScale(-1, 1);
        }
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 图片翻转
     * @param v true-垂直翻转 false-水平翻转
     * @return
     */
    public static Bitmap flip(boolean v, Bitmap bitmap){
        if (bitmap == null){
            return null;
        }

        //旋转图片 动作
        Matrix matrix = new Matrix();
        if (v){
            matrix.postScale(1, -1);
        }else {
            matrix.postScale(-1, 1);
        }
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     * @param context
     * @param resId
     * @param config
     * @return
     */
    public static Bitmap readBitmap(Context context, int resId, Config config, int dstWidth, int dstHeight){
        BitmapFactory.Options opt = new BitmapFactory.Options();

        //解析图片宽高
        opt.inJustDecodeBounds = true;
        InputStream is = context.getResources().openRawResource(resId);
        BitmapFactory.decodeStream(is, null, opt);

        //设置DecodingOpts
        opt.inSampleSize = computeSampleSize(opt, -1, dstWidth * dstHeight);
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = config;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is2 = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is2,null,opt);
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else{
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static void fetchShareThumb(final Context context, final String imageUrl, final int defaultResId, final OnFetchThumbListener listener){

        new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap defaultThumb = BitmapFactory.decodeResource(context.getResources(), defaultResId);
                if(TextUtils.isEmpty(imageUrl)){
                    if(listener != null){
                        listener.onFetchThumbFinished(defaultThumb);
                    }
                    return;
                }
                Bitmap thumb = null;
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
                    if(bitmap != null){
                        thumb = ImageUtils.centerSquareScaleBitmap(bitmap, 120);
                        if(bitmap != thumb){
                            bitmap.recycle();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(listener != null){
                    listener.onFetchThumbFinished(thumb == null? defaultThumb : thumb);
                }
            }
        }).start();

    }

    public static String compressBitmapToPath(Context context, String srcPath, int angle){
        File dstFile = new File(FileUtils.getCacheChildDir(context, FileUtils.DIR_IMAGES), "chat_send_" + System.currentTimeMillis() + FileUtils.JPG_SUFFIX);
        return compressBitmapToPath(srcPath, dstFile, angle);
    }

    /**  edit by tiny on 2017/3/24 17:36 * */
    public static String compressBitmapToPath(String srcPath, File dstFile, int angle){

       return compressBitmapToPath(srcPath,dstFile,angle, CompressFormat.JPEG);
    }
    public static String compressBitmapToPath(String srcPath, File dstFile, int angle, CompressFormat format){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);


        if(angle == 90 || angle == 270){
            if(options.outHeight > 800){
                options.inSampleSize = options.outHeight / 800;
            }
        }else{
            if(options.outWidth > 800){
                options.inSampleSize = options.outWidth / 800;
            }
        }

        options.inJustDecodeBounds = false;
        options.inInputShareable = true;
        options.inPurgeable = true;
        options.inDither = false;
        options.inPreferredConfig = Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);

        if(angle == 90 || angle == 270){
            if(bitmap.getHeight() > 800) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 800 / bitmap.getHeight(), 800, true);
            }
        }else{
            if(bitmap.getWidth() > 800) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, bitmap.getHeight() * 800 / bitmap.getWidth(), true);
            }
        }

        Bitmap rotatedBitmap = rotate(angle, bitmap);
        if(bitmap != null && bitmap != rotatedBitmap && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }

        try {
            if(!dstFile.exists()) {
                dstFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            /**  edit by tiny on 2017/3/24 17:34 * */
            rotatedBitmap.compress(format, 90, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(rotatedBitmap != null && !rotatedBitmap.isRecycled()){
            rotatedBitmap.recycle();
            rotatedBitmap = null;
        }

        return dstFile.getPath();
    }


    public interface OnFetchThumbListener{
        public void onFetchThumbFinished(Bitmap thumb);
    }

}
