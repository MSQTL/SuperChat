package sample;

import Windows.ClientWindow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
                            if(strFromServer.startsWith("//вход_выполнен")) {
                                setAuthorized(true);
                                break;
                            }
                            //clientWindow.chat.append(strFromServer + "\n");
                        }
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.equalsIgnoreCase("/конец")) {
                                break;
                            }
                            else if (strFromServer.contains("клиенты")){
                                clientWindow.chat.append(strFromServer.substring(9));
                                clientWindow.refresh(strFromServer.substring(9));
                                System.out.println(123);
                            }
                            else {
                                clientWindow.chat.append(strFromServer + strFromServer.startsWith("//клиенты"));
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
}
