package CommonFunc;

import java.util.HashSet;

/**
 * Created by Administrator on 2019/4/8.
 */
public class DataStoreStringHashSet {
    private HashSet<String> dataSet;
    {
        dataSet = new HashSet<String>(2700000);
    }
    public boolean add(String temp){
        dataSet.add(temp);
        return true;
    }
    public boolean clear(){
        dataSet.clear();
        return true;
    }
    public boolean contains(String temp){
        return dataSet.contains(temp);
    }
}
