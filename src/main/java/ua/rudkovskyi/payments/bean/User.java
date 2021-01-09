package ua.rudkovskyi.payments.bean;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class User {
    private Long id;
    private String username;
    private String password;
    private boolean isActive;

    private Set<Role> roles;

    public User(){
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isActive = true;
        this.roles = EnumSet.noneOf(Role.class);
        this.roles.add(Role.USER);
    }

    public User(Long id, String username, String password, boolean isActive, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection getAuthorities() {
        return getRoles();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return isActive();
    }

    public boolean isAdmin() {
        return this.getRoles().contains(Role.ADMIN);
    }
}
