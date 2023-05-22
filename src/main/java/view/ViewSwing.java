package view;

import application.Requester;
import config.Config;
import queries.Queries;
import queries.TypeSearchBook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ViewSwing extends JFrame implements IView, Runnable {
    private final JPanel entryField;
    private final Requester requester;

    public ViewSwing(){
        entryField = new JPanel();
        requester = new Requester();
    }

    @Override
    public void start(){
        javax.swing.SwingUtilities.invokeLater(this);
    }

    private void init(){
        setSize(Config.WIDTH, Config.HEIGHT);
        getContentPane().setBackground(Config.color); // Set background color
        setDefaultCloseOperation(EXIT_ON_CLOSE); // When "(X)" clicked, process is being killed
        setTitle("Library"); // Set title
        setResizable(true);
        setVisible(true); // Show everything
    }

    private void draw(Component[] components){
        javax.swing.SwingUtilities.invokeLater(() -> {
            entryField.removeAll();
            entryField.setLayout(new GridLayout(components.length, 1));
            for (Component component: components)
                entryField.add(component);
            setContentPane(entryField);
        });
    }

    private void draw(List<Component> components){
        javax.swing.SwingUtilities.invokeLater(() -> {
            entryField.removeAll();
            entryField.setLayout(new GridLayout(components.size(), 1));
            for (Component component: components)
                entryField.add(component);
            setContentPane(entryField);
        });
    }

    @Override
    public void run() {
        init();
        startMenu();
    }

    @Override
    public void startMenu() {
        JLabel label = new JLabel("Select your role:");
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton reader = new JButton("Reader");
        reader.addActionListener(e -> readerMenu());
        JButton librarian = new JButton("Librarian");
        librarian.addActionListener(e -> librarianAuth());

        entryField.setLayout(new GridLayout(3, 1));
        entryField.add(label);
        entryField.add(reader);
        entryField.add(librarian);
        setContentPane(entryField);
    }

    private void returnStartMenu(){
        entryField.removeAll();
        startMenu();
    }

    @Override
    public void readerMenu() {
        JLabel label = new JLabel("Select action:");
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton reg = new JButton("Registration in library");
        reg.addActionListener(e -> readerRegistration());
        JButton find = new JButton("Find book");
        find.addActionListener(e -> readerFindBook());
        JButton form = new JButton("Show popular books");
        form.addActionListener(e -> readerShowPopularBooks());
        draw(new Component[]{label, reg, find, form});
    }

    @Override
    public void readerRegistration() {
        try {
            JLabel label = new JLabel("Select library");
            label.setHorizontalAlignment(JLabel.CENTER);
            ResultSet librariesSet = requester.libraries();
            List<Component> libraries = new ArrayList<>();
            libraries.add(label);
            while (librariesSet.next()){
                JButton library = new JButton(librariesSet.getString("Name"));
                library.addActionListener(e -> formReaderRegistration(Integer.parseInt(library.getText())));
                libraries.add(library);
            }
            draw(libraries);
        } catch (SQLException e) {
            JLabel label = new JLabel("Sorry, impossible do anything");
            label.setHorizontalAlignment(JLabel.CENTER);
            JButton ret = new JButton("return");
            ret.addActionListener(ev -> readerMenu());
            draw(new Component[]{label, ret});
        }
    }

    private List<Component> createForm(String[] fields){
        List<Component> form = new ArrayList<>();
        for (String field: fields){
            JLabel label = new JLabel("Enter your " + field);
            label.setHorizontalAlignment(JLabel.CENTER);
            JTextField textField = new JTextField();
            textField.setHorizontalAlignment(JTextField.CENTER);
            form.add(label);
            form.add(textField);
        }
        return form;
    }

    private void formReaderRegistration(Integer library){
        JLabel idl = new JLabel("Choose natural number");
        idl.setHorizontalAlignment(JLabel.CENTER);
        JTextField id = new JTextField();
        id.setHorizontalAlignment(JTextField.CENTER);
        List<Component> form = createForm(new String[]{"name", "second name", "birthdate"});
        form.add(0, idl);
        form.add(1, id);
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            try {
                JTextField name = (JTextField) form.get(3);
                JTextField secondName = (JTextField) form.get(5);
                JTextField birthdate = (JTextField) form.get(7);
                Date date = new SimpleDateFormat("dd.MM.yyyy").parse(birthdate.getText());
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                Integer idn = Integer.parseInt(id.getText());
                requester.registerReader(idn, name.getText(), secondName.getText(), sqlDate, library);
                chooseCategories(idn);
            } catch (SQLException | ParseException ex) {
                JLabel label = new JLabel("Failed create reader: " + ex);
                label.setHorizontalAlignment(JLabel.CENTER);
                JButton ret = new JButton("return");
                ret.addActionListener(ev -> formReaderRegistration(library));
                draw(new Component[]{label, ret});
            }
        });
        form.add(ok);
        draw(form);
    }

    private void chooseCategories(Integer id){
        JLabel label = new JLabel("Choose your category");
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton scientist = new JButton("Scientist");
        scientist.addActionListener(e -> formScientistReg(id));
        JButton student = new JButton("Student");
        student.addActionListener(e -> formStudentReg(id));
        JButton teacher = new JButton("Teacher");
        teacher.addActionListener(e -> formTeacherReg(id));
        JButton proletarian = new JButton("Proletarian");
        proletarian.addActionListener(e -> formProletarianReg(id));
        JButton pensioner = new JButton("Pensioner");
        pensioner.addActionListener(e -> formPensionerReg(id));
        JButton nothing = new JButton("Nothing");
        nothing.addActionListener(e -> readerMenu());
        draw(new Component[]{label, scientist, student, teacher, proletarian, pensioner, nothing});
    }

    private void handleExceptionRegCategory(String category, SQLException e, Integer id){
        JLabel label = new JLabel("Failed create " + category + e);
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton ret = new JButton("return");
        ret.addActionListener(ev -> chooseCategories(id));
        draw(new Component[]{label, ret});
    }

    private void formScientistReg(Integer id){
        List<Component> form = createForm(new String[]{"institution", "topic of work", "degree"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            JTextField institution = (JTextField) form.get(1);
            JTextField topic = (JTextField) form.get(3);
            JTextField degree = (JTextField) form.get(5);
            try {
                requester.regScientist(id, institution.getText(), topic.getText(), degree.getText());
                chooseCategories(id);
            } catch (SQLException ex) {
                handleExceptionRegCategory("scientist", ex, id);
            }
        });
        form.add(ok);
        draw(form);
    }

    private void formStudentReg(Integer id){
        List<Component> form = createForm(new String[]{"university", "department", "course", "group"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            JTextField university = (JTextField) form.get(1);
            JTextField dep = (JTextField) form.get(3);
            Integer course = Integer.parseInt(((JTextField) form.get(5)).getText());
            Integer group = Integer.parseInt(((JTextField) form.get(7)).getText());
            try {
                requester.regStudent(id, university.getText(), dep.getText(), course, group);
                chooseCategories(id);
            } catch (SQLException ex) {
                handleExceptionRegCategory("student", ex, id);
            }
        });
        form.add(ok);
        draw(form);
    }

    private void formTeacherReg(Integer id){
        List<Component> form = createForm(new String[]{"university", "degree"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            JTextField university = (JTextField) form.get(1);
            JTextField degree = (JTextField) form.get(3);
            try {
                requester.regTeacher(id, university.getText(), degree.getText());
                chooseCategories(id);
            } catch (SQLException ex) {
                handleExceptionRegCategory("teacher", ex, id);
            }
        });
        form.add(ok);
        draw(form);
    }

    private void formProletarianReg(Integer id){
        List<Component> form = createForm(new String[]{"organization", "position"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            JTextField org = (JTextField) form.get(1);
            JTextField pos = (JTextField) form.get(3);
            try {
                requester.regProletarian(id, org.getText(), pos.getText());
                chooseCategories(id);
            } catch (SQLException ex) {
                handleExceptionRegCategory("teacher", ex, id);
            }
        });
        form.add(ok);
        draw(form);
    }

    private void formPensionerReg(Integer id){
        try{
            requester.regPensioner(id);
            readerMenu();
        } catch (SQLException e) {
            handleExceptionRegCategory("pensioner", e, id);
        }
    }

    @Override
    public void readerFindBook() {
        JButton name = new JButton("Search by name");
        name.addActionListener(e -> searchBookMenu(TypeSearchBook.NAME));
        JButton author = new JButton("Search by author");
        author.addActionListener(e -> searchBookMenu(TypeSearchBook.AUTHOR));
        JButton ret = new JButton("Return");
        ret.addActionListener(e -> readerMenu());
        draw(new Component[]{name, author, ret});
    }

    private void searchBookMenu(TypeSearchBook format){
        JLabel label = new JLabel("Enter " + format + " of book");
        label.setHorizontalAlignment(JLabel.CENTER);
        JTextField field = new JTextField();
        field.setHorizontalAlignment(JTextField.CENTER);
        JButton ok = new JButton("Search");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(ev -> showResRequest(format, field.getText()));
        draw(new Component[]{label, field, ok});
    }

    private void showResRequest(TypeSearchBook typeRequest, String data){
        try {
            ResultSet res = requester.requestBooks(typeRequest, data);
            List<Component> list = new ArrayList<>();
            while (res.next()){
                JLabel label = new JLabel(res.getString("NomenclatureNumber") + " - " +
                        res.getString("Name"));
                label.setHorizontalAlignment(JLabel.CENTER);
                list.add(label);
            }
            JButton ok = new JButton("Return");
            ok.addActionListener(e -> readerFindBook());
            list.add(ok);
            draw(list);
        } catch (SQLException e) {
            JLabel label = new JLabel("Error: " + e);
            label.setHorizontalAlignment(JLabel.CENTER);
            JButton ret = new JButton("Return");
            ret.addActionListener(ev -> readerFindBook());
            draw(new Component[]{label, ret});
        }
    }

    @Override
    public void readerShowPopularBooks() {

    }

    private void librarianAuth(){
        JTextField loginField = new JTextField();
        loginField.setToolTipText("login");
        loginField.setHorizontalAlignment(JTextField.CENTER);

        JPasswordField passField = new JPasswordField();
        passField.setToolTipText("password");
        passField.setHorizontalAlignment(JPasswordField.CENTER);

        JButton authButton = new JButton("Sign in");
        authButton.setMnemonic(KeyEvent.VK_ENTER);
        authButton.addActionListener(e -> {
            if (Config.librarianLogin.equals(loginField.getText()) &&
                    Config.librarianPass.equals(new String(passField.getPassword())))
                librarianMenu();
            else {
                System.out.println(loginField.getText() + new String(passField.getPassword()));
                JLabel label = new JLabel("Incorrect login or password");
                label.setHorizontalAlignment(JLabel.CENTER);
                JButton ok = new JButton("OK");
                ok.setMnemonic(KeyEvent.VK_ENTER);
                ok.addActionListener(event -> returnStartMenu());
                draw(new Component[]{label, ok});
            }
        });
        JButton ret = new JButton("Return");
        ret.addActionListener(e -> returnStartMenu());
        draw(new Component[]{loginField, passField, authButton, ret});
    }

    @Override
    public void librarianMenu() {
        JLabel label = new JLabel("Select action:");
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton select = new JButton("Make request");
        select.addActionListener(e -> librarianSelect());
        JButton regBook = new JButton("Registry new book");
        regBook.addActionListener(e -> librarianRegistryBook());
        JButton regLibrarian = new JButton("Registry new librarian");
        regLibrarian.addActionListener(e -> librarianRegistryLibrarian());
        draw(new Component[]{label, select, regBook, regLibrarian});
    }

    @Override
    public void librarianSelect() {
        Map<String, String[]> queries = Queries.getLibrarianQueriesArgs();
        List<JButton> queriesButtons = new ArrayList<>();
        for (String query: queries.keySet()){
            JButton button = new JButton(query);
            button.addActionListener(e -> {
                List<Component> form = createForm(queries.get(query));
                JButton ok = new JButton("OK");
                ok.setMnemonic(KeyEvent.VK_ENTER);
                ok.addActionListener(e1 -> {
                    String[] args = new String[form.size() / 2];
                    for (int i = 0; i < form.size() / 2; i ++)
                        args[i] = ((JTextField) form.get(2*i + 1)).getText();
                    try {
                        Statement statement = requester.librarianQueries(query, args);


                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            });
        }
    }

    @Override
    public void librarianRegistryBook() {

    }

    @Override
    public void librarianRegistryLibrarian() {
        JLabel idl = new JLabel("Choose natural number");
        idl.setHorizontalAlignment(JLabel.CENTER);
        JTextField id = new JTextField();
        id.setHorizontalAlignment(JTextField.CENTER);
        List<Component> form = createForm(new String[]{"name", "second name", "birthdate", "number of hall"});
        form.add(0, idl);
        form.add(1, id);
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            JTextField name = (JTextField) form.get(3);
            JTextField secondName = (JTextField) form.get(5);
            JTextField birthdate = (JTextField) form.get(7);
            JTextField hallNumber = (JTextField) form.get(9);
            try {
                Integer idn = Integer.parseInt(id.getText());
                Integer hall = Integer.parseInt(hallNumber.getText());
                Date date = new SimpleDateFormat("dd.MM.yyyy").parse(birthdate.getText());
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                requester.regLibrarian(idn, hall, name.getText(), secondName.getText(), sqlDate);
                librarianMenu();
            } catch (SQLException ex) {
                JLabel label = new JLabel("Sorry, impossible do anything" + ex);
                label.setHorizontalAlignment(JLabel.CENTER);
                JButton ret = new JButton("return");
                ret.addActionListener(ev -> librarianMenu());
                draw(new Component[]{label, ret});
            } catch (ParseException ex) {
                JLabel label = new JLabel("Incorrect birthdate");
                label.setHorizontalAlignment(JLabel.CENTER);
                JButton ret = new JButton("return");
                ret.addActionListener(ev -> librarianRegistryLibrarian());
                draw(new Component[]{label, ret});
            }
        });
        form.add(ok);
        draw(form);
    }
}
