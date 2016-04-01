package com.radiuslabs.locus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiuslabs.locus.R;
import com.radiuslabs.locus.models.User;

import java.util.ArrayList;
import java.util.List;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_interest, new LinearLayout(context));

        ViewHolder vh = new ViewHolder(v);

        vh.text = (TextView) v.findViewById(R.id.tvInterest);
        vh.icon = (ImageView) v.findViewById(R.id.ivInterestLogo);
        vh.checkBox = (CheckBox) v.findViewById(R.id.checkbox);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(interests.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }

        ImageView icon;
        TextView text;
        CheckBox checkBox;
    }

}
