package cn.edu.sdu.online.entity;

import java.io.Serializable;

public class Task implements Serializable {
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public String getKey3() {
		return key3;
	}

	public void setKey3(String key3) {
		this.key3 = key3;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSpiritAward() {
		return spiritAward;
	}

	public void setSpiritAward(String spiritAward) {
		this.spiritAward = spiritAward;
	}

	public String getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(String limitTime) {
		this.limitTime = limitTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public int getAwardStatus() {
		return awardStatus;
	}

	public void setAwardStatus(int awardStatus) {
		this.awardStatus = awardStatus;
	}

	public int getScoreAward() {
		return scoreAward;
	}

	public void setScoreAward(int scoreAward) {
		this.scoreAward = scoreAward;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getTipAward() {
		return tipAward;
	}

	public void setTipAward(int tipAward) {
		this.tipAward = tipAward;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	private String id, createTime, key1, key2, key3, content, spiritAward,
			limitTime, userId, details, location, destination;

	private int awardStatus, scoreAward, state, tipAward;
	private double location_x, location_y, location_x1, location_y1;
	// /涉及到用户信息的
	private String email, nickName, school, phoneNo, weixin, qq;
	private int sex, level;

	public double getLocation_x() {
		return location_x;
	}

	public void setLocation_x(double location_x) {
		this.location_x = location_x;
	}

	public double getLocation_y() {
		return location_y;
	}

	public void setLocation_y(double location_y) {
		this.location_y = location_y;
	}

	public double getLocation_x1() {
		return location_x1;
	}

	public void setLocation_x1(double location_x1) {
		this.location_x1 = location_x1;
	}

	public double getLocation_y1() {
		return location_y1;
	}

	public void setLocation_y1(double location_y1) {
		this.location_y1 = location_y1;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	// 在list方法里面重谢equals方法才可以比较
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task u = (Task) obj;
			if (this.id.equals(u.getId()) || this.userId.equals(u.getUserId()))
				return true;
			else
				return false;

		}
		return super.equals(obj);
	}
}