package com.tfg.lauragc94.mytraining.FreeTraining;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tfg.lauragc94.mytraining.R;

/**
 * Created by SONU on 31/08/15.
 */
public abstract class Exercises_ViewHolder extends RecyclerView.ViewHolder {


    public Button exercise_name;
    public ImageButton exercise_play;


    Exercises_ViewHolder(View view) {
        super(view);


        this.exercise_name = (Button) view.findViewById(R.id.exercise_name);
        this.exercise_play = (ImageButton) view.findViewById(R.id.exercise_play);

    }


}