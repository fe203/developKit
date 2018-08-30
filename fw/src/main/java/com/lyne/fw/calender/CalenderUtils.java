package com.lyne.fw.calender;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

/**
 * Created by liht on 2018/4/20.
 * 日历相关操作
 */

public class CalenderUtils {

    /**
     * 添加日历事件
     * @param context
     * @param title
     * @param address
     * @param beginTime
     */
    public static int addCalendarEvent(Context context, String title, String address, long beginTime){

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, address);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTime);
        intent.putExtra(CalendarContract.Events.ALLOWED_REMINDERS, true);

        try {
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }

        return 0;

    }


}
