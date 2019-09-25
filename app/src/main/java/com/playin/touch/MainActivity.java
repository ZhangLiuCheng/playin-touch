package com.playin.touch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private final StringBuilder controlBuilder = new StringBuilder();
    private int width;
    private int height;
    private boolean flag;
    private List<String> touches = new ArrayList<>();

    private List<GameEvent> gameEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        findViewById(R.id.simulateTouch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
//                for (int i = 0; i < touches.size(); i++) {
//                    InjectEvents.testInjectEvents(width, height,touches.get(i));
//                }
//                InjectEvents.testDefaultEvents(MainActivity.this);

                final Instrumentation inst = new Instrumentation();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < touches.size(); i++) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            GameEvent ge = gameEvents.get(i);
//                            MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), ge.action, ge.pointerCount,
//                                    ge.properties, ge.coords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
//                            inst.sendPointerSync(event);

                            try {
                                JSONObject obj = new JSONObject(touches.get(i));
                                int pointerCount = obj.length();
                                int action = 0;

                                MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[pointerCount];
                                MotionEvent.PointerCoords[] coords = new MotionEvent.PointerCoords[pointerCount];

                                int index = 0;
                                Iterator it = obj.keys();
                                while (it.hasNext()) {
                                    String key = it.next().toString();
                                    String value = obj.optString(key);
                                    String[] commands = value.split("_");
                                    float x = Float.parseFloat(commands[0]) * width;
                                    float y = Float.parseFloat(commands[1]) * height;
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

                                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), action, pointerCount,
                                        properties, coords, 0, 0, 1, 1, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
                                inst.sendPointerSync(event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public class GameEvent {
        int action;
        int pointerCount;
        MotionEvent.PointerProperties[] properties;
        MotionEvent.PointerCoords[] coords;
    }

    private String convertGameEvent(GameEvent gameEvent) {
        JSONObject obj = new JSONObject();
        for (int i = 0; i < gameEvent.pointerCount; i++) {
            try {
                float rateWidth = gameEvent.coords[i].x / width;
                float rateHeight = gameEvent.coords[i].y / height;
                String control = rateWidth + "_" + rateHeight + "_" + gameEvent.action + "_0_0";
                obj.put("" + gameEvent.properties[i].id, control);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return obj.toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (flag) return true;

        int action = event.getActionMasked();  // 0 down, 1 up, 2 move
        int pointerCount = event.getPointerCount();
        MotionEvent.PointerProperties[] pps = new MotionEvent.PointerProperties[pointerCount];
        MotionEvent.PointerCoords[] pcs = new MotionEvent.PointerCoords[pointerCount];

        for (int i = 0; i < pointerCount; i ++) {
            MotionEvent.PointerProperties pp = new MotionEvent.PointerProperties();
            event.getPointerProperties(i, pp);
            pps[i] = pp;
            MotionEvent.PointerCoords pc = new MotionEvent.PointerCoords();
            event.getPointerCoords(i, pc);
            pcs[i] = pc;
        }
        GameEvent gameEvent = new GameEvent();

        // 兼容ios
        if (action == 1) {
            action = 2;
        } else if (action == 2) {
            action = 1;
        }

        gameEvent.action = action;
        gameEvent.pointerCount = pointerCount;
        gameEvent.properties = pps;
        gameEvent.coords = pcs;
//        gameEvents.add(gameEvent);

        String conStr = convertGameEvent(gameEvent);
        touches.add(conStr);


        /*
        // 目标触摸 0-down,1-move,2-up
        if (action == 1) {
            action = 2;
        } else if (action == 2) {
            action = 1;
        }
        float rateWidth = event.getX() / width;
        float rateHeight = event.getY() / height;

        controlBuilder.delete(0, controlBuilder.length());
        controlBuilder.append(rateWidth).append("_")
                .append(rateHeight).append("_")
                .append(action)
                .append("_0_0");
        sendControl(event.getActionIndex(), controlBuilder.toString());
         */
        return true;
    }

    private void sendControl(int finger, String control) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("" + finger, control);
            Log.e(TAG, obj.toString());
            touches.add(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
