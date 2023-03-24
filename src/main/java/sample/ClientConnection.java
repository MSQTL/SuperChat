package sample;

import Windows.ClientWindow;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ClientConnection {
    ClientWindow clientWindow;
    DataInputStream in;
    public DataOutputStream out;
    private Socket socket;
    private boolean isAuthorized;
    Font fontText = new Font("", Font.PLAIN, 17);
    Font fontButton = new Font("", Font.BOLD, 12);
    public void setAuthorized(boolean flag){
        this.isAuthorized = flag;
    }
    public ClientConnection(ClientWindow clientWindow){
        this.clientWindow = clientWindow;
    }
    public void start(){
        try {
            String HOST = "localhost";
            int PORT = 8080;
            socket = new Socket(HOST, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(() -> {
                try {
                    while (!isAuthorized) {
                        String strFromServer = in.readUTF();
                        if(strFromServer.startsWith("/вход_выполнен")) {
                            String[] array = strFromServer.split(" ", 2);
                            clientWindow.setTitle(array[1]);
                            setAuthorized(true);
                            break;
                        }
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
                                privateSendButton.addActionListener(e -> onPrivateClick());
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
                        }
                        else {
                            clientWindow.chat.append(strFromServer);
                            clientWindow.chat.append("\n");
                        }
                    }
                } catch (Exception ignored) {
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException ignored) {
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
