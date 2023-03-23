package Windows;

import sample.ClientConnection;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClientWindow extends JFrame{
    public JTabbedPane tabs = new JTabbedPane();
    JPanel all = new JPanel();
    public JTextArea chat = new JTextArea();
    Font fontTabs = new Font("Century Gothic", Font.PLAIN, 15);
    Font fontText = new Font("", Font.PLAIN, 17);
    Font fontButton = new Font("", Font.BOLD, 12);
    JScrollPane scrollChat = new JScrollPane(chat);
    public JTable users = new JTable();
    JScrollPane scrollUsers = new JScrollPane(users);
    public JTextField messageText;
    public JButton sendButton = new JButton("Отправить");
    ClientConnection clientConnection = new ClientConnection(this);
    public ClientWindow() {
        setTitle("Чатек");
        setLayout(null);
        setSize(500, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        all.setLayout(null);
        tabs.addTab("Общий", all);
        tabs.setBounds(5,5,473,560);
        tabs.setFont(fontTabs);
        all.setBackground(new Color(198, 219, 241));

        chat.setFont(fontText);
        chat.setEditable(false);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);
        chat.setForeground(new Color(28, 71, 110));
        chat.setBackground(new Color(237, 240, 238));
        chat.setBorder(new MatteBorder(1, 1, 1, 1, new Color(237, 240, 238)));
        scrollChat.setBounds(5,5, 350, 478);
        scrollChat.setBorder(new MatteBorder(1, 1, 1, 1, new Color(237, 240, 238)));

        users.setBackground(new Color(237, 240, 238));
        users.setFocusable(false);
        users.setRowHeight(30);
        users.setShowHorizontalLines(false);
        users.setShowVerticalLines(false);
        users.setIntercellSpacing( new Dimension(0, 0));
        users.setFont(fontText);
        scrollUsers.setBounds(360,5,105, 513);
        scrollUsers.setBackground(new Color(237, 240, 238));
        scrollUsers.setFont(fontText);

        messageText = new JTextField("/войти ");
        messageText.setBounds(5,488,270, 30);
        messageText.setBackground(new Color(233, 236, 234));
        messageText.setForeground(new Color(28, 71, 110));
        messageText.setBorder(new MatteBorder(1, 1, 1, 1, new Color(233, 236, 234)));
        messageText.setFont(fontText);

        sendButton.setBounds(280, 489, 74, 28);
        sendButton.setFont(fontButton);
        sendButton.setMargin(new Insets(0, 0, 0, 0));
        sendButton.setBackground(new Color(233, 236, 234));
        sendButton.setForeground(new Color(28, 71, 110));
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
        privateChatArea.setEditable(false);
        privateChatArea.setLineWrap(true);
        privateChatArea.setWrapStyleWord(true);
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
