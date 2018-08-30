package com.lyne.utils.download;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class DownloadUtil {

    private static DownloadUtil downloadUtil;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {

    }

    /**
     * @param url 下载连接
     * @param dir 储存下载文件的SDCard目录
     * @param fileName 文件名
     * @param listener 下载监听
     */
    public void download(final String url, File dir, final String fileName, final OnDownloadListener listener) {

        File dstFile = null;
        if (fileName != null && !fileName.isEmpty()) {
            dstFile = new File(dir, fileName);
            if (dstFile.exists()) {
                listener.onDownloadSuccess(dstFile);
                return;
            }
        }else {
            dstFile = new File(dir, getNameFromUrl(url));
        }

        InputStream is = null;
        OutputStream fos = null;

        try {
            URL uri = new URL(url);
            URLConnection conn = uri.openConnection();

            is = conn.getInputStream();
            long total = conn.getContentLength();
            fos = new FileOutputStream(dstFile);
            long sum = 0;
            byte[] buf = new byte[2048];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
                // 下载中
                if (listener != null) {
                    listener.onDownloading(progress);
                }
            }
            fos.flush();
            // 下载完成
            if (listener != null){
                listener.onDownloadSuccess(dstFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null){
                listener.onDownloadFailed();
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {

            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {

            }
        }

        if (listener != null){
            listener.onDownloadStart();
        }
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();


        /**
         * 下载开始
         */
        void onDownloadStart();
    }
}
