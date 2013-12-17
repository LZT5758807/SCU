package com.nsmss.scu.dao;

import com.nsmss.scu.bean.CourseInfo;
import com.nsmss.scu.common.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CourseInfoDao {
	
	private DBHelper globalDBHelper;
	private SQLiteDatabase db;
	
	public CourseInfoDao(Context context) {
		globalDBHelper = new DBHelper(context);
		db = globalDBHelper.getWritableDatabase();
	}
	
	public int insert(CourseInfo cInfo) {
		try {
			// ����ҵ���ͬ��ʱ��ص���ͬ���γ̣�������Ϣ
			String sql1 = "SELECT cid FROM course_info WHERE courseid=? AND num=? AND weekfrom=? AND weekto=? AND weektype=? " +
					"AND day=? AND lesson_from=? AND lesson_to=? AND campus=? AND bld=? AND place=?";
			Cursor c = db.rawQuery(sql1, new String[]{
					cInfo.getCourseid(), 
					cInfo.getNum(),
					cInfo.getWeekfrom()+"",
					cInfo.getWeekto()+"",
					cInfo.getWeektype()+"",
					cInfo.getDay()+"",
					cInfo.getLessonfrom()+"",
					cInfo.getLessonto()+"",
					cInfo.getCampus(),
					cInfo.getBld(),
					cInfo.getPlace()
					});
			if (c.getCount() != 0) {
				c.moveToFirst();
				int cid = c.getInt(c.getColumnIndex("cid"));
				String sql2 = "UPDATE course_info SET name=?,credit=?,attr=?,exam=?,teacher=? WHERE cid=?";
				db.execSQL(sql2, new Object[] {
						cInfo.getName(),
						cInfo.getCredit(),
						cInfo.getAttr(),
						cInfo.getExam(),
						cInfo.getTeacher(),
						cid
				});
				return cid;
			}
			// �������γ�
			String sql = "INSERT INTO course_info (cid,weekfrom,weekto,weektype,day,lesson_from,lesson_to,courseid,num,name,credit,attr,exam,teacher,campus,bld,place) " +
					"VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			db.execSQL(sql, new Object[] {
					cInfo.getWeekfrom(),
					cInfo.getWeekto(),
					cInfo.getWeektype(),
					cInfo.getDay(),
					cInfo.getLessonfrom(),
					cInfo.getLessonto(),
					cInfo.getCourseid(),
					cInfo.getNum(),
					cInfo.getName(),
					cInfo.getCredit(),
					cInfo.getAttr(),
					cInfo.getExam(),
					cInfo.getTeacher(),
					cInfo.getCampus(),
					cInfo.getBld(),
					cInfo.getPlace()
			});
			Cursor c1 = db.rawQuery("SELECT last_insert_rowid()", null);
			c1.moveToFirst();
			return c1.getInt(c1.getColumnIndex("last_insert_rowid()"));
		} catch (Exception e) {
			return 0;
		}

	}
	
	public boolean delete(int cid) {
		try {
			String sql = "DELETE FROM course_info WHERE cid=?";
			db.execSQL(sql, new Object[] {cid});
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
