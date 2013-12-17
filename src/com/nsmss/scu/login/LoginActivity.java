package com.nsmss.scu.login;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.bean.UserData;
import com.nsmss.scu.common.NetHelper;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.dao.UserDataDao;
import com.nsmss.scu.main.MainActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @version 1.1
 * @author LMD
 * @modify LT
 * ���ڵ�¼
 */
public class LoginActivity extends Activity {
	
	/**
	 *  ��̬��Ա����
	 */
	private static Context context;
	
	/**
	 * UI��س�Ա����
	 */
	private ProgressDialog loadDialog;
	
	/**
	 * View��س�Ա����
	 */
	private EditText numText;
	private EditText passwdText;
	private Button loginButton;
	private CheckBox autoBox;
	private CheckBox passwdBox;
	
	/**
	 * Dao��Ա����
	 */
	private GlobalInfoDao gInfoDao;
	private UserDataDao uDataDao;
	
	/**
	 * ����ģ�ͱ���
	 */
	private GlobalInfo gInfo;
	private UserData uData;

	/**
	 * ���ݴ洢����
	 */
	private String numStr;
	private String passwdStr;

	/**
	 * ״̬����
	 */
	

	/**
	 * ��ʱ����
	 */
	private String errCodeStr;
	
	
	/**
	 * Activity�ص�����
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// �̳и��෽������View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// ��ʼ��context
		context = getApplicationContext(); 
		
		// ��ʼ��View��Ա����
		initView();
		initListener();
		
		// ��ʼ��Dao��Ա����
		gInfoDao = new GlobalInfoDao(context);
		uDataDao = new UserDataDao(context);
		
		// ��ʼ������ģ�ͱ���
		gInfo = gInfoDao.query();
		
		// ��ʼ���洢����
		numStr = new String();
		passwdStr = new String();
		
		// ��ʼ��״̬����
		
		// ��ʼ����ʱ����
		
		// �Զ��庯��
		initInput();
		
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
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * �̶߳���
	 */
	private Runnable loginRunnable = new Runnable() {
		@Override
		public void run() {
			NetHelper netHelper = new NetHelper();
			errCodeStr = netHelper.login(numStr, passwdStr);
			if (loadDialog.isShowing()) {
				loadDialog.dismiss();
				if (errCodeStr == null || errCodeStr.equals("100") || 
						errCodeStr.equals("103") || errCodeStr.equals("104")) {
					runOnUiThread(connErrRunnable);
				}
				else if (errCodeStr.equals("101")) {
					runOnUiThread(numErrRunnable);
				}
				else if (errCodeStr.equals("102")) {
					runOnUiThread(passwdErrRunnable);
				}
				else {
					int returnCode = initUser(errCodeStr);
					if (returnCode == 0) {
						jumpToMain();
					}
					else {
						errCodeStr = returnCode+"";
						runOnUiThread(dbErrRunnable);
					}
				}
			}
		}
	};
	
