package db;

import utils.ReadPropertiesFromPropsFile;
import java.sql.*;
import java.util.Properties;



public class MySqlDbExecutor {

  private static Connection connect = null;
  private static Statement statement = null;

  public Statement getStatement() {
    ReadPropertiesFromPropsFile readerProps = new ReadPropertiesFromPropsFile();
    Properties properties = readerProps.read();

    try {
      connect = DriverManager.getConnection(
              properties.getProperty("url") + "/" + properties.getProperty("db_name"),
              properties.getProperty("username"),
              properties.getProperty("password")
      );
      statement = connect.createStatement();
    } catch (Exception ex) {
      System.err.println(ex.getClass() + ": Не удалось подключиться к БД");
      throw new RuntimeException();
    }

    return statement;
  }

  public void close() {
    try {
      connect.close();
      statement.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}
