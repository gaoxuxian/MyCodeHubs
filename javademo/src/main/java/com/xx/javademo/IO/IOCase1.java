package com.xx.javademo.IO;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import util.FileUtil;

public class IOCase1 {
    private String testString = "IOCase1";

    public void init(Context context) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_STORAGE, 0);
            }
        }
    }

    public void writeTestStringToSD() {
        Writer writer = null;
        try {
            File file = new File(FileUtil.getSDPath() + "MyCodeHubs_IO_Case1.txt");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writer = new FileWriter(file);
//            writer = new BufferedWriter(writer, 2);
            writer.write(testString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSDTestString() {
        try {
            Reader reader = new FileReader(FileUtil.getSDPath() + "MyCodeHubs_IO_Case1.txt");
            reader = new BufferedReader(reader);
//            String out = null;
//            while ((out = ((BufferedReader)reader).readLine()) != null) {
//                Log.e("MyCodeHubs_IO_Case1", "readSDTestString: " + out);
//            }
            char[] out = new char[1024];
            StringBuilder builder = new StringBuilder();
            int len;
            while ((len = reader.read(out)) > 0) {
                builder.append(out);
                Log.e("MyCodeHubs_IO_Case1", "readSDTestString: " + builder.substring(0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
