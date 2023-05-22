package application;

import config.Config;
import queries.Queries;
import queries.TypeSearchBook;

import java.sql.*;
import java.util.Map;

public class Requester {
    private Connection connection;

    public Requester(){
        Queries.setLibrarianQueries();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        connection = null;
        try{
            connection = DriverManager.getConnection(Config.serverURL, Config.serverLogin, Config.serverPass);
            if (connection == null)
                throw new SQLException("connection is null");
        } catch (SQLException e) {
            System.out.println("Нет подключения к серверу:" + e);
            System.exit(0);
        }
    }

    public ResultSet requestBooks(TypeSearchBook typeRequest, String data) throws SQLException {
        String sql = null;
        if (typeRequest == TypeSearchBook.NAME)
            sql = String.format(Queries.searchBookName, data);
        if (typeRequest == TypeSearchBook.AUTHOR)
            sql = String.format(Queries.searchBookAuthor, data);
        return connection.createStatement().executeQuery(sql);
    }

    public ResultSet libraries() throws SQLException {
        return connection.createStatement().executeQuery(Queries.libraries);
    }
    
    public void registerReader(Integer id, String name, String secondName, Date birthdate, Integer library) throws SQLException {
        String sql = String.format(Queries.registrationReader, id, name, secondName, birthdate, library);
        connection.createStatement().executeQuery(sql);
    }

    public void regScientist(Integer id, String institution, String topic, String degree) throws SQLException {
        String sql = String.format(Queries.regScientist, id, institution, topic, degree);
        connection.createStatement().executeQuery(sql);
    }

    public void regStudent(Integer id, String university, String department, Integer course, Integer group) throws SQLException {
        String sql = String.format(Queries.regStudent, id, university, department, course, group);
        connection.createStatement().executeQuery(sql);
    }

    public void regTeacher(Integer id, String university, String degree) throws SQLException {
        String sql = String.format(Queries.regTeacher, id, university, degree);
        connection.createStatement().executeQuery(sql);
    }

    public void regProletarian(Integer id, String organization, String position) throws SQLException {
        String sql = String.format(Queries.regProletarian, id, organization, position);
        connection.createStatement().executeQuery(sql);
    }

    public void regPensioner(Integer id) throws SQLException {
        String sql = String.format(Queries.regPensioner, id);
        connection.createStatement().executeQuery(sql);
    }

    public void regLibrarian(Integer id, Integer hall, String name, String secondName, Date birthdate) throws SQLException {
        String sql = String.format(Queries.regLibrarian, id, hall, name, secondName, birthdate);
        connection.createStatement().executeQuery(sql);
    }

    public Statement librarianQueries(String query, String[] args) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = Queries.getLibrarianQuerySql(query);
        statement.executeQuery(String.format(sql, args));
        return statement;
    }
}
