package Windows;

import sample.ClientConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.UnknownHostException;

public class ClientWindow extends JFrame{
    JTabbedPane tabs = new JTabbedPane();
    JPanel all = new JPanel();
    public JTextArea chat = new JTextArea();
    JScrollPane scrollChat = new JScrollPane(chat);
    JTable users;
    DefaultTableModel chatModel = new DefaultTableModel();
    JScrollPane scrollUsers = new JScrollPane(users);
    public JTextField messageText = new JTextField();
    public JButton sendButton = new JButton("Отправить");
    public ClientWindow() throws UnknownHostException {
        setTitle("Чатек");
        setLayout(null);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        all.setLayout(null);
        tabs.addTab("Общий", all);
        tabs.setBounds(5,5,473,550);
        scrollChat.setBounds(5,5, 350, 478);
        scrollUsers.setBounds(360,5,105, 513);
        messageText.setBounds(5,488,270, 30);
        sendButton.setBounds(280, 489, 74, 28);
        all.add(scrollUsers);
        all.add(scrollChat);
        all.add(messageText);
        all.add(sendButton);
        add(tabs);

        setVisible(true);

        ClientConnection clientConnection = new ClientConnection(this);
    }
}
