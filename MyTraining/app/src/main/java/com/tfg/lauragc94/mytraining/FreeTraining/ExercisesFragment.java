package com.tfg.lauragc94.mytraining.FreeTraining;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tfg.lauragc94.mytraining.MenuActivity;
import com.tfg.lauragc94.mytraining.R;

import java.util.ArrayList;

/**
 * Created by SONU on 16/09/15.
 */
public class ExercisesFragment extends Fragment {
    private static final String TAB_TITLE = "tab_title";
    private View view;
    ArrayList<String> list_exercises = new ArrayList<>();

    private String tab_title;//String for tab title

    public ExercisesFragment() {
    }

    /**
     * static instance of ExercisesFragment
     * in this method we will pass the title of our selected tab via Bundle
     *
     * @param tab_title tab title
     **/
    public static ExercisesFragment newInstance(String tab_title) {

        Bundle args = new Bundle();
        args.putString(TAB_TITLE, tab_title);
        ExercisesFragment fragment = new ExercisesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fetch the title from passed arguments
        tab_title = getArguments().getString(TAB_TITLE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.exercises_fragment, container, false);
            setRecyclerView();

        return view;

    }

    //Setting recycler view
    private void setRecyclerView() {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//Linear Items

        if(tab_title.equals("MUSCULACIÓN")) {
            recyclerView.setAdapter(MenuActivity.adapter1);// set adapter on recyclerview
        }
        if(tab_title.equals("PÉRDIDA DE PESO")) {
            recyclerView.setAdapter(MenuActivity.adapter2);// set adapter on recyclerview
        }
        if(tab_title.equals("MANTENIMIENTO")) {
            recyclerView.setAdapter(MenuActivity.adapter3);// set adapter on recyclerview
        }

    }

}
