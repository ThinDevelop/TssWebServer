package com.tss.webserver.constant;

public class Constants {
	
	public static final String NextActvity = "NextActvity";
	public static final String LastActvity = "LastActvity";
	
	public static class transCode {
	
		public static final String ini_terminal = "93190";
		public static final String update_key = "93200";
		public static final String update_key_inform = "93210";
		public static final String sign_in = "93010";
		public static final String sign_out = "93020";
		public static final String consume = "92040";
		public static final String scan = "92041";
		public static final String consume_void = "92070";
		public static final String refund = "92090";
		public static final String query_balance = "92030";
		public static final String upload_terminal_status = "93080";
		public static final String download_terminal_para = "93060";
		
		
		public static final String quan_cun = "93081";
		public static final String electronic_cash = "93082";
		public static final String pre_licensing = "93083";
		public static final String pre_licensing_completation = "93084";
		
		
		// 微信支付
		public static final String wechat_consume = "94040";
		public static final String wechat_consume_void = "94030";
		public static final String wechat_consume_refund = "94050";
		//支付宝支付
		public static final String alipay_consume = "95040";
		public static final String alipay_consume_void = "95030";
		public static final String alipay_consume_refund = "95050";
		
		//通联支付
		public static final String tonglian_consume = "97010";
		public static final String tonglian_void = "97020";
		public static final String tonglian_refund = "97040";
		
		//微信卡券
		public static final String coupon_code_search = "99010";
		public static final String coupon_code_hexiao = "99020";
		public static final String coupon_send = "99030";
		
		//银联钱包优惠券
		public static final String unionpay_search = "96010";
		public static final String unionpay_consume = "96020";
		public static final String unionpay_void = "96030";
		public static final String unionpay_refund = "96040";
		public static final String unionpay_coupon_consume_void = "96030";
		public static final String unionpay_coupon_consume_refund = "96040";
	}

	public static class resultCode {
		
		public static final int Success = 0;
		
		public static final int TimeOut = 1;
		
		public static final int Cancel = 2;
		
		public static final int Error = 3;
		
		public static final int PackUnpackError = 4;
	}
	
	public static class transResultCode {
		
		public static final int Success = 0;
		
		public static final int Failed = 1;
		
		public static final int Started = 2;
		
	}
	
	public static class KeyboardEvent {
		public static final String key0 			= "0";
		public static final String key1 			= "1";
		public static final String key2 			= "2";
		public static final String key3 			= "3";
		public static final String key4 			= "4";
		public static final String key5 			= "5";
		public static final String key6 			= "6";
		public static final String key7 			= "7";
		public static final String key8 			= "8";
		public static final String key9 			= "9";
		public static final String keydot 			= ".";
		public static final String keyDel 		= "delete";
		public static final String keyCcl 		= "cancel";
		public static final String keyCfm 		= "confirm";
	}
}
