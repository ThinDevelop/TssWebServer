package com.tss.webserver.utils;
/**
 *  Copyright 2014, Fujian Centerm Information Co.,Ltd.  All right reserved.
 *  THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF  FUJIAN CENTERM PAY CO.,
 *  LTD.  THE CONTENTS OF THIS FILE MAY NOT BE DISCLOSED TO THIRD
 *  PARTIES, COPIED OR DUPLICATED IN ANY FORM, IN WHOLE OR IN PART,
 *  WITHOUT THE PRIOR WRITTEN PERMISSION OF  FUJIAN CENTERM PAY CO., LTD.
 *
 *  TLV函数
 *  Edit History:
 *
 *    2014/09/11 - Created by Xrh.
 *    
 *  Edit History：
 *  
 *   2014/10/22 - Modified by Xrh.
 *   L字段长度改为无符号整型
 */

import com.centerm.iso8583.util.BCDUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * tlv工具类
 *
 */
public class TlvUtil {
	
	
	/**
	 * tlv格式字符串解析成MAP对象
	 * @param tlv tlv格式数据
	 * @return
	 */
	public static Map<String, String> tlvToMap(String tlv){
		return tlvToMap(HexUtil.hexStringToBytes(tlv));
	}
	
	/**
	 * 若tag标签的第一个字节后四个bit为“1111”,则说明该tag占两个字节
	 * 例如“9F33”;否则占一个字节，例如“95”
	 * @param tlv
	 * @return
	 */
	public static Map<String, String> tlvToMap(byte[] tlv){
		Map<String, String> map = new HashMap<String, String>();
		int index = 0;
		while (index < tlv.length){
			if (HexUtil.bytesToHexString(new byte[] {tlv[index]}).substring(1, 2).equals("F")){ //tag双字节
				byte[] tag = new byte[2];
				System.arraycopy(tlv, index, tag, 0, 2);
				index += 2;
				int length = 0;
				//表示该L字段占一个字节
				if (tlv[index] >> 7 == 0) {
					length = tlv[index];	//value字段长度
					index ++;
				}
				// 表示该L字段不止占一个字节
				else {
					//获取该L字段占字节长度
					int lenlen = tlv[index]&0x7F; 
					index ++;
					for (int i = 0; i < lenlen; i++) {
						length =length << 8;
						//value字段长度 &ff转为无符号整型
						length += tlv[index] & 0xff;  
						index ++;
					}
				}
				byte[] value =  new byte[length];
				System.arraycopy(tlv, index, value, 0, length);
				index += length;
				map.put(BCDUtil.bcd2Str(tag), BCDUtil.bcd2Str(value));
			}
			//tag单字节
			else {
				byte[] tag = new byte[1];
				System.arraycopy(tlv, index, tag,0 , 1);
				index ++;
				int length = 0;
				if (tlv[index] >> 7 == 0){	//表示该L字段占一个字节
					length = tlv[index]; //value字段长度
					index ++;
				}
				//表示该L字段不止占一个字节
				else {
					//获取该L字段占字节长度
					int lenlen = tlv[index]&0x7F;
					index ++;
					for (int i = 0; i < lenlen; i++) {
						length =length<<8;
						//value字段长度&ff转为无符号整型
						length += tlv[index]&0xff;
						index ++;
					}
				}
				byte[] value = new byte[length];
				System.arraycopy(tlv, index, value, 0, length);
				index += length;
				map.put(BCDUtil.bcd2Str(tag), BCDUtil.bcd2Str(value));
			}
		}
		return map;
	}
	
}