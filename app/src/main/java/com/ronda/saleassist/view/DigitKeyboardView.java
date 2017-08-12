package com.ronda.saleassist.view;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.ronda.saleassist.R;
import com.ronda.saleassist.base.MyApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 用法：mDigitKeyboardView.bindEditTextViews(getWindow(), mEtEmpNum, mEtPassword);
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/20
 * Version: v1.0
 */

public class DigitKeyboardView extends LinearLayout {
    private static final String TAG = DigitKeyboardView.class.getSimpleName();

    private EditText mFocusEditText;

    private StringBuilder digitnum = new StringBuilder();//内容
    private int limitLen = 20; //字符限制默认为20


    public DigitKeyboardView(Context context) {
        super(context);
    }


    public DigitKeyboardView(Context context, AttributeSet attrs) {// 这里的context 和 getContext()都是指Activity
        super(context, attrs);


        LayoutInflater.from(context).inflate(R.layout.customview_digit_keyboard, this, true);

        init(context, attrs);
    }

    public DigitKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {

        //覆盖构造函数获取attrs中声明的属性,并且将这些属性的值放入具体的属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DigitKeyboardView);
        float digitTextSize = ta.getDimension(R.styleable.DigitKeyboardView_digitTextSize, 14);
        int digitTextColor = ta.getColor(R.styleable.DigitKeyboardView_digitTextColor, Color.BLACK);
        Drawable digitBtndrawable = ta.getDrawable(R.styleable.DigitKeyboardView_digitBackground);
        ta.recycle();

        // 初始化 对象
        Button[] btns = new Button[11];
        btns[0] = (Button) findViewById(R.id.digitkeypad_0);
        btns[1] = (Button) findViewById(R.id.digitkeypad_1);
        btns[2] = (Button) findViewById(R.id.digitkeypad_2);
        btns[3] = (Button) findViewById(R.id.digitkeypad_3);
        btns[4] = (Button) findViewById(R.id.digitkeypad_4);
        btns[5] = (Button) findViewById(R.id.digitkeypad_5);
        btns[6] = (Button) findViewById(R.id.digitkeypad_6);
        btns[7] = (Button) findViewById(R.id.digitkeypad_7);
        btns[8] = (Button) findViewById(R.id.digitkeypad_8);
        btns[9] = (Button) findViewById(R.id.digitkeypad_9);
        btns[10] = (Button) findViewById(R.id.digitkeypad_point);
        ImageButton digitkeypad_c = (ImageButton) findViewById(R.id.digitkeypad_clear);


        // 添加点击事件
        DigitKeypadClickListener dkl = new DigitKeypadClickListener();
        for (Button btn : btns) {
            btn.setOnClickListener(dkl);
            // 自定义的属性
            btn.setTextColor(digitTextColor);
            btn.setTextSize(digitTextSize);
            if (digitBtndrawable != null)
                btn.setBackground(digitBtndrawable); //一旦这样设置，则Btn默认情况下背景是透明的,所以加上一个判断
        }
        digitkeypad_c.setOnClickListener(dkl);

    }


