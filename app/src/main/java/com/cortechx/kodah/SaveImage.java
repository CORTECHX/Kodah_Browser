package com.cortechx.kodah;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Shawn Grant @ Cortechx on 8/6/2018.
 */

public class SaveImage {

    Context context;
    public void SaveImage(Context c){
        context = c;
    }

    public void SaveImage(String url) {
        Toast.makeText(context, "Starting Download", Toast.LENGTH_SHORT).show();

        if (url.startsWith("data:image/")) {

            String encodingPrefix = "base64,";
            int contentStartIndex = url.indexOf(encodingPrefix) + encodingPrefix.length();
            byte[] imageData = Base64.encode(url.substring(contentStartIndex).getBytes(), Base64.DEFAULT);

            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            FileOutputStream fos = null;

            try {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/Download");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                String name = new Date().toString() + ".jpg";
                myDir = new File(myDir, name);

                fos = new FileOutputStream(myDir);
                if (fos != null) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                }
                Toast.makeText(context, "Saved as " + myDir.getPath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Picasso.get().load(url).
                    into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            try {
                                String root = Environment.getExternalStorageDirectory().toString();
                                File myDir = new File(root + "/Download");
                                if (!myDir.exists()) {
                                    myDir.mkdirs();
                                }
                                String name = new Date().toString() + ".jpg";
                                myDir = new File(myDir, name);
                                FileOutputStream out = new FileOutputStream(myDir);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();
                                Toast.makeText(context, "Saved as " + myDir.getPath(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
        }
    }
}

