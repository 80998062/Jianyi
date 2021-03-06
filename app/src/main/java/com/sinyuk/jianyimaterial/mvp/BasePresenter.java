package com.sinyuk.jianyimaterial.mvp;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import rx.subscriptions.CompositeSubscription;
/**
 * Created by Sinyuk on 16.3.16.
 */

/**
 * MVP中的Presenter基类，因为Activity，Fragment可能会回收或者变化
 * <p>
 * 所以要持有这些View的弱引用，一旦View被回收了（比如Activity被系统收回），就不再保留引用
 * <p>
 * 如果你要实现自己的Presenter，最好继承这个类
 *
 * @param <V> View的接口的类型【你的Activity肯定会实现View的接口的】
 */

public abstract class BasePresenter<V> {

    protected SoftReference<V> mViewRef;

    protected V mView;

    protected CompositeSubscription mCompositeSubscription;

    @CallSuper
    public void attachView(@NonNull V view) {
        detachView();
        mViewRef = new SoftReference<>(view);
        mView = mViewRef.get();
        mCompositeSubscription = new CompositeSubscription();
    }


    public void detachView() {
        if (mViewRef != null) {
            mView = null;
            mViewRef.clear();
            mViewRef = null;
        }
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }


}
