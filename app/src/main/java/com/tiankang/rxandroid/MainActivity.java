package com.tiankang.rxandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tiankang.rxandroid.rxbus.KissEvent;
import com.tiankang.rxandroid.rxbus.RxBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private RxBus _rxBus;

    public RxBus getRxBusSingleTon(){
        if (_rxBus == null){
            _rxBus = new RxBus();
        }
        return _rxBus;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getRxBusSingleTon();

        _rxBus.toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof KissEvent){
                    showToast();
                }
            }
        });
    }

    private void showToast() {
        Toast.makeText(this,"Kiss me",Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_kiss)
    public void kissMe(){
        _rxBus.send(new KissEvent());
    }




}
