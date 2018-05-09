package com.tfg.lauragc94.mytraining.Entities;

/* Entity of the routine */

public class Workout {

	private int ID_workout; //routine's ID
	private String Name;
	private String Description;
	private String USER_phone; //user's ID

	public Workout() {
	}

	public Workout(int ID_workout, String Name, String Description, String USER_phone) {
		this.ID_workout = ID_workout;
		this.Name = Name;
		this.Description = Description;
		this.USER_phone = USER_phone;
	}

	public int getID_workout() {
		return ID_workout;
	}
	public void setID_workout(int ID_routine) {
		this.ID_workout = ID_routine;
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
	public String getUSER_phone() {
		return USER_phone;
	}
	public void setUSER_phone(String USER_phone) {
		this.USER_phone = USER_phone;
	}

}