package com.tutor.model;

import java.sql.Date;

public class Order {
private String id;
private int user_id;
private int tutor_id;
private int order_status;
private int order_num;
private Date begain_time;
private Date end_time;
private String comment;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public int getUser_id() {
	return user_id;
}
public void setUser_id(int user_id) {
	this.user_id = user_id;
}
public int getTutor_id() {
	return tutor_id;
}
public void setTutor_id(int tutor_id) {
	this.tutor_id = tutor_id;
}
public int getOrder_status() {
	return order_status;
}
public void setOrder_status(int order_status) {
	this.order_status = order_status;
}
public int getOrder_num() {
	return order_num;
}
public void setOrder_num(int order_num) {
	this.order_num = order_num;
}
public Date getBegain_time() {
	return begain_time;
}
public void setBegain_time(Date begain_time) {
	this.begain_time = begain_time;
}
public Date getEnd_time() {
	return end_time;
}
public void setEnd_time(Date end_time) {
	this.end_time = end_time;
}
public String getComment() {
	return comment;
}
public void setComment(String comment) {
	this.comment = comment;
}
public Order(int user_id, int tutor_id, int order_status, int order_num, Date begain_time, Date end_time,
		String comment) {
	super();
	this.user_id = user_id;
	this.tutor_id = tutor_id;
	this.order_status = order_status;
	this.order_num = order_num;
	this.begain_time = begain_time;
	this.end_time = end_time;
	this.comment = comment;
}
public Order() {
	super();
}
@Override
public String toString() {
	return "Order [id=" + id + ", user_id=" + user_id + ", tutor_id=" + tutor_id + ", order_status=" + order_status
			+ ", order_num=" + order_num + ", begain_time=" + begain_time + ", end_time=" + end_time + ", comment="
			+ comment + "]";
}

}
