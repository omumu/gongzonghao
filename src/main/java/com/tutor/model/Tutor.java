package com.tutor.model;

public class Tutor {
	private int tutorId;
	private String tutorName;
	private String tutorTitle;
	private String tutorLabel;
	private String tutorUrl;
	private String tutorTime;
	private String description;
	private String recommendedInf;
	private String price;
	private String tutorOrder;
	private String status;
	private int page;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTutorId() {
		return tutorId;
	}

	public void setTutorId(int tutorId) {
		this.tutorId = tutorId;
	}

	public String getTutorName() {
		return tutorName;
	}

	public void setTutorName(String tutorName) {
		this.tutorName = tutorName;
	}

	public String getTutorTitle() {
		return tutorTitle;
	}

	public void setTutorTitle(String tutorTitle) {
		this.tutorTitle = tutorTitle;
	}

	public String getTutorLabel() {
		return tutorLabel;
	}

	public void setTutorLabel(String tutorLabel) {
		this.tutorLabel = tutorLabel;
	}

	public String getTutorUrl() {
		return tutorUrl;
	}

	public void setTutorUrl(String tutorUrl) {
		this.tutorUrl = tutorUrl;
	}

	public String getTutorTime() {
		return tutorTime;
	}

	public void setTutorTime(String tutorTime) {
		this.tutorTime = tutorTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecommendedInf() {
		return recommendedInf;
	}

	public void setRecommendedInf(String recommendedInf) {
		this.recommendedInf = recommendedInf;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTutorOrder() {
		return tutorOrder;
	}

	public void setTutorOrder(String tutorOrder) {
		this.tutorOrder = tutorOrder;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Tutor [tutorId=" + tutorId + ", tutorName=" + tutorName + ", tutorTitle=" + tutorTitle + ", tutorLabel="
				+ tutorLabel + ", tutorUrl=" + tutorUrl + ", tutorTime=" + tutorTime + ", description=" + description
				+ ", recommendedInf=" + recommendedInf + ", price=" + price + ", tutorOrder=" + tutorOrder + ", status="
				+ status + ", page=" + page + "]";
	}

	public Tutor() {
		super();
	}

}
