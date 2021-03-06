package com.aidigame.hisun.imengstar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.text.Html;
import android.text.Spannable;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.aidigame.hisun.imengstar.R;
import com.aidigame.hisun.imengstar.bean.PetPicture;
import com.aidigame.hisun.imengstar.bean.PetPicture.Comments;
import com.aidigame.hisun.imengstar.constant.Constants;
import com.aidigame.hisun.imengstar.huanxin.NewSmileUtils;
import com.aidigame.hisun.imengstar.util.LogUtil;
import com.aidigame.hisun.imengstar.util.StringUtil;
import com.aidigame.hisun.imengstar.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
/**
 * 评论列表
 * @author admin
 *
 */
public class CommentListViewAdapter extends BaseAdapter{
	Context context;
	ArrayList<PetPicture.Comments> listComment;
	DisplayImageOptions displayImageOptions;
	ClickUserName clickUserName;
	public CommentListViewAdapter(Context context,ArrayList<PetPicture.Comments> listComment){
		this.context=context;
		this.listComment=listComment;
BitmapFactory.Options options=new BitmapFactory.Options();
		
		options.inJustDecodeBounds=false;
		options.inSampleSize=8;
		options.inPreferredConfig=Bitmap.Config.RGB_565;
		options.inPurgeable=true;
		options.inInputShareable=true;
		displayImageOptions=new DisplayImageOptions
	            .Builder()
	            .showImageOnLoading(R.drawable.user_icon)
	            .showImageOnFail(R.drawable.user_icon)
		        .cacheInMemory(true)
		        .cacheOnDisc(true)
		        .bitmapConfig(Bitmap.Config.RGB_565)
		        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//		        .decodingOptions(options)
                .build();
	}
	public void update(ArrayList<PetPicture.Comments> listComment){
		this.listComment=listComment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listComment.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listComment.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_comment_listview, null);
			holder.nameTextView=(TextView)convertView.findViewById(R.id.textView1);
			holder.body=(TextView)convertView.findViewById(R.id.textView2);
			holder.time=(TextView)convertView.findViewById(R.id.textView3);
			holder.layout=(RelativeLayout)convertView.findViewById(R.id.layout);
			holder.warning_iv=(ImageView)convertView.findViewById(R.id.warning_iv);
//			holder.byLayout=(LinearLayout)convertView.findViewById(R.id.bylayout);
//			holder.byTv=(TextView)convertView.findViewById(R.id.bytv);
//			holder.huifuTv=(TextView)convertView.findViewById(R.id.huifu_tv);
			holder.usericon=(RoundImageView)convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
		final PetPicture.Comments comments=listComment.get(position);
		ImageLoader imageLoader=ImageLoader.getInstance();
		int w=context.getResources().getDimensionPixelSize(R.dimen.one_dip)*54;
		imageLoader.displayImage(Constants.USER_THUBMAIL_DOWNLOAD_TX+comments.usr_tx+"@"+w+"w_"+w+"h_0l.jpg", holder.usericon, displayImageOptions);
		if(!StringUtil.isEmpty(Constants.CON_VERSION)&&"1.0".equals(Constants.CON_VERSION)){
			holder.warning_iv.setVisibility(View.VISIBLE);
		}else{
			holder.warning_iv.setVisibility(View.GONE);
		}
		holder.body.setVisibility(View.VISIBLE);
		if(comments.isReply){
			
			String[] str=comments.reply_name.split("@");
			if(comments.reply_name.contains("@")){
				str=comments.reply_name.split("@");
			}else if(comments.reply_name.contains("&")){
				str=comments.reply_name.split("&");
			}else
			if(!StringUtil.isEmpty(comments.reply_name)){
				str=new String[2];
				str[0]=comments.name;
				str[1]=comments.reply_name;
			}
			if(str.length>1){
				/*holder.byLayout.setVisibility(View.VISIBLE);
				holder.huifuTv.setVisibility(View.GONE);
				holder.body.setVisibility(View.GONE);
				String html="<html><body>"
						+ "<font color=\"#3d3d3d\">"
                        +"回复  "
						+ "</font>"
						
						+str[1]
						+ "<font color=\"#3d3d3d\">"
                        +":"+(comments.body==null?"":comments.body)
                        + "</font>"
						+ "</body></html>";
				holder.byTv.setText(Html.fromHtml(html));
				holder.nameTextView.setText(comments.name);*/
//				holder.byLayout.setVisibility(View.GONE);
//				holder.huifuTv.setVisibility(View.GONE);
				String html="<html><body>"
						+comments.name
						+ "<font color=\"#3d3d3d\">"
                        +"回复  "
						+ "</font>"
						+str[1]
						/*+ "<font color=\"#3d3d3d\">"
                        +":"+(comments.body==null?"":comments.body)
                        + "</font>"*/
						+ "</body></html>";
				Paint paint=holder.nameTextView.getPaint();
			    float nw=paint.measureText(comments.name+"回复  "+str[1]);
			    float rw=holder.nameTextView.getWidth();
			    LogUtil.i("mi", "holder.nameTextView:"+"nw="+nw+";rw="+rw);
			    if(nw>rw){
			    	holder.nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
			    }
				holder.nameTextView.setText(Html.fromHtml(html));
			}else{
//				holder.byLayout.setVisibility(View.GONE);
//				holder.huifuTv.setVisibility(View.GONE);
				holder.nameTextView.setText(comments.name);
			}
			
		}else{
//			holder.byLayout.setVisibility(View.GONE);
//			holder.huifuTv.setVisibility(View.GONE);
			holder.nameTextView.setText(comments.name);
		}
		Spannable span = NewSmileUtils.getSmiledText(context, ""+(comments.body==null?"":comments.body));
		// 设置内容
		
