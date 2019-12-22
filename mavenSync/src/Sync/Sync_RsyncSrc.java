package Sync;

import CommonFunc.DataStoreChecksumArray;
import CommonFunc.DataTypeRsyncChecksum;
import NettySelf.NettyClient.SyncClient.SyncPciClient;
import NettySelf.NettyClient.SyncClient.SyncRsyncClient;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class Sync_RsyncSrc {
    private int lenOfWindow;
    private Sync_Parameters para;
    private Sync_fingerprint fp;
    private StringBuilder stringBuilder;
    private StringBuilder stringBuilderDiff;
    public DataStoreChecksumArray dataStore;

    {
        para=new Sync_Parameters();
        fp=new Sync_fingerprint();
        stringBuilder = new StringBuilder("");
        stringBuilderDiff = new StringBuilder("");
        dataStore = new DataStoreChecksumArray();
        lenOfWindow=para.lenOfWindow;
    }

    public void DoSync_Rsync2(){
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add(para.fileBackup);
        fileList.add(para.fileSrc_add);
        fileList.add(para.fileSrc_delete);
        fileList.add(para.fileSrc_insert);

        for(String testFile:fileList){
            deBlock(testFile);
        }
    }
    private void deBlock(String fileName){
        try {
            InputStream fileIn = new FileInputStream(fileName);
            BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
            deBlockFromInputBuffer(bufferIn,fileName);
            bufferIn.close();
            fileIn.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    private void deBlockFromInputBuffer(BufferedInputStream in,String fileToCheck) throws IOException {
        long startTime,endTime;
        startTime=currentTimeMillis();
        int byteRead; //读取的字节
        int blockIndex=0; //区块下标
        int fileIndex=0; //文件下标
        int startIndex=0;
        String intTo16String;
        DataTypeRsyncChecksum dataTypeOfRsync = new DataTypeRsyncChecksum();
        int diffNum = 0;
        int len = 0;
        int windowIndex = 0; //窗口内包含字节数
        char[] charArray = new char[2]; //用来存放需要替换的字符串
        String exchangeStringx = ""; //用来存放需要替换的字符串
        String strTemp1="";
        while ((byteRead = in.read()) != -1) {

            intTo16String = para.HexStringTable[byteRead];
            stringBuilder.append(intTo16String);
            windowIndex++;//自增
            if(windowIndex%lenOfWindow==0){
                //找到新切点
                strTemp1 = stringBuilder.toString();
                dataTypeOfRsync.adler32 = (int)(fp.getAdler32_1(fp.hexToByteArray(strTemp1),lenOfWindow)&0x7fffffff);
                if (dataStore.contains(dataTypeOfRsync.adler32)){
                    dataTypeOfRsync.MD5 = fp.getMD5(strTemp1);
                    if (dataStore.contains(dataTypeOfRsync.MD5)){
                        len = stringBuilderDiff.length();
                        stringBuilder.delete(0,stringBuilder.length());
                        stringBuilderDiff.delete(0,stringBuilderDiff.length());
                        diffNum = diffNum+len;
                        windowIndex=0;
                    }else{
                        //命中弱校验，但是没有命中强校验，后移一位
                        windowIndex--;
                        stringBuilder.getChars(0,2,charArray,0);
                        stringBuilderDiff.append(charArray);
                        stringBuilder.delete(0,2);
                    }
                }else{
                    //没有命中弱校验，后移一位
                    windowIndex--;
                    stringBuilder.getChars(0,2,charArray,0);
                    stringBuilderDiff.append(charArray);
                    stringBuilder.delete(0,2);
                }

                blockIndex++;
            }
            fileIndex=(fileIndex+1)&0xfffffff;//防止超出int范围;
        }
        diffNum = diffNum + stringBuilderDiff.length()+stringBuilder.length(); //最后没有分块的加上
        stringBuilder.delete(0,stringBuilder.length());
        stringBuilderDiff.delete(0,stringBuilderDiff.length());
        endTime = currentTimeMillis();
        System.out.printf("Time to CheckSame with Rsync on %s: %d ms. Number of diff bytes: %d.\n",fileToCheck,endTime-startTime,diffNum/2);
        System.out.println("End of CheckSame by Rsync!");

        return;
    }
}
