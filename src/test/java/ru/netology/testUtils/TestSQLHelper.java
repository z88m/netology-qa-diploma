package ru.netology.testUtils;

import java.sql.*;

public class TestSQLHelper {

    //private static final String dbUrl = System.getProperty("db.url"); //для запуска из консоли
    //private static final String dbUrl = "jdbc:mysql://127.0.0.1:3306/app"; //для запуска из Idea
    private static final String dbUrl = "jdbc:postgres://127.0.0.1:5432/app"; //для запуска из Idea
    private static final String dbUser = "app";
    private static final String dbPass = "pass";

    public static void cleanTables() throws SQLException {
        String cleanCreditTables = "DELETE FROM app.credit_request_entity;";
        String cleanOrderTable = "DELETE FROM app.order_entity;";
        String cleanPaymentTable = "DELETE FROM app.payment_entity;";
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        Statement statement = conn.createStatement();
        statement.executeUpdate(cleanCreditTables);
        statement.executeUpdate(cleanOrderTable);
        statement.executeUpdate(cleanPaymentTable);
        conn.close();
    }


    public static String getOperationStatus(String table) {
        String status = "";
        try {
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM " + table + " ORDER BY id DESC LIMIT 1;");
            while (resultSet.next()) {
                status = resultSet.getNString("status");
            }
            conn.close();
        } catch (SQLException exception) {
            exception.getErrorCode();
        }
        return status;
    }
}