		holder.body.setText(span, BufferType.SPANNABLE);
		holder.time.setText(""+StringUtil.judgeTime(comments.create_time));
		holder.warning_iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(clickUserName!=null){
					clickUserName.reportComment();
				}
			}
		});
		/*holder.nameTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(clickUserName!=null){
					clickUserName.clickUserName(comments);
				}
			}
		});*/
		/*holder.layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(clickUserName!=null){
					clickUserName.clickUserName(comments);
				}
			}
		});*/
		final GestureDetector gesture=new GestureDetector(new MyGestureDector(comments));
		holder.layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gesture.onTouchEvent(event);
			}
		});
	/*	holder.layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return new GestureDetector(new MyListGestureDector(comments)).onTouchEvent(event);
			}
		});*/
		
		return convertView;
	}
	class Holder{
		TextView nameTextView;
		TextView body;
		TextView time;
		RelativeLayout layout;
		ImageView warning_iv;
//		LinearLayout byLayout;
//		TextView byTv,huifuTv;
		RoundImageView usericon;
	}
	class MyGestureDector implements OnGestureListener{
		Comments comments;
		public MyGestureDector(Comments comments){
			this.comments=comments;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			if(clickUserName!=null){
				clickUserName.clickUserName(comments);
			}
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			if(clickUserName!=null){
				clickUserName.onScroll(e1, e2, distanceX, distanceY);;
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

	public void setClickUserName(ClickUserName clickUserName){
		this.clickUserName=clickUserName;
	}
	public static interface ClickUserName{
		void clickUserName(PetPicture.Comments cmt);
		void reportComment();
		void onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3);
	}
	
	
	/*public class MyListGestureDector implements OnGestureListener{
    	int mode;//1，分享，送礼，点赞列表；2，评论列表
    	boolean hasStart=false;
    	int touchSlop;
    	Comments comments;
    	public MyListGestureDector(Comments comments){
    		this.comments=comments;
    		touchSlop=ViewConfiguration.getTouchSlop();
    	}
		
		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			if(clickUserName!=null){
				clickUserName.clickUserName(comments);
			}
			hasStart=false;
			return true;
		}
		
		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			LogUtil.i("mi", "arg2==="+arg2+",arg3="+arg3);
			if(!hasStart&&Math.abs(arg2)>Math.abs(arg3)&&Math.abs(arg2)>touchSlop){
				if(NewShowTopicActivity.newShowTopicActivity.current_page==1){
					if(arg2<0){
						NewShowTopicActivity.newShowTopicActivity.positive=true;
						NewShowTopicActivity.newShowTopicActivity.applyRotation(1, 0, 90); 
						
					}else{
						NewShowTopicActivity.newShowTopicActivity.positive=false;
//						applyRotation(1, 180, 90); 
						NewShowTopicActivity.newShowTopicActivity.applyRotation(1, 180, 90); 
					}
					
					NewShowTopicActivity.newShowTopicActivity.current_page=-1;
				}else{
					if(arg2>0){
						NewShowTopicActivity.newShowTopicActivity.positive=false;
						NewShowTopicActivity.newShowTopicActivity.applyRotation(-1, 180, 90); 
						
					}else{
						NewShowTopicActivity.newShowTopicActivity.positive=true;
						NewShowTopicActivity.newShowTopicActivity.applyRotation(-1, 0, 90); 
						
					}
					
					NewShowTopicActivity.newShowTopicActivity.current_page=1;
				}
				hasStart=true;
				return true;
			}else{
				return false;
			}
			
		}
		
		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			hasStart=false;
			return true;
		}
	}*/

}
