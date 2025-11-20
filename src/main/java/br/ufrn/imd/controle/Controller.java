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

/**
 * Controller class for managing the game interface and interactions.
 * Handles initialization, grid creation, and game event handling.
 */
public class Controller {

    @FXML
    private AnchorPane gamePane;
    @FXML
    private GridPane playerGrid;
    @FXML
    private GridPane computerGrid;
    @FXML
    private Button startGameButton;
    @FXML
    private Label label;
    @FXML
    private Label labelRadar;

    private Game game;
    private Player jogador;
    private Player computador;
    private Board jogadorTabuleiro;
    private Board computadorTabuleiro;
    private List<Ship> jogadorTabuleiroNavios;
    private List<Ship> computadorTabuleiroNavios;
    
    
    private GameState estado;
    private boolean deitado;
    private List<CellButton> radar;

    // Variables for targeting player's ships
    private List<CellButton> alvosMiradosCorveta;
    private List<CellButton> alvosMiradosSubmarino;
    private List<CellButton> alvosMiradosFragata;
    private List<CellButton> alvosMiradosDestroyer;
    
 // maps de apoio para unificar lógica por tipo de navio
    private Map<ShipType, Supplier<Ship>> shipFactory;
    private Map<ShipType, List<CellButton>> alvosPorNavio;

    /**
     * Constructor for the Controller class.
     * Initializes game state and other attributes.
     */
    public Controller() {
        game = new Game();
        estado = GameState.CLIQUE;
        deitado = true;
        radar = new ArrayList<>();
        alvosMiradosCorveta = new ArrayList<>();
        alvosMiradosSubmarino = new ArrayList<>();
        alvosMiradosFragata = new ArrayList<>();
        alvosMiradosDestroyer = new ArrayList<>();
        jogador = game.getPlayer1();
        computador = game.getPlayer2();
        jogadorTabuleiro = jogador.getBoard();
        computadorTabuleiro = computador.getBoard();
        jogadorTabuleiroNavios = jogadorTabuleiro.getShips();
        computadorTabuleiroNavios = computadorTabuleiro.getShips();
        
        // inicializa fábricas e mapa de listas de alvos
        shipFactory = new HashMap<>();
        shipFactory.put(ShipType.CORVETA, Corvette::new);
        shipFactory.put(ShipType.SUBMARINO, Submarine::new);
        shipFactory.put(ShipType.FRAGATA, Frigate::new);
        shipFactory.put(ShipType.DESTROYER, Destroyer::new);

        // mapear nomes com as listas já existentes (mantendo as listas individuais por enquanto)
        alvosPorNavio = new HashMap<>();
        alvosPorNavio.put(ShipType.CORVETA, new ArrayList<>());
        alvosPorNavio.put(ShipType.SUBMARINO, new ArrayList<>());
        alvosPorNavio.put(ShipType.FRAGATA, new ArrayList<>());
        alvosPorNavio.put(ShipType.DESTROYER, new ArrayList<>());

    }

    /**
     * Initializes the game interface.
     * Sets styles, creates grids, and defines event handlers.
     */
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

