package com.ronda.saleassist.zznew;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class SuperUtil {
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    if (SuperUtil.onResult != null) {
                        SuperUtil.onResult.onResult(msg.obj.toString());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private static onResult onResult;

    public static void sendPost(final String url, final Map<String, String> map, final onResult onResult) {
        new Thread() {
            @Override
            public void run() {
                SuperUtil.onResult = onResult;
                PrintWriter out = null;
                BufferedReader in = null;
                String result = "";
                try {
                    URL realUrl = new URL(url);
                    URLConnection conn = realUrl.openConnection();
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    out = new PrintWriter(conn.getOutputStream());
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        sb.append(entry.getKey() + "=" + entry.getValue() + "&");
                    }
                    out.print(sb.toString());
                    out.flush();
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        result += line;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.start();
    }

    public interface onResult {
        public void onResult(String resString);
    }

//    private static MediaPlayer mPlayer = null;
//    private static List<MediaPlayer> players = new ArrayList<MediaPlayer>();
//    private static int playCount = 0;

//    public static void playMusic(Context context, int resourceId) {
//        players.add(MediaPlayer.create(context, resourceId));
//        players.get(playCount).setLooping(false);
//        players.get(playCount).start();
//        playCount++;
//    }


//    private static SoundPool soundPool;
//    private static SoundPool.Builder mBuilder;//Android 5.0 Lollipop
//    public static void playMusic(Context context, int resourceId){
//        soundPool = new SoundPool(5,AudioManager.STREAM_SYSTEM, 1);
//        soundPool.load(context,resourceId,1);
//        soundPool.play(1,1, 1, 1, 0, 1);
//    }

}
