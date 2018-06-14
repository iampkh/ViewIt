package com.noname.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noname.demo.Listeners.JsonRequest;
import com.noname.demo.Listeners.RequestQueueSingleton;
import com.noname.demo.util.Constants;
import com.noname.demo.util.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,OnItemClickListener{

    private EditText mSearchBox;
    private Button mSearchImageButton;
    private RecyclerView mSearchedImageList;
    private ViewGroup mSearchContainer;
    private SearchHelper mSearchHelper;


    private GridLayoutManager mGridLayoutManager = null;
    private RecyclerViewDataAdapter mRecyclerViewAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mSearchBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    displayImageFromWeb();
                    return true;
                }
                return false;
            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }

     private void initRecyclerView(){
        mGridLayoutManager = new GridLayoutManager(this.getApplicationContext(), 2);
        // Set layout manager.
        mSearchedImageList.setLayoutManager(mGridLayoutManager);
        mRecyclerViewAdapter = new RecyclerViewDataAdapter(this);
        mSearchedImageList.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void init(){
        //UI initialization
        mSearchBox = (EditText)findViewById(R.id.searchBox);
        mSearchedImageList = (RecyclerView)findViewById(R.id.list_recycle_view);
        mSearchImageButton = (Button)findViewById(R.id.searchBtn);
        mSearchContainer = (LinearLayout)findViewById(R.id.search_container);

        mSearchBox.setOnClickListener(this);
        mSearchImageButton.setOnClickListener(this);

        //Object initialization
        mSearchHelper = SearchHelper.getInstance();
    }
    private void destroy(){
        mSearchBox = null;
        mSearchedImageList = null;
        mSearchImageButton = null;
        mSearchContainer = null;
    }

    private void handleSearchBarAnimation(){
        if(mSearchImageButton.getVisibility() == GONE) {
            TransitionManager.beginDelayedTransition(mSearchContainer);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) mSearchContainer.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL);
            mSearchContainer.setLayoutParams(layoutParams);

            mSearchImageButton.setVisibility(View.VISIBLE);
            mSearchedImageList.setVisibility(View.VISIBLE);
        }
    }

    private void displayImageFromWeb(){
        /**
         * close the software keyboard
         */
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);

        String string = mSearchBox.getText().toString();
        JsonRequest jsonRequest = new JsonRequest(mSearchHelper.getUrlString(string), new JsonRequest.IJsonResponse() {
            @Override
            public Object onResultReceived(String json) {
                //This api call runs in worker thread
                Logger.dLog(json);
                mSearchHelper.setInput(json);
                return mSearchHelper.getImageHolder();
            }

            @Override
            public void onUiUpdate(Object object) {
                //This api call runs in Main thread
                //Object in param is returned from onResultReceived

                initRecyclerView();
            }
        });

        RequestQueueSingleton instance = RequestQueueSingleton.getInstance(getApplicationContext());
        instance.addToRequestQueue(jsonRequest.getStringRequest());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchBox:
                handleSearchBarAnimation();
                break;
            case R.id.searchBtn:
                displayImageFromWeb();
                break;
            default:
        }

    }

    /**
     * item click listener for recyler view
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Intent intent = Gallery.getIntent(this);
        intent.putExtra(Gallery.KEY_POSITION,position);
        startActivity(intent);
    }


    class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerViewDataAdapter.ImageRecyclerViewHolder> {
        OnItemClickListener mRecyclerViewClickListner;

        private RecyclerViewDataAdapter(){

        }
        RecyclerViewDataAdapter(OnItemClickListener listener){
            mRecyclerViewClickListner = listener;
        }

        @Override
        public ImageRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View cardView = layoutInflater.inflate(R.layout.image_holder_thumb, parent, false);

            return new ImageRecyclerViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(final ImageRecyclerViewHolder holder, int position) {
            ImageHolder imageHolder = mSearchHelper.getImageHolder().get(position);
            holder.progressBar.setVisibility(View.VISIBLE);

            Logger.dLog("Total list position="+position);
            Picasso.with(holder.imageView.getContext())
                    .load(imageHolder.previewImages)
                    .fit().centerCrop()
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        @Override
        public int getItemCount() {
            Logger.dLog("Total list count="+mSearchHelper.getImageHolder().size());
            return mSearchHelper.getImageHolder().size();
        }


        class ImageRecyclerViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            ProgressBar progressBar;
            public ImageRecyclerViewHolder(final View itemView) {
                super(itemView);
                if(itemView != null) {
                    imageView = itemView.findViewById(R.id.item_thumb);
                    progressBar = itemView.findViewById(R.id.progressBar);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mRecyclerViewClickListner != null){
                                int pos = mSearchedImageList.getChildAdapterPosition(itemView);
                                mRecyclerViewClickListner.onItemClick(pos);
                            }
                        }
                    });
                }
            }
        }
    }
}
