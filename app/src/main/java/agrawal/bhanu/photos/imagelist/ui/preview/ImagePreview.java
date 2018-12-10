package agrawal.bhanu.photos.imagelist.ui.preview;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import agrawal.bhanu.photos.MainActivity;
import agrawal.bhanu.photos.R;
import agrawal.bhanu.photos.imagelist.PostViewModel;
import agrawal.bhanu.photos.imagelist.datamodel.ImageDTO;
import agrawal.bhanu.photos.imagelist.datamodel.Post;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImagePreview.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImagePreview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagePreview extends Fragment implements View.OnTouchListener, ViewPager.OnPageChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final int PIC_CROP = 1;


    // TODO: Rename and change types of parameters
    int position;
    Boolean editable;


    @BindView(R.id.imageviewer)
    ViewPager imageViewer;

    @BindView(R.id.crop)
    ImageView crop;

    Button uploadButton, cancelButton;

    private Unbinder uibinder;
    @BindView(R.id.upload_footer)
    LinearLayout uploadFooter;




    private OnFragmentInteractionListener mListener;
    private float mScaleFactor = 1.0f;
    private Uri uri;
    private float initialWitdh;
    private float initialHeight;
    private File imgFile;
    private NavController navigationController;
    private PostViewModel postViewModel;
    private int currentPage;
    private PagedList<Post> pagedList;
    private ImageViewPagerAdapter adapter;
    private ImageDTO.mode mode;
    private Observer<PagedList<Post>> postObserver;

    public ImagePreview() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ImagePreview newInstance(Bundle args) {
        ImagePreview fragment = new ImagePreview();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);
            editable = getArguments().getBoolean(ARG_PARAM2);
            mode = (ImageDTO.mode)getArguments().getSerializable(ARG_PARAM3);
        }

        postViewModel = ViewModelProviders.of(getActivity()).get(PostViewModel.class);
        adapter = new ImageViewPagerAdapter(getChildFragmentManager(), pagedList);

        postObserver = new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable final PagedList<Post> pagedList) {
                ImagePreview.this.pagedList = pagedList;
                adapter.setPosts(ImagePreview.this.pagedList);
                imageViewer.setAdapter(adapter);
                imageViewer.setCurrentItem(position);
            }
        };
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        postViewModel.getPostList().observe(getActivity(), postObserver);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_upload_image_preview, container, false);
        uibinder = ButterKnife.bind(this, rootView);
        if(savedInstanceState != null &&
                savedInstanceState.containsKey("currentPage")){

            Log.d("currentPageReceived", String.valueOf(savedInstanceState.getInt("currentPage")));
            currentPage = savedInstanceState.getInt("currentPage");
        }
        imageViewer.addOnPageChangeListener(this);

        if(!editable){
            uploadFooter.setVisibility(View.GONE);
            crop.setVisibility(View.GONE);
        }





        uploadButton = (Button) rootView.findViewById(R.id.confirmUpload);


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                try {

                    if(uri!= null){
                        mListener.onCofirmUpload( new File(getPath(uri)));

                    }
                    else{
                        mListener.onCofirmUpload(new File(filePath));
                    }



                } catch (Exception exc) {
                    Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        cancelButton = (Button) rootView.findViewById(R.id.cancelUpload);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                mListener.onCancelUpload(filePath);*/
            }
        });


        //crop.setVisibility(View.GONE);



        crop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
/*                new Handler(Looper.myLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        myImage.invalidate();
                        Bitmap original = myImage.getVisibleRectangleBitmap();
                        createFile(original);
                        loadFile();

*//*                        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.UPLOAD_PREVIEW_FRAGMENT);
                        Fragment updatedFrg = ImagePreview.newInstance("7055553175", filePath, null,  true);
                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.detach(frg);
                        ft.attach(updatedFrg);
                        ft.commit();*//*
                    }
                });*/

            }
        });
        
        


        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEnterVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other com.example.bhanu.feedme.fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCofirmUpload(File path);
        void onCancelUpload(String path);
        void onBackPressed();
    }




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uibinder.unbind();
        postViewModel.getPostList().removeObserver(postObserver);
    }



    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");
                Uri imageUri = getImageUri(getContext(), selectedBitmap);
                Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.UPLOAD_PREVIEW_FRAGMENT);
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.commit();


                navigationController = Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment);

                Bundle bundle = new Bundle();
                bundle.putString("param1", "7055553175");
                bundle.putString("param2", imageUri.getPath());
                bundle.putParcelable("param3", imageUri);
                bundle.putBoolean("param4", true);

                navigationController.navigate(R.id.uploadImagePreview, bundle);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    class ImageViewPagerAdapter extends FragmentStatePagerAdapter{
        public void setPosts(PagedList<Post> posts) {
            this.posts = posts;
        }

        PagedList<Post> posts;
        public ImageViewPagerAdapter(FragmentManager fm, PagedList<Post> posts) {
            super(fm);
            this.posts = posts;
        }

        @Override
        public Fragment getItem(int i) {
            Bundle args = new Bundle();
            args.putInt("param1", i);
            args.putSerializable("param2", mode);
            return Image.newInstance(args);
        }

        @Override
        public int getCount() {
            if(mode == ImageDTO.mode.LOAD_FROM_SERVER){
                return posts.size();
            }
            return 1;
        }
    }

}
