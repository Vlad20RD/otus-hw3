import tables.Table;

public class Main {
  public static void main(String... args) {
    new Table("mysql").dropTable("student");
    new Table("mysql").dropTable("student_group");
    new Table("mysql").dropTable("curator");
    new Table("mysql").createTable("curator", "id int not null primary key, fio char(40)");
    new Table("mysql").createTable("student_group", "id int not null primary key, name char(20), id_curator int not null, foreign key (id_curator) references curator(id)");
    new Table("mysql").createTable("student", "id int not null primary key, fio char(40), sex char(10), id_group int not null, foreign key (id_group) references student_group(id)");
    new Table("mysql").insertDataIntoTable("curator", 4);
    new Table("mysql").insertDataIntoTable("student_group", 3);
    new Table("mysql").insertDataIntoTable("student", 15);
    new Table("mysql").request(new String[]{"student.fio", "sex", "curator.fio", "student_group.name"}, "student", "JOIN curator ON student.id_group = curator.id JOIN student_group ON student.id_group = student_group.id;");
    new Table("mysql").request(new String[]{"COUNT(*) AS count"}, "student", "");
    new Table("mysql").request(new String[]{"fio"}, "student", "WHERE sex = 'female';");
    new Table("mysql").updateDataIntoTable(new String[]{"fio"}, new String[]{"Ivan Ivanov"}, "curator", "WHERE id=3;");
    new Table("mysql").request(new String[]{"name", "fio"}, "student_group", "JOIN curator ON student_group.id = curator.id;");
    new Table("mysql").request(new String[]{"fio", "name"}, "student", "JOIN student_group ON student.id_group = student_group.id WHERE student_group.name='Group 1';");
  }
}
