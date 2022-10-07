package com.absensi.sekolah.models;

public class Presensi {
    private boolean isheader;

    public String absensi_id;
    public String siswa_nik;
    public String absensi_tanggal;
    public String absensi_keterangan;
    public String absensi_kelas;
    public String absensi_jumlah;
    public String absensi_lat;
    public String absensi_long;
    public String absensi_jam;
    public String absensi_foto;
    public String siswa_nama;

    public boolean isHeader() {
        return isheader;
    }

    public Presensi(String absensi_tanggal){
        this.absensi_tanggal = absensi_tanggal;
        this.isheader = true;
    }

    public Presensi(
            String absensi_tanggal,
            String absensi_keterangan,
            String absensi_kelas,
            String absensi_jumlah){
        this.absensi_tanggal = absensi_tanggal;
        this.absensi_keterangan = absensi_keterangan;
        this.absensi_kelas = absensi_kelas;
        this.absensi_jumlah = absensi_jumlah;

        this.isheader = false;
    }

    public Presensi(
            String absensi_id,
            String siswa_nik,
            String absensi_tanggal,
            String absensi_keterangan,
            String absensi_lat,
            String absensi_long,
            String absensi_jam,
            String absensi_foto,
            String siswa_nama){

        this.absensi_id = absensi_id;
        this.siswa_nik = siswa_nik;

        this.absensi_tanggal = absensi_tanggal;
        this.absensi_keterangan = absensi_keterangan;
        this.absensi_lat = absensi_lat;
        this.absensi_long = absensi_long;
        this.absensi_jam = absensi_jam;
        this.absensi_foto = absensi_foto;
        this.siswa_nama = siswa_nama;

        this.isheader = false;
    }
}
