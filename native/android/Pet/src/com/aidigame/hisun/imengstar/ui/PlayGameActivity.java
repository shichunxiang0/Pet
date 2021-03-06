package com.aidigame.hisun.imengstar.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aidigame.hisun.imengstar.PetApplication;
import com.aidigame.hisun.imengstar.bean.Animal;
import com.aidigame.hisun.imengstar.constant.Constants;
import com.aidigame.hisun.imengstar.http.HttpUtil;
import com.aidigame.hisun.imengstar.http.json.UserImagesJson;
import com.aidigame.hisun.imengstar.util.ImageUtil;
import com.aidigame.hisun.imengstar.util.LogUtil;
import com.aidigame.hisun.imengstar.util.StringUtil;
import com.aidigame.hisun.imengstar.util.UiUtil;
import com.aidigame.hisun.imengstar.widget.WeixinShare;
import com.aidigame.hisun.imengstar.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;

public class PlayGameActivity extends BaseActivity {
	WebView webView;
	String url="http://"+Constants.IP+Constants.URL_ROOT+"r=game/2048&sig=";
	String shareUrl="http://"+Constants.IP+Constants.URL_ROOT+"r=game/dcz&aid=";
	Animal animal;
	ImageView back;
	TextView shareTv;
	UMSocialService mController;
	RelativeLayout rooLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(this, "play");
		setContentView(R.layout.activity_play_game);
		animal=(Animal)getIntent().getSerializableExtra("animal");
		webView=(WebView)findViewById(R.id.webview);
		back=(ImageView)findViewById(R.id.button1);
		shareTv=(TextView)findViewById(R.id.three_point_iv);
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		
		
		
		rooLayout=(RelativeLayout)findViewById(R.id.root_layout);
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize=4;
		rooLayout.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.blur, options)));
		
		
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this,Constants.Weixin_APP_KEY,Constants.Weixin_APP_SECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isTaskRoot()){
					if(HomeActivity.homeActivity!=null){
						ActivityManager am=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
						am.moveTaskToFront(HomeActivity.homeActivity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
					}else{
						Intent intent=new Intent(PlayGameActivity.this,HomeActivity.class);
						PlayGameActivity.this.startActivity(intent);
					}
				}
				
				
				
				finish();
				System.gc();
			}
		});
		shareTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				share();
			}

		});
		String sig=HttpUtil.getMD5Value("aid="+animal.a_id);
		url=url+sig+"&SID="+PetApplication.SID+"&aid="+animal.a_id;
		LogUtil.i("me", "逗一逗url="+url);
		webView.loadUrl(url);
		
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				LogUtil.i("me", "捏虫子url="+url);
				return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
			}
		});
		/**
		 * 必须得加
		 */
		WebSettings webSettings = webView.getSettings(); 
		webSettings.setJavaScriptEnabled(true); 
		
		
	}

	private void share() {
		// TODO Auto-generated method stub
		if(Constants.api==null){
			boolean flag=WeixinShare.regToWeiXin(this);
			if(!flag){
				Toast.makeText(this,"目前您的微信版本过低或未安装微信，安装微信才能使用。", Toast.LENGTH_LONG).show();
				
				return;
			}
		}
		Bitmap bmp=ImageUtil.getImageFromView(webView);
		String path=Constants.Picture_Root_Path+File.separator+System.currentTimeMillis()+".png";
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(path);
			bmp.compress(CompressFormat.PNG, 100, fos);
			UserImagesJson.Data data=new UserImagesJson.Data();
			data.path=path;
			friendShare(path);
			/*if(WeixinShare.shareHttpLink(shareUrl+animal.a_id, "O(∩_∩)O哈！", this)){
//				Toast.makeText(this,"成功分享到微信。", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this,"分享到微信失败。", Toast.LENGTH_LONG).show();
			}*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	 public void friendShare(String path){
		   CircleShareContent circleMedia = new CircleShareContent();
		   UMImage umImage=new UMImage(this, path);
		   circleMedia.setShareImage(umImage);
		   circleMedia.setTitle("痒痒痒，快给本宫挠挠！");
		   circleMedia.setTargetUrl(shareUrl+animal.a_id);
		   mController.setShareMedia(circleMedia);
		   mController.postShare(this,SHARE_MEDIA.WEIXIN_CIRCLE,
				   new SnsPostListener() {
//	           @Override
	           public void onStart() {
//	               Toast.makeText(NewShowTopicActivity.this, "开始分享.", Toast.LENGTH_SHORT).show();
	           }
	           @Override
	           public void onComplete(SHARE_MEDIA platform, int eCode,SocializeEntity entity) {
	                if (eCode == 200) {
	                 Toast.makeText(PlayGameActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
	                } else {
	                     String eMsg = "";
	                     if (eCode == -101){
	                         eMsg = "没有授权";
	                     }
	                     Toast.makeText(PlayGameActivity.this, "分享失败[" + eCode + "] " + 
	                                        eMsg,Toast.LENGTH_SHORT).show();
	                }
	         }
	});
		   
			
	   }
	

}
