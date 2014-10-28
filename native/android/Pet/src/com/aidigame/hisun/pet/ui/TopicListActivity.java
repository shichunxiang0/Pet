package com.aidigame.hisun.pet.ui;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aidigame.hisun.pet.R;
import com.aidigame.hisun.pet.adapter.TopicListAdapter;
import com.aidigame.hisun.pet.bean.Topic;
import com.aidigame.hisun.pet.constant.Constants;
import com.aidigame.hisun.pet.http.HttpUtil;
import com.aidigame.hisun.pet.util.HandleHttpConnectionException;
import com.aidigame.hisun.pet.util.StringUtil;
import com.aidigame.hisun.pet.util.UiUtil;
import com.aidigame.hisun.pet.view.PullToRefreshAndMoreView;
import com.aidigame.hisun.pet.view.PullToRefreshAndMoreView.PullToRefreshAndMoreListener;
import com.aidigame.hisun.pet.widget.fragment.HomeFragment;
/**
 * 话题列表
 * @author admin
 *
 */
public class TopicListActivity extends Activity implements PullToRefreshAndMoreListener{
	FrameLayout frameLayout;
	View viewTopWhite;
	ImageView back;
	TextView sureTV;
	EditText inputET;
	PullToRefreshAndMoreView pullToRefreshAndMoreView;
	ListView listView;
	ArrayList<Topic> topicList;
	TopicListAdapter adapter;
	HandleHttpConnectionException handleHttpConnectionException;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		UiUtil.setScreenInfo(this);
		UiUtil.setWidthAndHeight(this);
		setContentView(R.layout.activity_topic_list);
		handleHttpConnectionException=HandleHttpConnectionException.getInstance();
		
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		back=(ImageView)findViewById(R.id.back);
		sureTV=(TextView)findViewById(R.id.sure_tv);
		inputET=(EditText)findViewById(R.id.input_topic_et);
		pullToRefreshAndMoreView=(PullToRefreshAndMoreView)findViewById(R.id.activity_listview);
		pullToRefreshAndMoreView.setListener(this);
		listView=pullToRefreshAndMoreView.getListView();
		
		setBlurImageBackground(); 
		
		topicList=new ArrayList<Topic>();
		loadData();
		adapter=new TopicListAdapter(this, topicList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				SubmitPictureActivity.submitPictureActivity.setTopic(topicList.get(position));
				TopicListActivity.this.finish();
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TopicListActivity.this.finish();
			}
		});
		sureTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String topicStr=inputET.getText().toString();
				if(StringUtil.isEmpty(topicStr)){
					Toast.makeText(TopicListActivity.this, "话题名称不能为空", Toast.LENGTH_SHORT).show();
				}else{
					Topic topic=new Topic();
					topic.topic=topicStr;
					topic.topic_id=-1;
					SharedPreferences sp=getSharedPreferences(Constants.SHAREDPREFERENCE_NAME, Context.MODE_WORLD_WRITEABLE);
					Editor editor=sp.edit();
					String temp=sp.getString("topic", "");
					temp+=topic.topic+",";
					editor.putString("topic", temp);
					editor.commit();
					SubmitPictureActivity.submitPictureActivity.setTopic(topic);
					TopicListActivity.this.finish();
				}
				
				
			}
		});
		inputET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int originL=s.length();
				deleteErrorChar(s);
				int currentL=s.length();
				if(originL>currentL){
					Toast.makeText(TopicListActivity.this, "请不要输入\"#\"", Toast.LENGTH_SHORT).show();
				}
				String string=s.toString();
				if(StringUtil.isEmpty(string))return;
				ArrayList<Topic> temp=new ArrayList<Topic>();
				for(int i=0;i<topicList.size();i++){
					if (topicList.get(i).topic.contains(string)) {
						if(!temp.contains(topicList.get(i)))
						temp.add(topicList.get(i));
					}
					for(int j=0;j<s.length();j++){
						if((topicList.get(i).topic.contains(""+s.charAt(j)))){
							if(!temp.contains(topicList.get(i))){
								temp.add(topicList.get(i));
								break;
							}
								
						}
					}
				}
				for(int i=0;i<topicList.size();i++){
					if(!temp.contains(topicList.get(i))){
						temp.add(topicList.get(i));
					}
				}
				topicList=temp;
				adapter.updateList(temp);
				adapter.notifyDataSetChanged();
				
			}
			public void deleteErrorChar(Editable s){
				for(int i=0;i<s.length();i++){
                    if('#'==(s.charAt(i))){
						s.delete(i, i+1);
						deleteErrorChar(s);
						return;
					}
				}
			}
		});
	}
	private void loadData() {
		// TODO Auto-generated method stub
		//TODO 首先加载本地用户曾经创建的话题列表
		SharedPreferences sp=getSharedPreferences(Constants.SHAREDPREFERENCE_NAME, Context.MODE_WORLD_READABLE);
		String topicString=sp.getString("topic", null);
		if(!StringUtil.isEmpty(topicString)){
			String[] strs=topicString.split(",");
			Topic topic=null;
			if(strs!=null&&strs.length>0){
				for(int i=0;i<strs.length;i++){
					topic=new Topic();
					topic.topic_id=-1;
					topic.topic=strs[i];
					if(!topicList.contains(topic))
					topicList.add(topic);
				}
			}
		}
       new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final ArrayList<Topic> tempArrayList=HttpUtil.imageTopicApi(TopicListActivity.this,handleHttpConnectionException.getHandler(TopicListActivity.this));
				if(tempArrayList!=null){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							for(int i=tempArrayList.size()-1;i>=0;i--){
								if(topicList.contains(tempArrayList.get(i))){
									topicList.remove(tempArrayList.get(i));
								}
								topicList.add(0, tempArrayList.get(i));
							}
							adapter.updateList(topicList);
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		}).start();
	}
	/**
	 * 设置毛玻璃背景，列表滑动时顶部变透明并显示列表
	 */

	private void setBlurImageBackground() {
		// TODO Auto-generated method stub
		frameLayout=(FrameLayout)findViewById(R.id.framelayout);
		viewTopWhite=(View)findViewById(R.id.top_white_view);
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(HomeFragment.blurBitmap==null){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						frameLayout.setBackgroundDrawable(new BitmapDrawable(HomeFragment.blurBitmap));
						frameLayout.setAlpha(0.9342857f);
					}
				});
			}
		}).start();
		 listView.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					if(listView.getFirstVisiblePosition()==0&&listView.getChildAt(0).getTop()==0){
						viewTopWhite.setVisibility(View.VISIBLE);
					}else{
						if(viewTopWhite.getVisibility()!=View.GONE){
							viewTopWhite.setVisibility(View.GONE);
						}
					}
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
					
				}
			});
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		pullToRefreshAndMoreView.onRefreshFinish();
	}
	@Override
	public void onMore() {
		// TODO Auto-generated method stub
		pullToRefreshAndMoreView.onMoreFinish();
	}
	/*public static class Topic{
		public String name;
		public boolean isGovernment;
		public boolean isSelected;
		public int topic_id;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + topic_id;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Topic other = (Topic) obj;
			if (topic_id != other.topic_id)
				return false;
			return true;
		}
		
	}*/

}