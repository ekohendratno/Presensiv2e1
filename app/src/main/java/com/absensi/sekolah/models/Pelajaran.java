package com.absensi.sekolah.models;

public class Pelajaran {
    private boolean isheader;

    public String pelajaran_id;
    public String pelajaran_nama;
    public String pelajaran_tgl;
    public String pelajaran_hari;
    public String pelajaran_jam;
    public String pelajaran_kelas;
    public String guru_nama;

    public boolean isHeader() {
        return isheader;
    }

    public Pelajaran(String pelajaran_kelas){
        this.pelajaran_kelas = pelajaran_kelas;
        this.isheader = true;
    }

    public Pelajaran(
            String pelajaran_id,
            String pelajaran_nama,
            String pelajaran_tgl,
            String pelajaran_hari,
            String pelajaran_jam,
            String pelajaran_kelas,
            String guru_nama){

        this.pelajaran_id = pelajaran_id;
        this.pelajaran_nama = pelajaran_nama;
        this.pelajaran_tgl = pelajaran_tgl;

        this.pelajaran_hari = pelajaran_hari;
        this.pelajaran_jam = pelajaran_jam;
        this.pelajaran_kelas = pelajaran_kelas;
        this.guru_nama = guru_nama;

        this.isheader = false;
    }
}
