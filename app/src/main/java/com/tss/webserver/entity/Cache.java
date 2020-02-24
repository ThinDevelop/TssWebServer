package com.tss.webserver.entity;

import android.content.Context;

import java.util.Map;

public class Cache {

	private static Cache instance;
	
	private Cache() {

	}

	public synchronized static Cache getInstance() {
		if (null == instance) {
			instance = new Cache();
		}
		return instance;
	}
	
	public synchronized void clearAllData() {
		TerminalSerialNo = null;
	}
	
	private Context context;
	
	public Context getContext() {
		return context;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	private String scan_mode="";
	public String getScanMode() {
		return scan_mode;
	}
	
	public void setScanMode(String scanmode) {
		scan_mode = scanmode;
	}
	
	private String TerminalSerialNo;
	private String TransCode;
	private String TransMoney;
	private String ErrCode;
	private String ErrDesc;
	// 交易主账号,field2
	private String MasterAcct;
	// 卡序列号,field23
	private String CardSeqNo;
	// 55域,field55
	private String TlvTag55;
	// 卡有效期,field14
	private String InvalidDate;
	// 二磁,field35
	private String Track_2_Data;
	// 三磁,field36
	private String Track_3_Data;
	// 批次号
	private String BatchNo;
	// 流水号,field11
	private String SerialNo;
	// 交易类型
	private String TransType;
	// PinBlock
	private String PinBlock;
	private String SerInputCode;
	// 附加数据
	private String TransDate;
	private String AddDataWord;
	private String CardNo;
	private String DimensionCode;
	private String RefeNumber;
	private Map<String, String> responseMap;
	
	private boolean HasPin;
	
	private String TTEK;
	private String ITMK;
	private String Balance;
	
	//授权码
	private String License;
	
	public String getLicense() {
		return License;
	}
	
	public void setLicense(String license) {
		License = license;
	}
	
	public String getBalance() {
		return Balance;
	}
	
	public void setBalance(String balance) {
		Balance = balance;
	}
	
	public String getITMK() {
		return ITMK;
	}
	
	public void setITMK(String iTMK) {
		ITMK = iTMK;
	}
	
	public String getTTEK() {
		return TTEK;
	}
	
	public void setTTEK(String tTEK) {
		TTEK = tTEK;
	}
	
	public String getTerminalSerialNo() {
		return TerminalSerialNo;
	}
	
	public void setTerminalSerialNo(String terminalSerialNo) {
		TerminalSerialNo = terminalSerialNo;
	}
	
	public String getTransCode() {
		return TransCode;
	}
	
	public void setTransCode(String transCode) {
		TransCode = transCode;
	}
	
	public String getTransMoney() {
		return TransMoney;
	}

	public void setTransMoney(String transMoney) {
		TransMoney = transMoney;
	}
	
	public String getErrCode() {
		return ErrCode;
	}
	
	public void setErrCode(String errCode) {
		ErrCode = errCode;
	}
	
	public String getErrDesc() {
		return ErrDesc;
	}
	
	public void setErrDesc(String errDesc) {
		ErrDesc = errDesc;
	}
	
	public String getMasterAcct() {
		return MasterAcct;
	}
	
	public void setMasterAcct(String masterAcct) {
		MasterAcct = masterAcct;
	}
	
	public String getCardSeqNo() {
		return CardSeqNo;
	}
	
	public void setCardSeqNo(String cardSeqNo) {
		CardSeqNo = cardSeqNo;
	}
	
	public String getTlvTag55() {
		return TlvTag55;
	}
	
	public void setTlvTag55(String tlvTag55) {
		TlvTag55 = tlvTag55;
	}
	
	public String getInvalidDate() {
		return InvalidDate;
	}
	
	public void setInvalidDate(String invalidDate) {
		InvalidDate = invalidDate;
	}
	
	public String getTrack_2_Data() {
		return Track_2_Data;
	}
	
	public void setTrack_2_Data(String track_2_Data) {
		Track_2_Data = track_2_Data;
	}
	
	public String getTrack_3_Data() {
		return Track_3_Data;
	}
	
	public void setTrack_3_Data(String track_3_Data) {
		Track_3_Data = track_3_Data;
	}
	public String getBatchNo() {
		return BatchNo;
	}
	
	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}
	
	public String getSerialNo() {
		return SerialNo;
	}
	
	public void setSerialNo(String serialNo) {
		SerialNo = serialNo;
	}
	
	public String getTransType() {
		return TransType;
	}
	
	public void setTransType(String transType) {
		TransType = transType;
	}
	
	public String getPinBlock() {
		return PinBlock;
	}
	
	public void setPinBlock(String pinBlock) {
		PinBlock = pinBlock;
	}
	
	public String getSerInputCode() {
		return SerInputCode;
	}
	
	public void setSerInputCode(String serInputCode) {
		SerInputCode = serInputCode;
	}
	
	public String getAddDataWord() {
		return AddDataWord;
	}
	
	public void setAddDataWord(String addDataWord) {
		AddDataWord = addDataWord;
	}
	
	public String getCardNo() {
		if(CardNo != null && CardNo.length() > 19){
			CardNo = CardNo.substring(0, 20);
		}
		return CardNo;
	}
	
	public void setCardNo(String cardNo) {
		CardNo = cardNo;
	}
	
	public void setReferNumber(String refeNumber) {
		RefeNumber = refeNumber;
	}
	
	public String getReferNumber() {
		return RefeNumber;
	}
	
	public void setTransDate(String transDate) {
		TransDate = transDate;
	}
	
	public String getTransDate() {
		return TransDate;
	}
	
	public boolean isHasPin() {
		return HasPin;
	}
	
	public void setHasPin(boolean hasPin) {
		HasPin = hasPin;
	}
	
	public String getDimensionCode() {
		return DimensionCode;
	}
	
	public void setDimensionCode(String dimensionCode) {
		DimensionCode = dimensionCode;
	}
	
	public Map<String, String> getResponseMap() {
		return responseMap;
	}
	
	public void setResponseMap(Map<String, String> responseMap) {
		this.responseMap = responseMap;
	}
	
}
