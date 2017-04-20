package com.dch.test.ui.fragment;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dch.test.Injection;
import com.dch.test.R;
import com.dch.test.base.BaseFragment;
import com.dch.test.base.adapter.ListBaseAdapter;
import com.dch.test.base.adapter.SuperViewHolder;
import com.dch.test.contract.AndroidContract;
import com.dch.test.contract.presenter.AndroidPresenter;
import com.dch.test.repository.entity.GankEntity;
import com.dch.test.ui.DetailActivity;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：Dch on 2017/4/13 11:09
 * 描述：
 * 邮箱：daichuanhao@caijinquan.com
 */
public class GankAndroidFragment extends BaseFragment implements AndroidContract.AndroidView, OnRefreshListener, OnLoadMoreListener {
    private boolean loadMore = false;
    private List<GankEntity.Data> mData = new ArrayList<>();
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private DataAdapter<GankEntity.Data> mDataAdapter;

    @Inject
    AndroidPresenter presenter;

    @BindView(R.id.recyclerview)
    LRecyclerView mRecyclerView;

    public static GankAndroidFragment newInstance() {
        return new GankAndroidFragment();
    }

    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab1, null);
        ButterKnife.bind(this, rootView);
//        presenter = new AndroidPresenter(this, Injection.provideAndroidRepository(activity));
        initView();
        return rootView;
    }

    @TargetApi(21)
    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mDataAdapter = new DataAdapter<>(activity);
        mDataAdapter.setDataList(mData);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(lRecyclerViewAdapter);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.CubeTransition);
        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.forceToRefresh();
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(activity, DetailActivity.class);
                i.putExtra("url",mData.get(position).images[0]);
                View sharedView = view.findViewById(R.id.iv_item_gank);
                String transitionName = getString(R.string.transitionName);
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, transitionName);
                startActivity(i, transitionActivityOptions.toBundle());
            }
        });
    }


    @Override
    public void setPresenter(AndroidPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void showAndroidDailyList(GankEntity gankEntity) {
        if (loadMore) {
            mData = gankEntity.results;
            mDataAdapter.addAll(gankEntity.results);
        } else {
            mDataAdapter.clear();
            mData.clear();
            mData = gankEntity.results;
            mDataAdapter.setDataList(mData);
        }
        lRecyclerViewAdapter.notifyDataSetChanged();
        mRecyclerView.refreshComplete(mDataAdapter.getItemCount());
    }

    @Override
    public void showError(Throwable throwable) {
    }

    @Override
    public void onRefresh() {
        loadMore = false;
        presenter.getAndroidData();
    }

    @Override
    public void onLoadMore() {
        loadMore = true;
        presenter.getAndroidData();
    }

    private class DataAdapter<Data> extends ListBaseAdapter<GankEntity.Data> {


        public DataAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_gank;
        }

        @Override
        protected void onBindItemHolder(SuperViewHolder holder, int position) {
            GankEntity.Data data = mDataList.get(position);
            TextView textViewTitle = holder.getView(R.id.tv_item_title_gank);
            textViewTitle.setText(data.type);
            TextView textViewTime = holder.getView(R.id.tv_item_time_gank);
            textViewTime.setText(data.createdAt.substring(0,10));
            TextView textViewContent= holder.getView(R.id.tv_item_content_gank);
            textViewContent.setText(data.desc);
            try {
                ImageView imageView = holder.getView(R.id.iv_item_gank);
                Glide.with(activity).load(data.images[position]).fitCenter().placeholder(R.mipmap.ic_launcher).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}