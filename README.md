# KasGud

Aplikasi Desktop **Kasir (POS) + Gudang (Inventory)** dalam satu program.
Dibuat pakai Java 17 & Java Swing, tanpa library eksternal sama sekali.

## Fitur

**Tab KASIR**
- Cari barang (kode persis / sebagian nama), hasilnya muncul real-time saat mengetik
- Tambah barang ke keranjang, qty barang yang sama otomatis digabung
- Hapus item, reset keranjang
- Bayar + input uang, hitung kembalian otomatis, cetak struk
- Stok otomatis berkurang begitu transaksi berhasil

**Tab GUDANG**
- Tambah, update, hapus barang (hapus pakai konfirmasi)
- Tambah stok barang
- Validasi: kode unik & alfanumerik, harga > 0, stok >= 0

**Tab LAPORAN**
- Rekap total pendapatan & jumlah transaksi
- Daftar transaksi (tanggal, ID, total)
- Barang terlaris top 5

Struk:

```
===== STRUK PEMBAYARAN =====
ID Transaksi : TRX0001
Tanggal      : 2026-09-29 16:30
----------------------------
Indomie Goreng       x2   = Rp7.000
Aqua 600ml           x1   = Rp3.000
----------------------------
TOTAL         : Rp10.000
BAYAR         : Rp20.000
KEMBALIAN     : Rp10.000
============================
```

## Cara compile

```bash
javac -d bin -sourcepath src src/Main.java
```

Atur bisa pakai script, sekaligus compile lalu jalan:

```bash
build.bat      # Windows
./build.sh     # Linux / Mac
```

## Cara run

```bash
java -cp bin Main
```

## Cara bikin JAR

Buat file `manifest.txt` sudah tersedia di folder ini isinya `Main-Class: Main`, lalu:

```bash
jar cfm KasGud.jar manifest.txt -C bin .
```

## Cara run JAR

```bash
java -jar KasGud.jar
```

## Struktur folder

```
kasgud/
├── src/
│   ├── Main.java
│   ├── model/
│   │   ├── Barang.java
│   │   └── Transaksi.java
│   ├── service/
│   │   ├── GudangService.java
│   │   └── KasirService.java
│   ├── gui/
│   │   ├── MainFrame.java
│   │   ├── PanelKasir.java
│   │   ├── PanelGudang.java
│   │   └── PanelLaporan.java
│   └── util/
│       └── Fmt.java
├── build.bat
├── build.sh
├── manifest.txt
├── .gitignore
└── README.md
```

`Main.java` sengaja tidak memakai package, sedangkan class lain memakai package
sesuai foldernya (`model`, `service`, `gui`, `util`).

## Cara kerja kodenya

```
GUI (MainFrame + Panel*)  ->  Service (GudangService, KasirService)  ->  Model (Barang, Transaksi)
```

- **Model** cuma POJO: field + getter/setter, tanpa logic.
- **Service** tempat semua business logic: validasi, hitung total, potong stok,
  nomor transaksi, laporan. Data disimpan di memory (`HashMap` & `ArrayList`),
  jadi hilang kalau aplikasi ditutup.
- **GUI** cuma memanggil service lalu menampilkan hasilnya. Semua error dari
  service dilempar sebagai exception, ditangkap GUI, dan ditampilkan lewat
  `JOptionPane`.
- `GudangService` dan `KasirService` dibuat sekali di `MainFrame`, lalu dipakai
  bersama oleh ketiga tab. Jadi barang yang ditambah di tab GUDANG langsung
  kelihatan di tab KASIR.

## Data awal

| Kode  | Nama            | Harga   | Stok |
|-------|-----------------|---------|------|
| B001  | Indomie Goreng  | 3.500   | 100  |
| B002  | Aqua 600ml      | 3.000   | 50   |
| B003  | Kopi Kapal Api  | 1.500   | 200  |

Nomor transaksi otomatis: `TRX0001`, `TRX0002`, dan seterusnya.
