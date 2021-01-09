package ua.rudkovskyi.payments.dao;

import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class UserDAO {
    private static final String SELECT = "SELECT u.id, u.username, u.password, u.is_active, ";
    private static final String GROUP_CONCAT = "GROUP_CONCAT(r.roles ORDER BY r.roles SEPARATOR ', ') as 'roles' ";
    private static final String FROM = "FROM user u INNER JOIN user_role r ON u.id = r.user_id ";
    private static final String HAVING = " HAVING u.id IS NOT NULL";

    private UserDAO() {
        throw new IllegalStateException("DAO class");
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

    public static Set<Role> getMissingFromDBUserRoles(Connection conn, User user) throws SQLException {
        String sql = "SELECT r.roles FROM user_role r WHERE user_id = ? HAVING roles IS NOT NULL";
        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(user.getId()));
        ResultSet resSet = prepState.executeQuery();
        Set<Role> roles = EnumSet.noneOf(Role.class);
        if (!resSet.next()) {
            return user.getRoles();
        } else {
            do {
                Role role = Role.valueOf(resSet.getString("roles"));
                if (!user.getRoles().contains(role)) {
                    roles.add(role);
                }
            }
            while (resSet.next());
        }
        return roles;
    }

    public static void addUserRoles(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO user_role(user_id, roles) VALUES (?,?)";
        Set<Role> roles = getMissingFromDBUserRoles(conn, user);
        for (Role r : roles) {
            PreparedStatement prepState = conn.prepareStatement(sql);
            prepState.setBigDecimal(1, BigDecimal.valueOf(user.getId()));
            prepState.setString(2, r.toString());
            prepState.executeUpdate();
        }
    }

    public static void addUser(
            Connection conn,
            User user) throws SQLException {
        String sql = "INSERT INTO user(id, username, password, is_active) VALUES (?,?,?,?)";

        PreparedStatement pstm = conn.prepareStatement(sql);

        user.setId(selectAndIncrementUserId(conn));
        pstm.setBigDecimal(1, BigDecimal.valueOf(user.getId()));
        pstm.setString(2, user.getUsername());
        pstm.setString(3, user.getPassword());
        pstm.setBoolean(4, user.isActive());
        pstm.executeUpdate();
        addUserRoles(conn, user);
    }

    //TODO Delete User and Roles, lock balances
}
