package com.petcareadmin.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Custom TableCell — durum değerini (PLANNED, COMPLETED, CANCELLED, PENDING, DONE vb.)
 * renkli bir Canvas badge'i olarak çizer.
 *
 * Bu sınıf, "Custom GUI" kriterindeki standart bileşenlerin dışında özel grafik
 * gereksinimine karşılık gelmektedir (JavaFX Canvas API + GraphicsContext kullanımı).
 *
 * @param <T> TableView satır tipi
 */
public class StatusBadgeCell<T> extends TableCell<T, String> {

    private static final double BADGE_WIDTH = 120;
    private static final double BADGE_HEIGHT = 26;
    private static final double CORNER_RADIUS = 13;
    private static final double FONT_SIZE = 11.5;

    private final Canvas canvas;

    public StatusBadgeCell() {
        this.canvas = new Canvas(BADGE_WIDTH, BADGE_HEIGHT);
        HBox container = new HBox(canvas);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(2, 0, 2, 0));
        setGraphic(container);
        setText(null);
    }

    @Override
    protected void updateItem(String status, boolean empty) {
        super.updateItem(status, empty);
        if (empty || status == null) {
            setGraphic(null);
            return;
        }
        setGraphic(canvas.getParent());
        drawBadge(status);
    }

    /**
     * Canvas üzerine yuvarlak köşeli dikdörtgen (badge) çizer ve ortasına durum metnini yazar.
     * Durum değerine göre renk seçimi yapılır — bu kısım Custom Graphics örneğidir.
     */
    private void drawBadge(String status) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, BADGE_WIDTH, BADGE_HEIGHT);

        // Duruma göre renk seçimi (Strategy pattern benzeri dispatch)
        Color bgColor = resolveBgColor(status);
        Color textColor = resolveTextColor(status);

        // Yuvarlak köşeli dikdörtgen (Custom Graphics)
        gc.setFill(bgColor);
        gc.fillRoundRect(0, 0, BADGE_WIDTH, BADGE_HEIGHT, CORNER_RADIUS * 2, CORNER_RADIUS * 2);

        // İnce çerçeve
        gc.setStroke(bgColor.darker());
        gc.setLineWidth(1.2);
        gc.strokeRoundRect(0.6, 0.6, BADGE_WIDTH - 1.2, BADGE_HEIGHT - 1.2,
                CORNER_RADIUS * 2, CORNER_RADIUS * 2);

        // Metin
        gc.setFill(textColor);
        gc.setFont(Font.font("System", FontWeight.BOLD, FONT_SIZE));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);
        gc.fillText(status, BADGE_WIDTH / 2.0, BADGE_HEIGHT / 2.0);
    }

    /** Durum değerine göre arka plan rengi döndürür. */
    private Color resolveBgColor(String status) {
        return switch (status.toUpperCase()) {
            case "PLANNED", "PENDING"    -> Color.web("#dbeafe"); // mavi tonu
            case "COMPLETED", "DONE"     -> Color.web("#dcfce7"); // yeşil tonu
            case "CANCELLED"             -> Color.web("#fee2e2"); // kırmızı tonu
            case "ACTIVE"                -> Color.web("#d1fae5"); // koyu yeşil
            case "PASSIVE"               -> Color.web("#f3f4f6"); // gri
            default                      -> Color.web("#fef9c3"); // sarı
        };
    }

    /** Durum değerine göre metin rengi döndürür. */
    private Color resolveTextColor(String status) {
        return switch (status.toUpperCase()) {
            case "PLANNED", "PENDING"    -> Color.web("#1d4ed8");
            case "COMPLETED", "DONE"     -> Color.web("#166534");
            case "CANCELLED"             -> Color.web("#991b1b");
            case "ACTIVE"                -> Color.web("#065f46");
            case "PASSIVE"               -> Color.web("#6b7280");
            default                      -> Color.web("#854d0e");
        };
    }
}
