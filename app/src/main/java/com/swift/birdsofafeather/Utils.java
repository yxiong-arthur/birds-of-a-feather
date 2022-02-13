package com.swift.birdsofafeather;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.transition.Transition;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.swift.birdsofafeather.model.db.Class;
import com.swift.birdsofafeather.model.db.Student;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getApplicationContext().getString(R.string.preference_file_key), MODE_PRIVATE);
        return preferences;
    }

    public static Bitmap urlToBitmap(Context context, String URL) {
        ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
        Future<Bitmap> bmap;

        bmap = backgroundThreadExecutor.submit(() -> {
            return Glide
                    .with(context)
                    .asBitmap()
                    .load(URL)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.logo)
                            .override(200, 200)
                            .centerCrop())
                    .submit()
                    .get();
        });

        try {
            return bmap.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String bitmapToString(Bitmap bmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    public static Bitmap stringToBitmap(String bmap_string) {
        byte[] b = Base64.decode(bmap_string, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

        return bitmap;
    }

    public static String encodeStudent(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getApplicationContext().getString(R.string.preference_file_key), MODE_PRIVATE);

        String studentUUIDString = preferences.getString("student_id", "default");
        String studentName = preferences.getString("first_name", "default");
        String photoURL = preferences.getString("image_url", "default");

        return studentUUIDString + "," + studentName + "," + photoURL;
    }

    public static String encodeClasses(List<Class> classes) {
        String res = "";

        for (Class c : classes) {
            res += "," + c;
        }

        return res;
    }
}
