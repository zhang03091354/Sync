package CommonFunc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2019/12/21 0021.
 */
public class DataStoreChecksumArray {
    private List<List<Integer>> myList  = new ArrayList<List<Integer>>();
    private HashSet<String> dataSet;
    public DataStoreChecksumArray(){
        dataSet = new HashSet<String>(2700000);
        for(int i=0;i<216;i++){
            List<Integer> listTemp = new ArrayList<Integer>();
            myList.add(listTemp);
        }
    }
    public boolean add(DataTypeRsyncChecksum temp){
        dataSet.add(temp.MD5);
        int in = temp.adler32%216;//
        List<Integer> listTemp = myList.get(in);
        listTemp.add(temp.adler32);
        myList.set(in,listTemp);
        return true;
    }
    public boolean clear(){
        dataSet.clear();
        myList.clear();
        return true;
    }
    public boolean contains(DataTypeRsyncChecksum temp){
        int in = temp.adler32%216;
        List<Integer> listTemp = myList.get(in);
        if(listTemp.contains(temp.adler32)){
            return dataSet.contains(temp.MD5);
        }else{
            return false;
        }
    }
    public boolean contains(int adler32){
        int in = adler32%216;
        List<Integer> listTemp = myList.get(in);
        return listTemp.contains(adler32);
    }
    public boolean contains(String MD5){
        return dataSet.contains(MD5);
    }
}
