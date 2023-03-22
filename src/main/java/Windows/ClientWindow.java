package Windows;

import sample.ClientConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClientWindow extends JFrame{
    public JTabbedPane tabs = new JTabbedPane();
    JPanel all = new JPanel();
    public JTextArea chat = new JTextArea();
    JScrollPane scrollChat = new JScrollPane(chat);
    public JTable users = new JTable();
    JScrollPane scrollUsers = new JScrollPane(users);
    public JTextField messageText;
    public JButton sendButton = new JButton("Отправить");
    ClientConnection clientConnection = new ClientConnection(this);
    public ClientWindow() {
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
        messageText = new JTextField("/войти ");
        messageText.setBounds(5,488,270, 30);
        sendButton.setBounds(280, 489, 74, 28);
        sendButton.addActionListener(e -> clientConnection.onAuthClick());
        users.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 1){
                    addPrivateChat();
                }
            }
        });
        all.add(scrollUsers);
        all.add(scrollChat);
        all.add(messageText);
        all.add(sendButton);
        add(tabs);

        setVisible(true);


    }
    public void refresh(String strFromServer){
        String[] strings = strFromServer.split(" ");
        String[][] data = new String[strings.length][1];
        for (int i = 0; i < strings.length; i++) data[i][0] = strings[i];
        String[] columnNames = {"Пользователи"};
        DefaultTableModel defaultTableModel = new DefaultTableModel(data, columnNames){
            @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };
        users.setModel(defaultTableModel);
    }
    public void addPrivateChat(){

        String userName = (String) users.getValueAt(users.getSelectedRow(), users.getSelectedColumn());
        JPanel privateChat = new JPanel();
        privateChat.setLayout(null);

        tabs.addTab(userName, privateChat);
        JTextArea privateChatArea = new JTextArea("Чат с " + userName + "\n");
        JScrollPane scrollPrivateChatArea = new JScrollPane(privateChatArea);
        privateChatArea.setBounds(5, 5, 350, 478);
        scrollPrivateChatArea.setBounds(5,5, 350, 478);

        JTextField privateMessageText = new JTextField();
        privateMessageText.setBounds(5,488,270, 30);

        JButton privateSendButton = new JButton("Отправить");
        privateSendButton.setBounds(280, 489, 74, 28);
        privateSendButton.addActionListener(e -> clientConnection.onPrivateClick());
        privateChat.add(privateChatArea);
        privateChat.add(privateMessageText);
        privateChat.add(privateSendButton);
    }
}
