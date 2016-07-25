package com.tiankang.rxandroid.RxFeatures;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by TianKang on 2016/7/25.
 */
public class RxTestDemoActivity extends Activity {

    private ImageView imageCollectorView;

    private Subscription subscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Lambda
        imageCollectorView.setOnClickListener(v -> Toast.makeText(this,"test lambda",Toast.LENGTH_SHORT).show());
    }

    //---------------------------------1.简洁-------------------------------------------//

    public void traverseList(final List<File> folders) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                for (File folder : folders) {
                    File[] files = folder.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith(".png")) {
                            final Bitmap bitmap = getBitmapFromFile(file);
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    imageCollectorView.addImage(bitmap);
//                                }
//                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageCollectorView.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (File folder : folders) {
                    File[] files = folder.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith(".png")) {
                            final Bitmap bitmap = getBitmapFromFile(file);
                            runOnUiThread(() -> imageCollectorView.setImageBitmap(bitmap));
                        }
                    }
                }
            }
        }.start();


        rx.Observable.from(folders).flatMap(new Func1<File, rx.Observable<File>>() {
            @Override
            public rx.Observable<File> call(File file) {
                return Observable.from(file.listFiles());
            }
        }).filter(new Func1<File, Boolean>() {
            @Override
            public Boolean call(File file) {
                return file.getName().endsWith(".png");
            }
        }).map(new Func1<File, Bitmap>() {
            @Override
            public Bitmap call(File file) {
                return getBitmapFromFile(file);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
//                imageCollectorView.addImage(bitmap);
                        imageCollectorView.setImageBitmap(bitmap);
                    }
                });
        rx.Observable.from(folders)
                .flatMap(file -> Observable.from(file.listFiles()))
                .filter(file1 -> file1.getName().endsWith("png"))
                .map(file2 -> getBitmapFromFile(file2))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> imageCollectorView.setImageBitmap(bitmap));
    }

    private Bitmap getBitmapFromFile(File file) {
        return null;
    }
    //---------------------------------2.异步-------------------------------------------//

    public void rxAsyncTask(){

        subscription = Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer number) {
                        Log.d("xxxx", "number:" + number);
                    }
                });
        subscription = Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(number -> Log.d("xxxx","number:" + number));

        /***
         * subscribeOn()只能定义一次，而观察者subscriber，observerOn()方法可以定义多次，也就是可以通过observerOn方法实现线程位置的多重转换
         * Schedulers.immediate()这个方法是在当前线程运行
         * Schedulers.newThread()是开启一个新线程，然后在里面运行接下来的代码
         * Schedulers.io() I/O操作，什么读写文件啊，数据库啊，网络信息交互啊，图片啊什么都往这里面扔
         * Schedulers.computation()，这个就是针对IO最好不处理的计算工作
         * AndroidSchedulers.mainThread()，这个应该就不用介绍了
         */
        Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
//                .map(mapOperator) // 新线程，由 observeOn() 指定
                .observeOn(Schedulers.io())
//                .map(mapOperator2) // IO 线程，由 observeOn() 指定
                .observeOn(AndroidSchedulers.mainThread());
//                .subscribe(subscriber);  // Android 主线程，由 observeOn() 指定
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null){
            subscription.unsubscribe();
        }
    }
}
