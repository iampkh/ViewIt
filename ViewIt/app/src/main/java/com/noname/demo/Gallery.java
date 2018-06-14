package com.noname.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.noname.demo.util.Constants;
import com.pkh.eazyview.lazyviewreplica.EazyView;
import com.pkh.eazyview.util.EazyViewUtil;
import com.pkh.eazyview.util.OptionView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class Gallery extends AppCompatActivity {

    private ViewPager mViewPager;
    private SearchHelper mSearchHelper;
    private CustomPagerAdapter mCustomPagerAdapter;

    public static final String KEY_POSITION = "key_position";

    /**
     * Intent to launch Gallery
     * @param context
     * @return
     */
    public static Intent getIntent(Context context){
        Intent intent = new Intent(context,Gallery.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_preview);
        init();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        int currentPos = intent.getIntExtra(KEY_POSITION,0);

        if(mViewPager != null) {
            mViewPager.setAdapter(mCustomPagerAdapter);
            mViewPager.setCurrentItem(currentPos);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }

    public void init(){
        mViewPager = (ViewPager)findViewById(R.id.imagePager);
        mSearchHelper = SearchHelper.getInstance();

        mCustomPagerAdapter = new CustomPagerAdapter(this);
    }

    public void destroy(){
        mViewPager = null;
    }

    private void addEazyView(){
       // EazyView eazyView = new EazyView(new Eazy);

        ArrayList optionList=new ArrayList();
        OptionView option1=new OptionView(getApplicationContext());
        option1.setOptionHolderBackgroundColor(Color.BLUE);
        option1.setOptionHolderBorderColor(Color.BLACK);
        option1.setQuodrant(EazyViewUtil.QUODRANT.ONE);
        option1.setImage(R.drawable.ic_menu_manage);
        option1.setAlpha(100);

        optionList.add(option1);

        OptionView option2=new OptionView(getApplicationContext());
        option2.setOptionHolderBackgroundColor(Color.BLUE);
        option2.setOptionHolderBorderColor(Color.BLACK);
        option2.setQuodrant(EazyViewUtil.QUODRANT.ONE);
        option2.setImage(R.drawable.ic_menu_slideshow);
        option2.setAlpha(100);

        optionList.add(option2);

        OptionView option3=new OptionView(getApplicationContext());
        option3.setOptionHolderBackgroundColor(Color.BLUE);
        option3.setOptionHolderBorderColor(Color.BLACK);
        option3.setQuodrant(EazyViewUtil.QUODRANT.ONE);
        option3.setImage(R.drawable.ic_menu_share);
        option3.setAlpha(100);

        optionList.add(option3);
    }


    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        Transformation mPicassoTransformation;
        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            mPicassoTransformation = new Transformation() {

                @Override
                public Bitmap transform(Bitmap source) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int targetWidth = size.x;

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View itemView = mLayoutInflater.inflate(R.layout.image_holder_preview, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_preivew);
            final ProgressBar progressBar = itemView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            ImageHolder imageHolder = mSearchHelper.getImageHolder().get(position);

            Picasso.with(imageView.getContext())
                    .load(imageHolder.actualImages)
                    .transform(mPicassoTransformation)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
                    //.into(imageView);

            container.addView(itemView);

            return itemView;
        }
        @Override
        public int getCount() {
            return mSearchHelper.getImageHolder().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((CardView) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((CardView) object);
        }
    }
}
