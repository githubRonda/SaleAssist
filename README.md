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
    * 数据项可以点击（切换类别）， 可以长按（修改，删除）[todo]
    * footerView （仅能点击）添加分类 [todo]

* 货物列表
    * 当分类列表数据加载完成之后，才加载货物数据
    * 下拉刷新（加载当前分类下的第一页数据）
    * 上拉加载更多（当加载下一页数据成功之后，页码才自加1，以表示当前页码）
    * 注意：pageCount 表示当前页码，加载更多成功之后，才自加1
    * 当“下拉刷新”的过程中，禁用“上拉加载更多”； 反之亦然。
    * 数据项可以点击（选择货物）， 可以长按（修改，删除）[todo]
    * footerView （仅能点击）添加货物 [todo]







No adapter attached; skipping layout

GoodsAdapter中的最后一个添加项margin失效

竟然不会自动调用 onLoadMoreRequested() 回调

现在突然感觉不需要上拉加载更多，下拉刷新这么复杂的逻辑。直接一次性把数据给加载完毕即可（反正数据也不算多）


翻页
http://blog.csdn.net/u014165119/article/details/46834265









Frame中BaseActivity中DialogFactory

