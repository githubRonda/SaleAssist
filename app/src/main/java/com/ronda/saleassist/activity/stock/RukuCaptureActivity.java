/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ronda.saleassist.activity.stock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.zxing.Result;
import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.bean.BarcodeInfoBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.other.zxing.activity.CaptureActivity;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public class RukuCaptureActivity extends CaptureActivity {

    private static final String TAG = RukuCaptureActivity.class.getSimpleName();

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    @Override
    public void setContent() {
        setContentView(R.layout.activity_capture);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //quit the scan view
        findViewById(R.id.btn_cancel_scan).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RukuCaptureActivity.this.finish();
            }
        });
    }

    @Override
    public void handleDecode(Result rawResult, Bundle bundle) {
        super.handleDecode(rawResult, bundle);


        /*
        Intent resultIntent = new Intent();
        bundle.putInt("width", mCropRect.width());
        bundle.putInt("height", mCropRect.height());
        bundle.putString("result", rawResult.getText());
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
        */

        final String resultString = rawResult.getText();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("正在处理，请稍后");
        pd.setCanceledOnTouchOutside(false); //设置进度对话框不能用回退按钮关闭

        pd.show();

        if (resultString.equals("")) {
            Toast.makeText(RukuCaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {

//            Toast.makeText(CaptureActivity.this, resultString, Toast.LENGTH_SHORT).show();
            UserApi.bargoodsInfoAndCreate(TAG, token, shopId, resultString,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responseStr) {
                            KLog.json(responseStr);

                            try {
                                JSONObject response = new JSONObject(responseStr);
                                int status = response.getInt("status");
                                String msg = response.getString("msg");

                                Toast.makeText(RukuCaptureActivity.this, msg, Toast.LENGTH_SHORT).show();

                                if (status == 1) {

                                    JSONObject data = response.getJSONObject("data");
                                    String name = data.getString("name");
                                    String barcode = data.getString("barcode");
                                    String price = data.getString("price");
                                    String goodid = data.getString("goodid");


                                    showDialog(new BarcodeInfoBean(name, barcode, price, goodid), resultString, "");
                                } else if (status == -20) {
                                    showDialog(null, resultString, "1");
                                }
                                else{
                                    new AlertDialog.Builder(RukuCaptureActivity.this).setTitle("提示").setMessage(msg).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //持续扫描
                                            continuePreview();
                                        }
                                    }).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();

                                new AlertDialog.Builder(RukuCaptureActivity.this).setTitle("提示").setMessage("数据解析有误").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //持续扫描
                                        continuePreview();
                                    }
                                }).show();
                            }
                            finally {
                                pd.dismiss(); //放在后面可以避免出现两个对话框切换时有明显的间隔现象
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            new AlertDialog.Builder(RukuCaptureActivity.this).setTitle("提示").setMessage(R.string.no_respnose).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //持续扫描
                                    continuePreview();
                                }
                            }).show();

                            pd.dismiss();
                        }
                    });


        }




    }

    private void showDialog(final BarcodeInfoBean bean, final String barcode, final String newFlag) {
        final AlertDialog barcodeInfoDialog = new AlertDialog.Builder(this).create();
        barcodeInfoDialog.show();

        barcodeInfoDialog.getWindow().setContentView(R.layout.dialog_code_good_info);
        barcodeInfoDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //barcodeInfoDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        barcodeInfoDialog.setCanceledOnTouchOutside(false);


        final TextView tv_new = (TextView) barcodeInfoDialog.getWindow().findViewById(R.id.tv_new);

        final EditText edit_name = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_name);
        final EditText edit_purchase_price = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_purchase_price);//进价
        final EditText edit_number = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_num);
        final EditText edit_purchase_total_cost = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_cost_in);//根据数量和进价算出的总花费
        final EditText edit_selling_price1 = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_selling_price1);
        final EditText edit_selling_price2 = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_selling_price2);

        final EditText edit_discount2 = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_discount2);
        final EditText edit_discount3 = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_discount3);

        final EditText edit_num = (EditText) barcodeInfoDialog.getWindow().findViewById(R.id.edit_num);

        ImageButton btn_sub_num = (ImageButton) barcodeInfoDialog.getWindow().findViewById(R.id.btn_sub_num);
        ImageButton btn_add_num = (ImageButton) barcodeInfoDialog.getWindow().findViewById(R.id.btn_add_num);

        ImageButton btn_add1 = (ImageButton) barcodeInfoDialog.getWindow().findViewById(R.id.btn_add1);
        ImageButton btn_add2 = (ImageButton) barcodeInfoDialog.getWindow().findViewById(R.id.btn_add2);
        ImageButton btn_sub1 = (ImageButton) barcodeInfoDialog.getWindow().findViewById(R.id.btn_sub1);
        ImageButton btn_sub2 = (ImageButton) barcodeInfoDialog.getWindow().findViewById(R.id.btn_sub2);

        Button btn_confirm = (Button) barcodeInfoDialog.getWindow().findViewById(R.id.btn_confirm);
        Button btn_cancel = (Button) barcodeInfoDialog.getWindow().findViewById(R.id.btn_cancel);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.btn_add1) {
                    String value = edit_selling_price1.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int++;
                    edit_selling_price1.setText(value_int + "");
                } else if (id == R.id.btn_sub1) {
                    String value = edit_selling_price1.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    --value_int;
                    if (value_int >= 0) {
                        edit_selling_price1.setText(value_int + "");
                    }
                } else if (id == R.id.btn_add2) {
                    String value = edit_selling_price2.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int = value_int + 10;
                    if (value_int >= 99) {
                        value_int = 0;
                    }
                    edit_selling_price2.setText(value_int + "");
                } else if (id == R.id.btn_sub2) {
                    String value = edit_selling_price2.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "0";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int = value_int - 10;
                    if (value_int < 0) {
                        value_int = 90;
                    }
                    edit_selling_price2.setText(value_int + "");
                } else if (id == R.id.btn_sub_num) {
                    String value = edit_num.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "1";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int--;
                    if (value_int >= 1) {
                        edit_num.setText(value_int + "");
                    }
                } else if (id == R.id.btn_add_num) {
                    String value = edit_num.getText().toString().trim();
                    if (value == null || value.isEmpty()) {
                        value = "1";
                    }
                    int value_int = Integer.parseInt(value);
                    value_int++;
                    edit_num.setText(value_int + "");
                }
            }
        };

        btn_add1.setOnClickListener(listener);
        btn_sub1.setOnClickListener(listener);
        btn_add2.setOnClickListener(listener);
        btn_sub2.setOnClickListener(listener);

        btn_add_num.setOnClickListener(listener);
        btn_sub_num.setOnClickListener(listener);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String price_t = edit_purchase_price.getText().toString().trim();

                    //设置默认售价(把售价的初始值设为进价)
                    String sell_price = price_t;
                    String[] strArr = sell_price.split("\\.");//正则中"."有特殊意义，需要转义
                    if (strArr.length == 1) {
                        if(strArr[0].isEmpty())
                            edit_selling_price1.setText("0");
                        else
                            edit_selling_price1.setText(strArr[0]);
                        edit_selling_price2.setText("0");
                    } else if (strArr.length == 2) {
                        edit_selling_price1.setText(strArr[0]);
                        edit_selling_price2.setText(strArr[1].length() > 2 ? strArr[1].substring(0, 2) : strArr[1]);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(RukuCaptureActivity.this, "输入有误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edit_purchase_price.addTextChangedListener(textWatcher);

        if (bean != null){
            //设置name
            edit_name.setText(bean.getName());
            //设置默认售价
            final String sell_price = bean.getPrice();
            String[] strArr = sell_price.split("\\.");//正则中"."有特殊意义，需要转义
            if (strArr.length == 1) {
                edit_selling_price1.setText(strArr[0]);
                edit_selling_price2.setText("0");
            } else if (strArr.length == 2) {
                edit_selling_price1.setText(strArr[0]);
                edit_selling_price2.setText(strArr[1].length() > 2 ? strArr[1].substring(0, 2) : strArr[1]);
            }
        }
        else{
            tv_new.setVisibility(View.VISIBLE);//显示新建的提示信息
        }



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeInfoDialog.dismiss();
                //持续扫描
                continuePreview();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String number = edit_number.getText().toString().trim();
                String goods = "";
                if(bean!=null) goods = bean.getGoodid();

                String name = edit_name.getText().toString().trim();
                String purchase_price = edit_purchase_price.getText().toString().trim();//进价
                String selling_price_t = edit_selling_price1.getText().toString() + "." + edit_selling_price2.getText().toString();//售价
                String discount2 = edit_discount2.getText().toString().trim();
                String discount3 = edit_discount3.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(RukuCaptureActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (purchase_price.isEmpty()) {
                    Toast.makeText(RukuCaptureActivity.this, "进价不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (number.isEmpty()) {
                    Toast.makeText(RukuCaptureActivity.this, "数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.parseDouble(selling_price_t) == 0) {
                    Toast.makeText(RukuCaptureActivity.this, "售价不能为0", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserApi.bargoodsInSet(TAG, token, shopId, barcode, number, newFlag, goods, name, number, purchase_price, selling_price_t, discount2, discount3, "",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseStr) {
                                KLog.json(responseStr);

                                try {
                                    JSONObject response = new JSONObject(responseStr);
                                    int status = response.getInt("status");
                                    String msg = response.getString("msg");

                                    Toast.makeText(RukuCaptureActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ToastUtils.showToast(R.string.no_respnose);
                            }
                        });

                //关闭对话框
                barcodeInfoDialog.dismiss();
                //持续扫描
                continuePreview();

            }
        });
    }
}