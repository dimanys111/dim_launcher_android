package com.example.mylauncher;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (@NotNull Thread thread, Throwable e)
            {
                handleUncaughtException (e);
            }
        });
    }


    private static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    private static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }


    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ?
                        getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    private void handleUncaughtException (Throwable e)
    {
        File f=getDiskCacheDir(this,"log");
        if (!f.exists()) {
            f.mkdirs();
        }
        File fileLog = new File(f, "log.log");
        String s=fileLog.getAbsolutePath();
        String s1=e.getMessage()+e.getLocalizedMessage();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(s, true)));
            out.println(s1);
            out.close();
        } catch (IOException v) {
            //exception handling left as an exercise for the reader
        }
    }
}