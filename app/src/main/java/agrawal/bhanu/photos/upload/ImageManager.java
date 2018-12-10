package agrawal.bhanu.photos.upload;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import agrawal.bhanu.photos.network.WebService;
import agrawal.bhanu.photos.upload.datamodel.UploadResponse;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class ImageManager {

    WebService webService;
    ApiInterface apiService;
    Application application;

    public ImageManager(Application application) {
        this.application = application;
        webService = new WebService(application);
        apiService  = webService.getClient().create(ApiInterface.class);
    }

    public void uploadFile(File file){

        RequestBody username =
                RequestBody.create(MediaType.parse("text/plain"), "7017881787");

        try {
            file = new Compressor(application.getApplicationContext()).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("Image/*"), file));

        RequestBody showInfo =
                RequestBody.create(MediaType.parse("text/plain"), "false");

        RequestBody receivers =
                RequestBody.create(MediaType.parse("text/plain"), "7017881787 7055553175");

        String token = "7017881787 de-Rm1BBDoc:APA91bF4Ium0lXph4zMoD2NlN1lGQhXyXsySYTFsNZLyLUKgkaiw4SFq0d8kMf_go71Fdty-zLp2NU4MB6uMBQUxQCzk3tGL_qT9j7ffeFNNX3F3Kf2K8Qiwy4g_55iXuKg0FesJ8Ief";


        apiService.uploadFile(token, filePart, username, receivers, showInfo).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse>call, Response<UploadResponse> response) {


                //Toast.makeText(application.getApplicationContext(), "Image  uploaded!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("agrawal.bhanu.marplay.IMAGE_UPLOADED");
                application.sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(application.getApplicationContext(), "Could not upload Image to server", Toast.LENGTH_SHORT).show();

            }
        });

    }



    public interface ApiInterface {
        @Multipart
        @POST("/feed/Image/")
        Call<UploadResponse> uploadFile(@Header("token") String token, @Part MultipartBody.Part file,
                                        @Part("username") RequestBody username, @Part("receivers") RequestBody receivers, @Part("show_info") RequestBody showInfo);
    }

}
