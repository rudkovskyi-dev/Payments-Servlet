package ua.rudkovskyi.payments.util;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.Transaction;
import ua.rudkovskyi.payments.bean.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class DAOUtil {
    private DAOUtil() {
        throw new IllegalStateException("Utility class");
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

    public static User createDestinationUserFromResultSet(ResultSet resSet) throws SQLException {
        Set<Role> roles = EnumSet.noneOf(Role.class);
        Arrays.stream(resSet.getString("d_roles").split(", "))
                .forEach(r -> roles.add(Role.valueOf(r)));
        return new User(
                resSet.getLong("d_user_id"),
                resSet.getString("d_username"),
                resSet.getString("d_password"),
                resSet.getBoolean("d_is_active"),
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

    public static Balance createSourceBalanceFromResultSetAndUser(ResultSet resSet, User user) throws SQLException {
        return new Balance(
                resSet.getLong("b_id"),
                resSet.getString("name"),
                resSet.getLong("s_amount"),
                resSet.getBoolean("is_locked"),
                resSet.getBoolean("is_requested"),
                user
        );
    }

    public static Balance createDestinationBalanceFromResultSetAndUser(ResultSet resSet, User user) throws SQLException {
        return new Balance(
                resSet.getLong("d_b_id"),
                resSet.getString("d_name"),
                resSet.getLong("d_amount"),
                resSet.getBoolean("d_is_locked"),
                resSet.getBoolean("d_is_requested"),
                user
        );
    }

    public static Transaction createTransactionFromResultSetAndSourceBalanceAndDestinationBalance(
            ResultSet resSet, Balance sourceBalance, Balance destinationBalance) throws SQLException {
        return new Transaction(
                resSet.getLong("id"),
                sourceBalance,
                destinationBalance,
                resSet.getLong("amount"),
                resSet.getBoolean("is_sent")
        );
    }
}
