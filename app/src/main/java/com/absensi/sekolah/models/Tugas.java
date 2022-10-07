package com.absensi.sekolah.models;

public class Tugas {
    private boolean isheader;

    public String tugas_id;
    public String tugas_tanggal;
    public String tugas_terlambat;
    public String tugas_title;
    public String tugas_keterangan;
    public String tugas_point;
    public String tugas_kelas;

    public String guru_id;
    public String guru_nama;
    public String siswa_nik;
    public String siswa_nama;
    public String siswa_foto;

    public String tugas_dikerjakan_id;
    public String tugas_dikerjakan_text;
    public String tugas_dikerjakan_text_balas;
    public String tugas_dikerjakan_file;
    public String tugas_dikerjakan_status;
    public String tugas_dikerjakan_point;
    public String tugas_dikerjakan_tanggal;

    public boolean isHeader() {
        return isheader;
    }

    public Tugas(String tugas_tanggal){
        this.tugas_tanggal = tugas_tanggal;
        this.isheader = true;
    }

    public Tugas(
            String tugas_id,
            String tugas_tanggal,
            String tugas_title,
            String tugas_keterangan,
            String tugas_point,
            String tugas_kelas,
            String tugas_terlambat,
            String x){

        this.tugas_id = tugas_id;
        this.tugas_tanggal = tugas_tanggal;
        this.tugas_title = tugas_title;
        this.tugas_keterangan = tugas_keterangan;
        this.tugas_point = tugas_point;
        this.tugas_kelas = tugas_kelas;
        this.tugas_terlambat = tugas_terlambat;

        this.isheader = false;
    }

    public Tugas(
            String tugas_id,
            String tugas_dikerjakan_id,
            String tugas_dikerjakan_text,
            String tugas_dikerjakan_file,
            String tugas_dikerjakan_status,
            String tugas_dikerjakan_point,
            String tugas_dikerjakan_tanggal){

        this.tugas_id = tugas_id;

        this.tugas_dikerjakan_id = tugas_dikerjakan_id;
        this.tugas_dikerjakan_text = tugas_dikerjakan_text;
        this.tugas_dikerjakan_file = tugas_dikerjakan_file;
        this.tugas_dikerjakan_status = tugas_dikerjakan_status;
        this.tugas_dikerjakan_point = tugas_dikerjakan_point;
        this.tugas_dikerjakan_tanggal = tugas_dikerjakan_tanggal;

        this.isheader = false;
    }

    public Tugas(
            String tugas_id,
            String tugas_tanggal,
            String tugas_title,
            String tugas_keterangan,
            String tugas_point,
            String guru_nama,
            String tugas_dikerjakan_id,
            String tugas_dikerjakan_text,
            String tugas_dikerjakan_file,
            String tugas_dikerjakan_status,
            String tugas_dikerjakan_point,
            String tugas_dikerjakan_tanggal){

        this.tugas_id = tugas_id;

        this.tugas_tanggal = tugas_tanggal;
        this.tugas_title = tugas_title;
        this.tugas_keterangan = tugas_keterangan;
        this.tugas_point = tugas_point;
        this.guru_nama = guru_nama;

        this.tugas_dikerjakan_id = tugas_dikerjakan_id;
        this.tugas_dikerjakan_text = tugas_dikerjakan_text;
        this.tugas_dikerjakan_file = tugas_dikerjakan_file;
        this.tugas_dikerjakan_status = tugas_dikerjakan_status;
        this.tugas_dikerjakan_point = tugas_dikerjakan_point;
        this.tugas_dikerjakan_tanggal = tugas_dikerjakan_tanggal;

        this.isheader = false;
    }

    public Tugas(
            String tugas_id,
            String tugas_tanggal,
            String tugas_title,
            String tugas_keterangan,
            String tugas_point,
            String guru_id,
            String guru_nama,
            String siswa_nik,
            String siswa_nama,
            String siswa_foto,
            String tugas_dikerjakan_id,
            String tugas_dikerjakan_text,
            String tugas_dikerjakan_text_balas,
            String tugas_dikerjakan_file,
            String tugas_dikerjakan_status,
            String tugas_dikerjakan_point,
            String tugas_dikerjakan_tanggal){

        this.tugas_id = tugas_id;

        this.tugas_tanggal = tugas_tanggal;
        this.tugas_title = tugas_title;
        this.tugas_keterangan = tugas_keterangan;
        this.tugas_point = tugas_point;
        this.guru_id = guru_id;
        this.guru_nama = guru_nama;
        this.siswa_nik = siswa_nik;
        this.siswa_nama = siswa_nama;
        this.siswa_foto = siswa_foto;

        this.tugas_dikerjakan_id = tugas_dikerjakan_id;
        this.tugas_dikerjakan_text = tugas_dikerjakan_text;
        this.tugas_dikerjakan_text_balas = tugas_dikerjakan_text_balas;
        this.tugas_dikerjakan_file = tugas_dikerjakan_file;
        this.tugas_dikerjakan_status = tugas_dikerjakan_status;
        this.tugas_dikerjakan_point = tugas_dikerjakan_point;
        this.tugas_dikerjakan_tanggal = tugas_dikerjakan_tanggal;

        this.isheader = false;
    }
}
