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
import android.provider.ContactsContract;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;

public class Utils {
    public static int MESSAGE_READ = 0;
    public static int MESSAGE_WRITE = 1;

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        else return str.equals("");
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

    public static void showAlert(Activity activity, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder.setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> {dialog.cancel();})
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                context.getApplicationContext().getString(R.string.preference_file_key), MODE_PRIVATE);
        return preferences;
    }

    public static Bitmap urlToBitmap(Context context, ImageView imageResult, String URL){
        Glide
                .with(context)
                .load(URL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.logo)
                        .override(200, 200)
                        .centerCrop())
                .into(imageResult);

        imageResult.buildDrawingCache();
        Bitmap bmap = imageResult.getDrawingCache();

        return bmap;
    }

    public static String bitmapToString(Bitmap bmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    public static Bitmap stringToBitmap(String bmap_string){
        byte[] b = Base64.decode(bmap_string, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

        return bitmap;
    }
}
