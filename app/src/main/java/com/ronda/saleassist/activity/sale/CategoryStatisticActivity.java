package com.ronda.saleassist.activity.sale;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.CategoryStatisticAdapter;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.CategoryStatisticBean;
import com.ronda.saleassist.bean.GoodsStatisticBean;
import com.ronda.saleassist.bean.OrderSurvey;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lrd on 0020,2016/6/20.
 */
public class CategoryStatisticActivity extends BaseActivty implements View.OnClickListener {

    private static int DAY;

    private LinearLayout llTimestamp;

    private RadioGroup radioGroup;
    private RadioButton rbDay, rbWeek, rbMonth; //三个radiobutton
    private Button btnDefine; //自定义
    private EditText etEndDate;

    private EditText etStartDate;
    private Button btnQuery;


    private RecyclerView rvCategory;
    private ArrayList<CategoryStatisticBean> mDatas = new ArrayList<>();
    private CategoryStatisticAdapter mAdapter;

    private Map<String, ArrayList<GoodsStatisticBean>> map = new HashMap<>();

    private RecyclerView.LayoutManager mLinearLayoutManager;
    private boolean isSpread = false;//是否展开


    private Bundle empBundle;// 表示员工的bundle, 包含uid，nick, 若bundle.size()==0,则表示调用店老板的报表接口，否则调用员工的报表接口.empBundle 最开始是由EmpManageActivity传入的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_statistic);

        empBundle = getIntent().getExtras();

        String title = "类别统计";
        if (empBundle.size() > 0) {
            title = empBundle.getString("nick") + "员工-类别统计";
        }
        initToolbar(title, true);


        initView();

        initEvent();
    }

    private void initView() {

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        rbDay = (RadioButton) findViewById(R.id.rb_cur_day);
        rbWeek = (RadioButton) findViewById(R.id.rb_week);
        rbMonth = (RadioButton) findViewById(R.id.rb_month);

        btnDefine = (Button) findViewById(R.id.btn_define);
        llTimestamp = (LinearLayout) findViewById(R.id.ll_timestamp);
        etStartDate = (EditText) findViewById(R.id.et_start_time);
        etEndDate = (EditText) findViewById(R.id.et_end_time);
        btnQuery = (Button) findViewById(R.id.btn_query);

        rvCategory = (RecyclerView) findViewById(R.id.recyler_view_category);

        mAdapter = new CategoryStatisticAdapter(this, mDatas);
        rvCategory.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvCategory.setLayoutManager(mLinearLayoutManager);
        rvCategory.setItemAnimator(new DefaultItemAnimator());
        rvCategory.setHasFixedSize(true);

    }

    private void initEvent() {

        //RadioButton的点击事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb_cur_day) {
                    DAY = 1;
                    //这里把getOrderSurvey放到了getCategoryStatistic()中了
                    //getOrderSurvey(DAY);
                    getCategoryStatistic(DAY, "");
                } else if (checkedId == R.id.rb_week) {
                    DAY = 7;
                    //getOrderSurvey(DAY);
                    getCategoryStatistic(DAY, "");
                } else if (checkedId == R.id.rb_month) {
                    DAY = 30;
                    //getOrderSurvey(DAY);
                    getCategoryStatistic(DAY, "");
                }
            }
        });
        rbDay.setChecked(true);//必须要写在监听方法后面


        //自定义按钮的点击事件
        btnDefine.setOnClickListener(this);
        btnQuery.setOnClickListener(this);

        etStartDate.setOnClickListener(dateListener);
        etEndDate.setOnClickListener(dateListener);


        //列表的item的点击事件
        mAdapter.setmOnItemClickListener(new CategoryStatisticAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CategoryStatisticActivity.this, GoodsStatisticActivity.class);
                intent.putExtra("day", DAY);
                String endtime = etEndDate.getText() == null ? "" : etEndDate.getText().toString();
                intent.putExtra("endtime", endtime); //这两个参数是为了给第三级获取货物详细信息时使用
