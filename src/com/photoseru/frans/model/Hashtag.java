package com.photoseru.frans.model;

public class Hashtag {

	int id;
	int status;
	int iduser;
	String hashtag;
	String taken_time;
	String desc;
	String start;
	String end;

	// constructors
	public Hashtag() {
	}

	public Hashtag(String hashtag, int status) {
		this.hashtag = hashtag;
		this.status = status;
	}

	public Hashtag(int id, String hashtag, int status) {
		this.id = id;
		this.hashtag = hashtag;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public String getTaken_time() {
		return taken_time;
	}

	public void setTaken_time(String taken_time) {
		this.taken_time = taken_time;
	}

	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	

}
