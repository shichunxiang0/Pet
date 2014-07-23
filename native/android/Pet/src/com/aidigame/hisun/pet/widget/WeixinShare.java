package com.aidigame.hisun.pet.widget;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;

import com.aidigame.hisun.pet.R;
import com.aidigame.hisun.pet.constant.Constants;
import com.aidigame.hisun.pet.http.json.UserImagesJson;
import com.aidigame.hisun.pet.util.LogUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WeixinShare {
	public static boolean getToken(){
		
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "post_timeline";
		req.state = "none";
		Constants.api.sendReq(req);
		Intent intent=new Intent();
		Constants.api.handleIntent(intent, new IWXAPIEventHandler() {
			
			@Override
			public void onResp(BaseResp arg0) {
				// TODO Auto-generated method stub
				LogUtil.i("exception", ""+arg0.toString());
				LogUtil.i("exception", ""+arg0.transaction);
			}
			
			@Override
			public void onReq(BaseReq arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		return false;
		
	}
	public static boolean regToWeiXin(Context context){
		//通过WXAPIFactory获得IWXAPI实例
		IWXAPI api=WXAPIFactory.createWXAPI(context, Constants.Weixin_APP_KEY, true);
		//将应用的appid注册 到微信
		boolean flag=api.registerApp(Constants.Weixin_APP_KEY);
		LogUtil.i("exception","分享到朋友圈,注册到微信："+flag);
		if(flag){
			Constants.api=api;
		}
//		shareText("分享到朋友圈");
//		sharePicture(null,context);
//		shareBitmap(context);
		return flag;
	}
	public static boolean shareText(String text){
		boolean flag=false;
//		String text="分享到朋友圈";
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text");; // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
//		req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		
		// 调用api接口发送数据到微信
		flag=Constants.api.sendReq(req);
		LogUtil.i("exception", "分享文字返回结果："+flag);
		return flag;
	}
	/**
	 * 分享文字加图片
	 * @param context
	 * @param mode 1 微信联系人；2 朋友圈
	 */
	public static void sharePicture(UserImagesJson.Data data,int mode){
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://www.baidu.com";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "1";
		msg.description = "1";
		byte[] temp=scaleSize(data.path);
		if(temp==null)return;
		msg.thumbData = temp;
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		if(mode==2){
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		}else{
			req.scene =SendMessageToWX.Req.WXSceneSession;
		}
		
		boolean flag=Constants.api.sendReq(req);
		LogUtil.i("exception", "上传图片成功"+flag);
	}
	/**
	 * 只分享照片
	 * @param context
	 */
	public static boolean shareBitmap(UserImagesJson.Data data,int mode){
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize=2;
		Bitmap bitmap=BitmapFactory.decodeFile(data.path,options);
		WXImageObject imgObj = new WXImageObject(bitmap);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		
//		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//		bmp.recycle();
		byte[] temp=scaleSize(data.path);
		msg.thumbData = temp;  // 设置缩略图  大小要小于32kb

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;	
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		if(mode==1){
			req.scene = SendMessageToWX.Req.WXSceneSession;
		}else{
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		}
//		req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		boolean flag=Constants.api.sendReq(req);
		return flag;
	}
	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	private static Bitmap compressImage(Bitmap image) {  
		
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>10) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
           
            if(options>10){
            	options -= 10;//每次都减少10 
            } else{
            	options -= 1;//每次都减少10 
            }
            if(options<0){
            	options=0;
            	break;
            }
            
            LogUtil.i("exception", "分享图片大小："+baos.toByteArray().length / 1024+"kb");
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }
	public static byte[] scaleSize(String path){
		int mode=1;
		if(path.endsWith("png")){
			mode=1;
		}else{
			mode=2;
		}
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		Bitmap bmp=BitmapFactory.decodeFile(path);
		if(mode==1){
			bmp.compress(CompressFormat.PNG, 100, bos);
		}else{
			bmp.compress(CompressFormat.JPEG, 100, bos);
		}
		bmp.recycle();
		if(bos.size()<29){
			return bos.toByteArray();
		}
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize=2;
	    bmp=BitmapFactory.decodeFile(path, options);
	   try {
		bos.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    bos=new ByteArrayOutputStream();
	    if(mode==1){
			bmp.compress(CompressFormat.PNG, 100, bos);
		}else{
			bmp.compress(CompressFormat.JPEG, 100, bos);
		}
	    if(bos.size()/1024<29){
	    	return bos.toByteArray();
	    }
	    
	    options=new BitmapFactory.Options();
		options.inSampleSize=4;
	    bmp=BitmapFactory.decodeFile(path, options);
	   try {
		bos.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    bos=new ByteArrayOutputStream();
	    if(mode==1){
			bmp.compress(CompressFormat.PNG, 100, bos);
		}else{
			bmp.compress(CompressFormat.JPEG, 100, bos);
		}
	    if(bos.size()/1024<29){
	    	return bos.toByteArray();
	    }
	    
	    options=new BitmapFactory.Options();
		options.inSampleSize=8;
	    bmp=BitmapFactory.decodeFile(path, options);
	   try {
		bos.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    bos=new ByteArrayOutputStream();
	    if(mode==1){
			bmp.compress(CompressFormat.PNG, 100, bos);
		}else{
			bmp.compress(CompressFormat.JPEG, 100, bos);
		}
	    if(bos.size()/1024<29){
	    	return bos.toByteArray();
	    }
	    
	    options=new BitmapFactory.Options();
		options.inSampleSize=16;
	    bmp=BitmapFactory.decodeFile(path, options);
	   try {
		bos.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    bos=new ByteArrayOutputStream();
	    if(mode==1){
			bmp.compress(CompressFormat.PNG, 100, bos);
		}else{
			bmp.compress(CompressFormat.JPEG, 100, bos);
		}
	    if(bos.size()/1024<29){
	    	return bos.toByteArray();
	    }
	    
	    options=new BitmapFactory.Options();
		options.inSampleSize=32;
	    bmp=BitmapFactory.decodeFile(path, options);
	   try {
		bos.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	    bos=new ByteArrayOutputStream();
	    if(mode==1){
			bmp.compress(CompressFormat.PNG, 100, bos);
		}else{
			bmp.compress(CompressFormat.JPEG, 100, bos);
		}
	    if(bos.size()/1024<29){
	    	return bos.toByteArray();
	    }
	    return null;
		
	}

}