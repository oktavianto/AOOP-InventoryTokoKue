package com.inventorikue;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.inventorikue.model.Produk;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    private JPanel panel;
    private JTable tblProduk;
    private JTextField txtPencarian;
    private JButton refreshResetFormButton;
    private JTextField txtNama;
    private JTextField txtHarga;
    private JTextField txtJumlah;
    private JButton btnTambah;
    private JButton btnHapus;
    private JButton btnEdit;
    private JRadioButton darkModeRadioButton;
    private JLabel txtTitle;
    private JComboBox selectJenis;
    private JComboBox selectSatuan;

    private DefaultTableModel tableModel;
    private Produk produk;
    private String selectedBarang = "";

    public App() {
        initComponent();
        initTable();
        txtPencarian.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                cariData(txtPencarian.getText());
            }
        });
    }

    private void initComponent() {
        produk = new Produk();
        tblProduk.setShowHorizontalLines(true);
        tblProduk.setShowVerticalLines(true);
        txtPencarian.putClientProperty("JTextField.placeholderText", "Pencarian...");

        btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validasiTidakKosong()) {
                    produk.insert(txtNama.getText(), selectSatuan.getSelectedItem().toString(), selectJenis.getSelectedItem().toString(), Integer.parseInt(txtHarga.getText()), Integer.parseInt(txtJumlah.getText()));
                    initTable();
                    resetForm();
                }
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBarang.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Belum memilih data untuk di edit.");
                } else {
                    if (validasiTidakKosong()) {
                        produk.update(selectedBarang,txtNama.getText(),selectSatuan.getSelectedItem().toString(),selectJenis.getSelectedItem().toString(),Integer.parseInt(txtHarga.getText()),Integer.parseInt(txtJumlah.getText()));
                        initTable();
                        resetForm();
                    }
                }
            }
        });

        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBarang.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Belum memilih data untuk di hapus.");
                } else {
                    if (validasiTidakKosong()) {
                        produk.delete(selectedBarang);
                        initTable();
                        resetForm();
                    }
                }
            }
        });

        refreshResetFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initTable();
                resetForm();
                txtPencarian.setText("");
            }
        });

        tblProduk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int tampil = tblProduk.rowAtPoint(e.getPoint());
                if (tampil >= 0) {
                    selectedBarang = tblProduk.getValueAt(tampil, 0).toString();
                    txtNama.setText(tblProduk.getValueAt(tampil, 1).toString());
                    if (tblProduk.getValueAt(tampil, 2).toString().equals("Kg")) {
                        selectSatuan.setSelectedIndex(1);
                    } else if (tblProduk.getValueAt(tampil, 2).toString().equals("Butir")) {
                        selectSatuan.setSelectedIndex(2);
                    } else if (tblProduk.getValueAt(tampil, 2).toString().equals("Botol")) {
                        selectSatuan.setSelectedIndex(3);
                    } else if (tblProduk.getValueAt(tampil, 2).toString().equals("Buah")) {
                        selectSatuan.setSelectedIndex(4);
                    }
                    if (tblProduk.getValueAt(tampil, 3).toString().equals("Dekorasi")) {
                        selectJenis.setSelectedIndex(1);
                    } else if (tblProduk.getValueAt(tampil, 3).toString().equals("Baku")) {
                        selectJenis.setSelectedIndex(2);
                    }
                    txtHarga.setText(tblProduk.getValueAt(tampil, 4).toString());
                    txtJumlah.setText(tblProduk.getValueAt(tampil, 5).toString());
                }
            }
        });

        darkModeRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (darkModeRadioButton.isSelected()) {
                    try {
                        txtTitle.setForeground(Color.WHITE);
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                    } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                        unsupportedLookAndFeelException.printStackTrace();
                    }
                } else {
                    try {
                        UIManager.setLookAndFeel(new FlatLightLaf());
                        txtTitle.setForeground(Color.BLACK);
                    } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                        unsupportedLookAndFeelException.printStackTrace();
                    }
                }
                FlatLaf.updateUI();
            }
        });
    }

    private boolean validasiTidakKosong() {
        if (txtNama.getText().isEmpty()
                || selectSatuan.getSelectedIndex() == 0
                || selectJenis.getSelectedIndex() == 0
                || txtHarga.getText().isEmpty()
                || txtJumlah.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harap lengkapi seluruh data.");
            return false;
        } else {
            return true;
        }
    }

    private void initTable(){
        tblProduk.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Kode", "Nama Bahan", "Satuan", "Jenis", "Harga", "Jumlah"
                }
        ));
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Kode");
        tableModel.addColumn("Nama Bahan");
        tableModel.addColumn("Satuan");
        tableModel.addColumn("Jenis");
        tableModel.addColumn("Harga");
        tableModel.addColumn("Jumlah");
        loadData();
        tblProduk.setModel(tableModel);
        tblProduk.setFillsViewportHeight(true);
    }

    private void loadData() {
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        try{
            Connection connection = Database.getConnection();
            String query = "SELECT * FROM produk";
            Statement statement = (Statement) connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                Object[] data = new Object[6];
                data[0] = resultSet.getString("KodeBahan");
                data[1] = resultSet.getString("NamaBahan");
                data[2] = resultSet.getString("Satuan");
                data[3] = resultSet.getString("JenisBahan");
                data[4] = resultSet.getInt("Harga");
                data[5] = resultSet.getInt("Jumlah");
                tableModel.addRow(data);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void cariData(String keyword) {
        tableModel.getDataVector().removeAllElements();
        tableModel.fireTableDataChanged();
        try{
            Connection connection = Database.getConnection();
            String query = "SELECT * FROM produk WHERE KodeBahan like '%"+keyword+"%' OR NamaBahan like '%"+keyword+"%'";
            Statement statement = (Statement) connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                Object[] data = new Object[6];
                data[0] = resultSet.getString("KodeBahan");
                data[1] = resultSet.getString("NamaBahan");
                data[2] = resultSet.getString("Satuan");
                data[3] = resultSet.getString("JenisBahan");
                data[4] = resultSet.getInt("Harga");
                data[5] = resultSet.getInt("Jumlah");
                tableModel.addRow(data);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void resetForm() {
        selectedBarang = "";
        txtNama.setText("");
        txtHarga.setText("");
        selectJenis.setSelectedIndex(0);
        selectSatuan.setSelectedIndex(0);
        txtJumlah.setText("");
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        JFrame frame = new JFrame("Aplikasi Inventori Toko Kue");
        frame.setContentPane(new App().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