//                intent.putExtra("categoryId", mDatas.get(position).getCategoryId());


                intent.putExtra("datas", map.get(mDatas.get(position).getCategoryId()));

                startActivity(intent);
            }


            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    private View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(CategoryStatisticActivity.this, new DatePickerDialog.OnDateSetListener() {
                //void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    int t = monthOfYear + 1;//monthOfYear表示的是0-11
                    String month = t < 10 ? "0" + t : t + "";
                    String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
                    String t_date = "" + year + "-" + month + "-" + day;
                    ((EditText) view).setText(t_date);

                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
        }
    };


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_define) {
            isSpread = !isSpread;
            if (isSpread) {
                llTimestamp.setVisibility(View.VISIBLE);
                btnDefine.setBackgroundResource(R.drawable.arrow_up);
            } else {
                llTimestamp.setVisibility(View.GONE);
                etStartDate.setText("");
                etEndDate.setText("");
                btnDefine.setBackgroundResource(R.drawable.arrow_down);
            }
        } else if (id == R.id.btn_query) {

            int date_t = getQueryDays(etStartDate.getText().toString(), etEndDate.getText().toString());
            if (date_t == 0) {
                Toast.makeText(CategoryStatisticActivity.this, "输入有误", Toast.LENGTH_SHORT).show();
                return;
            } else {
                DAY = date_t;
                getOrderSurvey(DAY);
                getCategoryStatistic(DAY, etEndDate.getText().toString());

            }
        }
    }

    private int getQueryDays(String startDate, String endDate) {
        SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = smdf.parse(startDate);
            Date end = smdf.parse(endDate);
            int t = (int) ((end.getTime() - start.getTime()) / (3600 * 24 * 1000)) + 1;//这里是要+1

            if (t <= 0) return 0;
            return t;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void getCategoryStatistic(int day, String endtime) {

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                KLog.json(responseStr);
                mDatas.clear();

                try {
                    JSONObject response = new JSONObject(responseStr);
                    int status = response.getInt("status");
                    String msg = response.getString("msg");

                    if (status == -9) {
//                        mDatas.clear();
                        Toast.makeText(CategoryStatisticActivity.this, "无记录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CategoryStatisticActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                    if (status == 1) { //当数据为空时，status虽然返回1,但是data的内容形式是："goods": []， 所以在获取goods这个json数组中的属性时，会出现异常,所以clear()和notifyDataSetChanged()的位置要注意
//                        mDatas.clear();
                        map.clear();
                        setOrderSurveyView(new OrderSurvey("0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"));

                        JSONObject data = response.getJSONObject("data");

                        JSONArray goodcategory = data.getJSONArray("goodcategory");//只要status==1，data就不为空，这里的goodcategory必定也不为空
                        JSONObject goods = data.getJSONObject("goods");

                        // mDatas.clear();
                        for (int i = 0; i < goodcategory.length(); i++) {
                            String categoryId = goodcategory.getJSONObject(i).getString("goodcategory");
                            String name = goodcategory.getJSONObject(i).getString("name");
                            String goodnum = goodcategory.getJSONObject(i).getString("goodnum");//销售量
                            String income = goodcategory.getJSONObject(i).getString("income");//销售收入
                            String businessnum = goodcategory.getJSONObject(i).getString("businessnum");//笔数（订单）

                            mDatas.add(new CategoryStatisticBean(categoryId, name, goodnum, income, businessnum));

                            JSONArray arr = goods.getJSONArray(categoryId);
                            ArrayList<GoodsStatisticBean> goodDatas = new ArrayList<GoodsStatisticBean>();
                            for (int j = 0; j < arr.length(); j++) {
                                String goodid_t = arr.getJSONObject(j).getString("goodid");
                                String businessnum_t = arr.getJSONObject(j).getString("businessnum");
                                String income_t = arr.getJSONObject(j).getString("income");
                                String goodnum_t = arr.getJSONObject(j).getString("goodnum");
                                String name_t = arr.getJSONObject(j).getString("name");
                                goodDatas.add(new GoodsStatisticBean(goodid_t, name_t, goodnum_t, income_t, businessnum_t));
                            }

                            map.put(categoryId, goodDatas);

                        }
                        //mAdapter.notifyDataSetChanged();
                    }
                    //当请求列表中的数据成功时，在发起交易笔数等统计信息的请求，把这两个并行的请求变成一个串行的请求
                    //这样可以加入cancelAll()方法，避免频繁切换单选按钮而导致数据紊乱
                    getOrderSurvey(DAY);

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(R.string.no_respnose);
            }
        };

        if (empBundle.size() > 0) {// 店员报表的接口
            UserApi.getMemberOrderInfoGoods(TAG, token,shopId, empBundle.getString("uid"), day + "", "", "", endtime, listener, errorListener);
        } else { //说明是店老板的报表
            UserApi.getOrderInfoGoods(TAG, token, shopId, day + "", "", "", endtime, listener, errorListener);
        }

    }


    //获取线上、线下的总体的交易信息（笔数，总收入，均价）
    public void getOrderSurvey(int day) {

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                KLog.json(responseStr);

                try {
                    JSONObject response = new JSONObject(responseStr);
                    int status = response.getInt("status");
                    String msg = response.getString("msg");

                    if (status == -9) {//其实这里的无记录信息，应该后台处理好才对（这里是获取失败和无记录重合了）
                        Toast.makeText(CategoryStatisticActivity.this, "无数据", Toast.LENGTH_SHORT).show();
                    }

                    if (status == 1) {
                        JSONObject data = response.getJSONObject("data");
                        OrderSurvey survey = new Gson().fromJson(data.toString(), OrderSurvey.class);

//                        setButtonTextViewValue(count, turnover, avgturnover, cash_count, cash_turnover, cash_avgturnover, web_count, web_turnover, web_avgturnover);
                        setOrderSurveyView(survey);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(R.string.no_respnose);
            }
        };



        if (empBundle.size() > 0){ // 员工报表
            UserApi.getMemberOrderSurvey(TAG, token, shopId, empBundle.getString("uid"), day + "", listener, errorListener);
        }
        else { // 老板报表
            UserApi.getOrderSurvey(TAG, token, shopId, day + "",  listener, errorListener);
        }
    }

    //设置概览的显示信息
    private void setOrderSurveyView(OrderSurvey survey) {

        //总计
        ((TextView)findViewById(R.id.count)).setText(survey.getCount());
        ((TextView)findViewById(R.id.turnover)).setText(survey.getTurnover());
        ((TextView)findViewById(R.id.avgturnover)).setText(survey.getAvgturnover());

        //现金
        ((TextView)findViewById(R.id.cash_count)).setText(survey.getCash_count());
        ((TextView)findViewById(R.id.cash_turnover)).setText(survey.getCash_turnover());
        ((TextView)findViewById(R.id.cash_avgturnover)).setText(survey.getCash_avgturnover());

        //支付宝
        ((TextView)findViewById(R.id.web_count)).setText(survey.getWeb_count());
        ((TextView)findViewById(R.id.web_turnover)).setText(survey.getWeb_turnover());
        ((TextView)findViewById(R.id.web_avgturnover)).setText(survey.getWeb_avgturnover());

        //现金(未结)
        ((TextView)findViewById(R.id.laterpay_count)).setText(survey.getLaterpay_count());
        ((TextView)findViewById(R.id.laterpay_turnover)).setText(survey.getLaterpay_turnover());
        ((TextView)findViewById(R.id.laterpay_avgturnover)).setText(survey.getLaterpay_avgturnover());

        //现金(已结)
        ((TextView)findViewById(R.id.laterpay_complete_count)).setText(survey.getLaterpay_complete_count());
        ((TextView)findViewById(R.id.laterpay_complete_turnover)).setText(survey.getLaterpay_complete_turnover());
        ((TextView)findViewById(R.id.laterpay_complete_avgturnover)).setText(survey.getLaterpay_complete_avgturnover());

        //会员支付
        ((TextView)findViewById(R.id.vippay_count)).setText(survey.getVippay_count());
        ((TextView)findViewById(R.id.vippay_turnover)).setText(survey.getVippay_turnover());
        ((TextView)findViewById(R.id.vippay_avgturnover)).setText(survey.getVippay_avgturnover());
    }


    private void drawChart() {
        String[] titles = new String[]{"Crete"};
        List<double[]> x = new ArrayList<double[]>();
        for (int i = 0; i < titles.length; i++) {
            x.add(new double[]{1, 2, 3, 4, 5});//每个序列中点的X坐标
        }
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[]{10, 20, 50, 26, 15});//序列1中点的y坐标
        int[] colors = new int[]{Color.BLUE};
        PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);//调用AbstractDemoChart中的方法设置renderer.
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);//设置图上的点为实心
        }
        setChartSettings(renderer, "成绩排名趋势", "学期", "名次", 0.5, 5.5, 0, 50, Color.LTGRAY,
                Color.LTGRAY);//调用AbstractDemoChart中的方法设置图表的renderer属性.
        renderer.setChartTitleTextSize(50);
        renderer.setXLabels(5);//设置x轴显示5个点,根据setChartSettings的最大值和最小值自动计算点的间隔
        renderer.setYLabels(5);//设置y轴显示5个点,根据setChartSettings的最大值和最小值自动计算点的间隔
        renderer.setShowGrid(true);//是否显示网格
        renderer.setXLabelsAlign(Paint.Align.RIGHT);//刻度线与刻度标注之间的相对位置关系
        renderer.setYLabelsAlign(Paint.Align.CENTER);//刻度线与刻度标注之间的相对位置关系
        renderer.setZoomButtonsVisible(true);//是否显示放大缩小按钮
        renderer.setXLabels(0);
        for (int i = 0; i < 5; i++) {
            renderer.addXTextLabel(i + 1, "label:" + i + 1);
        }
        renderer.setPanLimits(new double[]{-10, 20, -10, 40}); //设置拖动时X轴Y轴允许的最大值最小值.
        renderer.setZoomLimits(new double[]{-10, 20, -10, 40});//设置放大缩小时X轴Y轴允许的最大最小值.

        GraphicalView chartView = ChartFactory.getLineChartView(this, buildDataset(titles, x, values), renderer);
