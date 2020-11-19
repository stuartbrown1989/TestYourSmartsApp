package com.example.testyoursmarts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentSelectTopic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentSelectTopic extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridView catView;
    private List<CategoryModel> catList = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mtopicParam1;
    private String mtopicParam2;

    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mtopicParam1 = getArguments().getString(ARG_PARAM1);
            mtopicParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        catView = view.findViewById(R.id.cat_Grid);

        loadCategories();
        CategoryAdapter adapter = new CategoryAdapter(catList);
        catView.setAdapter(adapter);


        return view;


    }

    private void loadCategories()
    {
        catList.clear();
        catList.add(new CategoryModel("6","History", 20));
        catList.add(new CategoryModel("7","Science", 30));
        catList.add(new CategoryModel("8","Sports", 10));
        catList.add(new CategoryModel("9","Film/TV", 25));
        catList.add(new CategoryModel("10","Geography", 11));
        catList.add(new CategoryModel("11","Music", 6));




    }
}