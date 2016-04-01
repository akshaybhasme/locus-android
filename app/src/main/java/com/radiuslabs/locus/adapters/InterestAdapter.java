package com.radiuslabs.locus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiuslabs.locus.R;
import com.radiuslabs.locus.models.User;

import java.util.ArrayList;
import java.util.List;

public class InterestAdapter extends BaseAdapter {

    private List<User.Interest> interests;
    private LayoutInflater inflater;
    private Context context;

    public InterestAdapter(Context context){
        inflater = LayoutInflater.from(context);
        this.context = context;
        interests = new ArrayList<>();
    }

    public void setInterests(List<User.Interest> interests){
        this.interests = interests;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return interests.size();
    }

    @Override
    public Object getItem(int i) {
        return interests.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder;
        if(row == null) {
            row = inflater.inflate(R.layout.row_interest, new LinearLayout(context));

            holder = new ViewHolder();
            holder.icon = (ImageView) row.findViewById(R.id.ivInterestLogo);
            holder.text = (TextView) row.findViewById(R.id.tvInterest);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkbox);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.text.setText(interests.get(i).getText());

        return row;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder{
        ImageView icon;
        TextView text;
        CheckBox checkBox;
    }

}
