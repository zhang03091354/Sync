package CommonFunc;

/**
 * Created by Administrator on 2019/12/21 0021.
 */
public class DataTypeRsyncChecksum {
    public int adler32;
    public String MD5;

    public DataTypeRsyncChecksum(int adler32, String MD5){
        this.adler32 = adler32;
        this.MD5 = MD5;
    }
    public DataTypeRsyncChecksum(){
        this.adler32 = 0;
        this.MD5 = "";
    }
}
