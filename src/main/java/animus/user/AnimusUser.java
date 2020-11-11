package animus.user;

import java.util.UUID;

/**
 * @author Ethan Borawski
 */
public class AnimusUser {

    private int user_id;
    private UUID user_uuid;
    private String user_name;

    public AnimusUser() {
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public UUID getUser_uuid() {
        return user_uuid;
    }

    public void setUser_uuid(UUID user_uuid) {
        this.user_uuid = user_uuid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
