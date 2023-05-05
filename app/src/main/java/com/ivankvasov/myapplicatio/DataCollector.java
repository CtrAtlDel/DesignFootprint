package com.ivankvasov.myapplicatio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataCollector extends Service {
    private final static String FILE_NAME = "static.txt";

    private FileOutputStream fos = null;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        try {
            this.fos = new FileOutputStream(getExternalPath());
            String start = "application is starting";
            fos.write(start.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getExternalPath() {
        return new File(getExternalFilesDir(null), FILE_NAME);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            this.fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    // сохранение файла
    public void saveText(View view) {

        try (FileOutputStream fos = new FileOutputStream(getExternalPath())) {
//            EditText textBox = findViewById(R.id.editor);
//            String text = textBox.getText().toString();
//            fos.write(text.getBytes());
            boolean flag = true;
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // открытие файла
    public void openText(View view) {

//        TextView textView = findViewById(R.id.text);
        File file = getExternalPath();
        // если файл не существует, выход из метода
        if (!file.exists()) return;
        try (FileInputStream fin = new FileInputStream(file)) {
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
//            textView.setText(text);
        } catch (IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
