package queries;


import java.util.HashMap;
import java.util.Map;

public class Queries {

    private static Map<String, String[]> librarianQueriesArgs;

    public static Map<String, String[]> getLibrarianQueriesArgs() {
        return librarianQueriesArgs;
    }

    private static Map<String, String> librarianQueriesSql;
    public static String popularCompositions = "WITH COUNT_EDITIONS AS (SELECT Book, COUNT(*) AS Count" +
            " FROM TakeRegistration GROUP BY Book)," +
            " COUNT_BOOKS AS (SELECT Book, SUM(Count) AS Count FROM Bibliofond JOIN COUNT_EDITIONS" +
            " ON Bibliofond.NomenclatureNumber = COUNT_EDITIONS.Book GROUP BY Book)" +
            " SELECT Composition, SUM(Count) AS Count FROM ContentBooks JOIN COUNT_BOOKS USING (Book)" +
            " GROUP BY Composition ORDER BY Count DESC";
    public static String searchBookName = "WITH COMPOSITIONS AS (SELECT ID FROM Composition WHERE Name = '%s')," +
            " BOOKS_WITH_COMP AS (SELECT Book FROM ContentBooks WHERE Composition IN (SELECT * FROM COMPOSITIONS))" +
            " SELECT NomenclatureNumber, Name FROM Bibliofond JOIN Books ON Bibliofond.Book = Books.ID" +
            " WHERE Books.ID IN (SELECT * FROM BOOKS_WITH_COMP)";
    public static String searchBookAuthor = "WITH COMPOSITIONS AS (SELECT ID FROM Composition WHERE Author = '%s')," +
            " BOOKS_WITH_COMP AS (SELECT Book FROM ContentBooks WHERE Composition IN (SELECT * FROM COMPOSITIONS))" +
            " SELECT NomenclatureNumber, Name FROM Bibliofond JOIN Books ON Bibliofond.Book = Books.ID" +
            " WHERE Books.ID IN (SELECT * FROM BOOKS_WITH_COMP)";
    public static String libraries = "SELECT Name FROM Libraries";
    public static String registrationReader = "INSERT INTO READERS VALUES(%s, %s, %s, %s, %s)";
    public static String regScientist = "INSERT INTO SCIENTIST VALUES(%s, %s, %s, %s)";
    public static String regStudent = "INSERT INTO STUDENTS VALUES(%s, %s, %s, %s, %s)";
    public static String regTeacher = "INSERT INTO TEACHERS VALUES(%s, %s, %s)";
    public static String regProletarian = "INSERT INTO PROLETARIANS VALUES(%s, %s, %s)";
    public static String regPensioner = "INSERT INTO PENSIONERS VALUES(%s)";
    public static String regLibrarian = "INSERT INTO LIBRARIANS VALUES(%s, %s, '%s', '%s', to_date('%s', 'YYYY-MM-DD'))";


    public static void setLibrarianQueries(){
        librarianQueriesArgs = new HashMap<>();
        librarianQueriesSql = new HashMap<>();
        librarianQueriesArgs.put("Readers with given composition", new String[]{"composition"});
        librarianQueriesSql.put("Readers with given composition", readersWithComposition);
        librarianQueriesArgs.put("Readers with given book", new String[]{"composition"});
        librarianQueriesSql.put("Readers with given book", readersWithBook);
        librarianQueriesArgs.put("Books taken in given period", new String[]{"composition", "begin date", "end date"});
        librarianQueriesSql.put("Books taken in given period", takenInPeriod);
        librarianQueriesArgs.put("Formular of reader in his library", new String[]{"reader second name", "reader name", "begin date", "end date"});
        librarianQueriesSql.put("Formular of reader in his library", readerFormularOn);
        librarianQueriesArgs.put("Formular of reader out his library", new String[]{"reader second name", "reader name", "begin date", "end date"});
        librarianQueriesSql.put("Formular of reader out his library", readerFormularOut);
        librarianQueriesArgs.put("Books issued from given shelf", new String[]{"shelf number", "library number"});
        librarianQueriesSql.put("Books issued from given rack", booksIssuedFromGivenShelf);
        librarianQueriesArgs.put("Work given librarian in period", new String[]{"librarian second name", "librarian name", "begin date", "end date"});
        librarianQueriesSql.put("Work given librarian in period", workGivenLibrarianInPeriod);
        librarianQueriesArgs.put("Work all librarians in period", new String[]{"begin date", "end date"});
        librarianQueriesSql.put("Work all librarians in period", workAllLibrariansInPeriod);
        librarianQueriesArgs.put("Readers overdue his books", new String[0]);
        librarianQueriesSql.put("Readers overdue his books", getReaderDebtor);
        librarianQueriesArgs.put("Books received in period", new String[]{"begin date", "end date"});
        librarianQueriesSql.put("Books received in period", getNewBooks);
        librarianQueriesArgs.put("Librarians from given hall", new String[]{"hall number"});
        librarianQueriesSql.put("Librarians from given hall", librariansFromHall);
        librarianQueriesArgs.put("Readers who does not visit library in period", new String[]{"begin date", "end date"});
        librarianQueriesSql.put("Readers who does not visit library in period", readersNotVisitInPeriod);
    }

