package ua.rudkovskyi.payments.dao;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BalanceDAO {
    public static final String SELECT = "SELECT b.id, b.name, b.amount, b.is_locked, b.is_requested, " +
            "b.user_id, u.username, u.password, u.is_active, u.roles ";
    public static final String FROM_START = "FROM balance b INNER JOIN (";
    public static final String INNER_SELECT = "SELECT u.id, u.username, u.password, u.is_active, " +
            "GROUP_CONCAT(r.roles ORDER BY r.roles SEPARATOR ', ') as 'roles' ";
    public static final String INNER_FROM = "FROM user u INNER JOIN user_role r ON u.id = r.user_id ";
    public static final String INNER_WHERE = "WHERE u.id = ? HAVING u.id IS NOT NULL";
    public static final String FROM_END = ") u ON b.user_id = u.id";
    public static final String WHERE = " WHERE b.id = ?";
    private static final String HAVING = " HAVING b.id IS NOT NULL";

    private BalanceDAO() {
        throw new IllegalStateException("DAO class");
    }

    public static boolean findIfBalanceExistsByBalanceIdAndUserId(Connection conn, Long userId, Long balanceId) throws SQLException {
        String sql = "SELECT b.id FROM balance b " +
                "WHERE b.user_id = ? AND b.id = ?" + HAVING;
        PreparedStatement prepState = conn.prepareStatement(sql);

        prepState.setBigDecimal(1, BigDecimal.valueOf(userId));
        prepState.setBigDecimal(2, BigDecimal.valueOf(balanceId));
        ResultSet resSet = prepState.executeQuery();
        if (resSet.next()) {
            return true;
        }
        return false;
    }

    public static Balance findBalanceByIdWithoutOwner(Connection conn, long id) throws SQLException {
        String sql = "SELECT b.id, b.name, b.amount, b.is_locked, b.is_requested " +
                "FROM balance b WHERE b.id = ? HAVING b.id IS NOT NULL";
        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id));
        ResultSet resSet = prepState.executeQuery();
        if (resSet.next()) {
            return createBalanceFromResultSetAndUser(resSet, null);
        }
        return null;
    }

    public static Balance findBalanceByIdAndOwnerId(Connection conn, long balanceId, long ownerId) throws SQLException {
        String sql = SELECT + FROM_START + INNER_SELECT + INNER_FROM + INNER_WHERE + FROM_END + WHERE + HAVING;
        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(ownerId));
        prepState.setBigDecimal(2, BigDecimal.valueOf(balanceId));
        ResultSet resSet = prepState.executeQuery();
        if (resSet.next()) {
            User user = createUserFromResultSet(resSet);
            return createBalanceFromResultSetAndUser(resSet, user);
        }
        return null;
    }

    public static List<Balance> findBalancesByOwnerId(Connection conn, long id) throws SQLException {
        String sql = SELECT + FROM_START + INNER_SELECT + INNER_FROM + INNER_WHERE + FROM_END + HAVING;
        List<Balance> balances = new ArrayList<>();

        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id));
        ResultSet resSet = prepState.executeQuery();
        if (resSet.next()) {
            User user = createUserFromResultSet(resSet);
            do {
                balances.add(createBalanceFromResultSetAndUser(resSet, user));
            }
            while (resSet.next());
        }
        return balances;
    }

    public static void createBalanceWithUserId(Connection conn, Balance balance, long id) throws SQLException {
        String sql = "INSERT INTO balance(id, name, amount, double_amount, is_locked, is_requested, user_id) " +
                "VALUES (?,?,?,?,?,?,?)";
        PreparedStatement prepState = conn.prepareStatement(sql);
        balance.setId(selectAndIncrementBalanceId(conn));

        prepState.setBigDecimal(1, BigDecimal.valueOf(balance.getId()));
        prepState.setString(2, balance.getName());
        prepState.setBigDecimal(3, BigDecimal.valueOf(balance.getAmount()));
        prepState.setDouble(4, balance.getDoubleAmount());
        prepState.setBoolean(5, balance.getIsLocked());
        prepState.setBoolean(6, balance.getIsRequested());
        prepState.setBigDecimal(7, BigDecimal.valueOf(id));
        prepState.executeUpdate();
    }

    public static void updateBalance(Connection conn, Balance balance) throws SQLException {
        String sqlSelect = "SELECT b.id, b.name, b.amount, b.double_amount, b.is_locked, b.is_requested " +
                "FROM balance b WHERE b.id = " + balance.getId() + " HAVING b.id IS NOT NULL FOR UPDATE";
        String sqlUpdate = "UPDATE balance b SET b.name = ?, b.amount = ?, b.double_amount = ?, " +
                "b.is_locked = ?, b.is_requested = ? WHERE b.id = ?";
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resSet = statement.executeQuery(sqlSelect);
        if (resSet.next()) {
            PreparedStatement prepState = conn.prepareStatement(sqlUpdate);
            prepState.setString(1, balance.getName());
            prepState.setBigDecimal(2, BigDecimal.valueOf(balance.getAmount()));
            prepState.setDouble(3, balance.getDoubleAmount());
            prepState.setBoolean(4, balance.isLocked());
            prepState.setBoolean(5, balance.isRequested());
            prepState.setBigDecimal(6, BigDecimal.valueOf(balance.getId()));
            prepState.executeUpdate();
        }
    }

    public static void deleteBalanceById(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM balance b WHERE b.id = ?";
        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id));
        prepState.execute();
    }

    public static User createUserFromResultSet(ResultSet resSet) throws SQLException {
        Set<Role> roles = EnumSet.noneOf(Role.class);
        Arrays.stream(resSet.getString("roles").split(", "))
                .forEach(r -> roles.add(Role.valueOf(r)));
        return new User(
                resSet.getLong("user_id"),
                resSet.getString("username"),
                resSet.getString("password"),
                resSet.getBoolean("is_active"),
                roles);
    }

    public static Balance createBalanceFromResultSetAndUser(ResultSet resSet, User user) throws SQLException {
        return new Balance(
                resSet.getLong("id"),
                resSet.getString("name"),
                resSet.getLong("amount"),
                resSet.getBoolean("is_locked"),
                resSet.getBoolean("is_requested"),
                user
        );
    }

    public static long selectAndIncrementBalanceId(Connection conn) throws SQLException {
        String sqlSelect = "SELECT h.next_val FROM hibernate_sequence_2 h LIMIT 1 FOR UPDATE";
        String sqlUpdate = "UPDATE hibernate_sequence_2 h SET h.next_val = ?";
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resSet = statement.executeQuery(sqlSelect);
        resSet.next();
        long id = resSet.getLong("next_val");
        PreparedStatement prepState = conn.prepareStatement(sqlUpdate);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id + 1));
        prepState.executeUpdate();
        return id;
    }
}
