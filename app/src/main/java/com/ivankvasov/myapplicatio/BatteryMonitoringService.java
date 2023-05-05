package com.ivankvasov.myapplicatio;

import static android.app.Service.START_STICKY;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

//import java.lang.management.ManagementFactory;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BatteryMonitoringService extends Service {
    private Handler handler; // создаем класс для обработки событий
    private Runnable runnable; // создаем класс для запуска задачи в фоновом режиме

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // инициализируем объект handler
        handler = new Handler();

        // создаем объект runnable, который будет запускаться каждую минуту
        runnable = new Runnable() {
            @Override
            public void run() {
                // получаем данные о батарее, нагрузке CPU и температуре устройства
                int batteryLevel = getBatteryLevel();
//                int cpuLoad = getCpuLoad();
                float deviceTemperature = getDeviceTemperature();

                // записываем данные в файл
                writeDataToFile(batteryLevel, deviceTemperature);

                // запускаем задачу через 1 минуту
                handler.postDelayed(runnable, 60000);
            }
        };

        // запускаем задачу
        handler.post(runnable);

        return START_STICKY;
    }

    private int getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = -1;
        int scale = -1;
        if (batteryIntent != null) {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        return Math.round(level / (float) scale * 100);
    }

//    private Intent registerReceiver(Object o, IntentFilter intentFilter) {}

//    private int getCpuLoad() {
//        return (int) (ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() * 100);
//    }

    private float getDeviceTemperature() {
        float temperature = -1;
        try {
            RandomAccessFile reader = new RandomAccessFile("/sys/class/thermal/thermal_zone0/temp", "r");
            String line = reader.readLine();
            temperature = Float.parseFloat(line) / 1000.0f;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temperature;
    }

    private void writeDataToFile(int batteryLevel, float deviceTemperature) {
        try {
            String fileName = "BatteryMonitorData.txt";
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
            FileOutputStream fos = new FileOutputStream(file, true);

            // записываем данные в файл
            fos.write((batteryLevel + "," + deviceTemperature + "\n").getBytes());

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // останавливаем задачу
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
