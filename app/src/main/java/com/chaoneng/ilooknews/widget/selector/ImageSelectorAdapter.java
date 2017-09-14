package com.chaoneng.ilooknews.widget.selector;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chaoneng.ilooknews.R;
import com.chaoneng.ilooknews.library.glide.ImageLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by magical on 17/8/23.
 * Description : 选图片的适配器
 */

public class ImageSelectorAdapter extends RecyclerView.Adapter<ImageSelectorAdapter.ImageHolder> {

    private List<String> mDataList = new ArrayList<>();
    private Context context;
    private ImageSelectorCallback callback;
    private int MAX = 9;

    public ImageSelectorAdapter(Context context, List<String> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate =
                LayoutInflater.from(context).inflate(R.layout.item_image_selector, parent, false);

        return new ImageHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, int position) {

        if (position < mDataList.size()) {
            final String url = mDataList.get(position);
            ImageLoader.loadImage(url, holder.imageView);
            holder.closeView.setVisibility(View.VISIBLE);

            holder.closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != callback) {
                        callback.onRemove(holder.getAdapterPosition());
                    }
                }
            });
            holder.imageView.setOnClickListener(null);
        } else if (position == mDataList.size()) {

            holder.closeView.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.wpk_bbs_img_add_normal);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) {
                        callback.onAdd();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size() >= MAX ? MAX : mDataList.size() + 1;
    }

    public void setOnImageCallback(ImageSelectorCallback callback) {
        this.callback = callback;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_item_image) ImageView imageView;
        @BindView(R.id.id_item_close) ImageView closeView;

        public ImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
