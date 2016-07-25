package com.tiankang.rxandroid.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by 1 on 2016/7/22.
 */
public class RxBus {

    private final Subject<Object,Object> _bus = new SerializedSubject<>(PublishSubject.create());

    private RxBus(){};

    public static RxBus getInstance(){
        return RxBusHolder.instance;
    }
    public static class RxBusHolder{
        public static final RxBus instance = new RxBus();
    }


    public void send(Object o){
        _bus.onNext(o);
    }

    public Observable<Object> toObserverable(){
        return _bus;
    }

    /**与send结合用，判断是否有观察者，没有不发事件
     * @return
     */
    public boolean hasObservers(){
        return _bus.hasObservers();
    }


}
