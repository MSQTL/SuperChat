package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Post {
    private ServerConnection serverConnection;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String name;
    public String getName(){
        return name;
    }
    public Post(ServerConnection serverConnection, Socket socket){
        try{
            this.serverConnection = serverConnection;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try{
                    authentication();
                    readMessages();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    closeConnection();
                }
            }).start();
        }
        catch (IOException e) {
            throw new RuntimeException("Проблемы какие-то...");
        }
    }
    public void authentication() throws IOException{
        while (true) {
            String string = inputStream.readUTF();
            if (string.startsWith("/войти")){
                String[] parts = string.split("\\s");
                String nickname = serverConnection.getAuthService().getNicknameByLoginPassword(parts[1], parts[2]);
                if (nickname != null){
                    if (!serverConnection.isNickBusy(nickname)){
                        sendMessage("/вход_выполнен" + nickname);
                        name = nickname;
                        serverConnection.broadcastMsg(name + " зашел в чат");
                        serverConnection.subscribe(this);
                        return;
                    }
                    else {
                        sendMessage("Учетная запись уже используется");
                    }
                }else{
                    sendMessage("Неверный логин/пароль");
                }
            }
        }
    }
    public void readMessages() throws IOException {
        while (true) {
            String stringFromClient = inputStream.readUTF();
            System.out.println("от " + name + ": " + stringFromClient);
            if (stringFromClient.equals("/конец")) {
                return;
            }
            serverConnection.broadcastMsg(name + ": " + stringFromClient);
        }
    }
    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection() {
        serverConnection.unsubscribe(this);
        serverConnection.broadcastMsg(name + " вышел из чата");
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
