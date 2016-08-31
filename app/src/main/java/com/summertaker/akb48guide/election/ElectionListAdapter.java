package com.summertaker.akb48guide.election;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseDataAdapter;
import com.summertaker.akb48guide.data.ElectionData;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class ElectionListAdapter extends BaseDataAdapter {

    private Context mContext;
    private Resources mResources;
    private LayoutInflater mLayoutInflater;

    ArrayList<ElectionData> mDataList = new ArrayList<>();

    public ElectionListAdapter(Context context, ArrayList<ElectionData> dataList) {
        this.mContext = context;
        this.mResources = mContext.getResources();
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
        ViewHolder holder;
        ElectionData data = mDataList.get(position);

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.election_list_item, null);
            //view = mLayoutInflater.inflate(R.layout.election_list_item_text, null);

            holder = new ViewHolder();
            holder.ivPicture = (ImageView) view.findViewById(R.id.ivMatchMemberPicture);
            holder.tvCount = (TextView) view.findViewById(R.id.tvCountIcon);
            holder.tvCountSuffix = (TextView) view.findViewById(R.id.tvCountSuffix);
            holder.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            holder.tvCatchPhrase = (TextView) view.findViewById(R.id.tvCatchPhrase);
            holder.tvDate = (TextView) view.findViewById(R.id.tvDate);
            holder.tvPlace = (TextView) view.findViewById(R.id.tvPlace);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(mContext).load(data.getDrawable()).into(holder.ivPicture);

        int count = data.getCount();
        String countText = count + "";
        holder.tvCount.setText(countText);
        String countSuffix;
        String locale = Util.getLocaleStrng(mContext);
        switch (locale) {
            case "KR":
            case "JP":
                countSuffix = mResources.getString(R.string.suffix_time);
                break;
            default:
                countSuffix = Util.getOrdinal(count);
                break;
        }
        holder.tvCountSuffix.setText(countSuffix);

        String title = mResources.getString(R.string.akb48_nth_single_selected_general_election);
        int singleNumber = data.getSingleNumber();
        String singleNumberSuffix = Util.getOrdinal(singleNumber);
        title = String.format(title, singleNumber + singleNumberSuffix);
        holder.tvTitle.setText(title);

        String catchPhrase = data.getCatchPhrase();
        holder.tvCatchPhrase.setText(catchPhrase);

        String date = data.getDate();
        holder.tvDate.setText(date);

        String place = data.getPlace();
        holder.tvPlace.setText(place);

        return view;
    }

    static class ViewHolder {
        ImageView ivPicture;
        TextView tvCount;
        TextView tvCountSuffix;
        TextView tvTitle;
        TextView tvCatchPhrase;
        TextView tvDate;
        TextView tvPlace;
    }
}
