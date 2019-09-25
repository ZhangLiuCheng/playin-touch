package com.playin.touch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private final StringBuilder controlBuilder = new StringBuilder();
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rateWidth = event.getX() / width;
        float rateHeight = event.getY() / height;

        int action = event.getAction();  // 0 down, 1 up, 2 move
        // 目标触摸 0-down,1-move,2-up
        if (action == 1) {
            action = 2;
        } else if (action == 2) {
            action = 1;
        }
        controlBuilder.delete(0, controlBuilder.length());
        controlBuilder.append(rateWidth).append("_")
                .append(rateHeight).append("_")
                .append(action)
                .append("_0_0");
        sendControl(event.getPointerCount(), controlBuilder.toString());
        return true;
    }

    private void sendControl(int finger, String control) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("" + finger, control);
            Log.e(TAG, obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
