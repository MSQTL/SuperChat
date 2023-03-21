package sample;

import Windows.ServerWindow;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection {
    final int PORT = 8080;
    private AuthService authService;
    public AuthService getAuthService(){
        return authService;
    }
    private List<Post> postList;
    private ServerWindow serverWindow;

    public ServerConnection(ServerWindow serverWindow){
        this.serverWindow = serverWindow;
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
    public synchronized void unsubscribe(Post post) {
        postList.remove(post);
    }
    public synchronized void subscribe(Post post) {
        postList.add(post);
    }
}
