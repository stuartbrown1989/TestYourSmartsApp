package com.example.testyoursmarts;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class testAdapter extends BaseAdapter {

    private List<TestModel> cat_List;

    public testAdapter(List<TestModel> cat_List) {
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
