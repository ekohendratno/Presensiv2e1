package com.absensi.sekolah.models;

public class Notif {

    public String notif_id;
    public String notif_title;
    public String notif_body;
    public String notif_topik;
    public String notif_tanggal;

    public Notif(
            String notif_id,
            String notif_title,
            String notif_body,
            String notif_topik,
            String notif_tanggal){

        this.notif_id = notif_id;
        this.notif_title = notif_title;
        this.notif_body = notif_body;

        this.notif_topik = notif_topik;
        this.notif_tanggal = notif_tanggal;

    }
}
