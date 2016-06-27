# IF409X-TA
## Pengembangan Sistem Essay Grader pada Ujian Online

### Latar belakang
Metode yang digunakan dalam memeriksa soal ujian essay pada Moodle masih manually graded. Hal ini sangat tidak efisien apabila jumlah peserta ujian banyak, karena penguji harus memeriksa banyak jawaban dan beberapa diantaranya pasti terdapat iterasi atau jawaban yang sama.

### Tujuan
Untuk menghasilkan sebuah plugin Moodle dengan tipe ujian essay, tetapi ketika pemeriksaan akan memberikan usulan nilai untuk jawaban tersebut kepada penguji. Pertimbangan atas hal ini adalah untuk mencegah kesalahan sistem otomasi yang dibuat.

### Kakas-kakas dan Algoritma-algoritma yang digunakan
1. _Library_ INANLP buatan Ibu Ayu Purwarianti, S.T., M.T., dosen prodi Teknik Informatika Institut Teknologi Bandung,
2. Open Multilingual WordNet bahasa Indonesia, dan
3. Algoritma Pattern Matching Jaro-Winkler.

### Ide Umum
Ide umum dari modul ini adalah sebagai berikut.
1. Memberikan POS _tag_ kepada setiap kata pada suatu kalimat,
2. Menghilangkan kata-kata hubung,
3. Menghilangkan pasangan kata yang sama dan memiliki POS _tag_ yang sama,
4. Apabila masih tersisa kata-kata dari kedua kalimat, cari sinonim-sinonim dari salah satu pasangan kata dan cocokkan kembali dengan pasangannya,
5. Apabila masih tersisa kata-kata dari kedua kalimat, gunakan algoritma Jaro-Winkler, dan
6. Hitung kesamaan secara keseluruhan dari kedua kalimat tersebut.