package com.ivankvasov.myapplicatio;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class BatteryService extends Service {
    private static final String TAG = BatteryService.class.getSimpleName();
    private final Handler handler = new Handler();
    private Runnable runnable;
    private final int delay = 60000; // 1 minute
    private boolean isRunning = false;
    private BufferedWriter writer;

    private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale;
            Log.d(TAG, "Battery level: " + batteryPct);
            try {
                writer.write(System.currentTimeMillis() + "," + batteryPct + "\n");
                writer.flush();
            } catch (IOException e) {
                Log.e(TAG, "Error writing to file: " + e.getMessage());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        File file = new File(getExternalFilesDir(null), "battery_data.txt");
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("Time,Battery Level\n");
        } catch (IOException e) {
            Log.e(TAG, "Error creating file: " + e.getMessage());
        }
        registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        runnable = new Runnable() {
            public void run() {
                if (isRunning) {
                    handler.postDelayed(this, delay);
                    // do something
                }
            }
        };
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacks(runnable);
        unregisterReceiver(batteryInfoReceiver);
        try {
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing file: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
