package com.tiankang.rxandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tiankang.rxandroid.rxbus.KissEvent;
import com.tiankang.rxandroid.rxbus.RxBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private RxBus _rxBus = RxBus.getInstance();
    private CompositeSubscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        subscription = new CompositeSubscription();
        subscription.add(_rxBus.toObserverable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof KissEvent) {
                    showToast();
                }
            }
        }));
    }

    private void showToast() {
        Toast.makeText(this, "Kiss me", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_kiss)
    public void kissMe() {
        if (_rxBus.hasObservers()) {
            _rxBus.send(new KissEvent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscription.clear();
    }

    /*----------------------------------Bundle应用 舍弃setArgment-------------------------------------------------------------*/

    public void send(Bundle bundle) {
        bundle.putString("eventname", "addOrderInfoGoods");
        _rxBus.send(bundle);
    }

    /**
     * 首先使用map进行类型转换，将Object类型转换为Bundle类型
     * 之后在onNext中进行处理（这里使用一个参的new Action1替换）
     */
    public void operateBundle() {

        subscription.add(_rxBus.toObserverable()
                .map(new Func1<Object, Bundle>() {
                    @Override
                    public Bundle call(Object o) {
                        return (Bundle) o;
                    }
                })
                .subscribe(new Action1<Bundle>() {
                    @Override
                    public void call(Bundle bundle) {
                        if (bundle.getString("eventname").equals("addOrderInfoGoods")) {
                            //show(bundle);
                        }
                        if (bundle != null){
                            //handle(bundle)
                        }
                    }
                }));
    }

}