        // Handles right-click event on the gamePane
        gamePane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                alternarOrientacaoNavio();
            }
        });
    }

    /**
     * Creates a grid and initializes its nodes and cells.
     * Sets up event handlers for cell clicks.
     *
     * @param grid The GridPane to be created.
     * @param gridType The type of grid (player or computer).
     */
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
        int size = type.getSize();

        List<CellButton> posicoes = new ArrayList<>();
        posicoes.add(celIni);

        boolean sucesso = adicionarPosicoesNavio(celIni, posicoes, size);

        try {
            if (sucesso) {
                navio.setPosition(posicoes);
                jogador.placeShip(navio, celIni);
                updateBoard(board);
            }
        } catch (CelulaInvalidaException e) {
            desfazerNavio(posicoes);
            updateBoard(board);
            updateLabel(e.getMessage());
        }
    }


    private void processarSelecaoAlvos(ShipType tipoNavio, List<CellButton> lista, int fileira, int coluna) throws ArrayIndexOutOfBoundsException, CelulaInvalidaException {
		selecionarAlvos(tipoNavio, lista, fileira, coluna);
		radar.add(computadorTabuleiro.getCell(fileira, coluna));
		estado = GameState.SELECIONAR_ALVOS;
	}
    
    /**
     * Handles cell click events during various game states.
     *
     * @param event The mouse click event.
     * @param gridType The type of grid (player or computer).
     * @throws CelulaInvalidaException If an invalid cell is clicked.
     */
    private void handleCellClick(MouseEvent event, PlayerName gridType) throws CelulaInvalidaException {

        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode == null) return;

        int coluna = GridPane.getColumnIndex(clickedNode);
        int fileira = GridPane.getRowIndex(clickedNode);

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
	            processarSelecaoAlvos(ShipType.CORVETA, alvosMiradosCorveta, fileira, coluna);
	            return;
	
	        case SELECIONAR_ALVOS_SUBMARINO:
	            processarSelecaoAlvos(ShipType.SUBMARINO, alvosMiradosSubmarino, fileira, coluna);
	            return;
	
	        case SELECIONAR_ALVOS_FRAGATA:
	            processarSelecaoAlvos(ShipType.FRAGATA, alvosMiradosFragata, fileira, coluna);
	            return;
	
	        case SELECIONAR_ALVOS_DESTROYER:
	            processarSelecaoAlvos(ShipType.DESTROYER, alvosMiradosDestroyer, fileira, coluna);
	            return;
	
	        case SELECIONAR_ALVOS:
	            // comportamento genérico (se quiser manter)
	            return;
	
	        case ENDGAME:
	            return;
	    }
    }

    
    public void handleButtonCorvette() {
        handleButtonShip(ShipType.CORVETA);
    }

    public void handleButtonSubmarine() {
        handleButtonShip(ShipType.SUBMARINO);
    }

    public void handleButtonFrigate() {
        handleButtonShip(ShipType.FRAGATA);
    }

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

        // Estados organizados para JML
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
			break;
        }
    }

    /**
     * Handles the usage of the Shoot button for firing at the enemy.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    public void handleButtonAtirar() throws InterruptedException {
        if (estado == GameState.SELECIONAR_ALVOS){
            int naviosVivos = jogadorTabuleiroNavios.size();
            int naviosMirados = 0;

            if (!alvosMiradosCorveta.isEmpty()){naviosMirados++;}
            if (!alvosMiradosSubmarino.isEmpty()){naviosMirados++;}
            if (!alvosMiradosFragata.isEmpty()){naviosMirados++;}
            if (!alvosMiradosDestroyer.isEmpty()){naviosMirados++;}

            if (naviosVivos == naviosMirados){ // Verifica se todos navios vivos miraram

                atiraCelulasMiradas(computadorTabuleiro);
                updateBoard(computadorTabuleiro);

                //verificaRadar(); excluído

                updateLabel("Você atirou no campo inimigo");

                alvosMiradosCorveta.clear();
                alvosMiradosSubmarino.clear();
                alvosMiradosFragata.clear();
                alvosMiradosDestroyer.clear();

                // lógica para o COMPUTADOR ATIRAR
                computadorTabuleiro.attListaNavios();
                ataquePc();

            } else {
                updateLabel("Você ainda não mirou com algum navio");
            }

            jogadorTabuleiro.attListaNavios();
            computadorTabuleiro.attListaNavios();

            updateLabel("SEUS navios vivos: " + jogadorTabuleiro.getShips().size() + "    Navios vivos do PC: " + computadorTabuleiro.getShips().size());

            if (computadorTabuleiro.getShips().size() == 0){
                updateLabel("PC PERDEU");
                System.out.println("PC PERDEU");
                estado = GameState.ENDGAME;
            } else if (jogadorTabuleiro.getShips().size() == 0){
                updateLabel("PLAYER PERDEU");
                System.out.println("PLAYER PERDEU");
                estado = GameState.ENDGAME;
            }
        } // só faz algo se o estado é selecionar alvos
    }


    /**
     * Executa um ataque do computador, selecionando alvos aleatórios para atacar.
     * Atualiza o estado do tabuleiro e verifica se todos os navios do jogador foram destruídos.
     */
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
                    jogadorTabuleiro.buscarCellNavio(c.getRow(), c.getCol());
                }
            }
            jogadorTabuleiro.attListaNavios();
            updateBoard(jogadorTabuleiro);
            if (jogadorTabuleiro.getShips().size() == 0) {
                updateLabel("PLAYER PERDEU");
                System.out.println("PLAYER PERDEU");
                estado = GameState.ENDGAME;
            }
        }
    }

    /**
     * Gerencia o botão "Começar Jogo", verificando se o jogador posicionou todos os navios.
     * Se todos os navios estiverem posicionados, posiciona os navios do computador e muda o estado do jogo.
     *
     * @throws CelulaInvalidaException se houver uma tentativa de posicionamento inválido de navios.
     */
    private void handleStartGame() throws CelulaInvalidaException {
        if (jogadorTabuleiroNavios.size() == 4 && estado != GameState.ENDGAME) {
            if (computadorTabuleiroNavios.size() < 4) {
                posicionaComputador();
            }
            estado = GameState.SELECIONAR_ALVOS;
            label.setText("É o seu turno, faça seu(s) ataque(s)");
        } else if (estado == GameState.ENDGAME) {
            // Nada a fazer se o jogo já terminou
        } else {
            label.setText("Voce ainda nao posicionou todos os navios!!!");
        }
    }

    /**
     * Posiciona os navios do computador no tabuleiro.
     *
     * @throws CelulaInvalidaException se houver uma tentativa de posicionamento inválido de navios.
     */
    private void posicionaComputador() throws CelulaInvalidaException {
        for (int i = 2; i < 6; i++) {
            posicionaNaviosPc(i);
        }
    }

    /**
     * Posiciona um navio específico do computador no tabuleiro.
     *
     * @param tamanho o tamanho do navio a ser posicionado.
     * @throws CelulaInvalidaException se houver uma tentativa de posicionamento inválido de navios.
     */
    private void posicionaNaviosPc(int tamanho) throws CelulaInvalidaException {
        RandomDataGenerator randomData = new RandomDataGenerator();
        int virado = randomData.nextInt(0, 1);

        boolean sucessoPosicao;
        List<CellButton> posicoesNavio = new ArrayList<>();
        sucessoPosicao = adicionarPosicoesNavioPc(posicoesNavio, tamanho, virado);
        try {
            if (sucessoPosicao) {
                switch (tamanho) {
                    case 2:
                        Ship ship1 = new Corvette(posicoesNavio);
                        computadorTabuleiro.placeShip(ship1, posicoesNavio.get(0));
                        updateBoard(computadorTabuleiro);
                        break;
                    case 3:
                        Ship ship2 = new Submarine(posicoesNavio);
                        computadorTabuleiro.placeShip(ship2, posicoesNavio.get(0));
                        updateBoard(computadorTabuleiro);
                        break;
                    case 4:
                        Ship ship3 = new Frigate(posicoesNavio);
                        computadorTabuleiro.placeShip(ship3, posicoesNavio.get(0));
                        updateBoard(computadorTabuleiro);
                        break;
                    case 5:
                        Ship ship4 = new Destroyer(posicoesNavio);
                        computadorTabuleiro.placeShip(ship4, posicoesNavio.get(0));
                        updateBoard(computadorTabuleiro);
                        break;
                    default:
                        throw new IllegalArgumentException("Tamanho de navio inválido: " + tamanho);
                }
            }
        } catch (CelulaInvalidaException e) {
            updateLabel(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    /**
     * Seleciona e demarca no mapa os alvos mirados de um navio específico, procedimento necessário antes de atirar.
     *
     * @param tipoNavio o tipo de navio.
     * @param alvosTemp a lista de alvos temporários.
     * @param fileira a fileira inicial para selecionar os alvos.
     * @param coluna a coluna inicial para selecionar os alvos.
     * @throws CelulaInvalidaException se houver uma tentativa de selecionar uma célula inválida.
     * @throws ArrayIndexOutOfBoundsException se houver uma tentativa de acessar uma célula fora do tabuleiro.
     */
    private void selecionarAlvos(ShipType tipoNavio, List<CellButton> alvosTemp, int fileira, int coluna) throws CelulaInvalidaException, ArrayIndexOutOfBoundsException {
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
                if (celula.getCol() < 10 && celula.getCol() >= 0 && celula.getRow() < 10 && celula.getRow() >= 0) {
                    computadorTabuleiro.getCell(celula.getRow(), celula.getCol()).setAimed(true);
                }
            }
            updateBoard(computadorTabuleiro);
        }
    }

    /**
     * Adiciona posições de navio para o computador, verificando a validade das posições.
     *
     * @param posicoesNavio a lista de posições do navio.
     * @param tamanho o tamanho do navio a ser posicionado.
     * @param virado a orientação do navio (0 para horizontal, 1 para vertical).
     * @return true se as posições forem válidas e o navio puder ser posicionado, false caso contrário.
     */
    private boolean adicionarPosicoesNavioPc(List<CellButton> posicoesNavio, int tamanho, int virado) {
        Random random = new Random();

        while (true) {
            int fileira = random.nextInt(10);
            int coluna = random.nextInt(10);

            boolean posicaoValida = true;
            posicoesNavio.clear();

            if (virado == 0) {
                for (int i = 1; i <= tamanho; i++) {
                    if ((coluna + i) >= 10 || computadorTabuleiro.getCell(fileira, coluna + i).getState() == CellButton.State.SHIP) {
                        posicaoValida = false;
                        break;
                    } else {
                        CellButton celulaAdjacente = computadorTabuleiro.getCell(fileira, coluna + i);
                        posicoesNavio.add(celulaAdjacente);
                    }
                }
            } else {
                for (int i = 1; i <= tamanho; i++) {
                    if ((fileira + i) >= 10 || computadorTabuleiro.getCell(fileira + i, coluna).getState() == CellButton.State.SHIP) {
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

    /**
     * Adiciona coordenadas de células necessárias para definir (inicializar) um navio específico.
     *
     * @param celIni a célula inicial do navio
     * @param posicoesNavio a lista de células que compõem o navio
     * @param tamanho o tamanho do navio
     * @return true se as posições do navio foram adicionadas com sucesso, caso contrário false
     */
    private boolean adicionarPosicoesNavio(CellButton celIni, List<CellButton> posicoesNavio, int tamanho) {
        int fileira = celIni.getRow();
        int coluna = celIni.getCol();

        try {
            if (deitado) {
                for (int i = 1; i < tamanho; i++) {
                    if ((coluna + i) > 10) {throw new NavioForaDoMapaException("O navio ficou em parte fora do mapa, posicione-o de novo");}
                    posicoesNavio.add(jogadorTabuleiro.getCell(fileira, coluna + i));
                }
            } else {
                for (int i = 1; i < tamanho; i++) {
                    if ((fileira + i) > 10) {throw new NavioForaDoMapaException("O navio ficou em parte fora do mapa, posicione-o de novo");}
                    posicoesNavio.add(jogadorTabuleiro.getCell(fileira + i, coluna));
                }
            }
        } catch (NavioForaDoMapaException e) {
            desfazerNavio(posicoesNavio);
            //desfazerPintura(posicoesNavio);
            updateBoard(jogadorTabuleiro);
            updateLabel(e.getMessage());
            System.out.println(e.getMessage());
            return false;
        } catch (ArrayIndexOutOfBoundsException e){
            desfazerNavio(posicoesNavio);
            //desfazerPintura(posicoesNavio);
            updateBoard(jogadorTabuleiro);
            updateLabel(e.getMessage());
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Reseta as células que foram posicionadas corretamente após um erro no posicionamento de um navio.
     *
     * @param posicoesNavio a lista de células do navio a serem resetadas
     */
    private void desfazerNavio(List<CellButton> posicoesNavio) {
        for (CellButton cell : posicoesNavio) {
            cell.undoShipPositioning();
        }
    }
    
    /**
     * Percorre todas as células do tabuleiro e executa um tiro nas células miradas.
     *
     * @param b o tabuleiro em que os tiros serão executados
     */
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

        for (Ship ship : b.getShips()) {
            if (!ship.isAlive()) {
                b.setNumShips(b.getNumShips() -1);
            }
        }

    }

    /**
     * Atualiza o estado visual do tabuleiro.
     *
     * @param b o tabuleiro a ser atualizado
     */
    private void updateBoard(Board b) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                CellButton cell = b.getCell(row, col);
                Node cellNode = cell.getNode();

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

    /**
     * Atualiza o texto da label principal de comunicação com o jogador.
     *
     * @param s o texto a ser definido na label
     */
    public void updateLabel(String s){
        label.setText(s);
    }

    /**
     * Alterna a orientação do navio entre horizontal e vertical.
     */
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
