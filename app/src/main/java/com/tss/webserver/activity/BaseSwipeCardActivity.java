package com.tss.webserver.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.iso8583.util.DataConverter;
import com.centerm.smartpos.aidl.pboc.AidlCheckCardListener;
import com.centerm.smartpos.aidl.pboc.AidlEMVL2;
import com.centerm.smartpos.aidl.pboc.CardInfoData;
import com.centerm.smartpos.aidl.pboc.CardLoadLog;
import com.centerm.smartpos.aidl.pboc.CardTransLog;
import com.centerm.smartpos.aidl.pboc.ParcelableTrackData;
import com.centerm.smartpos.aidl.pinpad.AidlPinPad;
import com.centerm.smartpos.aidl.pinpad.PinPadBuilder;
import com.centerm.smartpos.aidl.rfcard.AidlRFCard;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.EMVTAGS;
import com.tss.webserver.PbocListener;
import com.tss.webserver.R;
import com.tss.webserver.constant.Constants;
import com.tss.webserver.constant.TransFieldConstant;
import com.tss.webserver.constant.VersionControl;
import com.tss.webserver.device.EMVTAGStr;
import com.tss.webserver.device.PbocDev;
import com.tss.webserver.entity.Cache;
import com.tss.webserver.utils.CommonUtil;
import com.tss.webserver.utils.Convert;
import com.tss.webserver.utils.DialogFactory;
import com.tss.webserver.utils.HexUtil;
import com.tss.webserver.utils.LogUtil;
import com.tss.webserver.utils.StringUtil;
import com.tss.webserver.utils.TlvUtil;
import com.tss.webserver.view.IDialogItemClickListener;

import java.util.Map;

import app.TssServerApplication;

public abstract class BaseSwipeCardActivity extends BaseActivity {

	private static final int MSG_TIP = 101;// 显示提示
	private static final int TOAST_TIP = 102;// Toast提示回调
	private static final int PBOC_REPEAT = 104;// 重新开始
	private static final int PBOC_MUL_AID = 105;// 多应用选择
	private static final int PBOC_USE_ECASH = 106;// 是否使用电子现金
	private static final int PBOC_START_READ = 107;// 开始加载IC卡数据

	protected TextView mInputTip, mMoneyTv, mCarnoTv, mCarnoTipTV, mCustomerIdTV;
	// protected MEditText mCarnoEt;

	protected LinearLayout mMoneyLl;

	private String batchno; // 批次号
	private String billno; // 凭证号
	private String systraceno; // POS流水号
	private String inputMethodStr = ""; // 输入条件

	private String mCarno;
	private String mFirstTrack;
	private String mSecondTrack;
	private String mThirdTrack;
	private String mInvalidate;

	// protected AidlPboc mPbocDev;
	// private AidlPinpad mPinpadDev;
	protected AidlEMVL2 mPbocDev;
	private AidlPinPad mPinpadDev;
	private AidlRFCard mRFcardDev;
	// 是否开启检卡
	private boolean mIsChecking = false;
	// 是否开始PBOC
	private boolean mIsStartPBOC = false;
	// 是否支持电子现金
	private boolean mIsECash = false;
	// 多应用选择
	private String[] multAids = null;

	private int startTimes = 1;

