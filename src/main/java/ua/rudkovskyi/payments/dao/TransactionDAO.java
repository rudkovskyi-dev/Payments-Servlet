package ua.rudkovskyi.payments.dao;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.bean.Transaction;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.util.DAOUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    public static final String SELECT_TRANSACTION_SOURCE = "SELECT * FROM transaction t INNER JOIN(";
    public static final String SELECT_BALANCE_SOURCE = "SELECT b.id AS b_id, b.name, b.amount AS s_amount, " +
            "b.double_amount AS s_double_amount, b.is_locked, b.is_requested, b.user_id, " +
            "u.username, u.password, u.is_active, u.roles ";
    public static final String FROM_BALANCE_INNER_START = "FROM balance b INNER JOIN (";
    public static final String SELECT_USER = "SELECT u.id, u.username, u.password, u.is_active, " +
            "GROUP_CONCAT(r.roles ORDER BY r.roles SEPARATOR ', ') as 'roles' ";
    public static final String FROM_USER_INNER = "FROM user u INNER JOIN user_role r ON u.id = r.user_id " +
            "GROUP BY u.id HAVING u.id IS NOT NULL";
    public static final String FROM_BALANCE_SOURCE_INNER_END = ") u ON b.user_id = u.id HAVING b_id IS NOT NULL";
    public static final String TRANSACTION_SOURCE_ON_INNER = ") s ON t.source_id = s.b_id INNER JOIN(";
    public static final String SELECT_BALANCE_DESTINATION = "SELECT b.id AS d_b_id, b.name AS d_name, " +
            "b.amount AS d_amount, b.double_amount AS d_double_amount, b.is_locked AS d_is_locked, " +
            "b.is_requested AS d_is_requested, b.user_id AS d_user_id, u.username AS d_username, " +
            "u.password AS d_password, u.is_active AS d_is_active, u.roles AS d_roles ";
    public static final String FROM_BALANCE_DESTINATION_INNER_END = ") u ON b.user_id = u.id HAVING d_b_id IS NOT NULL";
    public static final String TRANSACTION_DESTINATION_ON = ") d ON t.destination_id = d.d_b_id ";
    public static final String WHERE_BALANCE_ID = "WHERE s.b_id = ? OR d.d_b_id = ? HAVING t.id IS NOT NULL";
    public static final String WHERE_TRANSACTION_ID = "WHERE t.id = ? HAVING t.id IS NOT NULL";
    public static final String FROM_BALANCE_SELECT_USER = FROM_BALANCE_INNER_START + SELECT_USER + FROM_USER_INNER;

    public static boolean findIfTransactionExistsByUserIdAndBalanceIdAndTransactionId(
            Connection conn, Long userId, Long balanceId, Long transactionId) throws SQLException {
        String sql = "SELECT t.id FROM transaction t INNER JOIN(SELECT b.id AS b_id, b.user_id FROM balance b " +
                "HAVING b.id IS NOT NULL) s ON t.source_id = s.b_id INNER JOIN (" +
                "SELECT b.id AS d_b_id, b.user_id AS d_user_id FROM balance b HAVING b.id IS NOT NULL" +
                ") d ON t.destination_id = d.d_b_id WHERE (s.user_id = ? OR d.d_user_id = ?) AND (t.source_id = ? OR " +
                "t.destination_id = ?) AND t.id = ? HAVING t.id IS NOT NULL;";
        PreparedStatement prepState = conn.prepareStatement(sql);

        prepState.setBigDecimal(1, BigDecimal.valueOf(userId));
        prepState.setBigDecimal(2, BigDecimal.valueOf(userId));
        prepState.setBigDecimal(3, BigDecimal.valueOf(balanceId));
        prepState.setBigDecimal(4, BigDecimal.valueOf(balanceId));
        prepState.setBigDecimal(5, BigDecimal.valueOf(transactionId));
        ResultSet resSet = prepState.executeQuery();
        if (resSet.next()) {
            return true;
        }
        return false;
    }

    public static List<Transaction> findTransactionsByBalanceId(Connection conn, long id) throws SQLException {
        String sql = SELECT_TRANSACTION_SOURCE + SELECT_BALANCE_SOURCE + FROM_BALANCE_SELECT_USER +
                FROM_BALANCE_SOURCE_INNER_END + TRANSACTION_SOURCE_ON_INNER + SELECT_BALANCE_DESTINATION +
                FROM_BALANCE_SELECT_USER + FROM_BALANCE_DESTINATION_INNER_END + TRANSACTION_DESTINATION_ON +
                WHERE_BALANCE_ID;

        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id));
        prepState.setBigDecimal(2, BigDecimal.valueOf(id));
        ResultSet resSet = prepState.executeQuery();
        List<User> usersInMemory = new ArrayList<>();
        List<Balance> balancesInMemory = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();
        while(resSet.next()) {
            long sourceUserId = resSet.getLong("user_id");
            long destUserId = resSet.getLong("d_user_id");
            User sourceUser = null;
            User destinationUser = null;
            for (User u : usersInMemory) {
                long uId = u.getId();
                if (uId == sourceUserId) {
                    sourceUser = u;
                }
                if (uId == destUserId) {
                    destinationUser = u;
                }
            }
            if (sourceUser == null) {
                sourceUser = DAOUtil.createUserFromResultSet(resSet);
                usersInMemory.add(sourceUser);
            }
            if (destinationUser == null) {
                destinationUser = DAOUtil.createDestinationUserFromResultSet(resSet);
                usersInMemory.add(destinationUser);
            }

            long sourceBalanceId = resSet.getLong("b_id");
            long destBalanceId = resSet.getLong("d_b_id");
            Balance sourceBalance = null;
            Balance destinationBalance = null;
            for (Balance b : balancesInMemory) {
                long bId = b.getId();
                if (bId == sourceBalanceId) {
                    sourceBalance = b;
                }
                if (bId == destBalanceId) {
                    destinationBalance = b;
                }
            }
            if (sourceBalance == null) {
                sourceBalance = DAOUtil.createSourceBalanceFromResultSetAndUser(resSet, sourceUser);
                balancesInMemory.add(sourceBalance);
            }
            if (destinationBalance == null) {
                destinationBalance = DAOUtil.createDestinationBalanceFromResultSetAndUser(resSet, destinationUser);
                balancesInMemory.add(destinationBalance);
            }
            transactions.add(DAOUtil.createTransactionFromResultSetAndSourceBalanceAndDestinationBalance(
                    resSet, sourceBalance, destinationBalance));
        }
        return transactions;
    }

    public static Transaction findTransactionById(Connection conn, long id) throws SQLException {
        String sql = SELECT_TRANSACTION_SOURCE + SELECT_BALANCE_SOURCE + FROM_BALANCE_SELECT_USER +
                FROM_BALANCE_SOURCE_INNER_END + TRANSACTION_SOURCE_ON_INNER + SELECT_BALANCE_DESTINATION +
                FROM_BALANCE_SELECT_USER + FROM_BALANCE_DESTINATION_INNER_END + TRANSACTION_DESTINATION_ON +
                WHERE_TRANSACTION_ID;
        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id));
        ResultSet resSet = prepState.executeQuery();
        if (resSet.next()) {
            User sourceUser = DAOUtil.createUserFromResultSet(resSet);
            User destinationUser = DAOUtil.createDestinationUserFromResultSet(resSet);
            Balance sourceBalance = DAOUtil.createSourceBalanceFromResultSetAndUser(resSet, sourceUser);
            Balance destinationBalance = DAOUtil.createDestinationBalanceFromResultSetAndUser(resSet, destinationUser);
            return DAOUtil.createTransactionFromResultSetAndSourceBalanceAndDestinationBalance(
                    resSet, sourceBalance, destinationBalance);
        }
        return null;
    }

    public static void createTransactionByBalanceSourceIdAndDestinationBalanceId(
            Connection conn, Transaction transaction, long sourceId, long destinationId) throws SQLException {
        String sql = "INSERT INTO transaction(id, amount, double_amount, is_sent, source_id, destination_id) " +
                "VALUES (?,?,?,?,?,?)";
        PreparedStatement prepState = conn.prepareStatement(sql);
        transaction.setId(selectAndIncrementTransactionId(conn));

        prepState.setBigDecimal(1, BigDecimal.valueOf(transaction.getId()));
        prepState.setBigDecimal(2, BigDecimal.valueOf(transaction.getAmount()));
        prepState.setDouble(3, transaction.getDoubleAmount());
        prepState.setBoolean(4, transaction.isSent());
        prepState.setBigDecimal(5, BigDecimal.valueOf(sourceId));
        prepState.setBigDecimal(6, BigDecimal.valueOf(destinationId));
        prepState.executeUpdate();
    }

    public static long selectAndIncrementTransactionId(Connection conn) throws SQLException {
        String sqlSelect = "SELECT h.next_val FROM hibernate_sequence_3 h LIMIT 1 FOR UPDATE";
        String sqlUpdate = "UPDATE hibernate_sequence_3 h SET h.next_val = ?";
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resSet = statement.executeQuery(sqlSelect);
        resSet.next();
        long id = resSet.getLong("next_val");
        PreparedStatement prepState = conn.prepareStatement(sqlUpdate);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id + 1));
        prepState.executeUpdate();
        return id;
    }

    public static boolean completeTransaction(Connection conn, Transaction transaction) throws SQLException {
        long sourceId = transaction.getSource().getId();
        long destinationId = transaction.getDestination().getId();
        long transactionId = transaction.getId();

        String sqlSelectSource = "SELECT s.amount, s.double_amount, s.is_locked FROM balance s WHERE s.id = " +
                sourceId + " FOR UPDATE";
        String sqlSelectDestination = "SELECT d.amount, d.double_amount, d.is_locked FROM balance d WHERE d.id = " +
                destinationId + " FOR UPDATE";
        String sqlSelectTransaction = "SELECT t.amount, t.double_amount, t.is_sent FROM transaction t WHERE t.id = " +
                transactionId + " FOR UPDATE";
        String sqlUpdateSource = "UPDATE balance s SET s.amount = ?, s.double_amount = ? WHERE s.id = " +
                sourceId;
        String sqlUpdateDestination = "UPDATE balance s SET s.amount = ?, s.double_amount = ? WHERE s.id = " +
                destinationId;
        String sqlUpdateTransaction = "UPDATE transaction t SET t.is_sent = ? WHERE t.id = " +
                transactionId;

        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resSet = statement.executeQuery(sqlSelectTransaction);
        if (resSet.next()){
            boolean isSent = resSet.getBoolean("is_sent");
            if (!isSent){
                long transactionAmount = resSet.getLong("amount");
                ResultSet sourceResultSet = statement.executeQuery(sqlSelectSource);
                if (sourceResultSet.next()){
                    boolean isLocked = sourceResultSet.getBoolean("is_locked");
                    if (!isLocked && transactionAmount > 0) {
                        long sourceBalance = sourceResultSet.getLong("s.amount");
                        boolean isSufficient = sourceBalance >= transactionAmount;
                        if (isSufficient){
                            sourceBalance -= transactionAmount;
                            ResultSet destinationResultSet = statement.executeQuery(sqlSelectDestination);
                            if (destinationResultSet.next()){
                                long destinationBalance = destinationResultSet.getLong("d.amount");
                                destinationBalance += transactionAmount;
                                PreparedStatement prepStateTransaction = conn.prepareStatement(sqlUpdateTransaction);
                                prepStateTransaction.setBoolean(1,true);

                                PreparedStatement prepStateSource = conn.prepareStatement(sqlUpdateSource);
                                prepStateSource.setBigDecimal(1, BigDecimal.valueOf(sourceBalance));
                                prepStateSource.setDouble(2, (sourceBalance / 100.0));

                                PreparedStatement prepStateDestination = conn.prepareStatement(sqlUpdateDestination);
                                prepStateDestination.setBigDecimal(
                                        1, BigDecimal.valueOf(destinationBalance));
                                prepStateDestination.setDouble(
                                        2, (destinationBalance / 100.0));

                                prepStateSource.executeUpdate();
                                prepStateDestination.executeUpdate();
                                prepStateTransaction.executeUpdate();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void deleteTransactionById(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM transaction t WHERE t.id = ?";
        PreparedStatement prepState = conn.prepareStatement(sql);
        prepState.setBigDecimal(1, BigDecimal.valueOf(id));
        prepState.execute();
    }
}