    /**
     * 用于关联哪些输入框
     *
     * @param editTexts
     */
    public void bindEditTextViews(final Window window, EditText... editTexts) {
        digitnum.setLength(0); // 清空

        OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (mOnMyFoucusChangeListener != null) {
                    mOnMyFoucusChangeListener.onFocusChange(v, hasFocus);
                }

                Log.d(TAG, "onFocusChange --> " + (v instanceof EditText) + ", " + hasFocus);

                if (v instanceof EditText && hasFocus) {
                    digitnum.setLength(0);
                    digitnum.append(((EditText) v).getText().toString());
                    mFocusEditText = (EditText) v;

                    //隐藏系统键盘
                    hideSoftInputMethod(window, (EditText) v);
                }
            }
        };

        for (EditText editText : editTexts) {
            editText.setOnFocusChangeListener(focusChangeListener);
        }
    }

    public void bindEditTextViews(Window window, int... resIds) {
        EditText[] editTexts = new EditText[resIds.length];
        for (int i = 0; i < resIds.length; i++) {
            editTexts[i] = (EditText) getRootView().findViewById(resIds[i]);
        }

        bindEditTextViews(window, editTexts);
    }

    // 隐藏系统键盘
    public static void hideSoftInputMethod(Window window, EditText ed) {
        //KLog.e("getContext: " + getContext());
        //当该View是Activity中的时候 getContext() 和 构造器中的context都是指Activity
        //但是当View位于DialogFragment时，getContext()就是ContextThemeWrapper， 里面没有getWindow()方法
        //((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        else if (context instanceof DialogFragment){
//
//        }


        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //数字键盘中各个按钮的点击事件
    private class DigitKeypadClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            if (mFocusEditText == null) {
                Toast.makeText(MyApplication.getInstance(), "未绑定EditText", Toast.LENGTH_SHORT).show();
                return;
            }

            //点击按钮震动效果，需要权限
            Vibrator vibrator = (Vibrator) mFocusEditText.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(30);//震动30ms

            // 当全选文本时， 要清空 digitnum （mFocusEditText要么是插入模式，要么是全选模式）
            if (mFocusEditText.getSelectionStart() != mFocusEditText.getSelectionEnd()) {
                digitnum.setLength(0);
            }

            switch (v.getId()) {
                case R.id.digitkeypad_1:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("1");
                    }
                    break;
                case R.id.digitkeypad_2:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("2");
                    }
                    break;
                case R.id.digitkeypad_3:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("3");
                    }
                    break;
                case R.id.digitkeypad_4:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("4");
                    }
                    break;
                case R.id.digitkeypad_5:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("5");
                    }
                    break;
                case R.id.digitkeypad_6:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("6");
                    }
                    break;
                case R.id.digitkeypad_7:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("7");
                    }
                    break;
                case R.id.digitkeypad_8:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("8");
                    }
                    break;
                case R.id.digitkeypad_9:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("9");
                    }
                    break;
                case R.id.digitkeypad_0:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append("0");
                    }

                    break;
                case R.id.digitkeypad_point:
                    if (digitnum.length() == limitLen) {
                        return;
                    } else {
                        digitnum.append(".");
                    }

                    break;
                case R.id.digitkeypad_clear:// 后退
                    if (digitnum.length() > 0) {
                        digitnum.setLength(digitnum.length() - 1); // 减掉一位
                    }
                    break;
            }

            Log.d(TAG, "onclick --> " + digitnum);


            if (digitnum.length() == 0) { // 回退键导致的空字符串
                mFocusEditText.setText(digitnum);
            } else if (isFloatTwo(digitnum.toString())) { // digitnum不为空，且为两位小数时，才把digitnum设置到EditText中显示.
                //if ("00".equals(digitnum.toString())) digitnum.replace(0, 2, "0");// 整数部分最多只有一个0

                mFocusEditText.setText(digitnum);
                mFocusEditText.setSelection(null != digitnum ? digitnum.length() : 0);//把光标位置定位到最后
            } else {// digitnum超过两位小数时
                // 若小数超过了两位则不进行设置，且去掉digitnum刚刚添加的那一位数字
                digitnum.setLength(digitnum.length() - 1);
            }

//            if (digitnum.length() == 0) { // 回退键导致的空字符串
//                mFocusEditText.setText(digitnum);
//            } else if (isDecimalTwo(digitnum.toString())) { // digitnum不为空，且为两位小数时，才把digitnum设置到EditText中显示.
//                //if ("00".equals(digitnum.toString())) digitnum.replace(0, 2, "0");// 整数部分最多只有一个0
//
//                mFocusEditText.setText(digitnum);
//                mFocusEditText.setSelection(null != digitnum ? digitnum.length() : 0);//把光标位置定位到最后
//            } else {// digitnum超过两位小数时
//                // 若小数超过了两位则不进行设置，且去掉digitnum刚刚添加的那一位数字
//                digitnum.setLength(digitnum.length() - 1);
//            }
        }
    }

    /**
     * 匹配两位小数的正则
     *
     * @param str
     * @return
     */
    private boolean isFloatTwo(String str) {
        return str.matches("^\\d*(\\.\\d{0,2})?$");
    }

    /**
     * 判断当前字符串中的小数部分是否超过了两位
     * 小数，最多两位小数,如：10.2, 0.25, 20
     *
     * @param str
     */
    private boolean isDecimalTwo(String str) {
        return str.matches("^[0-9]\\d*(\\.\\d{0,2})?|0\\.(0[1-9]?|[1-9]\\d?)?$");
    }

    /**
     * 设置最大可输入的长度
     *
     * @param limitLen
     */
    public void setLimitLen(int limitLen) {
        this.limitLen = limitLen;
    }

    /* OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                Log.d(TAG, "onFocusChange --> " + (v instanceof EditText) + ", " + hasFocus);

                if (v instanceof EditText && hasFocus) {
                    digitnum.setLength(0);
                    digitnum.append(((EditText) v).getText().toString());
                    mFocusEditText = (EditText) v;

                    //隐藏系统键盘
                    hideSoftInputMethod(window, (EditText) v);
                }
            }
        };*/


    private OnMyFoucusChangeListener mOnMyFoucusChangeListener;

    public void setOnMyFoucusChangeListener(OnMyFoucusChangeListener onMyFoucusChangeListener) {
        mOnMyFoucusChangeListener = onMyFoucusChangeListener;
    }

    /**
     * 自定义的 OnMyFoucusChangeListener 接口，避免被数字键盘绑定的EditText不能设置焦点改变事件
     */
    public interface OnMyFoucusChangeListener {
        void onFocusChange(View v, boolean hasFocus);
    }
}
