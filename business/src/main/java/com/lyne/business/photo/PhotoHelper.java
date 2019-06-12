package com.lyne.business.photo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.lyne.business.actionSheet.ActionItem;
import com.lyne.business.actionSheet.ActionSheetDialog;
import com.lyne.fw.file.FileUtils;
import com.lyne.fw.image.ImageUtils;
import com.lyne.fw.log.LogUtils;
import com.lyne.fw.permission.PermissionEnum;
import com.lyne.fw.permission.PermissionManager;
import com.lyne.utils.algorithm.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/5/18.
 */

public class PhotoHelper implements PermissionManager.OnPermissionRequestListener {
    private static final int REQ_CODE_TAKE_PHOTO = 1001;
    private static final int REQ_CODE_PICK_PHOTO = 1002;
    private static final int REQ_CODE_CUT_PHOTO = 1003;
    private static final int IMAGE_MAX_SIDE = 800;
    private static final int IMAGE_CUT_SIZE = 640;
    private static final int IMAGE_QUALITY = 80;
    private final String IMAGE_FILE_SUFFIX = "images_";
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private String takePhotoPath;
    private String cutPhotoPath;

    //外部可配置
    private int imgQuality = IMAGE_QUALITY;
    private int imgMaxSide = IMAGE_MAX_SIDE;
    private int imgCutSize = IMAGE_CUT_SIZE;
    private boolean needCrop = false;
    private int tag = 0;
    private int maxPicSize = 1;
    private int selectedSize = 0;
    private int ratioX = 1, ratioY = 1; //宽与高之比
    private boolean supplyTake = true;
    private boolean supplyPick = true;
    private String take = "拍照";
    private String pick = "相册";

    private List<String> photoList = new ArrayList<>();
    private List<String> needCropPathList = new ArrayList<>();
    private int tempCropPosition;

    private ResultListener resultListener;
    private Activity activity;

    public PhotoHelper(Activity activity, ResultListener resultListener){
        this.activity = activity;
        this.resultListener = resultListener;
    }

    public void setImgQuality(int imgQuality) {
        this.imgQuality = imgQuality;
    }

    public void setImgMaxSide(int imgMaxSide) {
        this.imgMaxSide = imgMaxSide;
    }

    public void setImgCutSize(int imgCutSize) {
        this.imgCutSize = imgCutSize;
    }

    public void setNeedCrop(boolean needCrop) {
        this.needCrop = needCrop;
    }

