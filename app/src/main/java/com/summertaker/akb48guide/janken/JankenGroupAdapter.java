package com.summertaker.akb48guide.janken;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseDataAdapter;
import com.summertaker.akb48guide.data.GroupData;

import java.util.ArrayList;

public class JankenGroupAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<GroupData> mDataList = new ArrayList<>();

    public JankenGroupAdapter(Context context, ArrayList<GroupData> dataList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = mLayoutInflater.inflate(R.layout.janken_group_item, null);

            holder = new ViewHolder();
            holder.ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
            holder.ivLock = (ImageView) view.findViewById(R.id.ivLock);
            holder.tvCaption = (TextView) view.findViewById(R.id.tvCaption);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        GroupData item = mDataList.get(position);

        String caption = item.getName();
        if (item.isLocked()) {
            holder.ivPicture.setImageResource(R.drawable.card_background);
            holder.ivLock.setVisibility(View.VISIBLE);
            caption = "잠김";
        } else {
            holder.ivPicture.setImageResource(item.getImage());
            holder.ivLock.setVisibility(View.GONE);
        }
        holder.tvCaption.setText(caption);

        //Log.e(mTag, "item.getName(): " + item.getName());

        return view;
    }

    static class ViewHolder {
        ImageView ivPicture;
        ImageView ivLock;
        TextView tvCaption;
    }
}
