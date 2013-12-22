package com.nsmss.scu.exam;


import java.util.List;
import java.util.Map;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.bean.UserData;
import com.nsmss.scu.common.NetHelper;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.dao.UserDataDao;
import com.nsmss.scu.main.MainActivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ExamActivity extends Activity
{
	/**
	 * ��̬��Ա����
	 */
	private static Context context;

	/**
	 * UI��س�Ա����
	 */
	ProgressDialog progressDialog;

	
	/**
	 * View��س�Ա����
	 */
	View backView;
	View refreshView;
	private ListView listView;
	private SimpleAdapter simpleAdapter;

	
	/**
	 * Dao��Ա����
	 */
	private GlobalInfoDao gDao;
	private UserDataDao uDao;

	
	/**
	 * ����ģ�ͱ���
	 */
	GlobalInfo gInfo;
	private UserData uData;

	
	/**
	 * ����ģ�Ͷ���
	 */
	NetHelper netHelper;

	
	/**
	 * ���ݴ洢����
	 */
	List<Map<String, String>> data;

	/**
	 * ״̬����
	 */

	/**
	 * ��ʱ����
	 */
	int uid;
	private String errCodeStr;

	/**
	 * Activity�ص�����
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// �̳и��෽������View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exam);

		// ��ʼ��context
		context = getApplicationContext();
		
		//��ʼ��UI��س�Ա����
		
		// ��ʼ������ģ�Ͷ���
		netHelper = new NetHelper();

		// ��ʼ��Dao��Ա����
		gDao = new GlobalInfoDao(context);
		uDao = new UserDataDao(context);

		// ��ʼ������ģ�ͱ���
		gInfo = gDao.query();
		uid = gInfo.getActiveUserUid();
		uData = uDao.query(uid);

		// ��ʼ��View��Ա����
		initView();
		initListener();
		// ��ʼ��״̬����

		// ��ʼ����ʱ����

		// �Զ��庯��
		getExamInfo();
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
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			jumpToMain();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exam, menu);
		return true;
	}

	/**
	 * �̶߳���
	 */
	private Runnable connection = new Runnable()
	{
		@Override
		public void run()
		{
			data = netHelper.examInfo(uData);

			if (data != null && !data.isEmpty())
			{ // ���ˢ�³ɹ��������б�
				progressDialog.dismiss();
				runOnUiThread(succRunnable);
			} 
			else if (data != null && data.isEmpty()) {
				progressDialog.dismiss();
				runOnUiThread(retRunnable);
			}
			else
			{ // ˢ��ʧ�ܣ���ʾ��Ϣ
				progressDialog.dismiss();
				errCodeStr = "104";
				runOnUiThread(errRunnable);
			}
		}
	};

	/**
	 * �Զ����Ա����
	 */
	private Runnable succRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			updateToListview();
		}
	};
	
	private Runnable retRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			Toast.makeText(ExamActivity.this, "��ʱû�п�����Ϣ��", Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable errRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			Toast.makeText(ExamActivity.this, "���Ӵ��󣡴�����룺"+errCodeStr, Toast.LENGTH_SHORT).show();
		}
	};

	/**
	 * �Զ��巽��
	 */
	private void getExamInfo()
	{
		// TODO Auto-generated method stub
		progressDialog = ProgressDialog.show(ExamActivity.this, null, getResources().getString(R.string.loading_tip));// ��ʾProgressBar
		new Thread(connection).start();	// ���������߳�
	}

	private void updateToListview()
	{
		// TODO Auto-generated method stub
		simpleAdapter = new SimpleAdapter(context, data, R.layout.list_exam, new String[]
		{ "subject", "time", "place" }, new int[]
		{ R.id.list_subject, R.id.list_time, R.id.list_place });

		listView.setAdapter(simpleAdapter);
		listView.setDivider(null);
	}

	private void initListener()
	{
		// TODO Auto-generated method stub
		backView.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				jumpToMain();
			}

		});
		refreshView.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				refreshList();
			}

		});
	}

	private void initView()
	{
		// TODO Auto-generated method stub
		backView = findViewById(R.id.Btn_Change_Password_Back);
		refreshView = findViewById(R.id.Btn_Change_Password_Refresh);
		listView = (ListView) findViewById(R.id.listView_Exam);
	}

	private void jumpToMain()
	{
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(ExamActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		finish();
	}

	private void refreshList()
	{
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getResources().getString(R.string.loading_tip));
		progressDialog.setCancelable(true);
		progressDialog.show();
		new Thread(connection).start(); // ���������߳�
	}

}
