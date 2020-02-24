package com.tss.webserver.activity;

import android.content.Intent;
import android.view.View;

import com.tss.webserver.utils.DialogFactory;

import org.json.JSONException;
import org.json.JSONObject;

import app.TssServerApplication;

public class SwipeCardActivity extends BaseSwipeCardActivity {

	final static String URL_DEV_TRANSECTION = "https://secure-dev.reddotpayment.com/service/payment-api";
	final static String URL_PRO_TRANSECTION = "https://secure.reddotpayment.com/service/payment-api";
	String amount = "";
	String id = "";

	@Override
	public void initViewData() {

		boolean showMoney = true;
		boolean showCatdNo = false;
		amount = getIntent().getStringExtra("amount");
		id = getIntent().getStringExtra("id");
//		String title =getResources().getString(R.string.input_cardno);//"输入卡号";
//		if(Cache.getInstance().getTransCode().equals(Constants.transCode.consume)){
//			title = getResources().getString(R.string.gene_trans_consume);//消费
//		}else if(Cache.getInstance().getTransCode().equals(Constants.transCode.consume_void)){
//			title = getResources().getString(R.string.gene_trans_consume_void);//消费撤销
//		}else if(Cache.getInstance().getTransCode().equals(Constants.transCode.query_balance)){
//			title = getResources().getString(R.string.gene_trans_balance);//余额查询
//		}else if(Cache.getInstance().getTransCode().equals(Constants.transCode.refund)){
//			title = getResources().getString(R.string.gene_trans_refund);//退货
//		}else if(Cache.getInstance().getTransCode().equals(Constants.transCode.unionpay_search)){
//			title = getResources().getString(R.string.cup_coupon);//银联优惠券
//		}else if(Cache.getInstance().getTransCode().equals(Constants.transCode.pre_licensing_completation)){
//			showMoney=false;
//			showCatdNo=true;
//		}

		setTopTitle("IP : " + TssServerApplication.Companion.getIpAddress());
		showTransMoney(showMoney);
		showInputCarno(showCatdNo);
	}

	public void completeDialog() {
		DialogFactory.showConfirmMessageTimeout(5000, SwipeCardActivity.this, "การชำระเงิน", "ชำระเงินสำเร็จ", "close", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.putOpt("status", "200");
					jsonObject.putOpt("msg", "transection success");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Intent data = new Intent();
				data.putExtra("response", jsonObject.toString());
				setResult(RESULT_OK, data);
				finish();
			}
		});
	}

	@Override
	public void getNextStep(final String cardno) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogFactory.showConfirmMessage(mActivity, "ชำระเงิน", "รหัสสมาชิก: " + id + "\nเลขที่บัตร: " + cardno + "\nจำนวนเงิน " + amount + "บาท", "ยืนยัน", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						completeDialog();
					}
				});
			}
		});
//		AndroidNetworking.post(URL_DEV_TRANSECTION)
//				.addBodyParameter("firstname", "Amit")
//				.setTag("test")
//				.setPriority(Priority.MEDIUM)
//				.build()
//				.getAsJSONObject(new JSONObjectRequestListener() {
//					@Override
//					public void onResponse(JSONObject response) {
//						Log.e("panya", "onResponse : mCarno"+response.toString());
//						// do anything with response
//						Intent data = new Intent();
//						data.putExtra("response", response.toString());
//						setResult(RESULT_OK, data);
//						finish();
//					}
//					@Override
//					public void onError(ANError error) {
//						Log.e("panya", "onError : "+error.getMessage());
//						// handle error
//						Intent data = new Intent();
//						data.putExtra("response", "{'errorCode': '"+error.getErrorCode()+"','msg': '"+error.getMessage()+"'}");
//						setResult(RESULT_OK, data);
//						finish();
//					}
//				});
	}

	@Override
	public void onBackPressed() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.putOpt("status", "100");
			jsonObject.putOpt("msg", "transaction problem");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent data = new Intent();
		data.putExtra("response", jsonObject.toString());
		setResult(RESULT_OK, data);
		finish();
	}
}
