package tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;



public class ResponseConsole implements IPrint{

    //Вывод результата запроса
    public void response(String requestText, List<Map<String, String>> requestResultList) {
        System.out.println("Запрос:\n" + requestText + "\n\nРезультат запроса:");

        for(Map<String, String> r : requestResultList){
            for (Map.Entry<String, String> m : r.entrySet()){
                System.out.println(m.getKey() + ":  " + m.getValue());
            }
        }

        System.out.print("\n\n\n");
    }

    //Вывод результата запроса count(*)
    public void responseCount(String requestText, String columnNameCount, ResultSet requestResult) {
        System.out.println("Запрос:\n" + requestText + "\n\nРезультат запроса:");
        try {
            System.out.println(columnNameCount + ":  " + requestResult.getString(columnNameCount));
        } catch (SQLException ex){
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    //Вывод результата запроса по добавлению таблицы
    public void responseCreateTable(String requestText, String tableName) {
        System.out.println(String.format("Запрос:\n%s\n\nРезультат запроса:", requestText));
        System.out.println(String.format("Добавлена таблица \"%s\";\n\n\n", tableName));
    }

    //Вывод результата запроса по удалению таблицы
    public void responseDropTable(String requestText, String tableName) {
        System.out.println(String.format("Запрос:\n%s\n\nРезультат запроса:", requestText));
        System.out.println(String.format("Удалена таблица \"%s\";\n\n\n", tableName));
    }

    //Вывод результата запроса по обновлению таблицы
    public void responseUpdateTable(String requestText, String tableName) {
        System.out.println(String.format("Запрос:\n%s\n\nРезультат запроса:", requestText));
        System.out.println(String.format("Таблица \"%s\" обновлена;\n\n\n", tableName));
    }
}
