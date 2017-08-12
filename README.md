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


* 货篮
    * 点击货物，添加至货篮（onEventMainThread）
    * 删除指定货物（确认对话框）
    * 清空
    * 暂存
    * 保存订单
    * 结账（现金，支付宝，微信，挂账，会员支付）






待解决
修改货物两个折扣，不能使用LSpinner, 要改为 EditText属性的View
登录成功保存结算的进制方式
登录成功时，保存是否支持wechatpay 和 alipay
当是总额逢1进或逢5进的时候，CartBean#getDiscountCost() 到底要不要只保留两位小数，多的直接舍去

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

