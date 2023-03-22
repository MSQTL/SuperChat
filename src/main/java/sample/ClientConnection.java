package sample;

import Windows.ClientWindow;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class ClientConnection {
    ClientWindow clientWindow;
    DataInputStream in;
    DataOutputStream out;
    private final String HOST = "localhost";
    private final int PORT = 8080;
    private Socket socket;
    private boolean isAuthorized;
    public void setAuthorized(boolean flag){
        this.isAuthorized = flag;
    }
    public ClientConnection(ClientWindow clientWindow){
        this.clientWindow = clientWindow;
    }
    public void start(){
        try {
            socket = new Socket(HOST, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!isAuthorized) {
                            String strFromServer = in.readUTF();
                            if(strFromServer.startsWith("/вход_выполнен")) {
                                setAuthorized(true);
                                break;
                            }
                            clientWindow.chat.append(strFromServer + "\n");
                        }
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.equalsIgnoreCase("/конец")) {
                                break;
                            }
                            else if (strFromServer.startsWith("/клиенты")){
                                clientWindow.refresh(strFromServer.substring(9));
                            }
                            else if (strFromServer.startsWith("/f")){
                                String[] message = strFromServer.split(" ", 2);
                                int count = 0;
                                for (int i = 0; i < clientWindow.tabs.getTabCount(); i++){
                                    if(Objects.equals(clientWindow.tabs.getTitleAt(i), message[0].substring(2))){
                                        JTextArea textArea = (JTextArea)clientWindow.tabs.getComponentAt(i).getComponentAt(10, 10);
                                        textArea.append(message[0].substring(2) + ": " + message[1]);
                                        textArea.append("\n");
                                        count++;
                                    }
                                }
                                if (count == 0){

                                    JPanel privateChat = new JPanel();
                                    privateChat.setLayout(null);

                                    clientWindow.tabs.addTab(message[0].substring(2), privateChat);
                                    JTextArea privateChatArea = new JTextArea("Чат с " +
                                            (message[0].substring(2)) + "\n" +
                                            message[0].substring(2) + ": " + message[1] + "\n");
                                    JScrollPane scrollPrivateChatArea = new JScrollPane(privateChatArea);
                                    privateChatArea.setBounds(5, 5, 350, 478);
                                    scrollPrivateChatArea.setBounds(5,5, 350, 478);

                                    JTextField privateMessageText = new JTextField();
                                    privateMessageText.setBounds(5,488,270, 30);

                                    JButton privateSendButton = new JButton("Отправить");
                                    privateSendButton.setBounds(280, 489, 74, 28);
                                    privateSendButton.addActionListener(e -> onPrivateClick());
                                    privateChat.add(privateChatArea);
                                    privateChat.add(privateMessageText);
                                    privateChat.add(privateSendButton);
                                }
                            }
                            else {
                                clientWindow.chat.append(strFromServer);
                                clientWindow.chat.append("\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onAuthClick(){
        if (socket == null || socket.isClosed()) {
            start();
        }
        try {
            out.writeUTF(clientWindow.messageText.getText());
            clientWindow.messageText.setText(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onPrivateClick(){
        if (socket == null || socket.isClosed()) {
            start();
        }
        try {
            String nickTo = clientWindow.tabs.getTitleAt(clientWindow.tabs.getSelectedIndex());
            JTextField textField = (JTextField) clientWindow.tabs.getComponentAt(clientWindow.tabs.getSelectedIndex()).getComponentAt(6, 489);
            out.writeUTF("/p" + nickTo + " " + textField.getText());
            JTextArea textArea = (JTextArea)clientWindow.tabs.getComponentAt(clientWindow.tabs.getSelectedIndex()).getComponentAt(10, 10);
            textArea.append("Вы: " + textField.getText());
            textArea.append("\n");
            textField.setText(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
