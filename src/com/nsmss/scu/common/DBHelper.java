package com.nsmss.scu.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "scu_db.db";
	private static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		//CursorFactory����Ϊnull,ʹ��Ĭ��ֵ
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//���ݿ��һ�α�����ʱonCreate�ᱻ����
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE IF NOT EXISTS global_info" +		// ȫ����Ϣ��
				"(key TEXT PRIMARY KEY, " +
				"value TEXT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS global_option" +		// ȫ��ѡ���
				"(key TEXT PRIMARY KEY, " +
				"value TEXT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS global_url" +		// ȫ��URL��
				"(key TEXT PRIMARY KEY, " +
				"value TEXT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS user_data" +			// �û���Ϣ��
				"(uid INTEGER PRIMARY KEY AUTOINCREMENT, " +		// �û�ID
				"num TEXT, " +										// ѧ��
				"passwd TEXT, " +									// ����
				"session TEXT, " +									// SESSION��Ϣ
				"lastlogin INTEGER, " +								// �ϴε�¼ʱ��
				"lastlogout INTEGER, " +							// �ϴ�ע��ʱ��
				"savepasswd INTEGER, " +							// �Ƿ񱣴�����
				"autologin INTEGER, " +								// �Ƿ��Զ���¼
				"headshot TEXT)"									// ͷ��
				);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS personal_info" +		// ������Ϣ��
				"(uid INTEGER PRIMARY KEY, " +		// �û�ID
				"sid INTEGER, "+ 					// ѧԺID
				"name TEXT, " +						// ����
				"days INTEGER, " +					// ��ѧ��ȥ����
				"percent TEXT, " +					// ������ɶ�
				"avarage TEXT, " +					// ����ƽ����
				"gpa TEXT )"						// ƽ������
				);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS personal_roll" +		// ѧ����Ϣ��
				"(uid INTEGER , " +					// �û�ID
				"key TEXT , " +						// ��Ŀ
				"value TEXT)"						// ֵ
				);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS course_info" +		// �γ���Ϣ��
				"(cid INTEGER PRIMARY KEY AUTOINCREMENT, " +		// �γ�ID
				"weekfrom INTEGER, " +		// ��ʼ�ܴ�
				"weekto INTEGER, " +		// �����ܴ�
				"weektype INTEGER, " +		// �ܴ�����
				"day INTEGER, " +			// ���ڼ�
				"lesson_from INTEGER, " +	// �ڴο�ʼ
				"lesson_to INTEGER, " +		// �ڴν���
				"courseid TEXT, " +			// �γ̺�
				"num TEXT, " +				// �����
				"name TEXT, " +				// �γ���
				"credit TEXT ," +			// ѧ��
				"attr TEXT, " +				// �γ�����
				"exam TEXT, " +				// ��������
				"teacher TEXT, " +			// ��ʦ
				"campus TEXT, " +			// У��
				"bld TEXT, " +				// ��ѧ¥
				"place TEXT )"				// ����	
				);
				
		db.execSQL("CREATE TABLE IF NOT EXISTS course_data" +		// �γ̼�¼��
				"(uid INTEGER, " +			// �û�ID
				"cid INTEGER )"				// �γ�ID	
				);
	}

	//���DATABASE_VERSIONֵ����Ϊ2,ϵͳ�����������ݿ�汾��ͬ,�������onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}