    public void setRatio(int ratioX, int ratioY) {
        this.ratioX = ratioX;
        this.ratioY = ratioY;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void setMaxPicSize(int maxPicSize) {
        this.maxPicSize = maxPicSize;
    }

    public void setSelectedSize(int selectedSize) {
        this.selectedSize = selectedSize;
    }

    public void setSupplyTake(boolean supplyTake) {
        this.supplyTake = supplyTake;
    }

    public void setSupplyPick(boolean supplyPick) {
        this.supplyPick = supplyPick;
    }

    public void setActionName(String take, String pick){
        this.take = take;
        this.pick = pick;
    }

    /**
     * 显示拍照或从相册选择的对话框
     *
     */
    public void fetchPicture() {
        if (supplyPick && supplyTake){

            List<ActionItem> itemList = new ArrayList<>();
            itemList.add(new ActionItem(take));
            itemList.add(new ActionItem(pick));

            ActionSheetDialog.Builder builder = new ActionSheetDialog.Builder(activity)
                    .setNavigateButton("取消")
                    .setItems(itemList)
                    .setActionSheetListener(new ActionSheetDialog.ActionSheetListener() {
                        @Override
                        public void onActionClick(int position) {
                            if (position == 0){
                                // 拍照
                                if (PermissionManager.getInstance().isPermissionGranted(activity, PermissionEnum.PERMISSION_CAMERA)){
                                    takePhoto();
                                }else{
                                    PermissionManager.getInstance().setOnPermissionRequestListener(PhotoHelper.this);
                                    PermissionManager.getInstance().requestPermission(activity, PermissionEnum.PERMISSION_CAMERA);
                                }
                            }else if (position == 1){
                                // 从相册选择
                                if (PermissionManager.getInstance().isPermissionGranted(activity, PermissionEnum.PERMISSION_READ_EXTERNAL_STORAGE)){
                                    pickPhoto();
                                }else{
                                    PermissionManager.getInstance().setOnPermissionRequestListener(PhotoHelper.this);
                                    PermissionManager.getInstance().requestPermission(activity, PermissionEnum.PERMISSION_READ_EXTERNAL_STORAGE);
                                }
                            }
                        }

                        @Override
                        public void onPositiveButtonClick() {

                        }
                    });
            builder.build().show();

        }else if (supplyTake){
            // 拍照
            if (PermissionManager.getInstance().isPermissionGranted(activity, PermissionEnum.PERMISSION_CAMERA)){
                takePhoto();
            }else{
                PermissionManager.getInstance().setOnPermissionRequestListener(PhotoHelper.this);
                PermissionManager.getInstance().requestPermission(activity, PermissionEnum.PERMISSION_CAMERA);
            }
        }else if (supplyPick){
            // 从相册选择
            if (PermissionManager.getInstance().isPermissionGranted(activity, PermissionEnum.PERMISSION_READ_EXTERNAL_STORAGE)){
                pickPhoto();
            }else{
                PermissionManager.getInstance().setOnPermissionRequestListener(PhotoHelper.this);
                PermissionManager.getInstance().requestPermission(activity, PermissionEnum.PERMISSION_READ_EXTERNAL_STORAGE);
            }
        }

    }

    private void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (TextUtils.isEmpty(takePhotoPath)){
            takePhotoPath = new File(FileUtils.getCacheChildDir(activity, FileUtils.DIR_IMAGES).getAbsolutePath(),System.currentTimeMillis() + FileUtils.JPG_SUFFIX).getPath();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".business.fileProvider", new File(takePhotoPath));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(takePhotoPath)));
        }
        activity.startActivityForResult(intent, REQ_CODE_TAKE_PHOTO);
    }

    private void pickPhoto() {
        if (maxPicSize == 1){
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            activity.startActivityForResult(intent, REQ_CODE_PICK_PHOTO);
        }else {
            resultListener.gotoAlbum(activity, maxPicSize - selectedSize);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQ_CODE_TAKE_PHOTO:
                if (needCrop){
                    needCropPathList.add(takePhotoPath);
                    startPhotoZoom();
                }else {
                    compressBitmap(takePhotoPath);
                    resultListener.onPhotoResult(photoList, tag);
                }
                break;
            case REQ_CODE_PICK_PHOTO:
                File file = FileUtils.getFileByUri(activity.getApplicationContext(), data.getData());
                if (needCrop){
                    needCropPathList.add(file.getPath());
                    startPhotoZoom();
                }else{
                    compressBitmap(file.getPath());
                    resultListener.onPhotoResult(photoList, tag);
                }
                break;
            case REQ_CODE_CUT_PHOTO:
                compressBitmap(cutPhotoPath);
                startPhotoZoom();
                break;
        }
    }

    public void onChooseFromAlbumResult(List<String> choseList){
        if (!CollectionUtils.isEmpty(choseList)){
            needCropPathList.clear();
            needCropPathList.addAll(choseList);
            if (needCrop){
                startPhotoZoom();
            }else{
                for (int i = 0; i < needCropPathList.size(); i++) {
                    compressBitmap(needCropPathList.get(i));
                }
                resultListener.onPhotoResult(photoList, tag);
            }
        }
    }

    private void compressBitmap(String filePath){
        int degree = ImageUtils.readImageDegree(filePath);
        File dstFile = new File(FileUtils.getCacheChildDir(activity.getApplicationContext(), FileUtils.DIR_IMAGES),
                IMAGE_FILE_SUFFIX + System.currentTimeMillis() + FileUtils.JPG_SUFFIX);
        Bitmap srcBitmap = ImageUtils.getBitmapFromFile(new File(filePath), imgMaxSide);
        ImageUtils.compressBitmapToFile(srcBitmap, imgQuality, imgMaxSide, degree, dstFile);
        if (srcBitmap != null && !srcBitmap.isRecycled()) {
            srcBitmap.recycle();
            srcBitmap = null;
        }
        photoList.add(dstFile.getPath());
    }

    private void startPhotoZoom() {
        if (tempCropPosition >= needCropPathList.size()){
            resultListener.onPhotoResult(photoList, tag);
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Uri fileUri;
        Uri outputUri;
        cutPhotoPath = new File(FileUtils.getAppChildDir(activity, FileUtils.DIR_IMAGES),System.currentTimeMillis() + FileUtils.JPG_SUFFIX).getPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".business.fileProvider", new File(needCropPathList.get(tempCropPosition)));
        } else {
            fileUri = Uri.fromFile(new File(needCropPathList.get(tempCropPosition)));
        }
        //注 crop时output务必使用fromFile形式，否则会提示无权限。。。
        outputUri = Uri.fromFile(new File(cutPhotoPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

        tempCropPosition++;
        try{
            intent.setDataAndType(fileUri, IMAGE_UNSPECIFIED);
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");

            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", ratioX);
            intent.putExtra("aspectY", ratioY);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", imgCutSize);
            intent.putExtra("outputY", imgCutSize / ratioX * ratioY);
//            intent.putExtra("return-data", false);

            activity.startActivityForResult(intent, REQ_CODE_CUT_PHOTO);
        }catch (ActivityNotFoundException e) {
            secondWayToZoom(fileUri);
        }

    }

    private void secondWayToZoom(Uri uri){
        File file = FileUtils.getFileByUri(activity.getApplicationContext(), uri);
        if(file == null || file.length()==0){
            LogUtils.print(getClass(), "图片裁剪失败，请稍后再试");
            return;
        }
        Bitmap bitmap = ImageUtils.getBitmapFromFile(file, imgCutSize);
        Bitmap squareBitmap = ImageUtils.centerSquareScaleBitmap(bitmap, imgCutSize);
        if(bitmap != null && !bitmap.isRecycled() && bitmap != squareBitmap){
            bitmap.recycle();
            bitmap = null;
        }
        int degree = ImageUtils.readImageDegree(file.getPath());
        File dstFile = new File(FileUtils.getCacheChildDir(activity.getApplicationContext(), FileUtils.DIR_IMAGES),
                IMAGE_FILE_SUFFIX + System.currentTimeMillis() + FileUtils.JPG_SUFFIX);
        ImageUtils.compressBitmapToFile(squareBitmap, imgQuality, imgCutSize, degree, dstFile);
        if(squareBitmap != null && !squareBitmap.isRecycled()){
            squareBitmap.recycle();
            squareBitmap = null;
        }
        photoList.add(dstFile.getPath());
        startPhotoZoom();
    }

    @Override
    public void onPermissionRequestSucceed(PermissionEnum permissionType) {
        if (permissionType == PermissionEnum.PERMISSION_CAMERA){
            takePhoto();
        }else if (permissionType == PermissionEnum.PERMISSION_READ_EXTERNAL_STORAGE){
            pickPhoto();
        }
    }

    @Override
    public void onPermissionRequestFailed(PermissionEnum permissionType, String permission, boolean rejectByUser) {
        resultListener.onPermissionDeny(permissionType, rejectByUser);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface ResultListener {

        void onPhotoResult(List<String> filePathList, int tag);

        void onPermissionDeny(PermissionEnum permissionType, boolean rejectByUser);

        void gotoAlbum(Activity activity, int desireCount);
    }
}
