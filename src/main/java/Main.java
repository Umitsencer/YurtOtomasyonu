import com.yurt.database.DatabaseConnection;
import com.yurt.view.LoginView;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Uygulama Başlatılıyor...
        System.out.println("Yurt Otomasyon Sistemi Başlatılıyor...");

        // 1. Veritabanı bağlantısını başlat (Singleton Deseni)
        DatabaseConnection db = DatabaseConnection.getInstance();

        // 2. İlk Kurulum: Eğer hiç kullanıcı yoksa, sadece YÖNETİCİ oluştur.
        initAdminUser(db.getConnection());

        // 3. Arayüzü Başlat
        SwingUtilities.invokeLater(() -> {
            new LoginView();
        });
    }

    // Sadece Yönetici (Personel) oluşturan metot
    private static void initAdminUser(Connection conn) {
        try {
            // Kullanıcı tablosu boş mu kontrol et
            ResultSet rs = conn.createStatement().executeQuery("SELECT count(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Sistem ilk kez çalıştırıldı. Varsayılan YÖNETİCİ oluşturuluyor...");

                String sql = "INSERT INTO users (tc_no, ad, soyad, email, sifre, rol) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                // Sadece Yönetici Ekliyoruz (TC: 456, Şifre: 456)
                ps.setString(1, "456");
                ps.setString(2, "Ayse");
                ps.setString(3, "Yonetici");
                ps.setString(4, "admin@yurt.com");
                ps.setString(5, "456");
                ps.setString(6, "PERSONEL");

                ps.executeUpdate();

                System.out.println("Kurulum Tamamlandı.");
                System.out.println("Lütfen sisteme şu bilgilerle giriş yapıp öğrenci ekleyiniz:");
                System.out.println("-> Personel Giriş: 456 / 456");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}