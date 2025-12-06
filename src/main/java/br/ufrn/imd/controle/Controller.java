package br.ufrn.imd.controle;

import br.ufrn.imd.modelo.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.math3.random.RandomDataGenerator;

public class Controller {

    @FXML
    /*@ spec_public @*/ private AnchorPane gamePane;
    @FXML
    /*@ spec_public @*/ private GridPane playerGrid;
    @FXML
    /*@ spec_public @*/ private GridPane computerGrid;
    @FXML
    /*@ spec_public @*/ private Button startGameButton;
    @FXML
    /*@ spec_public @*/ private Label label;
    @FXML
    /*@ spec_public @*/ private Label labelRadar;

    /*@ spec_public @*/ private Game game;
    /*@ spec_public @*/ private Player jogador;
    /*@ spec_public @*/ private Player computador;
    /*@ spec_public @*/ private Board jogadorTabuleiro;
    /*@ spec_public @*/ private Board computadorTabuleiro;
    /*@ spec_public @*/ private List<Ship> jogadorTabuleiroNavios;
    /*@ spec_public @*/ private List<Ship> computadorTabuleiroNavios;

    /*@ spec_public @*/ private GameState estado;
    /*@ spec_public @*/ private boolean deitado;
    /*@ spec_public @*/ private List<CellButton> radar;

    /*@ spec_public @*/ private Map<ShipType, Supplier<Ship>> shipFactory;
    /*@ spec_public @*/ private Map<ShipType, List<CellButton>> alvosPorNavio;

