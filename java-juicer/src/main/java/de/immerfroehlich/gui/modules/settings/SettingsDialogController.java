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
	@FXML private ChoiceBox<ObservablePattern> presetChoiceBox;
	@FXML private Button addPresetButton;
	@FXML private Button removePresetButton;
	@FXML private TextField presetNameTextField;
	@FXML private TextField namingTextField;
	@FXML private TextField exampleTextField;
	@FXML private Button updateExampleButton;
	@FXML private Button updatePresetButton;
	@FXML private Button saveButton;
	@FXML private Button cancelButton;
	
	private ObservablePattern selectedPreset;
	
	ConfigurationService configService = new ConfigurationService();
	private Parent settingsDialogView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		archivePathTextField.setText(Configuration.rootPath.get());
		
		presetChoiceBox.setConverter(new ObservablePatternStringConverter());		
		
		presetChoiceBox.getItems().addAll(Configuration.namings); //BUGFIX: ChoiceBox not correctly listening to changes to the underlying list.
	    presetChoiceBox.getSelectionModel().selectedIndexProperty().addListener(this::selectPreset);
		
		addPresetButton.setOnAction(this::addPreset);
		updateExampleButton.setOnAction(this::updateExample);
		updatePresetButton.setOnAction(this::updatePreset);
		saveButton.setOnAction(this::save);
		cancelButton.setOnAction(this::cancel);
	}
	
	private void addPreset(ActionEvent event) {
		ObservablePattern preset = new ObservablePattern(new SimpleStringProperty("Unnamed"), new SimpleStringProperty(""));
		Configuration.namings.add(preset);
		presetChoiceBox.getItems().add(preset); //BUGFIX: ChoiceBox not correctly listening to changes to the underlying list.
		namingTextField.setText(preset.pattern.getValue());
	}
	
	private void selectPreset(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		selectedPreset = Configuration.namings.get(newValue.intValue());
		
		presetNameTextField.setText(selectedPreset.name.getValue());
		namingTextField.setText(selectedPreset.pattern.getValue());
		updateExample(null);
	}

	private void updateExample(ActionEvent event) {
		NamingSchemeExampleUpdater updater = new NamingSchemeExampleUpdater();
		updater.updateExampleTextField(namingTextField.getText(), exampleTextField);
	}
	
	private void updatePreset(ActionEvent event) {
		selectedPreset.name.setValue(presetNameTextField.getText());
		selectedPreset.pattern.setValue(namingTextField.getText());
		try {
			updateExample(null);
		} catch(RuntimeException e) {
			namingTextField.setStyle("-fx-control-inner-background: #BA55D3;");
		}
		presetChoiceBox.getItems().clear();
		presetChoiceBox.getItems().addAll(Configuration.namings);
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
	}

	public void initView(Parent settingsDialogView) {
		this.settingsDialogView = settingsDialogView;
		ApplicationContext.stackPane.getChildren().add(settingsDialogView);
	}
}
