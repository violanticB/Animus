package animus.item;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Ethan Borawski
 */

@Entity
public class AnimusItem {

    @Id
    private Long id;
    private String stackBase64;
    private int index;
    private boolean locked;

    public AnimusItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStackBase64() {
        return stackBase64;
    }

    public void setStackBase64(String stackBase64) {
        this.stackBase64 = stackBase64;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
