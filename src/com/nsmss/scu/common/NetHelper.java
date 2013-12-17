package com.nsmss.scu.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;

import com.nsmss.scu.bean.CourseInfo;
import com.nsmss.scu.bean.PersonalInfo;
import com.nsmss.scu.bean.UserData;

public class NetHelper {
	
	// ������Ϣ����
	private class PInfo {
		public String name;			// ѧ������
		public Date inSchool;		// ��ѧʱ��
		public float creditGot;		// ���ѧ��
		public float creditTotal;	// ��ѧ��
	}
	
	private class CourseProperty {
		public String tmpStringID;
		public String tmpStringName;
		public String tmpStringNum;
		public String tmpStringCredit;
		public String tmpStringAttr;
		public String tmpStringExam;
		public String tmpStringTeacher;
		public String tmpStringWeeks;
		public String tmpStringWeek;
		public String tmpStringLesson;
		public String tmpStringCamp;
		public String tmpStringBld;
		public String tmpStringPlace;
	};
	
	public String login(String num, String passwd) {
		try {
			Connection con = Jsoup.connect("http://202.115.47.141/loginAction.do")
					.data("zjh", num)
					.data("mm",passwd)
					.timeout(10000)
					.method(Method.POST);
			Response response = con.execute();
			Document doc = response.parse();
			
			if(doc.title().equals("ѧ�����ۺϽ���"))
			{
				Iterator<Entry<String, String>> ite = response.cookies().entrySet().iterator();
				while(ite.hasNext()){
					Map.Entry<String, String> entry = ite.next();
					String key = entry.getKey();
					String value = entry.getValue();
					if (key.equals("JSESSIONID"))
						return value;
				}
				return "100";	// TODO �������100��δ��ȡ��SESSION
			}
			
			Element prompt = doc.getElementsByAttributeValue("class", "errorTop").first(); 
			String strong = prompt.select("strong>font").text().trim();	
			
			if(strong.contains("������"))
			{
				return "101";	// TODO �������101��ѧ�Ų�����
			}
			else if(strong.contains("���벻��ȷ"))
			{
				return "102";	// TODO �������102�����벻��ȷ
			}
			else {
				return "103";	// TODO �������103����ҳ��ʽ����
			}
		}
		catch (Exception e) {
			return "104";		// TODO �������104�����ӳ�ʱ�������쳣
		}
	}
	
