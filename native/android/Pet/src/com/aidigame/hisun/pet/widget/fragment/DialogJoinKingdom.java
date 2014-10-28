package com.aidigame.hisun.pet.widget.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.aidigame.hisun.pet.R;
import com.aidigame.hisun.pet.bean.Animal;
import com.aidigame.hisun.pet.constant.Constants;
import com.aidigame.hisun.pet.http.HttpUtil;
import com.aidigame.hisun.pet.ui.ChoseAcountTypeActivity;
import com.aidigame.hisun.pet.ui.NewHomeActivity;
import com.aidigame.hisun.pet.util.HandleHttpConnectionException;
import com.aidigame.hisun.pet.util.StringUtil;

/**
 * 用户是否注册弹窗
 * @author admin
 *
 */
public class DialogJoinKingdom {
	View parent,view;
	Context context;
	PopupWindow popupWindow;
	View blackView;
	Animal animal;
	HandleHttpConnectionException handleHttpConnectionException;
	ResultListener listener;
	public DialogJoinKingdom(View parent,Context context,View blackView,Animal animal){
		this.parent=parent;
		this.context=context;
		this.blackView=blackView;
		this.animal=animal;
		handleHttpConnectionException=HandleHttpConnectionException.getInstance();
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		view=LayoutInflater.from(context).inflate(R.layout.popup_dialog_join_kingdom, null);
		popupWindow=new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int[] location=new int[2];
		parent.getLocationInWindow(location);
		blackView.setBackgroundResource(R.color.window_black_bagd);
		popupWindow.showAsDropDown(parent, location[0]+parent.getWidth()/2-view.getMeasuredWidth()/2, -view.getMeasuredHeight()/2);
		view.findViewById(R.id.imageView1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				blackView.setBackgroundDrawable(null);
			}
		});
		view.findViewById(R.id.textView3).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						/*
						 * 加入王国
						 */
						final boolean flag=HttpUtil.joinOrQuitKingdom(context,animal, handleHttpConnectionException.getHandler(context), 0);
						HttpUtil.animalInfo(context,animal, handleHttpConnectionException.getHandler(context));
					    	
	                        handleHttpConnectionException.getHandler(context).post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(flag){
									animal.is_join=true;
							    	animal.fans++;
							    	String[] jobs=StringUtil.getUserJobs();
							    	animal.u_rank=jobs[0];
							    	animal.job=jobs[0];
							    	animal.u_rankCode=1;
									Toast.makeText(context, "捧TA成功", 1000).show();
									if(Constants.user!=null&&Constants.user.aniList!=null){
										if(!Constants.user.aniList.contains(animal))
										Constants.user.aniList.add(animal);
									}else{
										Constants.user.aniList=new ArrayList<Animal>();
										Constants.user.aniList.add(animal);
									}
									if(MenuFragment.menuFragment!=null){
										MenuFragment.menuFragment.setViews();
									}
									 }else{
										 Toast.makeText(context, "加入王国失败", 1000).show();
									 }
									if(listener!=null){
										listener.getResult(flag);
									}
								}
							});
					   
					}
				}).start();
				popupWindow.dismiss();
				
			}
		});
		popupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				closePopupTodo();
				blackView.setBackgroundDrawable(null);
			}
		});
		
	}
	public void closePopupTodo(){
		if(context instanceof NewHomeActivity){
			((NewHomeActivity) context).homeFragment.setBlureViewInvisible();
		}
	}
	public void setResultListener(ResultListener listener){
		this.listener=listener;
	}
public static interface ResultListener{
	void getResult(boolean isSuccess);
}
}