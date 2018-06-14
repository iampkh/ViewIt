package com.noname.demo.Listeners;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.noname.demo.ErrorHandler;

import java.util.List;
import java.util.logging.Logger;

public class JsonRequest {

    /**
     * Interface to send the json response
     */
    public interface IJsonResponse {
        /**
         * This interface will always run in worker thread
         * thus should not call any UI component from this.
         * @param json
         */
        public Object onResultReceived(String json);

        /**
         * Object processed in onResultReceived will be
         * provided to onUiUpdate as a param,
         * This callback runs in UI thread, thus should not
         * perform long running oprations
         * @param object
         */
        public void onUiUpdate(Object object);
    }

    /**
     * String request from server which posted in queue for response
     */
    private StringRequest mStringRequest = null;
    private JsonRequest(){}
    public JsonRequest(String url,IJsonResponse response) {
        com.noname.demo.util.Logger.eLog(url);
        JsonResponseListener responseListener = new JsonResponseListener();
        responseListener.setResponseListener(response);
        mStringRequest = new StringRequest(Request.Method.GET, url, responseListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ErrorHandler.sendErrorData(error.getMessage());
                    }
                });
    }

    /**
     * Set tag to string request to identify the request in the queue
     * @param tag
     */
    public void setTag(String tag){
        mStringRequest.setTag(tag != null ? tag : JsonRequest.class.getSimpleName());
    }

    /**
     * Get the builded string request.
     * @return
     */
    public StringRequest getStringRequest() {
        return mStringRequest;
    }
    private class JsonResponseListener implements Response.Listener<String>{
        /**
         * JsonResponse listener notifcation from different thread.
         */
        private FetchJsonAsyncTask mFetchJsonAsyncTask;

        JsonResponseListener(){
            mFetchJsonAsyncTask = new FetchJsonAsyncTask();
        }
        public void setResponseListener(IJsonResponse response){
            mFetchJsonAsyncTask.setJsonResponseListener(response);
        }
        @Override
        public void onResponse(String response) {
            if(mFetchJsonAsyncTask != null ) {
                mFetchJsonAsyncTask.execute(response);
            }
        }
    }

    /**
     * Asyntask to implement background implementation after receiving the response
     * (eg) parsing json, creating mapping list..etc.
     */
    private class FetchJsonAsyncTask extends AsyncTask<String,Void,Object> {
        IJsonResponse mJsonResponse;

        /**
         * register for listener
         * @param jsonResponse
         */
        void setJsonResponseListener(IJsonResponse jsonResponse){
            mJsonResponse = jsonResponse;
        }
        @Override
        protected Object doInBackground(String... strings) {
            if (mJsonResponse !=null) {
               return mJsonResponse.onResultReceived(strings[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            if (mJsonResponse !=null) {
                mJsonResponse.onUiUpdate(object);
            }
        }
    }
}
