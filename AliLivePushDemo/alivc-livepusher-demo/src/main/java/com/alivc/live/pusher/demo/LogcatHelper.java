package com.alivc.live.pusher.demo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private static String PATH_DIR = "LivePush";
    private LogDumper mLogDumper = null;
    private int mPId;

    private void init(Context context) {

        PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                + File.separator + PATH_DIR;

        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        autoClear(PATH_LOGCAT);
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);

        Log.d("LogcatHelper", "logcat thread " + mLogDumper.isAlive());
         if(!mLogDumper.isAlive()) {
             try {
                 mLogDumper.start();
             } catch (IllegalThreadStateException e) {
                 Log.e("LogcatHelper", "thread already started");
             }
        }
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                out = new FileOutputStream(new File(dir, PATH_DIR + "-"
                        + LogcatDate.getFileName()+ ".log"), true);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            //cmds = "logcat";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            //cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }

                    if(isCreatNewFile(new File(PATH_LOGCAT, PATH_DIR + "-"
                            + LogcatDate.getFileName()+ ".log"))) {
                        new File(PATH_LOGCAT, PATH_DIR + "-"
                                + LogcatDate.getFileName()+ ".log").renameTo(new File(PATH_LOGCAT, PATH_DIR + "-"
                                + LogcatDate.getDateEN()+ ".log"));
                        try {
                            out = new FileOutputStream(new File(PATH_LOGCAT, PATH_DIR + "-"
                                    + LogcatDate.getFileName()+ ".log"), true);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (out != null && line.contains(mPID)) {
                        out.write((LogcatDate.getDateEN() + "  " + line + "\n")
                                .getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }

    private boolean isCreatNewFile(File file) {
        boolean bool = false;
        FileInputStream inputStream = null;
        if(file.exists()) {
            try {
                 inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                return false;
            }
        }

        if(inputStream != null) {
            try {
                if(inputStream.available()/1024.f/1024.f > 10) {
                    bool = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bool;
    }

    private void autoClear(String dir) {

        if(new File(dir).exists() && new File(dir).isDirectory()) {
            File[] files = new File(dir).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    //大于2天
                    if((System.currentTimeMillis() - file.lastModified()) / 1000 > 24 * 60 * 60 * 2) {
                        return true;
                    }
                    return false;
                }
            });

            if(files != null && files.length > 0) {
                for(File file : files) {
                    file.delete();
                }
            }
        }

    }

    public static class LogcatDate {
        public static String getFileName() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date(System.currentTimeMillis()));
            return date;
        }

        public static String getDateEN() {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            String date1 = format1.format(new Date(System.currentTimeMillis()));
            return date1;
        }
    }
}