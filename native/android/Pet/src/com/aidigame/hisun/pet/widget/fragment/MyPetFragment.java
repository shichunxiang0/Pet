package com.aidigame.hisun.pet.widget.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aidigame.hisun.pet.FirstPageActivity;
import com.aidigame.hisun.pet.R;
import com.aidigame.hisun.pet.adapter.HomePetPictureAdapter;
import com.aidigame.hisun.pet.adapter.HomeSearchListAdapter;
import com.aidigame.hisun.pet.adapter.HomeViewPagerAdapter;
import com.aidigame.hisun.pet.bean.Animal;
import com.aidigame.hisun.pet.bean.Gift;
import com.aidigame.hisun.pet.bean.PetPicture;
import com.aidigame.hisun.pet.bean.MyUser;
import com.aidigame.hisun.pet.blur.TopCenterImageView;
import com.aidigame.hisun.pet.constant.Constants;
import com.aidigame.hisun.pet.http.HttpUtil;
import com.aidigame.hisun.pet.http.json.LoginJson;
import com.aidigame.hisun.pet.http.json.UserImagesJson;
import com.aidigame.hisun.pet.service.BlurImageBroadcastReceiver;
import com.aidigame.hisun.pet.ui.AlbumPictureBackground;
import com.aidigame.hisun.pet.ui.ChoseAcountTypeActivity;
import com.aidigame.hisun.pet.ui.DialogGiveSbGiftActivity1;
import com.aidigame.hisun.pet.ui.HomeActivity;
import com.aidigame.hisun.pet.ui.PlayGameActivity;
import com.aidigame.hisun.pet.ui.ShakeActivity;
import com.aidigame.hisun.pet.ui.TakePictureBackground;
import com.aidigame.hisun.pet.ui.TouchActivity;
import com.aidigame.hisun.pet.ui.UsersListActivity;
import com.aidigame.hisun.pet.util.HandleHttpConnectionException;
import com.aidigame.hisun.pet.util.ImageUtil;
import com.aidigame.hisun.pet.util.LogUtil;
import com.aidigame.hisun.pet.util.StringUtil;
import com.aidigame.hisun.pet.util.UserStatusUtil;
import com.aidigame.hisun.pet.view.PullToRefreshAndMoreView;
import com.aidigame.hisun.pet.view.PullToRefreshAndMoreView.PullToRefreshAndMoreListener;
import com.aidigame.hisun.pet.widget.HomeMyPet;
import com.aidigame.hisun.pet.widget.HomePetPictures;
import com.aidigame.hisun.pet.widget.PLAWaterfull;
import com.aidigame.hisun.pet.widget.ShakeSensor;
import com.aidigame.hisun.pet.widget.ShakeSensor.OnShakeLisener;
import com.aidigame.hisun.pet.widget.ShowProgress;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.umeng.analytics.MobclickAgent;

public class MyPetFragment extends Fragment implements OnClickListener{
	public static MyPetFragment myPetFragment;
	public View menuView,popupParent;//主界面,弹出框所在位置相关父控件
    HomeActivity homeActivity;
    public static MyPetFragment homeFragment;
    public ImageView cameraBt;
    TextView titleTv;
    HandleHttpConnectionException handleHttpConnectionException;
	public ViewPager viewPager;//列表界面，三个图片列表
	public ArrayList<View> viewList;//viewPager中加载的所有view
	HomeViewPagerAdapter homeViewPagerAdapter;//viewPager的适配器
	Button hostBt;//打开左侧抽屉按钮
	LinearLayout camera_album;//显示获取照片界面
	public LinearLayout progressLayout;//显示加载进度条界面
	public ShowProgress showProgress;
    public RelativeLayout black_layout;
    public HomeMyPet homeMyPet;
	public static int COMPLETE=0;
	

	public ArrayList<PetPicture> petPictures;
	public ArrayList<Animal> animals=new ArrayList<Animal>();
	//下载完一张图片
	public  static final int MESSAGE_DOWNLOAD_IMAGE=2;
	//更新瀑布流
	public  static final int UPDATE_WATERFULL=201;
	public static final int DISMISS_PROGRESS=202;
	public static final int SHOW_PROGRESS=203;
	public static final int SHOW_HEADER=204;
	public static final int SHOW_FOOTER=205;
	public static final int HIDE_HEADER=206;
	public static final int HIDE_FOOTER=207;
	public static final int SHOW_BACKGROUND_CONTROL=208;
	public static final int HIDE_BACKGROUND_CONTROL=209;
	
