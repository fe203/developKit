package com.lyne.socialmodule.share;

/**
 * Created by shaohui on 2016/11/18.
 */

public abstract class ShareListener {

    public abstract void shareSuccess();

    public abstract void shareFailure(Exception e);

    public abstract void shareCancel();

}
