package c3p0;

/**
 * Created by Administrator on 2019/12/18 0018.
 */
public class TestMain {
    public static void main(String[] args) throws Exception{
        DBUtil_BO dbBo = new DBUtil_BO();
        dbBo.conn = C3p0Utils.getConnection();
        String sql = "select * from test";
        try {
            dbBo.st=dbBo.conn.prepareStatement(sql);
        }finally {

        }
        DBUtils.executeQuery(dbBo);
        //从dbBo类提取操作结果
        while (dbBo.rs.next()) {
            String index = dbBo.rs.getString("index");
            String value = dbBo.rs.getString("value");
            System.out.println(index+value);
        }
        //结果集遍历完了，手动释放连接回连接池
        DBUtils.realseSource(dbBo);
    }
}
