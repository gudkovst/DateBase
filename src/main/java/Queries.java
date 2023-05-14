package queries;

import javax.swing.plaf.PanelUI;

public class Queries {
    public static String readerFormularOn = "WITH INTEREST_READER AS (SELECT ID, Library FROM Readers " +
            "WHERE SecondName = '%s' AND Name = '%s')," +
            "LIBR AS (SELECT Librarians.ID AS Librarian, Libraries.ID AS Library " +
            "FROM Librarians JOIN Halls ON Librarians.HallNumber = Halls.ID JOIN Libraries ON Halls.Library = Libraries.ID)," +
            "TAKE_BY_READER AS ( SELECT Book, Librarian FROM TakeRegistration" +
            "WHERE Reader IN (SELECT ID FROM INTEREST_READER) AND Dateop BETWEEN '20.03.20' AND '20.05.20')," +
            "EDITIONS AS (SELECT Book FROM TAKE_BY_READER" +
            "WHERE Librarian IN (SELECT Librarian FROM LIBR JOIN INTEREST_READER USING (Library)))" +
            "SELECT DISTINCT Books.Name FROM Books JOIN Bibliofond ON Books.ID = Bibliofond.Book" +
            "WHERE Bibliofond.Book IN (SELECT * FROM EDITIONS)";
    public static String readerFormularOut = "WITH INTEREST_READER AS (SELECT ID, Library FROM Readers" +
            "WHERE SecondName = '%s' AND Name = '%s')," +
            "LIBR AS (SELECT Librarians.ID AS Librarian, Libraries.ID AS Library " +
            "FROM Librarians JOIN Halls ON Librarians.HallNumber = Halls.ID JOIN Libraries ON Halls.Library = Libraries.ID)," +
            "TAKE_BY_READER AS (SELECT Book, Librarian FROM TakeRegistration" +
            "WHERE Reader IN (SELECT ID FROM INTEREST_READER) AND Dateop BETWEEN '20.03.20' AND '20.05.20')," +
            "EDITIONS AS (SELECT Book FROM TAKE_BY_READER" +
            "WHERE Librarian NOT IN (SELECT Librarian FROM LIBR JOIN INTEREST_READER USING (Library)))" +
            "SELECT DISTINCT Books.Name FROM Books JOIN Bibliofond ON Books.ID = Bibliofond.Book" +
            "WHERE Bibliofond.Book IN (SELECT * FROM EDITIONS)";

    public static String searchBookName = "WITH COMPOSITIONS AS (SELECT ID FROM Composition WHERE Name = '%s')," +
            "BOOKS_WITH_COMP AS (SELECT Book FROM ContentBooks WHERE Composition IN (SELECT * FROM COMPOSITIONS))" +
            "SELECT NomenclatureNumber, Name FROM Bibliofond JOIN Books ON Bibliofond.Book = Books.ID" +
            "WHERE Books.ID IN (SELECT * FROM BOOKS_WITH_COMP)";

    public static String searchBookAuthor = "WITH COMPOSITIONS AS (SELECT ID FROM Composition WHERE Author = '%s')," +
            "BOOKS_WITH_COMP AS (SELECT Book FROM ContentBooks WHERE Composition IN (SELECT * FROM COMPOSITIONS))" +
            "SELECT NomenclatureNumber, Name FROM Bibliofond JOIN Books ON Bibliofond.Book = Books.ID" +
            "WHERE Books.ID IN (SELECT * FROM BOOKS_WITH_COMP)";

    public static String libraries = "SELECT Name FROM Libraries";

    public static String registrationReader = "INSERT INTO READERS VALUES(%s, %s, %s, %s, %s)";
    public static String regScientist = "INSERT INTO SCIENTIST VALUES(%s, %s, %s, %s)";
    public static String regStudent = "INSERT INTO STUDENTS VALUES(%s, %s, %s, %s, %s)";
    public static String regTeacher = "INSERT INTO TEACHERS VALUES(%s, %s, %s)";
    public static String regProletarian = "INSERT INTO PROLETARIANS VALUES(%s, %s, %s)";
    public static String regPensioner = "INSERT INTO PENSIONERS VALUES(%s)";
    public static String regLibrarian = "INSERT INTO LIBRARIANS VALUES(%s, %s, %s, %s, %s)";
}
