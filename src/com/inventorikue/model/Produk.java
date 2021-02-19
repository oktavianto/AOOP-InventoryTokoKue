package com.inventorikue.model;

import com.inventorikue.Database;

import javax.swing.*;
import java.sql.*;

public class Produk {

    public String latest(){
        try{
            Connection connection = Database.getConnection();
            String query = "SELECT * FROM produk ORDER BY KodeBahan DESC LIMIT 1";
            Statement statement = (Statement) connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return newId(resultSet.getString("KodeBahan"));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public String newId(String id) {
        String newId;
        String oldId = id.substring(1,4);
        int tambah = Integer.parseInt(oldId)+1;
        if (tambah < 10) {
            newId = "A00"+tambah;
        } else if (tambah > 99) {
            newId = "A"+tambah;
        } else {
            newId = "A0"+tambah;
        }
        return newId;
    }

    public void insert(String nama, String satuan, String jenis, int harga, int jumlah){
        try {
            Connection connection = Database.getConnection();
            String query = "INSERT INTO produk (KodeBahan, NamaBahan, Satuan, JenisBahan, Harga, Jumlah) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);

            statement.setString(1, latest());
            statement.setString(2, nama);
            statement.setString(3, satuan);
            statement.setString(4, jenis);
            statement.setInt(5, harga);
            statement.setInt(6, jumlah);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Berhasil menambah data.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal tambah data, silahkan coba lagi.");
            System.out.println(ex.getMessage());
        }
    }

    public void update(String kode, String nama, String satuan, String jenis, int harga, int jumlah){
        try {
            Connection connection = Database.getConnection();
            String query = "UPDATE produk SET NamaBahan=?,Satuan=?,JenisBahan=?,Harga=?,Jumlah=? WHERE KodeBahan=?";
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);

            statement.setString(1, nama);
            statement.setString(2, satuan);
            statement.setString(3, jenis);
            statement.setInt(4, harga);
            statement.setInt(5, jumlah);
            statement.setString(6, kode);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Berhasil mengedit data.");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, "Gagal edit data, silahkan coba lagi.");
        }
    }

    public void delete(String kode){
        try {
            Connection connection = Database.getConnection();
            String query = "DELETE FROM produk WHERE KodeBahan=?";
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);

            statement.setString(1, kode);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Berhasil menghapus data.");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, "Gagal hapus data, silahkan coba lagi.");
        }
    }
}
