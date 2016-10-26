package com.photoseru.frans.model;

public class Photo {

	int iduser;
	int idphoto;
	String name;
	String take_time;

	// constructors
	public Photo() {

	}

	public int getIdphoto() {
		return idphoto;
	}

	public int getIduser() {
		return iduser;
	}

	public String getName() {
		return name;
	}

	public String getTake_time() {
		return take_time;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTake_time(String take_time) {
		this.take_time = take_time;
	}
	
	public void setIdphoto(int idphoto) {
		this.idphoto = idphoto;
	}

}
