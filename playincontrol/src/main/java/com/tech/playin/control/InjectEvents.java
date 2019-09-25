package com.tech.playin.control;

import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InjectEvents {

    /**
     * 将服务器触摸手势转成MotionEvents。
     * @param destWidth  目标宽
     * @param destHeight 目标高
     * @param controlStr 触控事件字符串
     * @return
     */
    public static List<MotionEvent> parseMotionEvents(float destWidth, float destHeight, String controlStr) {
        JSONObject obj;
        try {
            obj = new JSONObject(controlStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        List<MotionEvent> motionEvents = new ArrayList<MotionEvent>();
        Iterator it = obj.keys();
        while (it.hasNext()) {
            String key = it.next().toString();
            String value = obj.optString(key);
            try {
                motionEvents.add(convertStrToMotionEvent(destWidth, destHeight, value));
            } catch (Exception ex) {
                Log.v("TAG", "格式不对");
            }
        }
        return motionEvents;
    }

    private static MotionEvent convertStrToMotionEvent(float destWidth, float destHeight, String value) {
        String[] commands = value.split("_");
        int action = -1;
        switch (Integer.parseInt(commands[2])) {
            case 0:
                action = MotionEvent.ACTION_DOWN;
                break;
            case 1:
                action = MotionEvent.ACTION_MOVE;
                break;
            case 2:
                action = MotionEvent.ACTION_UP;
                break;
            default:
                break;
        }
        return MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action,
                Float.parseFloat(commands[0]) * destWidth, Float.parseFloat(commands[1]) * destHeight, 0);
    }


    /**
     * 将服务器触摸手势转成MotionEvents,包括多点触控功能。
     * @param destWidth
     * @param destHeight
     * @param controlStr
     * @return MotionEvent。如果格式不标准，有可能返回null。
     */
    public static MotionEvent parseMotionEventsMultipoint(float destWidth, float destHeight, String controlStr) {
        try {
            JSONObject obj = new JSONObject(controlStr);
            int pointerCount = obj.length();
            MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[pointerCount];
            MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[pointerCount];
            int action = 0;
            int index = 0;
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                String value = obj.optString(key);
                String[] commands = value.split("_");
                float x = Float.parseFloat(commands[0]) * destWidth;
                float y = Float.parseFloat(commands[1]) * destHeight;
                action = Integer.parseInt(commands[2]);

                // 兼容ios
                if (action == 1) {
                    action = 2;
                } else if (action == 2) {
                    action = 1;
                }

                MotionEvent.PointerProperties pp = new MotionEvent.PointerProperties();
                pp.id = Integer.parseInt(key);
                properties[index] = pp;
                MotionEvent.PointerCoords pc = new MotionEvent.PointerCoords();
                pc.x = x;
                pc.y = y;
                coords[index] = pc;
                index++;
            }
            MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, pointerCount, properties,
                    coords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
            return event;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}