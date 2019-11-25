package com.playin.touch;

import android.app.Instrumentation;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tech.playin.control.InjectEvents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.view.MotionEvent.ACTION_POINTER_DOWN;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private List<String> touches = new ArrayList<>();

    private int width;
    private int height;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        int p3d   = ACTION_POINTER_DOWN | 0x0200;
        int p4d   = ACTION_POINTER_DOWN | 0x0300;

        Log.e("TAG", "----> p3d :" + p3d + "  ===  p4d:" + p4d);

        int p3d1   = ACTION_POINTER_DOWN | 8 << 8;
        int p4d1   = ACTION_POINTER_DOWN | 9 << 8;

        Log.e("TAG", "----> p3d1 :" + p3d1 + "  ===  p4d1:" + p4d1);


        findViewById(R.id.simulateTouch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag = true;
                final Instrumentation inst = new Instrumentation();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        testSaveJson();
//                        testRestore(inst);

                        for (int i = 0; i < touches.size(); i++) {
                            MotionEvent event = InjectEvents.parseMotionEventsMultipoint(width, height, touches.get(i));
                            inst.sendPointerSync(event);
                        }

                    }
                }).start();
            }
        });
    }

    private void testSaveJson() {
        try {
            File file = new File(getExternalFilesDir("Test"), "test.json");
            Log.e("TAG", "===============>  " + file.getAbsolutePath() + "  ===  " + touches.size());
            FileWriter fileWriter = new FileWriter(file);
            for (int i = 0; i < touches.size(); i++) {
                fileWriter.write(touches.get(i) + ",\n");
            }
            fileWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void testRestore(Instrumentation inst) {

        try {
            InputStream is = getAssets().open("test.json");
            int len = is.available();
            byte[] buffer = new byte[len];
            is.read(buffer);
            String result = new String(buffer);
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                MotionEvent event = InjectEvents.parseMotionEventsMultipoint(width, height, array.optJSONObject(i).toString());
                inst.sendPointerSync(event);
//                Log.e("TAG", array.optJSONObject(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GameEvent {
        int action;
        int pointerCount;
        MotionEvent.PointerProperties[] properties;
        MotionEvent.PointerCoords[] coords;
    }

    private String convertGameEvent(GameEvent gameEvent) {
        try {
            int action = gameEvent.action;
            if (action == 1) {
                action = 2;
            } else if (action == 2) {
                action = 1;
            }

            JSONObject obj1 = new JSONObject();
            for (int i = 0; i < gameEvent.pointerCount; i++) {
                float rateWidth = gameEvent.coords[i].x / width;
                float rateHeight = gameEvent.coords[i].y / height;
                String control = rateWidth + "_" + rateHeight + "_" + action + "_0_0";
                obj1.put("" + gameEvent.properties[i].id, control);
            }
            Log.e("TAG", "原始数据 ====>  " + obj1.toString());

            JSONObject obj = new JSONObject();
            for (int i = 0; i < gameEvent.pointerCount; i++) {
                float rateWidth = gameEvent.coords[i].x / width;
                float rateHeight = gameEvent.coords[i].y / height;
                int tmpAction = actionFilter(action, i);
                String control = rateWidth + "_" + rateHeight + "_" + tmpAction + "_0_0";
                obj.put("" + gameEvent.properties[i].id, control);
            }
            Log.e("TAG", "修改数据 ----->  " + obj.toString());
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int actionFilter(int action, int i) {
        int tmpAction = action;
        // down
        if (action == 5) {
            if (i == 0) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 261) {
            if (i == 1) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 517) {
            if (i == 2) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 773) {
            if (i == 3) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1029) {
            if (i == 4) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1085) {
            if (i == 5) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1541) {
            if (i == 6) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1797) {
            if (i == 7) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 2053) {
            if (i == 8) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        } else if (action == 2309) {
            if (i == 9) {
                tmpAction = 0;
            } else {
                tmpAction = 1;
            }
        }

        // up
        if (action == 6) {
            if (i == 0) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 262) {
            if (i == 1) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 518) {
            if (i == 2) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 774) {
            if (i == 3) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1030) {
            if (i == 4) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1086) {
            if (i == 5) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1542) {
            if (i == 6) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 1798) {
            if (i == 7) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 2054) {
            if (i == 8) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        } else if (action == 2310) {
            if (i == 9) {
                tmpAction = 2;
            } else {
                tmpAction = 1;
            }
        }
        return tmpAction;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (flag) return true;

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
        int action = event.getAction();
        gameEvent.action = action;
        gameEvent.pointerCount = pointerCount;
        gameEvent.properties = pps;
        gameEvent.coords = pcs;
        String conStr = convertGameEvent(gameEvent);
        if (!flag && !TextUtils.isEmpty(conStr)) touches.add(conStr);
        return true;
    }
}
