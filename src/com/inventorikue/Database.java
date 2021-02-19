package com.inventorikue;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static Connection connection;
    public static Connection getConnection(){
        if (connection==null){
            try{
                String username = "root";
                String password = "";
                String url = "jdbc:mysql://localhost:3306/thecake";

                MysqlDataSource source = new MysqlDataSource();
                source.setUser(username);
                source.setPassword(password);
                source.setURL(url);

                connection = source.getConnection();
            } catch (SQLException ex){
                System.out.println("Tidak dapat menghubungkan ke database.");
            }
        }

        return connection;
    }
}
