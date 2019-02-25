package com.lyne.fw.permission;

/**
 * Created by liht on 2018/8/28.
 */

public enum PermissionEnum {

    PERMISSION_CAMERA(2101),
    PERMISSION_RECORD_AUDIO(2102),
    PERMISSION_FINE_LOCATION(2103),
    PERMISSION_READ_CONTACTS(2104),
    PERMISSION_READ_EXTERNAL_STORAGE(2105),
    PERMISSION_WRITE_EXTERNAL_STORAGE(2106),
    PERMISSION_SYSTEM_ALERT_WINDOW(2107),
    PERMISSION_SEND_SMS(2108),
    PERMISSION_CALL_PHONE(2109),
    PERMISSION_RECORD_AV(2110),

    ;

    private int requestCode;

    PermissionEnum(int requestCode){
        this.requestCode = requestCode;
    }

    public int getRequestCode(){
        return requestCode;
    }

}