    public static String getLibrarianQuerySql(String query){
        return librarianQueriesSql.get(query);
    }
    private static String readersWithComposition = "WITH COMPOSITION AS (SELECT ID FROM Composition WHERE Name = '%s'" +
            " INTEREST_BOOKS AS (SELECT Book FROM ContentBooks WHERE Composition IN (SELECT * FROM COMPOSITION))," +
            " NOMENCLATURE AS (SELECT NomenclatureNumber FROM Bibliofond WHERE Book IN (SELECT * FROM INTEREST_BOOKS))," +
            " INTEREST_READERS AS (SELECT Reader FROM TakeRegistration LEFT JOIN ReturnRegistration USING (ID)" +
            " WHERE ReturnRegistration.ID IS NULL AND Book IN (SELECT * FROM NOMENCLATURE))" +
            " SELECT * FROM Readers WHERE ID IN (SELECT * FROM INTEREST_READERS)";
    private static String readersWithBook = "WITH EDITION AS (SELECT ID FROM Books WHERE Name = '%s')," +
            " NOMENCLATURE AS (SELECT NomenclatureNumber FROM Bibliofond WHERE Book IN (SELECT * FROM EDITION))," +
            " INTEREST_READERS AS (SELECT Reader FROM TakeRegistration LEFT JOIN ReturnRegistration USING (ID)" +
            " WHERE ReturnRegistration.ID IS NULL AND Book IN (SELECT * FROM NOMENCLATURE))" +
            " SELECT * FROM Readers WHERE ID IN (SELECT * FROM INTEREST_READERS)";
    private static String takenInPeriod = "WITH COMPOSITION AS (SELECT ID FROM Composition WHERE Name = '%s')," +
            " INTEREST_BOOKS AS (SELECT Book FROM ContentBooks WHERE Composition IN (SELECT * FROM COMPOSITION))," +
            " SELECT Readers.*, Books.Name FROM Books, TakeRegistration JOIN Readers ON TakeRegistration.Reader = Readers.ID" +
            " WHERE TakeRegistration.Dateop BETWEEN '%s' AND '%s'";
    private static String readerFormularOn = "WITH INTEREST_READER AS (SELECT ID, Library FROM Readers " +
            " WHERE SecondName = '%s' AND Name = '%s')," +
            " LIBR AS (SELECT Librarians.ID AS Librarian, Libraries.ID AS Library " +
            " FROM Librarians JOIN Halls ON Librarians.HallNumber = Halls.ID JOIN Libraries ON Halls.Library = Libraries.ID)," +
            " TAKE_BY_READER AS ( SELECT Book, Librarian FROM TakeRegistration" +
            " WHERE Reader IN (SELECT ID FROM INTEREST_READER) AND Dateop BETWEEN '%s' AND '%s')," +
            " EDITIONS AS (SELECT Book FROM TAKE_BY_READER" +
            " WHERE Librarian IN (SELECT Librarian FROM LIBR JOIN INTEREST_READER USING (Library)))" +
            " SELECT DISTINCT Books.Name FROM Books JOIN Bibliofond ON Books.ID = Bibliofond.Book" +
            " WHERE Bibliofond.Book IN (SELECT * FROM EDITIONS)";
    private static String readerFormularOut = "WITH INTEREST_READER AS (SELECT ID, Library FROM Readers" +
            " WHERE SecondName = '%s' AND Name = '%s')," +
            " LIBR AS (SELECT Librarians.ID AS Librarian, Libraries.ID AS Library " +
            " FROM Librarians JOIN Halls ON Librarians.HallNumber = Halls.ID JOIN Libraries ON Halls.Library = Libraries.ID)," +
            " TAKE_BY_READER AS (SELECT Book, Librarian FROM TakeRegistration" +
            " WHERE Reader IN (SELECT ID FROM INTEREST_READER) AND Dateop BETWEEN '%s' AND '%s')," +
            " EDITIONS AS (SELECT Book FROM TAKE_BY_READER" +
            " WHERE Librarian NOT IN (SELECT Librarian FROM LIBR JOIN INTEREST_READER USING (Library)))" +
            " SELECT DISTINCT Books.Name FROM Books JOIN Bibliofond ON Books.ID = Bibliofond.Book" +
            " WHERE Bibliofond.Book IN (SELECT * FROM EDITIONS)";
    private static String booksIssuedFromGivenShelf = "WITH BOOKS_IN_INTEREST_PLACE AS (SELECT NomenclatureNumber FROM Bibliofond" +
            " WHERE ShelfNumber = %s AND HallNumber IN (SELECT Halls.ID FROM Halls JOIN Libraries ON Halls.Library = Libraries.ID" +
            " WHERE Libraries.ID = 25))," +
            " TAKING_BOOKS AS (SELECT Book FROM TakeRegistration LEFT JOIN ReturnRegistration USING (ID)" +
            " WHERE ReturnRegistration.ID IS NULL AND Book IN (SELECT * FROM BOOKS_IN_INTEREST_PLACE))" +
            " SELECT DISTINCT Name FROM Books WHERE ID IN (SELECT * FROM TAKING_BOOKS)";
    private static String workGivenLibrarianInPeriod = "WITH INTEREST_LIBRARIAN AS (SELECT ID FROM Librarians" +
            " WHERE SecondName = '%s' AND Name = '%s')," +
            " TAKE_OPERS AS (SELECT Reader, Dateop FROM TakeRegistration WHERE Librarian IN (SELECT * FROM INTEREST_LIBRARIAN))," +
            " RET_OPERS AS (SELECT Reader, ReturnRegistration.Dateop AS Dateop FROM TakeRegistration JOIN ReturnRegistration USING (ID)" +
            " WHERE ReturnRegistration.Librarian IN (SELECT * FROM INTEREST_LIBRARIAN))" +
            " SELECT DISTINCT SecondName, Name" +
            " FROM (Readers JOIN TAKE_OPERS ON Readers.ID = TAKE_OPERS.Reader UNION Readers JOIN RET_OPERS ON  Readers.ID = RET_OPERS.Reader)\n" +
            " WHERE Dateop BETWEEN '%s' AND '%s'";
    private static String workAllLibrariansInPeriod = "WITH OPERS AS (SELECT Librarian, COUNT(*) AS COUNT_OPERS" +
            " FROM (SELECT ID, Librarian, Dateop FROM TakeRegistration UNION SELECT ID, Librarian, Dateop FROM ReturnRegistration)" +
            " WHERE Date BETWEEN '%s' AND '%s' GROUP BY Librarian)" +
            " SELECT SecondName, Name, OPERS.COUNT_OPERS FROM Librarians JOIN OPERS ON Librarians.ID = OPERS.Librarian";
    private static String getReaderDebtor = "WITH OVERDUE AS (SELECT Reader FROM TakeRegistration LEFT JOIN ReturnRegistration USING (ID)" +
            " WHERE ReturnRegistration.ID IS NULL AND DateReturn < CURRENT_DATE())" +
            " SELECT SecondName, Name FROM Readers WHERE ID IN (SELECT * FROM OVERDUE)";
    private static String getNewBooks = "WITH APPLY AS (SELECT Book FROM Bibliofond WHERE DateRegistration BETWEEN '%s' AND '%s')" +
            " SELECT DISTINCT Name FROM Books WHERE ID IN (SELECT * FROM APPLY)";
    private static String librariansFromHall = "SELECT SecondName, Name FROM Librarians WHERE HallNumber = %s";
    private static String readersNotVisitInPeriod = "WITH READERS_IN_PERIOD AS (SELECT Readers FROM (SELECT ID, Readers, Dateop" +
            " FROM TakeRegistration UNION SELECT ID, Readers, ReturnRegistration.Dateop AS Dateop" +
            " FROM TakeRegistration JOIN ReturnRegistration USING (ID)) WHERE Dateop BETWEEN '%s' AND '%s')" +
            " SELECT SecondName, Name FROM Readers WHERE ID NOT IN (SELECT * FROM READERS_IN_PERIOD)";
}
