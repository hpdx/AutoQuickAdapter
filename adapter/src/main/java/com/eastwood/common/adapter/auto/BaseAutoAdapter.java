package com.eastwood.common.adapter.auto;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eastwood.common.adapter.BaseAdapterHelper;
import com.eastwood.common.adapter.BaseQuickAdapter;
import com.eastwood.common.adapter.R;
import com.eastwood.common.adapter.ViewHelper;

import java.util.List;

public abstract class BaseAutoAdapter<T> extends BaseQuickAdapter<T, BaseAdapterHelper> {

    protected boolean autoLoadEnable;

    protected boolean manualLoad;

    protected boolean loadError;
    protected boolean loadEnd;
    protected boolean loading;

    private int loadingLayoutResId;
    private int manualLayoutResId;
    private int loadEndLayoutResId;
    private int loadErrorLayoutResId;

    protected boolean alwaysShowHeader = false;

    protected OnAutoLoadListener mOnAutoLoadListener;
    protected View.OnClickListener mOnLastItemClickListener;
    protected View.OnClickListener mEmptyClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    public BaseAutoAdapter() {
        super();
    }

    public BaseAutoAdapter(Context context, int layoutResId) {
        super(context, layoutResId, null);
    }

    public BaseAutoAdapter(Context context, int layoutResId, List<T> data) {
        super(context, layoutResId, data);
    }

    protected abstract int getBodyCount();

    @Override
    public int getCount() {
        return getBodyCount() + (autoLoadEnable ? 1 : 0);
    }

    @Override
    public T getItem(int position) {
        if (position >= data.size()) return null;
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            final ViewHelper helper = getAdapterHelper(position, convertView, parent);
            T item = getItem(position);
            convert(position, helper, item);
            return helper.getView();
        }
        return createAutoLoadView(convertView, parent);
    }

    protected View createAutoLoadView(View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (loadingLayoutResId == 0) {
            loadingLayoutResId = resolve(context, R.attr.auto_adapter_loading_layout, R.layout.default_loading_layout);
        }
        if (manualLayoutResId == 0) {
            manualLayoutResId = resolve(context, R.attr.auto_adapter_manual_layout, R.layout.default_manual_layout);
        }
        if (loadEndLayoutResId == 0) {
            loadEndLayoutResId = resolve(context, R.attr.auto_adapter_end_layout, R.layout.default_end_layout);
        }
        if (loadErrorLayoutResId == 0) {
            loadErrorLayoutResId = resolve(context, R.attr.auto_adapter_error_layout, R.layout.default_error_layout);
        }

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (loadError) {
            convertView = layoutInflater.inflate(loadErrorLayoutResId, parent, false);
            convertView.setOnClickListener(mOnLastItemClickListener);
        } else if (loadEnd) {
            convertView = layoutInflater.inflate(loadEndLayoutResId, parent, false);
            convertView.setOnClickListener(mEmptyClick);
        } else {
            if (!manualLoad || loading) {
                if (!loading && mOnAutoLoadListener != null) {
                    mOnAutoLoadListener.onLoading();
                }
                convertView = layoutInflater.inflate(loadingLayoutResId, parent, false);
                convertView.setOnClickListener(mEmptyClick);
            } else {
                convertView = layoutInflater.inflate(manualLayoutResId, parent, false);
                convertView.setOnClickListener(mOnLastItemClickListener);
            }
        }
        return convertView;
    }

    public void setAutoLoadEnable(boolean enable) {
        if (enable == autoLoadEnable) return;
        autoLoadEnable = enable;
        notifyDataSetChanged();
    }

    public boolean isAutoLoadEnable() {
        return autoLoadEnable;
    }

    @Deprecated
    public void setAutoLoadUsable(boolean usable) {
        setAutoLoadEnable(usable);
    }

    @Deprecated
    public boolean isAutoLoadUsable() {
        return isAutoLoadEnable();
    }

    public boolean isManualLoad() {
        return manualLoad;
    }

    public void setManualLoad(boolean manualLoad) {
        if (manualLoad == this.manualLoad) return;
        this.manualLoad = manualLoad;
        notifyDataSetChanged();
    }

    public boolean isLoadError() {
        return loadError;
    }

    public void setLoadError(boolean loadError) {
        if (loadError == this.loadError) return;
        this.loadError = loadError;
        notifyDataSetChanged();
    }

    public boolean isLoadEnd() {
        return loadEnd;
    }

    public void setLoadEnd(boolean loadEnd) {
        if (loadEnd == this.loadEnd) return;
        this.loadEnd = loadEnd;
        notifyDataSetChanged();
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        if (loading == this.loading) return;
        this.loading = loading;
        notifyDataSetChanged();
    }

    public void setLoadingLayoutResId(int resId) {
        this.loadingLayoutResId = resId;
    }

    public int getLoadingLayoutResId() {
        return loadingLayoutResId;
    }

    public void setManualLayoutResId(int resId) {
        this.manualLayoutResId = resId;
    }

    public int getManualLayoutResId() {
        return manualLayoutResId;
    }

    public int getLoadEndLayoutResId() {
        return loadEndLayoutResId;
    }

    public void setLoadEndLayoutResId(int loadEndLayoutResId) {
        this.loadEndLayoutResId = loadEndLayoutResId;
    }

    public int getLoadErrorLayoutResId() {
        return loadErrorLayoutResId;
    }

    public void setLoadErrorLayoutResId(int loadErrorLayoutResId) {
        this.loadErrorLayoutResId = loadErrorLayoutResId;
    }

    public void setOnAutoLoadListener(OnAutoLoadListener listener) {
        mOnAutoLoadListener = listener;
    }

    public void setOnLastItemClickListener(View.OnClickListener listener) {
        this.mOnLastItemClickListener = listener;
    }

    public void setAlwaysShowHeader(boolean alwaysShowHeader) {
        this.alwaysShowHeader = alwaysShowHeader;
    }

    public boolean isAlwaysShowHeader() {
        return alwaysShowHeader;
    }

    @Override
    public boolean isEmpty() {
        if (alwaysShowHeader) {
            return false;
        }
        return super.isEmpty();
    }

    public void onAutoLoadError() {
        loadError = true;
        notifyDataSetChanged();
    }

    public void onAutoLoadStart() {
        manualLoad = false;
        loading = false;
        loadError = false;
        loadEnd = false;
        notifyDataSetChanged();
    }

    public void onAutoLoadFinished(boolean loadEnd) {
        this.loading = false;
        this.loadError = false;
        this.loadEnd = loadEnd;
        notifyDataSetChanged();
    }

    private int resolve(Context context, int attr, int defValue) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getResourceId(0, defValue);
        } finally {
            a.recycle();
        }
    }

}
