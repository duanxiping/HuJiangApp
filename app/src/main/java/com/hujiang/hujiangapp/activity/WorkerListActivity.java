package com.hujiang.hujiangapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.event.UpdateHireEvent;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.Hire;
import com.hujiang.hujiangapp.model.ImageResource;
import com.hujiang.hujiangapp.model.Page;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.RegisterData;
import com.hujiang.hujiangapp.shared.SessionManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class WorkerListActivity extends BaseActivity {
    @BindView(R.id.refreshLayout)
    protected RefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;

    private MyAdapter adapter = null;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setupRecyclerView();
        setupRefreshView();

        refreshLayout.autoRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHireUpdatedEvent(UpdateHireEvent event) {
        Hire hire = event.getHire();
        List<Hire> data = adapter.getData();
        if (hire != null) {
            int index = data.indexOf(hire);
            if (0 <= index) {
                data.set(index, hire);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void setupRefreshView() {
        refreshLayout.setEnableAutoLoadMore(true);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                reloadAllData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                fetchCurrentPageData();
            }
        });
    }

    private void finishRefresh() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    private void reloadAllData() {
        currentPage = 0;
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setNoMoreData(false);

        fetchCurrentPageData();
    }

    private void fetchCurrentPageData() {
        User user = SessionManager.shared().getCurrentUser();
        Project project = SessionManager.shared().getCurrentProject();
        if (user == null || project == null) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<Page<Hire>>> call = apiService.queryHires(user.getId(), project.getId(), currentPage, MyConstants.pageSize);

        startApiCall(call, new ApiResultHandler<Page<Hire>>() {
            @Override
            boolean showLoading() {
                return false;
            }

            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiFailed() {
                finishRefresh();
            }

            @Override
            void apiSucceed(Page<Hire> hirePage) {
                finishRefresh();

                if (currentPage == 0) {
                    adapter.setNewData(hirePage.getContent());
                } else {
                    adapter.addData(hirePage.getContent());
                }

                if (hirePage.getTotalPages() <= currentPage + 1) {
                    refreshLayout.setEnableLoadMore(false);
                    refreshLayout.setNoMoreData(true);
                } else {
                    currentPage += 1;
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new MyAdapter(this, R.layout.worker_row, null);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (preventDoubleClick()) {
                    return;
                }

                Hire hire = (Hire)adapter.getItem(position);
                showWorkerActivity(hire);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupEmptyView();
    }

    private void setupEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) recyclerView.getParent(), false);
        adapter.setEmptyView(emptyView);
    }

    private void showWorkerActivity(Hire hire) {
        RegisterData.shared().clearAllData();
        RegisterData.shared().isChangingWorkerInfo = true;

        Gson gson = new Gson();
        String json = gson.toJson(hire);

        Intent intent = new Intent(this, WorkerChangeActivity.class);
        intent.putExtra(MyConstants.ExtraHireJson, json);
        startActivity(intent);
    }

    private class MyAdapter extends BaseQuickAdapter<Hire, BaseViewHolder> {
        private Context context;

        public MyAdapter(Context context, int layoutResId, @Nullable List<Hire> data) {
            super(layoutResId, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, Hire item) {
            helper.setText(R.id.nameTextView, item.getTitle());

            ImageResource imageResource = item.getFacePhoto();
            if (imageResource == null || StringUtils.isEmpty(imageResource.getUrl())) {
                helper.setImageBitmap(R.id.imageView, null);
            } else {
                Glide.with(WorkerListActivity.this).load(imageResource.getUrl()).into((ImageView)helper.getView(R.id.imageView));
            }
        }
    }
}
