package c3p0;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Administrator on 2019/12/18 0018.
 */
public class DBUtil_BO {
    public Connection conn = null;
    public PreparedStatement st = null;
    public ResultSet rs = null;
    public DBUtil_BO() {
        super();
    }
}