//        FrameLayout layout = (FrameLayout) findViewById(R.id.fl_chart);
//        layout.removeAllViews();
//        layout.addView(chartView,
//                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 构建 XYMultipleSeriesDataset.
     *
     * @param titles  每个序列的图例
     * @param xValues X轴的坐标
     * @param yValues Y轴的坐标
     * @return XYMultipleSeriesDataset
     */
    protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues, List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        addXYSeries(dataset, titles, xValues, yValues, 0);
        return dataset;
    }

    //向DataSet中添加序列.
    public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
                            List<double[]> yValues, int scale) {
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i], scale); //这里注意与TimeSeries区别.
            double[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
    }

    /**
     * 构建XYMultipleSeriesRenderer.
     *
     * @param colors 每个序列的颜色
     * @param styles 每个序列点的类型(可设置三角,圆点,菱形,方块等多种)
     * @return XYMultipleSeriesRenderer
     */
    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        setRenderer(renderer, colors, styles);
        return renderer;
    }

    protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
        //整个图表属性设置
        //-->start
        renderer.setAxisTitleTextSize(16);//设置轴标题文字的大小
        renderer.setChartTitleTextSize(40);//设置整个图表标题文字的大小
        renderer.setLabelsTextSize(15);//设置轴刻度文字的大小
        renderer.setLegendTextSize(15);//设置图例文字大小
        renderer.setPointSize(5f);//设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
        renderer.setMargins(new int[]{20, 30, 15, 20});//设置图表的外边框(上/左/下/右)
        //-->end

        //以下代码设置没个序列的颜色.
        //-->start
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);//设置颜色
            r.setPointStyle(styles[i]);
            renderer.addSeriesRenderer(r);
        }
        //-->end
    }

    /**
     * 设置renderer的一些属性.
     *
     * @param renderer    要设置的renderer
     * @param title       图表标题
     * @param xTitle      X轴标题
     * @param yTitle      Y轴标题
     * @param xMin        X轴最小值
     * @param xMax        X轴最大值
     * @param yMin        Y轴最小值
     * @param yMax        Y轴最大值
     * @param axesColor   X轴颜色
     * @param labelsColor Y轴颜色
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle,
                                    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }
}
