package com.radiuslabs.locus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiuslabs.locus.R;
import com.radiuslabs.locus.models.DrawerItem;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends BaseAdapter {

    private List<DrawerItem> items;

    private LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }

    public List<DrawerItem> getItems() {
        return items;
    }

    public void setItems(List<DrawerItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_drawer_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageResource(items.get(i).getIcon());
        holder.title.setText(items.get(i).getTitle());
        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
    }
}
