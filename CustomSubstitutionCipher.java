import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class CustomSubstitutionCipher extends Application {
    
    private Map<Character, String> letterToNumber = new HashMap<>();
    private Map<String, Character> numberToLetter = new HashMap<>();
    private TextArea inputArea;
    private TextArea outputArea;
    private GridPane mappingGrid;
    private TextField[] numberFields = new TextField[26];
    private ToggleGroup modeGroup;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Custom Substitution Cipher");
        
        // Initialize with default mapping (A=1, B=2, etc.)
        initializeDefaultMapping();
        
        // Main layout with tabs
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #1a1a2e;");
        
        // Tab 1: Cipher Tool
        Tab cipherTab = new Tab("Cipher Tool");
        cipherTab.setClosable(false);
        cipherTab.setContent(createCipherPane());
        
        // Tab 2: Mapping Editor
        Tab mappingTab = new Tab("Edit Mapping");
        mappingTab.setClosable(false);
        mappingTab.setContent(createMappingPane());
        
        tabPane.getTabs().addAll(cipherTab, mappingTab);
        
        Scene scene = new Scene(tabPane, 700, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createCipherPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: #16213e;");
        
        // Title
        Label title = new Label("🔢 Letter ↔ Number Cipher");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e94560;");
        
        // Mode selection
        HBox modeBox = new HBox(15);
        modeBox.setAlignment(Pos.CENTER);
        Label modeLabel = new Label("Mode:");
        modeLabel.setStyle("-fx-text-fill: #f1f1f1; -fx-font-size: 14px;");
        
        modeGroup = new ToggleGroup();
        RadioButton encryptRadio = new RadioButton("Encrypt (Letters → Numbers)");
        RadioButton decryptRadio = new RadioButton("Decrypt (Numbers → Letters)");
        encryptRadio.setToggleGroup(modeGroup);
        decryptRadio.setToggleGroup(modeGroup);
        encryptRadio.setSelected(true);
        encryptRadio.setStyle("-fx-text-fill: #f1f1f1;");
        decryptRadio.setStyle("-fx-text-fill: #f1f1f1;");
        
        encryptRadio.setOnAction(e -> processText());
        decryptRadio.setOnAction(e -> processText());
        
        modeBox.getChildren().addAll(modeLabel, encryptRadio, decryptRadio);
        
        // Input area
        Label inputLabel = new Label("Input:");
        inputLabel.setStyle("-fx-text-fill: #f1f1f1; -fx-font-size: 14px;");
        
        inputArea = new TextArea();
        inputArea.setPromptText("Enter text here...");
        inputArea.setPrefHeight(200);
        inputArea.setWrapText(true);
        inputArea.setStyle("-fx-control-inner-background: #0f3460; -fx-text-fill: #f1f1f1; -fx-font-size: 14px; -fx-font-family: 'Courier New';");
        inputArea.textProperty().addListener((obs, oldVal, newVal) -> processText());
        
        // Output area
        Label outputLabel = new Label("Output:");
        outputLabel.setStyle("-fx-text-fill: #f1f1f1; -fx-font-size: 14px;");
        
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-control-inner-background: #533483; -fx-text-fill: #f1f1f1; -fx-font-size: 14px; -fx-font-family: 'Courier New';");
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button copyButton = new Button("📋 Copy Output");
        copyButton.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 20;");
        copyButton.setOnAction(e -> copyToClipboard());
        
        Button clearButton = new Button("Clear All");
        clearButton.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 20;");
        clearButton.setOnAction(e -> {
            inputArea.clear();
            outputArea.clear();
        });
        
        buttonBox.getChildren().addAll(copyButton, clearButton);
        
        pane.getChildren().addAll(title, new Separator(), modeBox, inputLabel, inputArea, outputLabel, outputArea, buttonBox);
        return pane;
    }
    
    private ScrollPane createMappingPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: #16213e;");
        
        Label title = new Label("⚙️ Customize Letter → Number Mapping");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e94560;");
        
        Label instruction = new Label("Enter a 1-3 digit number for each letter (must be unique):");
        instruction.setStyle("-fx-text-fill: #f1f1f1; -fx-font-size: 13px;");
        
        // Grid for letter mappings
        mappingGrid = new GridPane();
        mappingGrid.setHgap(10);
        mappingGrid.setVgap(10);
        mappingGrid.setAlignment(Pos.CENTER);
        
        // Create input fields for each letter
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            
            Label letterLabel = new Label(letter + ":");
            letterLabel.setStyle("-fx-text-fill: #f1f1f1; -fx-font-size: 14px; -fx-font-weight: bold;");
            
            TextField numberField = new TextField(letterToNumber.get(letter));
            numberField.setPrefWidth(60);
            numberField.setStyle("-fx-font-size: 13px;");
            numberField.setPromptText("1-999");
            
            // Validate input
            final int index = i;
            numberField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d{0,3}")) {
                    numberField.setText(oldVal);
                }
            });
            
            numberFields[i] = numberField;
            
            int row = i / 6;
            int col = (i % 6) * 2;
            
            mappingGrid.add(letterLabel, col, row);
            mappingGrid.add(numberField, col + 1, row);
        }
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button applyButton = new Button("✓ Apply Mapping");
        applyButton.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        applyButton.setOnAction(e -> applyMapping());
        
        Button resetButton = new Button("Reset to Default");
        resetButton.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        resetButton.setOnAction(e -> {
            initializeDefaultMapping();
            updateMappingFields();
        });
        
        Button randomButton = new Button("🎲 Random Mapping");
        randomButton.setStyle("-fx-background-color: #533483; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        randomButton.setOnAction(e -> generateRandomMapping());
        
        buttonBox.getChildren().addAll(applyButton, resetButton, randomButton);
        
        pane.getChildren().addAll(title, instruction, new Separator(), mappingGrid, buttonBox);
        
        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #16213e;");
        return scrollPane;
    }
    
    private void initializeDefaultMapping() {
        letterToNumber.clear();
        numberToLetter.clear();
        
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            String number = String.valueOf(i + 1);
            letterToNumber.put(letter, number);
            numberToLetter.put(number, letter);
        }
    }
    
    private void updateMappingFields() {
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            numberFields[i].setText(letterToNumber.get(letter));
        }
    }
    
    private void applyMapping() {
        Map<Character, String> newLetterToNumber = new HashMap<>();
        Map<String, Character> newNumberToLetter = new HashMap<>();
        Set<String> usedNumbers = new HashSet<>();
        
        // Validate all inputs
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            String number = numberFields[i].getText().trim();
            
            if (number.isEmpty()) {
                showAlert("Error", "Please provide a number for letter " + letter);
                return;
            }
            
            if (usedNumbers.contains(number)) {
                showAlert("Error", "Number " + number + " is used more than once!");
                return;
            }
            
            usedNumbers.add(number);
            newLetterToNumber.put(letter, number);
            newNumberToLetter.put(number, letter);
        }
        
        // Apply new mapping
        letterToNumber = newLetterToNumber;
        numberToLetter = newNumberToLetter;
        
        showAlert("Success", "Mapping applied successfully!");
        processText(); // Re-process current text
    }
    
    private void generateRandomMapping() {
        List<Integer> numbers = new ArrayList<>();
        Random rand = new Random();
        
        // Generate 26 unique random numbers (1-999)
        while (numbers.size() < 26) {
            int num = rand.nextInt(999) + 1;
            if (!numbers.contains(num)) {
                numbers.add(num);
            }
        }
        
        letterToNumber.clear();
        numberToLetter.clear();
        
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            String number = String.valueOf(numbers.get(i));
            letterToNumber.put(letter, number);
            numberToLetter.put(number, letter);
            numberFields[i].setText(number);
        }
        
        showAlert("Success", "Random mapping generated!");
    }
    
    private void processText() {
        String input = inputArea.getText();
        RadioButton selected = (RadioButton) modeGroup.getSelectedToggle();
        boolean encrypt = selected.getText().contains("Encrypt");
        
        String result = encrypt ? encrypt(input) : decrypt(input);
        outputArea.setText(result);
    }
    
    private String encrypt(String text) {
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char upper = Character.toUpperCase(c);
                result.append(letterToNumber.get(upper));
                result.append("-");
            } else if (c == ' ') {
                result.append(" ");
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private String decrypt(String text) {
        StringBuilder result = new StringBuilder();
        String[] parts = text.split(" ");
        
        for (String part : parts) {
            if (part.isEmpty()) {
                result.append(" ");
                continue;
            }
            
            String[] numbers = part.split("-");
            for (String num : numbers) {
                if (!num.isEmpty() && numberToLetter.containsKey(num)) {
                    result.append(numberToLetter.get(num));
                } else if (!num.isEmpty()) {
                    result.append("?");
                }
            }
            result.append(" ");
        }
        
        return result.toString().trim();
    }
    
    private void copyToClipboard() {
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(outputArea.getText());
        clipboard.setContent(content);
        showAlert("Copied", "Output copied to clipboard!");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}