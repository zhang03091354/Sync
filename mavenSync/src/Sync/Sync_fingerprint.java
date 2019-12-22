package Sync;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class Sync_fingerprint {
    private byte[] bytes = new byte[1000];
    private int index_adler32 = 0;
    private int numOfBytes = 0;
    private long presentAdler=0;

    public long getAdler32(byte next, int isToClear, int len){
        if(isToClear==1){
            index_adler32=0;
            numOfBytes=0;
        }
        long s1=0,s2=0;
        if(numOfBytes==len){
            s1=presentAdler&0xffff;
            s2=presentAdler>>16;
            s1=s1-bytes[index_adler32%len]+next;
            s2=s2-len*bytes[index_adler32%len]+s1;
        }
        bytes[index_adler32%len]=next;
        if(numOfBytes==len-1){
            for(int i=0;i<len;i++){
                s1=(s1+bytes[i])%65521;
                s2=(s2+s1)%65521;
            }
            numOfBytes++;
        }else {
            return -1;
        }
        return (s2<<16)+s1;
    }
    public long getAdler32_1(byte[] array,int len){
        long s1=0,s2=0;
        for(int i=0;i<len;i++){
            s1=(s1+array[i])%65521;
            s2=(s2+s1)%65521;
        }
        return (s2<<16)+s1;
        //String Long.toHexString(Long v);
        //Long Long.valueOf(String v, int radix);//其中radix表示将字符串v为哪种进制的表示方法
    }
    public String getMD5(String stringSrc){
        try {
            MessageDigest md=MessageDigest.getInstance("MD5");
            byte[] secretBytes=md.digest(stringSrc.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : secretBytes) {
                stringBuilder.append(String.format("%02x", b & 0xff));
            }
            String secretString=stringBuilder.toString();
            return secretString;
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }
    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }
}
