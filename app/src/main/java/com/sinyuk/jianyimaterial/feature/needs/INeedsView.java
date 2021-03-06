package com.sinyuk.jianyimaterial.feature.needs;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.api.JNeed;

/**
 * Created by Sinyuk on 16.4.19.
 */
public interface INeedsView {

    void showRefreshProgress();

    void dismissRefreshProgress();

    void showList(JNeed data, boolean isRefresh);

    void onNeedsVolleyError(@NonNull String message);

    void onNeedsParseError(@NonNull String message);

    void onNeedsReachBottom();

    void toWantView();

    void toLoginView();
}
