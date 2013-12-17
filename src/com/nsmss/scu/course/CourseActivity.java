package com.nsmss.scu.course;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.nsmss.scu.R;
import com.nsmss.scu.bean.CourseData;
import com.nsmss.scu.bean.CourseInfo;
import com.nsmss.scu.bean.GlobalInfo;
import com.nsmss.scu.bean.UserData;
import com.nsmss.scu.common.MyFragmentPagerAdapter;
import com.nsmss.scu.common.NetHelper;
import com.nsmss.scu.common.Utility;
import com.nsmss.scu.dao.CourseDataDao;
import com.nsmss.scu.dao.CourseInfoDao;
import com.nsmss.scu.dao.GlobalInfoDao;
import com.nsmss.scu.dao.UserDataDao;
import com.nsmss.scu.main.MainActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CourseActivity extends FragmentActivity {
	/**
	 *  ��̬��Ա����
	 */
	private static Context context;
	private Resources resources;
	
	/**
	 * UI��س�Ա����
	 */
	private ProgressDialog progressDialog;
	private MyFragmentPagerAdapter myAdapter;
	private ViewPager mPager;
	
	/**
	 * View��س�Ա����
	 */
	private View backView;
	private View refreshView;
	private ImageView ivBottomLine;
	private TextView weekDaysTextView[];
	private View positionView1;
	
	/**
	 * Dao��Ա����
	 */
	GlobalInfoDao gDao;
	UserDataDao uDao;
	CourseInfoDao cInfoDao;
	CourseDataDao cDataDao;
	
	/**
	 * ����ģ�ͱ���
	 */
	GlobalInfo gInfo;
	UserData uData;

	/**
	 * ���ݴ洢����
	 */
	private ArrayList<Fragment> fragmentsList;
	private LinkedList<CourseInfo> cList;
	ArrayList<ArrayList<LinkedList<Map<String, Object>>>> weekCourse;

	/**
	 * ״̬����
	 */
	private boolean is_first = true;

	/**
	 * ��ʱ����
	 */
	private NetHelper nHelper;
	private int uid;
	private int currIndex;
	private int currentWeek;
	private int position[];
	private int offset;
	
	/**
	 * Activity�ص�����
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// �̳и��෽������View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course);
		
		// ��ʼ��context
		context = getApplicationContext();
		resources = getResources();
		
		// ��ʼ��View��Ա����
		
		// ��ʼ��Dao��Ա����
		gDao = new GlobalInfoDao(context);
		uDao = new UserDataDao(context);
		cInfoDao = new CourseInfoDao(context);
		cDataDao = new CourseDataDao(context);
		
		// ��ʼ������ģ�ͱ���
		gInfo = gDao.query();
		uid = gInfo.getActiveUserUid();
		uData = uDao.query(uid);
		
		// ��ʼ��״̬����
		
		// ��ʼ����ʱ����
		nHelper = new NetHelper();
		currentWeek = Utility.getWeeks(gInfo.getTermBegin());
		position = new int[7];
		offset = 0;
		
		// �Զ��庯��
		 initDay();
		 initView();
		 initListener();
	}
	
    @Override  
    public void onWindowFocusChanged(boolean hasFocus)  
    {  
        if (hasFocus && is_first)
        {  
        	initWidth();
        	is_first = false;
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
	private Runnable connRunnable = new Runnable() {
    	@Override  
		public void run() {
    		cList = nHelper.getCourse(uData);
    		// ������ӳɹ��������˸�������
    		if (cList != null) {
    			// �ж�״̬�Ի����Ƿ���ʾ
				if (progressDialog.isShowing()) {
					if (saveCourse()) {
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
			// TODO ��������
			Toast.makeText(CourseActivity.this, "�ɹ�", Toast.LENGTH_SHORT).show();
	    }
	};
	
	// ���Ӵ����߳�
	private Runnable errRunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(CourseActivity.this, "���Ӵ��������������ӣ�", Toast.LENGTH_SHORT).show();
	    }
	};
	// ���´����߳�
	private Runnable errURunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(CourseActivity.this, "���´���", Toast.LENGTH_SHORT).show();
	    }
	};
	
	/**
	 * �Զ����Ա����
	 */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = currIndex;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };
    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            
            // ��֮ǰ�ĸ�����ɫ�ָ�
			animation = new TranslateAnimation(position[currIndex], position[arg0], 0, 0);
			weekDaysTextView[currIndex].setTextColor(resources.getColor(R.color.scu__defaultSubHeadText));
     
            // ����ѡ�е�����
            weekDaysTextView[arg0].setTextColor(resources.getColor(R.color.scu__defaultMianTitle));

            // ���鶯���ƶ�
            animation.setFillAfter(true);
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
            
            // ����Current
            currIndex = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
	
	/**
	 * �Զ��巽��
	 */
	
	private void initDay() {
		Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        int dayOfWeek =cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
        	currIndex = 6;
		}
        else {
        	currIndex = dayOfWeek-2;
		}
	}
	
	private void initView() {
		backView = findViewById(R.id.Btn_Course_Back);
		refreshView = findViewById(R.id.Btn_Course_Refresh);
		ivBottomLine = (ImageView) findViewById(R.id.Img_Course_Subhead_Line);
		
    	weekDaysTextView = new TextView[7];
    	weekDaysTextView[0] = (TextView) findViewById(R.id.Text_Course_Subhead_Mon);
    	weekDaysTextView[1] = (TextView) findViewById(R.id.Text_Course_Subhead_Tue);
    	weekDaysTextView[2] = (TextView) findViewById(R.id.Text_Course_Subhead_Wed);
    	weekDaysTextView[3] = (TextView) findViewById(R.id.Text_Course_Subhead_Thu);
    	weekDaysTextView[4] = (TextView) findViewById(R.id.Text_Course_Subhead_Fri);
    	weekDaysTextView[5] = (TextView) findViewById(R.id.Text_Course_Subhead_Sat);
    	weekDaysTextView[6] = (TextView) findViewById(R.id.Text_Course_Subhead_Sun);
    	weekDaysTextView[currIndex].setTextColor(resources.getColor(R.color.scu__defaultMianTitle));
    	
    	positionView1 = findViewById(R.id.View_Course_Position_1);
    	
        mPager = (ViewPager) findViewById(R.id.Pager_Course_Content);
        
        fragmentsList = new ArrayList<Fragment>();
        
        for (int i = 0; i < 7; i++) {
        	ArrayList<Map<String, String>> dayData = new ArrayList<Map<String, String>>();
        	for (int j = 0; j < 12; j++) {
        		Map<String, String> tmpMap = new HashMap<String, String>();
        		tmpMap.put("subject", "����");
        		tmpMap.put("lesson", "����");
        		dayData.add(tmpMap);
			}
        	Fragment courseFragment = CourseFragment.newInstance(dayData);
       	 	fragmentsList.add(courseFragment);
		}
       
        myAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList);
        mPager.setAdapter(myAdapter);
        mPager.setCurrentItem(currIndex);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
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
				refresh();
			}
		});
		for (int i = 0; i < 7; i++) {
    		weekDaysTextView[i].setOnClickListener(new MyOnClickListener(i));
		}
	}
	
	private void initWidth() {
		
		View wraper1 = findViewById(R.id.View_Course_SubWrap_1);
		View wraper2 = findViewById(R.id.View_Course_SubWrap_2);

		int left = positionView1.getLeft();
		
		int pos1 = wraper1.getLeft();
		int pos2 = wraper2.getLeft();
		offset = pos2-pos1;
		
        LinearLayout.LayoutParams lineParams = (LinearLayout.LayoutParams) ivBottomLine.getLayoutParams(); 
        lineParams.width = positionView1.getRight()-left;
        ivBottomLine.setLayoutParams(lineParams);
        
        for (int i = 0; i < 7; i++) {
        	position[i] = left+offset*i;
		}
        
        // ���û����ʼλ��
        Animation animation = new TranslateAnimation(0, position[currIndex], 0, 0);            
        animation.setFillAfter(true);
        animation.setDuration(0);
        ivBottomLine.startAnimation(animation);
        
	}
	
	private void jumpToMain() {
		Intent intent=new Intent();
		intent.setClass(CourseActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		finish();
	}
	
	private void refresh() {
    	// ��ʾ״̬�Ի���
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getResources().getString(R.string.loading_tip));
		progressDialog.setCancelable(true);
		progressDialog.show();
		
		// ���������߳�
		new Thread(connRunnable).start();
	}
	
	/**
	 * ���γ��б�������ݿ�
	 * @param cList
	 * @return
	 */
	private boolean saveCourse() {
		if (cDataDao.clear(uid)) {
			for (CourseInfo cInfo : cList) {
				int cid = cInfoDao.insert(cInfo);
				if (cid == 0) {
					return false;
				}
				CourseData cData = new CourseData();
				cData.setUid(uid);
				cData.setCid(cid);
				if(!cDataDao.insert(cData)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * ����γ�
	 */
	private void processCourse() {
		// �ȳ�ʼ��һ��Map������Ķ�ά�����ʾһ��ÿ��ÿС�����ԵĿγ���Ϣ
		weekCourse = new ArrayList<ArrayList<LinkedList<Map<String, Object>>>>(7);
		
		for (int i = 0; i < 7; i++) {
			ArrayList<LinkedList<Map<String, Object>>> dayCourse = new ArrayList<LinkedList<Map<String, Object>>>(12);
			for (int j = 0; j < 12; j++) {
				LinkedList<Map<String, Object>> lessonCourse = new LinkedList<Map<String, Object>>();
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				/**
				 * ����һ
				 * ����ĵ�һ��Ԫ������ָʾ��ǰС��λ�õ�״̬
				 * typeΪ0����ʾ��ǰС��û�пγ̣�����ʾС�ںţ���û�пγ���Ϣ��
				 * typeΪ-1����ʾ��ǰС���Ǵ���ĳ����ڵķǿ�ʼС�ڣ���������ʾ��
				 * typeΪ1����ʾ��ǰС����ĳ��С�ڻ���ĳ����ڵĵ�һС�ڣ���ʾС�ںŻ��ߴ�ڵ���ʼ-��ֹС�ڣ��Լ���ɫ��ʾ�Ŀγ���Ϣ��
				 * typeΪ2����ʾ��ǰС���ڵ�ǰ��û�пγ̣������������пγ̣���ʾС�ںŻ��ߴ�ڵ���ʼ-��ֹС�ڣ��Լ���ɫ��ʾ�Ŀγ���Ϣ��
				 */
				
				/**
				 * ������
				 * 
				 * ͷ�ڵ�
				 * ��"num"ָʾ��ǰλ���м����γ̣�����ռλ�ģ���
				 * ��"active"��ʾ��ǰ������ǵڼ�������1��ʼ��
				 * 
				 * �м�ڵ�
				 * ��"dummy"(Ϊ1)��ʾ��ǰλ��������ռλ�ģ�ʵ�ʱ�С��û�пγ̵��ǵ�ǰ�������ĳ��С��������γ̣�
				 */
				
				/**
				 * ������
				 * 
				 */
				tmpMap.put("num", 0);
				tmpMap.put("active", 0);
				
				lessonCourse.add(tmpMap);
				dayCourse.add(lessonCourse);
			}
			weekCourse.add(dayCourse);
		}
		
		for (CourseInfo cInfoTmp : cList) {
			int lessonFrom = cInfoTmp.getLessonfrom();
			int lessonTo = cInfoTmp.getLessonto();
			int lessons = lessonTo-lessonFrom;
			int day = cInfoTmp.getDay();
			
			for (int i = lessonFrom; i <= lessonTo; ) {
				if (i == 1 || i == 3 || i == 5) {
					insertCourse(day, i, 0, cInfoTmp);
				}
				
			}
			
			if (lessonFrom == 1 || lessonFrom == 3 || lessonFrom == 5) {
				insertCourse(day, lessonFrom, 0, cInfoTmp);
				switch (lessons) {
				case 1:
					insertCourse(day, lessonFrom+1, 1, cInfoTmp);
					break;
				case 2:
					insertCourse(day, lessonFrom+1, 1, cInfoTmp);
					insertCourse(day, lessonFrom+1, 1, cInfoTmp);
					break;
				}
			}
		}
	}
	
	private void insertCourse(int day, int lesson, int dummy, CourseInfo info) {
		Map<String, Object> newMap = new HashMap<String, Object>();
		newMap.put("dummy", dummy);
		newMap.put("course", info);
		
		LinkedList<Map<String, Object>> tmpList = weekCourse.get(day-1).get(lesson-1);
		tmpList.add(newMap);
		
		Map<String, Object> tmpMap = tmpList.get(0);
		tmpMap.put("num", ((Integer)tmpMap.get("num"))+1);
		tmpMap.put("active", tmpList.size()-1);
	}
	
	private boolean isCurrWeek(CourseInfo cInfoTmp) {
		// ȫ��
		if (cInfoTmp.getWeektype() == 1) {
			if ((cInfoTmp.getWeekfrom() <= currentWeek) && (currentWeek <= cInfoTmp.getWeekto())) {
				return true;
			}
			else {
				return false;
			}
		}
		// ����
		else if (cInfoTmp.getWeektype() == 2) {
			if (currentWeek%2 == 1) {
				return true;
			}
			else {
				return false;
			}
		}
		// ˫��
		else {
			if (currentWeek%2 == 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	public static Context getContext() {
		return context;
	}
}
