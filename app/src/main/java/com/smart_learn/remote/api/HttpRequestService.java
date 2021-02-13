package com.smart_learn.remote.api;

import android.util.Log;

import com.smart_learn.config.CurrentConfig;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.core.utilities.Logs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.ParametersAreNonnullByDefault;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/** Singleton class */
public final class HttpRequestService {

    private static HttpRequestService httpRequestServiceInstance = null;

    /** client for HTTP requests */
    OkHttpClient okHttpClient;

    public final String METHOD_GET = "GET";
    public final String METHOD_POST = "POST";

    // info codes
    public static final int NO_REQUEST_MADE = 0;

    public static final int REQUEST_SUCCESS = 1;
    // TODO: create more specific messages on success and error for every end point
    //  if  several requests for different api`s are made in parallel in several threads problems
    //  can occur if messages are the same
    public static final int API_SERVER_REQUEST_ERROR = 2;
    public static final int PUBLIC_IP_REQUEST_ERROR = 3;


    private HttpRequestService() {
        okHttpClient = new OkHttpClient();
    }

    public static HttpRequestService getHttpRequestServiceInstance(){
        if(httpRequestServiceInstance == null){
            httpRequestServiceInstance = new HttpRequestService();
        }
        return httpRequestServiceInstance;
    }


    /** Helper to compose url when query parameters are added :
     * @param format 1 for the following format server/api_end_point?param1=value1&param2=value2....
     *               2 for the following format server/api_end_point/value_param_1/value_param_2....
     *
     *               for other value the initial url will be returned
     *  */
    private String addUrlParameters(final byte format, String url, final Map<String,String> urlParams){
        AtomicReference<String> params = new AtomicReference<>("");
        final String symbol;
        switch (format){
            case 1:
                symbol = "?";
                urlParams.forEach((key, value) -> {
                    params.set(params + key + "=" + value + "&");
                });
                // remove last "&"
                params.set(params.get().substring(0,params.get().length() - 1));
                break;
            case 2:
                symbol = "/";
                urlParams.forEach((key, value) -> {
                    params.set(params + "/" + value);
                });
                // remove first "/"
                params.set(params.get().substring(1));
                break;
            default:
                Log.e(Logs.UNEXPECTED_ERROR,"No correct format [" + format + "] for addUrlParameters");
                return url;
        }

        if(!params.get().isEmpty()){
            url += symbol + params;
        }

        return url;
    }


    /** helper to create request */
    private void sendRequestOkHttp3(final String url, final int errorCode, final String method,
                                    final okhttp3.Request request, final ApiRequestCallback apiCallback){

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            @ParametersAreNonnullByDefault
            public void onFailure(Call call, IOException error) {
                call.cancel();

                CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(errorCode);
                Log.e(Logs.API_REQ_ERROR_TAG, "Response Failed on send " + method +
                        "  request on url [" + url + "]  [" + error + "]");
            }

            @Override
            @ParametersAreNonnullByDefault
            public void onResponse(Call call, Response response) {
                String responseBody;
                try {
                    assert response.body() != null;
                    responseBody = response.body().string();
                } catch (IOException e) {
                    Log.e(Logs.UNEXPECTED_ERROR, " response.body().string() from request on url [" +
                            url + "] NOT done " + e);
                    e.printStackTrace();
                    return;
                }

                Log.i(Logs.API_REQ_SUCCESS_TAG, method + " request to url [" + url +
                        "] was done successfully");
                Log.i(Logs.API_REQ_RESPONSE_TAG, GeneralUtilities.stringToPrettyJson(responseBody));
                CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(REQUEST_SUCCESS);

                apiCallback.onSuccess(responseBody);
            }

        });

    }


    /** Send GET HTTP request using okhttp3 library WITHOUT url additional params.
     *
     * Obs: This method does not support request body because okhttp3 does not support this for
     *      GET method.
     * */
    public void sendGetRequestOkHttp3(String url, final int errorCode,final Map<String,String> headers,
                                      final ApiRequestCallback apiCallback) {
        sendGetRequestOkHttp3(url,(byte) 0,false, errorCode, new HashMap<>(), headers, apiCallback);
    }


    /** Send GET HTTP request using okhttp3 library WITH url additional params.
     *
     * Obs: This method does not support request body because okhttp3 does not support this for
     *      GET method.
     * */
    public void sendGetRequestOkHttp3(String url, final byte urlTypeFormat, final boolean formatUrl,
                                      final int errorCode, final Map<String,String> urlParams,
                                      final Map<String,String> headers, final ApiRequestCallback apiCallback){

        /*
        // check for internet connection
        if(NetworkUtilities.notGoodConnection()){
            CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(errorCode);
            return;
        }
         */

        if(formatUrl){
            // create url using url params
            url = addUrlParameters(urlTypeFormat,url,urlParams);
        }

        Log.i(Logs.INFO, METHOD_GET + " request to url [" + url + "]");
        CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(NO_REQUEST_MADE);

        // build request
        //https://stackoverflow.com/questions/41648687/cannot-resolve-symbol-builder-android
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .build();

        // send request
        sendRequestOkHttp3(url,errorCode,METHOD_GET,request,apiCallback);
    }

    /** Send POST HTTP request using okhttp3 library WITHOUT url additional params */
    public void sendPostRequestOkHttp3(String url, final int errorCode, final Map<String,String> headers,
                                       final String requestBody, final ApiRequestCallback apiCallback) {

        sendPostRequestOkHttp3(url,(byte) 0,false, errorCode, new HashMap<>(),
                headers, requestBody, apiCallback);
    }

    /** Send POST HTTP request using okhttp3 library WITH url additional params */
    public void sendPostRequestOkHttp3(String url, final byte urlTypeFormat, final boolean formatUrl,
                                       final int errorCode, final Map<String,String> urlParams,
                                       final Map<String,String> headers, final String requestBody,
                                       final ApiRequestCallback apiCallback){

        /*
        // check for internet connection
        if(NetworkUtilities.notGoodConnection()){
            CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(errorCode);
            return;
        }
         */

        if (formatUrl){
            // create url using url params
            url = addUrlParameters(urlTypeFormat,url,urlParams);
        }

        Log.i(Logs.INFO, METHOD_POST + " request to url [" + url + "]");
        CurrentConfig.getCurrentConfigInstance().requestErrorCode.set(NO_REQUEST_MADE);

        // build request
        //https://stackoverflow.com/questions/41648687/cannot-resolve-symbol-builder-android
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody))
                .build();

        // send request
        sendRequestOkHttp3(url,errorCode,METHOD_POST,request,apiCallback);
    }

}

