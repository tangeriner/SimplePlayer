package com.sunny.simple_video.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sunny on 2017/8/8.
 */

public abstract class BaseSingleAdapter<T extends RecyclerView.ViewHolder, E> extends RecyclerView.Adapter<T> {
    private int mLayoutId;
    private List<E> mList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private boolean isScrolling;

    public BaseSingleAdapter(int layoutId, List<E> list) {
        this.mLayoutId = layoutId;
        this.mList = list;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return createViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final T holder, int position) {
        final int pos = position;
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, pos);
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mOnItemLongClickListener.onItemLongClick(v, pos);
                }
            });
        }
        convert(holder, mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    public abstract T createViewHolder(View itemView);

    public abstract void convert(T holder, E e);

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int pos);
    }
}
