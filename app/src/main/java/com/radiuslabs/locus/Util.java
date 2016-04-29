package com.radiuslabs.locus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.radiuslabs.locus.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static final int imageMaxSize = 1440;

    public static User user = null;

    public static boolean isStringEmpty(String str){
        return str == null || str.length() == 0;
    }

    public static Uri openImageIntent(Activity context, int REQUEST_IMAGE_CAPTURE) {

        Uri outputFileUri = getImageUri(false);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        context.startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);

        return outputFileUri;
    }

    public static Uri getImageUri(boolean isCropped) {
        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Locus" + File.separator);
        root.mkdirs();
        String fname;

        if (isCropped)
            fname = "Image_Cropped_" + System.currentTimeMillis() + ".jpg";
        else
            fname = "Image_" + System.currentTimeMillis() + ".jpg";

        File sdImageMainDirectory = new File(root, fname);
        return Uri.fromFile(sdImageMainDirectory);
    }

}
