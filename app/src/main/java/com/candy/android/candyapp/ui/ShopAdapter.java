package com.candy.android.candyapp.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.candyapp.view.ColorGenerator;
import com.candy.android.candyapp.view.TextDrawable;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by marcingawel on 12.06.2016.
 */

public class ShopAdapter <T extends ShopAdapter.ViewHolder> extends RecyclerView.Adapter<T> {

    private List<? extends ModelViewHolder> items;
    private ShopAdapter.ViewHolder.Builder<T> builder;
    private LayoutInflater inflater;
    private AdapterClickListener listener;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();
    private ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

    public ShopAdapter(Context context, List<? extends ModelViewHolder> items, ShopAdapter.ViewHolder.Builder<T> builder,
                       AdapterClickListener listener) {
        this.items = items;
        this.listener = listener;
        this.builder = builder;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return builder.newInstance(inflater.inflate(builder.getLayout(), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        holder.bind(items.get(position), drawableBuilder, colorGenerator);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static abstract class ViewHolder<T extends ModelViewHolder> extends RecyclerView.ViewHolder {

        public interface Builder<T extends ViewHolder> {
            T newInstance(View view, AdapterClickListener listener);
            @LayoutRes
            int getLayout();
        }

        public ViewHolder(View itemView, AdapterClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener((v) -> listener.onClick(getAdapterPosition()));
        }

        public abstract void bind(T item, TextDrawable.IBuilder builder, ColorGenerator color);
    }
}