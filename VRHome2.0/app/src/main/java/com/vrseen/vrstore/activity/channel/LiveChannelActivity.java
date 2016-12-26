package com.vrseen.vrstore.activity.channel;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.vrseen.vrstore.R;
import com.vrseen.vrstore.VRHomeConfig;
import com.vrseen.vrstore.activity.BaseActivity;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.model.panorama.PanoramaDetailData;
import com.vrseen.vrstore.util.CommonUtils;
import com.vrseen.vrstore.util.SPFConstant;
import com.vrseen.vrstore.util.SharedPreferencesUtils;
import com.vrseen.vrstore.util.StringUtils;
import com.vrseen.vrstore.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * live VR直播
 * Created by mll on 2016/6/27.
 */
public class LiveChannelActivity extends BaseActivity  {

    private WebView _webView;
    //private ProgressRelativeLayout _progressLayout;
    private String _type;
    private String _url;
    private EditText _edText;
    private static final String LIVE_LINK = "liveLink";

    private String _link = "";

    public static void actionStart(Context context,String url)
    {
        Intent intent = new Intent(context,LiveChannelActivity.class);
        intent.putExtra(LIVE_LINK,url);
        CommonUtils.startActivityWithAnim(context,intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setPageName("LiveChannelActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_channel);

        //_progressLayout = (ProgressRelativeLayout)findViewById(R.id.progress_layout);

        findViewById(R.id.view_back).setOnClickListener(this);
        findViewById(R.id.textConfig).setOnClickListener(this);

        _edText = (EditText) findViewById(R.id.edit_website);
        _webView = (WebView) findViewById(R.id.webview_liveChannel);

        _link = this.getIntent().getStringExtra(LIVE_LINK);
        _webView.getSettings().setJavaScriptEnabled(true);
        _webView.getSettings().setDefaultTextEncodingName("utf-8");
        _webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            _webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        if(StringUtils.isNotBlank(_link))
        {
            _webView.loadUrl(_link);
        }

        _webView.addJavascriptInterface(new JavaScriptLogic(this),"JavaScriptInterface");

        _webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view,String url)
            {
                sendInfoToJs();
            }
        });

        _webView.setWebChromeClient(new WebChromeClient());
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

       final String [] stringArr = this.getResources().getStringArray(R.array.channels_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,stringArr);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              if(position ==0)
              {
                  _type = "1";
              }
              else
              {
                  _type = "2";
              }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //在java中调用js代码
    public void sendInfoToJs() {
        String token = (String) SharedPreferencesUtils.getParam(this,
                SPFConstant.KEY_USER_TOKEN, "");
        token = token.split(" ")[1];
        _webView.loadUrl("javascript:sendToken1('"+token+"')");
    }

    @Override
    public void onClick(View v) {

         switch ( v.getId() )
         {
             case R.id.view_back:
                 this.finish();
                 break;
             case R.id.textConfig:
                 _url = _edText.getText().toString();
                 handlerData(_url,_type);
                 break;
             default:break;

         }
    }

    public void handlerData(String url,String type)
    {
        if(StringUtils.isHttp(url) ==false)
        {
            ToastUtils.showShort(this,R.string.no_http_url_error);
            return;
        }

        if(type.equals("1"))
        {
            PanoramaDetailData.PanoramaDetail vo = new PanoramaDetailData.PanoramaDetail();
            vo.setId(-1);
            vo.setStoragepath(url);
            U3dMediaPlayerLogic.getInstance().startPlayPanorama(this,vo, VRHomeConfig.TYPE_VR,false, VRHomeConfig.VR_PANOVIDEO_ID);
        }
        else if(url.equals("2"))
        {
            U3dMediaPlayerLogic.getInstance().startPlayFilm(this, _url,
                    VRHomeConfig.TYPE_2D, getString(R.string.channel_live),0,-2,0);
        }
    }

    private class JavaScriptLogic {

        private Context _context;
        private String _type = "" ;
        private String _url = "" ;

        public JavaScriptLogic(Context context) {
            _context = context;
        }

        /**
         * 调用vr
         * @param jsonStr {type,url}
         * @return
         */
        @JavascriptInterface
        public void startLiveVR(final String jsonStr)
        {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);

                if(jsonObject.has("type"))
                {
                    _type = jsonObject.getString("type");
                }
                if (jsonObject.has("url"))
                {
                    _url = jsonObject.getString("url");
                }

                if(StringUtils.isNotBlank(_type) && StringUtils.isNotBlank(_url)) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ((LiveChannelActivity)_context).handlerData(_url,_type);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