	/**
	 * ��ȡ�û��ڽ���ϵͳ��SESSIONֵ
	 * @param uData �û�����
	 * @param force �Ƿ�ǿ�Ƹ���SESSION
	 * @return SESSION
	 */
	private String getSession(UserData uData, boolean force) {
		int lastLogin = uData.getLastlogin();
		if (force || (Utility.time() - lastLogin > 1800)) {
			String newSession = login(uData.getNum(), uData.getPasswd());
			return newSession;
		}
		else {
			return uData.getSession();
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public PersonalInfo getPersonalInfo(UserData uData) {
		try {
			String session = getSession(uData, false);
			if (session.length() <= 3) {
				session = getSession(uData, true);
			}
			
			PInfo info = new PInfo();
			PersonalInfo pInfo = new PersonalInfo();
			List <Map<String, String>> data = new ArrayList<Map<String,String>>();
			
			Document doc = Jsoup.connect("http://202.115.47.141/reportFiles/student/cj_zwcjd_all.jsp")
					.cookie("JSESSIONID", session)
					.timeout(10000)
					.get();
			
			// ��ȡ���
			Element report = doc.getElementById("report1");
			Elements tblView_tr = report.select("tr");
			
			// ������������Ϣ�������ݼ�¼��info��
			info.name = tblView_tr.get(1).select("td").eq(2).text().trim();
			String dateIn = tblView_tr.get(3).select("td").eq(4).text().trim();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			info.inSchool = sdf.parse(dateIn);

			// �������ɼ�����������ӵ�data�� 
			String tmpStringAttr= new String();
			String tmpStringSubject= new String();
			String tmpStringCredit= new String();
			String tmpStringGrade= new String();
			String tmpStringTime = new String();
			Map<String, String> tmpMap = new HashMap<String, String>();
			
			// ��һ��ѭ������ȡ����
			for (int j = 7; j < tblView_tr.size(); j++) {
				Element tblView_line = tblView_tr.get(j);
				String height = tblView_line.attr("height").trim();
				if (height.equals("14")) {
					String creditGot = tblView_line.select("td").eq(2).text().trim();
					String creditTotal = tblView_line.select("td").eq(4).text().trim();
					info.creditGot = Float.parseFloat(creditGot);
					info.creditTotal = Float.parseFloat(creditTotal);
					break;
				}
				
				tmpStringAttr = tblView_line.select("td").eq(4).text().trim();
				if (tmpStringAttr.equals("����")) 
					tmpStringAttr = "0";
				else {
					tmpStringAttr = "1";
				}
				
				tmpStringSubject = tblView_line.select("td").eq(1).text().trim();
				tmpStringCredit = tblView_line.select("td").eq(2).text().trim();
				tmpStringGrade = tblView_line.select("td").eq(3).text().trim();
				tmpStringTime = tblView_line.select("td").eq(5).text().trim();
				
				if (tmpStringSubject.length() != 0) {
					tmpMap = new HashMap<String, String>();
					tmpMap.put("attr", tmpStringAttr);
					tmpMap.put("subject", tmpStringSubject);
					tmpMap.put("credit", tmpStringCredit);
					tmpMap.put("grade", tmpStringGrade);
					tmpMap.put("time", tmpStringTime);
					data.add(tmpMap);
				}
			}
			
			// �ڶ���ѭ������ȡ����
			for (int j = 7; j < tblView_tr.size(); j++) {
				Element tblView_line = tblView_tr.get(j);
				String height = tblView_line.attr("height").trim();
				if (!height.equals("12")) {
					break;
				}
				
				tmpStringAttr = tblView_line.select("td").eq(9).text().trim();
				if (tmpStringAttr.equals("����")) 
					tmpStringAttr = "0";
				else {
					tmpStringAttr = "1";
				}
				
				tmpStringSubject = tblView_line.select("td").eq(6).text().trim();
				tmpStringCredit = tblView_line.select("td").eq(7).text().trim();
				tmpStringGrade = tblView_line.select("td").eq(8).text().trim();
				tmpStringTime = tblView_line.select("td").eq(10).text().trim();
				
				if (tmpStringSubject.length() != 0) {
					tmpMap = new HashMap<String, String>();
					tmpMap.put("attr", tmpStringAttr);
					tmpMap.put("subject", tmpStringSubject);
					tmpMap.put("credit", tmpStringCredit);
					tmpMap.put("grade", tmpStringGrade);
					tmpMap.put("time", tmpStringTime);
					data.add(tmpMap);
				}
			}
			
			// ����������������
	    	float total_cr1 = 0; 	//���޿���ѧ��
	    	float total_gr1 = 0;	//���޿��ܷ�
	    	
	    	float total_cr2 = 0; 	//���п�Ŀ��ѧ��
	    	float total_gr2 = 0; 	//���п�Ŀ��ѧ�ּ���
	    	
	    	float tmp_cr = 0;		//ѧ����ʱ����
	    	float tmp_gr = 0;		//������ʱ����
	    	
	    	for (Map<String, String> i : data) {
	    		
	    		tmp_cr = Float.parseFloat(i.get("credit"));
	    		tmp_gr = Float.parseFloat(i.get("grade"));
	    		
	    		// �γ�Ϊѡ��
	    		if (i.get("attr").equals("1")) {
	    			if (tmp_gr >= 60) {
	    				total_cr2 += tmp_cr;
	    				total_gr2 += Utility.credit(tmp_gr)*tmp_cr;
					}
				}
	    		// �γ�Ϊ����
	    		else {
	    			total_cr1 += tmp_cr;
	    			total_gr1 += tmp_gr*tmp_cr;
					total_cr2 += tmp_cr;
					total_gr2 += Utility.credit(tmp_gr)*tmp_cr;
				}
			}
	    	
	    	// ���޿�ƽ����
	    	float avarage = total_gr1/total_cr1;
	    	
	    	// ���п�Ŀƽ������
	    	float GPA = total_gr2/total_cr2;
	    	
	    	// ��ѧ�ѹ�ȥ����
	    	Date now = new Date();
	    	int days = (int)((now.getTime() - info.inSchool.getTime()) / (86400000));
	    
	    	// ������ɰٷֱ�
	    	float percent = info.creditGot/info.creditTotal * 100;
	    	
	    	pInfo.setUid(uData.getUid());
	    	// TODO ѧԺID
	    	pInfo.setSid(0);
	    	pInfo.setName(info.name);
	    	pInfo.setDays(days);
	    	pInfo.setPercent((percent));
	    	pInfo.setAvarage(avarage);
	    	pInfo.setGpa(GPA);
			return pInfo;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public ArrayList<Map<String, String>> getRoll(UserData uData) {
		try {
			String session = getSession(uData, false);
			if (session.length() <= 3) {
				session = getSession(uData, true);
			}
			
			ArrayList<Map<String, String>> rollList = new ArrayList<Map<String, String>>();
			
			Document doc = Jsoup.connect("http://202.115.47.141/xjInfoAction.do?oper=xjxx")
					.cookie("JSESSIONID", session)
					.timeout(10000)
					.get();
			Element tblView = doc.getElementById("tblView");
			Elements tblView_tr = tblView.select("tr");
			
			String tmpStringKey = new String();
			String tmpStringValue = new String();
			Map<String, String> tmpMap = new HashMap<String, String>();
		
			for (Element tblView_line : tblView_tr)
			{
				tmpStringKey = tblView_line.select("td").eq(0).text().trim();
				tmpStringValue = tblView_line.select("td").eq(1).text().trim();
				if (tmpStringKey.length() != 0) {
					tmpMap = new HashMap<String, String>();
					tmpMap.put("key", tmpStringKey);
					tmpMap.put("value", tmpStringValue);
					rollList.add(tmpMap);
				}
				tmpStringKey = tblView_line.select("td").eq(2).text().trim();
				tmpStringValue = tblView_line.select("td").eq(3).text().trim();
				if (tmpStringKey.length() != 0) {
					tmpMap = new HashMap<String, String>();
					tmpMap.put("key", tmpStringKey);
					tmpMap.put("value", tmpStringValue);
					rollList.add(tmpMap);
				}
			}
			
			return rollList;
		} catch (Exception e) {
			return null;
		}
	}
	
	private CourseInfo processCourse(CourseProperty cp) {
		
		if (cp.tmpStringWeeks.equals("") || cp.tmpStringWeek.equals("") || cp.tmpStringLesson.equals("")) {
			return null;
		}
		
		try {
			CourseInfo cInfo = new CourseInfo();
			
			cInfo.setCourseid(cp.tmpStringID);
			cInfo.setName(cp.tmpStringName);
			cInfo.setNum(cp.tmpStringNum);
			cInfo.setCredit(cp.tmpStringCredit);
			cInfo.setAttr(cp.tmpStringAttr);
			cInfo.setExam(cp.tmpStringExam);
			cInfo.setTeacher(cp.tmpStringTeacher);
			cInfo.setCampus(cp.tmpStringCamp);
			cInfo.setBld(cp.tmpStringBld);
			cInfo.setPlace(cp.tmpStringPlace);
			
			int lessonStart = 0;
			int lessonEnd = 0;
			if (cp.tmpStringLesson.contains("~")) {
				String lessonArray[] = cp.tmpStringLesson.split("~");
				lessonStart = Integer.parseInt(lessonArray[0]);
				lessonEnd = Integer.parseInt(lessonArray[1]);
			}
			else {
				lessonStart = lessonEnd = Integer.parseInt(cp.tmpStringLesson);
			}
			cInfo.setLessonfrom(lessonStart);
			cInfo.setLessonto(lessonEnd);
			cInfo.setDay(Integer.parseInt(cp.tmpStringWeek));
			
			if (cp.tmpStringWeeks.equals("1-17��")) {
				cInfo.setWeekfrom(1);
				cInfo.setWeekto(17);
				cInfo.setWeektype(1);
			}
			else if (cp.tmpStringWeeks.equals("˫��")) {
				cInfo.setWeekfrom(1);
				cInfo.setWeekto(17);
				cInfo.setWeektype(3);
			}
			else if (cp.tmpStringWeeks.equals("�����Ͽ�")) {
				cInfo.setWeekfrom(1);
				cInfo.setWeekto(17);
				cInfo.setWeektype(2);
			}
			// ?-?����
			else if (cp.tmpStringWeeks.contains("-")) {
				Pattern pattern = Pattern.compile("(\\d+)-(\\d+)\\w*");
				Matcher matcher = pattern.matcher(cp.tmpStringWeeks);
				matcher.find();
				int weekStart = Integer.parseInt(matcher.group(1));
				int weekEnd = Integer.parseInt(matcher.group(2));
				cInfo.setWeekfrom(weekStart);
				cInfo.setWeekto(weekEnd);
				cInfo.setWeektype(1);
			}
			// ?����
			else {
				Pattern pattern = Pattern.compile("(\\d+)\\w*");
				Matcher matcher = pattern.matcher(cp.tmpStringWeeks);
				matcher.find();
				int week = Integer.parseInt(matcher.group(1));
				cInfo.setWeekfrom(week);
				cInfo.setWeekto(week);
				cInfo.setWeektype(1);
			}
			return cInfo;
		} catch (Exception e) {
			return null;
		}
	}
	
	public LinkedList<CourseInfo> getCourse(UserData uData) {
		try {
			String session = getSession(uData, false);
			if (session.length() <= 3) {
				session = getSession(uData, true);
			}
			
			Document doc = Jsoup.connect("http://202.115.47.141/xkAction.do?actionType=6")
					.cookie("JSESSIONID", session)
					.timeout(10000)
					.get();
		
			Element table = doc.select(".displayTag").get(1);
			Elements tableTr = table.select("tbody tr");

			LinkedList<CourseInfo> cList = new LinkedList<CourseInfo>();
			
			for (int i = 0; i < tableTr.size();) {
				Elements tableTds = tableTr.get(i).select("td");
				
				// ĳһ�е�<td>������6����һ���µĿγ�
				if ( tableTds.size() > 6 ) {
					
					String rowspan = tableTds.get(0).attr("rowspan");
					int rowpanInt = 0;
					if (rowspan.equals("")) {
						rowpanInt = 1;
					}
					else {
						rowpanInt = Integer.parseInt(rowspan);
					}
					
					CourseProperty courseP = new CourseProperty();
					courseP.tmpStringID = tableTds.get(1).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringName = tableTds.get(2).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringNum = tableTds.get(3).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringCredit = tableTds.get(4).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringAttr = tableTds.get(5).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringExam = tableTds.get(6).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringTeacher = tableTds.get(7).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringWeeks = tableTds.get(11).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringWeek = tableTds.get(12).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringLesson = tableTds.get(13).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringCamp = tableTds.get(14).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringBld = tableTds.get(15).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					courseP.tmpStringPlace = tableTds.get(16).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
					
					CourseInfo cInfo = processCourse(courseP);
					if (cInfo != null) {
						cList.add(cInfo);
					}
					
					for (int j = 1; j < rowpanInt; j++) {
						tableTds = tableTr.get(i+j).select("td");
						
						courseP.tmpStringWeeks = tableTds.get(0).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
						courseP.tmpStringWeek = tableTds.get(1).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
						courseP.tmpStringLesson = tableTds.get(2).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
						courseP.tmpStringCamp = tableTds.get(3).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
						courseP.tmpStringBld = tableTds.get(4).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
						courseP.tmpStringPlace = tableTds.get(5).text().trim().replace(" &nbsp;", "").replaceAll("\\s*","");
						
						CourseInfo cInfoNew = processCourse(courseP);
						if (cInfoNew != null) {
							cList.add(cInfoNew);
						}
					}
					i += rowpanInt;
				}
			}
			return cList;
		} catch (Exception e) {
			return null;
		}
	}
}
