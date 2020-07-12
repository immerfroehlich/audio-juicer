package de.immerfroehlich.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import de.immerfroehlich.gui.ApplicationContext;
import de.immerfroehlich.javajuicer.model.Album;
import de.immerfroehlich.javajuicer.model.Configuration;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.services.ConfigurationService;
import de.immerfroehlich.services.parser.FileNamingConfigParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SettingsDialogController implements Initializable {
	
	@FXML private TextField archivePathTextField;
	@FXML private TextField namingTextField;
	@FXML private TextField exampleTextField;
	@FXML private Button updateExampleButton;
	@FXML private Button saveButton;
	@FXML private Button cancelButton;
	
	ConfigurationService configService = new ConfigurationService();
	private Parent settingsDialogView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		archivePathTextField.setText(Configuration.rootPath.get());
		namingTextField.setText(Configuration.naming.get());
		updateExampleButton.setOnAction(this::updateExample);
		saveButton.setOnAction(this::save);
		cancelButton.setOnAction(this::cancel);
	}
	
	private void updateExample(ActionEvent event) {
		FileNamingConfigParser parser = new FileNamingConfigParser();
		
		Album albumInfo = new Album();
		albumInfo.artist = "AlbumArtist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = true;
		albumInfo.cdNumber = "2";
		
		String directories = parser.parse(namingTextField.getText(), albumInfo);
		
		Mp3Track track = new Mp3Track();
		track.title = "TrackTitle";
		track.artist = "TrackArtist";
		String trackNumber = "11";
		String fileName = parser.parseFileName(track, trackNumber);
		
		String text = directories + "/" + fileName;
		exampleTextField.setText(text);
	}
	
	private void save(ActionEvent event) {
		Configuration.rootPath.set(archivePathTextField.getText());
		Configuration.naming.set(namingTextField.getText());
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
