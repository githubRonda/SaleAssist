# 秤心（7寸屏）

## 启动界面
* 最少3s的显示时间
* token登录(mobile + token)

## 登录
* mobile + password
* mobile + code

todo 待保存的信息可能还需要在看看

## 注册
mobile + password + confirmPassword + nickname + code

## 主界面(MainActivity)
* 初始化
    * 初始化分类View, 加载分类数据(加载过程中，禁用下拉刷新)
    * 初始化货物View --> 数据为空时，不会自动回调 onLoadMoreRequested()。 等分类数据加载成功之后，再加载货物数据

* 分类列表
    * 数据项可以点击（切换类别）， 可以长按（修改，删除）
    * footerView （仅能点击）添加分类

* 货物列表
    * 当分类列表数据加载完成之后，才加载货物数据
    * 下拉刷新（加载当前分类下的第一页数据）
    * 上拉加载更多（当加载下一页数据成功之后，页码才自加1，以表示当前页码）
    * 注意：pageCount 表示当前页码，加载更多成功之后，才自加1
    * 当“下拉刷新”的过程中，禁用“上拉加载更多”； 反之亦然。
    * 数据项可以点击（选择货物）， 可以长按（修改，删除）[todo] [修改折扣待优化]
    * footerView （仅能点击）添加货物
    * 点击货物省略掉了确认对话框
    * 添加金额（额外项）
    * 排序功能
    * 货物显示样式(品名 / 价格) [todo]
    * 货物显示需要一个PLU编号,需要后台增加一个属性 [非必须]
    * 暂时把折扣和计件的标识给隐藏了


* 货篮
    * 点击货物，添加至货篮（onEventMainThread）
    * 删除指定货物（确认对话框）
    * 清空
    * 暂存
    * 保存订单
    * 结账（现金，支付宝，微信，挂账，会员支付）


* 硬件相关功能
    * USB扫码 --> 绑定到MainActivity，一直执行，当监听到条码时，自动获取对应货物，添加至货篮

* 浮窗
    * 参考群图片
    * 注意：总计的显示--> 先显示小计，过一会就显示总计






待解决
修改货物两个折扣，不能使用LSpinner, 要改为 EditText属性的View
当是总额逢1进或逢5进的时候，CartBean#getDiscountCost() 到底要不要只保留两位小数，多的直接舍去

指令

浮框 --> 参考群图片
    注意：总计的显示--> 先显示小计，过一会就显示总计

模糊查询
    数码管显示在确定一下





待优化
USB扫码 可以当成一个服务（这样只能是无障碍服务） 一直启动， 当检测到条码值时，就自动获取对应货物，添加至货篮
使用提取出来的LeftView， 降低 CartFragment 类的复杂度


No adapter attached; skipping layout

GoodsAdapter中的最后一个添加项margin失效

竟然不会自动调用 onLoadMoreRequested() 回调

现在突然感觉不需要上拉加载更多，下拉刷新这么复杂的逻辑。直接一次性把数据给加载完毕即可（反正数据也不算多）

有时间把 DropEditText 给优化一下



选择图片还可以在优化（参考毕设）

Activity.startActivityForResult(intent, requestCode)  --> 只会回调此Activity中的 onActivityResult(requestCode, resultCode, Intent) 方法
Fragment.startActivityForResult(intent, requestCode)  --> 既会回调此Fragment中的 onActivityResult() 方法，也会回调宿主Activity中的 onActivityResult() 方法，不过一般不使用宿主Activity中的回调，因为其requestCode是错误的(值始终是65536)
一句话：谁启动的，就在谁内部进行回调


翻页
http://blog.csdn.net/u014165119/article/details/46834265









Frame中BaseActivity中DialogFactory




    private void checkUpdate() {
         /*
            1. 点击检查更新按钮
            2. 显示获取新版本的加载进度对话框，并且向后台发送请求来获取版本信息（若网络较好的话，这个加载进度对话框就看不出来，其实这个对话框也可以不要，只不过是为了网络慢是的友好性交互）
            3. 当请求结束时，关闭对话框。
                 若请求成功，比较版本是否相同？若不同，则弹出对话框，提示有新版本；若相同，则Toast信息
                 若请求失败，Toast信息
            4. 若选择下载新版本，则访问后台进行下载，并显示下载进度对话框
            5. 下载完毕后，关闭对话框，
                 若下载成功，则跳转到安装界面
                 若下载失败，则Toast信息
             */
        checkDialog = new ProgressDialog(this);
        checkDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        checkDialog.setMessage("正在获取新版本");
        checkDialog.setCancelable(false);
        checkDialog.show();

        new AsyncTask<String, Void, String>() { //获取后台数据，需要异步操作（开启一个新的线程）
            @Override
            protected String doInBackground(String... params) {

                BufferedReader br = null;
                StringBuilder sb = new StringBuilder();
                try {
                    URL url = new URL(params[0]);
                    URLConnection connection = url.openConnection(); //获取连接
                    InputStream is = connection.getInputStream(); //获取字节输入流
                    InputStreamReader isr = new InputStreamReader(is, "utf-8"); //把字节输入流转成字符输入流
                    br = new BufferedReader(isr);

                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return sb.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                checkDialog.cancel();
                parseXMLStr(s);
            }
        }.execute("http://soft.edianlai.com/cxmm/update/update.xml");
    }


    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    private void parseXMLStr(String responseStr) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            //parser.setInput(new StringReader(xmlStr));
            parser.setInput(new ByteArrayInputStream(responseStr.getBytes()), "UTF-8");
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:// 开始元素事件
                        String name = parser.getName();

                        if (name.equalsIgnoreCase("version")) {
                            version = parser.nextText(); // 如果后面是Text节点,即返回它的值
                        } else if (name.equalsIgnoreCase("description")) {
                            description = parser.nextText();
                        } else if (name.equalsIgnoreCase("url")) {
                            url = parser.nextText();
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //KLog.i("version:"+version+", description:"+description+", url:"+url);

        if (!version.equals(getVersion())) { //不相等，即有新版本
            showUpdateDialog();
        } else {
            Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("提示");
        builder.setMessage("发现新版本，是否更新？");
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//若sd卡可用
                    //downloadFile();
                } else {
                    Toast.makeText(SettingActivity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }



    //安装文件，一般固定写法
    private void install() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "chengxin.apk")),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

