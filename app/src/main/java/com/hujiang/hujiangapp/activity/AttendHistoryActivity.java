package com.hujiang.hujiangapp.activity;

import android.content.Context;
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
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.misc.MyConstants;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.FaceLog;
import com.hujiang.hujiangapp.model.Hire;
import com.hujiang.hujiangapp.model.ImageResource;
import com.hujiang.hujiangapp.model.Page;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.shared.SessionManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class AttendHistoryActivity extends BaseActivity {
    @BindView(R.id.refreshLayout)
    protected RefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;

    private MyAdapter adapter = null;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_history);
        ButterKnife.bind(this);

        setupRecyclerView();
        setupRefreshView();

        refreshLayout.autoRefresh();
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
        if (user == null) {
            return;
        }

        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<Page<FaceLog>>> call = apiService.queryTodayFaceLogs(user.getId(), currentPage, MyConstants.pageSize);

        startApiCall(call, new ApiResultHandler<Page<FaceLog>>() {
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
            void apiSucceed(Page<FaceLog> hirePage) {
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
        adapter = new MyAdapter(this, R.layout.face_log_row, null);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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

    private class MyAdapter extends BaseQuickAdapter<FaceLog, BaseViewHolder> {
        private Context context;

        public MyAdapter(Context context, int layoutResId, @Nullable List<FaceLog> data) {
            super(layoutResId, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, FaceLog item) {
            Hire hire = item.getHire();
            if (hire != null) {
                helper.setText(R.id.nameTextView, hire.getTitle());
            } else {
                helper.setText(R.id.nameTextView, "");
            }

            Dict inOut = item.getInOut();
            if (inOut != null && StringUtils.equals(inOut.getId(), Dict.attendIn().getId())) {
                helper.setText(R.id.inOutTextView, R.string.attend_in);
            } else {
                helper.setText(R.id.inOutTextView, R.string.attend_out);
            }

            helper.setText(R.id.timeTextView, item.getCreateTime());

            ImageResource imageResource = item.getFacePhoto();
            if (imageResource == null || StringUtils.isEmpty(imageResource.getUrl())) {
                helper.setImageBitmap(R.id.imageView, null);
            } else {
                Glide.with(AttendHistoryActivity.this).load(imageResource.getUrl()).into((ImageView)helper.getView(R.id.imageView));
            }
        }
    }
}
