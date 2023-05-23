package view;

import application.Requester;
import config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    void draw(Component[] components){
        javax.swing.SwingUtilities.invokeLater(() -> {
            entryField.removeAll();
            entryField.setLayout(new GridLayout(components.length, 1));
            for (Component component: components)
                entryField.add(component);
            setContentPane(entryField);
        });
    }

    void draw(List<Component> components){
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
        reader.addActionListener(e -> {
            ViewSwingReader viewReader = new ViewSwingReader(this, requester);
            viewReader.readerMenu();
        });
        JButton librarian = new JButton("Librarian");
        librarian.addActionListener(e -> librarianAuth());
        JButton info = new JButton("Information about program");
        info.addActionListener(e -> showInfo());

        entryField.setLayout(new GridLayout(4, 1));
        entryField.add(label);
        entryField.add(reader);
        entryField.add(librarian);
        entryField.add(info);
        setContentPane(entryField);
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
                    Config.librarianPass.equals(new String(passField.getPassword()))) {
                ViewSwingLibrarian viewLibrarian = new ViewSwingLibrarian(this, requester);
                viewLibrarian.librarianMenu();
            }
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
    public void showInfo(){
        JLabel label = new JLabel("Information system of the library fund of the city.\nDeveloped by Gudkov S. A. in May 2023");
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton ret = new JButton("Return");
        ret.setMnemonic(KeyEvent.VK_ENTER);
        ret.addActionListener(e -> startMenu());
        draw(new Component[]{label, ret});
    }

    void returnStartMenu(){
        entryField.removeAll();
        startMenu();
    }

    List<Component> createForm(String[] fields){
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

    void showResults(ResultSet resultSet, FunctionPointer f) throws SQLException {
        int countColumns = resultSet.getMetaData().getColumnCount();
        String[] columnNames = new String[countColumns];
        for (int i = 0; i < countColumns; i++)
            columnNames[i] = resultSet.getMetaData().getColumnName(i);
        resultSet.last();
        int countRows = resultSet.getRow();
        String[][] data = new String[countRows][countColumns];
        resultSet.first();
        for (int i = 0; i < countRows; i++) {
            for (int j = 0; j < countColumns; j++)
                data[i][j] = resultSet.getString(j);
            resultSet.next();
        }
        JTable table = new JTable(data, columnNames);
        JScrollPane pane = new JScrollPane(table);
        JButton ok = new JButton("Return");
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.addActionListener(e -> f.function());
        draw(new Component[]{pane});
    }

    void handleException(Exception e, FunctionPointer f){
        JLabel label = new JLabel("Error: " + e);
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton ret = new JButton("return");
        ret.setMnemonic(KeyEvent.VK_ENTER);
        ret.addActionListener(ev -> f.function());
        draw(new Component[]{label, ret});
    }

    interface FunctionPointer {
        void function();
    }
}
