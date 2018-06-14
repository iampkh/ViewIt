package com.noname.demo;

import com.noname.demo.util.Constants;
import com.noname.demo.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class .which helps to show serach UI and image slider
 */
public class SearchHelper {
    private static SearchHelper mInstance;
    private List<ImageHolder> mImageHolderList;
    //default is set to food
    private String mCurrentKey = "food";

    /**
     * single instance object retreiver method
     * @return
     */
    public static synchronized SearchHelper getInstance() {
        if (mInstance == null) {
            mInstance = new SearchHelper();
        }
        return mInstance;
    }

    /**
     * This method forms url string from the given key
     * @param key
     * @return
     */
    public String getUrlString(String key){
        key = key.replace(" ","+");
        if(!mCurrentKey.equalsIgnoreCase(key)){
            mCurrentKey = key;
        }
        return Constants.URL+mCurrentKey+Constants.URL_PARAM;
    }

    /**
     * current displayed url
     * @return
     */
    public String getCurrentUrl(){
        return Constants.URL+mCurrentKey+Constants.URL_PARAM;
    }

    /**
     * set the input as json format
     * This method internally parses the input generates output
     * as Map format.
     *
     * This makes the thread to wait, so dont call from UI thread.
     * @param json
     */
    public void setInput(String json) {
        try {
            mImageHolderList = new ArrayList<>();
            JSONObject obj = new JSONObject(json);
            JSONArray hits = obj.getJSONArray(Constants.HITS_JSON);
            int totalHits = hits.length();

            for(int i=0; i < totalHits ; i++){
                JSONObject hit = hits.getJSONObject(i);
                String prevUrl = hit.getString(Constants.PREV_IMAGES_JSON);
                String actUrl = hit.getString(Constants.ACTUAL_IMAGES_JSON);
                Logger.eLog("prevUrl=" + prevUrl);
                Logger.eLog("actualUrl=" + actUrl);

                ImageHolder holder = new ImageHolder();
                holder.setActualImages(actUrl);
                holder.setPreviewImages(prevUrl);
                holder.setId(i);

                mImageHolderList.add(holder);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Image holder to returned from json string
     * @return
     */
    public List<ImageHolder> getImageHolder(){
        if(mImageHolderList == null){
            mImageHolderList = new ArrayList<>();
        }
        return mImageHolderList;
    }

}
