package sample;

import Windows.ServerWindow;
import java.util.ArrayList;
import java.util.List;

public class AuthService{
    public record User(String login, String password, String nickname) {
    }
    private final List<User> users;
    public List<User> getUsers(){
        return users;
    }
    private final ServerWindow serverWindow;
    public void start() {
        serverWindow.eventLog.append("Сервис аутентификации запущен" + "\n");
    }
    public void stop() {
        serverWindow.eventLog.append("Сервис аутентификации остановлен" + "\n");
    }
    public AuthService(ServerWindow serverWindow){
        this.serverWindow = serverWindow;
        users = new ArrayList<>();

        //добавление базовых пользователей
        users.add(new User("sonya", "koza", "Соня"));
        users.add(new User("serg", "2802", "Серёжа"));
        users.add(new User("user1", "0", "Пользователь_1"));
    }
    public String getNicknameByLoginPassword(String login, String password){
        for(User user : users){
            if(user.login.equals(login) && user.password.equals(password)) return user.nickname;
        }
        return null;
    }
}
