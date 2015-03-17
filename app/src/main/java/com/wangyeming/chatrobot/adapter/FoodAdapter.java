package com.wangyeming.chatrobot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wangyeming.chatrobot.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Wang
 * @data 2015/3/17
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>  {

    private List<Map<String, Object>> foodDisplay = new ArrayList<>();
    private Context context;
    private LayoutInflater mInflater;

    public FoodAdapter(Context context, List<Map<String, Object>> foodDisplay) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.foodDisplay = foodDisplay;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_display, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.pic = (ImageView) view.findViewById(R.id.pic);
        vh.title = (TextView) view.findViewById(R.id.info_title);
        vh.rawMaterial = (TextView) view.findViewById(R.id.raw_material);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int i) {
        String name = (String) foodDisplay.get(i).get("name");
        String info = (String) foodDisplay.get(i).get("info");
        String detailUrl = (String) foodDisplay.get(i).get("detailUrl");
        String icon = (String) foodDisplay.get(i).get("icon");
        vh.title.setText(name);
        vh.rawMaterial.setText(info);
        Picasso.with(context).load(icon).into(vh.pic);
        //vh.pic.setBackgroundResource(R.mipmap.tc1);
    }

    @Override
    public int getItemCount() {
        return foodDisplay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView pic;
        public TextView title;
        public TextView rawMaterial;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
