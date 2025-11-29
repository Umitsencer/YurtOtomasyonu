package com.yurt.view;

import com.yurt.database.DatabaseConnection;
import com.yurt.model.User;
import com.yurt.patterns.builder.StudentBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PersonnelView extends BasePage {

    private User currentUser;

    // Tablolar
    private JTable tblRequests, tblRooms, tblStudents;
    private DefaultTableModel modelRequests, modelRooms, modelStudents;

    // Form Elemanları
    private JTextField txtOdaBaslangic, txtOdaBitis, txtKapasite;
    private JTextField txtOgrenciTc, txtAtanacakOda;

    // Öğrenci Ekleme Formu
    private JTextField txtYeniAd, txtYeniSoyad, txtYeniTc, txtYeniSifre, txtYeniEmail, txtYeniTel;

    // Arama ve Raporlama
    private JTextField txtOgrenciAra;
    private JTextField txtRaporBaslangic;

    public PersonnelView(User user) {
        super("Yurt Yönetim Paneli - " + user.getAdSoyad(), 1100, 700);
        this.currentUser = user;
        initializeComponents();

        loadPermissionRequests("BEKLEMEDE");
        loadRooms();
        loadStudents("");

        setVisible(true);
    }

    @Override
    public void initializeComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(10, 10, 1060, 630);

        // --- SEKME 1: İZİN YÖNETİMİ ---
        JPanel pnlPermissions = new JPanel(null);
        JLabel lblBaslik1 = new JLabel("İZİN TALEPLERİ VE RAPORLAMA");
        lblBaslik1.setFont(new Font("Arial", Font.BOLD, 14));
        lblBaslik1.setBounds(20, 20, 300, 20);
        pnlPermissions.add(lblBaslik1);

        JButton btnBekleyenler = new JButton("Onay Bekleyenleri Göster");
        btnBekleyenler.setBounds(20, 50, 200, 30);
        btnBekleyenler.setBackground(Color.ORANGE);
        pnlPermissions.add(btnBekleyenler);

        JButton btnOnaylilar = new JButton("Onaylanmış İzinleri Listele");
        btnOnaylilar.setBounds(230, 50, 200, 30);
        btnOnaylilar.setBackground(new Color(100, 149, 237));
        btnOnaylilar.setForeground(Color.WHITE);
        pnlPermissions.add(btnOnaylilar);

        JLabel lblTarih = new JLabel("Tarih Ara (örn: 2025):");
        lblTarih.setBounds(450, 50, 150, 30);
        pnlPermissions.add(lblTarih);

        txtRaporBaslangic = new JTextField();
        txtRaporBaslangic.setBounds(580, 50, 100, 30);
        pnlPermissions.add(txtRaporBaslangic);

        JButton btnAraTarih = new JButton("Ara");
        btnAraTarih.setBounds(690, 50, 80, 30);
        pnlPermissions.add(btnAraTarih);

        String[] colPerms = {"ID", "Öğrenci Adı", "TC No", "Başlangıç", "Bitiş", "Sebep", "Durum"};
        modelRequests = new DefaultTableModel(colPerms, 0);
        tblRequests = new JTable(modelRequests);
        JScrollPane scrollPerms = new JScrollPane(tblRequests);
        scrollPerms.setBounds(20, 90, 1000, 350);
        pnlPermissions.add(scrollPerms);

        JButton btnOnayla = new JButton("SEÇİLENİ ONAYLA");
        btnOnayla.setBounds(350, 460, 150, 40);
        btnOnayla.setBackground(new Color(60, 179, 113)); btnOnayla.setForeground(Color.WHITE);
        pnlPermissions.add(btnOnayla);

        JButton btnReddet = new JButton("SEÇİLENİ REDDET");
        btnReddet.setBounds(550, 460, 150, 40);
        btnReddet.setBackground(new Color(220, 20, 60)); btnReddet.setForeground(Color.WHITE);
        pnlPermissions.add(btnReddet);

        btnBekleyenler.addActionListener(e -> loadPermissionRequests("BEKLEMEDE"));
        btnOnaylilar.addActionListener(e -> loadPermissionRequests("ONAYLANDI"));
        btnAraTarih.addActionListener(e -> searchPermissionsByDate(txtRaporBaslangic.getText()));
        btnOnayla.addActionListener(e -> updatePermissionStatus("ONAYLANDI"));
        btnReddet.addActionListener(e -> updatePermissionStatus("REDDEDILDI"));

        tabbedPane.addTab("İzin Yönetimi", pnlPermissions);

        // --- SEKME 2: ODA VE KAYIT ---
        JPanel pnlRooms = new JPanel(null);
        int rightX = 450;

        JLabel lblBaslik2 = new JLabel("MEVCUT ODALAR");
        lblBaslik2.setFont(new Font("Arial", Font.BOLD, 14));
        lblBaslik2.setBounds(20, 20, 200, 20);
        pnlRooms.add(lblBaslik2);

        String[] colRooms = {"ID", "Oda No", "Kapasite", "Mevcut", "Durum"};
        modelRooms = new DefaultTableModel(colRooms, 0);
        tblRooms = new JTable(modelRooms);
        JScrollPane scrollRooms = new JScrollPane(tblRooms);
        scrollRooms.setBounds(20, 50, 400, 500);
        pnlRooms.add(scrollRooms);

        JLabel lblOdaEkle = new JLabel("--- TOPLU ODA OLUŞTUR ---");
        lblOdaEkle.setBounds(rightX, 20, 200, 20); lblOdaEkle.setForeground(Color.BLUE);
        pnlRooms.add(lblOdaEkle);

        pnlRooms.add(new JLabel("Başlangıç No:")).setBounds(rightX, 50, 90, 20);
        txtOdaBaslangic = new JTextField(); txtOdaBaslangic.setBounds(rightX + 90, 50, 60, 25); pnlRooms.add(txtOdaBaslangic);

        pnlRooms.add(new JLabel("Bitiş No:")).setBounds(rightX + 160, 50, 60, 20);
        txtOdaBitis = new JTextField(); txtOdaBitis.setBounds(rightX + 220, 50, 60, 25); pnlRooms.add(txtOdaBitis);

        pnlRooms.add(new JLabel("Kapasite:")).setBounds(rightX + 300, 50, 60, 20);
        txtKapasite = new JTextField(); txtKapasite.setBounds(rightX + 360, 50, 50, 25); pnlRooms.add(txtKapasite);

        JButton btnOdaEkle = new JButton("Odaları Ekle");
        btnOdaEkle.setBounds(rightX + 430, 50, 110, 25);
        pnlRooms.add(btnOdaEkle);

        JLabel lblOgrEkle = new JLabel("--- YENİ ÖĞRENCİ KAYDET ---");
        lblOgrEkle.setBounds(rightX, 100, 250, 20); lblOgrEkle.setForeground(Color.BLUE);
        pnlRooms.add(lblOgrEkle);

        pnlRooms.add(new JLabel("Ad:")).setBounds(rightX, 130, 60, 20);
        txtYeniAd = new JTextField(); txtYeniAd.setBounds(rightX + 50, 130, 120, 25); pnlRooms.add(txtYeniAd);

        pnlRooms.add(new JLabel("Soyad:")).setBounds(rightX + 190, 130, 60, 20);
        txtYeniSoyad = new JTextField(); txtYeniSoyad.setBounds(rightX + 250, 130, 120, 25); pnlRooms.add(txtYeniSoyad);

        pnlRooms.add(new JLabel("TC No:")).setBounds(rightX, 170, 60, 20);
        txtYeniTc = new JTextField(); txtYeniTc.setBounds(rightX + 50, 170, 120, 25); pnlRooms.add(txtYeniTc);

        txtYeniTc.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String text = txtYeniTc.getText();
                if (!text.matches("\\d*")) txtYeniTc.setText(text.replaceAll("[^\\d]", ""));
                if (txtYeniTc.getText().length() != 11) txtYeniTc.setBackground(new Color(255, 200, 200));
                else txtYeniTc.setBackground(Color.WHITE);
            }
            @Override public void keyTyped(KeyEvent e) { if (txtYeniTc.getText().length() >= 11) e.consume(); }
        });

        pnlRooms.add(new JLabel("Şifre:")).setBounds(rightX + 190, 170, 60, 20);
        txtYeniSifre = new JTextField(); txtYeniSifre.setBounds(rightX + 250, 170, 120, 25); pnlRooms.add(txtYeniSifre);

        pnlRooms.add(new JLabel("Email:")).setBounds(rightX, 210, 60, 20);
        txtYeniEmail = new JTextField(); txtYeniEmail.setBounds(rightX + 50, 210, 120, 25); pnlRooms.add(txtYeniEmail);

        pnlRooms.add(new JLabel("Telefon:")).setBounds(rightX + 190, 210, 60, 20);
        txtYeniTel = new JTextField(); txtYeniTel.setBounds(rightX + 250, 210, 120, 25); pnlRooms.add(txtYeniTel);

        JButton btnOgrKaydet = new JButton("Öğrenciyi Kaydet");
        btnOgrKaydet.setBounds(rightX + 50, 250, 320, 30);
        btnOgrKaydet.setBackground(new Color(255, 140, 0)); btnOgrKaydet.setForeground(Color.WHITE);
        pnlRooms.add(btnOgrKaydet);

        JLabel lblAtama = new JLabel("--- ODAYA ÖĞRENCİ YERLEŞTİR ---");
        lblAtama.setBounds(rightX, 320, 250, 20); lblAtama.setForeground(Color.BLUE);
        pnlRooms.add(lblAtama);

        pnlRooms.add(new JLabel("Öğrenci TC:")).setBounds(rightX, 350, 80, 20);
        txtOgrenciTc = new JTextField(); txtOgrenciTc.setBounds(rightX + 80, 350, 150, 25); pnlRooms.add(txtOgrenciTc);

        pnlRooms.add(new JLabel("Oda No:")).setBounds(rightX, 390, 80, 20);
        txtAtanacakOda = new JTextField(); txtAtanacakOda.setBounds(rightX + 80, 390, 100, 25); pnlRooms.add(txtAtanacakOda);

        JButton btnAta = new JButton("Yerleştir");
        btnAta.setBounds(rightX + 80, 430, 150, 30);
        pnlRooms.add(btnAta);

        btnOdaEkle.addActionListener(e -> addBulkRooms());
        btnOgrKaydet.addActionListener(e -> addNewStudent());
        btnAta.addActionListener(e -> assignStudentToRoom());

        tabbedPane.addTab("Oda ve Kayıt İşlemleri", pnlRooms);

        // --- SEKME 3: ÖĞRENCİ LİSTESİ ---
        JPanel pnlStudentList = new JPanel(null);
        JLabel lblListeBaslik = new JLabel("SİSTEMDE KAYITLI ÖĞRENCİLER");
        lblListeBaslik.setFont(new Font("Arial", Font.BOLD, 14));
        lblListeBaslik.setBounds(20, 20, 300, 20);
        pnlStudentList.add(lblListeBaslik);

        pnlStudentList.add(new JLabel("İsimle Ara:")).setBounds(20, 50, 80, 25);
        txtOgrenciAra = new JTextField(); txtOgrenciAra.setBounds(100, 50, 200, 25); pnlStudentList.add(txtOgrenciAra);

        JButton btnAra = new JButton("Ara"); btnAra.setBounds(310, 50, 80, 25); pnlStudentList.add(btnAra);
        JButton btnYenile = new JButton("Listeyi Yenile"); btnYenile.setBounds(400, 50, 120, 25); pnlStudentList.add(btnYenile);

        String[] colStudents = {"ID", "TC No", "Ad", "Soyad", "Email", "Telefon", "Kaldığı Oda"};
        modelStudents = new DefaultTableModel(colStudents, 0);
        tblStudents = new JTable(modelStudents);
        JScrollPane scrollStudents = new JScrollPane(tblStudents);
        scrollStudents.setBounds(20, 90, 1000, 450);
        pnlStudentList.add(scrollStudents);

        btnAra.addActionListener(e -> loadStudents(txtOgrenciAra.getText()));
        btnYenile.addActionListener(e -> { txtOgrenciAra.setText(""); loadStudents(""); });
        tabbedPane.addTab("Öğrenci Listesi", pnlStudentList);

        add(tabbedPane);
    }

    // --- FONKSİYONLAR ---

    private void loadPermissionRequests(String durumFiltresi) {
        modelRequests.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT p.id, u.ad, u.soyad, u.tc_no, p.baslangic, p.bitis, p.sebep, p.durum FROM permissions p JOIN users u ON p.ogrenci_id = u.id WHERE p.durum = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, durumFiltresi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelRequests.addRow(new Object[]{rs.getInt("id"), rs.getString("ad") + " " + rs.getString("soyad"), rs.getString("tc_no"), rs.getString("baslangic"), rs.getString("bitis"), rs.getString("sebep"), rs.getString("durum")});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void searchPermissionsByDate(String dateText) {
        if (dateText.isEmpty()) { loadPermissionRequests("ONAYLANDI"); return; }
        modelRequests.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT p.id, u.ad, u.soyad, u.tc_no, p.baslangic, p.bitis, p.sebep, p.durum FROM permissions p JOIN users u ON p.ogrenci_id = u.id WHERE p.durum = 'ONAYLANDI' AND (p.baslangic LIKE ? OR p.bitis LIKE ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + dateText + "%"); ps.setString(2, "%" + dateText + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelRequests.addRow(new Object[]{rs.getInt("id"), rs.getString("ad") + " " + rs.getString("soyad"), rs.getString("tc_no"), rs.getString("baslangic"), rs.getString("bitis"), rs.getString("sebep"), rs.getString("durum")});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void updatePermissionStatus(String yeniDurum) {
        int selectedRow = tblRequests.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Lütfen listeden bir talep seçin!"); return; }
        String mevcutDurum = (String) modelRequests.getValueAt(selectedRow, 6);
        if (!mevcutDurum.equals("BEKLEMEDE") && !yeniDurum.equals("BEKLEMEDE")) { JOptionPane.showMessageDialog(this, "Bu işlem zaten sonuçlanmış."); return; }
        try {
            int id = (int) modelRequests.getValueAt(selectedRow, 0);
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE permissions SET durum = ? WHERE id = ?");
            ps.setString(1, yeniDurum); ps.setInt(2, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "İşlem Tamam.");
            loadPermissionRequests("BEKLEMEDE");
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void addBulkRooms() {
        try {
            int start = Integer.parseInt(txtOdaBaslangic.getText());
            int end = Integer.parseInt(txtOdaBitis.getText());
            int kap = Integer.parseInt(txtKapasite.getText());
            if (start > end) { JOptionPane.showMessageDialog(this, "Hata: Başlangıç > Bitiş"); return; }
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO rooms (oda_no, kapasite, mevcut_kisi, durum) VALUES (?, ?, 0, 'MUSAIT')");
            int count = 0;
            for (int i = start; i <= end; i++) {
                try { ps.setString(1, String.valueOf(i)); ps.setInt(2, kap); ps.executeUpdate(); count++; }
                catch (SQLException ignored) {}
            }
            JOptionPane.showMessageDialog(this, count + " oda eklendi.");
            loadRooms();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage()); }
    }

    private void addNewStudent() {
        String ad = txtYeniAd.getText(); String soyad = txtYeniSoyad.getText(); String tc = txtYeniTc.getText();
        String sifre = txtYeniSifre.getText(); String email = txtYeniEmail.getText(); String tel = txtYeniTel.getText();

        if(ad.isEmpty() || soyad.isEmpty() || tc.isEmpty()) { JOptionPane.showMessageDialog(this, "Ad, Soyad ve TC zorunludur!"); return; }
        if (tc.length() != 11) { JOptionPane.showMessageDialog(this, "TC 11 hane olmalı!"); return; }

        try {
            String finalSifre = sifre.isEmpty() ? tc : sifre;
            String finalEmail = email.isEmpty() ? tc + "@ogrenci.com" : email;
            StudentBuilder builder = new StudentBuilder();
            User yeniOgrenci = builder.setAd(ad).setSoyad(soyad).setTcNo(tc).setSifre(finalSifre).setEmail(finalEmail).setTelefon(tel).build();
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sqlUser = "INSERT INTO users (tc_no, ad, soyad, email, sifre, rol) VALUES (?, ?, ?, ?, ?, 'OGRENCI')";
            PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, yeniOgrenci.getTcNo()); psUser.setString(2, yeniOgrenci.getAd()); psUser.setString(3, yeniOgrenci.getSoyad());
            psUser.setString(4, yeniOgrenci.getEmail()); psUser.setString(5, yeniOgrenci.getSifre());
            psUser.executeUpdate();

            int userId = -1; ResultSet rsKeys = psUser.getGeneratedKeys();
            if (rsKeys.next()) userId = rsKeys.getInt(1);
            else { ResultSet rsId = conn.createStatement().executeQuery("SELECT last_insert_rowid()"); if(rsId.next()) userId = rsId.getInt(1); }

            if (userId != -1) {
                PreparedStatement psDetail = conn.prepareStatement("INSERT INTO student_details (user_id, adres, telefon) VALUES (?, ?, ?)");
                psDetail.setInt(1, userId); psDetail.setString(2, "Girilmedi"); psDetail.setString(3, tel.isEmpty() ? "Girilmedi" : tel);
                psDetail.executeUpdate();
                JOptionPane.showMessageDialog(this, "Kayıt Başarılı!\nŞifre: " + finalSifre);
                txtYeniAd.setText(""); txtYeniSoyad.setText(""); txtYeniTc.setText(""); txtYeniSifre.setText(""); txtYeniEmail.setText(""); txtYeniTel.setText("");
                loadStudents("");
            }
        } catch (SQLException e) {
            if(e.getMessage().contains("UNIQUE")) JOptionPane.showMessageDialog(this, "Bu TC zaten kayıtlı!");
            else JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
        }
    }

    private void loadRooms() {
        modelRooms.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM rooms");
            while(rs.next()) modelRooms.addRow(new Object[]{rs.getInt("id"), rs.getString("oda_no"), rs.getInt("kapasite"), rs.getInt("mevcut_kisi"), rs.getString("durum")});
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadStudents(String searchText) {
        modelStudents.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT u.id, u.tc_no, u.ad, u.soyad, u.email, sd.telefon, r.oda_no FROM users u LEFT JOIN student_details sd ON u.id = sd.user_id LEFT JOIN rooms r ON sd.oda_id = r.id WHERE u.rol = 'OGRENCI'";
            if (!searchText.isEmpty()) sql += " AND (lower(u.ad) LIKE ? OR lower(u.soyad) LIKE ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            if (!searchText.isEmpty()) { ps.setString(1, "%" + searchText.toLowerCase() + "%"); ps.setString(2, "%" + searchText.toLowerCase() + "%"); }
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String oda = rs.getString("oda_no");
                modelStudents.addRow(new Object[]{rs.getInt("id"), rs.getString("tc_no"), rs.getString("ad"), rs.getString("soyad"), rs.getString("email"), rs.getString("telefon"), (oda==null?"-":oda)});
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- REVIZE EDİLEN AKILLI ATAMA METODU ---
    private void assignStudentToRoom() {
        String tc = txtOgrenciTc.getText(); String odaNo = txtAtanacakOda.getText();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // 1. Hedef Oda
            PreparedStatement psOda = conn.prepareStatement("SELECT id, kapasite, mevcut_kisi FROM rooms WHERE oda_no = ?");
            psOda.setString(1, odaNo); ResultSet rsOda = psOda.executeQuery();
            if (!rsOda.next()) { JOptionPane.showMessageDialog(this, "Hedef oda yok!"); return; }
            int yeniOdaId = rsOda.getInt("id");
            int kapasite = rsOda.getInt("kapasite");
            int mevcut = rsOda.getInt("mevcut_kisi");

            // 2. Öğrenci
            PreparedStatement psOgr = conn.prepareStatement("SELECT id FROM users WHERE tc_no = ? AND rol = 'OGRENCI'");
            psOgr.setString(1, tc); ResultSet rsOgr = psOgr.executeQuery();
            if (!rsOgr.next()) { JOptionPane.showMessageDialog(this, "Öğrenci yok!"); return; }
            int userId = rsOgr.getInt("id");

            // 3. Durum Analizi
            int eskiOdaId = -1;
            boolean kayitVar = false;
            ResultSet rsCheck = conn.createStatement().executeQuery("SELECT oda_id FROM student_details WHERE user_id=" + userId);
            if (rsCheck.next()) {
                kayitVar = true;
                Object objOda = rsCheck.getObject("oda_id");
                if (objOda != null) eskiOdaId = (Integer) objOda;
            }

            if (eskiOdaId == yeniOdaId) { JOptionPane.showMessageDialog(this, "Zaten bu odada!"); return; }
            if (mevcut >= kapasite) { JOptionPane.showMessageDialog(this, "Oda Dolu!"); return; }

            // 4. İşlemler
            if (eskiOdaId != -1 && eskiOdaId != 0) {
                conn.createStatement().executeUpdate("UPDATE rooms SET mevcut_kisi = mevcut_kisi - 1, durum='MUSAIT' WHERE id=" + eskiOdaId);
            }

            if (kayitVar) conn.createStatement().executeUpdate("UPDATE student_details SET oda_id=" + yeniOdaId + " WHERE user_id=" + userId);
            else conn.createStatement().executeUpdate("INSERT INTO student_details (user_id, oda_id) VALUES (" + userId + ", " + yeniOdaId + ")");

            conn.createStatement().executeUpdate("UPDATE rooms SET mevcut_kisi = mevcut_kisi + 1 WHERE id=" + yeniOdaId);
            if (mevcut + 1 >= kapasite) conn.createStatement().executeUpdate("UPDATE rooms SET durum='DOLU' WHERE id=" + yeniOdaId);

            JOptionPane.showMessageDialog(this, "Atama Başarılı.");
            loadRooms(); loadStudents("");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}