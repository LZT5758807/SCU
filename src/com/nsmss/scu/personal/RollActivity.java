package com.nsmss.scu.personal;

import java.util.ArrayList;
import java.util.Map;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.bean.UserData;
import com.nsmss.scu.common.NetHelper;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.dao.RollInfoDao;
import com.nsmss.scu.dao.UserDataDao;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RollActivity extends Activity {
	
	/**
	 *  ��̬��Ա����
	 */
	private static Context context;
	
	/**
	 * UI��س�Ա����
	 */
	private ProgressDialog progressDialog;
	private SimpleAdapter sAdapter;
	
	/**
	 * View��س�Ա����
	 */
	View backView;
	View refreshView;
	ListView rollListView;
	
	/**
	 * Dao��Ա����
	 */
	GlobalInfoDao gDao;
	private UserDataDao uDao;
	RollInfoDao prDao;
	
	/**
	 * ����ģ�ͱ���
	 */
	GlobalInfo gInfo;
	private UserData uData;

	/**
	 * ���ݴ洢����
	 */
	ArrayList <Map<String, String>> rollList;

	/**
	 * ״̬����
	 */


	/**
	 * ��ʱ����
	 */
	int uid;
	
	/**
	 * Activity�ص�����
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// �̳и��෽������View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roll);
		
		// ��ʼ��context
		context = getApplicationContext(); 
		
		// ��ʼ��View��Ա����
		
		// ��ʼ��Dao��Ա����
		gDao = new GlobalInfoDao(context);
		uDao = new UserDataDao(context);
		prDao = new RollInfoDao(context);
		
		// ��ʼ������ģ�ͱ���
		gInfo = gDao.query();
		uid = gInfo.getActiveUserUid();
		uData = uDao.query(uid);
		rollList = prDao.query(uid);
		
		// ��ʼ��״̬����
		
		// ��ʼ����ʱ����
		
		// �Զ��庯��
		initView();
		initListener();
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
			jumpToPersonal();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	/**
	 * �̶߳���
	 */
    // �����߳�
    private Runnable connRunnable = new Runnable() {
    	@Override  
		public void run() {
			NetHelper nHelper = new NetHelper();
			rollList = nHelper.getRoll(uData);
    		// ������ӳɹ��������˸�������
    		if (rollList != null) {
    			// �ж�״̬�Ի����Ƿ���ʾ
				if (progressDialog.isShowing()) {
					if (prDao.update(rollList, uid)) {
						progressDialog.dismiss();
						runOnUiThread(succRunnable);
					}
					else {
						progressDialog.dismiss();
						runOnUiThread(errURunnable);
					}
				}
			}
    		// ���Ӵ���
    		else {
    			// �ж�״̬�Ի����Ƿ���ʾ
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
					runOnUiThread(errRunnable);
				}
			}
    	}
    };
    // ���ӳɹ��߳�
	private Runnable succRunnable = new Runnable() {
		@Override  
	    public void run() {
			updateRoll();
	    }
	};
	
	// ���Ӵ����߳�
	private Runnable errRunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(RollActivity.this, "���Ӵ��������������ӣ�", Toast.LENGTH_SHORT).show();
	    }
	};
	// ���´����߳�
	private Runnable errURunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(RollActivity.this, "���´���", Toast.LENGTH_SHORT).show();
	    }
	};
	
	/**
	 * �Զ����Ա����
	 */
	
	
	/**
	 * �Զ��巽��
	 */
	private void initView() {
		backView = findViewById(R.id.Btn_Roll_Back);
		refreshView = findViewById(R.id.Btn_Roll_Refresh);
		rollListView = (ListView) findViewById(R.id.List_Roll_Main);
		if (rollList == null) {
			rollList = new ArrayList<Map<String, String>>();
		}
		updateRoll();
	}
	
	private void initListener() {
		backView.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToPersonal();
			}
		});
		refreshView.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshRoll();
			}
		});
		
	}
	
	private void jumpToPersonal() {
		Intent intent=new Intent();
		intent.setClass(RollActivity.this, PersonalActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		finish();
	}
	
	private void refreshRoll() {
    	// ��ʾ״̬�Ի���
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getResources().getString(R.string.loading_tip));
		progressDialog.setCancelable(true);
		progressDialog.show();
		
		// ���������߳�
		new Thread(connRunnable).start();
	}
	
	private void updateRoll() {
		if (rollList == null) {
			return;
		}
		sAdapter = new SimpleAdapter(this, rollList, R.layout.list_item_personal_roll, 
				new String[] { "key", "value" }, 
				new int[] { R.id.Text_Roll_List_Key, R.id.Text_Roll_List_Value});
		
		rollListView.setAdapter(sAdapter);
		rollListView.setDivider(null);
	}
    
}
