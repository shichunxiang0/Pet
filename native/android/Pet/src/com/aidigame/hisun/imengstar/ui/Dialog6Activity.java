package com.aidigame.hisun.imengstar.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.aidigame.hisun.imengstar.PetApplication;
import com.aidigame.hisun.imengstar.bean.Animal;
import com.aidigame.hisun.imengstar.bean.PetPicture;
import com.aidigame.hisun.imengstar.constant.Constants;
import com.aidigame.hisun.imengstar.http.HttpUtil;
import com.aidigame.hisun.imengstar.http.json.UserImagesJson;
import com.aidigame.hisun.imengstar.util.HandleHttpConnectionException;
import com.aidigame.hisun.imengstar.util.ImageUtil;
import com.aidigame.hisun.imengstar.util.LogUtil;
import com.aidigame.hisun.imengstar.util.StringUtil;
import com.aidigame.hisun.imengstar.util.UiUtil;
import com.aidigame.hisun.imengstar.util.UserStatusUtil;
import com.aidigame.hisun.imengstar.R;
import com.example.android.bitmapfun.util.ImageCache;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.android.bitmapfun.util.ImageWorker;
import com.example.android.bitmapfun.util.ImageCache.ImageCacheParams;
import com.example.android.bitmapfun.util.ImageWorker.LoadCompleteListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 弹出框样式3
 * @author admin
 *
 */
public class Dialog6Activity extends Activity implements OnClickListener{
	ImageView closeIv;
	public static Dialog6ActivityListener listener;
	Handler handler;
	PetPicture pp;
	ImageView iv,weixinIv,friendIv,xinlangIv;
	TextView name,foodNum,timeTv;
	ImageFetcher mImageFetcher;
	UMSocialService mController;
	private LinearLayout numslayout;
	RelativeLayout parent;
//	String shareUrl="http://kouliang.tuturead.com/index.php?r=social/foodShareApi&img_id=";
	String shareUrl="http://"+Constants.IP+Constants.URL_ROOT+"r=social/foodShareApi&img_id=";
	String path;
	private long star_id=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		UiUtil.setScreenInfo(this);
		UiUtil.setWidthAndHeight(this);
		setContentView(R.layout.activity_dialog6);
		pp=(PetPicture)getIntent().getSerializableExtra("picture");
		star_id=getIntent().getLongExtra("star_id", -1);
		closeIv=(ImageView)findViewById(R.id.close);
		closeIv.setOnClickListener(this);
		handler=HandleHttpConnectionException.getInstance().getHandler(this);
		 mImageFetcher = new ImageFetcher(this, Constants.screen_width);
		name=(TextView)findViewById(R.id.name_tv);
		foodNum=(TextView)findViewById(R.id.food_tv);
		timeTv=(TextView)findViewById(R.id.time_tv);
		iv=(ImageView)findViewById(R.id.beg_iv);
		weixinIv=(ImageView)findViewById(R.id.weixin);
		friendIv=(ImageView)findViewById(R.id.friend);
		xinlangIv=(ImageView)findViewById(R.id.xinlang);
		parent=(RelativeLayout)findViewById(R.id.parent);
		numslayout=(LinearLayout)findViewById(R.id.numslayout);
		if(star_id==-1){
			numslayout.setVisibility(View.VISIBLE);
		}else{
			TextView note1_tv=(TextView)findViewById(R.id.note1_tv);
			TextView note2_tv=(TextView)findViewById(R.id.note2_tv);
			note1_tv.setText("不在应用，也能投票！");
			note2_tv.setText("每天都有免费投票的机会哦~");
		}
		
		
		
       mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    // 添加微信平台
    	UMWXHandler wxHandler = new UMWXHandler(this,Constants.Weixin_APP_KEY,Constants.Weixin_APP_SECRET);
    		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this,Constants.Weixin_APP_KEY,Constants.Weixin_APP_SECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		SinaSsoHandler  sinaSsoHandler=new SinaSsoHandler(this);
		sinaSsoHandler.addToSocialSDK();
		
		
		closeIv.setOnClickListener(this);
		friendIv.setOnClickListener(this);
		xinlangIv.setOnClickListener(this);
		weixinIv.setOnClickListener(this);
		displayImage();
		
	}
	@Override
	   protected void onPause() {
	   	// TODO Auto-generated method stub
	   	super.onPause();
	   	StringUtil.umengOnPause(this);
	   }
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	      @Override
	   protected void onResume() {
	   	// TODO Auto-generated method stub
	   	super.onResume();
	   	StringUtil.umengOnResume(this);
	   }
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) {
			case R.id.close:
				if(listener!=null){
					listener.onClose();
				}
				iv.setImageDrawable(new BitmapDrawable());
				
				
				
				finish();
				System.gc();
				break;
			case R.id.weixin:
				weixinShare();
				
				break;

			case R.id.friend:
				friendShare();
				
				break;

			case R.id.xinlang:
				xinlangShare();
				break;
				
			}
			
		}
		public static void setDialog3ActivityListener(Dialog6ActivityListener listener){
			Dialog6Activity.listener=listener;
		}
		/**
		 * 展示图片
		 */
		private void displayImage() {
			// TODO Auto-generated method stub
			String html="<html>"
		             +"<body>"
					   +"快分享给小伙伴，一起为"
		                +"<font color=\"#fb6137\">"
		                +pp.animal.pet_nickName+""
		                +"</font>"
		                +"助力！ "
		             +"</body>"
		      + "</html>";;
			
			name.setText(Html.fromHtml(html));
			foodNum.setText(""+pp.animal.foodNum);
			
			
			final BitmapFactory.Options options=new BitmapFactory.Options();
			options.inJustDecodeBounds=false;
			options.inSampleSize=StringUtil.getScaleByDPI(this);
			LogUtil.i("me", "照片详情页面Topic图片缩放比例"+StringUtil.topicImageGetScaleByDPI(this));
			if(StringUtil.topicImageGetScaleByDPI(this)>=2){
				options.inPreferredConfig=Bitmap.Config.ARGB_4444;
			}else{
				options.inPreferredConfig=Bitmap.Config.ARGB_8888;
			}
			
			options.inPurgeable=true;
			options.inInputShareable=true;
			mImageFetcher.setWidth(iv.getMeasuredWidth());
			int w=getResources().getDimensionPixelSize(R.dimen.one_dip)*100;
			int h=getResources().getDimensionPixelSize(R.dimen.one_dip)*150;
			mImageFetcher.IP=mImageFetcher.UPLOAD_THUMBMAIL_IMAGE;
			mImageFetcher.setImageCache(new ImageCache(this, new ImageCacheParams(pp.url+"@"+w+"w_"+h+"h_0l.jpg")));
			mImageFetcher.setLoadCompleteListener(new LoadCompleteListener() {
				
				@Override
				public void onComplete(Bitmap bitmap) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void getPath(String path) {
					// TODO Auto-generated method stub
					Dialog6Activity.this.path=path;
				}
			});
			mImageFetcher.loadImage(/*Constants.UPLOAD_IMAGE_RETURN_URL+*/pp.url+"@"+w+"w_"+h+"h_0l.jpg", iv,/*options*/null);
			if(myTimerTask!=null){
			    myTimerTask.cancel();
			}
			myTimerTask=new MyTimerTask(pp.create_time*1000+24*3600-System.currentTimeMillis());
			Timer timer=new Timer();
			timer.schedule(myTimerTask, 0, 1000);
		
		}
		/**
	      倒计时
	    */
	    MyTimerTask myTimerTask;
		class MyTimerTask extends TimerTask{
			long time;
	        public MyTimerTask(long time){
	        	this.time=time;
	        }
			@Override
			public void run() {
				// TODO Auto-generated method stub
				timeHandler.sendEmptyMessage(1);
			}
		}
	    Handler timeHandler=new Handler(){
	    	public void handleMessage(android.os.Message msg) {
	    			long time=pp.create_time+24*3600-System.currentTimeMillis()/1000;
	    		/*	if(time<=0){
	    				timeTvs.get(current_position).setText("00:00:00");
	    				return;
	    			}*/
	    			long h=time/3600;
	    			String hh="";
	    			if(h<10){
	    				hh="0"+h;
	    			}else{
	    				hh=""+h;
	    			}
	    			long m=time/60%60;
	    			String mm="";
	    			if(m<10){
	    				mm="0"+m;
	    			}else{
	    				mm=""+m;
	    			}
	    			long s=time%60;
	    			String ss="";
	    			if(s<10){
	    				ss="0"+s;
	    			}else{
	    				ss=""+s;
	    			}
	    			timeTv.setText(""+hh+":"+mm+":"+ss);
	    		
	    		
	    	};
	    };
		public static interface Dialog6ActivityListener{
			void onClose();
			void onButtonOne();
			void onButtonTwo();
		}
		 public void weixinShare(){
		   	   WeiXinShareContent weixinContent = new WeiXinShareContent();
		   	 //设置分享文字
		   	   if(star_id==-1){
		   		 if(StringUtil.isEmpty(pp.cmt)){
			   		 weixinContent.setShareContent("看在我这么努力卖萌的份上快来宠宠我！免费送我点口粮好不好？");
			   	   }else{
			   		 weixinContent.setShareContent(pp.cmt);
			   	   }
		   	//设置title
			   	 weixinContent.setTitle("轻轻一点，免费赏粮！我的口粮就靠你啦~");
		   	   }else{
		   		weixinContent.setShareContent("我家大萌星"+pp.animal.pet_nickName+"参加了"+pp.star_title+"活动，投票走起~");
		   		 weixinContent.setTitle("我是"+pp.animal.pet_nickName+"，快来给我投票啦~");
		   	   }
		   	  
		   	
		   	 
		   	 //设置分享内容跳转URL
		   	 weixinContent.setTargetUrl(shareUrl+pp.img_id/*+"&to=wechat"*/);
		   	 //设置分享图片
		   	 UMImage umImage=new UMImage(this,path );
		   	 weixinContent.setShareImage(umImage);
		   	 mController.setShareMedia(weixinContent);
//		   	 mController.openShare(this, true);
		   	 mController.postShare(this,SHARE_MEDIA.WEIXIN, 
		   		        new SnsPostListener() {
		   		                @Override
		   		                public void onStart() {
//		   		                    Toast.makeText(NewShowTopicActivity.this, "开始分享.", Toast.LENGTH_SHORT).show();
		   		                }
		   		                @Override
		   		                public void onComplete(SHARE_MEDIA platform, int eCode,SocializeEntity entity) {
		   		                     if (eCode == 200) {
		   		                    	
		   		     				/*if(pp.picture_type==2){
			         					MobclickAgent.onEvent(Dialog6Activity.this, "topic1_share_suc");
			         				}else if(pp.picture_type==2){
			         					MobclickAgent.onEvent(Dialog6Activity.this, "topic2_share_suc");
			         				}else {
											MobclickAgent.onEvent(Dialog6Activity.this, "food_share_suc");
									 
			         				}*/
		   		                    	if(pp.picture_type==2){
		   			                  		MobclickAgent.onEvent(Dialog6Activity.this, "topic1_share_suc");
		   									
		   								}else if(pp.picture_type==3){
		   									MobclickAgent.onEvent(Dialog6Activity.this, "topic2_share_suc");
		   		         				}else if(pp.picture_type==0){
		   		         					
		   		         				}else{
		   		         					MobclickAgent.onEvent(Dialog6Activity.this, "food_share_suc");
		   		         				}
		   		                    	 
		   		                         Toast.makeText(Dialog6Activity.this, "分享成功.", Toast.LENGTH_SHORT).show();
		   		                     } else {
		   		                          String eMsg = "";
		   		                          if (eCode == -101){
		   		                              eMsg = "没有授权";
		   		                          }
		   		                          Toast.makeText(Dialog6Activity.this, "分享失败[" + eCode + "] " + 
		   		                                             eMsg,Toast.LENGTH_SHORT).show();
		   		                     }
		   		              }
		   		});
		   	   
		   		
		      }
		 public void friendShare(){
			   CircleShareContent circleMedia = new CircleShareContent();
			   UMImage umImage=new UMImage(this, path);
			   circleMedia.setShareImage(umImage);
			   if(star_id==-1){
			   if(StringUtil.isEmpty(pp.cmt)){
				   circleMedia.setShareContent("看在我这么努力卖萌的份上快来宠宠我！免费送我点口粮好不好？");
			   	   }else{
			   		circleMedia.setShareContent(pp.cmt); 
			   	   }
			   circleMedia.setTitle(StringUtil.isEmpty(pp.cmt)?"轻轻一点，免费赏粮！我的口粮就靠你啦~":pp.cmt);
			   }else{
				   circleMedia.setShareContent("我家大萌星"+pp.animal.pet_nickName+"参加了"+pp.star_title+"活动，投票走起~");
//				   circleMedia.setTitle("我是"+pp.animal.pet_nickName+"，快来给我投票啦~");
				   circleMedia.setTitle("我家大萌星"+pp.animal.pet_nickName+"参加了"+pp.star_title+"活动，投票走起~");
			   	   }
			  
			   circleMedia.setTargetUrl(shareUrl+pp.img_id/*+"&to=wechat"*/);
			   mController.setShareMedia(circleMedia);
			   mController.postShare(this,SHARE_MEDIA.WEIXIN_CIRCLE,
					   new SnsPostListener() {
//		           @Override
		           public void onStart() {
//		               Toast.makeText(NewShowTopicActivity.this, "开始分享.", Toast.LENGTH_SHORT).show();
		           }
		           @Override
		           public void onComplete(SHARE_MEDIA platform, int eCode,SocializeEntity entity) {
		                if (eCode == 200) {
		                	/*if(pp.picture_type==2){
	         					MobclickAgent.onEvent(Dialog6Activity.this, "topic1_share_suc");
	         				}else if(pp.picture_type==2){
	         					MobclickAgent.onEvent(Dialog6Activity.this, "topic2_share_suc");
	         				}else {
									MobclickAgent.onEvent(Dialog6Activity.this, "food_share_suc");
							 
	         				}*/
		                	if(pp.picture_type==2){
		                  		MobclickAgent.onEvent(Dialog6Activity.this, "topic1_share_suc");
								
							}else if(pp.picture_type==3){
								MobclickAgent.onEvent(Dialog6Activity.this, "topic2_share_suc");
	         				}else if(pp.picture_type==0){
	         					
	         				}else{
	         					MobclickAgent.onEvent(Dialog6Activity.this, "food_share_suc");
	         				}
		                 Toast.makeText(Dialog6Activity.this, "分享成功.", Toast.LENGTH_SHORT).show();
		                } else {
		                     String eMsg = "";
		                     if (eCode == -101){
		                         eMsg = "没有授权";
		                     }
		                     Toast.makeText(Dialog6Activity.this, "分享失败[" + eCode + "] " + 
		                                        eMsg,Toast.LENGTH_SHORT).show();
		                }
		         }
		});
			   
				
		   }
		 public void xinlangShare(){
			 UserImagesJson.Data data=new UserImagesJson.Data();
				data.path=path;
				data.des=(StringUtil.isEmpty(pp.cmt)?"看在我这么努力卖萌的份上快来宠宠我！免费送我点口粮好不好？":pp.cmt)+shareUrl+pp.img_id/*+"&to=webo"*/+" #我是大萌星#";
			
		   	   SinaShareContent content=new SinaShareContent();
		   	   if(star_id==-1){
		   	   content.setShareContent(data.des);
		   	   }else{
		   		content.setShareContent("我家大萌星"+pp.animal.pet_nickName+"参加了"+("#"+pp.star_title+"#")+"活动，投票走起~"+shareUrl+pp.img_id/*+"&to=webo"*/+" #我是大萌星#"); 
		   	   }
		   	   UMImage umImage=new UMImage(Dialog6Activity.this, data.path);
		   	  
		   	   content.setShareImage(umImage);
		   	   mController.setShareMedia(content);
		   	   mController.postShare(Dialog6Activity.this, SHARE_MEDIA.SINA,new SnsPostListener() {
		   		
		   		@Override
		   		public void onStart() {
		   			// TODO Auto-generated method stub
		   			
		   		}
		   		
		   		@Override
		   		public void onComplete(SHARE_MEDIA arg0, int eCode, SocializeEntity arg2) {
		   			// TODO Auto-generated method stub
		   			if (eCode == 200) {
		   				/*if(pp.picture_type==2){
         					MobclickAgent.onEvent(Dialog6Activity.this, "topic1_share_suc");
         				}else if(pp.picture_type==2){
         					MobclickAgent.onEvent(Dialog6Activity.this, "topic2_share_suc");
         				}else {
								MobclickAgent.onEvent(Dialog6Activity.this, "food_share_suc");
						 
         				}*/
		   				if(pp.picture_type==2){
	                  		MobclickAgent.onEvent(Dialog6Activity.this, "topic1_share_suc");
							
						}else if(pp.picture_type==3){
							MobclickAgent.onEvent(Dialog6Activity.this, "topic2_share_suc");
         				}else if(pp.picture_type==0){
         					
         				}else{
         					MobclickAgent.onEvent(Dialog6Activity.this, "food_share_suc");
         				}
		                   Toast.makeText(Dialog6Activity.this, "分享成功.", Toast.LENGTH_SHORT).show();
		                  } else {
		                       String eMsg = "";
		                       if (eCode == -101){
		                           eMsg = "没有授权";
		                       }
		                       Toast.makeText(Dialog6Activity.this, "分享失败[" + eCode + "] " + 
		                                          eMsg,Toast.LENGTH_SHORT).show();
		                  }
		   		}
		   	});
			 
			 
			 
		/*	 if(!UserStatusUtil.hasXinlangAuth(this)){
					return;
				}
					UserImagesJson.Data data=new UserImagesJson.Data();
					data.path=path;
					data.des="轻轻一点，免费赏粮！快把你每天的免费粮食赏给我家"+pp.animal.pet_nickName+"！#挣口粮# "+shareUrl+pp.img_id+"&to=webo"+"（分享自@宠物星球社交应用）";
					if(UserStatusUtil.hasXinlangAuth(this)){
						XinlangShare.sharePicture(data,this);
					}*/
		 }
		 
		 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	        if(ssoHandler != null){
	           ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	        }
		}
		
}