    /*@ public normal_behavior
      @   ensures game != null;
      @   ensures jogador != null && computador != null;
      @   ensures jogadorTabuleiro != null && computadorTabuleiro != null;
      @*/
    public Controller() {
        game = new Game();
        estado = GameState.CLIQUE;
        deitado = true;
        radar = new ArrayList<>();
        jogador = game.getPlayer1();
        computador = game.getPlayer2();
        jogadorTabuleiro = jogador.getBoard();
        computadorTabuleiro = computador.getBoard();
        jogadorTabuleiroNavios = jogadorTabuleiro.getShips();
        computadorTabuleiroNavios = computadorTabuleiro.getShips();

        shipFactory = new HashMap<>();
        shipFactory.put(ShipType.CORVETA, Corvette::new);
        shipFactory.put(ShipType.SUBMARINO, Submarine::new);
        shipFactory.put(ShipType.FRAGATA, Frigate::new);
        shipFactory.put(ShipType.DESTROYER, Destroyer::new);

        alvosPorNavio = new HashMap<>();
        alvosPorNavio.put(ShipType.CORVETA,   new ArrayList<CellButton>());
        alvosPorNavio.put(ShipType.SUBMARINO, new ArrayList<CellButton>());
        alvosPorNavio.put(ShipType.FRAGATA,   new ArrayList<CellButton>());
        alvosPorNavio.put(ShipType.DESTROYER, new ArrayList<CellButton>());

    }

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    @FXML
    public void initialize() {
        gamePane.setStyle("-fx-background-color: #B9D9EB;");
        createGrid(playerGrid, PlayerName.JOGADOR);
        createGrid(computerGrid, PlayerName.PC);

        startGameButton.setOnAction(event -> {
            try {
                handleStartGame();
            } catch (CelulaInvalidaException e) {
                throw new RuntimeException(e);
            }
        });

        gamePane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                alternarOrientacaoNavio();
            }
        });
    }

    private void createGrid(GridPane grid, PlayerName gridType) {
        Board board1;
        if (gridType == PlayerName.JOGADOR){
            board1 = jogadorTabuleiro;
        } else {
            board1 = computadorTabuleiro;
        }
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = new Rectangle (30,30);
                cell.getStyleClass().add("cell");
                grid.add(cell, col, row);

                CellButton cellButton = board1.getCell(row, col);
                cellButton.setNode(cell);

                cell.setOnMouseClicked(event -> {
                    try {
                        handleCellClick(event, gridType);
                    } catch (CelulaInvalidaException e) {
                        updateLabel(e.getMessage());
                        System.out.println(e.getMessage());
                    }
                });
            }
        }
    }

    private void posicionarNavio(ShipType type, CellButton celIni, Board board) {
        Ship navio = shipFactory.get(type).get();

        int fileira = celIni.getRow();
        int coluna = celIni.getCol();
        boolean horizontal = deitado;

        try {
            jogador.placeShip(navio, fileira, coluna, horizontal);
            updateBoard(board);
        } catch (IllegalArgumentException e) {
            updateLabel(e.getMessage());
        }
    }

    private void processarSelecaoAlvos(ShipType tipoNavio, List<CellButton> lista,
                                       int fileira, int coluna)
            throws ArrayIndexOutOfBoundsException, CelulaInvalidaException {
        selecionarAlvos(tipoNavio, lista, fileira, coluna);
        radar.add(computadorTabuleiro.getCell(fileira, coluna));
        estado = GameState.SELECIONAR_ALVOS;
    }

    private void handleCellClick(MouseEvent event, PlayerName gridType)
            throws CelulaInvalidaException {

        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode == null) return;

        Integer colunaObj = GridPane.getColumnIndex(clickedNode);
        Integer fileiraObj = GridPane.getRowIndex(clickedNode);
        if (colunaObj == null || fileiraObj == null) return;

        int coluna = colunaObj;
        int fileira = fileiraObj;

        Board board1 = gridType == PlayerName.JOGADOR ? jogadorTabuleiro : computadorTabuleiro;
        CellButton celIni = board1.getCell(fileira, coluna);

        switch (estado) {

            case CLIQUE:
                updateLabel("Célula clicada em col: " + coluna + " fileira: " + fileira);
                return;

            case POSICIONAR_CORVETA:
                posicionarNavio(ShipType.CORVETA, celIni, board1);
                estado = GameState.CLIQUE;
                return;

            case POSICIONAR_SUBMARINO:
                posicionarNavio(ShipType.SUBMARINO, celIni, board1);
                estado = GameState.CLIQUE;
                return;

            case POSICIONAR_FRAGATA:
                posicionarNavio(ShipType.FRAGATA, celIni, board1);
                estado = GameState.CLIQUE;
                return;

            case POSICIONAR_DESTROYER:
                posicionarNavio(ShipType.DESTROYER, celIni, board1);
                estado = GameState.CLIQUE;
                return;

            case SELECIONAR_ALVOS_CORVETA:
                processarSelecaoAlvos(ShipType.CORVETA, alvosPorNavio.get(ShipType.CORVETA), fileira, coluna);
                return;

            case SELECIONAR_ALVOS_SUBMARINO:
                processarSelecaoAlvos(ShipType.SUBMARINO, alvosPorNavio.get(ShipType.SUBMARINO), fileira, coluna);
                return;

            case SELECIONAR_ALVOS_FRAGATA:
                processarSelecaoAlvos(ShipType.FRAGATA, alvosPorNavio.get(ShipType.FRAGATA), fileira, coluna);
                return;

            case SELECIONAR_ALVOS_DESTROYER:
                processarSelecaoAlvos(ShipType.DESTROYER, alvosPorNavio.get(ShipType.DESTROYER), fileira, coluna);
                return;

            case SELECIONAR_ALVOS:
            case ENDGAME:
                return;
        }
    }

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public void handleButtonCorvette() {
        handleButtonShip(ShipType.CORVETA);
    }

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public void handleButtonSubmarine() {
        handleButtonShip(ShipType.SUBMARINO);
    }

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public void handleButtonFrigate() {
        handleButtonShip(ShipType.FRAGATA);
    }

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public void handleButtonDestroyer() {
        handleButtonShip(ShipType.DESTROYER);
    }

    private void handleButtonShip(ShipType type) {
        Supplier<Ship> factory = shipFactory.get(type);
        List<CellButton> listaAlvos = alvosPorNavio.get(type);

        boolean posicionado = jogadorTabuleiroNavios.stream()
                .anyMatch(s -> s.getClass() == factory.get().getClass());

        boolean vivo = jogadorTabuleiroNavios.stream()
                .filter(s -> s.getClass() == factory.get().getClass())
                .anyMatch(Ship::isAlive);

        switch (estado) {
            case CLIQUE:
                if (!posicionado) {
                    updateLabel("Posicione seu " + type);
                    estado = GameState.valueOf("POSICIONAR_" + type.name());
                } else {
                    updateLabel("Você já posicionou esse navio.");
                }
                return;
            case SELECIONAR_ALVOS:
                if (!vivo) {
                    updateLabel("Seu " + type + " está afundado!");
                    return;
                }
                if (!listaAlvos.isEmpty()) {
                    updateLabel("Este navio já mirou.");
                    return;
                }
                updateLabel("Selecione alvos para " + type);
                estado = GameState.valueOf("SELECIONAR_ALVOS_" + type.name());
                return;
            default:
                return;
        }
    }

    /*@ public behavior
      @   assignable \everything;
      @*/
    public void handleButtonAtirar() throws InterruptedException {
        if (estado == GameState.SELECIONAR_ALVOS){
            int naviosVivos = jogadorTabuleiroNavios.size();
            int naviosMirados = 0;

            for (ShipType t : ShipType.values()) {
                if (!alvosPorNavio.get(t).isEmpty()) naviosMirados++;
            }

            if (naviosVivos == naviosMirados){

                atiraCelulasMiradas(computadorTabuleiro);
                updateBoard(computadorTabuleiro);

                updateLabel("Você atirou no campo inimigo");

                for (ShipType t : ShipType.values()) {
                    alvosPorNavio.get(t).clear();
                }

                computadorTabuleiro.attListaNavios();
                ataquePc();

            } else {
                updateLabel("Você ainda não mirou com algum navio");
            }

            jogadorTabuleiro.attListaNavios();
            computadorTabuleiro.attListaNavios();

            updateLabel("SEUS navios vivos: " + jogadorTabuleiro.getShips().size() +
                        "    Navios vivos do PC: " + computadorTabuleiro.getShips().size());

            if (computadorTabuleiro.getShips().size() == 0){
                updateLabel("PC PERDEU");
                estado = GameState.ENDGAME;
            } else if (jogadorTabuleiro.getShips().size() == 0){
                updateLabel("PLAYER PERDEU");
                estado = GameState.ENDGAME;
            }
        }
    }

    private void ataquePc() {
        int quantNavios = computadorTabuleiroNavios.size();
        List<CellButton> cellsAttk;

        for (int i = 0; i < quantNavios; i++) {
            RandomDataGenerator randomData = new RandomDataGenerator();
            int fileira = randomData.nextInt(0, 9);
            int coluna = randomData.nextInt(0, 9);

            cellsAttk = computadorTabuleiroNavios.get(i).attack(fileira, coluna);
            for (CellButton c : cellsAttk) {
                if (c.getRow() < 10 && c.getCol() < 10 && c.getRow() >= 0 && c.getCol() >= 0) {
                    jogadorTabuleiro.hitCells(c.getRow(), c.getCol());
                }
            }
            jogadorTabuleiro.attListaNavios();
            updateBoard(jogadorTabuleiro);
            if (jogadorTabuleiro.getShips().size() == 0) {
                updateLabel("PLAYER PERDEU");
                estado = GameState.ENDGAME;
            }
        }
    }

    private void handleStartGame() throws CelulaInvalidaException {
        if (jogadorTabuleiroNavios.size() == 4 && estado != GameState.ENDGAME) {
            if (computadorTabuleiroNavios.size() < 4) {
                posicionaComputador();
            }
            estado = GameState.SELECIONAR_ALVOS;
            label.setText("É o seu turno, faça seu(s) ataque(s)");
        } else if (estado == GameState.ENDGAME) {
            // nada a fazer
        } else {
            label.setText("Voce ainda nao posicionou todos os navios!!!");
        }
    }

    private void posicionaComputador() throws CelulaInvalidaException {
        for (int i = 2; i < 6; i++) {
            posicionaNaviosPc(i);
        }
    }

    private void posicionaNaviosPc(int tamanho) throws CelulaInvalidaException {
        RandomDataGenerator randomData = new RandomDataGenerator();
        int virado = randomData.nextInt(0, 1);

        boolean sucessoPosicao;
        List<CellButton> posicoesNavio = new ArrayList<>();
        sucessoPosicao = adicionarPosicoesNavioPc(posicoesNavio, tamanho, virado);
        try {
            if (sucessoPosicao && !posicoesNavio.isEmpty()) {
                CellButton inicio = posicoesNavio.get(0);
                int row = inicio.getRow();
                int col = inicio.getCol();
                boolean horizontal = (virado == 0);

                Ship ship;
                switch (tamanho) {
                    case 2:
                        ship = new Corvette();
                        break;
                    case 3:
                        ship = new Submarine();
                        break;
                    case 4:
                        ship = new Frigate();
                        break;
                    case 5:
                        ship = new Destroyer();
                        break;
                    default:
                        throw new IllegalArgumentException("Tamanho de navio inválido: " + tamanho);
                }

                computadorTabuleiro.placeShip(ship, row, col, horizontal);
                updateBoard(computadorTabuleiro);
            }
        } catch (IllegalArgumentException e) {
            updateLabel(e.getMessage());
        }
    }

    private void selecionarAlvos(ShipType tipoNavio, List<CellButton> alvosTemp,
                                 int fileira, int coluna)
            throws CelulaInvalidaException, ArrayIndexOutOfBoundsException {
        Ship navio;
        List<CellButton> listAlvos;

        switch (tipoNavio) {
            case CORVETA:
                navio = new Corvette();
                break;
            case SUBMARINO:
                navio = new Submarine();
                break;
            case FRAGATA:
                navio = new Frigate();
                break;
            case DESTROYER:
                navio = new Destroyer();
                break;
            default:
                throw new IllegalArgumentException("Tipo de navio inválido: " + tipoNavio.toString());
        }

        listAlvos = navio.attack(fileira, coluna);

        if (computadorTabuleiro.getCell(fileira, coluna).isHit()) {
            estado = GameState.SELECIONAR_ALVOS;
            throw new CelulaInvalidaException("Você está mirando numa célula já atingida");
        } else {
            if (!alvosTemp.isEmpty()) {
                alvosTemp.clear();
            }
            alvosTemp.addAll(listAlvos);

            for (CellButton celula : listAlvos) {
                if (celula.getCol() < 10 && celula.getCol() >= 0 &&
                    celula.getRow() < 10 && celula.getRow() >= 0) {
                    computadorTabuleiro.getCell(celula.getRow(), celula.getCol()).setAimed(true);
                }
            }
            updateBoard(computadorTabuleiro);
        }
    }

    private boolean adicionarPosicoesNavioPc(List<CellButton> posicoesNavio, int tamanho, int virado) {
        Random random = new Random();

        while (true) {
            int fileira = random.nextInt(10);
            int coluna = random.nextInt(10);

            boolean posicaoValida = true;
            posicoesNavio.clear();

            if (virado == 0) {
                for (int i = 1; i <= tamanho; i++) {
                    if ((coluna + i) >= 10 ||
                        computadorTabuleiro.getCell(fileira, coluna + i).getState() == CellButton.State.SHIP) {
                        posicaoValida = false;
                        break;
                    } else {
                        CellButton celulaAdjacente = computadorTabuleiro.getCell(fileira, coluna + i);
                        posicoesNavio.add(celulaAdjacente);
                    }
                }
            } else {
                for (int i = 1; i <= tamanho; i++) {
                    if ((fileira + i) >= 10 ||
                        computadorTabuleiro.getCell(fileira + i, coluna).getState() == CellButton.State.SHIP) {
                        posicaoValida = false;
                        break;
                    } else {
                        CellButton celulaAdjacente = computadorTabuleiro.getCell(fileira + i, coluna);
                        posicoesNavio.add(celulaAdjacente);
                    }
                }
            }

            if (posicaoValida) {
                return true;
            }
        }
    }

    private boolean adicionarPosicoesNavio(CellButton celIni, List<CellButton> posicoesNavio, int tamanho) {
        int fileira = celIni.getRow();
        int coluna = celIni.getCol();

        try {
            if (deitado) {
                for (int i = 1; i < tamanho; i++) {
                    if ((coluna + i) > 10) {
                        throw new NavioForaDoMapaException("O navio ficou em parte fora do mapa, posicione-o de novo");
                    }
                    posicoesNavio.add(jogadorTabuleiro.getCell(fileira, coluna + i));
                }
            } else {
                for (int i = 1; i < tamanho; i++) {
                    if ((fileira + i) > 10) {
                        throw new NavioForaDoMapaException("O navio ficou em parte fora do mapa, posicione-o de novo");
                    }
                    posicoesNavio.add(jogadorTabuleiro.getCell(fileira + i, coluna));
                }
            }
        } catch (NavioForaDoMapaException e) {
            desfazerNavio(posicoesNavio);
            updateBoard(jogadorTabuleiro);
            updateLabel(e.getMessage());
            return false;
        } catch (ArrayIndexOutOfBoundsException e){
            desfazerNavio(posicoesNavio);
            updateBoard(jogadorTabuleiro);
            updateLabel(e.getMessage());
            return false;
        }
        return true;
    }

    private void desfazerNavio(List<CellButton> posicoesNavio) {
        for (CellButton cell : posicoesNavio) {
            cell.undoShipPositioning();
        }
    }

    private void atiraCelulasMiradas(Board b){
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                CellButton cell = b.getCell(row, col);
                if (cell.getAimed()){
                    cell.setAimed(false);
                    cell.hit();
                }
            }
        }

        List<Ship> toRemove = new ArrayList<>();
        for (Ship ship : b.getShips()) {
            if (!ship.isAlive()) {
                toRemove.add(ship);
            }
        }
        b.getShips().removeAll(toRemove);
    }

    private void updateBoard(Board b) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                CellButton cell = b.getCell(row, col);
                Node cellNode = (Node) cell.getNode();

                if (cellNode != null) {
                    cellNode.getStyleClass().removeAll("cell-ship", "cell-hit", "cell-aimed", "cell-ship-hit");

                    if (cell.getAimed()){
                        cellNode.getStyleClass().add("cell-aimed");
                    } else {
                        if (cell.getState() == CellButton.State.SHIP && b == jogadorTabuleiro){
                            cellNode.getStyleClass().add("cell-ship");
                        }
                    }

                    if (cell.isHit()){
                        if (cell.getState() == CellButton.State.SHIP){
                            cellNode.getStyleClass().add("cell-ship-hit");
                        } else if (cell.getState() == CellButton.State.WATER) {
                            cellNode.getStyleClass().add("cell-hit");
                        }
                    }

                }
            }
        }
    }

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public void updateLabel(String s){
        label.setText(s);
    }

    private void alternarOrientacaoNavio() {
        deitado = !deitado;
        if (estado!= GameState.CLIQUE) {
            if (deitado) {
                updateLabel("Posicionar navio horizontalmente.");
            } else {
                updateLabel("Posicionar navio verticalmente.");
            }
        }
    }
}
