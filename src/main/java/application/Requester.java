package application;

import config.Config;
import queries.Queries;
import queries.TypeSearchBook;

import java.sql.*;

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

    public ResultSet librarianQueries(String query, String[] args) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = Queries.getLibrarianQuerySql(query);
        return statement.executeQuery(String.format(sql, args));
    }

    public ResultSet popularCompositions() throws SQLException {
        Statement statement = connection.createStatement();
        String sql = Queries.popularCompositions;
        return statement.executeQuery(sql);
    }

    public void regComposition(int id, String name, String author, String category) throws SQLException {
        String sql = String.format(Queries.regComposition, id, name, author, category);
        connection.createStatement().executeQuery(sql);
    }

    public void regBook(int id, String name, String type) throws SQLException {
        String sql = String.format(Queries.regBook, id, name, type);
        connection.createStatement().executeQuery(sql);
    }

    public void regContent(int id, int book, int composition, int start, int end) throws SQLException {
        String sql = String.format(Queries.regContent, id, book, composition, start, end);
        connection.createStatement().executeQuery(sql);
    }

    public void regBibliofond(int id, int book, Date dateReg, int time, int hall, int rack, int shelf) throws SQLException {
        String sql = String.format(Queries.regBibliofond, id, book, dateReg, time, hall, rack, shelf);
        connection.createStatement().executeQuery(sql);
    }
}
