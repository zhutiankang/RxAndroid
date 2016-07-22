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

    public void send(Object o){
        _bus.onNext(o);
    }

    public Observable<Object> toObservable(){
        return _bus;
    }

    public boolean hasObservers(){
        return _bus.hasObservers();
    }


}
