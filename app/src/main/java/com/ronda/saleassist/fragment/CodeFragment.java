package com.ronda.saleassist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ronda.saleassist.R;
import com.ronda.saleassist.activity.stock.RukuCaptureActivity;
import com.ronda.saleassist.base.BaseFragment;


/**
 * 条码类入库的Fragment
 * Created by lrd on 0014,2016/9/14.
 */
public class CodeFragment extends BaseFragment {


    private Button scanning;		//点击扫描按钮
//    private TextView result;		//扫描显示对象

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code, container, false);


        return view;
    }

    @Override
    public void init(View view) {
        scanning = (Button) view.findViewById(R.id.btn_scanning);
        scanning.setOnClickListener(listener);				//注册按钮的点击事件

//        result = (TextView) view.findViewById(R.id.result);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_scanning:
//                    Toast.makeText(getActivity(), "scanning", Toast.LENGTH_SHORT).show();
                    scanning();
                    break;
            }
        }
    };

    /**
     * 转跳到扫描页面
     *
     */
    private void scanning(){
        Intent intent = new Intent(getActivity(),RukuCaptureActivity.class);		//CaptureActivity是扫描的Activity类
        startActivityForResult(intent, 0);							//当前扫描完条码或二维码后,会回调当前类的onActivityResult方法,
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){	//判断回调
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");			//这就获取了扫描的内容了
            //result.setText(scanResult);
            Toast.makeText(getActivity(), scanResult, Toast.LENGTH_SHORT).show();
        }
    }
}
