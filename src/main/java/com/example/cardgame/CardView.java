package com.example.cardgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CardView extends StackPane {
    
    private Label rankLabel;

    public CardView(Card card) {
        // Konstruktor standar (default: Posisi Center)
        this(card, Pos.CENTER);
    }

    public CardView(Card card, Pos alignment) {
        Rectangle cardShape = new Rectangle(50, 75);
        cardShape.setArcWidth(8);
        cardShape.setArcHeight(8);
        cardShape.setFill(card != null ? Color.WHITE : Color.web("#2b2b2b"));
        cardShape.setStroke(Color.DARKGRAY);
        cardShape.setStrokeWidth(1.5);

        this.getChildren().add(cardShape);

        if (card != null) {
            rankLabel = new Label(String.valueOf(card.getRank()) + " " + card.getSuit());
            rankLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 13px;");

            // Gunakan alignment yang dikirim dari luar (bisa CENTER atau TOP_CENTER)
            StackPane.setAlignment(rankLabel, alignment);
            
            // Beri margin berbeda tergantung posisinya agar terlihat rapi
            if (alignment == Pos.TOP_CENTER) {
                StackPane.setMargin(rankLabel, new Insets(3, 0, 0, 0));
            }

            this.getChildren().add(rankLabel);
        }
    }
}