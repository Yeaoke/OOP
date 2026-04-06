package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class CompanyApp extends Application {

    private ApplicationContext context;

    @Override
    public void init() {
        context = SpringApplication.run(CompanyApp.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                CompanyApp.class.getResource("company-view.fxml")
        );

        fxmlLoader.setControllerFactory(context::getBean);

        BorderPane root = fxmlLoader.load();

        Scene scene = new Scene(root, 900, 650);
        stage.setTitle("Управление промышленными компаниями");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}