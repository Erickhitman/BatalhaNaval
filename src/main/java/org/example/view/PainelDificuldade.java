package org.example.view;

import org.example.enums.Dificuldade;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

// Escolha da dificuldade. Mesmo esquema do menu (usa o montarConteudo de lá).
public class PainelDificuldade extends PainelFundo {
    public PainelDificuldade(Consumer<Dificuldade> onSelect, Runnable onBack) {
        setLayout(new GridBagLayout());

        JLabel title = Tema.rotulo("Escolha a dificuldade", Font.BOLD, 36f);
        JLabel subtitle = Tema.legenda("Quanto maior o mar, maior a frota", 13f);

        // os dois com o mesmo estilo de propósito, nenhum é "mais certo" que o outro
        JButton easyButton = Tema.botao(descricao(Dificuldade.EASY));
        JButton hardButton = Tema.botao(descricao(Dificuldade.HARD));
        JButton backButton = Tema.botao("Voltar");

        add(PainelMenuPrincipal.montarConteudo(
                Tema.colunaBotoes(easyButton, hardButton, backButton), title, subtitle));

        easyButton.addActionListener(event -> onSelect.accept(Dificuldade.EASY));
        hardButton.addActionListener(event -> onSelect.accept(Dificuldade.HARD));
        backButton.addActionListener(event -> onBack.run());
    }

    // monta o texto do botão a partir do enum, ex.: "Fácil  ·  6x6  ·  30 tiros"
    // (se mudarem os valores no Dificuldade o botão já atualiza sozinho)
    private static String descricao(Dificuldade dificuldade) {
        int n = dificuldade.getBoardSize();
        return dificuldade.getDisplayName() + "  ·  " + n + "x" + n + "  ·  " + dificuldade.getShotLimit() + " tiros";
    }
}
