package com.tech.playin.control;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InjectEvents {

    public static void testDefaultEvents(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                int width = dm.widthPixels;
                int height = dm.heightPixels;
                String str = "{\"0\":\"0.621333_0.511994_0_0_0\",\"1\":\"0.697333_0.695652_0_0_0\",\"2\":\"0.306667_0.472264_0_0_0\"}";
                testInjectEvents(width, height, str);
            }
        }).start();
    }

    public static void testInjectEvents(float destWidth, float destHeight, String controlStr) {
        final List<MotionEvent> mes = parseMotionEvents(destWidth, destHeight, controlStr);
        final Instrumentation inst = new Instrumentation();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mes.size(); i++) {
                    inst.sendPointerSync(mes.get(i));
                }
            }
        };
        if (Looper.getMainLooper() == Looper.myLooper()) {
            new Thread(runnable).start();
        } else {
            runnable.run();
        }
    }

    /**
     * 将服务器触摸手势转成MotionEvents。
     * @param destWidth 目标宽
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
//        int action = -1;
//        switch (Integer.parseInt(commands[2])) {
//            case 0:
//                action = MotionEvent.ACTION_DOWN;
//                break;
//            case 1:
//                action = MotionEvent.ACTION_MOVE;
//                break;
//            case 2:
//                action = MotionEvent.ACTION_UP;
//                break;
//            default:
//                break;
//        }
        int action = Integer.parseInt(commands[2]);
        return MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action,
                Float.parseFloat(commands[0]) * destWidth, Float.parseFloat(commands[1]) * destHeight, 0);
    }
}
