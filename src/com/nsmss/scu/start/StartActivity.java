package com.nsmss.scu.start;

import java.util.Calendar;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.login.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;

public class StartActivity extends Activity {
	
	/**
	 *  ��̬��Ա����
	 */
	private static Context context;
	private final int SPLASH_DISPLAY_LENGHT = 0; // TODO ��Ϊ�ӳٵĺ�����2000
	
	/**
	 * UI��س�Ա����
	 */
	
	
	/**
	 * View��س�Ա����
	 */	
	
	
	/**
	 * Dao��Ա����
	 */
	private GlobalInfoDao gInfoDao;
	
	
	/**
	 * ����ģ�ͱ���
	 */
	private GlobalInfo gInfo;
	
	
	/**
	 * ���ݴ洢����
	 */
	

	/**
	 * ״̬����
	 */

	/**
	 * ��ʱ����
	 */
	private long timeStart;
	
	/**
	 * Activity�ص�����
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		timeStart = System.currentTimeMillis();
		
		// �̳и��෽������View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		// ��ʼ��context
		context = getApplicationContext(); 
		
		// ��ʼ��View��Ա����
		
		// ��ʼ��Dao��Ա����
		gInfoDao = new GlobalInfoDao(context);
		
		// ��ʼ������ģ�ͱ���
		gInfo = gInfoDao.query();
		
		// �Զ��庯��
		initGInfo(context);
		
		// �����ʼ�����ĵ�ʱ��С��Ԥ��ʱ��
		long timeInit = System.currentTimeMillis()-timeStart;
		if (timeInit < SPLASH_DISPLAY_LENGHT) {
			new Handler().postDelayed(new Runnable(){ 
		         @Override 
		         public void run() {
		        	 jumpToLogin();
		         }
		    }, SPLASH_DISPLAY_LENGHT-timeInit);
		}
		else {
			jumpToLogin();
		}
	}

    @Override
    protected void onPause() {
       super.onPause(); 
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    @Override
    protected void onResume() {
        super.onResume(); 
    }
    
	/**
	 * �̶߳���
	 */
	
	
	/**
	 * �Զ����Ա����
	 */
	
	
	/**
	 * �Զ��巽��
	 */

	private void initGInfo(Context context) {
    	if (gInfo == null) {
	    	int version = 0;
	    	String vsersionStr = "";
			try {
				PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				version = pi.versionCode;
				vsersionStr = pi.versionName;
			} catch (Exception e) {
				version = 1;
				vsersionStr = "1.0";
			}
			
			Calendar calendar = Calendar.getInstance();
			int month = calendar.get(Calendar.MONTH)+1;
			int year = calendar.get(Calendar.YEAR);

			gInfo = new GlobalInfo();
			gInfo.setVersion(version);
			gInfo.setVersionStr(vsersionStr);
			
			// TODO ��Ϊ��Ӧѧ�ڿ�ѧʱ��
			gInfo.setTermBegin("2013-09-08");

			// �°�ѧ��
			if (month < 8) {
				gInfo.setYearFrom(year-1);
				gInfo.setYearTo(year);
				gInfo.setTerm(2);
			}
			// �ϰ�ѧ��
			else {
				gInfo.setYearFrom(year);
				gInfo.setYearTo(year+1);
				gInfo.setTerm(1);
			}
			
			gInfo.setFirstUse(1);
			gInfo.setActiveUserUid(0);
			
			gInfoDao.insert(gInfo);
		}
    }
	
	private void jumpToLogin() {
   	 	Intent intent = new Intent(StartActivity.this, LoginActivity.class); 
        startActivity(intent); 
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        finish(); 
	}
}
