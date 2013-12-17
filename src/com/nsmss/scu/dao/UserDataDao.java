package com.nsmss.scu.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nsmss.scu.bean.UserData;
import com.nsmss.scu.common.DBHelper;

public class UserDataDao {
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	
	public UserDataDao(Context context) {
		dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * ����һ���û���Ϣ
	 * ����û�ѧ���Ѵ��ڣ����������
	 * @param uData �û���Ϣ
	 * @return ���β�����û���UID
	 */
	public int insert(UserData uData) {
		try {
			UserData uDataNew = query(uData.getNum());
			if ( uDataNew != null) {
				uData.setUid(uDataNew.getUid());
				if(update(uData)) {
					return uData.getUid();
				}
				else {
					return 0;
				}
			}
			String sql = "INSERT INTO user_data (uid, num, passwd, session, lastlogin, lastlogout, savepasswd, autologin, headshot) " +
					"VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
			db.execSQL(sql, new Object[] {
				uData.getNum(),
				uData.getPasswd(),
				uData.getSession(), 
				uData.getLastlogin(),
				uData.getLastlogout(),
				uData.getSavepasswd(),
				uData.getAutologin(),
				uData.getHeadshot()
			});
			Cursor c = db.rawQuery("SELECT last_insert_rowid()", null);
			c.moveToFirst();
			return c.getInt(c.getColumnIndex("last_insert_rowid()"));
		} catch (Exception e) {
			return 0;
		}

	}
	
	/**
	 * ����UIDɾ���û���¼
	 * @param uid
	 * @return ɾ���ɹ�����true
	 */
	public boolean delete(int uid) {
		try {
			String sql = "DELETE FROM user_data WHERE uid = ?";
			db.execSQL(sql, new Object[] {uid});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * ����ѧ��ɾ���û���¼
	 * @param num
	 * @return ɾ���ɹ�����true
	 */
	public boolean delete(String num) {
		try {
			String sql = "DELETE FROM user_data WHERE num = ?";
			db.execSQL(sql, new Object[] {num});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * �޸��û���Ϣ
	 * @param uData
	 * @return �������Ϊ�ջ���uidΪ�ն�����false
	 */
	public boolean update(UserData uData) {
		if (uData == null) {
			return false;
		}
		if (uData.getUid() == 0) {
			return false;
		}
		try {
			String sql = "UPDATE user_data SET num=?, passwd=?, session=?, lastlogin=?, lastlogout=?, savepasswd=?, " +
					"autologin=?, headshot=? WHERE uid=?";
			db.execSQL(sql, new Object[] {
				uData.getNum(),
				uData.getPasswd(),
				uData.getSession(), 
				uData.getLastlogin(),
				uData.getLastlogout(),
				uData.getSavepasswd(),
				uData.getAutologin(),
				uData.getHeadshot(),
				uData.getUid()
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * ͨ��UID�����û���Ϣ
	 * @param uid
	 * @return �û���Ϣ
	 */
	public UserData query(int uid) {
		try {
			UserData uData = new UserData();
			String sql = "SELECT * FROM user_data WHERE uid="+uid;
			Cursor c = db.rawQuery(sql, null);
			if (c.getCount() == 0) {
				return null;
			}
			else {
				c.moveToFirst();
				uData.setUid(uid);
				uData.setNum(c.getString(c.getColumnIndex("num")));
				uData.setPasswd(c.getString(c.getColumnIndex("passwd")));
				uData.setSession(c.getString(c.getColumnIndex("session")));
				uData.setLastlogin(c.getInt(c.getColumnIndex("lastlogin")));
				uData.setLastlogout(c.getInt(c.getColumnIndex("lastlogout")));
				uData.setSavepasswd(c.getInt(c.getColumnIndex("savepasswd")));
				uData.setAutologin(c.getInt(c.getColumnIndex("autologin")));
				uData.setHeadshot(c.getString(c.getColumnIndex("headshot")));
				return uData;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * ͨ��ѧ�Ų����û���Ϣ
	 * @param num
	 * @return �û���Ϣ
	 */
	public UserData query(String num) {
		try {
			UserData uData = new UserData();
			String sql = "SELECT * FROM user_data WHERE num="+num;
			Cursor c = db.rawQuery(sql, null);
			if (c.getCount() == 0) {
				return null;
			}
			else {
				c.moveToFirst();
				uData.setUid(c.getInt(c.getColumnIndex("uid")));
				uData.setNum(c.getString(c.getColumnIndex("num")));
				uData.setPasswd(c.getString(c.getColumnIndex("passwd")));
				uData.setSession(c.getString(c.getColumnIndex("session")));
				uData.setLastlogin(c.getInt(c.getColumnIndex("lastlogin")));
				uData.setLastlogout(c.getInt(c.getColumnIndex("lastlogout")));
				uData.setSavepasswd(c.getInt(c.getColumnIndex("savepasswd")));
				uData.setAutologin(c.getInt(c.getColumnIndex("autologin")));
				uData.setHeadshot(c.getString(c.getColumnIndex("headshot")));
				return uData;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
}
