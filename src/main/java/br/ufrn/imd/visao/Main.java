package br.ufrn.imd.visao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principal que inicializa a aplicação Batalha Naval.
 */
public class Main extends Application {

    /**
     * Método principal que inicia a aplicação JavaFX.
     *
     * @param stage o palco principal da aplicação onde a cena será exibida.
     * @throws IOException se ocorrer um erro ao carregar o arquivo FXML.
     * @throws IllegalStateException se o arquivo FXML não for encontrado.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Use o caminho correto para o arquivo FXML que está no mesmo pacote
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/gameScene.fxml"));

        // Verifique se o caminho é válido e não é nulo
        if (fxmlLoader.getLocation() == null) {
            throw new IllegalStateException("FXML file not found: " + "/br/ufrn/imd/visao/gameScene.fxml");
        }

        // Carregue o layout a partir do FXML
        AnchorPane root = fxmlLoader.load();

        // Crie a cena com o layout carregado
        Scene scene = new Scene(root, 850, 800);

//        // Adiciona o CSS à cena
//        String css = getClass().getResource("/css/styles.css").toExternalForm();
//        scene.getStylesheets().add(css);

        // Configure o título da janela
        stage.setTitle("Batalha Naval");

        // Defina a cena na janela principal (stage)
        stage.setScene(scene);

        // Mostre a janela
        stage.show();
    }

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args argumentos de linha de comando (não são usados neste contexto).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
