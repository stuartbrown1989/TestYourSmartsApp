package com.example.testyoursmarts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class leaderboardAdapter extends BaseAdapter {
    private List<leaderboardModel> cat_List;

    public leaderboardAdapter(List<leaderboardModel> cat_List) {
        this.cat_List = cat_List;
    }
    @Override
    public int getCount() {
        return cat_List.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View myView;


        if(view == null)
        {
            myView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cat_item_layout,viewGroup, false);
        }
        else
        {
            myView = view;
        }


        TextView catName = myView.findViewById(R.id.cat_name);


        catName.setText(cat_List.get(i).getName());


        return myView;
    }
}
