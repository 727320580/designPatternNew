package com.rubu.designpatternnew;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Email 727320580@qq.com
 * Created by xika on 2017/12/11
 * Vwesion 1.0
 * single Responsibility Principle
 * Dsscription: 图片加载器 (适配缓存) ,指责单一原则 , 将缓存放缓存   图片加载放图片加载  !!!!!!!!!!!!!!!!!!! 待修改
 */

public class ImageLoad {

    // 存放缓存图片的集合
    LruCache<String,Bitmap> mImageLucache;
    // 线程池数量,线程池的数量等于CPU的数量
    ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.
            getRuntime().availableProcessors());

    // 初始化
    public void ImageLoad(){
        initImageLoad();
    }

    private void initImageLoad() {
        // 计算最大可使用内存
         int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        // 缓存区域使用的最大缓存
         int lucacheSize = maxMemory/4;
         mImageLucache = new LruCache<String, Bitmap>(lucacheSize){
             @Override
             protected int sizeOf(String key, Bitmap value) {
                 // 计算每张图片的大小
                 return value.getRowBytes()*value.getHeight()/1024;
             }
         };

    }

    /**
     * 加载图片数据
     * @param url
     * @param imageView
     */
    public void displayBitmap(final String url, final ImageView imageView){
        // 从缓存中获取图片并且加载
        Bitmap bitmap = mImageLucache.get(url);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        // 图片位置和布局(对应)
        imageView.setTag(url);
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downLoadBitmap(url);
                if (bitmap==null){
                    return;
                }
                // 要显示的iamgeView的图片和下载之后的图片是同一个路径
                if (imageView.getTag().equals(url)){
                    imageView.setImageBitmap(bitmap);
                }
                mImageLucache.put(url,bitmap);
            }
        });
    }

    /**
     * 下载图片
     * @param psth
     * @return
     */
    private Bitmap downLoadBitmap(String psth) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(psth);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            // 关闭网络连接
            if (conn!=null){
                conn.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
