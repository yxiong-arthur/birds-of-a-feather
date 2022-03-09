package com.swift.birdsofafeather;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.swift.birdsofafeather.model.db.Class;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Utils {
    public static int MESSAGE_READ = 0;
    public static int MESSAGE_WRITE = 1;

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else return str.equals("");
    }

    public static int getWriteFrequency() {
        return 10;
    }

    public static int toIntNullsafe(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    public static boolean hasPermission(Context context, String permission) {
        int res = context.checkSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void showAlert(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder.setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.cancel())
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getApplicationContext().getString(R.string.preference_file_key), MODE_PRIVATE);
    }

    public static Bitmap urlToBitmap(Context context, String URL) {
        ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
        Future<Bitmap> bmap;

        bmap = backgroundThreadExecutor.submit(() -> {
            try {
                return Glide
                        .with(context)
                        .asBitmap()
                        .load(URL)
                        .apply(new RequestOptions()
                                .override(200, 200)
                                .centerCrop())
                        .submit()
                        .get();
            } catch (Exception e){
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            }
        });

        try {
            return bmap.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String bitmapToString(Bitmap bmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String bmap_string) {
        byte[] b = Base64.decode(bmap_string, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static String encodeStudent(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);

        String studentUUIDString = preferences.getString("student_id", "default");
        String studentName = preferences.getString("first_name", "default");
        String photoURL = preferences.getString("image_url", "default");

        return studentUUIDString + "," + studentName + "," + photoURL;
    }

    public static String encodeClasses(List<Class> classes) {
        StringBuilder res = new StringBuilder();

        for (Class c : classes) {
            res.append(",").append(c);
        }

        return res.toString();
    }

    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }

    public static double getClassSizeScore(String classSize) {
        switch (classSize) {
            case "tiny":
                return 1;
            case "small":
                return 0.33;
            case "medium":
                return 0.18;
            case "large":
                return 0.10;
            case "huge":
                return 0.06;
            case "gigantic":
                return 0.03;
            default:
                return 0;
        }
    }

    public static int getRecencyScore(String quarter) {
        switch (quarter) {
            case "wi":
                return 0;
            case "sp":
                return 1;
            case "ss1":
            case "ss2":
            case "sss":
                return 2;
            case "fa":
                return 3;
            default:
                return -1;
        }
    }
}
