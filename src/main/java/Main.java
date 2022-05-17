import tables.Table;



public class Main {
  public static void main(String... args) {
    Table tableOtus = new Table("mysql");

    tableOtus.dropTable("student");
    tableOtus.dropTable("student_group");
    tableOtus.dropTable("curator");
    tableOtus.createTable("curator", "id int not null primary key, fio char(40)");
    tableOtus.createTable("student_group", "id int not null primary key, name char(20), id_curator int not null, foreign key (id_curator) references curator(id)");
    tableOtus.createTable("student", "id int not null primary key, fio char(40), sex char(10), id_group int not null, foreign key (id_group) references student_group(id)");
    tableOtus.insertDataIntoTable("curator", 4);
    tableOtus.insertDataIntoTable("student_group", 3);
    tableOtus.insertDataIntoTable("student", 15);
    tableOtus.request(new String[]{"student.fio", "sex", "curator.fio", "student_group.name"}, "student", "JOIN curator ON student.id_group = curator.id JOIN student_group ON student.id_group = student_group.id;");
    tableOtus.requestCount("COUNT(*) AS count", "student");
    tableOtus.request(new String[]{"fio"}, "student", "WHERE sex = 'female';");
    tableOtus.updateDataIntoTable(new String[]{"fio"}, new String[]{"Ivan Ivanov"}, "curator", "WHERE id=3;");
    tableOtus.request(new String[]{"name", "fio"}, "student_group", "JOIN curator ON student_group.id = curator.id;");
    tableOtus.request(new String[]{"fio", "name"}, "student", "JOIN student_group ON student.id_group = student_group.id WHERE student_group.name='Group 1';");
  }
}
