import CommonFunc.DataStoreChecksumArray;
import CommonFunc.DataTypeRsyncChecksum;
import Sync.Sync_PciBackup;
import Sync.Sync_RsyncBackup;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2019/12/20 0020.
 */
public class TestList {
    public void testWrite(){
        String str = "sfda;fadsf;asdfasdfq1;2tr432t;";
        try{
            FileOutputStream fileOut = new FileOutputStream("e://abstractOfSrc"+".txt");
            BufferedOutputStream dataOut=new BufferedOutputStream(fileOut);
            str=str.replace(";","\r\n");
            System.out.println(str);
            dataOut.write((str).getBytes());
            dataOut.flush();
            dataOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pciSync_backup(){
        Sync_PciBackup handle = new Sync_PciBackup();
        handle.DoSync_Pci();
    }
    public void rsyncSync_backup(){
        Sync_RsyncBackup handle = new Sync_RsyncBackup();
        handle.DoSync_Rsync();
    }

    public void checkRsyncDataStore(){
        DataTypeRsyncChecksum test = new DataTypeRsyncChecksum(123,"test");
        DataStoreChecksumArray testStore = new DataStoreChecksumArray();
        testStore.add(test);
        boolean res = testStore.contains(test);
        System.out.println(res);
        return;
    }

    public void testString(){
        char[] cA=new char[100];
        String test = "abcdefg,hijklmnss";
        String str1="",str2="";
        test.getChars(0,10,cA,0);
        str1=String.valueOf(cA);
        System.out.println(str1);
        test.getChars(0,3,cA,0);
        str2=String.valueOf(cA);
        System.out.println(str1);
        System.out.println(str2);
        int len = test.length();
        System.out.println(len);
        return;
    }
    public void testHexToByte(){
        System.out.println(Integer.parseInt("ff",16));
        System.out.println((byte)Integer.parseInt("ff",16));
    }
}
