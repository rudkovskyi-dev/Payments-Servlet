package ua.rudkovskyi.payments.dao;

import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class UserDAO {
    private static final String SELECT = "SELECT u.id, u.username, u.password, u.is_active, ";
    private static final String GROUP_CONCAT = "GROUP_CONCAT(r.roles ORDER BY r.roles SEPARATOR ', ') as 'roles' ";
    private static final String FROM = "FROM user u INNER JOIN user_role r ON u.id = r.user_id ";
    private static final String HAVING = " HAVING u.id IS NOT NULL";

    private UserDAO() {
        throw new IllegalStateException("DAO class");
    }

    public static boolean findIfUserExistsById(Connection conn, Long id) throws SQLException {
        String sql = "SELECT u.id FROM user u " +
                "WHERE u.id = ?" + HAVING;
        PreparedStatement prepState = conn.prepareStatement(sql);

        prepState.setBigDecimal(1, BigDecimal.valueOf(id));
        ResultSet resSet = prepState.executeQuery();
        if (resSet.next()) {
            return true;
        }
        return false;
    }

    public static User findUserById(Connection conn, Long id)
            throws SQLException {

        String sql = SELECT + GROUP_CONCAT + FROM +
                "WHERE u.id = ?" + HAVING;

        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setLong(1, id);
        ResultSet resSet = prepState.executeQuery();

        if (resSet.next()) {
            Set<Role> roles = EnumSet.noneOf(Role.class);
            Arrays.stream(resSet.getString("roles").split(", "))
                    .forEach(r -> roles.add(Role.valueOf(r)));
            User user = new User();
            user.setId(resSet.getBigDecimal("id").longValue());
            user.setUsername(resSet.getString("username"));
            user.setPassword(resSet.getString("password"));
            user.setActive(resSet.getBoolean("is_active"));
            user.setRoles(roles);
            return user;
        }
        return null;
    }

    public static List<User> findAllUsers(Connection conn) throws SQLException {
        String sql = SELECT + GROUP_CONCAT + FROM + "GROUP BY u.id" + HAVING;
        Statement statement = conn.createStatement();
        ResultSet resSet = statement.executeQuery(sql);
        List<User> users = new ArrayList<>();
        while(resSet.next()){
            Set<Role> roles = EnumSet.noneOf(Role.class);
            Arrays.stream(resSet.getString("roles").split(", "))
                    .forEach(r -> roles.add(Role.valueOf(r)));
            User user = new User();
            user.setId(resSet.getLong("id"));
            user.setUsername(resSet.getString("username"));
            user.setPassword(resSet.getString("password"));
            user.setActive(resSet.getBoolean("is_active"));
            user.setRoles(roles);
            users.add(user);
        }
        return users;
    }

    public static User findUserByUsername(Connection conn, String username)
            throws SQLException {

        String sql = SELECT + GROUP_CONCAT + FROM +
                "WHERE u.username = ?" + HAVING;

        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setString(1, username);
        ResultSet resSet = prepState.executeQuery();

        if (resSet.next()) {
            Set<Role> roles = EnumSet.noneOf(Role.class);
            Arrays.stream(resSet.getString("roles").split(", "))
                    .forEach(r -> roles.add(Role.valueOf(r)));
            User user = new User();
            user.setId(resSet.getLong("id"));
            user.setUsername(username);
            user.setPassword(resSet.getString("password"));
            user.setActive(resSet.getBoolean("is_active"));
            user.setRoles(roles);
            return user;
        }
        return null;
    }

    public static User findUserByUsernameAndPassword(
            Connection conn,
            String username,
            String password)
            throws SQLException {

        String sql = SELECT + GROUP_CONCAT + FROM +
                "WHERE u.username = ? AND u.password = ?" + HAVING;

        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setString(1, username);
        prepState.setString(2, password);
        ResultSet resSet = prepState.executeQuery();

        if (resSet.next()) {
            Set<Role> roles = EnumSet.noneOf(Role.class);
            Arrays.stream(resSet.getString("roles").split(", "))
                    .forEach(r -> roles.add(Role.valueOf(r)));
            User user = new User();
            user.setId(resSet.getLong("id"));
            user.setUsername(username);
            user.setPassword(password);
            user.setActive(resSet.getBoolean("is_active"));
            user.setRoles(roles);
            return user;
        }
        return null;
    }

    public static long selectAndIncrementUserId(Connection conn) throws SQLException {
        String sqlSelect = "SELECT h.next_val FROM hibernate_sequence h LIMIT 1 FOR UPDATE";
        String sqlUpdate = "UPDATE hibernate_sequence h SET h.next_val = ?";
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resSet = statement.executeQuery(sqlSelect);
        resSet.next();
        long id = resSet.getLong("next_val");
        PreparedStatement prepState = conn.prepareStatement(sqlUpdate);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id + 1));
        prepState.executeUpdate();
        return id;
    }

    public static void updateUserRoles(Connection conn, User user) throws SQLException {
        String sqlDelete = "DELETE FROM user_role r WHERE user_id = ?";
        String sqlInsert = "INSERT INTO user_role(user_id, roles) " +
                "VALUES (?,?)";
        PreparedStatement prepStateDelete = conn.prepareStatement(sqlDelete);
        PreparedStatement prepStateInsert = conn.prepareStatement(sqlInsert);
        prepStateDelete.setBigDecimal(1, BigDecimal.valueOf(user.getId()));
        prepStateDelete.execute();
        for (Role r : user.getRoles()){
            prepStateInsert.setBigDecimal(1, BigDecimal.valueOf(user.getId()));
            prepStateInsert.setString(2, r.toString());
            prepStateInsert.executeUpdate();
        }
    }

    public static void createUser(
            Connection conn,
            User user) throws SQLException {
        String sql = "INSERT INTO user(id, username, password, is_active) VALUES (?,?,?,?)";

        PreparedStatement prepState = conn.prepareStatement(sql);

        user.setId(selectAndIncrementUserId(conn));
        prepState.setBigDecimal(1, BigDecimal.valueOf(user.getId()));
        prepState.setString(2, user.getUsername());
        prepState.setString(3, user.getPassword());
        prepState.setBoolean(4, user.isActive());
        prepState.executeUpdate();
        updateUserRoles(conn, user);
    }

    public static void updateUser(Connection conn, User user) throws SQLException {
        String sqlSelect = "SELECT u.id, u.username, u.password, u.is_active " +
                "FROM user u WHERE u.id = " + user.getId() + " HAVING u.id IS NOT NULL FOR UPDATE";
        String sqlUpdate = "UPDATE user u SET u.username = ?, u.password = ?, u.is_active = ? " +
                "WHERE u.id = ?";
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resSet = statement.executeQuery(sqlSelect);
        if (resSet.next()) {
            PreparedStatement prepState = conn.prepareStatement(sqlUpdate);
            prepState.setString(1, user.getUsername());
            prepState.setString(2, user.getPassword());
            prepState.setBoolean(3, user.isActive());
            prepState.setBigDecimal(4, BigDecimal.valueOf(user.getId()));
            prepState.executeUpdate();
            updateUserRoles(conn, user);
        }
    }
}
