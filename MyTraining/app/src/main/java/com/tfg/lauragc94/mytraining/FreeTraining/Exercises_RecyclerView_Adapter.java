package com.tfg.lauragc94.mytraining.FreeTraining;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tfg.lauragc94.mytraining.Entities.Exercise;
import com.tfg.lauragc94.mytraining.R;

import java.util.ArrayList;

/**
 * Created by SONU on 10/09/15.
 */
public class Exercises_RecyclerView_Adapter extends
        RecyclerView.Adapter<Exercises_ViewHolder> {
    private ArrayList<Exercise> list_exercises;
    private Context context;


    public Exercises_RecyclerView_Adapter(Context context,
                                          ArrayList<Exercise> list_exercises) {
        this.context = context;
        this.list_exercises = list_exercises;

    }


    @Override
    public int getItemCount() {
        return (null != list_exercises ? list_exercises.size() : 0);

    }

    @Override
    public void onBindViewHolder(Exercises_ViewHolder holder,
                                 int position) {


        final Exercises_ViewHolder mainHolder = (Exercises_ViewHolder) holder;
        //Setting text over textview
        mainHolder.exercise_name.setText(list_exercises.get(position).getName());

    }

    @Override
    public Exercises_ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.exercises_item, viewGroup, false);
        Exercises_ViewHolder mainHolder = new Exercises_ViewHolder(mainGroup) {
            @Override
            public String toString() {
                return super.toString();
            }
        };


        return mainHolder;

    }


}