package com.absensi.sekolah.models;

public class Siswa {

    public String siswa_nik;
    public String siswa_nama;
    public String siswa_kelas;
    public String siswa_alamat;
    public String siswa_ttl;
    public String siswa_agama;
    public String siswa_jenis_kelamin;
    public String siswa_no_telp;
    public String siswa_imei;
    public String siswa_password;
    public String nik_ortu;

    public Siswa(
            String siswa_nik,
            String siswa_nama,
            String siswa_kelas,
            String siswa_alamat,
            String siswa_ttl,
            String siswa_agama,
            String siswa_jenis_kelamin,
            String siswa_no_telp,
            String siswa_imei,
            String siswa_password,
            String nik_ortu){

        this.siswa_nik = siswa_nik;
        this.siswa_nama = siswa_nama;
        this.siswa_kelas = siswa_kelas;
        this.siswa_alamat = siswa_alamat;
        this.siswa_ttl = siswa_ttl;
        this.siswa_agama = siswa_agama;
        this.siswa_jenis_kelamin = siswa_jenis_kelamin;
        this.siswa_no_telp = siswa_no_telp;
        this.siswa_imei = siswa_imei;
        this.siswa_password = siswa_password;
        this.nik_ortu = nik_ortu;

    }
}
