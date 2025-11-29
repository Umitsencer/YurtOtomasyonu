package com.yurt.view;

import com.yurt.database.DatabaseConnection;
import com.yurt.model.User;
import com.yurt.patterns.UserFactory;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginView extends BasePage {

    private JTextField txtTcNo;
    private JPasswordField txtSifre;
    private JButton btnGiris;

    public LoginView() {
        super("Yurt Otomasyonu - Giriş", 400, 300);
        initializeComponents();
        setVisible(true); // Ekranı görünür yap
    }

    @Override
    public void initializeComponents() {
        // TC Kimlik Label ve Text
        JLabel lblTc = new JLabel("TC Kimlik No:");
        lblTc.setBounds(50, 50, 100, 25);
        add(lblTc);

        txtTcNo = new JTextField();
        txtTcNo.setBounds(150, 50, 150, 25);
        add(txtTcNo);

        // Şifre Label ve Text
        JLabel lblPass = new JLabel("Şifre:");
        lblPass.setBounds(50, 100, 100, 25);
        add(lblPass);

        txtSifre = new JPasswordField();
        txtSifre.setBounds(150, 100, 150, 25);
        add(txtSifre);

        // Giriş Butonu
        btnGiris = new JButton("Giriş Yap");
        btnGiris.setBounds(150, 150, 100, 30);
        add(btnGiris);

        // Butona tıklama olayı
        btnGiris.addActionListener(e -> loginIslemi());
    }

    private void loginIslemi() {
        String tc = txtTcNo.getText();
        String sifre = new String(txtSifre.getPassword());

        // Veritabanından kontrol et
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM users WHERE tc_no = ? AND sifre = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tc);
            ps.setString(2, sifre);

            ResultSet rs = ps.executeQuery();

            // TEK BİR IF BLOĞU KULLANIYORUZ
            if (rs.next()) {
                String rol = rs.getString("rol");

                // Factory kullanarak nesne üretiyoruz (Factory Deseni)
                User user = UserFactory.createUser(rol);

                // Veritabanından gelen verileri nesneye dolduruyoruz
                if (user != null) {
                    user.setId(rs.getInt("id"));
                    user.setTcNo(rs.getString("tc_no"));
                    user.setAd(rs.getString("ad"));
                    user.setSoyad(rs.getString("soyad"));

                    // Giriş ekranını kapat
                    this.dispose();

                    // Role göre ilgili ekranı aç
                    if (rol.equalsIgnoreCase("OGRENCI")) {
                        new StudentView(user); // Öğrenci panelini aç
                    } else if (rol.equalsIgnoreCase("PERSONEL")) {
                        new PersonnelView(user); // ARTIK PERSONEL PANELİNİ AÇIYOR
                    }
                }

            } else {
                // Kullanıcı bulunamadıysa
                JOptionPane.showMessageDialog(this, "Hatalı TC veya Şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası!");
        }
    }
}