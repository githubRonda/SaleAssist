package com.ronda.saleassist.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lrd on 0019,2016/8/19.
 */
public class LocationActivity extends CheckPermissionsActivity implements View.OnClickListener, AMapLocationListener {

    private static final String TAG = LocationActivity.class.getSimpleName();

    //开始定位
    public final static int MSG_LOCATION_START = 0;
    //定位完成
    public final static int MSG_LOCATION_FINISH = 1;
    //停止定位
    public final static int MSG_LOCATION_STOP = 2;

    private ImageButton ibBack;
    private TextView titleLabel;

    private Button btnLocation, btnUpload;
    private TextView tvResult;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;


    private double longitude; //经度
    private double latitude; //纬度
    private String province; //城市
    private String city; //城市
    private String adCode;  //区域码
    private String address; //地址


    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);


        setContentView(R.layout.activity_location);

        initView();

        btnLocation.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        ibBack.setOnClickListener(this);

        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = new AMapLocationClientOption();
        //设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位监听
        locationClient.setLocationListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyUtil.getInstance().cancelPendingRequests(TAG);
    }

    private void initView() {
        titleLabel = (TextView) findViewById(R.id.tv_title);
        titleLabel.setText("位置共享");

        ibBack = (ImageButton) findViewById(R.id.ib_back);
        btnLocation = (Button) findViewById(R.id.btn_location);
        btnUpload = (Button) findViewById(R.id.btn_upload);

        tvResult = (TextView) findViewById(R.id.tv_result);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_back){
            finish();
        }
        else if (id == R.id.btn_location){

            if (btnLocation.getText().equals("开始定位")){
                initOption();
                btnLocation.setText("停止定位");

                //设置定位参数
                locationClient.setLocationOption(locationOption);
                //启动定位
                locationClient.startLocation();
                mHandler.sendEmptyMessage(MSG_LOCATION_START);
            }
            else {
                btnLocation.setText("开始定位");

                //停止定位
                locationClient.stopLocation();
                mHandler.sendEmptyMessage(MSG_LOCATION_STOP);
            }
        }
        else if (id == R.id.btn_upload){

            UserApi.uploadLocation(TAG, token, shopId, province, city, adCode, address, longitude, latitude,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responseStr) {
                            KLog.json(responseStr);

                            try {
                                JSONObject response = new JSONObject(responseStr);
                                int status = response.getInt("status");
                                String msg = response.getString("msg");
                                Toast.makeText(LocationActivity.this, msg, Toast.LENGTH_SHORT).show();

                                if (status == 1) {
                                    btnUpload.setEnabled(false);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ToastUtils.showToast("上传失败");
                        }
                    });
        }
    }

    //设置定位参数
    private void initOption(){
        //设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        //设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
        //设置是否等待设备wifi刷新，如果为true，会自动变为单次定位
        //locationOption.setOnceLocationLatest(false);
        locationOption.setOnceLocationLatest(true);
        //设置发送定位请求的时间间隔，最小值为1000，若小于1000，按照1000算
        //只有持续定位时，定位间隔才有效，单次定位无效
        //locationOption.setInterval(1000);
    }

    Handler mHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOCATION_START:
                    tvResult.setText("正在定位...");
                    break;
                case MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = getLocationStr(loc);
                    tvResult.setText(result);
                    break;
                case MSG_LOCATION_STOP:
                    tvResult.setText("定位停止");
                    break;
            }
        }
    };

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation){
            Message msg = mHandler.obtainMessage();
            msg.obj = aMapLocation;
            msg.what = MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }


    private synchronized String getLocationStr(AMapLocation location) {
        if (null == location) return null;

        StringBuffer sb = new StringBuffer();

        //errCode等于0表示定位成功，其他的为定位失败
        if (location.getErrorCode() == 0) {

            btnUpload.setEnabled(true);

            longitude = location.getLongitude();
            latitude = location.getLatitude();
            province = location.getProvince();
            city = location.getCity();
            adCode = location.getAdCode();
            address =  location.getAddress();

            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");


            sb.append("经    度    : " + longitude + "\n");
            sb.append("纬    度    : " + latitude + "\n");

            // 提供者是GPS时是没有以下信息的
            sb.append("省份 : " + province + "\n");
            sb.append("城市 : " + city + "\n");
            sb.append("区域 码   : " + adCode + "\n");
            sb.append("地    址    : " + address + "\n");

        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }

        return sb.toString();
    }
}