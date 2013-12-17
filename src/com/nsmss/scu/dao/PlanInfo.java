package com.nsmss.scu.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nsmss.scu.common.DBHelper;

public class PlanInfo {
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	
	public PlanInfo(Context context) {
		dbHelper = new DBHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	

}
