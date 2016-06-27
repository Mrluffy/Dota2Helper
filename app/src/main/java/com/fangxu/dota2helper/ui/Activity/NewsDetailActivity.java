package com.fangxu.dota2helper.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fangxu.dota2helper.R;
import com.fangxu.dota2helper.RxCenter;
import com.fangxu.dota2helper.interactor.TaskIds;
import com.fangxu.dota2helper.network.AppNetWork;
import com.fangxu.dota2helper.util.ToastUtil;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lenov0 on 2016/4/13.
 */
public class NewsDetailActivity extends BaseActivity {
    public static final String NEWS_DATE = "news_date";
    public static final String NEWS_NID = "news_nid";

    @Bind(R.id.wv_news_detail)
    WebView mWebView;

    public static void toNewsDetailActivity(Context context, String date, String nid) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(NEWS_DATE, date);
        intent.putExtra(NEWS_NID, nid);
        context.startActivity(intent);
    }

    @Override
    public boolean applySystemBarDrawable() {
        return true;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_news_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setTitle(R.string.news_detail);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        loadDetail();
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        RxCenter.INSTANCE.removeCompositeSubscription(TaskIds.newsDetailTaskId);
        super.onDestroy();
    }

    private void loadDetail() {
        String date = getIntent().getStringExtra(NEWS_DATE);
        String nid = getIntent().getStringExtra(NEWS_NID);
        RxCenter.INSTANCE.getCompositeSubscription(TaskIds.newsDetailTaskId).add(AppNetWork.INSTANCE.getDetailsApi().getNewsDetail(date, nid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mWebView.loadDataWithBaseURL(null, s, "text/html", "UTF-8", null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showToast(getApplicationContext(), "error");
                    }
                }));
    }
}
