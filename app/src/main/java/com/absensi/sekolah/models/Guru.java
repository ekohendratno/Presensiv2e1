package com.absensi.sekolah.models;

public class Guru {

    public String guru_id;
    public String guru_nama;
    public String guru_jenis_kelamin;
    public String guru_alamat;
    public String guru_no_telp;
    public String guru_mapel;

    public Guru(
            String guru_id,
            String guru_nama,
            String guru_jenis_kelamin,
            String guru_alamat,
            String guru_no_telp,
            String guru_mapel){

        this.guru_id = guru_id;
        this.guru_nama = guru_nama;
        this.guru_jenis_kelamin = guru_jenis_kelamin;

        this.guru_alamat = guru_alamat;
        this.guru_no_telp = guru_no_telp;
        this.guru_mapel = guru_mapel;

    }
}
