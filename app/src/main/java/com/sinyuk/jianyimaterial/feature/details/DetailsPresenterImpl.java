package com.sinyuk.jianyimaterial.feature.details;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.events.XRequestLoginEvent;
import com.sinyuk.jianyimaterial.model.YihuoModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class DetailsPresenterImpl extends BasePresenter<DetailsView> implements IDetailsPresenter,
        YihuoModel.RequestYihuoDetailsCallback, YihuoModel.LikesCallback {

    @Override
    public void loadYihuoDetails(@NonNull String yihuoId) {
        YihuoModel.getInstance(mView).getDetails(yihuoId, this);
    }

    @Override
    public void getLikesState(@NonNull String yihuoId) {
      /*  YihuoModel.getInstance(mView).getLikeState(yihuoId).subscribeOn(AndroidSchedulers.mainThread()).subscribe(isAdded -> {
            mView.setupLikeButton((Boolean) isAdded);
        });*/
    }

    @Override
    public void addToLikes(@NonNull YihuoDetails data) {
        YihuoModel.getInstance(mView).addToLikes(data, this);
    }

    @Override
    public void removeFromLikes(@NonNull YihuoDetails data) {
        YihuoModel.getInstance(mView).removeFromLikes(data, this);
    }

    @Override
    public void loadComments(@NonNull int pageIndex) {
        if (/*应该在回调里面写*/true) {
            final List<String> dummyComments = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                dummyComments.add("");
            }
            boolean isRefresh = pageIndex == 1;
            mView.showComments(dummyComments, isRefresh); // 一次模拟3条
            mView.setRequestDataRefresh(false);
        } else {
            mView.hintNoComment();
            mView.setRequestDataRefresh(false);
        }
    }

    //** callbacks for loading Yihuo details **//
    @Override
    public void onVolleyError(String message) {

    }

    @Override
    public void onCompleted(YihuoDetails data) {
        if (mView != null) {
            mView.showDescription(data.getDetail());
            mView.showShots(data.getPics());
            mView.showViewCount(data.getViewcount());
        }
    }

    @Override
    public void onParseError(String message) {

    }

    @Override
    public void onAddToLikes() {

    }

    @Override
    public void onRemoveFromLikes() {

    }
    //** callbacks for loading Yihuo details **//

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestForLogin(XRequestLoginEvent event) {
        mView.hintRequestLogin();
    }
}
