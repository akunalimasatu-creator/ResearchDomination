package com.example.cardgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    // --- MODEL GAME  ---
    private Deck deck;
    private DiscardPile discardPile;
    private Literature literature;
    private Player[] players;
    private int playerCount = 4;
    private int playerTurn = 0; // Pemain yang sedang mendapat giliran (0 - 3)
    private String currentAction = null;
    private int actionPoint = 2;
    private boolean[] bonusReceived;

    // --- VIEW / KOMPONEN GUI ---
    private PlayerAreaView[] playerViews;
    private CenterBoardView centerBoardView;
    private Label statusTurnLabel;
    private Label statusAPLabel;
    private Label selectedCardLabel;
    private ScrollPane scrollPane;

    // -- Buttons
    Button btnAction1 = new Button("1. Melanjutkan Riset (1 AP)");
    Button btnAction2 = new Button("2. Ide Baru (1 AP)");
    Button btnAction3 = new Button("3. Adjust Strategi (1 AP)");
    Button btnAction4 = new Button("4. Publikasi (2 AP)");
    Button btnAction5 = new Button("5. Hambat Persaingan (2 AP)");
    Button btnAction6 = new Button("6. Gunakan Literatur (2 AP)");

    private Stage primaryStage;
    BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Simpan referensi stage

        initGame();

        // instruksi di bawah ini tuk menampilkan scroll bar in case layar lebih kecil dari jendela yang dibutuhkan
        // biasanya terjadi karena resolusi layar tidak memadai
        // jangan tanya artinya apa, karena ini ngambil dari internet
        StackPane scrollContentHolder = new StackPane(root);
        scrollContentHolder.setStyle("-fx-background-color: #1e5631;");

        scrollPane = new ScrollPane(scrollContentHolder);
        scrollPane.setFitToWidth(false);  // Jangan dipaksa pas agar scroll horizontal bisa aktif jika layar kecil
        scrollPane.setFitToHeight(false); // Jangan dipaksa pas agar scroll vertikal aktif
        scrollPane.setStyle("-fx-background: #1e5631; -fx-background-color: #1e5631;");

        // Buat Scene dengan ukuran jendela standar monitor Anda (misal 1200 x 800)
        // Karena jendela lebih kecil dari prefSize root (1400 x 1000), scroll bar akan otomatis keluar!
        Scene scene = new Scene(scrollPane, 1200, 800);


        // buat interface dimana kalau pengguna menekan tombol [ESC] maka akan muncul dialog dengan 3 pilihan
        // a. melanjutkan permainan
        // b. reset permainan
        // c. keluar dari aplikasi
        scene.setOnKeyPressed(event -> {
            // Cek apakah tombol yang ditekan adalah ESCAPE
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                showEscapeDialog(); // Panggil method dialog konfirmasi
            }
        });

        // instruksi ini tuk menampilkan jendela permainan
        primaryStage.setTitle("RESEARCH DOMINATION");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Menampilkan dialog konfirmasi saat tombol Escape ditekan.
     */
    private void showEscapeDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Menu Jeda / Keluar");
        alert.setHeaderText("Permainan Dijeda");
        alert.setContentText("Apa yang ingin Anda lakukan?");

        // Tombol-tombol opsi pilihan
        ButtonType btnRestart = new ButtonType("Main Ulang Dari Awal", ButtonBar.ButtonData.OTHER);
        ButtonType btnExit = new ButtonType("Keluar Aplikasi", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Lanjutkan Permainan", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnRestart, btnExit, btnCancel);

        java.util.Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnRestart) {
                // Jika memilih mengulang permainan
                resetAndRestartGame(); // Memanggil method reset yang sudah dibuat sebelumnya
            } else if (result.get() == btnExit) {
                // Jika memilih keluar aplikasi sepenuhnya
                javafx.application.Platform.exit();
            }
            // Jika memilih "Lanjutkan Permainan" atau menutup dialog (Cancel), dialog otomatis tertutup dan game lanjut.
        }
    }

    // fungsi untuk inisialisasi permainan
    // 1. tanyakan dulu berapa orang yang mau main
    // 2. kocok kartu, kosongkan publikasi, kosongkan research, kosongkan discard pile, bagikan kartu ke masing-masing pemain dan bagikan 6 kartu literatur --> semua ada di method initGameModel
    // 3. bikin playerareaview sebanyak jumlah pemain
    // 4. bikin centerareaview tuk yang di tengah
    // 5. tambahkan view-view tersebut ke layout papan permainan, dimulai dari kiri, atas (pemain ke 2), kanan (pemain ke 3) dan bawah sebagai pemain ke 4
    private void initGame() {
        // dialog tuk menanyakan jumlah pemain
        if (!promptPlayerCount()) {
            System.exit(0); // Jika user membatalkan dialog, keluar aplikasi
            return;
        }

        // 1. Setup Model Game
        initGameModel();

        // 2. Root Layout Utama
        root = new BorderPane();
        root.setStyle("-fx-background-color: #1e5631; -fx-padding: 20px;");
    
        // Berikan ukuran preferensi yang cukup besar untuk menampung seluruh papan permainan
        // (Misalnya 1400px x 1000px agar area cukup luas dan otomatis memicu scroll)
        root.setPrefSize(1400, 1000);

        // 3. Area Pemain (Bottom: P1, Left: P2, Top: P3, Right: P4)
        // disini dikasih array dengan dimensi 4 karena maksimal khan 4 pemain
        playerViews = new PlayerAreaView[4];
        
        // bikin playerview yang pertama. letaknya di kiri (makanya setLeft)
        playerViews[0] = new PlayerAreaView("Pemain 1 (Anda)");
        root.setLeft(playerViews[0]); // Pemain 1 default di Kiri

        // kalau pemainnya 2 orang atau lebih, bikin playerview yang ke 2, letaknya di atas (makanya setTop)
        if (playerCount >= 2) {
            playerViews[1] = new PlayerAreaView("Pemain 2");
            root.setTop(playerViews[1]);
        }
        // kalau pemainnya 3 orang atau lebih, bikin playerview yang ke 3, letaknya di kanan (makanya setRight)
        if (playerCount >= 3) {
            playerViews[2] = new PlayerAreaView("Pemain 3");
            root.setRight(playerViews[2]);
        }
        // kalau pemainnya 4 orang, bikin playerview yang ke 4, letaknya di bawah (makanya setBottom)
        if (playerCount == 4) {
            playerViews[3] = new PlayerAreaView("Pemain 4");
            root.setBottom(playerViews[3]);
        }

        // 4. Area Tengah Meja (Deck & Literature)
        centerBoardView = new CenterBoardView();

        // 5. Panel Kontrol Aksi (Pengganti menu Scanner switch-case)
        // ini akan diisi 6 tombol yang menggambarkan aksi yang akan dipilih
        VBox controlPanel = createActionControlPanel();

        // Gabungkan Area Tengah dan Panel Kontrol
        VBox centerLayout = new VBox(10, centerBoardView, controlPanel);
        centerLayout.setAlignment(Pos.CENTER);
        root.setCenter(centerLayout);

        // 6. Tampilkan UI pertama kali
        refreshUI();
    }

    /**
     * Memunculkan Popup ChoiceDialog untuk meminta user memilih jumlah pemain (2-4)
     */
    private boolean promptPlayerCount() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(2, Arrays.asList(2, 3, 4));
        dialog.setTitle("Pengaturan Permainan");
        dialog.setHeaderText("Selamat Datang di RESEARCH DOMINATION!");
        dialog.setContentText("Pilih jumlah pemain yang akan bermain (2 - 4 orang):");

        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            playerCount = result.get();
            return true;
        }
        return false;
    }

    /**
     * Inisialisasi Model Game (Sesuai setup awal main lama Anda)
     */
    private void initGameModel() {
        // inisialisasi bonusReceived (bonus publikasi pertama) dengan false semua terhadap semua bidang
        bonusReceived = new boolean[Suit.values().length];

        actionPoint = 2;

        // bikin kartu dan kocok
        deck = new Deck();
        deck.shuffle();

        // assign array players sesuai dengan masukan
        players = new Player[playerCount];
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player(i);
        }

        // inisialisasi literature dan discard pile
        literature = new Literature();
        discardPile = new DiscardPile();

        // Bagikan 5 kartu ke masing-masing pemain
        for (int round = 0; round < 5; round++) {
            for (int p = 0; p < playerCount; p++) {
                players[p].addCard(deck.drawCard());
            }
        }

        // Tambahkan 6 kartu awal ke literature (tengah)
        for (int i = 0; i < 6; i++) {
            literature.addCard(deck.drawCard());
        }
    }

    /**
     * Membuat Panel Tombol Aksi (Pengganti Menu Console / Switch-Case)
     */
    private VBox createActionControlPanel() {
        VBox panel = new VBox(8);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 10px; -fx-background-radius: 8px;");

        statusTurnLabel = new Label("Giliran Pemain #1");
        statusTurnLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold; -fx-font-size: 14px;");

        statusAPLabel = new Label("Sisa Action Point: 2");
        statusAPLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold; -fx-font-size: 12px;");

        selectedCardLabel = new Label("Kartu Dipilih: (Klik kartu di tangan Anda)");
        selectedCardLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");

        // Baris Tombol Aksi 1-3
        HBox row1 = new HBox(8);
        row1.setAlignment(Pos.CENTER);
        row1.getChildren().addAll(btnAction1, btnAction2, btnAction3);

        // Baris Tombol Aksi 4-6
        HBox row2 = new HBox(8);
        row2.setAlignment(Pos.CENTER);
        row2.getChildren().addAll(btnAction4, btnAction5, btnAction6);

        // --- EVENT HANDLER Aksi 1: Melanjutkan Riset ---
        btnAction1.setOnAction(e -> {
            Player currentPlayer = players[playerTurn];

            // Periksa apakah pemain yang sedang bergiliran memiliki kartu di tangan
            if (currentPlayer.isHandEmpty()) {
                showAlert("Peringatan", "Aksi ini tidak bisa dilakukan (Kartu di tangan Anda habis)!");
                return;
            }

            // Set status aksi yang aktif dan set judul aksi yang dipilih
            currentAction = "MELANJUTKAN_RISET";
            selectedCardLabel.setText("Aksi dipilih: Melanjutkan Riset. Silakan pilih kartu di tangan!");
        });

        // --- EVENT HANDLER Aksi 2: Mendapatkan ide baru ---
        btnAction2.setOnAction(e -> {
            Player currentPlayer = players[playerTurn];

            // Cek apakah kartu di tangan pemain sudah penuh (syarat: kartu di tangan maksimal 6)
            if (currentPlayer.isHandFull()) {
                showAlert("Peringatan", "Aksi ini tidak bisa dilakukan (Kartu anda sudah 6)!");
                return;
            }

            // cek apakah deck masih ada kartunya
            if( deck.isEmpty()) {
                showAlert("Peringatan", "Aksi ini tidak bisa dilakukan (kartu di deck sudah habis)!");
                return;
            }

            // panggil method tuk menjalankan aksi Mendapatkan ide baru
            executeNewIdea();
        });

        // --- EVENT HANDLER Aksi 3: Penyesuaian strategi ---
        btnAction3.setOnAction(e -> {
            Player currentPlayer = players[playerTurn];

            // Cek apakah pemain memiliki kartu di tangan untuk dibuang
            if (currentPlayer.isHandEmpty()) {
                showAlert("Peringatan", "Aksi ini tidak bisa dilakukan (Tangan Anda kosong)!");
                return;
            }

            // cek apakah kartu di deck 2 kartu atau lebih?
            if (deck.getRemainingCards() < 2 ) {
                showAlert("Peringatan", "Aksi ini tidak bisa dilakukan (kartu di deck kurang dari 2)!");
                return;
            }

            // karena aksi ini multiple action yaitu:
            // a. pilih kartu di tangan tuk dibuang
            // b. dikasih 2 kartu dari deck
            // c. pilih salah satu dari 2 kartu tersebut. yang terpilih masuk ke tangan dan yang tidak terpilih dibuang
            // maka kita kasih penanda bahwa masih dalam state "Penyesuaian strategi"
            currentAction = "PENYESUAIAN_STRATEGI_PILIH_BUANG";
            selectedCardLabel.setText("Aksi: Penyesuaian strategi. Pilih 1 kartu di tangan untuk dibuang!");
        });

        // --- EVENT HANDLER Aksi 4: melakukan publikasi ---
        btnAction4.setOnAction(e -> {
            Player currentPlayer = players[playerTurn];

            // Syarat: Pilih satu tumpukan penelitian yang berisi minimal 2 kartu.
            // cek dulu apakah actionpoint (AP) = 2
            if( actionPoint < 2 ) {
                showAlert("Peringatan", "Aksi Publikasi tidak bisa dilakukan! AP anda tidak cukup");
                return;
            }

            // cek dulu apakah ada salah satu dari tumpukan penelitian pemain ada yang memenuhi syarat (minimal 2 kartu)
            // metode hasValidResearchStackForPublication ada di class Player
            if (!currentPlayer.hasValidResearchStackForPublication()) {
                showAlert("Peringatan", "Aksi Publikasi tidak bisa dilakukan! Anda tidak memiliki tumpukan penelitian dengan minimal 2 kartu.");
                return;
            }

            // Set state aksi
            currentAction = "PUBLIKASI";
            selectedCardLabel.setText("Aksi dipilih: Publikasi. Pilih tumpukan penelitian Anda!");
            
            // Tampilkan dialog pilihan tumpukan penelitian milik pemain
            showPublicationStackSelectionDialog(currentPlayer);
        });

        // --- EVENT HANDLER Aksi 5: menghambat persaingan ---
        btnAction5.setOnAction(e -> {
            Player currentPlayer = players[playerTurn];

            // Syarat: Minimal ada satu pemain lain yang memiliki kartu pada tumpukan penelitian.
            if (!hasAnyOpponentWithResearch(currentPlayer)) {
                showAlert("Peringatan", "Aksi tidak bisa dilakukan! Tidak ada pemain lawan yang memiliki kartu di tumpukan penelitian mereka.");
                return;
            }

            // Set state aksi
            currentAction = "HAMBAT_PERSAINGAN";
            selectedCardLabel.setText("Aksi dipilih: Hambat Persaingan. Pilih pemain lawan!");

            // Tampilkan dialog untuk memilih pemain lawan
            showSelectOpponentDialog(currentPlayer);
        });

        // --- EVENT HANDLER Aksi 6: Menggunakan literatur utama ---
        btnAction6.setOnAction(e -> {
            // Syarat: Terdapat minimal 1 kartu pada Literatur Utama.
            if (literature == null || literature.isEmpty()) {
                showAlert("Peringatan", "Aksi tidak bisa dilakukan! Literatur Utama kosong.");
                return;
            }

            // Set state aksi menjadi menunggu klik pada literatur
            currentAction = "GUNAKAN_LITERATUR";
            selectedCardLabel.setText("Aksi: Gunakan Literatur. Klik salah satu kartu di Literatur Utama!");
        });

        // update tulisan status ketersediaan AP, status giliran dan status aksi
        panel.getChildren().addAll(statusTurnLabel, statusAPLabel, selectedCardLabel, row1, row2);
        return panel;
    }

    // methode ini tuk aksi 1: melanjutkan riset
    private void executeMelanjutkanRiset(Card selectedCard) {
        Player currentPlayer = players[playerTurn];

        // hilangkan kartu yang sudah dipilih (diclick) oleh pemain dari tangan
        currentPlayer.removeCard(selectedCard);
        // letakkan di tumpukan research sesuai dengan bidang kartu yang dipilih tadi
        currentPlayer.getResearchBoard().addCard(selectedCard);

        // Membuka kartu dari deck dan menempatkannya di tumpukan literatur
        Card drawnCard = deck.drawCard();
        if (drawnCard != null) {
            literature.addCard(drawnCard);
        }

        // Jika tumpukan literatur melebihi batas (atau mencapai syarat maksimal),
        // hilangkan kartu pertama (tertua) dan masukkan ke discard pile
        Card oldestCard = literature.trimToMaxSize();
        if (oldestCard != null) {
            discardPile.addCard(oldestCard);
        }

        // Reset status aksi dan status pilihan kartu
        currentAction = null;
        selectedCardLabel.setText("Pilih tombol aksi terlebih dahulu...");

        // kurangi AP dengan 1 dan tampilkan
        actionPoint = actionPoint - 1;
        statusAPLabel.setText("Sisa Action Point: " + actionPoint);
        // jika AP = 0, giliran pemain lain. Jika tidak, masih gilirannya
        if( actionPoint == 0 ) {
            nextTurn();
        }
        refreshUI();
    }

    // method ini tuk aksi 2: mendapatkan ide baru
    private void executeNewIdea() {
        Player currentPlayer = players[playerTurn];

        // Ambil 1 kartu dari deck
        Card drawnCard = deck.drawCard();
        // letakkan di tangan
        currentPlayer.addCard(drawnCard);

        // Reset label status aksi
        resetActionState();

        // kurangi AP dengan 1
        actionPoint = actionPoint - 1;
        // update status AP
        statusAPLabel.setText("Sisa Action Point: " + actionPoint);
        // jika AP = 0, giliran pemain lain
        if( actionPoint == 0 ) {
            nextTurn();
        }
        refreshUI();
    }

    /**
     * methode ini tuk aksi 4: Melakukan publikasi
     * Menampilkan dialog bagi pemain untuk memilih tumpukan penelitian yang akan dipublikasikan.
     */
    private void showPublicationStackSelectionDialog(Player player) {
        // Ambil daftar tumpukan penelitian milik pemain yang berisi minimal 2 kartu
        ArrayList<Suit> validResearch = player.getValidResearch();

        // bikin dialog pilihan
        ChoiceDialog<Suit> dialog = new ChoiceDialog<>(validResearch.get(0), validResearch);
        dialog.setTitle("Publikasi Penelitian");
        dialog.setHeaderText("Pilih subbidang penelitian yang ingin dipublikasikan:");
        dialog.setContentText("Subbidang:");

        // Hilangkan tombol Cancel agar pemain wajib memilih
        dialog.getDialogPane().getButtonTypes().remove(javafx.scene.control.ButtonType.CANCEL);

        // tampilkan dialog dan tunggu respond dari pemain
        Optional<Suit> result = dialog.showAndWait();
        // kalau sudah ada respond dari pemain
        if (result.isPresent()) {
            // disini pemain sudah memilih bidang yang akan dipublikasikan
            Suit chosenSuit = result.get();
            // eksekusi publikasi
            executePublikasi(player, chosenSuit);
        } else {
            resetActionState();
        }
    }

    /**
     * methode ini tuk aksi 4: Melakukan publikasi
     * setelah bidang yang akan dipublikasikan sudah dipilih oleh pemain
     * Mengeksekusi langkah-langkah Publikasi selanjutnya
     */
    private void executePublikasi(Player player, Suit suit) {
        // ambil research board pemain
        // ambil publication board pemain
        ResearchBoard playerResearchBoard = player.getResearchBoard();
        PublicationBoard playerPublicationBoard = player.getPublicationBoard();

        // hilangkan tumpukan (pile) dari research bidang terpilih
        ArrayList<Card> toPublishPile = playerResearchBoard.removePile(suit);
        // tambahkan tumpukan tersebut ke tumpukan publikasi
        playerPublicationBoard.addPile(toPublishPile);

        // hitung jumlah kartu di tumpukan yang akan dipublikasikan tersebut
        int cardCount = toPublishPile.size();
        int basePoints = 0;
        String publicationType = "";

        // 5. Hitung poin publikasi berdasarkan jumlah kartu
        if (cardCount == 2) {
            basePoints = 3;
            publicationType = "Presentasi Poster";
        } else if (cardCount == 3) {
            basePoints = 5;
            publicationType = "Makalah Konferensi Singkat";
        } else if (cardCount == 4) {
            basePoints = 7;
            publicationType = "Makalah Konferensi Reguler";
        } else if (cardCount >= 5) {
            basePoints = 9;
            publicationType = "Artikel Jurnal";
        }

        // 6. Cek apakah ini publikasi pertama dalam subbidang tersebut di SELURUH PERMAINAN
        int bonusPoints = 0;
        boolean isFirstInGame = false;
        // cari dari tumpukan publikasi pemain terhadap bidang yang baru dipublikasikan
        // jika tidak ada berarti publikasi ini adalah publikasi pertama
        // berarti dapat bonus 1 karena mempublikasikan tuk pertama kalinya
        if( !bonusReceived[suit.ordinal()] ) {
            bonusReceived[suit.ordinal()] = true;
            isFirstInGame = true;
            bonusPoints = 1;
        }

        // tambahkan bonus point
        int totalEarnedPoints = basePoints + bonusPoints;
        
        // Tambahkan poin ke skor pemain
        player.addScore(totalEarnedPoints);

        // Tampilkan informasi keberhasilan publikasi
        String msg = "Publikasi Berhasil (" + publicationType + " - " + cardCount + " kartu)!\n" +
                    "Poin Dasar: +" + basePoints;
        if (isFirstInGame) {
            msg += "\nBonus Subbidang Pertama: +1 poin!";
        }
        msg += "\nTotal Poin Ditambahkan: +" + totalEarnedPoints;
        
        showAlert("Sukses Publikasi", msg);

        // Reset state aksi, lanjut giliran, dan refresh UI

        // karena aksi melakukan publikasi membutuhkan 2 AP maka selesai aksi ini pasti pindah ke pemain lain
        resetActionState();
        nextTurn();
        refreshUI();
    }

    /**
     * methode ini tuk aksi 5: Melakukan aksi menghambat persaingan
     * Memeriksa apakah ada pemain lain selain pemain aktif yang memiliki minimal 1 kartu di research board mereka.
     */
    private boolean hasAnyOpponentWithResearch(Player currentPlayer) {
        // cek pemain lainnya
        for (int i = 0; i < playerCount; i++) {
            if (players[i] != currentPlayer) {
                // Cek apakah pemain lawan ini punya tumpukan riset yang tidak kosong
                ResearchBoard researchboard = players[i].getResearchBoard();
                for (Suit suit : Suit.values()) {
                    if( !researchboard.isEmpty(suit) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * methode ini tuk aksi 5: Melakukan aksi menghambat persaingan
     * Memeriksa apakah seorang pemain terpilih memiliki minimal 1 kartu di research board mereka di bidang apa saja
     * perhatikan perbedaan antara method hasAnyOpponentWithResearch
     */
    private boolean opponentWithResearch(Player player) {
        ResearchBoard researchboard = player.getResearchBoard();
        for (Suit suit : Suit.values()) {
            if( !researchboard.isEmpty(suit) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * methode ini tuk aksi 5: Melakukan aksi menghambat persaingan
     * Langkah 1: Memilih pemain lawan.
     */
    private void showSelectOpponentDialog(Player currentPlayer) {
        // cari dulu pemain(-pemain) yang mempunyai minimal 1 kartu di research board-nya
        // kumpulkan (tambahkan) pada variabel opponents
        List<Player> opponents = new java.util.ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            // Masukkan hanya pemain lain yang memiliki kartu di research board
            if (players[i] != currentPlayer && opponentWithResearch(players[i])) {
                opponents.add(players[i]);
            }
        }

        // Buat ChoiceDialog untuk memilih lawan (kita buat kustom penamaan tampilannya jika perlu)
        // disini pemain diminta tuk menentukan pemain mana yang akan dihambat
        ChoiceDialog<Player> dialog = new ChoiceDialog<>(opponents.get(0), opponents);
        dialog.setTitle("Hambat Persaingan - Pilih Lawan");
        dialog.setHeaderText("Pilih pemain lawan yang ingin Anda hambat penelitiannya:");
        dialog.setContentText("Pemain Lawan:");

        // Hilangkan tombol Cancel agar pemain wajib memilih
        dialog.getDialogPane().getButtonTypes().remove(javafx.scene.control.ButtonType.CANCEL);

        // buka dialog dan tunggu respond pemain
        Optional<Player> result = dialog.showAndWait();
        // kalau sudah terpilih
        if (result.isPresent()) {
            // sekarang kita sudah dapat informasi pemain mana yang akan dihambat
            Player chosenOpponent = result.get();
            
            // Lanjut ke langkah berikutnya: Pilih tumpukan riset milik lawan tersebut
            showSelectOpponentStackDialog(chosenOpponent);
        } else {
            resetActionState();
        }
    }

    /**
     * methode ini tuk aksi 5: Melakukan aksi menghambat persaingan
     * Langkah 2: Memilih salah satu tumpukan penelitian milik lawan yang sudah dipilih.
     */
    private void showSelectOpponentStackDialog(Player opponent) {
        // cari bidang research apa saja yang dimiliki oleh pemain tersebut
        // method researchExists ada di class Player
        ArrayList<Suit> validResearch = opponent.researchExists();

        // init dialog
        ChoiceDialog<Suit> dialog = new ChoiceDialog<>(validResearch.get(0), validResearch);
        dialog.setTitle("Hambat Persaingan - Pilih Tumpukan Riset");
        dialog.setHeaderText("Pilih tumpukan penelitian :");
        dialog.setContentText("Tumpukan Riset:");

        dialog.getDialogPane().getButtonTypes().remove(javafx.scene.control.ButtonType.CANCEL);

        // buka dialog dan tunggu respond
        Optional<Suit> result = dialog.showAndWait();
        // kalau user sudah memilih
        if (result.isPresent()) {
            // chosenSuit adalah bidang research yang dipilih pemain
            Suit chosenSuit = result.get();
            // eksekusi hambat persaingan
            executeHambatPersaingan(opponent, chosenSuit);
        } else {
            resetActionState();
        }
    }

    /**
     * methode ini tuk aksi 5: Melakukan aksi menghambat persaingan
     * mengambil kartu teratas dari suatu bidang research dan memasukkannya ke discard pile.
     */
    private void executeHambatPersaingan(Player opponent, Suit suit) {
        // Ambil kartu teratas dari tumpukan bidang research
        Card topCard = opponent.getResearchBoard().removeTopCard(suit);

        // buang ke discard Pile
        discardPile.addCard(topCard);

        // infokan kalau aksi berhasil dilakukan
        showAlert("Sukses Hambat Persaingan", "Berhasil menghambat penelitian lawan!\nKartu teratas (" + topCard.getRank() + " " + topCard.getSuit() + ") dari tumpukan milik lawan telah dibuang ke Discard Pile.");

        // berhubung menghambat persaingan butuh 2 AP, jadi sekarang langsung giliran pemain lain
        resetActionState();
        nextTurn();
        refreshUI();
    }

    /**
     * Perpindahan giliran ke pemain berikutnya
     */
    private void nextTurn() {
        // cek dulu apakah deck sudah kosong dan player turn adalah pemain terakhir dalam putaran
        // jika ya, maka permainan berakhir
        if (deck != null && deck.isEmpty() && playerTurn == playerCount - 1) {
            // Pemain terakhir sudah mendapat giliran dan deck kosong -> Game Over!
            triggerGameOver();
            return;
        }

        // pemain berikutnya
        playerTurn = (playerTurn + 1) % playerCount;
        // reset action point
        actionPoint = 2;
        // update status
        statusTurnLabel.setText("Giliran Pemain #" + (playerTurn + 1));
        statusAPLabel.setText("Sisa Action Point: 2");
    }

    // metode mengakhir permainan
    void triggerGameOver() {
        //  permainan selesai dan mulai melakukan perhitungan final
        // disable-kan dulu button agar gak ada pemain yang bisa klik button
        disableAllActionButtons();

        //hitung nilai sisa dan bonus bonus
        List<PlayerBonus> allBonuses = new java.util.ArrayList<>();
        // hitung bonus visibility
        allBonuses.addAll(calculateVisibilityBonus());
        // hitung bonus cakupan luas (broad coverage)
        allBonuses.addAll(calculateBroadCoverageBonus());
        // hitung bonus fokus research
        allBonuses.addAll(calculateResearchFocusBonus());
        // hitung bonus ongoing research
        allBonuses.addAll(calculateOngoingResearchBonus());
        
        // tambahkan skor dan bonus
        for (PlayerBonus pb : allBonuses) {
            pb.getPlayer().addScore(pb.getPoints());
        }

        // 3. Susun rekapitulasi bonus per pemain untuk ditampilkan ke UI/Dialog
        StringBuilder recap = new StringBuilder("PERMAINAN BERAKHIR!\n\n--- REKAPITULASI BONUS ---");
        
        for (int i = 0; i < playerCount; i++) {
            Player p = players[i];
            recap.append("\n\n").append("Pemain ").append(i).append(":");
            
            int totalBonusPlayer = 0;
            boolean hasBonus = false;

            for (PlayerBonus pb : allBonuses) {
                if (pb.getPlayer() == p) {
                    recap.append("\n  - ").append(pb.getBonusType()).append(" (+").append(pb.getPoints()).append(" pts)");
                    totalBonusPlayer += pb.getPoints();
                    hasBonus = true;
                }
            }

            if (!hasBonus) {
                recap.append("\n  - Tidak mendapatkan bonus.");
            } else {
                recap.append("\n  > Total Poin Bonus: +").append(totalBonusPlayer);
            }
            recap.append("\n  > Skor Akhir Total: ").append(p.getScore());
        }

        // 4. Cari Pemenang berdasarkan skor akhir tertinggi
        Player winner = players[0];
        int winner_urut = 0;
        for (int i = 1; i < playerCount; i++) {
            if (players[i].getScore() > winner.getScore()) {
                winner = players[i];
                winner_urut = i;
            }
        }

        recap.append("\n\nPemenangnya adalah ** Pemain").append(winner_urut).append(" ** dengan total skor ").append(winner.getScore()).append(" poin!");

        // Tampilkan hasil akhir
        showAlert("Game Over & Rekapitulasi", recap.toString());
        refreshUI();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Research Domination");
        alert.setHeaderText("PERMAINAN SELESAI!");
        alert.setContentText(recap.toString() + "\n\nApakah Anda ingin bermain lagi?");

        javafx.scene.control.ButtonType btnYes = new javafx.scene.control.ButtonType("Ya, Main Lagi", javafx.scene.control.ButtonBar.ButtonData.YES);
        javafx.scene.control.ButtonType btnNo = new javafx.scene.control.ButtonType("Tidak, Keluar", javafx.scene.control.ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(btnYes, btnNo);

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnYes) {
            // Jika pemain memilih Ya, reset game dari awal
            resetAndRestartGame();
        } else {
            // Jika memilih Tidak, biarkan game berhenti (atau tutup aplikasi)
            javafx.application.Platform.exit();
        }
    }

    // metode untuk mereset dan restart permainan
    private void resetAndRestartGame() {
        initGame();

        StackPane newHolder = new StackPane(root);

        newHolder.setStyle( "-fx-background-color: #1e5631;" );

        scrollPane.setContent(newHolder);
    }

    // fungsi pembantu
    // bikin semua button disable sehingga pemain tidak bisa lagi meng-klik tombol-tombol
    private void disableAllActionButtons() {
        if (btnAction1 != null) btnAction1.setDisable(true);
        if (btnAction2 != null) btnAction2.setDisable(true);
        if (btnAction3 != null) btnAction3.setDisable(true);
        if (btnAction4 != null) btnAction4.setDisable(true);
        if (btnAction5 != null) btnAction5.setDisable(true);
        if (btnAction6 != null) btnAction6.setDisable(true);
    }

    // fungsi pembantu
    // bikinsemua button enable
    private void enableAllActionButtons() {
        if (btnAction1 != null) btnAction1.setDisable(false);
        if (btnAction2 != null) btnAction2.setDisable(false);
        if (btnAction3 != null) btnAction3.setDisable(false);
        if (btnAction4 != null) btnAction4.setDisable(false);
        if (btnAction5 != null) btnAction5.setDisable(false);
        if (btnAction6 != null) btnAction6.setDisable(false);
    }

    /**
     * Menghitung dan memberikan Bonus Visibilitas (Mayoritas) di akhir permainan.
     * - Peringkat 1: +5 poin (bisa juara bersama jika jumlah kartu sama)
     * - Peringkat 2: +2 poin (bisa juara bersama jika jumlah kartu sama)
     */
    private List<PlayerBonus> calculateVisibilityBonus() {
        List<PlayerBonus> bonuses = new java.util.ArrayList<>();

        class PlayerPubCount {
            Player player;
            int totalCards;
            PlayerPubCount(Player player, int totalCards) {
                this.player = player;
                this.totalCards = totalCards;
            }
        }

        List<PlayerPubCount> pubCounts = new java.util.ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            Player p = players[i];
            int total = p.getPublicationBoard() != null ? p.getPublicationBoard().getTotalCards() : 0;
            pubCounts.add(new PlayerPubCount(p, total));
        }

        // Urutkan dari jumlah kartu terbanyak ke terkecil
        pubCounts.sort((a, b) -> Integer.compare(b.totalCards, a.totalCards));

        List<Integer> distinctCounts = new java.util.ArrayList<>();
        for (PlayerPubCount item : pubCounts) {
            if (item.totalCards > 0 && !distinctCounts.contains(item.totalCards)) {
                distinctCounts.add(item.totalCards);
            }
        }

        // Peringkat 1 (+5 poin)
        if (!distinctCounts.isEmpty()) {
            int firstPlaceCount = distinctCounts.get(0);
            for (PlayerPubCount item : pubCounts) {
                if (item.totalCards == firstPlaceCount && item.totalCards > 0) {
                    bonuses.add(new PlayerBonus(item.player, "Bonus Visibilitas (Juara 1)", 5));
                }
            }
        }

        // Peringkat 2 (+2 poin)
        if (distinctCounts.size() > 1) {
            int secondPlaceCount = distinctCounts.get(1);
            for (PlayerPubCount item : pubCounts) {
                if (item.totalCards == secondPlaceCount && item.totalCards > 0) {
                    bonuses.add(new PlayerBonus(item.player, "Bonus Visibilitas (Juara 2)", 2));
                }
            }
        }

        return bonuses;
    }


    /**
     * menghitung bonus broad coverage
     * kalau tumpukan publikasi semua bidang tidak kosong, maka bonus = 3
    */
    private List<PlayerBonus> calculateBroadCoverageBonus() {
        List<PlayerBonus> bonuses = new java.util.ArrayList<>();
        
        for (int i = 0; i < playerCount; i++) {
            Player p = players[i];
            
            // Pastikan pemain memiliki publication board
            if (p.getPublicationBoard() != null) {
                // Hitung berapa banyak subbidang (Suit) yang sudah memiliki minimal 1 kartu publikasi
                if( p.getPublicationBoard().hasAllFields() ) {
                    bonuses.add(new PlayerBonus(p, "Bonus Cakupan Luas (Semua Bidang)", 3));
                }
            }
        }

        return bonuses;
    }

    /**
     * Menghitung Bonus Fokus Penelitian (Research Focus) di akhir permainan.
     * Jika pemain memiliki > 8 kartu publikasi pada suatu bidang (Suit) tertentu, beri bonus +4 poin.
     */
    private List<PlayerBonus> calculateResearchFocusBonus() {
        List<PlayerBonus> bonuses = new java.util.ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            Player p = players[i];
            
            if (p.getPublicationBoard() != null) {
                // Cek setiap subbidang (Suit) yang ada
                for (Suit suit : Suit.values()) {
                    // Ambil jumlah kartu publikasi pada bidang ini
                    int countInSuit = p.getPublicationBoard().getCount(suit); // Sesuaikan nama method di project Anda
                    
                    // Aturan: Jika lebih dari 8 kartu pada suatu bidang tertentu
                    if (countInSuit > 8) {
                        bonuses.add(new PlayerBonus(p, "Bonus Fokus Penelitian (" + suit.name() + " - " + countInSuit + " kartu)", 4));
                    }
                }
            }
        }

        return bonuses;
    }

    /**
     * Menghitung Bonus Penelitian yang Masih Berlangsung (Ongoing Research) di akhir permainan.
     * Setiap kartu yang masih ada di penelitian mendapatkan +1 poin bonus per kartu.
     */
    private List<PlayerBonus> calculateOngoingResearchBonus() {
        List<PlayerBonus> bonuses = new java.util.ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            Player p = players[i];
            
            if (p.getResearchBoard() != null) {
                // Hitung total seluruh kartu yang tersisa di seluruh tumpukan research board
                int totalResearchCards = p.getResearchBoard().getTotalCards(); // Sesuaikan nama method dengan project Anda
                
                if (totalResearchCards > 0) {
                    bonuses.add(new PlayerBonus(p, "Bonus Penelitian Berlangsung (" + totalResearchCards + " kartu)", totalResearchCards));
                }
            }
        }

        return bonuses;
    }

    /**
     * Memperbarui UI visual sesuai kondisi model terbaru
     */
    private void refreshUI() {
        // update tampilan tengah yaitu deck, discard pile dan score
        centerBoardView.updateDiscardPile(discardPile);
        centerBoardView.updateDeck(deck);
        centerBoardView.updateScores(players);

        centerBoardView.updateLiterature(literature, (clickedLiteratureCard) -> {
            // in case pengguna iseng-iseng nge-klik kartu literatur maka pastikan bahwa
            // memang dia sedang melakukan aksi gunakan literatur utama
            // kalau aksi lainnya yang dia pilih, munculkan alert
            if ("GUNAKAN_LITERATUR".equals(currentAction)) {
                Player currentPlayer = players[playerTurn];

                // 1. Ambil kartu dari Literatur Utama
                // hilangkan kartu yang dipilih dari tumpukan literatur dan simpan kartu tersebut ke cardTaken
                Card cardTaken = literature.removeCardByClick(clickedLiteratureCard);
                // tambahkan kartu terpilih tersebut ke research board
                ResearchBoard researchboard = currentPlayer.getResearchBoard();
                researchboard.addCard(cardTaken);
                discardPile.addCard(cardTaken);

                // infokan bahwa proses menggunakan literatur sukses dilaksanakan
                showAlert("Sukses", "Berhasil menggunakan literatur: " + clickedLiteratureCard.getRank() + " " + clickedLiteratureCard.getSuit());

                // karena menggunakan literatur membutuhkan 2 AP, jadi langsung pindah giliran ke pemain berikutnya
                resetActionState();
                nextTurn();
                refreshUI();
            } else {
                showAlert("Informasi", "Silakan pilih tombol aksi 'Gunakan Literatur' terlebih dahulu!");
            }
        });

        // in case pengguna mengklik kartu di tangan
        for (int i = 0; i < playerCount; i++) {
            if (playerViews[i] != null) {
                // Buat salinan nilai 'i' agar effectively final dan bisa diakses dalam lambda
                final int idx = i; 

                // Update kartu di tangan, pastikan memang dia tadi memilih aksi melanjutkan riset atau penyesuaian strategi
                // kalau tuk aksi lainnya, abaikan saja (atau boleh juga kasih alert)
                playerViews[idx].updateHand(players[idx].getHand(), (clickedCard) -> {
                    // Event callback jika kartu di tangan diklik
                    if (idx == playerTurn) {
                        // Cek apakah user sudah menekan tombol aksi sebelumnya?
                        // kalau belum memilih aksi, kasih alert
                        if (currentAction == null) {
                            showAlert("Informasi", "Silakan pilih tombol aksi terlebih dahulu!");
                            return;
                        }

                        // Cek aksi apa yang sedang aktif, lalu panggil fungsi spesifiknya!
                        if ("MELANJUTKAN_RISET".equals(currentAction)) {
                            // laksanakan aksi melanjutkan riset
                            // method ini sudah dibuat, lihat baris 390-an
                            executeMelanjutkanRiset(clickedCard); // <--- DI SINI INPUT selectedCard DIMASUKKAN!
                        }
                        else if ("PENYESUAIAN_STRATEGI_PILIH_BUANG".equals(currentAction)) {
                            // Pemain memilih kartu dari tangan untuk dibuang ke discardPile
                            Player currentPlayer = players[playerTurn];
                            currentPlayer.removeCard(clickedCard);
                            discardPile.addCard(clickedCard);

                            // Pemain mendapatkan 2 kartu baru dari deck
                            Card card1 = deck.drawCard();
                            Card card2 = deck.drawCard();

                            // Simpan kartu yang berhasil ditarik ke dalam list sementara
                            List<Card> tempDrawn = new java.util.ArrayList<>();
                            if (card1 != null) tempDrawn.add(card1);
                            if (card2 != null) tempDrawn.add(card2);

                            // Gunakan dialog pilihan agar pemain memilih 1 dari 2 kartu baru tersebut
                            showChooseDrawnCardDialog(currentPlayer, tempDrawn);
                        }
                    }
                });

                // Update papan riset dan publikasi
                playerViews[idx].updateResearchBoard(players[idx].getResearchBoard());
                playerViews[idx].updatePublicationBoard(players[idx].getPublicationBoard());
            }
        }
    }

    /**
     * Memunculkan dialog bagi pemain untuk memilih 1 dari 2 kartu baru hasil "Ide Baru".
     */
    private void showChooseDrawnCardDialog(Player player, List<Card> drawnCards) {
        // init dialog
        ChoiceDialog<Card> dialog = new ChoiceDialog<>(drawnCards.get(0), drawnCards);
        dialog.setTitle("Ide Baru - Pilih Kartu");
        dialog.setHeaderText("Anda menarik 2 kartu baru dari Deck.");
        dialog.setContentText("Pilih 1 kartu untuk disimpan ke tangan Anda (kartu lainnya dibuang):");

        // hilangkan tombol Cancel
        dialog.getDialogPane().getButtonTypes().remove(javafx.scene.control.ButtonType.CANCEL);

        // buka dialog dan tunggu masukan dari pengguna
        Optional<Card> result = dialog.showAndWait();
        if (result.isPresent()) {
            // pengguna sudah memilih kartu yang akan dikeep di tangan
            Card chosenCard = result.get();
            
            // Masukkan kartu pilihan ke tangan pemain
            player.addCard(chosenCard);

            // Cari kartu sisanya (yang tidak dipilih) lalu buang ke discardPile
            for (Card c : drawnCards) {
                if (c != chosenCard) {
                    discardPile.addCard(c);
                }
            }
        } else {
            // Jika user menutup dialog tanpa memilih, default ambil yang pertama, sisa dibuang
            player.addCard(drawnCards.get(0));
            for (int i = 1; i < drawnCards.size(); i++) {
                discardPile.addCard(drawnCards.get(i));
            }
        }

        // Reset state aksi dan akhiri giliran
        resetActionState();
        // karena ide baru hanya membutuhkan 1 AP, maka kurangi action point dengan 1
        actionPoint = actionPoint - 1;
        statusAPLabel.setText("Sisa Action Point: " + actionPoint);
        // kalau action point = 0, maka berarti giliran pemain lainnya
        if( actionPoint == 0 ) {
            nextTurn();
        }
        refreshUI();
    }

    /**
     * Helper untuk mereset status aksi aktif
     */
    private void resetActionState() {
        this.currentAction = null;
        selectedCardLabel.setText("Kartu Dipilih: (Klik kartu di tangan Anda)");
    }

    /**
     * tuk menampilkan alert saja dengan parameter input judul (title) dan isi dari alert tersebut (content)
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}