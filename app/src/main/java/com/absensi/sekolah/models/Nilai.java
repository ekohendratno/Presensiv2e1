package com.absensi.sekolah.models;

public class Nilai {
    private boolean isheader;

    public String nilai_id;
    public String siswa_nik;
    public String nilai_tanggal;
    public String nilai_angka;
    public String nilai_keterangan;
    public String nilai_kelas;
    public String guru_nama;
    public String siswa_nama;

    public boolean isHeader() {
        return isheader;
    }

    public Nilai(String nilai_tanggal){
        this.nilai_tanggal = nilai_tanggal;
        this.isheader = true;
    }

    public Nilai(
            String nilai_tanggal,
            String nilai_keterangan,
            String nilai_kelas){

        this.nilai_tanggal = nilai_tanggal;
        this.nilai_keterangan = nilai_keterangan;
        this.nilai_kelas = nilai_kelas;
        this.isheader = false;
    }

    public Nilai(
            String nilai_id,
            String siswa_nik,
            String nilai_tanggal,
            String nilai_angka,
            String nilai_keterangan,
            String guru_nama,
            String siswa_nama){

        this.nilai_id = nilai_id;
        this.siswa_nik = siswa_nik;

        this.nilai_tanggal = nilai_tanggal;
        this.nilai_angka = nilai_angka;
        this.nilai_keterangan = nilai_keterangan;
        this.guru_nama = guru_nama;
        this.siswa_nama = siswa_nama;

        this.isheader = false;
    }
}
