package Sync;

import NettySelf.NettyClient.SyncClient.SyncPciClient;

import java.io.*;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Administrator on 2019/12/16 0016.
 */
public class Sync_PciBackup {
    private int lenOfWindow;
    private String fileName;
    private Sync_Parameters para;
    private Sync_fingerprint fp;
    private SyncPciClient pciClient;
    private StringBuilder stringBuilder;

    private int[] valuesInWindow;
    private int[] parityCheck;//一个字节包含多少个1
    private  int thresholdVaule;
    {
        para=new Sync_Parameters();
        fp=new Sync_fingerprint();
        pciClient=new SyncPciClient();
        stringBuilder = new StringBuilder("");
        pciClient.run();
        lenOfWindow=10;
        fileName=para.fileBackup;
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

    public void DoSync_Pci(){
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
        StringBuilder transformData = new StringBuilder("");

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

                transformData.append(fp.getMD5(stringBuilder.toString())+";");
                stringBuilder.delete(0,stringBuilder.length());

                blockIndex++;
                numOfParity=0;
                bytesNumInArray=0;
            }
            fileIndex++;//=(fileIndex+1)&0xfffffff;//防止超出int范围;
        }
        endTime = currentTimeMillis();
        System.out.printf("Time to deBlock with PCI: %d ms. Number of chunks:%d \n",endTime-startTime,blockIndex);
        pciClient.sendData(transformData.toString()+"end;");
        System.out.println("End of deblock by PCI!");
        //pciClient.closeClient(); //直接关闭会导致数据没有传输完成就提前关闭了

        //把所有摘要写入文件中
//        try{
//            FileOutputStream fileOut = new FileOutputStream("e://abstractOfBackup"+".txt");
//            BufferedOutputStream dataOut=new BufferedOutputStream(fileOut);
//            String strTemp = transformData.toString().replace(";","\r\n");
//            dataOut.write(strTemp.getBytes());
//            dataOut.flush();
//            dataOut.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        return;
    }
}
