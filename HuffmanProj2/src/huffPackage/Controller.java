package huffPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {

	Scanner scan;
File in;
@FXML
TextField suffix;
 Huffman myProcessor;
@FXML
 RadioButton myCountButton;
@FXML
 Text sourceFileTitle;
@FXML
TextArea srcFileLength;
@FXML
TextField srcFilePath;

@FXML
Text destFileTitle;
@FXML
TextArea destFileLength;
@FXML
TextField destFilePath;

@FXML
TextArea huffCodes;
@FXML
TextArea headerInfo;

Huffman processor= new Huffman();


	public void browse() throws FileNotFoundException {
		FileChooser chooser = new FileChooser();
		//chooser.getExtensionFilters().addAll(new ExtensionFilter("txt files", "*.txt"));
		try {	
		in = chooser.showOpenDialog(null);
		System.out.println(in.length());
		srcFilePath.setText(in.getAbsolutePath());


//			scan = new Scanner(in);
		} catch (java.lang.NullPointerException e2) {
			System.out.println("No file Selected");

		}
	}


	@FXML
	void compress() {
		compressIn(in);
	}
	
	void compressIn(File original) {
		if (original == null||original.length()==0) {
			Platform.runLater(() ->
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.setTitle("Error");
				alert.setHeaderText("Failure! file is empty!");
				alert.setContentText("Choose a valid file please.");
				alert.show();
			});
			return;
		}
		if (suffix.getText().length() == 0) {
			suffix.setText(".huff");
		}
		File compressed = new File(original.getPath() + suffix.getText());

		try {
			BitStreamIn in = new BitStreamIn(original);
			BitStreamOut out = new BitStreamOut(compressed);

			try {
				// HuffProcessor processor = getProcessor();
				double start = System.currentTimeMillis();
				// if (myCountButton.isSelected()) {
				// processor.setHeader(HuffProcessor.Header.COUNT_HEADER);
//				}
//				else {
//                       processor.setHeader(HuffProcessor.Header.TREE_HEADER);
//
//				}
				processor.compress(in, out);
				out.flush();
				sourceFileTitle.setText("Source File Information");
				srcFilePath.setText(original.getAbsolutePath());
				srcFileLength.setText("File Size:" + "\n" + original.length() + " bytes" + "\n" + "File Length: "
						+"\n"+ characterCount(original));

				destFileTitle.setText("Compressed File Information");
				destFilePath.setText(compressed.getAbsolutePath());
				double percentSaved = 1.0 - ((double) compressed.length()) / ((double) original.length());
				percentSaved *= 100.0;
				destFileLength.setText("File Size:" + "\n" + compressed.length() + " bytes" + "\n" + "Header Length:"
						+ "\n" + processor.headerLength + "\n" + "Actual Data Length: \n"
						+ (characterCount(compressed) - processor.headerLength) + "\n"
						+ (String.format("Compression Ratio:\n %.2f ", percentSaved) + "%"));


				huffCodes.setText(processor.info);
				headerInfo.setText(processor.s);
				System.out.println(processor.s + "headeeeeeeeeeeeeeeeeer");
			} catch (RunTimeError e) {
				compressed.delete();

			} catch (Exception e) {
				compressed.delete();

				e.printStackTrace();
			}

			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			compressed.delete();
		}

	}

	int characterCount(File file) throws FileNotFoundException {
		
    FileInputStream fileStream = new FileInputStream(file);
    InputStreamReader input = new InputStreamReader(fileStream);
    BufferedReader reader = new BufferedReader(input);
      
    String line;
        	int characterCount = 0;

    try {
		while((line = reader.readLine()) != null)
		{

		        characterCount += line.length();
		
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}return characterCount;
	}
	
	@FXML
	void decompress() {
		decompressIn(in);
	}
	
	
void decompressIn(File original) {
	if (original == null) {
		return;
	}

	String name = original.getPath();
	int ldotIndex = name.lastIndexOf('.');
	int fdotIndex = name.indexOf('.');
	if (ldotIndex != fdotIndex){
	    name = name.substring(0,ldotIndex);
	}
	
	File decompressed = new File(name );//=================================================================

	try {
		BitStreamIn in = new BitStreamIn(original);
		BitStreamOut out = new BitStreamOut(decompressed);

			try {
			//	HuffProcessor processor = getProcessor();
				double start = System.currentTimeMillis();
				processor.decompress(in, out);
				in.close();
                out.close();
				sourceFileTitle.setText("Compressed File Information");
				srcFilePath.setText(original.getAbsolutePath());
				srcFileLength.setText("File Size:"+"\n"+original.length()+" bytes"+"\n"+
						"File Length: \n"+characterCount(original)+"\n");
				
				destFileTitle.setText("decompressed File Information");
				destFilePath.setText(decompressed.getAbsolutePath());
				destFileLength.setText("File Size:"+"\n"+decompressed.length()+" bytes"
						+"\n"+"File Length: \n"+characterCount(decompressed)
						);


				huffCodes.setText(processor.info);
headerInfo.setText(processor.s);

			} catch (RunTimeError e) {
				decompressed.delete();

			} catch (Exception e) {
				e.printStackTrace();
				decompressed.delete();
			}

//			in.close();
//			out.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}



























}