	View view1;
	 HomePetPictures homePetPictures;
	public ListView listView;
	public int last_id=-1;
	public Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==COMPLETE){
				homeActivity.finish();
			}
			switch (msg.what) {
			case Constants.MESSAGE_DOWNLOAD_IMAGES_LIST:
				break;
			case MESSAGE_DOWNLOAD_IMAGE:
				break;
			case UPDATE_WATERFULL:
				break;
			case DISMISS_PROGRESS:
				if(showProgress!=null)
				showProgress.progressCancel();
				break;
			case SHOW_PROGRESS:
				if(showProgress==null){
					showProgress=new ShowProgress(homeActivity,  progressLayout);
				}else{
					showProgress.showProgress();
				}
				break;
			case SHOW_HEADER:
				handler.sendEmptyMessage(SHOW_BACKGROUND_CONTROL);
				break;
			case SHOW_FOOTER:
				break;
			case HIDE_HEADER:
				
				 
			        
				break;
			case HIDE_FOOTER:
				break;
			case SHOW_BACKGROUND_CONTROL:
				break;
            case HIDE_BACKGROUND_CONTROL:
				break;
				
			}
		};
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		menuView=inflater.inflate(R.layout.fragment_my_pet, null);
		homeFragment=this;
		homeActivity=HomeActivity.homeActivity;
		myPetFragment=this;
		LogUtil.i("mi", "homefragment====="+"onCreateView()");
		return menuView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		//登陆成功
		LogUtil.i("mi", "homefragment====="+"onViewCreated()");
		//TODO
