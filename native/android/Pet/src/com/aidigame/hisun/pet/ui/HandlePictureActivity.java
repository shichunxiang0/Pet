package com.aidigame.hisun.pet.ui;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aidigame.hisun.pet.R;
import com.aidigame.hisun.pet.util.ImageUtil;
import com.aidigame.hisun.pet.util.UiUtil;
import com.aidigame.hisun.pet.widget.CreateTitle;
import com.aidigame.hisun.pet.widget.fragment.Function4Fragment;
import com.aidigame.hisun.pet.widget.fragment.HorizontalListViewFragment;

public class HandlePictureActivity extends Activity implements OnClickListener{
	LinearLayout fragmentView,titleLayout;
	RelativeLayout relativeLayout;
	Button bt1,bt2,bt3,bt4;
	ImageView imageView;
	TextView reCameraTv,cancelTv,reDoTv,nextTv;
	Bitmap bmp;
	CreateTitle createTitle;
	public int actionCount=0;
	public String actionPicturePath=null;
	public String currentChartletPath=null;
	public static Bitmap handlingBmp;
	public static String originPicturePath=null;
	public boolean isMovingChartlet=false;
	public static final int SET_IMAGE_VIEW=0;//布局文件加载完毕，可以获得imageView的宽高
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SET_IMAGE_VIEW:
				//注意，折二返回的是新建额Bitmap对象
				Bitmap bmp=ImageUtil.scaleImageByView(HandlePictureActivity.handlingBmp, imageView);
				HandlePictureActivity.handlingBmp.recycle();
				HandlePictureActivity.handlingBmp=bmp;
				imageView.setImageBitmap(bmp);
				ImageUtil.compressImage(HandlePictureActivity.handlingBmp, 100);
				//初始化显示function1
				function1();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		UiUtil.setScreenInfo(this);
		setContentView(R.layout.activity_handle_picture);
		initView();
		initListener();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(5);
					handler.sendEmptyMessage(SET_IMAGE_VIEW);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();;
	}

	private void initView() {
		// TODO Auto-generated method stub
		fragmentView=(LinearLayout)findViewById(R.id.fragment_view);
		titleLayout=(LinearLayout)findViewById(R.id.linearLayout1);
		bt1=(Button)findViewById(R.id.button1);
		bt2=(Button)findViewById(R.id.button2);
		bt3=(Button)findViewById(R.id.button3);
		bt4=(Button)findViewById(R.id.button4);
		imageView=(ImageView)findViewById(R.id.imageView1);
		reCameraTv=(TextView)findViewById(R.id.textView1);
		cancelTv=(TextView)findViewById(R.id.textView2);
		reDoTv=(TextView)findViewById(R.id.textView3);
		nextTv=(TextView)findViewById(R.id.textView4);
		createTitle=new CreateTitle(this, titleLayout);
		relativeLayout=(RelativeLayout)findViewById(R.id.relativelayout);
	}
	private void initListener() {
		// TODO Auto-generated method stub
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
		bt4.setOnClickListener(this);
		reCameraTv.setOnClickListener(this);
		cancelTv.setOnClickListener(this);
		reDoTv.setOnClickListener(this);
		nextTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		isMovingChartlet=false;
		switch (v.getId()) {
		case R.id.button1:
			function1();
			
			break;
		case R.id.button2:
			function2();
			break;
		case R.id.button3:
			function3();
			break;
		case R.id.button4:
			function4();
			break;
		case R.id.textView1:
			Intent intent=new Intent(this,TakePictureActivity.class);
			this.startActivity(intent);
			this.finish();
			break;
		case R.id.textView2:
			actionBack();
			break;
		case R.id.textView3:
			actionReDo();
			break;
		case R.id.textView4:
			Intent intent4=new Intent(this,SubmitPictureActivity.class);
			HandlePictureActivity.handlingBmp=ImageUtil.getBitmapFromImageView(imageView);
			this.startActivity(intent4);
			break;
		}
		
	}




	private void function1(){
		isMovingChartlet=false;
		handlingBmp=ImageUtil.getBitmapFromImageView(imageView);
		HorizontalListViewFragment horizontalListViewFragment=new HorizontalListViewFragment(this,fragmentView,1 ,imageView);
        
	}
	private void function2(){
		isMovingChartlet=true;
		handlingBmp=ImageUtil.getBitmapFromImageView(imageView);
		HorizontalListViewFragment horizontalListViewFragment=new HorizontalListViewFragment(this,fragmentView,2 ,imageView);
		
	}
	private void function3(){
		isMovingChartlet=false;
		HorizontalListViewFragment horizontalListViewFragment=new HorizontalListViewFragment(this,fragmentView,3 ,imageView);

	}
	private void function4(){
		isMovingChartlet=false;
		Function4Fragment f4f=new Function4Fragment(this,fragmentView);
	}
	/**
	 * 撤销之前的操作
	 */
	private void actionBack() {
		// TODO Auto-generated method stub
		if(actionPicturePath==null)return;
		File file=new File(actionPicturePath);
		File[] files=file.listFiles();
		if(files==null)return;
		if(files.length==0)return;
		if(files.length==1){
			HandlePictureActivity.handlingBmp=BitmapFactory.decodeFile(originPicturePath);
			imageView.setImageBitmap(HandlePictureActivity.handlingBmp);
			new File(actionPicturePath+File.separator+(0)+".jpg").delete();
			actionCount=0;
		}else{
			Bitmap bmp=BitmapFactory.decodeFile(actionPicturePath+File.separator+(actionCount-1)+".jpg");
			new File(actionPicturePath+File.separator+(actionCount)+".jpg").delete();
			imageView.setImageBitmap(bmp);
			actionCount--;
		}
	}
	private void actionReDo() {
		// TODO Auto-generated method stub
		ImageUtil.deleteFile(actionPicturePath);
		actionPicturePath=null;
		if(originPicturePath==null)return;
		handlingBmp=BitmapFactory.decodeFile(originPicturePath);
		imageView.setImageBitmap(handlingBmp);
	}
}
