package Windows;

import sample.ClientConnection;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

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

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    clientConnection.out.writeUTF("/конец");
                } catch (IOException ignored) {}
                super.windowClosing(e);
            }
        });
        setTitle("Чатек");
        setLayout(null);
        setSize(500, 620);
        setResizable(false);
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
        chat.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(215, 253, 227), new Color(177, 224, 195)));
        scrollChat.setBounds(5,5, 350, 478);
        scrollChat.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(198, 219, 241), new Color(173, 192, 211)));

        users.setBackground(new Color(237, 240, 238));
        users.setFocusable(false);
        users.setRowHeight(30);
        users.setShowHorizontalLines(false);
        users.setShowVerticalLines(false);
        users.setIntercellSpacing( new Dimension(0, 0));
        users.setFont(fontText);
        users.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(215, 253, 227), new Color(177, 224, 195)));
        scrollUsers.setBounds(360,5,105, 513);
        scrollUsers.setBackground(new Color(237, 240, 238));
        scrollUsers.setFont(fontText);
        scrollUsers.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(198, 219, 241), new Color(173, 192, 211)));

        messageText = new JTextField("/войти ");
        messageText.setBounds(5,488,270, 30);
        messageText.setBackground(new Color(233, 236, 234));
        messageText.setMargin(new Insets(5, 5, 5, 5));
        messageText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(198, 219, 241), new Color(173, 192, 211)));
        messageText.setForeground(new Color(28, 71, 110));
        messageText.setFont(fontText);

        sendButton.setBounds(280, 488, 74, 31);
        sendButton.setFont(fontButton);
        sendButton.setMargin(new Insets(0, 0, 0, 0));
        sendButton.setBackground(new Color(233, 236, 234));
        sendButton.setForeground(new Color(28, 71, 110));
        sendButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(198, 219, 241), new Color(173, 192, 211)));
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

        if(isPrivateChatAdded(userName)) return;

        JPanel privateChat = new JPanel();
        privateChat.setLayout(null);

        tabs.addTab(userName, privateChat);
        JTextArea privateChatArea = new JTextArea("Чат с " + userName + "\n");
        privateChatArea.setEditable(false);
        privateChatArea.setLineWrap(true);
        privateChatArea.setWrapStyleWord(true);
        JScrollPane scrollPrivateChatArea = new JScrollPane(privateChatArea);
        privateChatArea.setBounds(5, 5, 457, 478);
        scrollPrivateChatArea.setBounds(5,5, 457, 478);


        JTextField privateMessageText = new JTextField();
        privateMessageText.setBounds(5,488,378, 30);

        JButton privateSendButton = new JButton("Отправить");
        privateSendButton.setBounds(388, 488, 74, 31);
        privateSendButton.addActionListener(e -> clientConnection.onPrivateClick());
        privateChat.add(privateChatArea);
        privateChat.add(privateMessageText);
        privateChat.add(privateSendButton);

        privateChatArea.setFont(fontText);
        privateChat.setBackground(new Color(198, 219, 241));
        privateChat.setForeground(new Color(28, 71, 110));
        privateChatArea.setBackground(new Color(237, 240, 238));
        privateChatArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(215, 253, 227), new Color(177, 224, 195)));
        scrollPrivateChatArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(198, 219, 241), new Color(173, 192, 211)));
        privateMessageText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(198, 219, 241), new Color(173, 192, 211)));
        privateMessageText.setForeground(new Color(28, 71, 110));
        privateMessageText.setBackground(new Color(233, 236, 234));
        privateMessageText.setFont(fontText);
        privateSendButton.setFont(fontButton);
        privateSendButton.setMargin(new Insets(0, 0, 0, 0));
        privateSendButton.setBackground(new Color(233, 236, 234));
        privateSendButton.setForeground(new Color(28, 71, 110));
        privateSendButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(198, 219, 241), new Color(173, 192, 211)));
    }
    public boolean isPrivateChatAdded(String userName){
        for (int i = 0; i < tabs.getTabCount(); i++){
            if(Objects.equals(tabs.getTitleAt(i), userName)) return true;
        }
        return false;
    }
}
