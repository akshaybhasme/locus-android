package com.radiuslabs.locus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.restservices.RestClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryCaptureActivity extends Activity {

    public static final String TAG = "StoryCaptureActivity";

    public static final int REQUEST_IMAGE_CAPTURE = 111;

    private ImageView imageView;
    private EditText storyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_capture);

        imageView = (ImageView) findViewById(R.id.ibSelectMedia);
        storyText = (EditText) findViewById(R.id.etStoryText);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        });

        findViewById(R.id.fabUploadStory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishStory();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }
                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data.getData();
                }
                Log.d(TAG, "Path: " + selectedImageUri.getPath());

                try {
                    Bitmap d = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    int nh = (int) (d.getHeight() * (720.0 / d.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(d, 720, nh, true);
                    imageView.setImageBitmap(scaled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Uri outputFileUri;

    //TODO use Util function
    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Locus" + File.separator);
        root.mkdirs();
        final String fname = "ImageName";//Utils.getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
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

        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void publishStory() {
        Story story = new Story();
        story.setContent_text(storyText.getText().toString());
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
        story.setLocation(location.getLatitude(), location.getLongitude());

        Call<Story> call = RestClient.getInstance().getStoryService().createStory(story);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                finish();
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                t.printStackTrace();
                ;
            }
        });

    }

}
