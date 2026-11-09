package org.example.view;

import org.example.enums.Dificuldade;
import org.example.enums.EstadoDisparo;
import org.example.model.ResultadoDisparo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.function.BiConsumer;

public class PainelJogo extends JPanel {
    private final JLabel shotsLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel messageLabel = new JLabel("Clique em uma casa para disparar.", SwingConstants.CENTER);
    private final JButton[][] cells;

    public PainelJogo(Dificuldade difficulty, BiConsumer<Integer, Integer> onShoot, Runnable onMenu) {
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new GridLayout(3, 1, 4, 4));
        JLabel title = new JLabel("BATALHA NAVAL - " + difficulty.getDisplayName(), SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        shotsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        messageLabel.setForeground(new Color(35, 83, 112));
        header.add(title);
        header.add(shotsLabel);
        header.add(messageLabel);
        add(header, BorderLayout.NORTH);

        int size = difficulty.getBoardSize();
        JPanel board = new PainelTabuleiro(new GridLayout(size + 1, size + 1, 2, 2));
        board.add(coordinateLabel(""));
        cells = new JButton[size][size];
        for (int column = 0; column < size; column++) {
            board.add(coordinateLabel(String.valueOf((char) ('A' + column))));
        }
        for (int row = 0; row < size; row++) {
            board.add(coordinateLabel(Integer.toString(row + 1)));
            for (int column = 0; column < size; column++) {
                JButton cell = new JButton();
                cell.setFocusable(false);
                cell.setContentAreaFilled(false);
                cell.setOpaque(false);
                cell.setBorder(BorderFactory.createLineBorder(new Color(17, 70, 91), 1));
                cell.setToolTipText("Linha " + (row + 1) + ", coluna " + (char) ('A' + column));
                final int selectedRow = row;
                final int selectedColumn = column;
                cell.addActionListener(event -> onShoot.accept(selectedRow, selectedColumn));
                cells[row][column] = cell;
                board.add(cell);
            }
        }
        add(board, BorderLayout.CENTER);

        JButton menuButton = new JButton("Voltar ao menu");
        menuButton.addActionListener(event -> onMenu.run());
        JPanel footer = new JPanel(new BorderLayout());
        footer.add(menuButton, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        updateShots(difficulty.getShotLimit());
    }

    public void markShot(int row, int column, ResultadoDisparo result) {
        JButton cell = cells[row][column];
        cell.setEnabled(false);
        if (result.getOutcome() == EstadoDisparo.HIT) {
            cell.setIcon(new IconeEmbarcacao(result.getShipType()));
            cell.setToolTipText(result.getShipType().getDisplayName() + " atingido");
            setMessage("Acertou! Embarcação atingida: " + result.getShipType().getDisplayName() + ".");
        } else {
            cell.setIcon(new IconeAgua());
            cell.setToolTipText("Tiro na agua");
            setMessage("Tiro na água.");
        }
    }

    public void updateShots(int shotsRemaining) {
        shotsLabel.setText("Tiros restantes: " + shotsRemaining);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    private JLabel coordinateLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(new Color(5, 39, 55));
        return label;
    }

    private static class PainelTabuleiro extends JPanel {
        private PainelTabuleiro(GridLayout layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D desenho = (Graphics2D) graphics.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            desenho.setPaint(new GradientPaint(0, 0, new Color(60, 190, 218),
                    getWidth(), getHeight(), new Color(8, 105, 153)));
            desenho.fillRect(0, 0, getWidth(), getHeight());
            desenho.setColor(new Color(207, 247, 249, 105));
            for (int y = 12; y < getHeight(); y += 24) {
                for (int x = (y / 24 % 2) * 12; x < getWidth(); x += 32) {
                    desenho.drawArc(x, y, 22, 9, 15, 150);
                    desenho.drawArc(x + 10, y + 7, 18, 8, 20, 145);
                }
            }
            desenho.dispose();
        }
    }
}
