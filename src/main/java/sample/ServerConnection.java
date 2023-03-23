package sample;

import Windows.ServerWindow;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection {
    private final ServerWindow serverWindow;
    final int PORT = 8080;
    private AuthService authService;
    public AuthService getAuthService(){
        return authService;
    }
    private List<Post> postList;

    public ServerConnection(ServerWindow serverWindow){
        this.serverWindow = serverWindow;
        serverWindow.sendButton.addActionListener(e -> broadcastFromSRV(serverWindow.messageText.getText()));
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverWindow.eventLog.append("Сервер активен" + "\n");
            authService = new AuthService(serverWindow);
            authService.start();
            postList = new ArrayList<>();
            while (true){
                Socket socket = serverSocket.accept();
                serverWindow.eventLog.append("Клиент подключен!" + "\n");
                new Post(this, socket, serverWindow);
            }
        }
        catch (IOException e) {
            serverWindow.eventLog.append("Ошибка в работе сервера" + "\n");
        }
        finally {
            if (authService != null){
                authService.stop();
            }
        }
    }

    public synchronized boolean isNickBusy(String nickname) {
        for (Post post : postList) {
            if (post.getName().equals(nickname)) {
                return true;
            }
        }
        return false;
    }
    public synchronized void broadcastMsg(String message) {
        for (Post post : postList) {
            post.sendMessage(message);
        }
    }
    public synchronized void broadcastFromSRV(String message){
        serverWindow.messageText.setText(null);
        serverWindow.eventLog.append("СЕРВЕР: " + message);
        for (Post post : postList){
            post.sendMessage("СЕРВЕР: " + message);
        }
    }
    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/клиенты ");
        for (Post post : postList) {
            sb.append(post.getName()).append(" ");
        }
        broadcastMsg(sb.toString());
    }
    public synchronized void unsubscribe(Post post) {
        postList.remove(post);
        broadcastClientsList();
    }
    public synchronized void subscribe(Post post) {
        postList.add(post);
        broadcastClientsList();
    }
    public synchronized void sendMessageToClient(String nickFrom, String message){
        String[] messageFrom = message.split(" ", 2);
        for(Post post : postList){
            if(post.getName().equals(messageFrom[0].substring(2))){
                post.sendMessage("/f" + nickFrom + " " + messageFrom[1]);
            }
        }
    }
}
