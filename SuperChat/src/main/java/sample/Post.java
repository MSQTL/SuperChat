package sample;

import Windows.ServerWindow;

import java.io.*;
import java.net.Socket;

public class Post {
    private final ServerConnection serverConnection;
    private final Socket socket;
    private final ServerWindow serverWindow;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private String name;
    public String getName(){
        return name;
    }
    public Post(ServerConnection serverConnection, Socket socket, ServerWindow serverWindow){
        try{
            this.serverConnection = serverConnection;
            this.socket = socket;
            this.serverWindow = serverWindow;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try{
                    authentication();
                    readMessages();
                }
                catch (IOException ignored) {
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
                try {
                    String nickname = serverConnection.getAuthService().getNicknameByLoginPassword(parts[1], parts[2]);
                    if (nickname != null){
                        if (!serverConnection.isNickBusy(nickname)){
                            sendMessage("/вход_выполнен " + nickname);
                            name = nickname;
                            serverWindow.eventLog.append("Клиент " + name + " подключен!" + "\n");
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
                catch (IndexOutOfBoundsException exception){
                    sendMessage("Неверная команда");
                }
            }
            else {
                sendMessage("Неверная команда");
            }
        }
    }
    public void readMessages() throws IOException {

        while (true) {
            long time = System.currentTimeMillis();
            String stringFromClient = inputStream.readUTF();
            //тайм-аут для отключения пользователя через 60 секунд...
            if (System.currentTimeMillis() - time > 60_000){
                serverWindow.eventLog.append(name + " отключен по тайм-ауту" + "\n");
                serverConnection.broadcastMsg(name + " отключен по тайм-ауту");
                return;
            }
            serverWindow.eventLog.append("от " + name + ": " + stringFromClient + "\n");

            if (stringFromClient.equals("/конец")) {
                serverWindow.eventLog.append("Клиент " + name + " отключен!" + "\n");
                return;
            }
            if(stringFromClient.startsWith("/p")){
                String[] str = stringFromClient.split(" ", 2);
                serverWindow.eventLog.append("Отправлено приватное сообщение");
                serverWindow.eventLog.append("\n");
                if(!this.getName().equals(str[0].substring(2))) {
                    serverConnection.sendMessageToClient(this.getName(), stringFromClient);
                }
            }
            else{
                serverConnection.broadcastMsg(name + ": " + stringFromClient);
            }
        }

    }
    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException ignored) {
        }
    }
    public void closeConnection() {
        serverConnection.broadcastMsg(name + " вышел из чата");
        serverConnection.unsubscribe(this);
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
