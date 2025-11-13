module br.ufrn.imd.visao {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires commons.math3;

    opens br.ufrn.imd.visao to javafx.fxml;
    exports br.ufrn.imd.visao;
    exports br.ufrn.imd.controle;
    opens br.ufrn.imd.controle to javafx.fxml;
    //exports br.ufrn.imd.dao;
    //exports br.ufrn.imd.controle;
    //exports br.ufrn.imd.modelo;
}