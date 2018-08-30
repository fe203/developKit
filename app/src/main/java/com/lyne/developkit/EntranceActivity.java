package com.lyne.developkit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lyne.business.dialog.DialogFactory;
import com.lyne.business.photo.PhotoHelper;
import com.lyne.fw.device.DeviceInfo;
import com.lyne.fw.permission.PermissionEnum;
import com.lyne.fw.permission.PermissionManager;

import java.util.List;

/**
 * Created by liht on 2018/8/29.
 */

public class EntranceActivity extends AppCompatActivity {

    private PhotoHelper photoHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.page_entrance);

        findViewById(R.id.show_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EntranceActivity.this, ShowPicActivity.class));
            }
        });

        findViewById(R.id.choose_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoHelper = new PhotoHelper(EntranceActivity.this, new PhotoHelper.ResultListener() {
                    @Override
                    public void onPhotoResult(List<String> filePathList, int tag) {
                        Toast.makeText(EntranceActivity.this, "照片选择成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDeny(PermissionEnum permissionType, boolean rejectByUser) {
                        if (!rejectByUser) {
                            String permission = PermissionManager.getInstance().getPermissionName(permissionType);
                            String appName = getString(R.string.app_name);
                            new DialogFactory.Builder(EntranceActivity.this)
                                    .setTitle("你关闭了" + permission + "权限")
                                    .setContent(getString(R.string.how_to_open_permission, appName, appName, permission))
                                    .setLeftText("继续使用")
                                    .setRightText("去设置")
                                    .setListener(new DialogFactory.OnDialogButtonClickListener() {
                                        @Override
                                        public void onDialogButtonClick(Dialog dialog, int which) {
                                            dialog.dismiss();
                                            if (which == DialogFactory.BTN_RIGHT) {
                                                DeviceInfo.gotoSetting(EntranceActivity.this);
                                            }
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    }

                    @Override
                    public void gotoAlbum(Activity activity, int desireCount) {
                        // 自行跳转选择，选择完成后，调用PhotoHelper.onChooseFromAlbumResult
                        Toast.makeText(activity, "请自行前往图片选择页", Toast.LENGTH_SHORT).show();
                    }
                });
                photoHelper.setMaxPicSize(9);
                photoHelper.fetchPicture();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoHelper.onActivityResult(requestCode,resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        photoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
