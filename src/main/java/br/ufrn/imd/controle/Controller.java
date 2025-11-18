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
    
    
    private String estado;
    private boolean deitado;
    private List<CellButton> radar;
    private String vencedor;

    // Variables for targeting player's ships
    private List<CellButton> alvosMiradosCorveta;
    private List<CellButton> alvosMiradosSubmarino;
    private List<CellButton> alvosMiradosFragata;
    private List<CellButton> alvosMiradosDestroyer;
    
    // Variables for each ship from the Player
    private Ship corvetaPlayer = new Corvette();
    private Ship submarinoPlayer = new Submarine();
    private Ship fragataPlayer = new Frigate();
    private Ship destroyerPlayer = new Destroyer();
    
    // Variables for each ship from the PC
    private Ship corvetaPC = new Corvette();
    private Ship submarinoPC= new Submarine();
    private Ship fragataPC= new Frigate();
    private Ship destroyerPC= new Destroyer();

    /**
     * Constructor for the Controller class.
     * Initializes game state and other attributes.
     */
    public Controller() {
        game = new Game();
        estado = "clique";
        deitado = true;
        radar = new ArrayList<>();
        alvosMiradosCorveta = new ArrayList<>();
        alvosMiradosSubmarino = new ArrayList<>();
        alvosMiradosFragata = new ArrayList<>();
        alvosMiradosDestroyer = new ArrayList<>();
        vencedor = "ninguem";
        jogador = game.getPlayer1();
        computador = game.getPlayer2();
        jogadorTabuleiro = jogador.getBoard();
        computadorTabuleiro = computador.getBoard();
        jogadorTabuleiroNavios = jogadorTabuleiro.getShips();
        computadorTabuleiroNavios = computadorTabuleiro.getShips();
    }

    /**
     * Initializes the game interface.
     * Sets styles, creates grids, and defines event handlers.
     */
    @FXML
    public void initialize() {
        gamePane.setStyle("-fx-background-color: #B9D9EB;");
        createGrid(playerGrid,"jogador");
        createGrid(computerGrid, "computador");

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
    private void createGrid(GridPane grid, String gridType) {
        Board board1;
        if (gridType.equals("jogador")){
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

	
    private void posicionarNavio(String nome, Ship navio, int tamanho, CellButton celIni, Board board1) {
		List<CellButton> posicoes = new ArrayList<>();
		posicoes.add(celIni);
		boolean sucesso = adicionarPosicoesNavio(celIni, posicoes, tamanho);
		
		try {
			if (sucesso) {
				navio.setPosition(posicoes);
				jogador.placeShip(navio, celIni);
				updateBoard(board1);
			}
		} catch (CelulaInvalidaException e) {
			desfazerNavio1(navio);
			updateBoard(board1);
			updateLabel(e.getMessage());
			System.out.println(e.getMessage());
		}
	}

    private void processarSelecaoAlvos(String nome, List<CellButton> lista, int fileira, int coluna) throws ArrayIndexOutOfBoundsException, CelulaInvalidaException {
		selecionarAlvos(nome, lista, fileira, coluna);
		radar.add(computadorTabuleiro.getCell(fileira, coluna));
		estado = "selecionarAlvos";
	}
    
    /**
     * Handles cell click events during various game states.
     *
     * @param event The mouse click event.
     * @param gridType The type of grid (player or computer).
     * @throws CelulaInvalidaException If an invalid cell is clicked.
     */
    private void handleCellClick(MouseEvent event, String gridType) throws CelulaInvalidaException {

        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode == null) return;

        int coluna = GridPane.getColumnIndex(clickedNode);
        int fileira = GridPane.getRowIndex(clickedNode);

        Board board1 = gridType.equals("jogador") ? jogadorTabuleiro : computadorTabuleiro;
        CellButton celIni = board1.getCell(fileira, coluna);
        
        if(gridType.equals("jogador")) {
        	switch (estado) {
	            case "clique":
	                updateLabel("Célula clicada em: [" + fileira + ", " + coluna + "] no grid: " + gridType);
	                return;
	
	            case "posicionarCorveta":
	                posicionarNavio("Corveta", new Corvette(), 2, celIni, board1);
	                estado = "clique";
	                return;
	
	            case "posicionarSubmarino":
	                posicionarNavio("Submarino", new Submarine(), 3, celIni, board1);
	                estado = "clique";
	                return;
	
	            case "posicionarFragata":
	                posicionarNavio("Fragata", new Frigate(), 4, celIni, board1);
	                estado = "clique";
	                return;
	
	            case "posicionarDestroyer":
	                posicionarNavio("Destroyer", new Destroyer(), 5, celIni, board1);
	                estado = "clique";
	                return;
        	}
        } else { // tabuleiro pc
        	switch (estado) {
	            case "clique":
	                updateLabel("Célula clicada em: [" + fileira + ", " + coluna + "] no grid: " + gridType);
	                return;
	            case "selecionarAlvosCorveta":
	                processarSelecaoAlvos("Corveta", alvosMiradosCorveta, fileira, coluna);
	                return;

	            case "selecionarAlvosSubmarino":
	                processarSelecaoAlvos("Submarino", alvosMiradosSubmarino, fileira, coluna);
	                return;

	            case "selecionarAlvosFragata":
	                processarSelecaoAlvos("Fragata", alvosMiradosFragata, fileira, coluna);
	                return;

	            case "selecionarAlvosDestroyer":
	                processarSelecaoAlvos("Destroyer", alvosMiradosDestroyer, fileira, coluna);
	                return;
	    	}
        }
    }
    
    public void handleButtonCorvette() {
        handleButtonShip("corvette");
    }

    public void handleButtonSubmarine() {
        handleButtonShip("submarine");
    }

    public void handleButtonFrigate() {
        handleButtonShip("frigate");
    }

    public void handleButtonDestroyer() {
        handleButtonShip("destroyer");
    }

    private void handleButtonShip(String shipType) {
        Class<?> shipClass;
        String nome;
        List<?> listaAlvos;

        switch (shipType.toLowerCase()) {
            case "corvette":
                shipClass = Corvette.class;
                nome = "Corveta";
                listaAlvos = alvosMiradosCorveta;
                break;

            case "submarine":
                shipClass = Submarine.class;
                nome = "Submarino";
                listaAlvos = alvosMiradosSubmarino;
                break;

            case "frigate":
                shipClass = Frigate.class;
                nome = "Fragata";
                listaAlvos = alvosMiradosFragata;
                break;

            case "destroyer":
                shipClass = Destroyer.class;
                nome = "Destroyer";
                listaAlvos = alvosMiradosDestroyer;
                break;

            default:
                throw new IllegalArgumentException("Tipo de navio desconhecido: " + shipType);
        }

        boolean isAlive = false;
        boolean encontrado = false;

        // Descobrir se existe esse navio e se está vivo
        for (Ship ship : jogadorTabuleiroNavios) {
            if (shipClass.isInstance(ship)) {
                isAlive = ship.isAlive();
                encontrado = true;
                break;
            }
        }

        // Se o navio não existe no tabuleiro, ele está "excluído"
        if (!encontrado) {
            isAlive = false;
        }

        // POSICIONAR
        if (estado.equals("clique")) {
            boolean posicionado = false;

            for (Ship s : jogadorTabuleiroNavios) {
                if (shipClass.isInstance(s)) {
                    posicionado = true;
                }
            }

            if (!posicionado) {
                updateLabel("Posicione seu " + nome);
                estado = "posicionar" + nome;
            } else {
                updateLabel("Você já posicionou seu " + nome);
            }
            return;
        }

        // MIRAR/ATIRAR
        if (estado.equals("selecionarAlvos")) {
            if (isAlive) {
                if (listaAlvos.isEmpty()) {
                    updateLabel("Selecione os alvos do seu " + nome);
                    estado = "selecionarAlvos" + nome;
                } else {
                    updateLabel("Você já mirou com o seu " + nome);
                }
            } else {
                updateLabel("Seu " + nome + " está afundado!!!");
            }
        }
    }

    

    /**
     * Handles the usage of the Shoot button for firing at the enemy.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    public void handleAtirar() throws InterruptedException {
        if (estado.equals("selecionarAlvos")){
            int naviosVivos = jogadorTabuleiroNavios.size();
            int naviosMirados = 0;

            if (!alvosMiradosCorveta.isEmpty()){naviosMirados++;}
            if (!alvosMiradosSubmarino.isEmpty()){naviosMirados++;}
            if (!alvosMiradosFragata.isEmpty()){naviosMirados++;}
            if (!alvosMiradosDestroyer.isEmpty()){naviosMirados++;}

            if (naviosVivos == naviosMirados){ // Verifica se todos navios vivos miraram

                atiraMirados(computadorTabuleiro);
                updateBoard(computadorTabuleiro);

                verificaRadar();

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

            updateLabel ("SEUS navios vivos: " + jogadorTabuleiro.getShips().size() + "    Navios do PC vivos: " + computadorTabuleiro.getShips().size());

            if (computadorTabuleiro.getShips().size() == 0){
                updateLabel("PC PERDEU");
                System.out.println("PC PERDEU");
                estado = "endGame";
            } else if (jogadorTabuleiro.getShips().size() == 0){
                updateLabel("PLAYER PERDEU");
                System.out.println("PLAYER PERDEU");
                estado = "endGame";
            }
        }
    }

    /**
     * Verifica o radar para detectar navios inimigos em volta das células selecionadas.
     * Atualiza o radar com informações sobre a presença de navios nas linhas ou colunas de ataque.
     *
     * @throws InterruptedException se a operação for interrompida.
     */
    private void verificaRadar() throws InterruptedException {
        int ataque = 1;
        int pegaNgm = 0;
        for (CellButton cell : radar) {
            for (int i = cell.getCol(); i < 10; i++) {
                if (computadorTabuleiro.getCell(cell.getRow(), i).getState() == CellButton.State.SHIP && !computadorTabuleiro.getCell(cell.getRow(), i).isHit()) {
                    updateLabelRadar("Existe um navio na linha de ataque do seu ataque numero " + ataque);
                    pegaNgm++;
                    break;
                }
            }
            for (int i = cell.getCol(); i >= 0; i--) {
                if (computadorTabuleiro.getCell(cell.getRow(), i).getState() == CellButton.State.SHIP && !computadorTabuleiro.getCell(cell.getRow(), i).isHit()) {
                    updateLabelRadar("Existe um navio na linha de ataque do seu ataque numero " + ataque);
                    pegaNgm++;
                    break;
                }
            }
            for (int i = cell.getRow(); i < 10; i++) {
                if (computadorTabuleiro.getCell(i, cell.getCol()).getState() == CellButton.State.SHIP && !computadorTabuleiro.getCell(i, cell.getCol()).isHit()) {
                    updateLabelRadar("Existe um navio na Coluna de ataque do seu ataque numero " + ataque);
                    pegaNgm++;
                    break;
                }
            }
            for (int i = cell.getRow(); i >= 0; i--) {
                if (computadorTabuleiro.getCell(i, cell.getCol()).getState() == CellButton.State.SHIP && !computadorTabuleiro.getCell(i, cell.getCol()).isHit()) {
                    updateLabelRadar("Existe um navio na coluna de ataque do seu ataque numero " + ataque);
                    pegaNgm++;
                    break;
                }
            }
            ataque++;
        }
        if (pegaNgm == 0) {
            updateLabelRadar("Nenhum Navio detectado pelo Radar...");
        }
        radar.clear();
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
                estado = "endGame";
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
        if (jogadorTabuleiroNavios.size() == 4 && !estado.equals("endGame")) {
            if (computadorTabuleiroNavios.size() < 4) {
                posicionaComputador();
            }
            estado = "selecionarAlvos";
            label.setText("É o seu turno, faça seu(s) ataque(s)");
        } else if (estado.equals("endGame")) {
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
    private void selecionarAlvos(String tipoNavio, List<CellButton> alvosTemp, int fileira, int coluna) throws CelulaInvalidaException, ArrayIndexOutOfBoundsException {
        CellButton cell = computadorTabuleiro.getCell(fileira, coluna);
        Ship navio;
        List<CellButton> listAlvos;

        switch (tipoNavio) {
            case "Corveta":
                navio = new Corvette();
                break;
            case "Submarino":
                navio = new Submarine();
                break;
            case "Fragata":
                navio = new Frigate();
                break;
            case "Destroyer":
                navio = new Destroyer();
                break;
            default:
                throw new IllegalArgumentException("Tipo de navio inválido: " + tipoNavio);
        }

        listAlvos = navio.attack(fileira, coluna);

        if (alvosMiradosCorveta.contains(cell) || alvosMiradosSubmarino.contains(cell) ||
                alvosMiradosFragata.contains(cell) || alvosMiradosDestroyer.contains(cell) ||
                computadorTabuleiro.getCell(fileira, coluna).isHit()) {
            throw new CelulaInvalidaException("Você está mirando numa célula inválida");
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
                    if ((coluna + i) > 10) {
                        throw new NavioForaDoMapaException("O navio ficou em parte fora do mapa, posicione-o de novo");
                    }
                    CellButton celulaAdjacente = jogadorTabuleiro.getCell(fileira, coluna + i);
                    posicoesNavio.add(celulaAdjacente);
                }
            } else {
                for (int i = 1; i < tamanho; i++) {
                    if ((fileira + i) > 10) {
                        throw new NavioForaDoMapaException("O navio ficou em parte fora do mapa, posicione-o de novo");
                    }
                    CellButton celulaAdjacente = jogadorTabuleiro.getCell(fileira + i, coluna);
                    posicoesNavio.add(celulaAdjacente);
                }
            }
        } catch (NavioForaDoMapaException e) {
            desfazerNavio(posicoesNavio);
            desfazerPintura(posicoesNavio);
            updateLabel(e.getMessage());
            System.out.println(e.getMessage());
            return false;
        } catch (ArrayIndexOutOfBoundsException e){
            desfazerNavio(posicoesNavio);
            desfazerPintura(posicoesNavio);
            updateLabel(e.getMessage());
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Corrige erros no posicionamento indevido de células num mapa e remove a pintura das células.
     *
     * @param posicoesNavio a lista de células a serem corrigidas e desfeitas
     */
    private void desfazerPintura(List<CellButton> posicoesNavio) {
        for (CellButton cell : posicoesNavio) {
            Node cellNode = cell.getNode();
            cellNode.getStyleClass().remove("cell-ship");
        }
    }

    /**
     * Reseta as células que foram posicionadas corretamente após um erro no posicionamento de um navio.
     *
     * @param posicoesNavio a lista de células do navio a serem resetadas
     */
    private void desfazerNavio(List<CellButton> posicoesNavio) {
        for (CellButton cell : posicoesNavio) {
            cell.reset();
        }
    }

    /**
     * Reseta as células de um navio.
     *
     * @param navio o navio cujas células serão resetadas
     */
    private void desfazerNavio1(Ship navio) {
        for (CellButton cell : navio.getPosition()) {
            cell.reset();
        }
    }

    /**
     * Percorre todas as células do tabuleiro e executa um tiro nas células miradas.
     *
     * @param b o tabuleiro em que os tiros serão executados
     */
    private void atiraMirados(Board b){
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
     * Atualiza o texto da label de radar.
     *
     * @param s o texto a ser definido na label de radar
     */
    public void updateLabelRadar(String s){
        labelRadar.setText(s);
    }

    /**
     * Alterna a orientação do navio entre horizontal e vertical.
     */
    private void alternarOrientacaoNavio() {
        deitado = !deitado;
        if (!estado.equals("clique") && !estado.equals("atacar")) {
            if (deitado) {
                updateLabel("Posicionar navio horizontalmente.");
            } else {
                updateLabel("Posicionar navio verticalmente.");
            }
        }
    }



}
