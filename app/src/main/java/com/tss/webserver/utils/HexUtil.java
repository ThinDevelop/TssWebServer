package com.tss.webserver.utils;

public class HexUtil {
	
	public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {  
            return null;  
        }  
        hexString = hexString.toUpperCase();  
        int length = hexString.length() / 2;  
        char[] hexChars = hexString.toCharArray();  
        byte[] d = new byte[length];  
        for (int i = 0; i < length; i++) {  
            int pos = i * 2;  
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
        }  
        return d;
    }
	
	private static byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    }
	
	public static final String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if(src == null || src.length<=0){
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}   
			stringBuilder.append(hv.toUpperCase());
		}   
		return stringBuilder.toString();  
	}
	
	
	/**
	 * @函数功能: 字节转换为16进制串
     * @输入参数: btye
     * @输出结果: 16进制串
     * @author Xrh @time 20130410
	 */
	public static String byteToHex(byte b) {
		return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
	}
	
//	public static byte[] hexStringToByte(String hex) {
//		if(hex ==null|| "".equals(hex)){
//			return null;
//		}
//		hex = hex.toUpperCase();
//		int len = (hex.length() / 2);
//		byte[] result = new byte[len];
//		char[] achar = hex.toCharArray();
//		for (int i = 0; i < len; i++) {
//			int pos = i * 2;
//			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
//		}
//		return result;
//	}
	
    private static byte asc_to_bcd(byte asc) {  
        byte bcd;  
  
        if ((asc >= '0') && (asc <= '9'))  
            bcd = (byte) (asc - '0');  
        else if ((asc >= 'A') && (asc <= 'F'))  
            bcd = (byte) (asc - 'A' + 10);  
        else if ((asc >= 'a') && (asc <= 'f'))  
            bcd = (byte) (asc - 'a' + 10);  
        else  
            bcd = (byte) (asc - 48);  
        return bcd;  
    }  
  
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {  
        byte[] bcd = new byte[asc_len / 2];  
        int j = 0;  
        for (int i = 0; i < (asc_len + 1) / 2; i++) {  
            bcd[i] = asc_to_bcd(ascii[j++]);  
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));  
        }  
        return bcd;  
    }  

    /** *//**
     * @函数功能: BCD码转为10进制串(阿拉伯数据)
     * @输入参数: BCD码
     * @输出结果: 10进制串
     */
 public static String bcd2Str(byte[] bytes){
     StringBuffer temp=new StringBuffer(bytes.length*2);
     for(int i=0;i<bytes.length;i++){
      temp.append((byte)((bytes[i]& 0xf0)>>>4));
      temp.append((byte)(bytes[i]& 0x0f));
     }
     return temp.toString().substring(0,1).equalsIgnoreCase("0")?temp.toString().substring(1):temp.toString();
 }

	public static String bcd2str(byte[] bcds) {
	    if(null==bcds){
	        return null;
	    }
		char[] ascii = "0123456789abcdef".toCharArray();
		byte[] temp = new byte[bcds.length * 2];
		for (int i = 0; i < bcds.length; i++) {
			temp[i * 2] = (byte) ((bcds[i] >> 4) & 0x0f);
			temp[i * 2 + 1] = (byte) (bcds[i] & 0x0f);
		}
		StringBuffer res = new StringBuffer();

		for (int i = 0; i < temp.length; i++) {
			res.append(ascii[temp[i]]);
		}
		return res.toString().toUpperCase();
	}

	/**
	 * @description 字符转换字节
	 * @param buff
	 * @return
	 */
	public static byte hex2Byte(String hex) {
		char[] achar = hex.toUpperCase().toCharArray();
		byte b = (byte) (toByte(achar[0]) << 4 | toByte(achar[1]));
		return b;
	}
	
	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	public static int bytes2int(byte[] b) {
		// byte[] b=new byte[]{1,2,3,4};
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}
	
    /** 
     * 将长度为2的byte数组转换为16位int 
     *  
     * @param res 
     *            byte[] 
     * @return int 
     * */  
	public static int bytes2short(byte[] b) {
		// byte[] b=new byte[]{1,2,3,4};
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 2; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	public static String getBinaryStrFromByteArr(byte[] bArr) {
		String result = "";
		for (byte b : bArr) {
			result += getBinaryStrFromByte(b);
		}
		return result;
	}


	public static String getBinaryStrFromByte(byte b) {
		String result = "";
		byte a = b;
		;
		for (int i = 0; i < 8; i++) {
			byte c = a;
			a = (byte) (a >> 1);
			a = (byte) (a << 1);
			if (a == c) {
				result = "0" + result;
			} else {
				result = "1" + result;
			}
			a = (byte) (a >> 1);
		}
		return result;
	}

	public static String getBinaryStrFromByte2(byte b) {
		String result = "";
		byte a = b;
		;
		for (int i = 0; i < 8; i++) {
			result = (a % 2) + result;
			a = (byte) (a >> 1);
		}
		return result;
	}

	public static String getBinaryStrFromByte3(byte b) {
		String result = "";
		byte a = b;
		;
		for (int i = 0; i < 8; i++) {
			result = (a % 2) + result;
			a = (byte) (a / 2);
		}
		return result;
	}
	

    public static byte[] toByteArray(int iSource, int iArrayLen) { 
        byte[] bLocalArr = new byte[iArrayLen]; 
        for ( int i = 0; (i < 4) && (i < iArrayLen); i++) { 
            bLocalArr[i] = (byte)( iSource>>8*i & 0xFF ); 
           
        } 
        return bLocalArr; 
    }  
    
  
    public static byte[] xor(byte[] op1,byte[] op2){
    	if(op1.length!=op2.length){
    		throw new IllegalArgumentException("参数错误，长度不一致");
    	}
    	byte[] result = new byte[op1.length];
    	for(int i=0;i<op1.length;i++){
    		result[i] = (byte) (op1[i] ^ op2[i]);
    	}
    	return result;
    }
}
