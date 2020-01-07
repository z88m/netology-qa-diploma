package ru.netology.testUtils;

import java.sql.*;

public class TestSQLHelper {

    private static final String dbUrl = System.getProperty("db.url"); //для запуска из консоли
    //private static final String dbUrl = "jdbc:mysql://127.0.0.1:3306/app"; //для запуска из Idea
    //private static final String dbUrl = "jdbc:postgresql://127.0.0.1:5432/app"; //для запуска из Idea
    private static final String dbUser = "app";
    private static final String dbPass = "pass";

    public static void cleanTables() throws SQLException {
        String cleanCreditTables = "DELETE FROM app.credit_request_entity;";
        String cleanOrderTable = "DELETE FROM app.order_entity;";
        String cleanPaymentTable = "DELETE FROM app.payment_entity;";
        Connection c = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        Statement s = c.createStatement();
        s.executeUpdate(cleanCreditTables);
        s.executeUpdate(cleanOrderTable);
        s.executeUpdate(cleanPaymentTable);
        c.close();
    }


    public static String getOperationStatus(String table) {
        String status = "";
        try {
            Connection c = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            Statement s = c.createStatement();
            ResultSet resultSet = s.executeQuery(
                    "SELECT * FROM " + table + " ORDER BY id DESC LIMIT 1;");
            while (resultSet.next()) {
                status = resultSet.getNString("status");
            }
            c.close();
        } catch (SQLException e) {
            e.getErrorCode();
        }
        return status;
    }
}