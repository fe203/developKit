package com.lyne.fw.file;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.TextUtils;

import com.lyne.fw.log.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtils {
    public static final long KB=1024;
    public static final long MB=KB*KB;

    public static final String FILE_URL_SUFFIX = "file://";

    public static final String TMP_SUFFIX = ".tmp";
    public static final String APK_SUFFIX = ".apk";
    public static final String JPG_SUFFIX = ".jpg";
    public static final String ZIP_SUFFIX = ".zip";
    public static final String VIDEO_SUFFIX = ".mp4";
    public static final String AUDIO_SUFFIX = ".aac";

    //cathe dir  ./appName/cathe
    private static final String DIR_JU_CACHE = "cache";
    //app dir    ./appName/data
    private static final String DIR_JU_DATA = "data";

    //child dir
    public static final String DIR_IMAGES = "Images";
    public static final String DIR_VIDEO = "Video";
    public static final String DIR_AUDIO = "Audio";
    public static final String DIR_APK = "Apk";
    public static final String DIR_IM = "IM";

    private static String dirName = null;

    /**
     * 获取文件Url
     * @param path
     * @return
     */
    public static String getFileUrl(String path) {
        if (path != null && path.startsWith(FILE_URL_SUFFIX)){
            return path;
        }
        return FILE_URL_SUFFIX + path;
    }

    /**
     * 获取文件path
     * @param fileUrl
     * @return
     */
    public static String getFilePath(String fileUrl){
        if(fileUrl == null){
            return null;
        }
        if(fileUrl.startsWith(FILE_URL_SUFFIX)){
            return fileUrl.substring(FILE_URL_SUFFIX.length());
        }
        return fileUrl;
    }

    /**
     * 获取app data 的子目录
     * @param context
     * @param dirName
     * @return
     */
    public static File getAppChildDir(Context context, String dirName) {
        File dir = new File(getAppDir(context), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取 app cathe 的子目录
     * @param context
     * @param dirName
     * @return
     */
    public static File getCacheChildDir(Context context, String dirName) {
        File dir = new File(getCacheDir(context), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static String getAppName(Context context){

        if (TextUtils.isEmpty(dirName)){
            dirName = getAppMetaDataName(context, "storage_dir_name");
        }

        if (TextUtils.isEmpty(dirName)){
            dirName = context.getPackageName();
        }

        return dirName;
    }

    private static String getAppMetaDataName(Context context, String metaName) {
        try {
            String value = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString(metaName);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static File getAppDir(Context context) {
        String state = Environment.getExternalStorageState();
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = context.getExternalFilesDir(null);
        } else {
            dir = context.getFilesDir();
        }
        if(dir == null){
            dir = getStorageChildDir(context, DIR_JU_DATA);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getCacheDir(Context context) {
        String state = Environment.getExternalStorageState();
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = context.getExternalCacheDir();
        } else {
            dir = context.getCacheDir();
        }
        if(dir == null){
            dir = getStorageChildDir(context, DIR_JU_CACHE);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getRootDir(Context context){
        String state = Environment.getExternalStorageState();
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            if(externalStorageDirectory == null){
                externalStorageDirectory = Environment.getRootDirectory();
            }
            dir = new File(externalStorageDirectory, getAppName(context));
        } else {
            dir = new File(Environment.getRootDirectory(), getAppName(context));
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取用于存储的dir
     * @param context
     * @param dirName
     * @return
     */
    private static File getStorageChildDir(Context context, String dirName) {
        File dir = new File(getRootDir(context), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getStorageDir(Context context){
        File dir = new File(getRootDir(context), getAppName(context));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static boolean isExist(File file){
        return file!=null && file.exists();
    }

    public static boolean isFileExists(String path) {

        if (TextUtils.isEmpty(path)){
            return false;
        }

        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static boolean isLocalFile(String url){
        if(url == null) return false;
        return url.startsWith(FILE_URL_SUFFIX);
    }

    public static File getFileByUri(Context context, Uri uri) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns._ID, Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            LogUtils.print(context.getClass(), "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    public static void fileChannelCopy(File fromFile, File toFile) {
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(fromFile);
            fo = new FileOutputStream(toFile);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static ArrayList<File> getChildFilesByDir(File dir){
        ArrayList<File> result=new ArrayList<>();
        if(dir==null||!dir.exists()){
            return  result;
        }
        File[] childs= dir.listFiles();
        for(File file:childs){
            if(file.isDirectory()){
                result.addAll(getChildFilesByDir(file));
            }else {
                result.add(file);
            }
        }
        return result;
    }


    public static long getCacheSize(Context context) {
        return getFileSize(getCacheDir(context));
    }

    public static long getFileSize(File fileDir){
        long totalSize = 0;
        if (fileDir != null && fileDir.exists()){

            ArrayList<File> files = getChildFilesByDir(fileDir);
            for (File file : files){
                totalSize += file.length();
            }

        }
        return totalSize;
    }

    public static void clearCathe(Context context){
        deleteFile(getCacheDir(context));
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File fileTmp : files) {
                    deleteFile(fileTmp);
                }
                // file.delete();
                deleteRenameFile(file);
            } else {
                // file.delete();
                deleteRenameFile(file);
            }
        }
    }

    private static void deleteRenameFile(File file) {
        File fileTo = new File(file.getAbsolutePath() + System.currentTimeMillis());
        file.renameTo(fileTo);
        fileTo.delete();
    }
}
