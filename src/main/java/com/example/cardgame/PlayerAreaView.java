package com.example.cardgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.function.Consumer;

public class PlayerAreaView extends VBox {
    
    private HBox handContainer;
    private HBox researchContainer;
    private HBox publicationContainer;

    public PlayerAreaView(String playerName) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        // Beri latar belakang tipis agar area setiap pemain jelas terbatasi di meja
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2); -fx-background-radius: 10px;");

        // 0. Judul / Nama Pemain di pinggir meja
        Label nameLabel = new Label(playerName);
        nameLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold; -fx-font-size: 13px;");

        // --- BAGIAN A: KARTU DI TANGAN ---
        VBox handSection = createSectionWithOutline("Kartu di Tangan");
        handContainer = new HBox(8);
        handContainer.setAlignment(Pos.CENTER);
        handSection.getChildren().add(handContainer);

        // --- BAGIAN B: RESEARCH BOARD ---
        VBox researchSection = createSectionWithOutline("Research Board");
        researchContainer = new HBox(15);
        researchContainer.setAlignment(Pos.CENTER);
        researchSection.getChildren().add(researchContainer);

        // --- BAGIAN C: PUBLICATION BOARD ---
        VBox publicationSection = createSectionWithOutline("Publication Board");
        publicationContainer = new HBox(15);
        publicationContainer.setAlignment(Pos.CENTER);
        publicationSection.getChildren().add(publicationContainer);

        // Gabungkan semuanya ke dalam PlayerAreaView
        this.getChildren().addAll(nameLabel, handSection, researchSection, publicationSection);
    }

    /**
     * Helper untuk membuat kotak ber-outline dengan judul di atasnya
     */
    private VBox createSectionWithOutline(String titleText) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(6));
        
        // Style CSS untuk membuat outline kotak dan sudut melengkung
        container.setStyle(
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 1.5px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-color: rgba(255, 255, 255, 0.03);"
        );

        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-text-fill: #a5d6a7; -fx-font-size: 11px; -fx-font-weight: bold;");

        container.getChildren().add(titleLabel);
        return container;
    }

    /**
     * Helper untuk membuat 1 kolom tumpukan kartu lengkap dengan label Suit di atasnya (AI, Security, dll)
     */
    private VBox createSuitPileColumn(String suitName, List<Card> cards) {
        // 1. Gunakan TOP_CENTER agar seluruh isi kolom tumbuh ke bawah secara konsisten
        VBox column = new VBox(5);
        column.setAlignment(Pos.TOP_CENTER); 

        // Label kategori suit (posisinya sekarang dijamin diam terkunci di atas)
        Label suitLabel = new Label(suitName);
        suitLabel.setStyle("-fx-text-fill: #ffecb3; -fx-font-size: 9px; -fx-font-weight: bold;");

        // 2. Bungkus tumpukan kartu dengan StackPane berukuran tetap (fixed height)
        // Ini membuat area tumpukan memiliki ruang sendiri, sehingga penambahan kartu 
        // tidak akan menggeser posisi label di atasnya.
        StackPane pileArea = new StackPane();
        pileArea.setMinHeight(140); // Sesuaikan tinggi area tumpukan
        pileArea.setAlignment(Pos.TOP_CENTER);

        // Wadah tumpukan kartu dengan efek bertumpuk (overlapping)
        VBox pileBox = new VBox(-58); 
        pileBox.setAlignment(Pos.TOP_CENTER);

        if (cards != null && !cards.isEmpty()) {
            int totalCards = cards.size();
            for (int i = 0; i < totalCards; i++) {
                Card card = cards.get(i);
                
                // Kartu terakhir (paling atas) di CENTER, kartu di bawahnya di TOP_CENTER
                Pos cardAlignment = (i == totalCards - 1) ? Pos.CENTER : Pos.TOP_CENTER;
                
                CardView cardView = new CardView(card, cardAlignment);
                pileBox.getChildren().add(cardView);
            }
        } else {
            // Slot kosong
            pileBox.getChildren().add(new CardView(null, Pos.CENTER));
        }

        pileArea.getChildren().add(pileBox);
        column.getChildren().addAll(suitLabel, pileArea);
    return column;
}

    // --- METHOD UPDATE YANG DIPANGGIL DARI MAIN ---

    public void updateHand(List<Card> cards, Consumer<Card> onCardClicked) {
        handContainer.getChildren().clear();
        if (cards != null) {
            for (Card card : cards) {
                CardView cardView = new CardView(card);
                cardView.setOnMouseClicked(e -> {
                    if (onCardClicked != null) {
                        onCardClicked.accept(card);
                    }
                });
                handContainer.getChildren().add(cardView);
            }
        }
    }

    public void updateResearchBoard(ResearchBoard researchBoard) {
        researchContainer.getChildren().clear();
        
        // Asumsi ResearchBoard memiliki metode untuk mengambil list kartu per suit
        researchContainer.getChildren().addAll(
            createSuitPileColumn("AI", researchBoard.getPile(Suit.AI)),
            createSuitPileColumn("Security", researchBoard.getPile(Suit.SEC)),
            createSuitPileColumn("Software Eng", researchBoard.getPile(Suit.SE)),
            createSuitPileColumn("Theoretical CS", researchBoard.getPile(Suit.TCS))
        );
    }

    public void updatePublicationBoard(PublicationBoard publicationBoard) {
        publicationContainer.getChildren().clear();
        
        // Asumsi PublicationBoard memiliki metode serupa untuk mengambil list kartu per suit
        publicationContainer.getChildren().addAll(
            createSuitPileColumn("AI", publicationBoard.getPile(Suit.AI)),
            createSuitPileColumn("Security", publicationBoard.getPile(Suit.SEC)),
            createSuitPileColumn("Software Eng", publicationBoard.getPile(Suit.SE)),
            createSuitPileColumn("Theoretical CS", publicationBoard.getPile(Suit.TCS))
        );
    }
}