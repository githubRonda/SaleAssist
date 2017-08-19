package com.ronda.saleassist;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ronda.saleassist.local.sqlite.GreenDaoHelper;
import com.ronda.saleassist.local.sqlite.table.SimpleGoodsBean;
import com.ronda.saleassist.local.sqlite.table.SimpleGoodsBeanDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.ronda.saleassist", appContext.getPackageName());


        SimpleGoodsBeanDao dao = GreenDaoHelper.getDaoSession().getSimpleGoodsBeanDao();
        //批量插入的话，就直接使用一个 SimpleGoodsBean 对象吧
        dao.insert(new SimpleGoodsBean(null, "10001", "白菜", "1.12"));
        dao.insert(new SimpleGoodsBean(null, "10002", "西红柿", "2.12"));
        dao.insert(new SimpleGoodsBean(null, "10003", "苹果", "1.56"));




    }


    @Test
    public void query() throws Exception {
        SimpleGoodsBeanDao dao = GreenDaoHelper.getDaoSession().getSimpleGoodsBeanDao();
        List<SimpleGoodsBean> list = dao.queryBuilder().where(SimpleGoodsBeanDao.Properties.Price.like("1.13%")).list();
        System.out.println(list);
    }

    @Test
    public void delete() throws Exception {
        SimpleGoodsBeanDao dao = GreenDaoHelper.getDaoSession().getSimpleGoodsBeanDao();
        dao.deleteAll();
    }
}
