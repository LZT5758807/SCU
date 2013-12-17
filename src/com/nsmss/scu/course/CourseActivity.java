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
	 *  静态成员变量
	 */
	private static Context context;
	private Resources resources;
	
	/**
	 * UI相关成员变量
	 */
	private ProgressDialog progressDialog;
	private MyFragmentPagerAdapter myAdapter;
	private ViewPager mPager;
	
	/**
	 * View相关成员变量
	 */
	private View backView;
	private View refreshView;
	private ImageView ivBottomLine;
	private TextView weekDaysTextView[];
	private View positionView1;
	
	/**
	 * Dao成员变量
	 */
	GlobalInfoDao gDao;
	UserDataDao uDao;
	CourseInfoDao cInfoDao;
	CourseDataDao cDataDao;
	
	/**
	 * 数据模型变量
	 */
	GlobalInfo gInfo;
	UserData uData;

	/**
	 * 数据存储变量
	 */
	private ArrayList<Fragment> fragmentsList;
	private LinkedList<CourseInfo> cList;
	ArrayList<ArrayList<LinkedList<Map<String, Object>>>> weekCourse;

	/**
	 * 状态变量
	 */
	private boolean is_first = true;

	/**
	 * 临时变量
	 */
	private NetHelper nHelper;
	private int uid;
	private int currIndex;
	private int currentWeek;
	private int position[];
	private int offset;
	
	/**
	 * Activity回调函数
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// 继承父类方法，绑定View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course);
		
		// 初始化context
		context = getApplicationContext();
		resources = getResources();
		
		// 初始化View成员变量
		
		// 初始化Dao成员变量
		gDao = new GlobalInfoDao(context);
		uDao = new UserDataDao(context);
		cInfoDao = new CourseInfoDao(context);
		cDataDao = new CourseDataDao(context);
		
		// 初始化数据模型变量
		gInfo = gDao.query();
		uid = gInfo.getActiveUserUid();
		uData = uDao.query(uid);
		
		// 初始化状态变量
		
		// 初始化临时变量
		nHelper = new NetHelper();
		currentWeek = Utility.getWeeks(gInfo.getTermBegin());
		position = new int[7];
		offset = 0;
		
		// 自定义函数
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
	 * 线程对象
	 */
	private Runnable connRunnable = new Runnable() {
    	@Override  
		public void run() {
    		cList = nHelper.getCourse(uData);
    		// 如果连接成功，返回了更新数据
    		if (cList != null) {
    			// 判断状态对话框是否显示
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
    		// 连接错误
    		else {
    			// 判断状态对话框是否显示
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
					runOnUiThread(errRunnable);
				}
			}
    	}
    };
    // 连接成功线程
	private Runnable succRunnable = new Runnable() {
		@Override  
	    public void run() {
			// TODO 更新数据
			Toast.makeText(CourseActivity.this, "成功", Toast.LENGTH_SHORT).show();
	    }
	};
	
	// 连接错误线程
	private Runnable errRunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(CourseActivity.this, "连接错误！请检查网络连接！", Toast.LENGTH_SHORT).show();
	    }
	};
	// 更新错误线程
	private Runnable errURunnable = new Runnable() {
		@Override  
	    public void run() {
			Toast.makeText(CourseActivity.this, "更新错误！", Toast.LENGTH_SHORT).show();
	    }
	};
	
	/**
	 * 自定义成员对象
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
            
            // 将之前的高亮颜色恢复
			animation = new TranslateAnimation(position[currIndex], position[arg0], 0, 0);
			weekDaysTextView[currIndex].setTextColor(resources.getColor(R.color.scu__defaultSubHeadText));
     
            // 高亮选中的文字
            weekDaysTextView[arg0].setTextColor(resources.getColor(R.color.scu__defaultMianTitle));

            // 滑块动画移动
            animation.setFillAfter(true);
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
            
            // 设置Current
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
	 * 自定义方法
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
        		tmpMap.put("subject", "午休");
        		tmpMap.put("lesson", "午休");
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
        
        // 设置滑块初始位置
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
    	// 显示状态对话框
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getResources().getString(R.string.loading_tip));
		progressDialog.setCancelable(true);
		progressDialog.show();
		
		// 开启连接线程
		new Thread(connRunnable).start();
	}
	
	/**
	 * 将课程列表存入数据库
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
	 * 处理课程
	 */
	private void processCourse() {
		// 先初始化一个Map的链表的二维数组表示一周每天每小节所以的课程信息
		weekCourse = new ArrayList<ArrayList<LinkedList<Map<String, Object>>>>(7);
		
		for (int i = 0; i < 7; i++) {
			ArrayList<LinkedList<Map<String, Object>>> dayCourse = new ArrayList<LinkedList<Map<String, Object>>>(12);
			for (int j = 0; j < 12; j++) {
				LinkedList<Map<String, Object>> lessonCourse = new LinkedList<Map<String, Object>>();
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				/**
				 * 方案一
				 * 链表的第一个元素用来指示当前小节位置的状态
				 * type为0：表示当前小节没有课程（将显示小节号，但没有课程信息）
				 * type为-1：表示当前小节是处于某个大节的非开始小节（将不会显示）
				 * type为1：表示当前小节是某个小节或者某个大节的第一小节（显示小节号或者大节的起始-终止小节，以及彩色显示的课程信息）
				 * type为2：表示当前小节在当前周没有课程，但在其他周有课程（显示小节号或者大节的起始-终止小节，以及灰色显示的课程信息）
				 */
				
				/**
				 * 方案二
				 * 
				 * 头节点
				 * 用"num"指示当前位置有几个课程（包括占位的），
				 * 用"active"表示当前激活的是第几个（从1开始）
				 * 
				 * 中间节点
				 * 用"dummy"(为1)表示当前位置是用来占位的（实际本小节没有课程但是当前大节中有某个小节有这个课程）
				 */
				
				/**
				 * 方案三
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
		// 全周
		if (cInfoTmp.getWeektype() == 1) {
			if ((cInfoTmp.getWeekfrom() <= currentWeek) && (currentWeek <= cInfoTmp.getWeekto())) {
				return true;
			}
			else {
				return false;
			}
		}
		// 单周
		else if (cInfoTmp.getWeektype() == 2) {
			if (currentWeek%2 == 1) {
				return true;
			}
			else {
				return false;
			}
		}
		// 双周
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
