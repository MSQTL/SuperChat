package sample;

public interface Auth {
    String getNicknameByLoginPassword(String login, String password);

    void start();
    void stop();
}
