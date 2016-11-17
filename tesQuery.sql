create table peserta(
    id_peserta varchar2(6) not null,
	nama varchar2(20) not null,
	jurusan varchar2(30) not null,
	total_sks  number not null,
	primary key(id_peserta)
 );
 
 create table Pelajaran(
   kode_p varchar2(8) not null,
   mata_pelajaran varchar2(30) not null,
   jurusan varchar2(20) not null,
   sks number not null,
   primary key(kode_p)
 );
 
 
 create table jadwal(
   kode_p varchar2(8) not null,
   sesi_jadwal char(1) not null,
   semester varchar2(15) not null,
   tahun char(4) not null,
   gedung varchar2(15) not null,
   ruang char(5) not null,
   waktu char(6) not null,
   foreign key(kode_p) references pelajaran(kode_p)
 );
 
 create table pengajar(
   id_pengajar char(6) not null,
   nama varchar2(20) not null,
   jurusan varchar2(30) not null,
   honor number,
   primary key(id_pengajar)  
  );
  
  
  create table kelas(
    id_pengajar char(6) not null,
    kode_p varchar2(8) not null,
	semester varchar2(15) not null,
	tahun char(4) not null,
	foreign key(id_pengajar) references pengajar(id_pengajar),
	foreign key(kode_p) references pelajaran (kode_p)
  );
  
  create table nilai(
    id_peserta varchar2(6) not null,
	kode_p varchar2(8) not null,
	semester varchar2(15) not null,
	tahun char(4) not null,
	huruf_mutu char(2) null,
	foreign key(id_peserta) references peserta(id_peserta),
	foreign key(kode_p) references pelajaran(kode_p)
  );