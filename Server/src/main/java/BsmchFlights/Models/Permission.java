package BsmchFlights.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_permissions")
public class Permission {
    @Id
    @Column(name="id")
    private int id;

    @Column(name="name")
    private String name;

    public Permission() {}

    public Permission(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return  id + " " + name;
    }

    public static Permission stringToPermission(String str) {
        String[] details = str.split(" ");

        return new Permission(Integer.parseInt(details[0]), details[1]);
    }
}
