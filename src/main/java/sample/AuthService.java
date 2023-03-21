package sample;

import Windows.ServerWindow;

import java.util.ArrayList;
import java.util.List;

public class AuthService implements Auth{
    private class User{
        private final String login;
        private final String password;
        private final String nickname;
        public User(String login, String password, String nickname){
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }
    private List<User> users;
    private ServerWindow serverWindow;
    public void start() {
        serverWindow.eventLog.append("Сервис аутентификации запущен" + "\n");
    }
    public void stop() {
        serverWindow.eventLog.append("Сервис аутентификации остановлен" + "\n");
    }
    public AuthService(ServerWindow serverWindow){
        this.serverWindow = serverWindow;
        users = new ArrayList<>();
        users.add(new User("sonya", "koza", "Sonya"));
        users.add(new User("serg", "2802", "Serg"));
        users.add(new User("user1", "0", "User1"));
    }
    public String getNicknameByLoginPassword(String login, String password){
        for(User user : users){
            if(user.login.equals(login) && user.password.equals(password)) return user.nickname;
        }
        return null;
    }
}
