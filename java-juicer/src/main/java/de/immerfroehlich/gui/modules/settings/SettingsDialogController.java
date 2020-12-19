package de.immerfroehlich.gui.modules.settings;

import java.net.URL;
import java.util.ResourceBundle;

import de.immerfroehlich.gui.ApplicationContext;
import de.immerfroehlich.javajuicer.model.Configuration;
import de.immerfroehlich.services.ConfigurationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class SettingsDialogController implements Initializable {
	
	@FXML private TextField archivePathTextField;
	@FXML private ChoiceBox<ObservableNamingScheme> namingSchemeChoiceBox;
	@FXML private Button addNamingSchemeButton;
	@FXML private Button removeNamingSchemeButton;
	@FXML private TextField schemeNameTextField;
	@FXML private TextField schemeTextField;
	@FXML private TextField exampleTextField;
	@FXML private Button updateExampleButton;
	@FXML private Button updateNamingSchemeButton;
	@FXML private Button saveButton;
	@FXML private Button cancelButton;
	
	private ObservableNamingScheme selectedPreset;
	
	ConfigurationService configService = new ConfigurationService();
	private Parent settingsDialogView;
	private Runnable onCloseCallback;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		archivePathTextField.setText(Configuration.rootPath.get());
		
		namingSchemeChoiceBox.setConverter(new ObservableNamingSchemeStringConverter());		
		
		namingSchemeChoiceBox.getItems().addAll(Configuration.namingSchemes); //BUGFIX: ChoiceBox not correctly listening to changes to the underlying list.
	    namingSchemeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(this::selectNamingScheme);
	    namingSchemeChoiceBox.getSelectionModel().selectFirst();
		
		addNamingSchemeButton.setOnAction(this::addNamingScheme);
		removeNamingSchemeButton.setOnAction(this::removeNamingScheme);
		updateExampleButton.setOnAction(this::updateExample);
		updateNamingSchemeButton.setOnAction(this::updateNamingScheme);
		saveButton.setOnAction(this::save);
		cancelButton.setOnAction(this::cancel);
	}
	
	private void addNamingScheme(ActionEvent event) {
		ObservableNamingScheme preset = new ObservableNamingScheme(new SimpleStringProperty("Unnamed"), new SimpleStringProperty("/%a/%l</CD%c>/%n %t"));
		Configuration.namingSchemes.add(preset);
		namingSchemeChoiceBox.getItems().add(preset); //BUGFIX: ChoiceBox not correctly listening to changes to the underlying list.
		schemeTextField.setText(preset.scheme.getValue());
		namingSchemeChoiceBox.getSelectionModel().select(preset);
	}
	
	private void removeNamingScheme(ActionEvent event) {
		Configuration.namingSchemes.remove(selectedPreset);
		namingSchemeChoiceBox.getItems().remove(selectedPreset);
		namingSchemeChoiceBox.getSelectionModel().select(0); 
	}
	
	private void selectNamingScheme(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		selectedPreset = namingSchemeChoiceBox.getItems().get((newValue.intValue())); //TODO leads to an IndexOutOfBoundsException in the underlying List. Bug in JavaFX?
		
		schemeNameTextField.setText(selectedPreset.name.getValue());
		schemeTextField.setText(selectedPreset.scheme.getValue());
		updateExample(null);
	}

	private void updateExample(ActionEvent event) {
		NamingSchemeExampleUpdater updater = new NamingSchemeExampleUpdater();
		updater.updateExampleTextField(schemeTextField.getText(), exampleTextField);
	}
	
	private void updateNamingScheme(ActionEvent event) {
		selectedPreset.name.setValue(schemeNameTextField.getText());
		selectedPreset.scheme.setValue(schemeTextField.getText());
		try {
			updateExample(null);
		} catch(RuntimeException e) {
			schemeTextField.setStyle("-fx-control-inner-background: #BA55D3;");
		}
		namingSchemeChoiceBox.getItems().clear();
		namingSchemeChoiceBox.getItems().addAll(Configuration.namingSchemes);
	}
	
	private void save(ActionEvent event) {
		Configuration.rootPath.set(archivePathTextField.getText());
		configService.saveConfig();
		restoreMainView();
	}
	
	private void cancel(ActionEvent event) {
		restoreMainView();
	}
	
	private void restoreMainView() {
		ApplicationContext.stackPane.getChildren().remove(settingsDialogView);
		onCloseCallback.run();
	}

	public void initView(Parent settingsDialogView, Runnable onCloseCallback) {
		this.settingsDialogView = settingsDialogView;
		this.onCloseCallback = onCloseCallback;
		ApplicationContext.stackPane.getChildren().add(settingsDialogView);
	}
}
