package com.playin.touch;

import android.app.Instrumentation;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.tech.playin.control.InjectEvents;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

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

        findViewById(R.id.simulateTouch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                final Instrumentation inst = new Instrumentation();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < touches.size(); i++) {
//                            List<MotionEvent> event = InjectEvents.parseMotionEvents(width, height, touches.get(i));
//                            for (int j = 0; j < event.size(); j ++) {
//                                inst.sendPointerSync(event.get(j));
//                            }

                            MotionEvent event = InjectEvents.parseMotionEventsMultipoint(width, height, touches.get(i));
                            inst.sendPointerSync(event);
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

        int action = event.getActionMasked();
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
}
