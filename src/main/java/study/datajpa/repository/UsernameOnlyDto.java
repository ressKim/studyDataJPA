package study.datajpa.repository;

public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) {//파라미터 명가지고 한다.
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
