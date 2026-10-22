package com.example.cardgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.List;

public class CenterBoardView extends VBox {
    private Label deckCountLabel;
    private Label discardCountLabel;
    private HBox literatureContainer;
    private VBox scoreBoxContainer;

    public CenterBoardView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(12);
        this.setStyle("-fx-padding: 10px;");

        // 1. Baris atas: Deck & Discard Pile berdampingan
        HBox deckDiscardBox = new HBox(25);
        deckDiscardBox.setAlignment(Pos.CENTER);

        // --- DECK PILE ---
        VBox deckCol = new VBox(3);
        deckCol.setAlignment(Pos.CENTER);
        Label deckTitle = new Label("Deck");
        deckTitle.setStyle("-fx-text-fill: #a5d6a7; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        StackPane deckPane = new StackPane();
        Rectangle deckBack = new Rectangle(50, 75);
        deckBack.setFill(Color.DARKRED);
        deckBack.setStroke(Color.GOLD);
        deckBack.setArcWidth(6);
        deckBack.setArcHeight(6);
        deckCountLabel = new Label("0");
        deckCountLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
        deckPane.getChildren().addAll(deckBack, deckCountLabel);
        deckCol.getChildren().addAll(deckTitle, deckPane);

        // --- DISCARD PILE (Awalnya kosong) ---
        VBox discardCol = new VBox(3);
        discardCol.setAlignment(Pos.CENTER);
        Label discardTitle = new Label("Discard Pile");
        discardTitle.setStyle("-fx-text-fill: #a5d6a7; -fx-font-size: 10px; -fx-font-weight: bold;");

        StackPane discardPane = new StackPane();
        Rectangle discardBack = new Rectangle(50, 75);
        discardBack.setFill(Color.DARKSLATEGRAY);
        discardBack.setStroke(Color.LIGHTGRAY);
        discardBack.setArcWidth(6);
        discardBack.setArcHeight(6);
        discardCountLabel = new Label("0");
        discardCountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 9px;");
        discardPane.getChildren().addAll(discardBack, discardCountLabel);
        discardCol.getChildren().addAll(discardTitle, discardPane);

        VBox scorePanel = new VBox(5);
        scorePanel.setAlignment(Pos.TOP_LEFT);
        scorePanel.setPrefWidth(140);
        
        // Memberikan outline (bingkai) dan padding yang rapi
        scorePanel.setStyle(
            "-fx-border-color: #ffd54f; " +
            "-fx-border-width: 1.5px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-color: rgba(0, 0, 0, 0.3); " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px;"
        );

        // Judul "SKOR" dengan ukuran font yang agak besar
        Label scoreTitle = new Label("SCORE");
        scoreTitle.setStyle("-fx-text-fill: #ffd54f; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Wadah list teks skor per pemain
        scoreBoxContainer = new VBox(3);
        scoreBoxContainer.setAlignment(Pos.TOP_LEFT);

        scorePanel.getChildren().addAll(scoreTitle, scoreBoxContainer);

        deckDiscardBox.getChildren().addAll(deckCol, discardCol, scorePanel);

        // 2. Baris bawah: Literature dengan Outline dan Judul
        VBox literatureSection = new VBox(5);
        literatureSection.setAlignment(Pos.CENTER);
        literatureSection.setPadding(new Insets(6));
        literatureSection.setStyle(
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 1.5px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-color: rgba(255, 255, 255, 0.03);"
        );

        Label litTitle = new Label("Literature (Maks 6)");
        litTitle.setStyle("-fx-text-fill: #a5d6a7; -fx-font-size: 11px; -fx-font-weight: bold;");

        literatureContainer = new HBox(8);
        literatureContainer.setAlignment(Pos.CENTER);
        for (int i = 0; i < 6; i++) {
            literatureContainer.getChildren().add(new CardView(null));
        }

        literatureSection.getChildren().addAll(litTitle, literatureContainer);

        this.getChildren().addAll(deckDiscardBox, literatureSection);
    }

    public void updateDeck(Deck deck) {
        deckCountLabel.setText(String.valueOf(deck.getRemainingCards()));
    }

    public void updateLiterature(Literature literature, java.util.function.Consumer<Card> onCardClicked) {
        literatureContainer.getChildren().clear();
        List<Card> cards = literature != null ? literature.getCards() : null; 
        
        for (int i = 0; i < 6; i++) {
            if (cards != null && i < cards.size()) {
                Card card = cards.get(i);
                CardView cardView = new CardView(card);
                
                // Tambahkan event klik khusus untuk kartu yang ada isinya
                cardView.setOnMouseClicked(e -> {
                    if (onCardClicked != null) {
                        onCardClicked.accept(card);
                    }
                });
                
                literatureContainer.getChildren().add(cardView);
            } else {
                // Slot kosong (null) tidak perlu event klik
                literatureContainer.getChildren().add(new CardView(null));
            }
        }
    }

    public void updateDiscardPile(DiscardPile discardPile) {
        if (discardPile != null) {
            // Asumsi kelas DiscardPile Anda memiliki method untuk mengambil jumlah/ukuran kartu, 
            // misalnya size() atau getRemainingCards() atau getCards().
            int count = discardPile.size(); // Sesuaikan dengan nama method di kelas DiscardPile Anda
            
            if (count > 0) {
                discardCountLabel.setText(String.valueOf(count));
            } else {
                discardCountLabel.setText("0");
            }
        }
    }

    /**
     * Memperbarui daftar skor pemain secara dinamis berdasarkan array pemain
     */
    public void updateScores(Player[] players) {
        scoreBoxContainer.getChildren().clear();

        if (players != null) {
            for (int i = 0; i < players.length; i++) {
                // Ambil skor pemain (pastikan kelas Player Anda punya method getScore() atau setara)
                int score = (players[i] != null) ? players[i].getScore() : 0;

                Label playerLabel = new Label("Pemain " + (i + 1) + ": " + score);
                playerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
                
                scoreBoxContainer.getChildren().add(playerLabel);
            }
        }
    }
}