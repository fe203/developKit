package com.lyne.socialmodule.mp;

/**
 * Created by shaohui on 2016/11/18.
 */

public abstract class MPListener {

    public abstract void callSuccess();

    public abstract void callFailure(Exception e);

    public abstract void callCancel();

}
