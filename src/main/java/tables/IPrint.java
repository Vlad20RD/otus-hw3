package tables;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;



public interface IPrint {
    void response(String requestText, List<Map<String, String>> requestResultList);
    void responseCount(String requestText, String columnNameCount, ResultSet requestResult);
    void responseCreateTable(String requestText, String tableName);
    void responseDropTable(String requestText, String tableName);
    void responseUpdateTable(String requestText, String tableName);
}
