package com.tfg.lauragc94.mytraining.Entities;

/* Entity of the exercise */

public class Exercise {

	private int ID_exercise; // exercise's ID
	private String Name;
	private String Description;
	private String Objective;
	private String Level;
	private int Series;
	private int Repetitions;


	public Exercise() {
	}

	public Exercise(int ID_exercise, String Name, String Description,String Objective, String Level,int Series, int Repetitions) {
		this.ID_exercise = ID_exercise;
		this.Name = Name;
		this.Description = Description;
		this.Objective = Objective;
		this.Level = Level;
		this.Series = Series;
		this.Repetitions = Repetitions;
	}

	public int getID_exercise() {
		return ID_exercise;
	}

	public void setID_exercise(int ID_exercise) {
		this.ID_exercise = ID_exercise;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String Description) {
		this.Description = Description;
	}

	public String getObjective() {
		return Objective;
	}

	public void setObjective(String Objective) {
		this.Objective = Objective;
	}

	public String getLevel() {
		return Level;
	}

	public void setLevel(String Level) {
		this.Level = Level;
	}


	public int getSeries() {
		return Series;
	}

	public void setSeries(int Series) {
		this.Series = Series;
	}

	public int getRepetitions() {
		return Repetitions;
	}

	public void setRepetitions(int Repetitions) {
		this.Repetitions = Repetitions;
	}

}
	


