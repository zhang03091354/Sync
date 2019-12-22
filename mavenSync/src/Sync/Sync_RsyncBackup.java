package Sync;

import NettySelf.NettyClient.SyncClient.SyncPciClient;
import NettySelf.NettyClient.SyncClient.SyncRsyncClient;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Administrator on 2019/12/16 0016.
 */
public class Sync_RsyncBackup {
    private int lenOfWindow;
    private String fileName;
    private Sync_Parameters para;
    private Sync_fingerprint fp;
    private SyncRsyncClient rsyncClient;
    private StringBuilder stringBuilder;
    private byte[] byteAdler32;
    private int adler32Value;

    public Sync_RsyncBackup(){
        para=new Sync_Parameters();
        fp=new Sync_fingerprint();
        rsyncClient=new SyncRsyncClient();
        stringBuilder = new StringBuilder("");
        lenOfWindow=para.lenOfWindow;
        byteAdler32 = new byte[lenOfWindow];
        fileName=para.fileBackup;
        rsyncClient.run();
    }

    public void DoSync_Rsync(){
        deBlock();
    }
    private void deBlock(){
        try {
            InputStream fileIn = new FileInputStream(fileName);
            BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
            deBlockFromInputBuffer(bufferIn);
            bufferIn.close();
            fileIn.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    private void deBlockFromInputBuffer(BufferedInputStream in) throws IOException {
        long startTime,endTime;
        startTime=currentTimeMillis();
        int byteRead; //读取的字节
        int blockIndex=0; //区块下标
        int fileIndex=0; //文件下标
        int startIndex=0;
        String intTo16String;
        String strTemp1="";
        int indexMod = 0;
        StringBuilder transformData = new StringBuilder("");


        while ((byteRead = in.read()) != -1) {

            intTo16String = para.HexStringTable[byteRead];
            stringBuilder.append(intTo16String);

            indexMod++;
            if(indexMod%lenOfWindow==0){
                //找到新切点
                strTemp1 = stringBuilder.toString();
                adler32Value = (int)(fp.getAdler32_1(fp.hexToByteArray(strTemp1),lenOfWindow)&0x7fffffff); //牺牲一位，转换成int
                transformData.append(Integer.toString(adler32Value)+",");
                transformData.append(fp.getMD5(strTemp1)+";");
                stringBuilder.delete(0,stringBuilder.length());

                blockIndex++;
                indexMod=0;
            }
            fileIndex=(fileIndex+1)&0xfffffff;//防止超出int范围;
        }
        endTime = currentTimeMillis();
        System.out.printf("Time to deBlock with Rsync: %d ms. Number of chunks:%d \n",endTime-startTime,blockIndex);
        rsyncClient.sendData(transformData.toString()+"end;");
        System.out.println("End of deblock by Rsync!");

        return;
    }
}
