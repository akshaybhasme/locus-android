package com.radiuslabs.locus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiuslabs.locus.imagetransformations.CircleTransform;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.restservices.RestClient;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

    private ProgressDialog pd;

    private Location location;

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
                pd = new ProgressDialog(StoryCaptureActivity.this, R.style.ProgressDialogTheme);
                pd.setCancelable(false);
                pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                pd.setMessage("Publishing story...");
                pd.show();
                uploadImage();
            }
        });


        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(bestProvider);

        ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        TextView tvName = (TextView) findViewById(R.id.tvName);

        if (Util.user != null) {
            tvName.setText(Util.user.getFullName());
            Picasso
                    .with(this)
                    .load(Util.user.getProfile_pic())
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.placeholder_user)
                    .into(ivProfilePic);
        }

        findViewById(R.id.llProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddresses(location);
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
                        String contentUrl = response.body().string().replace("\"", "");
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
        Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
        story.setLocation(location.getLatitude(), location.getLongitude());

        story.setContent_url(contentUrl);

        Call<Story> call = RestClient.getInstance().getStoryService().createStory(story);
        call.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                if (response.isSuccessful()) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getAddresses(Location location) {
        Geocoder geocoder = new Geocoder(StoryCaptureActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            for (Address address : addresses) {
                Log.d(TAG, address.getAddressLine(0));
            }
        }
    }

}
