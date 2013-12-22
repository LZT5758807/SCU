package com.nsmss.scu.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.common.Utility;
import com.nsmss.scu.course.CourseActivity;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.dao.PersonalInfoDao;
import com.nsmss.scu.dao.RollInfoDao;
import com.nsmss.scu.dao.UserDataDao;
import com.nsmss.scu.exam.ExamActivity;
import com.nsmss.scu.login.LoginActivity;
import com.nsmss.scu.personal.PersonalActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
	
	/**
	 *  ��̬��Ա����
	 */
	private static Context context;
    private static final String STATE_MENUDRAWER = "net.simonvt.menudrawer.samples.WindowSample.menuDrawer";
    private static final String STATE_ACTIVE_VIEW_ID = "net.simonvt.menudrawer.samples.WindowSample.activeViewId";
	
	/**
	 * UI��س�Ա����
	 */
    private MenuDrawer mMenuDrawer;
    private int mActiveViewId;
	
	/**
	 * View��س�Ա����
	 */
	View menuView;
	View refreshView;
	View newsView;
	View courseView;
	View examView;
	TextView dateTextView;
	TextView weekTextView;
	
	/**
	 * Dao��Ա����
	 */
	private GlobalInfoDao gInfoDao;
    private UserDataDao uDao;
    private PersonalInfoDao pDao;
    private RollInfoDao prDao;
	
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
	
	/**
	 * Activity�ص�����
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// �̳и��෽������View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ��ʼ��context 
		context = getApplicationContext(); 
		
		// ��ʼ��View��Ա����
		
		// ��ʼ��Dao��Ա����
		gInfoDao = new GlobalInfoDao(context);
		uDao = new UserDataDao(context);
		pDao = new PersonalInfoDao(context);
		prDao = new RollInfoDao(context);
		
		// ��ʼ������ģ�ͱ���
		gInfo = gInfoDao.query();
		uDao.query(gInfo.getActiveUserUid());
		
		// ��ʼ��״̬����
		
		// ��ʼ����ʱ����
		
		// �Զ��庯��
		initMenu(savedInstanceState);
		initView();
		initListener();
		initDate();
		initWeek();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MENUDRAWER, mMenuDrawer.saveState());
        outState.putInt(STATE_ACTIVE_VIEW_ID, mActiveViewId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mMenuDrawer.toggleMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();
            return;
        }

        super.onBackPressed();
    }
    
    @Override
    public void onClick(View v) {
        mActiveViewId = v.getId();
        switch (mActiveViewId) {
        case R.id.Menu_main_personal:
        	jumpToPersonal();
			break;
		case R.id.Menu_main_logout:
			userLogout();
			break;
		case R.id.Menu_main_exit:
			finish();
			break;
		}
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mMenuDrawer.isMenuVisible() == false) {
				mMenuDrawer.openMenu();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_MENU) {
			mMenuDrawer.toggleMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
	private void initMenu(Bundle inState) {
    	
        if (inState != null) {
            mActiveViewId = inState.getInt(STATE_ACTIVE_VIEW_ID);
        }

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_main);
        mMenuDrawer.setMenuView(R.layout.menu_scrollview);

        findViewById(R.id.Menu_main_home).setOnClickListener(this);
        findViewById(R.id.Menu_main_personal).setOnClickListener(this);
        findViewById(R.id.Menu_main_message).setOnClickListener(this);
        findViewById(R.id.Menu_main_options).setOnClickListener(this);
        findViewById(R.id.Menu_main_feedback).setOnClickListener(this);
        findViewById(R.id.Menu_main_update).setOnClickListener(this);
        findViewById(R.id.Menu_main_about).setOnClickListener(this);
        findViewById(R.id.Menu_main_logout).setOnClickListener(this);
        findViewById(R.id.Menu_main_exit).setOnClickListener(this);

        TextView activeView = (TextView) findViewById(mActiveViewId);
        if (activeView != null) {
            mMenuDrawer.setActiveView(activeView);
        }
	}
	
	private void initView() {
		menuView = findViewById(R.id.Btn_Main_Menu);
		refreshView = findViewById(R.id.Btn_Main_Refresh);
		newsView = findViewById(R.id.Btn_Main_Func_1);
		courseView = findViewById(R.id.Btn_Main_Func_2);
		examView = findViewById(R.id.Btn_Main_Func_3);
		dateTextView = (TextView) findViewById(R.id.scu_main_TextDate);
		weekTextView = (TextView) findViewById(R.id.scu_main_TextWeeks);
	}
	
	private void initListener() {
		menuView.setOnClickListener(new Button.OnClickListener(){
    		public void onClick(View v) {
    			mMenuDrawer.toggleMenu();
            }    
    	});
		refreshView.setOnClickListener(new Button.OnClickListener(){
    		public void onClick(View v) {

            }    
    	});
		newsView.setOnClickListener(new Button.OnClickListener(){
    		public void onClick(View v) {
    			/*
    			Intent intent=new Intent();
    			intent.setClass(MainActivity.this, NewsActivity.class);
    			startActivity(intent);
    			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    			finish();
    			*/
            }    
    	});
		courseView.setOnClickListener(new Button.OnClickListener(){
    		public void onClick(View v) {
    			Intent intent=new Intent();
    			intent.setClass(MainActivity.this, CourseActivity.class);
    			startActivity(intent);
    			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    			finish();
            }    
    	});
		examView.setOnClickListener(new Button.OnClickListener(){
    		public void onClick(View v) {
    			Intent intent=new Intent();
    			intent.setClass(MainActivity.this, ExamActivity.class);
    			startActivity(intent);
    			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    			finish();
            }    
    	});
	}
	
	@SuppressLint("SimpleDateFormat")
	private void initDate() {
		Date currentTime = new Date();
		String[] weekDays = {"��", "һ", "��", "��", "��", "��", "��"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)  w = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd��  ");
		String dateString = formatter.format(currentTime);
        dateTextView.setText(dateString + "����" + weekDays[w]);
	}
	
	private void initWeek() {
		weekTextView.setText("��" + Utility.getWeeks(gInfo.getTermBegin()) + "��ѧ��");
	}
	
	private void userLogout() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ȷ��Ҫע����¼��");
		builder.setMessage("ע����¼������������и�����Ϣ");
		builder.setPositiveButton("ע��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				int uid = gInfo.getActiveUserUid();
				
				uDao.delete(uid);
				pDao.delete(uid);
				prDao.delete(uid);
				//TODO ɾ�������û������Ϣ
				
				gInfo.setActiveUserUid(0);
				gInfoDao.insert(gInfo);
				
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				finish();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.create().show();
	}
	
	private void jumpToPersonal() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, PersonalActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
		finish();
	}
}
