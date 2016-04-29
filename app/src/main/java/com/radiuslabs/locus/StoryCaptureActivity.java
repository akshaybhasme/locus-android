package com.radiuslabs.locus;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.restservices.RestClient;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryCaptureActivity extends Activity {

    public static final String TAG = "StoryCaptureActivity";

    public static final int REQUEST_IMAGE_CAPTURE = 111;
    public static final int REQUEST_CROP_CAPTURE = 222;

    private ImageView imageView;
    private EditText storyText;

    private Uri outputFileUri, croppedImageUri;

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
                uploadImage();
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
                croppedImageUri = Util.getImageUri(true);
                Crop.of(selectedImageUri, croppedImageUri).withMaxSize(Util.imageMaxSize, Util.imageMaxSize).start(StoryCaptureActivity.this, REQUEST_CROP_CAPTURE);

            } else if (requestCode == REQUEST_CROP_CAPTURE) {
                imageView.setImageURI(croppedImageUri);
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);
//                    imageView.setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }
    }

    private void openImageIntent() {
        outputFileUri = Util.openImageIntent(this, REQUEST_IMAGE_CAPTURE);
    }

    private void uploadImage() {
        Log.d(TAG, "File path: " + croppedImageUri.getPath());
        File f = new File(croppedImageUri.getPath());
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), f);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", f.getName(), file);

        Call<ResponseBody> call = RestClient.getInstance().getStoryService().uploadImage(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String contentUrl = response.body().string();
                        Log.d(TAG, "Image file resp: " + contentUrl);
                        publishStory(contentUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void publishStory(String contentUrl) {
        Story story = new Story();
        story.setContent_text(storyText.getText().toString());
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
        story.setLocation(location.getLatitude(), location.getLongitude());

        story.setContent_url(contentUrl);

        Call<Story> call = RestClient.getInstance().getStoryService().createStory(story);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                if (response.isSuccessful()) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

}
