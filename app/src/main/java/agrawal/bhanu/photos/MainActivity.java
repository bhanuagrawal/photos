package agrawal.bhanu.photos;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import agrawal.bhanu.photos.imagelist.PostViewModel;
import agrawal.bhanu.photos.imagelist.datamodel.ImageDTO;
import agrawal.bhanu.photos.imagelist.ui.ImageList;
import agrawal.bhanu.photos.imagelist.ui.preview.Image;
import agrawal.bhanu.photos.upload.ui.Camera;
import agrawal.bhanu.photos.upload.ImageManager;
import agrawal.bhanu.photos.imagelist.ui.preview.ImagePreview;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        ImageList.OnFragmentInteractionListener,
        Camera.OnFragmentInteractionListener, ImagePreview.OnFragmentInteractionListener, Image.OnFragmentInteractionListener {

    public static final String MAIN_FRAGMENT = "dfsgfdhfmgcfxgd";
    public static final String UPLOAD_PREVIEW_FRAGMENT = "sdrtdthgfjgfdh" ;
    private static final String NAV_FRAGMENT = "asdfasdfasdfasdf";
    private ImageManager imageManager;
    private NavController navigationController;
    private NavHostFragment navHostFragment;
    private PostViewModel postViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageManager = new ImageManager(getApplication());
        setContentView(R.layout.activity_main);


        navigationController = Navigation.findNavController(this, R.id.my_nav_host_fragment);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {

            if (type.startsWith("Image/")) {

/*                Bundle bundle = new Bundle();
                bundle.putString("param1", "");
                bundle.putString("param2", "");
                navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
                navigationController.navigate(R.id.mainFragment, bundle);*/

                navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                openImagePreviewFragment(imageUri);
            }

            return;
        }
        if (savedInstanceState != null) {
            navHostFragment = (NavHostFragment) getSupportFragmentManager().getFragment(savedInstanceState, NAV_FRAGMENT);
        }
        else{
            navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
            savedInstanceState = new Bundle();
            savedInstanceState.putString("param1", "");
            savedInstanceState.putString("param2", "");
            navigationController.navigate(R.id.mainFragment, savedInstanceState);
        }

        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, NAV_FRAGMENT, navHostFragment);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void openImagePreviewFragment(Uri uri) {

        Bundle bundle = new Bundle();
        bundle.putInt("param1", 0);
        bundle.putBoolean("param2", true);
        bundle.putSerializable("param3", ImageDTO.mode.LOAD_LOCAL_FROM_URI);
        navigationController.navigate(R.id.uploadImagePreview, bundle);

        ImageDTO image = new ImageDTO(ImageDTO.mode.LOAD_LOCAL_FROM_URI);
        image.setUri(uri);


        postViewModel.getImageLiveData().postValue(image);

    }

    @Override
    public void onCapture(String path) {

        Bundle bundle = new Bundle();
        bundle.putInt("param1", 0);
        bundle.putBoolean("param2", true);
        bundle.putSerializable("param3", ImageDTO.mode.LOAD_LOCAL_FROM_PATH);
        navigationController.navigate(R.id.uploadImagePreview, bundle);

        ImageDTO image = new ImageDTO(ImageDTO.mode.LOAD_LOCAL_FROM_PATH);
        image.setPath(path);

        postViewModel.getImageLiveData().postValue(image);


    }

    @Override
    public void onFragmentCreated(Fragment fragment) {

    }

    @Override
    public void onCofirmUpload(File path) {
        imageManager.uploadFile(path);
        onSend();
    }

    private void onSend() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        Fragment mainFragment = navHostFragment.getChildFragmentManager().findFragmentByTag(MAIN_FRAGMENT);

        if(mainFragment == null){

/*            Bundle bundle = new Bundle();
            bundle.putString("param1", "");
            bundle.putString("param2", "");

            navigationController.navigate(R.id.mainFragment, bundle);*/
            onBackPressed();

        }
        else{
            onBackPressed();
        }
    }

    @Override
    public void onCancelUpload(String path) {
        navigationController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        Fragment mainFragment = navHostFragment.getChildFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
        if(mainFragment != null && mainFragment.isVisible()){

            ViewPager viewPager = ((MainFragment) mainFragment).getHomeViewPager();
            if(viewPager.getCurrentItem() != Constants.DEFAULT_PAGE_POSITION){
                viewPager.setCurrentItem(Constants.DEFAULT_PAGE_POSITION, true);
            }
            else{
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }

    public void showImagePreview(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("param1", position);
        bundle.putBoolean("param2", false);
        bundle.putSerializable("param3", ImageDTO.mode.LOAD_FROM_SERVER);
        navigationController.navigate(R.id.uploadImagePreview, bundle);


    }




}
