package com.tss.webserver.device;

import android.content.Context;
import android.os.RemoteException;

import com.centerm.smartpos.aidl.pboc.AidlCheckCardListener;
import com.centerm.smartpos.aidl.pboc.AidlEMVL2;
import com.centerm.smartpos.aidl.pboc.EmvTransData;
import com.centerm.smartpos.aidl.pboc.PBOCListener;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.EMVConstant;
import com.tss.webserver.constant.TransFieldConstant;

public class PbocDev {
//	private static Logger logger = Logger.getLogger(PbocDev.class);
//	private AidlPboc mDev = null;
//	private AidlDeviceService mDeviceService;
	private AidlEMVL2 mDev = null;
	private AidlDeviceManager mDeviceManager;
	private static PbocDev mInstance = null;
	private Context mContext;

	/**
	 * 开始PBOC流程
	 * 
	 * @param type
	 * @param requestAmtPosition
	 *            请求输入金额位置 0x01 显示卡号前 0x02后
	 * @param listener
	 */
	public void startProc(final byte type, final byte requestAmtPosition, PBOCListener listener) {

//		logger.debug(" cashup_startProc called..................");
//		EmvTransData transData = new EmvTransData(type// 交易类型0x00消费
//				, requestAmtPosition// 请求输入金额位置 0x01 显示卡号前 0x02后
//				, false// 是否支持电子现金
//				, false// 是否支持国密算法
//				, false// 是否强制联机
//				, (byte) 0x01// 0x01PBOC 0x02QPBOC
//				, (byte) 0x00 // 界面类型：0x00接触 0x01非接
//				, new byte[] { 0x00, 0x00, 0x00 });
		EmvTransData transData = new EmvTransData();
		transData.setTranstype(type);
		transData.setRequestAmtPosition(requestAmtPosition);// 请求输入金额位置
		transData.setIsEcashEnable(false); // 是否支持电子现金
		transData.setIsForceOnline(false); // 是否强制联机
		transData.setIsSmEnable(false); // 是否支持国密算法
		transData.setSlotType((byte)0x00); //界面类型 0x00接触 0x01非接
		try {
			mDev.processPBOC(transData, listener);
//			logger.debug("PBOC交易开始");
		} catch (RemoteException e) {
			try {
				listener.onError(0x99);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	/**
	 * 开始非接PBOC流程
	 * 
	 * @param type
	 * @param requestAmtPosition
	 *            请求输入金额位置 0x01 显示卡号前 0x02后
	 * @param listener
	 */
	public void startRfProc(final byte type, final byte requestAmtPosition, PBOCListener listener) {

//		logger.debug(" cashup_startProc called..................");
//		EmvTransData transData = new EmvTransData(type// 交易类型0x00消费
//				, requestAmtPosition// 请求输入金额位置 0x01 显示卡号前 0x02后
//				, true// 是否支持电子现金
//				, false// 是否支持国密算法
//				, false// 是否强制联机
//				, (byte) 0x01// 0x01PBOC 0x02QPBOC
//				, (byte) 0x01 // 界面类型：0x00接触 0x01非接
//				, new byte[] { 0x00, 0x00, 0x00 });
		EmvTransData transData = new EmvTransData();
		transData.setTranstype(type);
		transData.setRequestAmtPosition(requestAmtPosition);// 请求输入金额位置
		transData.setIsEcashEnable(true); // 是否支持电子现金
		transData.setIsForceOnline(false); // 是否强制联机
		transData.setIsSmEnable(false); // 是否支持国密算法
		transData.setSlotType((byte)0x01); //界面类型 0x00接触 0x01非接
		try {
			mDev.processPBOC(transData, listener);
//			logger.debug("PBOC交易开始");
		} catch (RemoteException e) {
			try {
				listener.onError(0x99);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	/**
	 * 获取启动PBOC参数对象
	 * 
	 * @param transCode
	 *            交易代码例 消费：002302
	 * 
	 * @return
	 */
	public static EmvTransData getPbocEmvTransData(String transCode) {

		/*
		 * * @param transType transtype:交易类型，定义如下： 消费 0x00 查询 0x31 预授权 0x03
		 * 指定账户圈存 0x60 非指定账户圈存 0x62 现金圈存 0x63 现金充值撤销 0x17 退货 0x20 消费撤销 0x20
		 * 非指定账户圈存读转入卡 0xF1 卡片余额查询 0xF2 卡片交易日志查询 0xF3 卡片圈存日志查询 0xF4
		 * 
		 * @param moneyPos 请求输入金额位置 0x01:显示卡号前 0x02： 显示卡号后
		 */
//		EmvTransData transData = new EmvTransData((byte) 0x00 // 交易类型0x00消费
//		, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//		, true// 是否支持电子现金
//				, false// 是否支持国密算法
//				, false// 是否强制联机
//				, (byte) 0x01// 0x01PBOC 0x02QPBOC
//				, (byte) 0x00 // 界面类型：0x00接触 0x01非接
//				, new byte[] { 0x00, 0x00, 0x00 });
		
		EmvTransData transData = new EmvTransData();
		transData.setTranstype((byte) EMVConstant.TransType.TRANS_TYPE_CONSUME);
		transData.setIsEcashEnable(true); // 是否支持电子现金
		transData.setIsForceOnline(false); // 是否强制联机
		transData.setIsSmEnable(false); // 是否支持国密算法
		transData.setSlotType((byte) EMVConstant.SlotType.SLOT_TYPE_IC); //界面类型 0x00接触 0x01非接
		transData.setEMVFlow((byte) EMVConstant.EMVFlowSelect.EMV_FLOW_PBOC);
		transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		
		transData.setConfirmCardNo(true);//更快速
		
		if (TransFieldConstant.TRANS_CODE_CONSUME.equals(transCode)) {// 消费
			transData.setTranstype((byte) 0x00);
			transData.setRequestAmtPosition((byte) 0x01);
		}
		else if (TransFieldConstant.TRANS_CODE_PRE.equals(transCode)) {// 预授权
			transData.setTranstype((byte) 0x03);
			transData.setRequestAmtPosition((byte) 0x02);
			//transData.setEcashEnable(false);
			transData.setIsEcashEnable(false);
		}
		else if (TransFieldConstant.TRANS_CODE_QC_ZD.equals(transCode)) {// 指定账户圈存
//			logger.debug("指定账户圈存" + transCode);
			transData.setTranstype((byte) 0x60);
			//transData.setEcashEnable(false);
			transData.setIsEcashEnable(false);
			transData.setRequestAmtPosition((byte) 0x02);
		}
		else if (TransFieldConstant.TRANS_CODE_QC_FZD.equals(transCode)) {// 非指定账户圈存
//			logger.debug("非指定账户圈存" + transCode);
			transData.setTranstype((byte) 0xF1);
			//transData.setEcashEnable(false);
			transData.setIsEcashEnable(false);
			transData.setRequestAmtPosition((byte) 0x02);
		}
		else if (TransFieldConstant.TRANS_CODE_QUERY_BALANCE.equals(transCode)) {// 余额查询
//			logger.debug("余额查询PBOC流程" + transCode);
			transData.setTranstype((byte) 0x31);
			transData.setRequestAmtPosition((byte) 0x02);
            //transData.setEcashEnable(false);
            transData.setIsEcashEnable(false);
		} 
		else if (TransFieldConstant.TRANS_CODE_CONSUME_CX.equals(transCode)) {// 消费撤销
//			logger.debug("PBOC简化流程" + transCode);
			transData.setTranstype((byte) 0x20);
			transData.setRequestAmtPosition((byte) 0x02);
           // transData.setEcashEnable(false);
            transData.setIsEcashEnable(false);
		}
		else if (TransFieldConstant.TRANS_CODE_DZXJ_TRANS_QUERY.equals(transCode)) {
			// 电子现金交易查询
//			logger.debug("电子现金交易查询获取非接EMV启动参数");
//			transData = new EmvTransData((byte) 0xF3 // 交易类型0x00消费
//			, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//			, true// 是否支持电子现金
//					, false// 是否支持国密算法
//					, false// 是否强制联机
//					, (byte) 0x01// 0x01PBOC 0x02QPBOC
//					, (byte) 0x00 // 界面类型：0x00接触 0x01非接
//					, new byte[] { 0x00, 0x00, 0x00 });
			transData.setTranstype((byte)0xF3);
			transData.setIsEcashEnable(true); // 是否支持电子现金
			transData.setIsForceOnline(false); // 是否强制联机
			transData.setIsSmEnable(false); // 是否支持国密算法
			transData.setSlotType((byte)0x00); //界面类型 0x00接触 0x01非接
			transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		}
		else if (TransFieldConstant.TRANS_CODE_DZXJ_BALANCE_QUERY.equals(transCode)) {
			// 电子现金余额查询
//			logger.debug("电子现金余额查询获取插卡EMV启动参数");
//			transData = new EmvTransData((byte) 0xF2 // 交易类型0x00消费
//			, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//			, true// 是否支持电子现金
//					, false// 是否支持国密算法
//					, false// 是否强制联机
//					, (byte) 0x01// 0x01PBOC 0x02QPBOC
//					, (byte) 0x00 // 界面类型：0x00接触 0x01非接
//					, new byte[] { 0x00, 0x00, 0x00 });
			transData.setTranstype((byte)0xF2);
			transData.setIsEcashEnable(true); // 是否支持电子现金
			transData.setIsForceOnline(false); // 是否强制联机
			transData.setIsSmEnable(false); // 是否支持国密算法
			transData.setSlotType((byte)0x00); //界面类型 0x00接触 0x01非接
			transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		} else if (TransFieldConstant.TRANS_CODE_DZXJ_TRANS_PUTONG_CONSUMER.equals(transCode)) {
			// 电子现金普通消费
//			logger.debug("电子现金普通消费插卡EMV启动参数");
//			transData = new EmvTransData((byte) 0x00 // 交易类型0x00消费
//			, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//			, true// 是否支持电子现金
//					, false// 是否支持国密算法
//					, false// 是否强制联机
//					, (byte) 0x01// 0x01PBOC 0x02QPBOC
//					, (byte) 0x00 // 界面类型：0x00接触 0x01非接
//					, new byte[] { 0x00, 0x00, 0x00 });
			transData.setIsEcashEnable(true); // 是否支持电子现金
			transData.setIsForceOnline(false); // 是否强制联机
			transData.setIsSmEnable(false); // 是否支持国密算法
			transData.setSlotType((byte)0x00); //界面类型 0x00接触 0x01非接
			transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		} else {// pboc简化流程，主要是为了读取IC卡卡号
//			logger.debug("PBOC简化流程" + transCode);
			transData.setTranstype((byte) EMVConstant.TransType.TRANS_TYPE_CONSUME);
			transData.setRequestAmtPosition((byte) 0x02);
			transData.setTransTypeSimpleFlow(true);//qpboc
		}
//		logger.debug("PBOC简化流程" + transData.getTranstype());
		return transData;
	}
	/**
	 * 获取启动PBOC参数对象 非接Q联机 消费强制联机设置
	 * 
	 * @param transCode
	 *            交易代码例 消费：002302
	 * 
	 * @return
	 */
	public static EmvTransData getPbocEmvTransData2(String transCode) {

		/*
		 * * @param transType transtype:交易类型，定义如下： 消费 0x00 查询 0x31 预授权 0x03
		 * 指定账户圈存 0x60 非指定账户圈存 0x62 现金圈存 0x63 现金充值撤销 0x17 退货 0x20 消费撤销 0x20
		 * 非指定账户圈存读转入卡 0xF1 卡片余额查询 0xF2 卡片交易日志查询 0xF3 卡片圈存日志查询 0xF4
		 * 
		 * @param moneyPos 请求输入金额位置 0x01:显示卡号前 0x02： 显示卡号后
		 */
//		EmvTransData transData = new EmvTransData((byte) 0x00 // 交易类型0x00消费
//		, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//		, true// 是否支持电子现金
//				, false// 是否支持国密算法
//				, true// 是否强制联机
//				, (byte) 0x02// 0x01PBOC 0x02QPBOC
//				, (byte) 0x01 // 界面类型：0x00接触 0x01非接
//				, new byte[] { 0x00, 0x00, 0x00 });
		EmvTransData transData = new EmvTransData();
		transData.setIsEcashEnable(true); // 是否支持电子现金
		transData.setIsForceOnline(true); // 是否强制联机
		transData.setIsSmEnable(false); // 是否支持国密算法
		transData.setSlotType((byte)0x01); //界面类型 0x00接触 0x01非接
		transData.setTransTypeSimpleFlow(true);//QPBOC
		transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		if (TransFieldConstant.TRANS_CODE_CONSUME.equals(transCode)) {// 消费
			transData.setTranstype((byte) 0x00);
			transData.setRequestAmtPosition((byte) 0x01);
		}
//		logger.debug("PBOC简化流程" + transData.getTranstype());
		return transData;
	}
	
	public static PbocDev getInstance(Context context, AidlDeviceManager manager) throws Exception {
		if (null == manager) {
			throw new Exception("服务未绑定无法启动EMV");
		}
		if(null==mInstance){
			mInstance = new PbocDev(context, manager);
		}
			
		if (null == mInstance) {
			throw new Exception("获取PBOC设备实例失败");
		}
		return mInstance;
	}

	private PbocDev(Context context, AidlDeviceManager manager) {
		mContext = context;
		mDeviceManager = manager;
		if (null == manager) {
//			logger.error("EMV设备获取失败，服务未绑定");
			return;
		}
		try {
			//mDev = AidlPboc.Stub.asInterface(service.getEMVL2());
			mDev = AidlEMVL2.Stub.asInterface(manager
					.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));
		} catch (RemoteException e) {
//			logger.error("EMV设备获取失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 中断PBOC
	 */
	public void abortPboc(){
	    try {
            mDev.abortPBOC();
        } catch (RemoteException e) {
//            logger.error("中断PBOC异常");
            e.printStackTrace();
        }
	}
	/**
	 * 结束PBOC
	 */
//	public void endPboc(){
//	    try {
//            mDev.endPBOC();
//        } catch (RemoteException e) {
//            logger.error("结束pboc异常");
//            e.printStackTrace();
//        }
//	}

	/**
	 * 开始检卡
	 * 
	 * @param supportMag
	 * @param supportIC
	 * @param supportRF
	 * @param timeout
	 * @param listener
	 * @return
	 */
	public boolean checkCard(boolean supportMag, boolean supportIC, boolean supportRF, int timeout,
			AidlCheckCardListener listener) {
		try {
			mDev.checkCard(supportMag, supportIC, supportRF, timeout, listener);
		} catch (RemoteException e) {
//			logger.error("PBOC检卡失败", e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 导入交易金额
	 * 
	 * @param money
	 * @return
	 */
	public boolean importMoney(String money) {
		try {
			mDev.importAmount(money);
		} catch (RemoteException e) {
//			logger.error("金额导入失败", e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean stopCheckCard() {
		try {
            mDev.cancelCheckCard();
		} catch (RemoteException e) {
//			logger.error("停止PBOC失败");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public AidlEMVL2 getOriginalDev() {
		return mDev;
	}

	public boolean importConfirmCardInfoRes(boolean confirm) {
		try {
			mDev.importConfirmCardInfoRes(confirm);
		} catch (RemoteException e) {

			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 获取启动非接QPBOC参数对象
	 * 
	 * @param transCode
	 *            交易代码例 消费：002302
	 * 
	 * @return
	 */
	public static EmvTransData getRFPbocEmvTransData(String transCode) {

		/*
		 * * @param transType transtype:交易类型，定义如下： 消费 0x00 查询 0x31 预授权 0x03
		 * 指定账户圈存 0x60 非指定账户圈存 0x62 现金圈存 0x63 现金充值撤销 0x17 退货 0x20 消费撤销 0x20
		 * 非指定账户圈存读转入卡 0xF1 卡片余额查询 0xF2 卡片交易日志查询 0xF3 卡片圈存日志查询 0xF4
		 * 
		 * @param moneyPos 请求输入金额位置 0x01:显示卡号前 0x02： 显示卡号后
		 */
//		EmvTransData transData = new EmvTransData((byte) 0x00 // 交易类型0x00消费
//		, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//		, true// 是否支持电子现金
//				, false// 是否支持国密算法
//				, false// 是否强制联机
//				, (byte) 0x01// 0x01PBOC 0x02QPBOC
//				, (byte) 0x00 // 界面类型：0x00接触 0x01非接
//				, new byte[] { 0x00, 0x00, 0x00 });
		EmvTransData transData = new EmvTransData();
		transData.setTranstype((byte)0x00);
		transData.setIsEcashEnable(true); // 是否支持电子现金
		transData.setIsForceOnline(false); // 是否强制联机
		transData.setIsSmEnable(false); // 是否支持国密算法
		transData.setSlotType((byte)0x00); //界面类型 0x00接触 0x01非接
		transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		if (TransFieldConstant.TRANS_CODE_CONSUME.equals(transCode)) {// 消费
			transData.setTranstype((byte) 0x00);
			transData.setRequestAmtPosition((byte) 0x01);
		} else if (TransFieldConstant.TRANS_CODE_PRE.equals(transCode)) {// 预授权
			transData.setTranstype((byte) 0x03);
			transData.setRequestAmtPosition((byte) 0x02);
		} else if (TransFieldConstant.TRANS_CODE_CONSUME_CX.equals(transCode)) {// 消费撤销
//			logger.debug("PBOC简化流程" + transCode);
			transData.setTranstype((byte) 0x03);
			transData.setRequestAmtPosition((byte) 0x02);
		} else if (TransFieldConstant.TRANS_CODE_DZXJ_TRANS_QUERY.equals(transCode)) {
			// 电子现金交易查询
//			logger.debug("电子现金交易查询获取非接EMV启动参数");
//			transData = new EmvTransData((byte) 0xF3 // 交易类型0x00消费
//			, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//			, true// 是否支持电子现金
//					, false// 是否支持国密算法
//					, false// 是否强制联机
//					, (byte) 0x02// 0x01PBOC 0x02QPBOC
//					, (byte) 0x01 // 界面类型：0x00接触 0x01非接
//					, new byte[] { 0x00, 0x00, 0x00 });
			transData.setTranstype((byte)0xF3);
			transData.setIsEcashEnable(true); // 是否支持电子现金
			transData.setIsForceOnline(false); // 是否强制联机
			transData.setIsSmEnable(false); // 是否支持国密算法
			transData.setSlotType((byte)0x01); //界面类型 0x00接触 0x01非接
			transData.setTransTypeSimpleFlow(true);//QPBOC
			transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		} else if (TransFieldConstant.TRANS_CODE_DZXJ_BALANCE_QUERY.equals(transCode)) {
			// 电子现金余额查询
//			logger.debug("电子现金余额查询获取非接EMV启动参数");
//			transData = new EmvTransData((byte) 0xF2 // 交易类型0x00消费
//			, (byte) 0x01// 请求输入金额位置 0x01 显示卡号前 0x02后
//			, true// 是否支持电子现金
//					, false// 是否支持国密算法
//					, false// 是否强制联机
//					, (byte) 0x02// 0x01PBOC 0x02QPBOC
//					, (byte) 0x01 // 界面类型：0x00接触 0x01非接
//					, new byte[] { 0x00, 0x00, 0x00 });
			transData.setTranstype((byte)0xF2);
			transData.setIsEcashEnable(true); // 是否支持电子现金
			transData.setIsForceOnline(false); // 是否强制联机
			transData.setIsSmEnable(false); // 是否支持国密算法
			transData.setSlotType((byte)0x01); //界面类型 0x00接触 0x01非接
			transData.setTransTypeSimpleFlow(true);//QPBOC
			transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.BEFORE_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		} else if (TransFieldConstant.TRANS_CODE_DZXJ_TRANS_QUICK_PAY.equals(transCode)) {
			// 电子现金快速支付
//			logger.debug("快速支付获取非接EMV启动参数");
//			transData = new EmvTransData((byte) 0x00 // 交易类型0x00消费
//			, (byte) 0x02// 请求输入金额位置 0x01 显示卡号前 0x02后
//			, true// 是否支持电子现金
//					, false// 是否支持国密算法
//					, false// 是否强制联机
//					, (byte) 0x02// 0x01PBOC 0x02QPBOC
//					, (byte) 0x01 // 界面类型：0x00接触 0x01非接
//					, new byte[] { 0x00, 0x00, 0x00 });
			transData.setTranstype((byte)0x00);
			transData.setIsEcashEnable(true); // 是否支持电子现金
			transData.setIsForceOnline(false); // 是否强制联机
			transData.setIsSmEnable(false); // 是否支持国密算法
			transData.setSlotType((byte)0x01); //界面类型 0x00接触 0x01非接
			transData.setTransTypeSimpleFlow(true);//QPBOC
			transData.setRequestAmtPosition((byte) EMVConstant.AmtPosition.AFTER_DISPLAY_CARD_NUMBER);// 请求输入金额位置
		} else {// pboc简化流程，主要是为了读取IC卡卡号
//			logger.debug("PBOC简化流程" + transCode);
			transData.setTranstype((byte) 0x03);
			transData.setRequestAmtPosition((byte) 0x02);
		}

//		logger.debug("PBOC简化流程" + transData.getTranstype());
		return transData;
	}

	/**
	 * 获取PBOC读取内核55域数据的TLV byte数组
	 * 
	 * @param transCode
	 * @return
	 * @throws Exception
	 */
	public static String[] getKernalTag(String transCode) throws Exception {
		if (transCode.equals(TransFieldConstant.TRANS_CODE_QC_ZD)
				|| transCode.equals(TransFieldConstant.TRANS_CODE_QC_FZD)
				|| transCode.equals(TransFieldConstant.TRANS_CODE_DZXJCZ)) {
			return EMVTAGStr.getLakalaTransferF55Tag();
		} else if (transCode.equals(TransFieldConstant.TRANS_CODE_PRE)
				|| transCode.equals(TransFieldConstant.TRANS_CODE_QUERY_BALANCE)
				|| transCode.equals(TransFieldConstant.TRANS_CODE_DZXJ_TRANS_PUTONG_CONSUMER)) {
			return EMVTAGStr.getLakalaF55UseModeOne();
		} else if (transCode.equals(TransFieldConstant.TRANS_CODE_CONSUME)) {
			return EMVTAGStr.getLakalaF55UseModeOneForOnlineSale();
		} else if (transCode.equals(TransFieldConstant.TRANS_CODE_DZXJCZ_CX)) {
			return EMVTAGStr.getLakalaCashValueVoidF55Tag();
		} else {
			return EMVTAGStr.getLakalaF55UseModeOne();
		}
	}
	
	/**
	 * @设置TLV
	 * @param tag
	 * @param value
	 * @throws RemoteException
	 */
	public void setTlv(String tag, byte[] value) throws RemoteException {
		try {
			//mDev.setTlv(tag, value);
			mDev.setTLV(Integer.valueOf(tag), value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
