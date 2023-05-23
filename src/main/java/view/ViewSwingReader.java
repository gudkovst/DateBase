package view;

import application.Requester;
import queries.TypeSearchBook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ViewSwingReader implements IViewReader{
    private final ViewSwing headView;
    private final Requester requester;
    public ViewSwingReader(ViewSwing view, Requester requester){
        headView = view;
        this.requester = requester;
        readerMenu();
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
        headView.draw(new Component[]{label, reg, find, form});
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
                String name = librariesSet.getString("Name");
                Integer id = librariesSet.getInt("ID");
                JButton library = new JButton(name);
                library.addActionListener(e -> formReaderRegistration(id));
                libraries.add(library);
            }
            headView.draw(libraries);
        } catch (SQLException e) {
            headView.handleException(e, this::readerMenu);
        }
    }

    private void formReaderRegistration(Integer library){
        JLabel idl = new JLabel("Choose natural number");
        idl.setHorizontalAlignment(JLabel.CENTER);
        JTextField id = new JTextField();
        id.setHorizontalAlignment(JTextField.CENTER);
        List<Component> form = headView.createForm(new String[]{"name", "second name", "birthdate"});
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
                headView.draw(new Component[]{label, ret});
            }
        });
        form.add(ok);
        headView.draw(form);
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
        headView.draw(new Component[]{label, scientist, student, teacher, proletarian, pensioner, nothing});
    }

    private void handleExceptionRegCategory(String category, SQLException e, Integer id){
        JLabel label = new JLabel("Failed create " + category + e);
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton ret = new JButton("return");
        ret.addActionListener(ev -> chooseCategories(id));
        headView.draw(new Component[]{label, ret});
    }

    private void formScientistReg(Integer id){
        List<Component> form = headView.createForm(new String[]{"institution", "topic of work", "degree"});
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
        headView.draw(form);
    }

    private void formStudentReg(Integer id){
        List<Component> form = headView.createForm(new String[]{"university", "department", "course", "group"});
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
        headView.draw(form);
    }

    private void formTeacherReg(Integer id){
        List<Component> form = headView.createForm(new String[]{"university", "degree"});
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
        headView.draw(form);
    }

    private void formProletarianReg(Integer id){
        List<Component> form = headView.createForm(new String[]{"organization", "position"});
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
        headView.draw(form);
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
        headView.draw(new Component[]{name, author, ret});
    }

    private void searchBookMenu(TypeSearchBook format){
        JLabel label = new JLabel("Enter " + format + " of book");
        label.setHorizontalAlignment(JLabel.CENTER);
        JTextField field = new JTextField();
        field.setHorizontalAlignment(JTextField.CENTER);
        JButton ok = new JButton("Search");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(ev -> showResRequest(format, field.getText()));
        headView.draw(new Component[]{label, field, ok});
    }

    private void showResRequest(TypeSearchBook typeRequest, String data){
        try {
            ResultSet res = requester.requestBooks(typeRequest, data);
            headView.showResults(res, this::readerMenu);
        } catch (SQLException e) {
            headView.handleException(e, this::readerFindBook);
        }
    }

    @Override
    public void readerShowPopularBooks() {
        try {
            ResultSet result = requester.popularCompositions();
            headView.showResults(result, this::readerMenu);
        } catch (SQLException e) {
            headView.handleException(e, this::readerMenu);
        }
    }
}
