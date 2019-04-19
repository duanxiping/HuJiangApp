package com.hujiang.hujiangapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.hujiang.hujiangapp.R;
import com.hujiang.hujiangapp.api.ApiService;
import com.hujiang.hujiangapp.api.HuJiangServer;
import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.WorkType;
import com.hujiang.hujiangapp.shared.RegisterData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;

// TODO: 2018/12/27 身份证拍照放大一些
public class WorkTypeActivity extends BaseActivity {
    @BindView(R.id.editTextSearch)
    protected EditText editTextSearch;

    @BindView(R.id.searchMaskView)
    protected View searchMaskView;

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;

    private MyAdapter adapter = null;

    private List<WorkType> hotWorkTypes = new ArrayList<>();
    private List<WorkType> allWorkTypes = new ArrayList<>();

    private long selectedId = -1;
    private WorkType selectedWorkType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_type);
        ButterKnife.bind(this);

        WorkType workType = RegisterData.shared().workType;
        if (workType != null) {
            selectedId = workType.getId();
        }

        setupRecyclerView();
        setupSearchEditText();
        fetchHotWorkType();
        fetchAllWorkType();
    }

    private void setupRecyclerView() {
        adapter = new MyAdapter(this, R.layout.work_type_row, R.layout.header_row, null);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SectionWorkType sectionWorkType = (SectionWorkType)adapter.getItem(position);
                if (sectionWorkType != null && !sectionWorkType.isHeader) {
                    selectedId = sectionWorkType.t.getId();
                    selectedWorkType = sectionWorkType.t;
                    adapter.notifyDataSetChanged();
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupEmptyView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_confirm:
                confirmSelectWorkType();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void confirmSelectWorkType() {
        if (selectedWorkType == null) {
            Toasty.warning(this, R.string.please_select_work_type).show();
            return;
        }

        RegisterData.shared().workType = selectedWorkType;
        finish();
    }

    private void setupEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) recyclerView.getParent(), false);
        adapter.setEmptyView(emptyView);
    }

    private void fetchHotWorkType() {
        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<List<WorkType>>> call = apiService.queryHotWorkType();

        startApiCall(call, new ApiResultHandler<List<WorkType>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            boolean showLoading() {
                return false;
            }

            @Override
            void apiSucceed(List<WorkType> workTypeList) {
                fetchHotSucceed(workTypeList);
            }
        });
    }

    private void fetchAllWorkType() {
        ApiService apiService = HuJiangServer.createService();
        Call<ApiResp<List<WorkType>>> call = apiService.queryWorkType();

        startApiCall(call, new ApiResultHandler<List<WorkType>>() {
            @Override
            String defaultErrorMessage() {
                return getString(R.string.loading_failed);
            }

            @Override
            void apiSucceed(List<WorkType> workTypeList) {
                fetchAllSucceed(workTypeList);
            }
        });
    }

    private void fetchHotSucceed(List<WorkType> workTypeList) {
        hotWorkTypes = workTypeList;
        setupAdapterNewData(hotWorkTypes, allWorkTypes);
    }

    private void fetchAllSucceed(List<WorkType> workTypeList) {
        allWorkTypes = workTypeList;
        setupAdapterNewData(hotWorkTypes, allWorkTypes);
    }

    private void setupAdapterNewData(List<WorkType> hotList, List<WorkType> allList) {
        List<SectionWorkType> newData = new ArrayList<>();
        if (hotList != null && 0 < hotList.size()) {
            newData.add(new SectionWorkType(true, getString(R.string.hot_work_type)));

            for (WorkType workType : hotList) {
                newData.add(new SectionWorkType(workType));
            }
        }

        if (allList != null && 0 < allList.size()) {
            newData.add(new SectionWorkType(true, getString(R.string.all_work_type)));

            for (WorkType workType : allList) {
                newData.add(new SectionWorkType(workType));
            }
        }

        adapter.setNewData(newData);
    }

    private class SectionWorkType extends SectionEntity<WorkType> {
        public SectionWorkType(boolean isHeader, String header) {
            super(isHeader, header);
        }

        public SectionWorkType(WorkType workType) {
            super(workType);
        }
    }

    private class MyAdapter extends BaseSectionQuickAdapter<SectionWorkType, BaseViewHolder> {
        private Context context;

        public MyAdapter(Context context, int layoutResId, int headerResId, @Nullable List<SectionWorkType> data) {
            super(layoutResId, headerResId, data);
            this.context = context;
        }

        @Override
        protected void convertHead(BaseViewHolder helper, SectionWorkType item) {
            helper.setText(R.id.headerTextView, item.header);
        }

        @Override
        protected void convert(BaseViewHolder helper, SectionWorkType item) {
            helper.setText(R.id.workTypeTextView, item.t.getTitle());

            if (item.t.getId() == selectedId) {
                helper.setTextColor(R.id.workTypeTextView, getResources().getColor(R.color.colorPrimary));
            } else {
                helper.setTextColor(R.id.workTypeTextView, getResources().getColor(R.color.black25PercentColor));
            }
        }
    }

    @OnClick(R.id.searchMaskView)
    protected void searchMaskViewClicked(View view) {
        searchMaskView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editTextSearch.setVisibility(View.VISIBLE);
                editTextSearch.setFocusableInTouchMode(true);
                editTextSearch.requestFocus();

                final InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);
    }

    private void setupSearchEditText() {
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (StringUtils.isEmpty(editTextSearch.getText().toString())) {
                        editTextSearch.setVisibility(View.INVISIBLE);
                        searchMaskView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        editTextSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    String searchText = editTextSearch.getText().toString().trim();
                    searchEditTextChanged(searchText);
                    editTextSearch.clearFocus();
                }

                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                searchEditTextChanged(searchText);
            }
        });
    }

    private void searchEditTextChanged(String searchText) {
        if (StringUtils.isEmpty(searchText)) {
            setupAdapterNewData(hotWorkTypes, allWorkTypes);
        } else {
            List<WorkType> filteredHot = new ArrayList<>();
            for (WorkType type : hotWorkTypes) {
                String title = type.getTitle();
                if (!StringUtils.isEmpty(title) && title.contains(searchText)) {
                    filteredHot.add(type);
                }
            }

            List<WorkType> filteredAll = new ArrayList<>();
            for (WorkType type : allWorkTypes) {
                String title = type.getTitle();
                if (!StringUtils.isEmpty(title) && title.contains(searchText)) {
                    filteredAll.add(type);
                }
            }

            setupAdapterNewData(filteredHot, filteredAll);
        }
    }
}
