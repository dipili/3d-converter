package com.github.diplombmstu.converter3d;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

public class MainWindowController
{
    private final static Logger LOGGER = Logger.getLogger(MainWindowController.class.getName());

    @FXML
    public TextField inputImageTextField;
    @FXML
    public TextField depthMapTextField;
    @FXML
    public TextField leftImageTextField;
    @FXML
    public TextField rightImageTextField;

    private Stage stage;

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public void convert(MouseEvent mouseEvent)
    {
        Path inputPath = Paths.get(inputImageTextField.getText());
        Path depthMap = Paths.get(depthMapTextField.getText());

        Path leftEye = Paths.get(leftImageTextField.getText().endsWith(".png") ?
                                 leftImageTextField.getText() :
                                 String.format("%s.png", leftImageTextField.getText()));

        Path rightEye = Paths.get(rightImageTextField.getText().endsWith(".png") ?
                                  rightImageTextField.getText() :
                                  String.format("%s.png", rightImageTextField.getText()));

        File leftEyeFile = leftEye.toFile();
        File rightEyeFile = rightEye.toFile();
        if ((leftEyeFile.exists() || rightEyeFile.exists()) && !confirmRewriting())
            return;
        else
        {
            if (leftEyeFile.exists())
                leftEyeFile.delete();

            if (rightEyeFile.exists())
                rightEyeFile.delete();
        }

        try
        {
            Converter3d converter = new Converter3d();
            converter.convert(inputPath, depthMap, leftEye, rightEye);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());

            showError(e.getMessage());

            return;
        }

        LOGGER.info("Conversion has been completed.");
    }

    private void showError(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void clear(MouseEvent mouseEvent)
    {
        inputImageTextField.clear();
        depthMapTextField.clear();
        leftImageTextField.clear();
        rightImageTextField.clear();
    }

    public void selectImage(MouseEvent mouseEvent)
    {
        File file = getFile("Выбирите изображение для конвертации");
        if (file == null)
            return;

        inputImageTextField.setText(file.getAbsolutePath());
    }

    public void selectDepthMap(MouseEvent mouseEvent)
    {
        File file = getFile("Выбирите карту глубины");
        if (file == null)
            return;

        depthMapTextField.setText(file.getAbsolutePath());
    }

    private boolean confirmRewriting()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Вы уверены, что хотите перезаписать существующие файлы?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    private File getFile(String title)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File("."));
        return fileChooser.showOpenDialog(stage);
    }
}
