package com.yurt.patterns.builder;

import com.yurt.model.Student;
import com.yurt.model.User;

public class StudentBuilder {
    private String ad;
    private String soyad;
    private String tcNo;
    private String sifre;
    private String email;
    // YENİ EKLENEN ALANLAR
    private String telefon;
    private String adres;

    public StudentBuilder setAd(String ad) {
        this.ad = ad;
        return this;
    }

    public StudentBuilder setSoyad(String soyad) {
        this.soyad = soyad;
        return this;
    }

    public StudentBuilder setTcNo(String tcNo) {
        this.tcNo = tcNo;
        return this;
    }

    public StudentBuilder setSifre(String sifre) {
        this.sifre = sifre;
        return this;
    }

    public StudentBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    // YENİ EKLENEN METOT (Hatanın Sebebi buydu)
    public StudentBuilder setTelefon(String telefon) {
        this.telefon = telefon;
        return this;
    }

    // YENİ EKLENEN METOT
    public StudentBuilder setAdres(String adres) {
        this.adres = adres;
        return this;
    }

    public User build() {
        Student s = new Student();
        s.setAd(ad);
        s.setSoyad(soyad);
        s.setTcNo(tcNo);
        s.setSifre(sifre);
        s.setEmail(email);

        // Yeni alanları nesneye aktar
        s.setTelefon(telefon);
        s.setAdres(adres);

        s.setRol("OGRENCI");
        return s;
    }
}