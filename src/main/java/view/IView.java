package view;

public interface IView {
    public void start();
    public void startMenu();

    // for readers
    public void readerMenu();
    public void readerRegistration();
    public void readerFindBook();
    public void readerShowFormular();

    //for librarians
    public void librarianMenu();
    public void librarianSelect();
    public void librarianRegistryBook();
    public void librarianRegistryLibrarian();
}
