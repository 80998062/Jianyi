package com.sinyuk.jianyimaterial.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.api.Show;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.common.Constants;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.events.XRequestLoginEvent;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.YihuoDetailsService;
import com.sinyuk.jianyimaterial.mvp.BaseModel;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.3.17.
 */
public class YihuoModel implements BaseModel {
    public static final String SHOW_REQUEST = "show";
    public static final String INDEX_REQUEST = "index";
    private static final String HEADLINE_REQUEST = "headline";
    private static final String HEADLINE_INDEX = "1";


    private static YihuoModel instance;
    private final Context mContext;
    private YihuoDetailsService yihuoDetailsService;
    private Gson gson;

    private YihuoModel(Context context) {
        this.mContext = context;
        yihuoDetailsService = DaoUtils.getYihuoDetailsService();
        gson = new Gson();
    }


    public static YihuoModel getInstance(Context context) {
        if (instance == null) {
            synchronized (UserModel.class) {
                if (instance == null) {
                    instance = new YihuoModel(context);
                }
            }
        }
        return instance;
    }

    public void loadLatestHeadline(RequestHeadlineCallback callback) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoDetails(HEADLINE_INDEX), null, response -> {
                    try {
                        Show show = gson.fromJson(response.toString(), Show.class);
                        Show.Data jsonData = show.getData();
                        String trans = gson.toJson(jsonData);
                        YihuoDetails data = gson.fromJson(trans,
                                YihuoDetails.class);
                        if (data != null) { callback.onCompleted(data); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }

                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, HEADLINE_REQUEST);
    }


    public void getProfile(int pageIndex, RequestYihuoProfileCallback callback) {
        boolean isRefresh = pageIndex == 1;
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoAll(pageIndex), null, response -> {
                    try {
                        Index index = gson.fromJson(response.toString(), Index.class);

                        List<Index.Data.Items> items = index.getData().getItems();

                        String trans = gson.toJson(items);

                        List<YihuoProfile> data = gson.fromJson(trans,
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());

                        // do clear
                        if (data != null) { callback.onCompleted(data, isRefresh); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }
                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, INDEX_REQUEST);
    }

    public void getDetails(@NonNull String yihuoId, RequestYihuoDetailsCallback callback) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoDetails(yihuoId), null, response -> {
                    try {
                        Show show = gson.fromJson(response.toString(), Show.class);
                        Show.Data jsonData = show.getData();
                        String trans = gson.toJson(jsonData);
                        YihuoDetails data = gson.fromJson(trans,
                                YihuoDetails.class);
                        if (data != null) { callback.onCompleted(data); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }

                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, SHOW_REQUEST);
    }

    /**
     * check whether this Yihuo has been added into Likes already
     *
     * @return
     */
    public Observable getLikeState(@NonNull String yihuoId) {
        return Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            subscriber.onNext(checkLikeState(yihuoId));
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io());
    }

    private Boolean checkLikeState(String yihuoId) {
        YihuoDetails data = (YihuoDetails) yihuoDetailsService.query(yihuoId);
        return null != data;
    }

    public void addToLikes(@NonNull YihuoDetails detailsData, LikesCallback callback) {
        String uId = PreferencesUtils.getString(mContext, Constants.Prefs_Uid);
        if (TextUtils.isEmpty(uId)) {
            EventBus.getDefault().post(new XRequestLoginEvent());
        } else {
            Date addedDate = new Date(System.currentTimeMillis()); //获取当前时间
            detailsData.setDate(addedDate);
            yihuoDetailsService.saveOrUpdate(detailsData);
            callback.onAddToLikes();
        }
    }

    public void removeFromLikes(@NonNull YihuoDetails detailsData, LikesCallback callback) {
        yihuoDetailsService.deleteByKey(detailsData.getId());
        callback.onRemoveFromLikes();
    }

    public interface LikesCallback {
        void onAddToLikes();

        void onRemoveFromLikes();
    }

    public interface RequestHeadlineCallback {

        void onVolleyError(String message);

        void onCompleted(YihuoDetails data);

        void onParseError(String message);
    }

    public interface RequestYihuoDetailsCallback {

        void onVolleyError(String message);

        void onCompleted(YihuoDetails data);

        void onParseError(String message);
    }

    public interface RequestYihuoProfileCallback {

        void onVolleyError(String message);

        void onCompleted(List<YihuoProfile> data, boolean isRefresh);

        void onParseError(String message);
    }
}
