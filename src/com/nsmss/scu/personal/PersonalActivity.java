package com.nsmss.scu.personal;

import java.text.DecimalFormat;
import java.util.Calendar;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.bean.PersonalInfo;
import com.nsmss.scu.bean.UserData;
import com.nsmss.scu.common.NetHelper;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.dao.PersonalInfoDao;
import com.nsmss.scu.dao.UserDataDao;
import com.nsmss.scu.main.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalActivity extends Activity {

	/**
	 *  ��̬��Ա����
	 */
	private static Context context;
	
	/**
	 * UI��س�Ա����
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * View��س�Ա����
	 */
	private View backView;
	private View refreshView;
	private View bannerView;
	private TextView nameTextView;
	private TextView numberTextView;
	private TextView daysTextView;
	private TextView percentTextView;
	private TextView avarageTextView;
	private TextView GPATextView;
	private View rollView;
	private View planView;
	private View passwdView;
	private View checkView;
	
	/**
	 * Dao��Ա����
	 */
	private GlobalInfoDao gDao;
	private UserDataDao uDao;
	private PersonalInfoDao pDao;
	
	/**
	 * ����ģ�ͱ���
	 */
	private GlobalInfo gInfo;
	private UserData uData;
	private PersonalInfo pInfo;

	/**
	 * ���ݴ洢����
	 */
	

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
		setContentView(R.layout.activity_personal);
		
		// ��ʼ��context
		context = getApplicationContext(); 
		
		// ��ʼ��View��Ա����
		
		// ��ʼ��Dao��Ա����
		gDao = new GlobalInfoDao(context);
		uDao = new UserDataDao(context);
		pDao = new PersonalInfoDao(context);
		
		// ��ʼ������ģ�ͱ���
		gInfo = gDao.query();
		uid = gInfo.getActiveUserUid();
		uData = uDao.query(uid);
		pInfo = pDao.query(uid);
		
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
			jumpToMain();
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
			pInfo = nHelper.getPersonalInfo(uData);
    		// ������ӳɹ��������˸�������
    		if (pInfo != null) {
    			// �ж�״̬�Ի����Ƿ���ʾ
				if (progressDialog.isShowing()) {
					if (pDao.update(pInfo)) {
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
			updatePInfo();
	    }
	};
	
	// ���Ӵ����߳�
	private Runnable errRunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(PersonalActivity.this, "���Ӵ��������������ӣ�", Toast.LENGTH_SHORT).show();
	    }
	};
	// ���´����߳�
	private Runnable errURunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(PersonalActivity.this, "���´���", Toast.LENGTH_SHORT).show();
	    }
	};
	
	/**
	 * �Զ����Ա����
	 */
	
	
	/**
	 * �Զ��巽��
	 */
	private void initView() {
		backView = findViewById(R.id.Btn_Personal_Back);
		refreshView = findViewById(R.id.Btn_Personal_Refresh);
		
		bannerView = findViewById(R.id.View_Personal_Banner);
    	Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		if (month==0||month==1||month==11) {
			bannerView.setBackgroundResource(R.drawable.bg_personal_banner4);
		}
		else if (month==2||month==3||month==4) {
			bannerView.setBackgroundResource(R.drawable.bg_personal_banner1);
		}
		else if (month==5||month==6||month==7) {
			bannerView.setBackgroundResource(R.drawable.bg_personal_banner2);
		}
		else {
			bannerView.setBackgroundResource(R.drawable.bg_personal_banner3);
		}
		
    	nameTextView = (TextView) this.findViewById(R.id.Text_Personal_Name);
    	numberTextView = (TextView) this.findViewById(R.id.Text_Personal_Number);
    	daysTextView = (TextView) this.findViewById(R.id.Text_Personal_Days);
    	percentTextView = (TextView) this.findViewById(R.id.Text_Personal_Percent);
    	avarageTextView = (TextView) this.findViewById(R.id.Text_Personal_Score);
    	GPATextView = (TextView) this.findViewById(R.id.Text_Personal_Credit);
    	
    	rollView = findViewById(R.id.Btn_Personal_Roll);
    	planView = findViewById(R.id.Btn_Personal_Plan);
    	passwdView = findViewById(R.id.Btn_Personal_Passwd);
    	checkView = findViewById(R.id.Btn_Personal_Check);
    	
    	if (uData != null) {
    		numberTextView.setText("ѧ�ţ�"+uData.getNum());
    		updatePInfo();
		}

	}
	private void initListener() {
		backView.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToMain();
			}
		});
		refreshView.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshPInfo();
			}
		});
		rollView.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToRoll();
			}
		});
		passwdView.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToPasswd();
			}
		});
	}
	
	private void jumpToMain() {
		Intent intent=new Intent();
		intent.setClass(PersonalActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		finish();
	}
	
	private void jumpToRoll() {
		Intent intent=new Intent();
		intent.setClass(PersonalActivity.this, RollActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
		finish();
	}
	
	private void jumpToPasswd() {
		Intent intent=new Intent();
		intent.setClass(PersonalActivity.this, ChangePasswordActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
		finish();
	}
	
	private void refreshPInfo() {
    	// ��ʾ״̬�Ի���
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getResources().getString(R.string.loading_tip));
		progressDialog.setCancelable(true);
		progressDialog.show();
		
		// ���������߳�
		new Thread(connRunnable).start();
	}
	
	private void updatePInfo() {
		if (pInfo == null) {
			return;
		}
		DecimalFormat dFormat = new DecimalFormat("##0.00"); 
    	nameTextView.setText(pInfo.getName()+"�����");
    	daysTextView.setText(pInfo.getDays()+"");
    	percentTextView.setText((int)(pInfo.getPercent())+"%");
    	avarageTextView.setText(dFormat.format(pInfo.getAvarage()));
    	GPATextView.setText(dFormat.format(pInfo.getGpa()));
	}
}
