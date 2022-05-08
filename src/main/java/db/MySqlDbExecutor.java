package db;

import utils.ReadPropertiesFromPropsFile;

import java.sql.*;
import java.util.Properties;

public class MySqlDbExecutor {

  private static Connection connect = null;
  private static Statement statement = null;

  public ResultSet executeQuery(String sqlRequest) {
    ReadPropertiesFromPropsFile readerProps = new ReadPropertiesFromPropsFile();
    Properties properties = readerProps.read();

    ResultSet resultSet = null;

    try {
      connect = DriverManager.getConnection(
          properties.getProperty("url") + "/" + properties.getProperty("db_name"),
          properties.getProperty("username"),
          properties.getProperty("password")
      );
      statement = connect.createStatement();
      resultSet = statement.executeQuery(sqlRequest);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return resultSet;
  }

  public void executeUpdate(String sqlRequest) {
    ReadPropertiesFromPropsFile readerProps = new ReadPropertiesFromPropsFile();
    Properties properties = readerProps.read();

    try {
      connect = DriverManager.getConnection(
              properties.getProperty("url") + "/" + properties.getProperty("db_name"),
              properties.getProperty("username"),
              properties.getProperty("password")
      );
      statement = connect.createStatement();
      statement.executeUpdate(sqlRequest);
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
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
