package tables;

import com.github.javafaker.Faker;
import db.MySqlDbExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Table extends TableAbs {

  IPrint responseInConsoleObj = new ResponseConsole();

  public Table(String dbType) {
    super(dbType);
  }

  //Выполнение запросов
  public void request(String[] columns, String tableName, String predicats) {

    //Если в массиве columns принимаем * , то в него записываем резуьтат метода getAllColumns()
    if (columns.length == 1 && columns[0].equals("*")){
      columns = getAllColumns(tableName).toArray(new String[0]);
    }

    //Формирование строки запроса и выполнение запроса
    String requestText = String.format("SELECT %s FROM %s %s", Arrays.toString(columns).replaceAll("^\\[|\\]$", ""), tableName, predicats);
    ResultSet requestResult = null;
    try {
      requestResult = new MySqlDbExecutor().getStatement().executeQuery(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }

    List<Map<String, String>> requestResultList = new ArrayList<>();

    //Резуьтат запроса
    try {
      while (requestResult.next()) {
        for (String column : columns) {
          Map<String, String> map = new HashMap<>();
          map.put(column, requestResult.getString(column));
          requestResultList.add(map);
        }
      }
      responseInConsoleObj.response(requestText, requestResultList);
    } catch (SQLException ex){
      ex.printStackTrace();
      throw new RuntimeException();
    }

    new MySqlDbExecutor().close();
  }

  //Выполнение запроса count
  public void requestCount(String column, String tableName) {

    //Формирование строки запроса и выполнение запроса
    String requestText = String.format("SELECT %s FROM %s;", column, tableName);
    ResultSet requestResult = null;
    try {
      requestResult = new MySqlDbExecutor().getStatement().executeQuery(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }

    String columnNameCount = "COUNT(*)";

    if(column.contains("AS")){
      columnNameCount = column.substring(column.indexOf("AS") + "AS".length()).trim();
    }

    try {
      while (requestResult.next()){
        responseInConsoleObj.responseCount(requestText, columnNameCount, requestResult);
      }
      System.out.print("\n\n");
    } catch (SQLException ex){
      ex.printStackTrace();
    }

    new MySqlDbExecutor().close();
  }



  //Добавление данных в таблицу
  public void insertDataIntoTable(String tableName, int numberOfLines) {
    Faker faker = new Faker(new Locale("en"));
    Map<String, String> map = getColumnAndType(tableName);

    //Цикл в котором выполняется количество запросов по добавлению данных в таблицу
    for(int i = 1; i<(numberOfLines+1); i++) {
      List<String> valuesList = new ArrayList<>();

      for (Map.Entry<String, String> m : map.entrySet()){
        String bindingColumnName = getBindingColumnName(tableName);

        if(m.getKey().equals("fio") && m.getValue().contains("char(40)")){
          valuesList.add("\"" + faker.name().fullName() + "\"");
        }
        else if(m.getKey().equals("id") && m.getValue().contains("int")){
          valuesList.add(Integer.toString(i));
        }
        else if(m.getKey().equals("name") && m.getValue().contains("char(20)")){
          valuesList.add(String.format("\"Group %s\"", i) );
        }
        else if(m.getKey().equals("sex") && m.getValue().contains("char(10)")){
          String[] sex = {"male", "female"};
          valuesList.add(String.format("\"%s\"", sex[new Random().nextInt(sex.length)]));
        }
        else if (!bindingColumnName.isEmpty()) {
          String[] valuesConnectingColumnArray = getConnectingColumn(tableName).toArray(new String[0]);

          if(m.getKey().equals(bindingColumnName)){
            valuesList.add(String.format("\"%s\"", valuesConnectingColumnArray[new Random().nextInt(valuesConnectingColumnArray.length)]));
          }
        }
      }

      //После генерации данных выполянется формирование строки запроса и выполнение запроса
      String requestText = String.format("INSERT %s VALUE (%s);", tableName, Arrays.toString((valuesList.toArray(new String[0]))).replaceAll("^\\[|\\]$", ""));
      System.out.println(String.format("Запрос:\n%s\n\nРезультат запроса:", requestText));
      try {
        new MySqlDbExecutor().getStatement().executeUpdate(requestText);
      } catch (SQLException throwables) {
        throwables.printStackTrace();
        throw new RuntimeException();
      }
      System.out.println("Данные добавлены в таблицу\n\n\n");
    }
    System.out.println(String.format("%s запрос/а/ов по добавлению данных в таблицу %s прош/ли/ел успешно\n\n\n", numberOfLines, tableName));
    new MySqlDbExecutor().close();
  }

  //Обновление данных в таблице
  public void updateDataIntoTable(String[] columns, String[] values, String tableName, String predicats) {

    List<String> columnsAndValueslist = new ArrayList<>();

    for (int i = 0; i < columns.length; i++) {
      columnsAndValueslist.add(String.format("%s='%s'", columns[i], values[i]));
    }

    String[] columnsAndValuesArray = columnsAndValueslist.toArray(new String[0]);

    //Формирование строки запроса и выполнение запроса
    String requestText = String.format("UPDATE %s SET %s %s", tableName, Arrays.toString(columnsAndValuesArray).replaceAll("^\\[|\\]$", ""), predicats);
    try {
      new MySqlDbExecutor().getStatement().executeUpdate(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    responseInConsoleObj.responseUpdateTable(requestText, tableName);
    new MySqlDbExecutor().close();
  }

  //Создание таблицы
  public void createTable(String tableName, String columns) {
    String requestText = String.format("CREATE TABLE %s (%s);", tableName, columns);
    try {
      new MySqlDbExecutor().getStatement().executeUpdate(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    responseInConsoleObj.responseCreateTable(requestText, tableName);
    new MySqlDbExecutor().close();
  }

  //Удаление таблицы
  public void dropTable(String tableName) {
    if(tablesList().contains(tableName)){
      String requestText = String.format("DROP TABLE %s;", tableName);
      try {
        new MySqlDbExecutor().getStatement().executeUpdate(requestText);
      } catch (SQLException throwables) {
        throwables.printStackTrace();
        throw new RuntimeException();
      }
      responseInConsoleObj.responseDropTable(requestText, tableName);
      new MySqlDbExecutor().close();
    }
  }



  //Получение колонки с MUL
  public String getBindingColumnName(String tableName) {
    String requestText = String.format("DESCRIBE %s", tableName);
    ResultSet resultRequest = null;
    try {
      resultRequest = new MySqlDbExecutor().getStatement().executeQuery(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    List<String> columnKeyList = new ArrayList<>();
    List<String> columnNameList = new ArrayList<>();

    int size = 1;
    String columnKey = "";
    try {
      while (resultRequest.next()){
        columnKeyList.add(resultRequest.getString(4));
        columnNameList.add(resultRequest.getString(1));
      }

      if(columnKeyList.contains("MUL")){
        for (String ckl : columnKeyList){
          if(!ckl.equals("MUL")){
            size++;
          }
          columnKey = columnNameList.get(size - 1);
        }
      }

    } catch (SQLException ex){
      ex.printStackTrace();
    } finally {
      new MySqlDbExecutor().close();
    }

    return columnKey;
  }

  //Получение связующей таблицы и колонки
  //В результате возвращается список значений из связующей таблицы и связующей колонки
  public List<String> getConnectingColumn(String tableName) {
    String requestText = String.format("SELECT * FROM information_schema.key_column_usage WHERE referenced_table_schema='otus' AND table_name='%s';", tableName);
    ResultSet resultRequest = null;
    try {
      resultRequest = new MySqlDbExecutor().getStatement().executeQuery(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    String tableNameKey = "";
    String columnNameKey = "";

    try {
      while (resultRequest.next()){
        tableNameKey = resultRequest.getString("REFERENCED_TABLE_NAME");
        columnNameKey = resultRequest.getString("REFERENCED_COLUMN_NAME");
      }
    } catch (SQLException ex){
      ex.printStackTrace();
    } finally {
      new MySqlDbExecutor().close();
    }

    return getColumnValues(columnNameKey, tableNameKey);
  }

  //Получить все значения колонки
  public List<String> getColumnValues(String columnName, String tableName) {
    String requestText = String.format("SELECT %s FROM %s;", columnName, tableName);
    ResultSet resultRequest = null;
    try {
      resultRequest = new MySqlDbExecutor().getStatement().executeQuery(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    List<String> columnValuesList = new ArrayList<>();

    try {
      while (resultRequest.next()){
        columnValuesList.add(resultRequest.getString(columnName));
      }
    } catch (SQLException ex){
      ex.printStackTrace();
    } finally {
      new MySqlDbExecutor().close();
    }

    return columnValuesList;
  }

  //Получение всех колонок таблицы и их тип
  public Map<String, String> getColumnAndType(String tableName) {
    String requestText = String.format("DESCRIBE %s", tableName);
    ResultSet resultRequest = null;
    try {
      resultRequest = new MySqlDbExecutor().getStatement().executeQuery(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    Map<String, String> map = new LinkedHashMap<>();

    try {
      while (resultRequest.next()){
        map.put(resultRequest.getString(1), resultRequest.getString(2));
      }
    } catch (SQLException ex){
      ex.printStackTrace();
    } finally {
      new MySqlDbExecutor().close();
    }

    return map;
  }

  //Получение всех колонок таблицы
  private List<String> getAllColumns(String tableName) {
    ResultSet requestResult = null;
    try {
      requestResult = new MySqlDbExecutor().getStatement().executeQuery(String.format("SELECT * FROM %s", tableName));
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    List<String> requestResultList = new ArrayList<>();

    try {
      int size = 1;
      while (requestResult.next()){
        if(!requestResult.getMetaData().getColumnName(size).isEmpty()) {
          requestResultList.add(requestResult.getMetaData().getColumnName(size));
          size++;
        }
      }
    } catch(SQLException ex) {
      ex.printStackTrace();
    } finally {
      new MySqlDbExecutor().close();
    }
    return requestResultList;
  }

  //Список таблиц
  public List<String> tablesList() {
    String requestText = "SHOW TABLES;";
    ResultSet requestResult = null;
    try {
      requestResult = new MySqlDbExecutor().getStatement().executeQuery(requestText);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      throw new RuntimeException();
    }
    List<String> requestResultList = new ArrayList<>();

    try {
      while (requestResult.next()){
        requestResultList.add(requestResult.getString("Tables_in_otus"));
      }
    } catch (SQLException ex){
      ex.printStackTrace();
    } finally {
      new MySqlDbExecutor().close();
    }

    return requestResultList;
  }

}