	/**
	 * 该变量在电子现金非指定账户圈存中使用， 用来判断是否为转入卡
	 */
	protected boolean isInCard = false;
	private LinearLayout cardPic;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (isCanceled) {
				// logger.debug("页面已取消，不执行");
				return;
			}
			switch (msg.what) {
			case MSG_TIP:
				String tip = (String) msg.obj;
				if (tip.contains(getResources().getString(
						R.string.please_swip_card))) {
					playSound(2, 0);
					// logger.debug("检卡降级，启动刷卡");
					abordPboc();
					// startCheckCart(1);
					DialogFactory.showConfirmMessage(mActivity, getResources()
							.getString(R.string.title_prompt), getResources()
							.getString(R.string.read_card_fail), getResources()
							.getString(R.string.confirm),
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									stopCheckCard();
									startCheckCard(3);
								}
							});
				} else if (tip.contains(getResources().getString(
						R.string.please_ic_card))) {
					playSound(2, 0);
					// logger.debug("检卡IC卡刷卡时，启动检卡");
					startCheckCard(2);
					DialogFactory.showConfirmMessage(mActivity, getResources()
							.getString(R.string.title_prompt), tip,
							getResources().getString(R.string.confirm),
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									stopCheckCard();
									startCheckCard(3);
								}
							});
				}
				// showTip(tip);
				super.handleMessage(msg);
				break;
			case TOAST_TIP:
				String toastTip = (String) msg.obj;
				playSound(2, 0);
				showTip(toastTip);
				break;
			case PBOC_REPEAT:
				startCheckCard(3);
				break;
			case PBOC_START_READ:
				DialogFactory.showLoadingDialog(mActivity,
						getString(R.string.find_iccard),

						getString(R.string.reading_iccard));
				break;
			case PBOC_MUL_AID:
				String title = 2 > multAids.length ? getString(R.string.single_app_selection)
						: getString(R.string.multi_app_selection);
				DialogFactory.dismissAlert(mActivity);
				DialogFactory.showChooseListDialog(mActivity, title, null,
						null, multAids, new IDialogItemClickListener() {
							@Override
							public void onDialogItemClick(View v, String title, int pos) {
								try {
									mPbocDev.importAidSelectRes(pos + 1);
								} catch (Exception e) {
									e.printStackTrace();
								}
								DialogFactory.dismissAlert(mActivity);
								mHandler.sendEmptyMessage(PBOC_START_READ);
							}
						});
				break;
			case PBOC_USE_ECASH:
				DialogFactory.showMessage(mActivity,
						getString(R.string.title_prompt),
						getString(R.string.electronic_cash),
						getString(R.string.use_prompt), new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									mIsECash = true;
									mPbocDev.importECashTipConfirmRes(true);
									DialogFactory.dismissAlert(mActivity);
									mHandler.sendEmptyMessage(PBOC_START_READ);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, getString(R.string.uselese_prompt),
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									mIsECash = false;
									mPbocDev.importECashTipConfirmRes(false);
									DialogFactory.dismissAlert(mActivity);
									mHandler.sendEmptyMessage(PBOC_START_READ);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
				break;
			default:
				break;
			}
		}
	};

	@Override
	public int contentViewSourceID() {
		return R.layout.activity_swipe_card;
	}

	@Override
	public void initView() {
		// setTopTitle("输入卡号");
		Cache.getInstance().setTransType("cardpay");
		Cache.getInstance().setTransCode(Constants.transCode.consume);
		Cache.getInstance().setCardNo("");// 清空卡号
		setTopDefaultReturn();
		cardPic = (LinearLayout) findViewById(R.id.card_pic);
		mMoneyTv = (TextView) findViewById(R.id.input_amt_tips);
		mCarnoTipTV = (TextView) findViewById(R.id.input_cardno_tips);
		mMoneyLl = (LinearLayout) findViewById(R.id.money_ll);
		mCustomerIdTV = (TextView) findViewById(R.id.input_customer_id);
		mCustomerIdTV.setText(getIntent().getStringExtra("id"));
		mMoneyTv.setText(getIntent().getStringExtra("amount"));
//		if (Cache.getInstance().getTransCode()
//				.equals(Constants.transCode.consume_void)) {
//			Cache.getInstance().setTransMoney("0.01");
//			mMoneyTv.setText(Cache.getInstance().getTransMoney());
//		} else {
//			mMoneyTv.setText(Cache.getInstance().getTransMoney());
//		}

		initViewData();
	}

	@Override
	public void onServiceConnecteSuccess(AidlDeviceManager manager) {
		initPobcDev();
		// abordPboc();
		startCheckCard(3);
	}

	public void initPobcDev() {
		if (null == mPbocDev) {
			try {
				// mPbocDev =
				// AidlPboc.Stub.asInterface(mDeviceService.getEMVL2());
				mPbocDev = AidlEMVL2.Stub.asInterface(mDeviceManager
						.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null == mPinpadDev) {
			try {
				// mPinpadDev =
				// AidlPinpad.Stub.asInterface(mDeviceService.getPinPad(0));
				mPinpadDev = AidlPinPad.Stub.asInterface(mDeviceManager
						.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PINPAD));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(null == 	mRFcardDev){
			try {
				mRFcardDev = AidlRFCard.Stub.asInterface(mDeviceManager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_RFCARD));
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}
	}

	private void abordPboc() {
		try {
			mPbocDev.abortPBOC();
			// Thread.sleep(100);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		TssServerApplication.Companion.getMPbocListener().clearListener();
		mIsStartPBOC = false;
	}

	public void endPboc() {
		if (!mIsStartPBOC) {
			return;
		}
		DialogFactory.dismissAlert(mActivity);
		// 结束PBOC流程
		try {
			mPbocDev.cancelCheckCard();
			mPbocDev.endPBOC();
		} catch (RemoteException e) {
			// logger.error("停止PBOC异常", e);
			e.printStackTrace();
		} catch (Exception e) {
			// logger.error("停止PBOC异常", e);
			e.printStackTrace();
		}
		// 清空回调
		TssServerApplication.Companion.getMPbocListener().clearListener();
		mIsStartPBOC = false;
	}

	/**
	 * 进入到下一步
	 * 
	 * @param inputMode
	 *            输入方式 </p> 011 手工方式，且带PIN </p> 012 手工方式，且不带PIN </p> 021
	 *            磁条读入，且带PIN </p> 022 磁条读入, 且不带PIN </p> 031 磁条读入, 微信被扫带PIN </p>
	 *            032 磁条读入, 微信被扫不带PIN </p> 05x IC卡输入, 且磁条信息可靠 </p> 80x
	 *            Fallback磁条卡 </p> 07x qPBOC快速支付 </p> 91x 非接触式磁条读入(CUPS MSD)
	 *            </p> 98x 标准PBOC借/贷记IC卡读入(非接触式) </p>
	 * 
	 * @return
	 */
	private boolean nextStep(String inputMode) {
		Cache.getInstance().setSerInputCode(inputMode);
		if (!saveValue()) {
			return false;
		}
		Intent it = null;
		// 手输卡号，需要输入卡有效期
		if (inputMode.startsWith("01")) {
			// it = new Intent(mContext, InputCardValidityActivity.class);
			// it.putExtra(TransStartActivity.TRANS_NEXT_ACTIVITY_TAG,
			// getNextStep().getComponent().getClassName());
		} else {
			getNextStep(mCarno);
		}
		Log.e("huang", "next");
//		goToNextActivity(it);
		// 非接300元以下 直接关闭这里的对话框 不结束页面 在当前页面发起交易
//		if (inputMode.equals(TransFieldConstant.INPUT_TYPE_QPBOC_NO_PIN)) {
//			DialogFactory.dismissAlert(mActivity);
//		} else {
//			finish();
//		}
		return true;
	}

	/**
	 * 启动检卡1：支持磁条卡 2：支持IC卡 3：同时支持磁条与IC卡
	 */
	private boolean startCheckCard(int checkType) {
		boolean result = false;
		try {
			Log.i("huang", "startCheckCard");
				switch (checkType) {
				case 3:// 仅磁条
					cardPic.setBackgroundResource(R.drawable.swipe_pic);
					mPbocDev.checkCard(true, false, false, 60000,
							mCheckCardListener);
					break;
				case 2:// 仅IC
					cardPic.setBackgroundResource(R.drawable.icard_pic);
					mPbocDev.checkCard(false, true, false, 60000,
							mCheckCardListener);
					break;
				case 13:
					mPbocDev.checkCard(true, true, true, 60000,
							mCheckCardListener);
//					mPbocDev.checkCard(true, true, true, 60000, mCheckCardListener); // allow 3 method
					break;
				default:
					mPbocDev.checkCard(true, true, true, 60000,
							mCheckCardListener);
					break;
				}
			result = true;
			mIsChecking = true;
		} catch (Exception e) {
			bindService();
			e.printStackTrace();
			mIsChecking = false;
			result = false;
		}
		return result;
	}

	public AidlCheckCardListener mCheckCardListener = new AidlCheckCardListener.Stub() {

		@Override
		public void onTimeout() throws RemoteException {
			// 检卡超时
			try {
				Log.e("huang", "onTimeout   ,执行 cancelCheckCard");
				mPbocDev.cancelCheckCard();
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onSwipeCardFail() throws RemoteException {
			// AVOSUtil.getInstance().createEvent003(mContext, false,
			// "SwipeCardFail", "02");
			// 刷卡失败
			// logger.debug("PBOC检卡失败");
			Log.e("huang", "PBOC   onSwipeCardFail, cancelCheckCard");
			playSound(2, 0);
			mIsChecking = false;
			showToastTip(getResources().getString(R.string.swope_fail_recheck));
			// 重新开始检卡
			mPbocDev.cancelCheckCard();
			mHandler.sendEmptyMessageDelayed(PBOC_REPEAT, 500l);
		}

		@Override
		public void onFindRFCard() throws RemoteException {
			// AVOSUtil.getInstance().createEvent003(mContext, true, null,
			// "07");
			// 检索到非接卡
			// logger.debug("PBOC检索到非接卡");
			Log.d("huang", "PBOC   onFindRFCard");
			mIsChecking = false;
			// logger.debug(" 检索到非接卡,交易代码" +
			// Cache.getInstance().getTransCode());
			mHandler.sendEmptyMessage(PBOC_START_READ);
			mIsChecking = false;
			// 启动PBOC流程
			new Thread() {

				@Override
				public void run() {
					try {
//						 // 通过300元来判断输入条件 低于300不需要pin 072 反之071
//						 double inputMoney = 0.00;
//						 if (Cache.getInstance().getTransMoney() != null) {
//						 inputMoney =
//						 Double.valueOf(Cache.getInstance().getTransMoney());
//						 }
//						 // String flag =
//						 //
//						 ParamsUtil.getInstance(getApplicationContext()).getParam("neednopin");
//						 String flag = "0";
//						 String noneedmoney = "300";
//						 // String noneedmoney =
//						 //
//						 ParamsUtil.getInstance(getApplicationContext()).getParam("nopinamount");
//						 if (inputMoney < Double.valueOf(noneedmoney)) {
//						 // inputMethodStr =
//						 TransFieldConstant.INPUT_TYPE_QPBOC_NO_PIN;
//						 } else {
//						 inputMethodStr =
//						 TransFieldConstant.INPUT_TYPE_QPBOC_PIN;
//						 runOnUiThread(new Runnable() {
//						 @Override
//						 public void run() {
//						  mCarnoEt.setText(arg0.getCardno());
//						 // mCarnoEt.refreshDrawableState();
//						 // mCarnoTv.setText(arg0.getCardno());
//						 // 默认磁卡输入有pin，到输密界面做调整
//						 nextStep(TransFieldConstant.INPUT_TYPE_FALLBACK_PIN);
//						 }
//						 });
//						 playSuccessVoic();
//						 return;
//						 }
						TssServerApplication.Companion.getMPbocListener().setPbocProcessListener(mPbocProcessListener);
//						 String transCode =
//						 Cache.getInstance().getTransCode();
						mIsStartPBOC = true;
						// AVOSUtil.getInstance().createEventCommon(mContext,
						// "E004");
						// logger.debug("检卡成功启动PBOC流程");
						mPbocDev.processPBOC(PbocDev.getPbocEmvTransData2(Cache
								.getInstance().getTransCode()),
								TssServerApplication.Companion.getMPbocListener());
					} catch (Exception e) {
						e.printStackTrace();
					}
					super.run();
				}

			}.start();
		}

		@Override
		public void onFindMagCard(ParcelableTrackData arg0)
				throws RemoteException {
			// AVOSUtil.getInstance().createEvent003(mContext, true, null,
			// "02");
			// logger.info("检卡检测到磁条卡");
			// 检索到磁条卡,如果是电子现金普通消费，磁条卡是不能用的
			Log.e("huang", "PBOC   onFindMagCard");
			if (Cache.getInstance().getTransCode() == TransFieldConstant.TRANS_CODE_DZXJ_TRANS_PUTONG_CONSUMER
					|| Cache.getInstance().getTransCode() == TransFieldConstant.TRANS_CODE_OFF_TH) {
				return;
			}
			mIsChecking = false;
			LogUtil.debug("<--------刷卡成功-------->");
			LogUtil.debug("CardNo:" + arg0.getCardNo());
			LogUtil.debug("ExpireDate：" + arg0.getExpireDate());
			LogUtil.debug("FirstTrackData" + arg0.getFirstTrackData());
			LogUtil.debug("getSecondTrackData" + arg0.getSecondTrackData());
			LogUtil.debug("getThirdTrackData" + arg0.getThirdTrackData());
			LogUtil.debug("<----------------------->");
			// mCarno = arg0.getCardno();
			// mInvalidate = arg0.getExpiryDate();
			// mFirstTrack = arg0.getFirstTrackData();
			// mSecondTrack = arg0.getSecondTrackData();
			// mThirdTrack = arg0.getThirdTrackData();
			mCarno = arg0.getCardNo();
			mInvalidate = arg0.getExpireDate();
			mFirstTrack = HexUtil.bytesToHexString(arg0.getFirstTrackData());
			mSecondTrack = HexUtil.bytesToHexString(arg0.getSecondTrackData());
			mThirdTrack = HexUtil.bytesToHexString(arg0.getThirdTrackData());
			if (StringUtil.isEmpty(mCarno) || mCarno.length() < 12
					|| mCarno.length() > 19) {
				showToastTip(getResources().getString(R.string.invalid_card_no));// 无效卡号
				mHandler.sendEmptyMessage(PBOC_REPEAT);
				return;
			}
			if (!StringUtil.isEmpty(mSecondTrack)) {
				mSecondTrack = mSecondTrack.replaceAll("[:;<>=]", "D"); // 替换二磁道信息中出现的特殊符号
			}
			if (!StringUtil.isEmpty(mThirdTrack)) {
				mThirdTrack = mThirdTrack.replaceAll("[:;<>=]", "D"); // 替换三磁道信息中出现的特殊符号
			}
			Cache.getInstance().setCardNo(mCarno);
			Cache.getInstance().setTrack_2_Data(mSecondTrack);
			Cache.getInstance().setTrack_3_Data(mThirdTrack);
			Cache.getInstance().setInvalidDate(mInvalidate);
			// 校验是否为fallback，false按普通磁条卡上送
			try {
				String posInputType = Cache.getInstance().getSerInputCode();
				if (TransFieldConstant.INPUT_TYPE_FALLBACK_PIN
						.equals(posInputType)) {
					// logger.debug("IC卡降级处理");
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// mCarnoEt.setText(arg0.getCardno());
							// mCarnoEt.refreshDrawableState();
							// mCarnoTv.setText(arg0.getCardno());
							// 默认磁卡输入有pin，到输密界面做调整
							nextStep(TransFieldConstant.INPUT_TYPE_FALLBACK_PIN);
						}
					});
					playSuccessVoic();
					return;
				} else {
					// 判断是否先刷IC卡（非fallback，刷卡）
					if (mSecondTrack != null && mSecondTrack.contains("D")) {
						String serviceCode = mSecondTrack.substring(
								mSecondTrack.indexOf("D") + 5,
								mSecondTrack.indexOf("D") + 6);
						// 如果是IC卡提示请插卡
						if ("2".equals(serviceCode) || "6".equals(serviceCode)) { // 先刷IC卡
							// 需要提示请插卡
							showResultTip(getString(R.string.please_ic_card));
							// logger.debug("该卡为IC卡，请插卡");
							return;
						}
					}
					Cache.getInstance().setSerInputCode(
							TransFieldConstant.INPUT_TYPE_MAG_PIN);
				}
			} catch (Exception e) {
				Cache.getInstance().setSerInputCode(
						TransFieldConstant.INPUT_TYPE_MAG_PIN);
				e.printStackTrace();
			}
			playSuccessVoic();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					 mCarnoTv.setText(arg0.getCardno());
					// 默认磁卡输入有pin，到输密界面做调整
					nextStep(TransFieldConstant.INPUT_TYPE_MAG_PIN);
				}
			});
		}

		@Override
		public void onFindICCard() throws RemoteException {
			// AVOSUtil.getInstance().createEvent003(mContext, true, null,
			// "05");
			Log.e("huang", "PBOC   onFindICCard");
			inputMethodStr = TransFieldConstant.INPUT_TYPE_IC_PIN;
			// logger.debug(" 检索到IC卡,交易代码" +
			// Cache.getInstance().getTransCode());
			mHandler.sendEmptyMessage(PBOC_START_READ);
			mIsChecking = false;
			// 启动PBOC流程
			new Thread() {
				@Override
				public void run() {
					try {
						 TssServerApplication.Companion.getMPbocListener()
								.setPbocProcessListener(mPbocProcessListener);
						mIsStartPBOC = true;
						mPbocDev.processPBOC(PbocDev.getPbocEmvTransData(Cache
										.getInstance().getTransCode()),
								TssServerApplication.Companion.getMPbocListener());
					} catch (Exception e) {
						// logger.error("启动PBOC流程失败，PBOC对象获取异常", e);
						e.printStackTrace();
					}
					super.run();
				}
			}.start();
		}

		@Override
		public void onError(int arg0) throws RemoteException {
			Log.e("huang", "PBOC检卡异常" + arg0);
			switch (arg0) {
			// IC卡刷卡会返回13
			case 13:
				// AVOSUtil.getInstance().createEvent003(mContext, false,
				// "ICSwipe", "02");
				showResultTip(getResources().getString(R.string.please_ic_card));
				break;
			default:
				showToastTip(getResources().getString(
						R.string.card_fail_recheck));
				// logger.debug("刷/插卡失败请重新刷/插卡");
				abordPboc();
				mHandler.sendEmptyMessage(PBOC_REPEAT);
				break;
			}

		}

		@Override
		public void onCanceled() throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("huang", "onCanceled");
		}
	};

	public PbocListener.PbocProcessListener mPbocProcessListener = new PbocListener.PbocProcessListener() {

		@Override
		public void requestUserAuth(int paramInt, String paramString)
				throws RemoteException {
			System.out.println("错误" + String.valueOf(paramInt));
			// /** 请求身份认证 */
			// logger.debug("PBOC请求身份认证 arg0=" + arg0 + ";arg1=" + arg1);
		}

		@Override
		public void requestTipsConfirm(String paramString)
				throws RemoteException {
			/**
			 * 请求提示信息， 提示信息格式为 16 进制字符串， 格式为 显示标志+显示超时时间+显示标题长度+显
			 * 示标题内容+显示内容长度+显示内容； 显示标志： 1byte，表示是否需要持卡人确 认； 0x00：不需要确认；
			 * 0x01：需要确认； 显示超时时间： 1byte，单位 s； 显示标题长度： 1byte，若为0，标题内容不 存在； 标题内容：
			 * ASC 码，若“显示标题长度”为 0，则该字段不存在； 显示内容长度： 1byte，若为0，若“显示内
			 * 容长度”为0，则该字段不存在； 显示内容： ASC 码
			 */
			// logger.debug("PBOC请求提示信息" + arg0);
			Log.d("huang", "mPbocProcessListener   requestTipsConfirm");
			try {
				mPbocDev.importMsgConfirmRes(true);
			} catch (Exception e) {
				// logger.error("请求信息确认失败", e);
				e.printStackTrace();
				abordPboc();
			}
		}

		@Override
		public void requestImportPin(int paramInt, boolean paramBoolean,
				String paramString) throws RemoteException {
			Log.d("huang", "mPbocProcessListener   requestImportPin");
			// /** 请求导入 PIN */
			// logger.debug("PBOC请求导入 PIN arg0" + arg0 + ";arg1:" + arg1 +
			// "arg2:" + arg2);
			try {
				mPbocDev.importPin("26888888FFFFFFFF");
			} catch (Exception e) {
				// logger.error("联机交易时导入PIN失败");
				e.printStackTrace();
			}
		}

		@Override
		public void requestImportAmount(int paramInt) throws RemoteException {
			/** 请求输入金额 */
			Log.d("huang", "mPbocProcessListener   requestImportAmount");
			// logger.debug("PBOC请求输入金额:" + arg0);
			/*
			 * 金额类别（ 1byte），取值说明： 0x01：只要授权金额； 0x02：只要返现金额； 0x03：既要授权金额，也要返现金额；
			 */
			switch (paramInt) {
			case 0x01:
				if (!StringUtil.isEmpty(Cache.getInstance().getTransMoney())) {
					// 如果缓存中不存在金额，说明需要输入金额，所以进入下一页（此处默认下一页会是输入金额）
					// 如果缓存中存在金额，则说明在刷卡前已输入金额，则直接传入金额
					try {
						if (Cache.getInstance().getTransMoney()
								.equals("9999999999.99")) {
							mPbocDev.importAmount("9999999999.98");
						} else {
							mPbocDev.importAmount(Cache.getInstance()
									.getTransMoney());
						}
					} catch (Exception e) {
						// logger.error("金额导入失败，获取PBOC实例异常", e);
						e.printStackTrace();
					}
				} else {
					try {
						mPbocDev.importAmount("0.01");
					} catch (Exception e) {
						// logger.error("金额导入失败，获取PBOC实例异常", e);
						e.printStackTrace();
					}
				}
				break;
			case 0x02:
				break;
			case 0x03:
				if (!StringUtil.isEmpty(Cache.getInstance().getTransMoney())) {
					// logger.debug("请求输入金额，既要授权又要反现金额");
					// 如果缓存中存在金额，则说明在刷卡前已输入金额，则直接传入金额
					try {
						mPbocDev.importAmount(Cache.getInstance()
								.getTransMoney());
					} catch (Exception e) {
						// logger.error("金额导入失败，获取PBOC实例异常", e);
						e.printStackTrace();
					}
				}
				break;
			}
		}

		@Override
		public void requestEcashTipsConfirm() throws RemoteException {
			Log.d("huang", "mPbocProcessListener   requestEcashTipsConfirm");
			// /**请求确认是否使用电子现金*/
			if (Cache.getInstance().getTransCode()
					.equals(TransFieldConstant.TRANS_CODE_QUERY_BALANCE)
					|| Cache.getInstance().getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_CONSUME_CX)
					|| Cache.getInstance().getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_LJTH)
					|| Cache.getInstance().getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_PRE)
					|| Cache.getInstance().getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_PRE_COMPLET)
					|| Cache.getInstance()
							.getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_PRE_COMPLET_CX)
					|| Cache.getInstance().getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_PRE_CX)
					|| Cache.getInstance().getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_CONSUME)) {// 消费暂时不使用电子现金
				try {
					mPbocDev.importECashTipConfirmRes(false);
				} catch (Exception e) {
					// logger.error("请求确认是否使用电子现金失败");
					e.printStackTrace();
				}
				return;
			}
			// logger.debug("PBOC请求确认是否使用电子现金");
			if (Cache.getInstance().getTransCode()
					.equals(TransFieldConstant.TRANS_CODE_QC_ZD)) {
				try {
					mPbocDev.importECashTipConfirmRes(true);
				} catch (Exception e) {
					// logger.error("请求确认是否使用电子现金失败");
					e.printStackTrace();
				}
				return;
			}
			mHandler.sendEmptyMessage(PBOC_USE_ECASH);
		}

		@Override
		public void requestAidSelect(int paramInt, String[] paramArrayOfString)
				throws RemoteException {
			Log.d("huang", "mPbocProcessListener   requestAidSelect");
			// /**请求多应用选择*/
			// logger.debug("PBOC请求多应用选择arg0="+arg0+"===="+arg1);
			multAids = paramArrayOfString;
			mHandler.sendEmptyMessage(PBOC_MUL_AID);
		}

		@Override
		public void onTransResult(int paramInt) throws RemoteException {
			Log.d("huang", "mPbocProcessListener   onTransResult");
			if (!mIsStartPBOC) {
				// 避免出现未启动PBOC时出错
				return;
			}
			Log.d("huang", "PBOC交易结果：" + paramInt);
			// 批准: 0x01 拒绝: 0x02 终止: 0x03
			// FALLBACK: 0x04 采用其他界面: 0x05 其他： 0x06
			// Intent it;
			// logger.debug("PBOC交易结果：" + arg0);
			if (paramInt != 0x01) {

					DialogFactory.dismissAlert(mActivity);


				// AVOSUtil.getInstance().createEvent014(mContext, arg0 +""
				// ,Cache.getInstance().getTransCode(),Cache.getInstance().getThreeAppid()
				// ,Cache.getInstance().getThreeMerid() +
				// Cache.getInstance().getThreeTermid());
			}
			switch (paramInt) {
			case 0x02:
				// it = new Intent(mContext, TransErrorResultActivity.class);
				// Cache.getInstance().setErrCode(LklException.ERR_DEV_TRANS_REFUSE_E307);
				// Cache.getInstance().setErrDesc(LklException.getMsg(LklException.ERR_DEV_TRANS_REFUSE_E307));
				// startActivity(it);
				endPboc();
				startCheckCard(3);// huang
				break;
			case 0x04:
				showResultTip(getResources().getString(
						R.string.please_swip_card));
				// Cache.getInstance().setSerInputCode("801");
				// endPboc();
				// finish();
				// endPboc();
				// startCheckCard(3);//huang
				break;
			case 0x05:
				// it = new Intent(mContext, TransErrorResultActivity.class);
				// Cache.getInstance().setErrCode(LklException.ERR_DEV_TRANS_OTHERPAGE_E324);
				// Cache.getInstance().setErrDesc(LklException.getMsg(LklException.ERR_DEV_TRANS_OTHERPAGE_E324));
				// startActivity(it);
				// if (null == Cache.getInstance().getCardNo()) {
				// Cache.getInstance().setCardNo("6222020011012883865");
				// }
				// goToNextActivity(getNextStep());
				// abordPboc();
				// endPboc();
				// showResultTip(getString(R.string.emv_read_err));
				// abordPboc();
				playSound(2, 0);
				//获取一下卡号啊啊啊啊啊啊
				mRFcardDev.open();
				mRFcardDev.reset();
				byte[] result = mRFcardDev.send(com.centerm.smartpos.util.HexUtil
						.hexStringToByte("00B300003F"));
				String emoneyCardNo = com.centerm.smartpos.util.HexUtil.bytesToHexString(result);
				if(VersionControl.getRecentVersion()==VersionControl.SPECIAL_VERSION_OF_FIVE_ONE){
					//这里为E-money 卡专门进行了一个结果进行处理
					endPboc();
//					Intent intentToSign = new Intent(mContext,SignActivity.class);
//					intentToSign.putExtra("emoneyCardNo",emoneyCardNo.substring(0,16));
					System.out.println("emoneyCard:"+emoneyCardNo);
//					startActivity(intentToSign);
					Toast.makeText(getBaseContext(), "emoneyCard:"+emoneyCardNo, Toast.LENGTH_LONG).show();

				}else{
					showResultTip(getResources().getString(
							R.string.trans_result_fail));
					finish();
				}


				// startCheckCard(3);//huang
				break;
			case 0x06:
			case 0x03:
				playSound(2, 0);
				showResultTip(getString(R.string.trans_result_fail));
				// 交易终止，返回首页
				// LklPaymentActivityManager.getActivityManager().removeAllActivityExceptOne(MainMenuActivity.class);
				// it = new Intent(mContext, TransErrorResultActivity.class);
				// Cache.getInstance().setErrCode(LklException.ERR_DEV_READ_CARD_E302);
				// Cache.getInstance().setErrDesc(LklException.getMsg(LklException.ERR_DEV_READ_CARD_E302));
				// startActivity(it);
				// endPboc();
				abordPboc();
				startCheckCard(3);// huang
				break;
			case 0x01:
				// showResultTip("交易成功");
				// showTip("交易成功");
				// 消费时使用电子现金
				if (mIsECash
						&& Cache.getInstance().getTransCode()
								.equals(TransFieldConstant.TRANS_CODE_CONSUME)) {
					Cache.getInstance()
							.setTransCode(
									TransFieldConstant.TRANS_CODE_DZXJ_TRANS_PUTONG_CONSUMER);
				}
				if (Cache
						.getInstance()
						.getTransCode()
						.equals(TransFieldConstant.TRANS_CODE_DZXJ_TRANS_PUTONG_CONSUMER)) {
					// Intent intent = new Intent(mContext,
					// DianZiXianJingConsumeResultActivity.class);
					// intent.putExtra("cardno", mCarno);
					// intent.putExtra("paymoney",
					// Cache.getInstance().getTransMoney());
					getTrans2Data();
					getICData();
					// saveDataToDB();
					// goToNextActivity(intent);
				}
			case 7:
				// playSound(2, 0);
				// showResultTip(getString(R.string.pboc_abort));
				// abordPboc();
				// startCheckCard(3);//huang
				// 重新开始检卡
				// mHandler.sendEmptyMessage(PBOC_REPEAT);
				break;
			default:
				// it = new Intent(mContext, TransErrorResultActivity.class);
				// //读卡失败，交易终止
				// Cache.getInstance().setErrCode(LklException.ERR_DEV_READ_CARD_E302);
				// Cache.getInstance().setErrDesc(LklException.getMsg(LklException.ERR_DEV_READ_CARD_E302));
				// startActivity(it);
				break;
			}
		}

		@Override
		public void onRequestOnline() throws RemoteException {
			Toast.makeText(getBaseContext(), "This comming soon ", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onReadCardTransLog(CardTransLog[] paramArrayOfPCardTransLog)
				throws RemoteException {
			Log.e("huang", "mPbocProcessListener   onReadCardTransLog");
			System.out.println("错误" + paramArrayOfPCardTransLog);
			// 返回读取卡片交易日志结果
			// logger.debug("PBOC返回读取卡片交易日志结果");
		}

		@Override
		public void onReadCardOffLineBalance(String paramString1,
                                             String paramString2, String paramString3, String paramString4)
				throws RemoteException {
			Log.e("huang", "mPbocProcessListener   onReadCardOffLineBalance");
			System.out.println("错误" + paramString1);
			// 返回读取卡片多级余额结果
			// logger.debug("PBOC返回读取卡片多级余额结果");
		}

		@Override
		public void onReadCardLoadLog(String paramString1, String paramString2,
                                      CardLoadLog[] paramArrayOfPCardLoadLog) throws RemoteException {
			Log.d("huang", "mPbocProcessListener   onReadCardLoadLog");
			System.out.println("错误" + paramString1);
		}

		@Override
		public void onError(int paramInt) throws RemoteException {
			Log.e("huang", "mPbocProcessListener   onError");
			System.out.println("错误" + String.valueOf(paramInt));
			finish();
			// logger.error("PBOC交易失败onError：" + arg0);
			// Intent it = new Intent(mContext, TransErrorResultActivity.class);
			// Cache.getInstance().setErrCode(LklException.ERR_DEV_READ_CARD_E301);
			// Cache.getInstance().setErrDesc(LklException.getMsg(LklException.ERR_DEV_READ_CARD_E301));
			// startActivity(it);
		}

		@Override
		public void onConfirmCardInfo(CardInfoData paramCardInfo)
				throws RemoteException {
			Log.d("huang", "mPbocProcessListener   onConfirmCardInfo");
			if (mIsECash) {
				// logger.debug("卡信息确认使用电子现金");
				try {
					mPbocDev.importConfirmCardInfoRes(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			// 清空回调,确认卡信息后不再处理回调结果
			TssServerApplication.Companion.getMPbocListener().clearListener();
			// 请求确认卡信息
			mCarno = paramCardInfo.getCardno();
			mCarno = mCarno.replace("F", "");
			Cache.getInstance().setCardNo(mCarno);// 设置卡号

			if (StringUtil.isEmpty(mCarno) || mCarno.length() < 12
					|| mCarno.length() > 19) {
				abordPboc();
				showToastTip(getResources().getString(R.string.invalid_card_no));// 无效卡号
				mHandler.sendEmptyMessage(PBOC_REPEAT);
				return;
			}
			// logger.debug("PBOC确认卡信息");
			if (Cache.getInstance().getTransCode()
					.equals(TransFieldConstant.TRANS_CODE_OFF_TH)) {
				// 电子现金脱机退货，这里直接结束
				getNextStep(mCarno);
				return;
			}
			if (Cache
					.getInstance()
					.getTransCode()
					.equals(TransFieldConstant.TRANS_CODE_DZXJ_TRANS_PUTONG_CONSUMER)) {
				try {
					if (Cache
							.getInstance()
							.getTransCode()
							.equals(TransFieldConstant.TRANS_CODE_DZXJ_TRANS_PUTONG_CONSUMER)) {
						try {
							byte[] results = new byte[1024];
							int count = 0;
							// count =
							// mPbocDev.readKernelData(PbocDev.getKernalTag(Cache.getInstance().getTransCode()),
							// results);
							count = mPbocDev.readKernelData(
									EMVTAGS.getF55Taglist(), results);
							byte[] datas2 = new byte[count];
							System.arraycopy(results, 0, datas2, 0, count);
							String f55Data = HexUtil.bcd2str(datas2);
							// 设置55域
							Cache.getInstance().setTlvTag55(f55Data);
							// logger.debug("读取内核数据结果：" +
							// DataConverter.bytesToHexString(results) + "长度：" +
							// count);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
			playSuccessVoic();
			if (isInCard) {// 当此处插卡的是转入卡,直接开始交易，因为转出卡仅需要55域数据，数据在交易时获取
				// logger.debug("PBOC非指定账户圈存，转入卡：" + mCarno);
				mInvalidate = Cache.getInstance().getInvalidDate();
				mFirstTrack = null;
				mSecondTrack = Cache.getInstance().getTrack_2_Data();
				mThirdTrack = null;
				// 附加数据为转入卡号
				Cache.getInstance().setAddDataWord(mCarno);
				getNextStep(mCarno);
				return;
			}
			// 不读取磁道2，到交易时读取
			// getTrans2Data();
			nextStep(inputMethodStr);
		}
	};

	@Override
	public void onServiceBindFaild() {
		// TODO Auto-generated method stub

	}

	public void stopCheckCard() {
		// logger.debug("取消检卡");
		try {
			mPbocDev.cancelCheckCard();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mIsChecking = false;
	}

	@Override
	public boolean saveValue() {
		// encryptTrackData();
		return true;
	}

	private void encryptTrackData() {
		String track2 = Cache.getInstance().getTrack_2_Data();
		String track2Length = StringUtil.leftAddZeroForNum(
				Integer.toHexString(track2.length() / 2).toUpperCase(), 2);
		track2 = track2Length + track2;
		if ((track2.length() / 2) % 8 != 0) {
			while ((track2.length() / 2) % 8 != 0) {
				track2 = track2 + "00";
			}
		}
		byte[] enc_track2 = null;
		try {
			// mPinpadDev.encryptByTdk(0, (byte) 0, null,
			// DataConverter.hexStringToByte(track2), enc_track2);
			enc_track2 = mPinpadDev.enCryptData(
					PinPadBuilder.DATAENCRYPT_MODE.DEFAULT, (byte) 0, null,
					Convert.hexStringToBytes(track2));
			Cache.getInstance().setTrack_2_Data(
					DataConverter.bcd2Str(enc_track2));
		} catch (RemoteException e) {
			LogUtil.error("加密二磁道数据出错");
			e.printStackTrace();
		}
		String track3 = Cache.getInstance().getTrack_3_Data();
		String track3Length = StringUtil.leftAddZeroForNum(
				Integer.toHexString(track3.length() / 2).toUpperCase(), 4);
		track3 = track3Length + track3;
		if ((track3.length() / 2) % 8 != 0) {
			while ((track3.length() / 2) % 8 != 0) {
				track3 = track3 + "00";
			}
		}
		byte[] enc_track3 = null;
		try {
			// mPinpadDev.encryptByTdk(0, (byte) 0, null,
			// DataConverter.hexStringToByte(track3), enc_track3);
			enc_track3 = mPinpadDev.enCryptData(
					PinPadBuilder.DATAENCRYPT_MODE.DEFAULT, (byte) 0, null,
					Convert.hexStringToBytes(track3));
			Cache.getInstance().setTrack_3_Data(
					DataConverter.bcd2Str(enc_track3));
		} catch (RemoteException e) {
			LogUtil.error("加密三磁道数据出错");
			e.printStackTrace();
		}
	}

	protected void showTransMoney(boolean isShow) {
		if (isShow) {
			mMoneyLl.setVisibility(View.VISIBLE);
		} else {
			mMoneyLl.setVisibility(View.GONE);
		}
	}

	protected void showInputCarno(boolean isShow) {
	}

	@Override
	protected void onDestroy() {
		android.util.Log.i("huang", "BaseSwipe destroy");
		super.onDestroy();
		stopCheckCard();
		DialogFactory.dismissAlert(mActivity);
//		TinyWebServer.stopServer();
	}

	public void getTrans2Data() {
		try {
			android.util.Log.i("huang", "getTrans2Data()");
			String[] tags = { EMVTAGStr.EMVTAG_APP_PAN,
					EMVTAGStr.EMVTAG_TRACK2, EMVTAGStr.EMVTAG_APP_PAN_SN };
			byte[] track_2_byte = new byte[512];
			int trac_count;
			// trac_count = mPbocDev.readKernelData(tags, track_2_byte);
			trac_count = mPbocDev.readKernelData(EMVTAGS.EMVTAG_TRACK2,
					track_2_byte);
			android.util.Log.i("huang", "readKernelData  ok");
			// logger.info("PBOC再次读取内核获取磁道：读取长度" + trac_count);
			if (trac_count > 0) {
				byte[] trackResult = new byte[trac_count];
				System.arraycopy(track_2_byte, 0, trackResult, 0, trac_count);
				// logger.debug("磁道等数据：" +
				// DataConverter.bytesToHexString(trackResult));
				Map<String, String> resMap = TlvUtil.tlvToMap(HexUtil
						.bcd2str(trackResult));
				if (resMap.get("5A") == null) {// 如果5A域未读取到，则从磁道数据中获取
					String track2data = resMap.get("57");
					String card = track2data.split("D")[0];
					resMap.put("5A", card);
				}
				Cache.getInstance()
						.setCardNo(resMap.get("5A").replace("F", ""));
				// logger.debug(resMap.get("5A").replace("F", "") + " " +
				// resMap.get("57") + " " + resMap.get("5F34"));
				// logger.debug("磁道2数据：" + resMap.get("57").replace("F", ""));
				String mSecondTrack = resMap.get("57").replace("F", "");
				mSecondTrack.replaceAll("[:;<>=]", "D");
				String invalidate = null;
				if (!StringUtil.isEmpty(mSecondTrack)
						&& mSecondTrack.contains("D")) {
					int pos = mSecondTrack.indexOf("D");
					invalidate = mSecondTrack.substring(pos + 1, pos + 5);
					if (StringUtil.isEmpty(invalidate)) {
						invalidate = "0000";
					}
					// logger.debug("IC卡有效期=" + invalidate);
					Cache.getInstance().setInvalidDate(invalidate);
				}
				// logger.info("qqq 卡片序列号：" + resMap.get("5F34"));
				Cache.getInstance().setCardSeqNo(resMap.get("5F34"));
				mInvalidate = invalidate;
				mFirstTrack = null;
				this.mSecondTrack = mSecondTrack;
				Cache.getInstance().setTrack_2_Data(mSecondTrack);
				mThirdTrack = null;
			}
		} catch (RemoteException e) {
			// logger.error("读取IC卡数据失败", e);
			android.util.Log.i("huang", "读取IC卡数据失败 RemoteException" + e);
			e.printStackTrace();
		} catch (Exception e) {
			// logger.error("读取IC卡数据失败", e);
			android.util.Log.i("huang", "读取IC卡数据失败" + e);
			e.printStackTrace();
		}
	}

	public void getICData() {
		byte[] resultTemp = new byte[1024];
		try {
			// int count = PbocDev.getInstance(mContext,
			// mDeviceManager).getOriginalDev().readKernelData(EMVTAGStr.getLakalaF55UseModeOne(),
			// resultTemp);
			int count = PbocDev.getInstance(mContext, mDeviceManager)
					.getOriginalDev()
					.readKernelData(EMVTAGS.getF55UseModeOneTags(), resultTemp);
			// logger.debug("读取内核55域长度："+count);
			if (count <= 0) {
				return;
			}
			byte[] icData = new byte[count];
			System.arraycopy(resultTemp, 0, icData, 0, count);
			// logger.debug("55域数据："+HexUtil.bcd2Str(icData));
			Cache.getInstance().setTlvTag55(HexUtil.bcd2str(icData));
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getTraceAndArqc() {
		String[] tags = { EMVTAGStr.EMVTAG_APP_PAN, EMVTAGStr.EMVTAG_TRACK2,
				EMVTAGStr.EMVTAG_APP_PAN_SN };
		byte[] track_2_byte = new byte[512];
		int trac_count = 0;
		try {
			// trac_count = PbocDev.getInstance(mContext,
			// mDeviceManager).getOriginalDev().readKernelData(tags,
			// track_2_byte);
			trac_count = mPbocDev.readKernelData(EMVTAGS.EMVTAG_TRACK2,
					track_2_byte);
		} catch (RemoteException e2) {
			e2.printStackTrace();
			// logger.error("读取内核磁道数据失败!",e2);
			return;
		} catch (Exception e2) {
			e2.printStackTrace();
			// logger.error("读取内核磁道数据失败!",e2);
			return;
		}
		// logger.info("再次读取内核获取磁道：读取长度"+trac_count);
		// 非指定账户圈存不获取转入卡磁道数据，其实此处可以去掉，因为在刷卡时已经获取了磁道数据
		if (trac_count > 0
				&& !Cache.getInstance().getTransCode()
						.equals(TransFieldConstant.TRANS_CODE_QC_FZD)) {
			byte[] trackResult = new byte[trac_count];
			System.arraycopy(track_2_byte, 0, trackResult, 0, trac_count);
			// String magData = DataConverter.bytesToHexString(trackResult);
			// logger.debug("磁道等数据："+DataConverter.bytesToHexString(trackResult));
			// String data = HexUtil.bcd2str(trackResult);
			Map<String, String> resMap = TlvUtil.tlvToMap(HexUtil
					.bcd2str(trackResult));
			if (resMap.get("5A") == null) {// 如果5A域未读取到，则从磁道数据中获取
				String track2data = resMap.get("57");
				String card = track2data.split("D")[0];
				resMap.put("5A", card);
				mCarno = card;
			} else {
				mCarno = resMap.get("5A");
			}
			Cache.getInstance().setCardNo(mCarno.replace("F", ""));
			// logger.debug(resMap.get("5A").replace("F", "")+"
			// "+resMap.get("57")+" "+resMap.get("5F34"));
			// logger.debug("磁道2数据："+resMap.get("57").replace("F", ""));
			String mSecondTrack = resMap.get("57").replace("F", "");
			mSecondTrack.replaceAll("[:;<>=]", "D");
			Cache.getInstance().setTrack_2_Data(mSecondTrack);
			if (!StringUtil.isEmpty(mSecondTrack) && mSecondTrack.contains("D")) {
				int pos = mSecondTrack.indexOf("D");
				String invalidate = mSecondTrack.substring(pos + 1, pos + 5);
				if (StringUtil.isEmpty(invalidate)) {
					invalidate = "0000";
				}
				// logger.debug("IC卡有效期="+invalidate);
				Cache.getInstance().setInvalidDate(invalidate);
			}
			// logger.info("卡片序列号：" + resMap.get("5F34"));
			Cache.getInstance().setCardSeqNo(resMap.get("5F34"));
		} else {
			if (Cache.getInstance().getTransCode()
					.equals(TransFieldConstant.TRANS_CODE_QC_FZD)) {
				// logger.debug("非指定账户圈存不读取装入卡的磁道数据");
			}
		}
		AidlEMVL2 mDev = null;
		try {
			mDev = PbocDev.getInstance(mContext, mDeviceManager)
					.getOriginalDev();
		} catch (Exception e1) {
			// logger.error("获取PBOC设备异常，读取ARQC失败");
			e1.printStackTrace();
			return;
		}
		// 读取ARQC值
		String arqc = null;
		try {
			byte[] arqcTemp = new byte[256];
			// int num = mDev.readKernelData(new String[] { EMVTAGStr.EMVTAG_AC
			// }, arqcTemp);
			int num = mPbocDev.readKernelData(EMVTAGS.EMVTAG_AC, arqcTemp);
			byte[] arqcData = new byte[num];
			if (num >= 0) {
				System.arraycopy(arqcTemp, 0, arqcData, 0, num);
				arqc = HexUtil.bcd2str(arqcData);
			}
			// Cache.getInstance().setPrintArqc(arqc);
			byte[] temp3 = new byte[1024];
			// int count3 =
			// mDev.readKernelData(EMVTAGStr.getkernelDataForPrint(), temp3);
			int count3 = mDev.readKernelData(EMVTAGS.EMVTAG_AC, temp3);
			byte[] data3 = new byte[count3];
			System.arraycopy(temp3, 0, data3, 0, count3);
			String printData = HexUtil.bcd2str(data3);
			// logger.debug("打印内核数据："+printData);
			if (!"".equals(arqc) && arqc != null) {
				// logger.info("arqc tag替换前："+arqc);
				arqc = arqc.replaceFirst("9F26", "9F99");
				// logger.info("arqc tag替换后："+arqc);
				printData = printData + arqc;
			}
			// Cache.getInstance().setPrintIcData(printData);
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("", e);
		}
		// logger.info("kernelProc读取 arqc =" + arqc);
	}

	private void showResultTip(String tip) {
		Message msg = new Message();
		msg.what = MSG_TIP;
		msg.obj = tip;
		mHandler.sendMessage(msg);
	}

	private void showToastTip(String tip) {
		Message msg = new Message();
		msg.what = TOAST_TIP;
		msg.obj = tip;
		mHandler.sendMessage(msg);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// startCheckCard(3);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		endPboc();// huang
	}

	/**
	 * 初始化页面
	 */
	public abstract void initViewData();

	/**
	 * 获取下一个页面的activity
	 * 
	 * @return
	 */
	public abstract void getNextStep(String cardno);

	private void playSuccessVoic() {
		if (CommonUtil.is960E(BaseSwipeCardActivity.this)
				|| CommonUtil.is960F(BaseSwipeCardActivity.this)) {
			// 960F刷卡成功 需要
			playSound(1, 0);
		}

	}
}