//		UserStatusUtil.downLoadUserInfo(homeActivity);
		homeActivity=HomeActivity.homeActivity;
		handleHttpConnectionException=HandleHttpConnectionException.getInstance();
		LogUtil.i("exception", "创建HomeActivity");
		long time=System.currentTimeMillis();
		LogUtil.i("exception", "创建HomeActivity");
		petPictures=new ArrayList<PetPicture>();
		
		initView();
		LogUtil.i("me", "Constants.isSuccess="+Constants.isSuccess);
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	private void initView() {
		LogUtil.i("mi", "homefragment====="+"initView()");
		// TODO Auto-generated method stub
        
        popupParent=menuView.findViewById(R.id.popup_parent);
		hostBt=(Button)menuView.findViewById(R.id.imageView1);
		black_layout=(RelativeLayout)menuView.findViewById(R.id.black_layout);
		titleTv=(TextView)menuView.findViewById(R.id.title_tv);
		cameraBt=(ImageView)menuView.findViewById(R.id.imageView2);
	
		
		
		
		
		if(Constants.user!=null&&Constants.user.aniList!=null){
			
			  ArrayList<Animal> animals=new ArrayList<Animal>();
		  		for(int i=0;i<Constants.user.aniList.size();i++){
		  			if(Constants.user.userId==Constants.user.aniList.get(i).master_id){
		  				animals.add(Constants.user.aniList.get(i));
		  			}
		  		}
		  		if(animals.size()>0){
		  		//使用系统相机
				cameraBt.setVisibility(View.VISIBLE);
		  		}else{
		  			cameraBt.setVisibility(View.GONE);
		  		}
		}
		   
		   
		   
		   
		   
		   
		camera_album=(LinearLayout)menuView.findViewById(R.id.camera_album);
		progressLayout=(LinearLayout)menuView.findViewById(R.id.progress_parent);
		showProgress=new ShowProgress(homeActivity, progressLayout);
		showProgress.progressCancel();
        viewPager=(ViewPager)menuView.findViewById(R.id.viewpager);
       
       homeMyPet=new HomeMyPet(homeActivity);
       view1=homeMyPet.getView();
       
      
        viewList=new ArrayList<View>();
        viewList.add(view1);
        homeViewPagerAdapter=new HomeViewPagerAdapter(viewList);
        viewPager.setAdapter(homeViewPagerAdapter);
		//标题
		hostBt.setOnClickListener(this);
		cameraBt.setOnClickListener(this);
		titleTv.setOnClickListener(this);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				LogUtil.i("scroll", "viewpager=====onPageSelected");
				switch (arg0) {
				case 0:
					if(!UserStatusUtil.isLoginSuccess(homeActivity,popupParent,black_layout)){
						
					}
					break;
				case 1:
					break;
				case 2:
					
					
					break;
				}
				switch (arg0) {
				case 0:
					
					

				default:
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				LogUtil.i("scroll", "viewpager=====onPageScrolled");
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				LogUtil.i("scroll", "viewpager=====onPageScrollStateChanged");
				
			}
		});
	}
	/**
	 * 底部四个功能按钮
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button3:
//			changeColors(R.id.button3);
			break;
		case R.id.imageView1:
           joinKingdom();
			break;
		case R.id.imageView2:
          if(!UserStatusUtil.isLoginSuccess(homeActivity,popupParent,black_layout)){
				
				return;
			}
       
			if(Constants.user!=null&&Constants.user.aniList!=null){
				/*if(Constants.user.userId==Constants.user.currentAnimal.master_id){
					cameraBt.setVisibility(View.VISIBLE);
				}else{
					Toast.makeText(homeActivity, "只有宠物主人才可以上传照片", Toast.LENGTH_LONG).show();
					return;
				}*/
				  ArrayList<Animal> animals=new ArrayList<Animal>();
			  		for(int i=0;i<Constants.user.aniList.size();i++){
			  			if(Constants.user.userId==Constants.user.aniList.get(i).master_id){
			  				animals.add(Constants.user.aniList.get(i));
			  			}
			  		}
			  		if(animals.size()>0){
			  		//使用系统相机
					showCameraAlbum(animals.get(0),false);
			  		}
			}else{
				Toast.makeText(homeActivity, "只有宠物主人才可以上传照片,目前您还没有创建的萌星", Toast.LENGTH_LONG).show();
			}
			
			
			break;
		case R.id.waterfall_parent:
			LogUtil.i("me","home_right_layout点击事件");
			break;
		}
	}
	public void joinKingdom(){
		 if(!UserStatusUtil.isLoginSuccess(homeActivity,popupParent,black_layout)){
				return;
		}
		if(Constants.user!=null&&Constants.user.aniList!=null){
			int num=0;
			if(Constants.user.aniList.size()>=10&&Constants.user.aniList.size()<=20){
				num=(Constants.user.aniList.size()+1)*5;
			}else  if(Constants.user.aniList.size()>20){
				num=100;
			}
			
			if(/*Constants.user.coinCount>=num*/true){
				Intent intent=new Intent(homeActivity,ChoseAcountTypeActivity.class);
				intent.putExtra("from", 1);
				homeActivity.startActivity(intent);
//				DialogGoRegister dialog=new DialogGoRegister(popup_parent, homeActivity, black_layout, 4);
			}else{
				
				DialogNote dialog=new DialogNote(popupParent, homeActivity, black_layout, 1);
			}
			
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.i("mi", "homefragment====="+"onStart");
	}
	boolean isFirst=true;
	
	boolean isShowingCameraAlbum=false;
	public void showCameraAlbum(final Animal animal,final boolean isBeg) {
		// TODO Auto-generated method stub
		isShowingCameraAlbum=true;
		long l1=System.currentTimeMillis();
		LogUtil.i("scroll",""+( System.currentTimeMillis()-l1));
		final View view=LayoutInflater.from(homeActivity).inflate(R.layout.popup_user_homepage, null);
		Animation animation=AnimationUtils.loadAnimation(homeActivity, R.anim.anim_translate_showtopic_addcommentlayout_in);
		view.clearAnimation();
		view.setAnimation(animation);
		animation.start();
		
		camera_album.removeAllViews();
		camera_album.addView(view);
		camera_album.setVisibility(View.VISIBLE);
		camera_album.setClickable(true);
		TextView camera=(TextView)view.findViewById(R.id.textView1);
		TextView album=(TextView)view.findViewById(R.id.textView2);
		TextView cancel=(TextView)view.findViewById(R.id.textView3);
		camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent2=new Intent(homeActivity,TakePictureBackground.class);
				intent2.putExtra("mode", TakePictureBackground.MODE_TOPIC);
				if(animal!=null){
					intent2.putExtra("animal", animal);
					intent2.putExtra("isBeg", isBeg);
				}else{
					intent2.putExtra("animal", Constants.user.currentAnimal);
					intent2.putExtra("isBeg", isBeg);
				}
				
				homeActivity.startActivity(intent2);
						camera_album.setVisibility(View.INVISIBLE);
						camera_album.setClickable(false);
						isShowingCameraAlbum=false;
				
			}
		});
		album.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent2=new Intent(homeActivity,AlbumPictureBackground.class);
				intent2.putExtra("mode", TakePictureBackground.MODE_TOPIC);
				
				if(animal!=null){
					intent2.putExtra("animal", animal);
					intent2.putExtra("isBeg", isBeg);
				}else{
					intent2.putExtra("animal", Constants.user.currentAnimal);
					intent2.putExtra("isBeg", isBeg);
				}
				homeActivity.startActivity(intent2);
				
						camera_album.setVisibility(View.INVISIBLE);
						camera_album.setClickable(false);
						isShowingCameraAlbum=false;
				
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Animation animation=AnimationUtils.loadAnimation(homeActivity, R.anim.anim_translate_showtopic_addcommentlayout_out);
				view.clearAnimation();
				view.setAnimation(animation);
				animation.start();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						camera_album.setVisibility(View.INVISIBLE);
						camera_album.setClickable(false);
						isShowingCameraAlbum=false;
					}
				}, 300);
				
			}
		});
	}

   



	
	/**
	 * 判断用户是创建王国还是认养宠物
	 */
	public void judgeAcountType(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Constants.isSuccess){
					while(Constants.user==null||Constants.user.currentAnimal==null){
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					homeActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(Constants.user.userId==Constants.user.currentAnimal.master_id){
								cameraBt.setVisibility(View.VISIBLE);
							}else{
								cameraBt.setVisibility(View.INVISIBLE);
							}
						}
					});
					
				}
			}
		}).start();
	}
	
	

	

    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	

}
