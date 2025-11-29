package com.yurt.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // İSİM DEĞİŞİKLİĞİ YAPTIK: yurt_v2.db
            // Bu sayede sistem mecburen yeni ve doğru tabloları oluşturacak.
            String url = "jdbc:sqlite:yurt_v2.db";
            connection = DriverManager.getConnection(url);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {

            // Kullanıcılar
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "tc_no TEXT UNIQUE NOT NULL," +
                    "ad TEXT NOT NULL," +
                    "soyad TEXT NOT NULL," +
                    "email TEXT," +
                    "sifre TEXT NOT NULL," +
                    "rol TEXT NOT NULL)");

            // Odalar
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "oda_no TEXT UNIQUE NOT NULL," +
                    "kapasite INTEGER NOT NULL," +
                    "mevcut_kisi INTEGER DEFAULT 0," +
                    "durum TEXT)");

            // Öğrenci Detayları (TELEFON SÜTUNU BURADA)
            stmt.execute("CREATE TABLE IF NOT EXISTS student_details (" +
                    "user_id INTEGER PRIMARY KEY," +
                    "oda_id INTEGER," +
                    "adres TEXT," +
                    "telefon TEXT," +  // İşte aradığımız sütun bu!
                    "FOREIGN KEY(user_id) REFERENCES users(id)," +
                    "FOREIGN KEY(oda_id) REFERENCES rooms(id))");

            // İzinler
            stmt.execute("CREATE TABLE IF NOT EXISTS permissions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ogrenci_id INTEGER," +
                    "baslangic TEXT," +
                    "bitis TEXT," +
                    "sebep TEXT," +
                    "durum TEXT DEFAULT 'BEKLEMEDE'," +
                    "FOREIGN KEY(ogrenci_id) REFERENCES users(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}