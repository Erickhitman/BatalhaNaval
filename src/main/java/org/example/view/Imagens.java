package org.example.view;

import org.example.enums.TipoEmbarcacao;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/*
 * Cuida das imagens que ficam em src/main/resources/imagens/.
 *
 * A ideia é: se a imagem existe, usa ela; se não existe, o jogo continua usando
 * os desenhos feitos no código (IconeEmbarcacao, IconeAgua...). Então dá pra ir
 * trocando uma imagem de cada vez sem quebrar nada.
 *
 * Nomes que o jogo procura:
 *   fundo.png       -> fundo de todas as telas
 *   tabuleiro.png   -> o mar dentro do tabuleiro (pode ser .jpg também)
 *   agua.png        -> marquinha de tiro na água
 *   submarino.png, destroier.png, cruzador.png, encouracado.png, porta_avioes.png
 *                   -> os navios (deitados, com a frente virada pra direita)
 */
public final class Imagens {
    // guarda o que já foi lido, pra não abrir o arquivo de novo toda vez que a tela redesenha
    // (guarda até o "não achei", por isso uso containsKey lá embaixo e não get != null)
    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private Imagens() {
    }

    // devolve a imagem, ou null se o arquivo não existir
    public static synchronized BufferedImage carregar(String nome) {
        if (!CACHE.containsKey(nome)) {
            BufferedImage imagem = ler(nome);
            if (imagem == null && nome.endsWith(".png")) {
                // se não tiver o .png tenta o .jpg com o mesmo nome (foto do mar é .jpg)
                imagem = ler(nome.substring(0, nome.length() - 4) + ".jpg");
            }
            CACHE.put(nome, imagem);
        }
        return CACHE.get(nome);
    }

    private static BufferedImage ler(String nome) {
        // getResourceAsStream procura dentro de src/main/resources (e dentro do .jar depois de compilado)
        try (InputStream entrada = Imagens.class.getResourceAsStream("/imagens/" + nome)) {
            return entrada == null ? null : ImageIO.read(entrada);
        } catch (Exception ignored) {
            return null; // deu ruim lendo? finge que não tem imagem e usa o desenho
        }
    }

    // qual arquivo é de qual navio
    public static String arquivoNavio(TipoEmbarcacao type) {
        return switch (type) {
            case SUBMARINE -> "submarino.png";
            case DESTROYER -> "destroier.png";
            case CRUISER -> "cruzador.png";
            case BATTLESHIP -> "encouracado.png";
            case AIRCRAFT_CARRIER -> "porta_avioes.png";
        };
    }

    // Ícone da lista "Embarcações restantes". Navio maior = ícone mais comprido.
    // A escala serve pra ele crescer junto quando a janela aumenta.
    public static Icon iconeFrota(TipoEmbarcacao type, double escala) {
        IconeFrota desenho = new IconeFrota(type);
        BufferedImage imagem = carregar(arquivoNavio(type));
        return imagem == null ? desenho
                : new IconeImagem(imagem, (int) ((70 + type.getLength() * 20) * escala),
                (int) (34 * escala), 1f);
    }

    // Navio inteiro dentro de uma casa. Hoje só é usado quando não tem imagem
    // (com imagem o tabuleiro desenha o navio em pedaços, ver PainelJogo).
    // revelado = navio que o jogador não achou, aparece meio apagado no fim.
    public static Icon iconeCasa(TipoEmbarcacao type, boolean revelado, int tamanhoCasa) {
        BufferedImage imagem = carregar(arquivoNavio(type));
        if (imagem == null) {
            return new IconeEmbarcacao(type, revelado);
        }
        int lado = tamanhoCasa - 12;
        return new IconeImagem(imagem, lado, lado, revelado ? 0.45f : 1f);
    }

    // marquinha do tiro na água
    public static Icon iconeAgua(int tamanhoCasa) {
        BufferedImage imagem = carregar("agua.png");
        int lado = tamanhoCasa - 12;
        return imagem == null ? new IconeAgua() : new IconeImagem(imagem, lado, lado, 1f);
    }

    // Estica a imagem até cobrir a área toda sem deformar (o que sobrar nas bordas fica de fora).
    // É o mesmo esquema do "background-size: cover" do CSS.
    public static void desenharCobrindo(Graphics2D desenho, BufferedImage imagem, int largura, int altura) {
        double escala = Math.max((double) largura / imagem.getWidth(), (double) altura / imagem.getHeight());
        int w = (int) Math.ceil(imagem.getWidth() * escala);
        int h = (int) Math.ceil(imagem.getHeight() * escala);
        desenho.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        desenho.drawImage(imagem, (largura - w) / 2, (altura - h) / 2, w, h, null); // centralizado
    }

    // Transforma uma imagem num Icon do Swing. Ela cabe inteira dentro da caixa
    // largura x altura, sem deformar, e fica centralizada.
    private static final class IconeImagem implements Icon {
        private final BufferedImage imagem;
        private final int largura;
        private final int altura;
        private final float opacidade; // 1 = normal, menos que 1 = mais transparente

        private IconeImagem(BufferedImage imagem, int largura, int altura, float opacidade) {
            this.imagem = imagem;
            this.largura = largura;
            this.altura = altura;
            this.opacidade = opacidade;
        }

        @Override
        public int getIconWidth() {
            return largura;
        }

        @Override
        public int getIconHeight() {
            return altura;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D desenho = (Graphics2D) graphics.create();
            // qualidade melhor na hora de diminuir a imagem, senão fica serrilhado
            desenho.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            desenho.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (opacidade < 1f) {
                desenho.setComposite(AlphaComposite.SrcOver.derive(opacidade));
            }
            // usa o menor dos dois zooms pra imagem caber inteira (o contrário do desenharCobrindo)
            double escala = Math.min((double) largura / imagem.getWidth(), (double) altura / imagem.getHeight());
            int w = (int) Math.round(imagem.getWidth() * escala);
            int h = (int) Math.round(imagem.getHeight() * escala);
            desenho.drawImage(imagem, x + (largura - w) / 2, y + (altura - h) / 2, w, h, null);
            desenho.dispose();
        }
    }
}
