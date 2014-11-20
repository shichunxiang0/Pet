package com.aidigame.hisun.pet.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aidigame.hisun.pet.R;
import com.aidigame.hisun.pet.adapter.HomeViewPagerAdapter;
import com.aidigame.hisun.pet.bean.Animal;
import com.aidigame.hisun.pet.bean.Gift;
import com.aidigame.hisun.pet.constant.Constants;
import com.aidigame.hisun.pet.http.HttpUtil;
import com.aidigame.hisun.pet.util.HandleHttpConnectionException;
import com.aidigame.hisun.pet.util.LogUtil;
import com.aidigame.hisun.pet.util.StringUtil;
import com.aidigame.hisun.pet.view.RoundImageView;
import com.aidigame.hisun.pet.widget.ShowProgress;
import com.aidigame.hisun.pet.widget.fragment.FourGiftBox;
import com.aidigame.hisun.pet.widget.fragment.FourGiftBox.SendGiftResultListener;
import com.aidigame.hisun.pet.widget.fragment.MarketFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 给人送礼物，弹出框
 * @author admin
 *
 */
public class DialogGiveSbGiftResultActivity extends Activity{
	

	TextView nextTV,sureTV,rqTV,giftNameTv,animalTV,desTv;
	ImageView closeIV,giftIV;
	RoundImageView icon;
	
	public static DialogGiveSbGiftResultActivity dialogGiveSbGiftActivity;
	RelativeLayout parent;
	HandleHttpConnectionException handleHttpConnectionException;
	Animal animal;
	Gift gift;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dialogGiveSbGiftActivity=this;
		setContentView(R.layout.activity_send_gift_result);
		MobclickAgent.onEvent(this, "gift_button");
		this.animal=(Animal)getIntent().getSerializableExtra("animal");
		gift=(Gift)getIntent().getSerializableExtra("gift");
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		closeIV=(ImageView)findViewById(R.id.close_view);
		animalTV=(TextView)findViewById(R.id.textView1);
		giftNameTv=(TextView)findViewById(R.id.gift_name);
		giftIV=(ImageView)findViewById(R.id.gift_iv);
		rqTV=(TextView)findViewById(R.id.rq_tv);
		nextTV=(TextView)findViewById(R.id.next);
		sureTV=(TextView)findViewById(R.id.sure);
		desTv=(TextView)findViewById(R.id.textView3);
		icon=(RoundImageView)findViewById(R.id.round);
		parent=(RelativeLayout)findViewById(R.id.share_bitmap_layout);
		 BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=false;
		options.inSampleSize=8;
		options.inPreferredConfig=Bitmap.Config.RGB_565;
		options.inPurgeable=true;
		options.inInputShareable=true;
		DisplayImageOptions displayImageOptions=new DisplayImageOptions
	            .Builder()
	            .showImageOnLoading(R.drawable.pet_icon)
	            .showImageOnFail(R.drawable.pet_icon)
		        .cacheInMemory(true)
		        .cacheOnDisc(true)
		        .bitmapConfig(Bitmap.Config.RGB_565)
		        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		        .decodingOptions(options)
                .build();
		
		if(animal!=null){
			animalTV.setText("给"+animal.pet_nickName+"送个礼物");
			ImageLoader imageLoader=ImageLoader.getInstance();
			imageLoader.displayImage(Constants.ANIMAL_DOWNLOAD_TX+animal.pet_iconUrl, icon, displayImageOptions);
		}
		if(gift!=null){
		
			giftNameTv.setText(""+gift.name+" X 1");
			if(gift.add_rq>0){
				rqTV.setText("人气  + "+gift.add_rq);
				if(animal!=null){
					desTv.setText("您送给"+gift.animal.pet_nickName+"一个"+gift.name+"，"+gift.animal.pet_nickName+gift.effect_des);
				}
				parent.setBackgroundResource(R.drawable.gift_result_good);
			}else{
				parent.setBackgroundResource(R.drawable.gift_result_bad);
				rqTV.setText("人气  - "+(-gift.add_rq));
				if(animal!=null){
					desTv.setText("您对"+gift.animal.pet_nickName+"扔了一个"+gift.name+"，"+gift.animal.pet_nickName+gift.effect_des);
				}
			}
			try {
				giftIV.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(""+gift.no+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		closeIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				if(DialogGiveSbGiftActivity1.dialogGiveSbGiftActivity!=null){
					DialogGiveSbGiftActivity1.dialogGiveSbGiftActivity.finish();;;
				}
			}
		});
		sureTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				if(DialogGiveSbGiftActivity1.dialogGiveSbGiftActivity!=null){
					DialogGiveSbGiftActivity1.dialogGiveSbGiftActivity.finish();;
				}
			}
		});
		nextTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				if(DialogGiveSbGiftActivity1.dialogGiveSbGiftActivity!=null){
					DialogGiveSbGiftActivity1.dialogGiveSbGiftActivity.setInvisible(false);;
				}
			}
		});
		
	}


}