	private Runnable numErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(LoginActivity.this, "�������ѧ�Ų����ڣ�����������", Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable passwdErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(LoginActivity.this, "����������벻��ȷ������������", Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable connErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(LoginActivity.this, "�������Ӵ��󣬴������"+errCodeStr, Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable dbErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(LoginActivity.this, "��Ϣ�洢���󣬴������"+errCodeStr, Toast.LENGTH_SHORT).show();
		}
	};
	
	
	/**
	 * �Զ����Ա����
	 */
	
	
	/**
	 * �Զ��巽��
	 */
    protected void initView() {
    	numText = (EditText)findViewById(R.id.EditText_Login_Num);
    	numText.addTextChangedListener(watcher);
		passwdText = (EditText)findViewById(R.id.EditText_Login_Passwd);
		passwdText.addTextChangedListener(watcher);
		loginButton = (Button)findViewById(R.id.Button_Login_Login);
		autoBox = (CheckBox)findViewById(R.id.CheckBox_Login_Auto);
		passwdBox = (CheckBox)findViewById(R.id.CheckBox_Login_Passwd);
		numText.setFocusable(true);
		numText.setFocusableInTouchMode(true);
		passwdText.setFocusable(true);
		passwdText.setFocusableInTouchMode(true);
    }
    
    protected void initListener() {
    	autoBox.setOnClickListener(new OnClickListener() {
    		@Override
			public void onClick(View v) {
				if (autoBox.isChecked()) {
					passwdBox.setChecked(true);
				}
			}
		});
    	passwdBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!passwdBox.isChecked()) {
					autoBox.setChecked(false);
				}
			}
		});
    	loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numStr = numText.getText().toString();
				passwdStr = passwdText.getText().toString();
				if (numStr.length()<6 ) {
					Toast.makeText(getApplicationContext(), "��������ȷ��ѧ�ţ�", Toast.LENGTH_SHORT).show();
					loginButton.setEnabled(false);  //ʹ��¼��ť������
					passwdText.clearFocus();
					numText.requestFocus();
				}
				else if (passwdStr.length()<1) {
					Toast.makeText(getApplicationContext(), "���������룡", Toast.LENGTH_SHORT).show();
					loginButton.setEnabled(false);  //ʹ��¼��ť������
					numText.clearFocus();
					passwdText.requestFocus();
				}
				else {
					loginConn();
				}
			}
		});
    }
    
    /**
     * �����ı��༭���������watcher
     */
    private TextWatcher watcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void afterTextChanged(Editable arg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub
			loginButton.setEnabled(true); //�༭���¼��ť����
		}  
  };  
  
    /**
     * �ж��������¼���û�ѡ�����Զ���¼
     * ����ת��������
     */
    protected void initInput() {
		if (gInfo != null) {
			int uid = gInfo.getActiveUserUid();
			if (uid != 0) {
				uData = uDataDao.query(uid);
				if (uData != null) {
					if (uData.getAutologin() == 1) {
						// TODO ��ת��������
						jumpToMain();
					}
					if (uData.getSavepasswd() == 1) {
						numText.setText(uData.getNum());
					}
				}
			}
		}
	}
    
    /**
     * ��ʾ��¼״̬ͼ��
     * ���������߳�
     */
    protected void loginConn() {
    	loadDialog = new ProgressDialog(this);
    	loadDialog.setIndeterminate(true);
    	loadDialog.setMessage("��¼��...");
    	loadDialog.setCancelable(true);
    	loadDialog.show();
    	new Thread(loginRunnable).start();
	}
    
    /**
     * ��ʼ��UserData���󣬴������ݿ⣬������global_info
     * @param session ��NetHelper���ص�JSESSIONID��ֵ
     * @return �ɹ�����0�����򷵻ش������
     */
    protected int initUser(String session) {
    	uData = new UserData();
    	uData.setNum(numStr);
    	uData.setPasswd(passwdStr);
    	uData.setSession(session);
    	uData.setLastlogin( (int)((System.currentTimeMillis())/1000) );
    	uData.setLastlogout(0);
    	if (autoBox.isChecked()) {
			uData.setAutologin(1);
    	}
    	else {
			uData.setAutologin(0);
		}
    	if (passwdBox.isChecked()) {
			uData.setSavepasswd(1);
		}
    	else {
    		uData.setSavepasswd(0);
    	}
    	uData.setHeadshot("");
    	int uid = uDataDao.insert(uData);
    	if (uid == 0) {
			return 201;
		}
    	
    	gInfo.setActiveUserUid(uid);
    	if(gInfoDao.insert(gInfo)) {
    		return 0;
    	}
    	else {
    		return 202;
    	}
	}
    
    /**
     * ��ת��MainActivity
     */
    protected void jumpToMain() {
   	 	Intent intent = new Intent(LoginActivity.this, MainActivity.class); 
        startActivity(intent); 
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        finish(); 
	}
    
}
