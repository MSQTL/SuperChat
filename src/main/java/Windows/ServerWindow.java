package Windows;

import sample.ServerConnection;

import javax.swing.*;

public class ServerWindow extends JFrame {
    ServerConnection serverConnection;
    public JTextArea eventLog = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(eventLog);
    public JTextField messageText = new JTextField();
    public JButton sendButton = new JButton("Отправить");
    public ServerWindow(){

        setTitle("Сервер");
        setLayout(null);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        scrollPane.setBounds(20,20,345, 370);
        eventLog.setEditable(false);
        eventLog.setLineWrap(true);
        eventLog.setWrapStyleWord(true);
        messageText.setBounds(20, 410, 225, 30);
        sendButton.setBounds(264, 410, 100, 30);

        add(scrollPane);
        add(messageText);
        add(sendButton);

        setVisible(true);
        serverConnection = new ServerConnection(this);
    }
}
