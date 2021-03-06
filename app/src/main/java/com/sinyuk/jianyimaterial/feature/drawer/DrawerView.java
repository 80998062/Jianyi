package com.sinyuk.jianyimaterial.feature.drawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.common.Constants;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.events.XLoginEvent;
import com.sinyuk.jianyimaterial.events.XLogoutEvent;
import com.sinyuk.jianyimaterial.feature.CategoryView;
import com.sinyuk.jianyimaterial.feature.login.LoginView;
import com.sinyuk.jianyimaterial.feature.needs.NeedsView;
import com.sinyuk.jianyimaterial.feature.profile.ProfileView;
import com.sinyuk.jianyimaterial.feature.settings.SettingsView;
import com.sinyuk.jianyimaterial.glide.BlurTransformation;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Sinyuk on 16.3.30.
 */
public class DrawerView extends BaseFragment<DrawerPresenterImpl> implements IDrawerView {
    private static DrawerView sInstance;
    @Bind(R.id.avatar)
    ImageView mAvatar;
    @Bind(R.id.user_name_tv)
    TextView mUserNameTv;
    @Bind(R.id.header_layout)
    RelativeLayout mHeaderLayout;
    LinearLayout mNavigationView;
    @Bind(R.id.drawer_menu_category)
    TextView mDrawerMenuCategory;
    @Bind(R.id.drawer_menu_needs)
    TextView mDrawerMenuWant;
    @Bind(R.id.drawer_menu_message)
    TextView mDrawerMenuMessage;
    @Bind(R.id.drawer_menu_account)
    TextView mDrawerMenuAccount;
    @Bind(R.id.drawer_menu_settings)
    TextView mDrawerMenuSettings;
    @Bind(R.id.backdrop_iv)
    ImageView mBackdrop;

    private DrawerLayout mDrawerLayout;
    private Intent mMenuIntent = null;

    public static DrawerView getInstance() {
        if (null == sInstance) { sInstance = new DrawerView(); }
        return sInstance;
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        if (null != mDrawerLayout) {
            mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {

                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if (mMenuIntent != null) { startActivity(mMenuIntent); }
                    mMenuIntent = null;
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }
    }

    @Override
    protected void beforeInflate() {
    }

    @Override
    protected DrawerPresenterImpl createPresenter() {
        return new DrawerPresenterImpl();
    }


    @Override
    protected void onFinishInflate() {
        if (!getUserVisibleHint()) {
            sMyHandler.postDelayed(() -> mPresenter.queryCurrentUser(), 2000);
        } else {
            mPresenter.queryCurrentUser();
        }
    }

    @Override
    protected int getContentViewID() {
        return R.layout.drawer_view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(XLoginEvent event) {
        LogUtils.simpleLog(DrawerView.class, "登录登录登录登录登录登录");
        mPresenter.queryCurrentUser();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(XLogoutEvent event) {
        LogUtils.simpleLog(DrawerView.class, "登出登出登出登出登出登出");
        onUserNotLogged();
    }


    @Override
    public void showMessageBadge() {

    }

    @Override
    public void toPersonalView() {
        mDrawerLayout.closeDrawers();
        /*mDrawerLayout.postDelayed(() -> startActivity(new Intent(getContext(), LoginView.class)), DRAWER_CLOSE_DURATION);*/
    }

    @Override
    public void toLoginView() {
        mDrawerLayout.closeDrawers();
        mMenuIntent = new Intent(getContext(), LoginView.class);
    }

    @Override
    public void onQuerySucceed(User user) {
        mDrawerMenuAccount.setVisibility(View.VISIBLE);
        mDrawerMenuMessage.setVisibility(View.VISIBLE);
        DrawableRequestBuilder<String> requestBuilder;
        requestBuilder = Glide.with(mContext).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT);
        requestBuilder.load(user.getHeading()).bitmapTransform(new CropCircleTransformation(mContext))
                .thumbnail(0.2f).error(R.drawable.ic_avatar_placeholder).priority(Priority.IMMEDIATE).into(mAvatar);

        requestBuilder.load(user.getHeading()).bitmapTransform(new BlurTransformation(mContext, Constants.BLUR_RADIUS, Constants.BLUR_SAMPLING))
                .crossFade(2000).priority(Priority.HIGH).error(R.drawable.header_backdrop).thumbnail(0.5f).into(mBackdrop);

        mUserNameTv.setText(StringUtils.check(mContext, user.getName(), R.string.unknown_user_name));
    }

    @Override
    public void onQueryFailed(String message) {
        mUserNameTv.setText(message);
    }

    @Override
    public void onUserNotLogged() {
        mDrawerMenuAccount.setVisibility(View.GONE);
        mDrawerMenuMessage.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.ic_avatar_placeholder)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mAvatar);
        mBackdrop.setImageResource(R.color.colorPrimary);
        /**
         * load custom picture in backdrop here
         */
        mUserNameTv.setText("点击登录");
    }

    @OnClick({R.id.avatar, R.id.user_name_tv})
    public void onUserInfoItemClick() {
        mPresenter.onUserInfoClick();
    }

    @OnClick({R.id.drawer_menu_category, R.id.drawer_menu_needs, R.id.drawer_menu_message, R.id.drawer_menu_account, R.id.drawer_menu_settings})
    public void onMenuItemSelected(View view) {
        toMenuItemIntent(view.getId());
    }

    private void toMenuItemIntent(int selected) {
        if (null != mDrawerLayout) { mDrawerLayout.closeDrawers(); }
        switch (selected) {
            case R.id.drawer_menu_category:
                mMenuIntent = new Intent(getContext(), CategoryView.class);
                break;
            case R.id.drawer_menu_needs:
                mMenuIntent = new Intent(getContext(), NeedsView.class);
                break;
            case R.id.drawer_menu_message:
                break;
            case R.id.drawer_menu_account:
                mMenuIntent = new Intent(getContext(), ProfileView.class);
                Bundle bundle = new Bundle();
                bundle.putFloat(ProfileView.PROFILE_TYPE, ProfileView.MINE);
                mMenuIntent.putExtras(bundle);
                break;
            case R.id.drawer_menu_settings:
                mMenuIntent = new Intent(getContext(), SettingsView.class);
                break;
        }
    }
}
