package sample;

import Windows.ClientWindow;
import javax.swing.*;
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
                            clientWindow.messageText.setToolTipText(null);
                            setAuthorized(true);
                            clientWindow.chat.setText(null);
                            clientWindow.chat.append("Добро пожаловать в чат" + "\n");
                            break;
                        }
                        clientWindow.chat.append(strFromServer + "\n");
                        clientWindow.messageText.setText("/войти ");
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
                                    JTextArea textArea =
                                            (JTextArea)clientWindow.tabs.getComponentAt(i).getComponentAt(10, 10);
                                    textArea.append(message[0].substring(2) + ": " + message[1]);
                                    textArea.append("\n");
                                    count++;
                                }
                            }
                            if (count == 0){

                                clientWindow.createNewPanel(message[0].substring(2), message[0].substring(2) + ": " + message[1] + "\n");
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
            JTextField textField = (JTextField) clientWindow.tabs.getComponentAt
                    (clientWindow.tabs.getSelectedIndex()).getComponentAt(6, 489);
            out.writeUTF("/p" + nickTo + " " + textField.getText());
            JTextArea textArea = (JTextArea)clientWindow.tabs.getComponentAt
                    (clientWindow.tabs.getSelectedIndex()).getComponentAt(10, 10);
            textArea.append("Вы: " + textField.getText());
            textArea.append("\n");
            textField.setText(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
