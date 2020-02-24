package com.tss.webserver.entity;

import java.io.Serializable;

/**
 * 交易数据对象，用于保存每个交易节点产生的数据。
 *
 */
public class TransactionEntity implements Serializable {

    private static final long serialVersionUID = 5390682994083594853L;

    public static final String TRANSACTION_CODE="transprocode";
    
    // 交易凭证号
    public String mBatchBillNo;
    // 交易金额
    public double mTransMoney;
    // 卡号
    public String mCardno;
}
