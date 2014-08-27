package frank.incubator.android.mysocial.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import frank.incubator.android.mysocial.common.Constants;

/**
 * Common Utilities APIs for common usage.
 * Created by f78wang on 8/20/14.
 */
public class CommonUtils {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Get the Stack trace from throwable instance.
     *
     * @param t Exception to be print.
     * @return String formed stack Trace.
     */
    public static String getStack(Throwable t) {
        if (t == null)
            return null;
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Transform Obj to Json String.
     * Null input will get Null output.
     *
     * @param obj Object to be transform to json.
     * @return String formed json object.
     */
    public static String toJson(Object obj) {
        if (obj == null)
            return null;
        return JSON.toJSONString(obj);
    }

    /**
     * Checks if external storage is available for read and write.
     *
     * @return if external storage is writable.
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read.
     *
     * @return if external storage is readable.
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    public static long queryFreeSpace(Context context, boolean isExternal) {
        long ret = -1;
        if(isExternal){
            if(isExternalStorageReadable())
                ret = Environment.getExternalStorageDirectory().getFreeSpace();
        }else{
            if( context.getFilesDir() != null )
            ret = context.getFilesDir().getFreeSpace();
        }
        return  ret;
    }

    public static File saveFileToExternal(Context context, InputStream in, String path, boolean isApplicationPrivate, boolean isPublic, String publicType) {
        File parent, targetFile = null;
        if (isExternalStorageWritable()) {
            FileOutputStream fos = null;
            if (isPublic) {
                if (isApplicationPrivate)
                    parent = context.getExternalFilesDir(publicType);
                else
                    parent = Environment.getExternalStoragePublicDirectory(publicType);
            } else {
                if (isApplicationPrivate)
                    parent = context.getExternalFilesDir(null);
                else
                    parent = Environment.getExternalStorageDirectory();
            }
            targetFile = new File(parent, path);
            if (!targetFile.getParentFile().exists())
                targetFile.getParentFile().mkdirs();
            try {
                fos = new FileOutputStream(targetFile);
                byte[] data = new byte[1024];
                int read;
                while ((read = in.read(data)) != -1) {
                    fos.write(data, 0, read);
                }
                fos.flush();
            } catch (IOException ex) {
                Log.e(Constants.LOG_TAG, "Save file to External failed. path=" + path + ",public:" + isPublic + ",type:" + publicType + ",isApplicationPrivate=" + isApplicationPrivate + ".\n" + getStack(ex));
            } finally {
                close(fos);
                close(in);
                close(fos);
            }
        }
        return targetFile;
    }

    public static File saveFileToInternal(Context context, InputStream in, String path, boolean isApplicationPrivate) {
        File parent, targetFile = null;
        FileOutputStream fos = null;
        if (isApplicationPrivate)
            parent = context.getFilesDir();
        else
            parent = context.getDir("", Context.MODE_PRIVATE);

        targetFile = new File(parent, path);
        if (!targetFile.getParentFile().exists())
            targetFile.getParentFile().mkdirs();
        try {
            fos = new FileOutputStream(targetFile);
            byte[] data = new byte[1024];
            int read;
            while ((read = in.read(data)) != -1) {
                fos.write(data, 0, read);
            }
            fos.flush();
        } catch (IOException ex) {
            Log.e(Constants.LOG_TAG, "Save file to External failed. path=" + path + ",isApplicationPrivate=" + isApplicationPrivate + ".\n" + getStack(ex));
        } finally {
            close(fos);
            close(in);
            close(fos);
        }
        return targetFile;
    }

    public static File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Get Temp File met exception." + getStack(e));
        }
        return file;
    }

    public static void setConfig(Context context, String key, String value){
        if(key ==null || value == null)
            return;
        SharedPreferences sharedPref = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value.toString());
        editor.commit();
    }
      
    public static String getConfig(Context context, String key, String defaultValue){
        if(context ==null || key == null)
            return defaultValue;
        SharedPreferences sharedPref = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        String ret = sharedPref.getString(key, defaultValue);
        if( ret == null )
            ret = defaultValue;
        return ret;
    }
}
