package com.daohang.trainapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amap.api.fence.GeoFence;
import com.daohang.trainapp.utils.WarningFlag;

import java.util.Objects;

import static com.daohang.trainapp.services.AMapLocationServicesKt.GEOFENCE_BROADCAST_ACTION;

public class GeoFenceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), GEOFENCE_BROADCAST_ACTION)) {
            Bundle bundle = intent.getExtras();
            //获取围栏行为：
            if (bundle != null){
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                //获取自定义的围栏标识：
                String customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                //获取围栏ID:
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                //获取当前有触发的围栏对象：
                GeoFence fence = bundle.getParcelable(GeoFence.BUNDLE_KEY_FENCE);

                if (customId != null) {
                    if (status == GeoFence.STATUS_OUT)
                        WarningFlag.setOutOfRangeWarning(Byte.parseByte(customId), '1');
                    else
                        WarningFlag.setOutOfRangeWarning(Byte.parseByte(customId), '0');
                }

                System.out.println("GeoFenceReceiver: 围栏标示--" + customId + "，围栏id--" + fenceId + "，围栏行为--" + status);
            }
        }
    }
}
