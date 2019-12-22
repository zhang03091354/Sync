package Sync;

import CommonFunc.DataStoreStringHashSet;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Administrator on 2019/12/19 0019.
 */
public class Sync_PciSrc {
    private int lenOfWindow;
    private Sync_Parameters para;
    private Sync_fingerprint fp;
    private StringBuilder stringBuilder;
    public DataStoreStringHashSet dataStore;

    private int[] valuesInWindow;
    private int[] parityCheck;//一个字节包含多少个1
    private  int thresholdVaule;
    {
        para=new Sync_Parameters();
        fp=new Sync_fingerprint();
        stringBuilder = new StringBuilder("");
        dataStore= new DataStoreStringHashSet();
        lenOfWindow=10;
        valuesInWindow=new int[lenOfWindow+1];
        parityCheck=new int[256];
        for(int i=0;i<256;i++){
            for(int j=0;j<8;j++){
                int temp1 = i>>j;
                int temp2 = temp1&0x01;
                parityCheck[i]+=temp2;
            }
        }
        thresholdVaule=43;
    }

    public void DoSync_Pci2(){
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add(para.fileBackup);
        fileList.add(para.fileSrc_add);
        fileList.add(para.fileSrc_delete);
        fileList.add(para.fileSrc_insert);

        for(String testFile:fileList){
            deBlock(testFile);
        }
    }
    private void deBlock(String fileToCheck){
        try {
            InputStream fileIn = new FileInputStream(fileToCheck);
            BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
            deBlockFromInputBuffer(bufferIn,fileToCheck);
            bufferIn.close();
            fileIn.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    private void deBlockFromInputBuffer(BufferedInputStream in, String fileToCheck) throws IOException {
        long startTime,endTime;
        startTime=currentTimeMillis();
        int byteRead; //读取的字节
        int blockIndex=0; //区块下标
        int fileIndex=0; //文件下标
        int startIndex=0;
        String intTo16String;
        String transformData = "";
        long diffNum = 0; //差异数据的大小
        int len = 0;

        //PCI特殊
        int bytesNumInArray=0; //数组内的字节数
        int indexOfArray=0; //数组内数据下标，指向下一个要插入的位置
        int numOfParity=0; //数组内 1 的个数

        while ((byteRead = in.read()) != -1) {

            intTo16String = para.HexStringTable[byteRead];
            stringBuilder.append(intTo16String);

            valuesInWindow[indexOfArray]=parityCheck[byteRead];  //新读入的字节的奇偶校验值存入循环队列中
            indexOfArray++;  //指向下一个要写入的下标
            indexOfArray = indexOfArray%(lenOfWindow+1);
            //更新奇偶校验值，计算窗口内的所有字节中的位中，包含1的个数
            if(bytesNumInArray<lenOfWindow){
                numOfParity+=parityCheck[byteRead];
                bytesNumInArray++;
            }
            else{
                int temp1 = valuesInWindow[(indexOfArray+lenOfWindow)%(lenOfWindow+1)];
                int temp2 = valuesInWindow[indexOfArray];
                numOfParity=numOfParity+temp1-temp2;
            }
            if(numOfParity>=thresholdVaule&&bytesNumInArray>=lenOfWindow){
                //找到新切点

                transformData=fp.getMD5(stringBuilder.toString());
                //TODO 分析是否存在该分块，并计算checksum
                if(!dataStore.contains(transformData)){
                    len = stringBuilder.length();
                    //System.out.println(len);
                    diffNum = diffNum + len;
                }
                stringBuilder.delete(0,stringBuilder.length());

                blockIndex++;
                numOfParity=0;
                bytesNumInArray=0;
            }
            fileIndex++;//=(fileIndex+1)&0xfffffff;//防止超出int范围;
        }
        endTime = currentTimeMillis();
        System.out.printf("Time to CheckSame with PCI on %s: %d ms. Number of diff bytes: %d.\n",fileToCheck,endTime-startTime,diffNum/2);
        System.out.println("End of CheckSame by PCI!");
        return;
    }
}
