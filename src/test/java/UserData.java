public class UserData {
    private String email;
    private String name;
    private String password;

    public UserData(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserData() {
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
