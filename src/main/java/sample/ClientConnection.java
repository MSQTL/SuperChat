package sample;

import Windows.ClientWindow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection {
    private final String HOST = "localhost";
    private final int PORT = 8080;
    private Socket socket;
    private boolean isAuthorized;
    public void setAuthorized(boolean flag){
        this.isAuthorized = flag;
    }
    public ClientConnection(ClientWindow clientWindow) throws UnknownHostException {
        try {
            socket = new Socket(HOST, PORT);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            clientWindow.sendButton.addActionListener(e -> {
                try {
                    out.writeUTF(clientWindow.messageText.getText());
                    clientWindow.messageText.setText(null);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String strFromServer = in.readUTF();
                            if(strFromServer.startsWith("//вход_выполнен")) {
                                setAuthorized(true);
                                break;
                            }
                            clientWindow.chat.append(strFromServer + "\n");
                        }
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.equalsIgnoreCase("/end")) {
                                break;
                            }
                            clientWindow.chat.append(strFromServer);
                            clientWindow.chat.append("\n");
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

}
