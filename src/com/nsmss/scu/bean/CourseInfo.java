package com.nsmss.scu.bean;

// CourseInfo���ڼ�¼�γ���Ϣ
public class CourseInfo {
	
	private int cid;		// ���ݼ�¼��ص�id
	
	private int weekfrom;	// ��ʼ��
	private int weekto;		// ������
	private int weektype;	// �����ͣ�1��ͨ��2���ܣ�3˫��
	private int day;		// �ܼ�
	private int lessonfrom;	// ��ʼ�ڴ�
	private int lessonto;	// �����ڴ�
	
	private String courseid;	// �γ̺�
	private String num;			// �����
	private String name;		// �γ���
	private String credit;		// ѧ��
	private String attr;		// ���ԣ����ޣ�ѡ��
	private String exam;		// �������ͣ����ԣ�����
	private String teacher;		// ��ʦ
	private String campus;		// У��
	private String bld;			// ��ѧ¥
	private String place;		// ����
	
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getWeekfrom() {
		return weekfrom;
	}
	public void setWeekfrom(int weekfrom) {
		this.weekfrom = weekfrom;
	}
	public int getWeekto() {
		return weekto;
	}
	public void setWeekto(int weekto) {
		this.weekto = weekto;
	}
	public int getWeektype() {
		return weektype;
	}
	public void setWeektype(int weektype) {
		this.weektype = weektype;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getLessonfrom() {
		return lessonfrom;
	}
	public void setLessonfrom(int lessonfrom) {
		this.lessonfrom = lessonfrom;
	}
	public int getLessonto() {
		return lessonto;
	}
	public void setLessonto(int lessonto) {
		this.lessonto = lessonto;
	}
	public String getCourseid() {
		return courseid;
	}
	public void setCourseid(String courseid) {
		this.courseid = courseid;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getExam() {
		return exam;
	}
	public void setExam(String exam) {
		this.exam = exam;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getCampus() {
		return campus;
	}
	public void setCampus(String campus) {
		this.campus = campus;
	}
	public String getBld() {
		return bld;
	}
	public void setBld(String bld) {
		this.bld = bld;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	
}