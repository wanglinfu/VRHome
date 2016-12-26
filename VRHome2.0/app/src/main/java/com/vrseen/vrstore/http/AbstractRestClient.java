package com.vrseen.vrstore.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.vrseen.vrstore.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jiangs on 16/4/27.
 */
public abstract class AbstractRestClient {
    public static final String PAGE_SIZE_STR = "40";
    public static final int PAGE_SIZE = 40;
    private static final String TAG = "AbstractRestClient";
    protected final int METHOD_GET = 0;
    protected final int METHOD_POST = 1;
    protected final int METHOD_DELETE = 2;
    protected AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    public AbstractRestClient() {
        client.setTimeout(60000);
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public void get(String url, RequestParams params,
                    AsyncHttpResponseHandler responseHandler) {

        if (!StringUtils.isHttp(url)) {
            url = getBaseUrl() + url;
        }

        if (params == null) {
            Log.d(TAG, "get " + url);
        } else {
            Log.d(TAG, "get " + url + "?" + params);
        }
        client.get(url, params, responseHandler);
    }

    /**
     * delete请求
     *
     * @param url
     * @param responseHandler
     */
    public void delete(String url,RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (!StringUtils.isHttp(url)) {
            url = getBaseUrl() + url;
        }
        if (params == null) {
            Log.d(TAG, "delete " + url);
        } else {
            Log.d(TAG, "delete " + url + "?" + params);
        }
        client.delete(url,params,  responseHandler);
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public void post(String url, RequestParams params,
                     AsyncHttpResponseHandler responseHandler) {


        if (!StringUtils.isHttp(url)) {
            url = getBaseUrl() + url;
        }
        if (params == null) {
            Log.d(TAG, "post " + url);
        } else {
            Log.d(TAG, "post " + url + "?" + params);
        }
        client.post(url, params, responseHandler);
    }

    /**
     * post请求
     *
     * @param url
     * @param params
     * @param responseHandler
     */
//    public void postWithNewUrl(String url, RequestParams params,
//                     AsyncHttpResponseHandler responseHandler) {
//
//
//        if (!StringUtils.isHttp(url)) {
//            url = getBaseUrl() + url;
//        }
//        if (params == null) {
//            Log.d(TAG, "post " + url);
//        } else {
//            Log.d(TAG, "post " + url + "?" + params);
//        }
//        client.post(url, params, responseHandler);
//    }

    /**
     * 执行POST请求，不解析结果
     *
     * @param url
     * @param params
     * @param callback
     */
    public void post(String url, RequestParams params,
                     final ResponseCallBack callback) {
        doRequest(METHOD_POST, url, params, callback);
    }

    /**
     * put请求
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public void put(String url, RequestParams params,
                     AsyncHttpResponseHandler responseHandler) {

        if (!StringUtils.isHttp(url)) {
            url = getBaseUrl() + url;
        }
        if (params == null) {
            Log.d(TAG, "put " + url);
        } else {
            Log.d(TAG, "put " + url + "?" + params);
        }
        client.put(url, params, responseHandler);
    }


    /**
     * 获取对象，通过JsonConvertable转化为对象，放在Response的model里面
     *
     * @param method   0=get，1=post
     * @param url
     * @param params
     * @param clazz
     * @param callback
     */
    protected void requestObject(final Context context,
                                 final int method, final String url,
                                 final RequestParams params,
                                 final Class<? extends JsonConvertable> clazz,
                                 final ResponseCallBack callback) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                Log.d(TAG, "requestObject<" + clazz.getSimpleName() + "> success. resp=" + response.toString());
                Response resp = Response.fromJson(statusCode, response);

                try {
                    if (resp.getData() != null && resp.getData() instanceof JSONObject) {
                        JSONObject obj = (JSONObject) resp.getData();
                        //调用传进来的类的fromjson方法
                        resp.setModel(clazz.newInstance().fromJson(context, obj));
                        callback.onSuccess(resp);
                    } else {
                        Log.e(TAG, "Object not found.");
                        callback.onFailure(resp, null);
                    }
                } catch (InstantiationException e) {
                    Log.e(TAG, "create Object failed.", e);
                    callback.onFailure(resp, e);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "create Object failed.", e);
                    callback.onFailure(resp, e);
                } catch (JSONException jsonException) {
                    Log.e(TAG, "create Object failed.", jsonException);
                    callback.onFailure(resp, jsonException);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                try {
                    Log.d(TAG, "requestObject<" + clazz.getSimpleName() + "> failed.", throwable);
                    callback.onFailure(Response.fromJson(statusCode, errorResponse),
                            throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                try {
                    Log.d(TAG, "requestObject<" + clazz.getSimpleName() + "> failed.", throwable);
                    callback.onFailure(null, throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        if (method == METHOD_GET) {
            get(url, params, handler);
        } else if (method == METHOD_POST) {
            post(url, params, handler);
        }
    }


    protected List<Object> parseList(final Context context, JSONArray jsonArray,
                                     final Class<? extends JsonConvertable> clazz) throws Exception {
        int length = jsonArray.length();
        List<Object> items = new ArrayList<>(length);

        try {
            for (int i = 0; i < length; i++) {
                JsonConvertable item = clazz.newInstance();
                JSONObject json = jsonArray.optJSONObject(i);
                item = item.fromJson(context, json);
                items.add(item);
            }
        } catch (Exception e) {
            Log.e(TAG, "create Object failed.", e);
            throw e;
        }
        return items;
    }


    /**
     * 创建返回结果
     *
     * @param objectList
     * @return
     */
    protected ListResult<Object> createListResult(List<Object> objectList) {
        ListResult<Object> result = new ListResult<Object>();
        result.setList(objectList);
//        result.setHasMore(result.getList().size() > 7);
        result.setHasMore(result.getList().size() != 0 &&
                ((result.getList().size() % AbstractRestClient.PAGE_SIZE) == 0));
//        result.setHasMore(PAGE_SIZE <= result.getTotal());
        //获取公告栏notice信息不能指定每页数量。。所以判断是10的整数倍都返回true。 //TODO
//        result.setHasMore(result.isHasMore() || (!result.getList().isEmpty() && result.getList().size() % 10 == 0));
        return result;
    }

    /**
     * 获取对象列表，通过JsonConvertable转化为对象，最后将ListResult放在Response的model里面
     *
     * @param method   0=get，1=post
     * @param url
     * @param params
     * @param clazz
     * @param callback
     */
    protected void requestObjectList(final Context context,
                                     final int method, final String url,
                                     final RequestParams params,
                                     final Class<? extends JsonConvertable> clazz,
                                     final ResponseCallBack callback) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                Log.d(TAG, "requestObjectList<" + clazz.getSimpleName() + "> from URL<" + url + "> success. resp=" + response.toString());
                Response resp = Response.fromJson(statusCode, response);
                if (resp.getData() != null) {
                    //总数
                    try {
                        JSONArray data;
                        if (resp.getData() instanceof JSONArray) {
                            data = (JSONArray) resp.getData();
                        } else {
                            JSONObject object = (JSONObject) resp.getData();
                            String dataName = url.substring(url.lastIndexOf('/') + 1);
                            data = object.getJSONArray(dataName);
                        }
                        ListResult<Object> result = createListResult(parseList(context, data, clazz));
                        result.setTotal(response.optInt("count", -1));
                        result.setNewOrderNum(response.optInt("newOrderNum", 0));
                        resp.setModel(result);
                        callback.onSuccess(resp);
                    } catch (Exception e) {
                        Log.e(TAG, "process error.", e);
                        callback.onFailure(resp, e);
                    }

                } else {
                    callback.onFailure(resp, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "requestObjectList<" + clazz.getSimpleName() + "> from URL<" + url + "> failed.", throwable);
                callback.onFailure(Response.fromJson(statusCode, errorResponse),
                        throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Log.d(TAG, "requestObjectList<" + clazz.getSimpleName() + "> from URL<" + url + "> failed.", throwable);
                callback.onFailure(null, throwable);
            }
        };
        if (method == METHOD_GET) {
            get(url, params, handler);
        } else if (method == METHOD_POST) {
            post(url, params, handler);
        }
    }

    /**
     * 获取对象列表，通过JsonConvertable转化为对象，最后将ListResult放在Response的model里面
     *
     * @param method   0=get，1=post
     * @param url
     * @param params
     * @param clazz
     * @param callback
     */
    protected void requestObjectList(final Context context, final String key,
                                     final int method, final String url,
                                     final RequestParams params,
                                     final Class<? extends JsonConvertable> clazz,
                                     final ResponseCallBack callback) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                Log.d(TAG, "requestObjectList<" + clazz.getSimpleName() + "> from URL<" + url + "> success. resp=" + response.toString());
                Response resp = Response.fromJson(statusCode, response);
                if (resp.getData() != null) {
                    //总数
                    try {
                        JSONArray data;
                        if (resp.getData() instanceof JSONArray) {
                            data = (JSONArray) resp.getData();
                        } else {
                            JSONObject object = (JSONObject) resp.getData();
                            String dataName = url.substring(url.lastIndexOf('/') + 1);
                            data = object.getJSONArray(key);
                        }
                        ListResult<Object> result = createListResult(parseList(context, data, clazz));
                        result.setTotal(response.optInt("count", -1));
                        result.setNewOrderNum(response.optInt("newOrderNum", 0));
                        resp.setModel(result);
                        callback.onSuccess(resp);
                    } catch (Exception e) {
                        Log.e(TAG, "process error.", e);
                        callback.onFailure(resp, e);
                    }

                } else {
                    callback.onFailure(resp, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "requestObjectList<" + clazz.getSimpleName() + "> from URL<" + url + "> failed.", throwable);
                callback.onFailure(Response.fromJson(statusCode, errorResponse),
                        throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Log.d(TAG, "requestObjectList<" + clazz.getSimpleName() + "> from URL<" + url + "> failed.", throwable);
                callback.onFailure(null, throwable);
            }
        };
        if (method == METHOD_GET) {
            get(url, params, handler);
        } else if (method == METHOD_POST) {
            post(url, params, handler);
        }
    }


    /**
     * 执行请求，不解析结果
     *
     * @param method   0=get，1=post
     * @param url
     * @param params
     * @param callback
     */
    public void doRequest(final int method, final String url, RequestParams params,
                          final ResponseCallBack callback) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                Log.d(TAG, "REQUEST [" + url + "] SUCCESS, RESPONSE=" + response.toString());
                if (callback != null) {
                    Response resp = Response.fromJson(statusCode, response);
                    try {
                        callback.onSuccess(resp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "REQUEST [" + url + "] FAILED", throwable);
                if (callback != null) {
                    callback.onFailure(Response.fromJson(statusCode, errorResponse), throwable);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                Log.d(TAG, "REQUEST [" + url + "] FAILED", throwable);
                if (callback != null) {
                    callback.onFailure(null, throwable);
                }
            }
        };
        if (method == METHOD_GET) {
            get(url, params, handler);
        } else if (method == METHOD_POST) {
            post(url, params, handler);
        } else if (method == METHOD_DELETE) {
            delete(url,params, handler);
        }
    }

    /**
     * 直接获取字符串的返回
     *
     * @param url      请求链接
     * @param params   参数
     * @param callback 回调
     */
    public void doRequest(final int method, String url, RequestParams params, final StringResponseCallback callback) {
        TextHttpResponseHandler textHttpResponseHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.onSuccess(responseString);
            }
        };
        if (method == METHOD_GET) {
            get(url, params, textHttpResponseHandler);
        } else {
            post(url, params, textHttpResponseHandler);
        }
    }

    /**
     * 执行GET请求，不解析结果
     *
     * @param url
     * @param params
     * @param callback
     */
    public void get(String url, RequestParams params,
                    final ResponseCallBack callback) {
        doRequest(METHOD_GET, url, params, callback);
    }

    /**
     * 获取基础url
     *
     * @return
     */
    protected abstract String getBaseUrl();

    /**
     * 简单的消息回调
     */
    public interface ResponseCallBack {
        void onFailure(Response resp, Throwable e);

        void onSuccess(Response resp) throws JSONException;

    }

    /**
     * 字符串类型的回调
     */
    public interface StringResponseCallback {
        void onFailure(String resp, Throwable e);

        void onSuccess(String resp);
    }
}
