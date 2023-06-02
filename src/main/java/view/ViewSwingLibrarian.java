package view;

import application.Requester;
import config.Config;
import queries.Queries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ViewSwingLibrarian implements IViewLibrarian{
    private final ViewSwing headView;
    private final Requester requester;
    public ViewSwingLibrarian(ViewSwing view, Requester requester){
        headView = view;
        this.requester = requester;
        librarianMenu();
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
        JButton change = new JButton("Work with data");
        change.addActionListener(e -> showTables());
        headView.draw(new Component[]{label, select, regBook, regLibrarian, change});
    }

    @Override
    public void librarianSelect() {
        Map<String, String[]> queries = Queries.getLibrarianQueriesArgs();
        List<Component> queriesButtons = new ArrayList<>();
        for (String query: queries.keySet()){
            JButton button = new JButton(query);
            button.addActionListener(e -> {
                List<Component> form = headView.createForm(queries.get(query));
                JButton ok = new JButton("OK");
                ok.setMnemonic(KeyEvent.VK_ENTER);
                ok.addActionListener(e1 -> {
                    String[] args = new String[form.size() / 2];
                    for (int i = 0; i < form.size() / 2; i ++)
                        args[i] = ((JTextField) form.get(2*i + 1)).getText();
                    try {
                        ResultSet resultSet = requester.librarianQueries(query, args);
                        headView.showResults(resultSet, this::librarianMenu);
                    } catch (SQLException ex) {
                        headView.handleException(ex, this::librarianMenu);
                    }
                });
                form.add(ok);
                headView.draw(form);
            });
            queriesButtons.add(button);
        }
        headView.draw(queriesButtons);
    }

    @Override
    public void librarianRegistryBook() {
        JLabel label = new JLabel("Choose option");
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton composition = new JButton("Add composition");
        composition.addActionListener(e -> addComposition());
        JButton books = new JButton("Add book");
        books.addActionListener(e -> addBook());
        JButton content = new JButton("Set content book");
        content.addActionListener(e -> setContent());
        JButton bibliofond = new JButton("Registry book in bibliofond");
        bibliofond.addActionListener(e -> regBibliofond());
        headView.draw(new Component[]{label, composition, books, content, bibliofond});
    }

    private void addComposition(){
        List<Component> form = headView.createForm(new String[]{"id", "name", "author", "category"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            JTextField id = (JTextField) form.get(1);
            JTextField name = (JTextField) form.get(3);
            JTextField author = (JTextField) form.get(5);
            JTextField category = (JTextField) form.get(7);
            try {
                int idn = Integer.parseInt(id.getText());
                requester.regComposition(idn, name.getText(), author.getText(), category.getText());
                librarianMenu();
            } catch (SQLException ex) {
                headView.handleException(ex, this::addComposition);
            }
        });
        form.add(ok);
        headView.draw(form);
    }

    private void addBook(){
        List<Component> form = headView.createForm(new String[]{"id", "name", "type"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            JTextField id = (JTextField) form.get(1);
            JTextField name = (JTextField) form.get(3);
            JTextField type = (JTextField) form.get(5);
            try {
                int idn = Integer.parseInt(id.getText());
                requester.regBook(idn, name.getText(), type.getText());
                librarianMenu();
            } catch (SQLException ex) {
                headView.handleException(ex, this::addBook);
            }
        });
        form.add(ok);
        headView.draw(form);
    }

    private void setContent(){
        List<Component> form = headView.createForm(new String[]{"id", "book", "composition", "start page", "end page"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            int id = Integer.parseInt(((JTextField) form.get(1)).getText());
            int book = Integer.parseInt(((JTextField) form.get(3)).getText());
            int composition = Integer.parseInt(((JTextField) form.get(5)).getText());
            int startPage = Integer.parseInt(((JTextField) form.get(7)).getText());
            int endPage = Integer.parseInt(((JTextField) form.get(9)).getText());
            try {
                requester.regContent(id, book, composition, startPage, endPage);
                librarianMenu();
            } catch (SQLException ex) {
                headView.handleException(ex, this::setContent);
            }
        });
        form.add(ok);
        headView.draw(form);
    }

    private void regBibliofond(){
        List<Component> form = headView.createForm(new String[]{"nomenclature number", "book", "time delivery days", "hall number", "rack number", "shelf number"});
        JButton ok = new JButton("Enter");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> {
            int id = Integer.parseInt(((JTextField) form.get(1)).getText());
            int book = Integer.parseInt(((JTextField) form.get(3)).getText());
            int time = Integer.parseInt(((JTextField) form.get(5)).getText());
            int hall = Integer.parseInt(((JTextField) form.get(7)).getText());
            int rack = Integer.parseInt(((JTextField) form.get(9)).getText());
            int shelf = Integer.parseInt(((JTextField) form.get(11)).getText());
            try {
                java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
                requester.regBibliofond(id, book, date, time, hall, rack, shelf);
                librarianMenu();
            } catch (SQLException ex) {
                headView.handleException(ex, this::regBibliofond);
            }
        });
        form.add(ok);
        headView.draw(form);
    }

    @Override
    public void librarianRegistryLibrarian() {
        JLabel idl = new JLabel("Choose natural number");
        idl.setHorizontalAlignment(JLabel.CENTER);
        JTextField id = new JTextField();
        id.setHorizontalAlignment(JTextField.CENTER);
        List<Component> form = headView.createForm(new String[]{"name", "second name", "birthdate", "number of hall"});
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
                headView.handleException(ex, this::librarianMenu);
            } catch (ParseException ex) {
                Exception exception = new Exception("Incorrect birthdate " + ex);
                headView.handleException(exception, this::librarianRegistryLibrarian);
            }
        });
        form.add(ok);
        headView.draw(form);
    }

    private void showTables(){
        int count = Config.tableNames.length;
        Component[] tables = new Component[count];
        for (int i = 0; i < count; i++) {
            JButton button = new JButton(Config.tableNames[i]);
            button.addActionListener(e -> changeData(button.getText()));
            tables[i] = button;
        }
        headView.draw(tables);
    }

    @Override
    public void changeData(String tableName){
        try {
            JScrollPane table = headView.getResults(requester.getTable(tableName));
            JLabel deleteLabel = new JLabel("Select id for delete:");
            deleteLabel.setHorizontalAlignment(JLabel.CENTER);
            JTextField deleteField = new JTextField();
            deleteField.setHorizontalAlignment(JTextField.CENTER);
            JLabel labelSet = new JLabel("Enter that your want update in field1=value1, field12=value2");
            labelSet.setHorizontalAlignment(JLabel.CENTER);
            JTextField fieldSet = new JTextField();
            fieldSet.setHorizontalAlignment(JTextField.CENTER);
            JLabel labelCond = new JLabel("Enter condition for update");
            labelCond.setHorizontalAlignment(JLabel.CENTER);
            JTextField fieldCond = new JTextField();
            fieldCond.setHorizontalAlignment(JTextField.CENTER);
            JButton ok = new JButton("OK");
            ok.setMnemonic(KeyEvent.VK_ENTER);
            ok.addActionListener(e -> {
                try {
                    requester.changeData(deleteField.getText(), fieldSet.getText(), fieldCond.getText(), tableName);
                    librarianMenu();
                } catch (SQLException ex) {
                    headView.handleException(ex, this::librarianMenu);
                }
            });
            JButton ret = new JButton("Return");
            ret.addActionListener(e -> librarianMenu());
            headView.draw(new Component[]{table, deleteLabel, deleteField, labelSet, fieldSet, labelCond, fieldCond, ok, ret});
        } catch (SQLException ex) {
            headView.handleException(ex, this::librarianMenu);
        }
    }
}
