package com.nsmss.scu.personal;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.bean.UserData;
import com.nsmss.scu.common.NetHelper;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.dao.UserDataDao;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity
{
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
	View backView;
	View refreshView;
	private EditText oldPasswdEditT;
	private EditText newPasswdEditT1;
	private EditText newPasswdEditT2;
	private Button submitButton;
	
	/**
	 * Dao��Ա����
	 */
	private GlobalInfoDao gInfoDao;
	GlobalInfoDao gDao;
	private UserDataDao uDao;
	
	/**
	 * ����ģ�ͱ���
	 */
	GlobalInfo gInfo;
	private UserData uData;

	/**
	 * ���ݴ洢����
	 */
	private String oldPasswdStr;
	private String newPasswdStr1;
	private String newPasswdStr2;

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
		setContentView(R.layout.activity_change_password);
		
		// ��ʼ��context
				context = getApplicationContext(); 
		
		// ��ʼ��View��Ա����
		
				// ��ʼ��Dao��Ա����
				
				gDao = new GlobalInfoDao(context);
				uDao = new UserDataDao(context);
				// ��ʼ������ģ�ͱ���
				gInfo = gDao.query();
				uid = gInfo.getActiveUserUid();
				uData = uDao.query(uid);
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

	private void initView()
	{
		backView = findViewById(R.id.Btn_Change_Password_Back);
		refreshView = findViewById(R.id.Btn_Change_Password_Refresh);
		
		oldPasswdEditT = (EditText) findViewById(R.id.edittext_change_password_oldpasswd);
		oldPasswdEditT.addTextChangedListener(watcher);
		newPasswdEditT1 = (EditText) findViewById(R.id.edittext_change_password_newpasswd1);
		newPasswdEditT1.addTextChangedListener(watcher);
		newPasswdEditT2 = (EditText)findViewById(R.id.edittext_change_password_newpasswd2);
		newPasswdEditT2.addTextChangedListener(watcher);
		submitButton = (Button) findViewById(R.id.button_change_password_submit);
	}
	private void initListener()
	{
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
	    submitButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				oldPasswdStr = oldPasswdEditT.getText().toString();
				newPasswdStr1 = newPasswdEditT1.getText().toString();
				newPasswdStr2 = newPasswdEditT2.getText().toString();
				if (oldPasswdStr.length()<1) {
					Toast.makeText(getApplicationContext(), "������ԭʼ���룡", Toast.LENGTH_SHORT).show();
					submitButton.setEnabled(false);  //ʹ��¼��ť������
					newPasswdEditT1.clearFocus();
					newPasswdEditT2.clearFocus();
					oldPasswdEditT.requestFocus();
				}
				else if (newPasswdStr1.length()<1||newPasswdStr2.length()<1) {
					Toast.makeText(getApplicationContext(), "�������������룡", Toast.LENGTH_SHORT).show();
					submitButton.setEnabled(false);  //ʹ��¼��ť������
					if (newPasswdStr1.length()<1)
					{
						oldPasswdEditT.clearFocus();
						newPasswdEditT2.clearFocus();
						newPasswdEditT1.requestFocus();
					}
					else {
						oldPasswdEditT.clearFocus();
						newPasswdEditT1.clearFocus();
						newPasswdEditT2.requestFocus();
					}
				}
				else if (!newPasswdStr1.equals(newPasswdStr2)) {
					Toast.makeText(getApplicationContext(), "�����������벻һ�£����������룡", Toast.LENGTH_SHORT).show();
					submitButton.setEnabled(false);  //ʹ��¼��ť������
					newPasswdEditT1.setText("");
					newPasswdEditT2.setText("");
					
					oldPasswdEditT.clearFocus();
					newPasswdEditT2.clearFocus();
					newPasswdEditT1.requestFocus();
				}
				else {
					submitConn();
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
			submitButton.setEnabled(true); //�༭���¼��ť����
		}  
  };  
  
	private void jumpToPersonal() {
		Intent intent=new Intent();
		intent.setClass(ChangePasswordActivity.this, PersonalActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		finish();
	}
	
	private void submitConn()
	{
		loadDialog = new ProgressDialog(this);
    	loadDialog.setIndeterminate(true);
    	loadDialog.setMessage("�ύ��...");
    	loadDialog.setCancelable(true);
    	loadDialog.show();
    	new Thread(submitRunnable).start();
	}
	
	private void refreshRoll() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_password, menu);
		return true;
	}
	
	/**
	 * �̶߳���
	 */
	
	
	private Runnable submitRunnable = new Runnable() {
		@Override
		public void run() {
			NetHelper netHelper = new NetHelper();
			errCodeStr = netHelper.changePassword(uData,oldPasswdStr, newPasswdStr1,newPasswdStr2);
			if (loadDialog.isShowing()) {
				loadDialog.dismiss();
				if (errCodeStr == null || errCodeStr.equals("100") || 
						errCodeStr.equals("103") || errCodeStr.equals("104")) {
					runOnUiThread(connErrRunnable);
				}
				else if (errCodeStr.equals("101")) {
					//runOnUiThread(numErrRunnable);
				}
				else if (errCodeStr.equals("102")) {
					//runOnUiThread(passwdErrRunnable);
				}
				else {
					int returnCode = correctUser(errCodeStr);
					if (returnCode == 0) {
						runOnUiThread(successRunnable);
						jumpToPersonal();
					}
					else {
						errCodeStr = returnCode+"";
						runOnUiThread(dbErrRunnable);
					}
				}
			}
		}
	};
	
	/**
	 * ������ص���ʾ��Ϣ
	 */
	private Runnable successRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(ChangePasswordActivity.this, "�����޸ĳɹ���", Toast.LENGTH_SHORT).show();
		}
	};
	private Runnable inputErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(ChangePasswordActivity.this, "�����������Ϣ��", Toast.LENGTH_SHORT).show();
		}
	};
	private Runnable oldpasswdErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(ChangePasswordActivity.this, "�������ԭʼ�����������������", Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable passwdErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(ChangePasswordActivity.this, "����������벻һ�£�����������", Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable connErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(ChangePasswordActivity.this, "�������Ӵ��󣬴������"+errCodeStr, Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable dbErrRunnable = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(ChangePasswordActivity.this, "��Ϣ�洢���󣬴������"+errCodeStr, Toast.LENGTH_SHORT).show();
		}
	};
	
    /**
     * ����UserData���󣬴������ݿ⣬������global_info
     * @param session ��NetHelper���ص�JSESSIONID��ֵ
     * @return �ɹ�����0�����򷵻ش������
     */
	private int correctUser(String session)
	{
    	//uData.setPasswd(newPasswdStr1);
    	uData.setSession(session);
    	
    	if (uDao.update(uData)) {
    		return 0;
    		//gInfo.setActiveUserUid(uid);
        	//if(gInfoDao.insert(gInfo)) {
        	//return 0;
        	//}
        	//else {
				//return 202;
			//}
		}
    	else {
    		return 202;
    	}
	}
	
	/**
	 * �Զ����Ա����
	 */
	
	
	/**
	 * �Զ��巽��
	 */

}
