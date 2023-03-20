package sample;

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

    public ServerConnection(){
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService = new AuthService();
            authService.start();
            postList = new ArrayList<>();
            while (true){
                System.out.println("Сервер ожидает подключения");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключен!");
                new Post(this, socket);
            }
        }
        catch (IOException e) {
            System.out.println("Ошибка в работе сервера");;
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
