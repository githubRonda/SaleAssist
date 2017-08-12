package com.ronda.saleassist.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ronda.saleassist.base.MyApplication;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于播放短音频
 * 1. 使用 SoundPool 播放音频（而不是MediaPlayer）
 *
 * 2. 在静态代码块中初始化 mSoundPool 和 加载所有音频
 * 为什么要在 静态代码块中加载所有音频呢？ 因为当 我们先后调用 SoundPool 的load() 和 play() 方法播放音频时是没有声音的，原因就是音频还没有加载完就播放了。
 * 所以解决思路：必须要先加载音频(load()), 当音频加载完成后再播放(play())
 * 方式1：一般是在初始化的时候加载（构造器，静态代码块等），然后在点击等事件中进行播放 (这个类在第一次播放音频时才加载的话，那么第一次播放时没有声音的，原因就是 load() 和 play() 同时执行)
 * 方式2：把 play() 方法延迟调用。即可以使用 new Handler().postDelayed(Runnable)
 * 方法3：在调用 mSoundPool.load(); 后，再给 mSoundPool.setOnLoadCompleteListener() 设置监听器，在监听器中调用 play() 方法
 *
 * 3. 使用反射获取所有音频时，要注意：获取到的字段，会比我们在代码中看到的要多出两个,即 $change 和 serialVersionUID, 这两个字段应该是编译的时候加上去的。值为：$change=null,serialVersionUID=0
 * field.getInt()方法对于 $change 来说会出现异常，null 不属于 int.（刚开始的时候没有注意，所以会造成程序闪退）
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/12/18
 * Version: v1.0
 */

public class MusicUtil {
    private static SoundPool mSoundPool;
    private static Map<Integer, Integer> mMap = new HashMap<>();

    static {
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);  //Android 5.0 Lollipop及以后使用Builder来创建 SoundPool

//        // 利用反射，先获取raw资源文件中所有音频的id(field.getInt()), 然后调用mSoundPool.load()方法先把音频加载到mSoundPool，这样可以避免load()和play()方法同时执行时无声音的现象。
//        Field[] fields = R.raw.class.getDeclaredFields();
//        for (Field field : fields) {
//            if (!field.getName().equals("$change") && !field.getName().equals("serialVersionUID")) { //这两个字段应该是编译的时候加上去的。$change=null,serialVersionUID=0
//                try {
//                    mMap.put(field.getInt(R.raw.class), mSoundPool.load(MyApplication.getInstance(), field.getInt(R.raw.class), 1));
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//       KLog.e("123456789");
    }

    // 使用这个方法，在第一次调用时，仍然是 静态代码块中的 load() 和 下面方法中的 play() 是同时执行的，第一次没有声音。
    // 注意： 这个类并不是一开始就加载，而是第一次使用这个类时才加载这个类。
    // 解决方法：自定义一个 initLoadData 方法，然后把静态代码块中的内容放到 这个方法中，可以使用单例模式，在 MyApplication 中调用
    public static void playMusic(final int resourceId) {
        AudioManager mgr = (AudioManager) MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final float volume = streamVolumeCurrent / streamVolumeMax;

        KLog.e("id:" + mMap.get(resourceId));

        KLog.e("mMap:" + mMap.size());


        mSoundPool.play(mMap.get(resourceId), volume, volume, 1, 0, 1f);

//        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                mSoundPool.play(mMap.get(resourceId), volume, volume, 1, 0, 1f);
//            }
//        });
    }


    public static void playMusic(Context c, final int resourceId) {
//        final SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        AudioManager manager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
//        manager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
//        float streamVolumeCurrent = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        float streamVolumeMax = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        final float volume = streamVolumeCurrent / streamVolumeMax;

        Integer integer = mMap.get(resourceId);
        if (null == integer){
            mMap.put(resourceId, mSoundPool.load(c, resourceId, 1));
        }
        else {// 若之前有load()过，则直接play(), 否则就load(), 当load() 完成后，就会触发下面的 setOnLoadCompleteListener 监听器
            mSoundPool.play(mMap.get(resourceId), 1f, 1f, 1, 0, 1f);
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSoundPool.play(mMap.get(resourceId), 1f, 1f, 1, 0, 1f);
            }
        });
    }
}
