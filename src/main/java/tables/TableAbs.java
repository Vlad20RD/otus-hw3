package tables;

import db.MySqlDbExecutor;

import java.util.Locale;


public abstract class TableAbs {

  private static MySqlDbExecutor dbExecutor;

  public TableAbs(String dbType) {
    switch(dbType.toLowerCase(Locale.ROOT)) {
      case "mysql": {
        dbExecutor = new MySqlDbExecutor();
        break;
      }
    }
  }

}
