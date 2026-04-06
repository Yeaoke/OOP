package org.example;

import org.example.repository.crudRealization;
import org.example.repository.holdingRealization;
import org.example.model.CoalCompany;
import org.example.model.IndustrialCompanies;
import org.example.model.OilCompany;
import org.example.model.HoldingCompanies;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

@Component
public class CompanyController implements Initializable {

    @Autowired
    private crudRealization companyCRUD;

    @Autowired
    private holdingRealization holdingRepository;

    @FXML private TableView<DisplayItem> companiesTable;
    @FXML private TableColumn<DisplayItem, Long> idColumn;
    @FXML private TableColumn<DisplayItem, String> nameColumn;
    @FXML private TableColumn<DisplayItem, String> typeColumn;
    @FXML private TableColumn<DisplayItem, Long> turnoverColumn;
    @FXML private TableColumn<DisplayItem, String> holdingColumn;
    @FXML private TableColumn<DisplayItem, String> detailsColumn;

    @FXML private TextField nameField;
    @FXML private TextField turnoverField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField coalVolumeField;
    @FXML private TextField minesField;
    @FXML private TextField oilVolumeField;
    @FXML private TextField wellsField;
    @FXML private ComboBox<String> assignHoldingComboBox;

    @FXML private Label statusLabel;
    @FXML private TextField addMinesField;
    @FXML private TextField addWellsField;

    @FXML private TextField newHoldingNameField;
    @FXML private TextField searchHoldingField;
    @FXML private ListView<String> holdingCompaniesList;

    private final ObservableList<DisplayItem> companiesData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCompaniesTable();
        setupTypeComboBox();
        loadCompanies();
        setupSelectionListener();
        updateHoldingComboBoxes();
    }

    private void setupCompaniesTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        turnoverColumn.setCellValueFactory(new PropertyValueFactory<>("turnover"));
        holdingColumn.setCellValueFactory(new PropertyValueFactory<>("holdingName"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        companiesTable.setItems(companiesData);
    }

    private void setupTypeComboBox() {
        typeComboBox.getItems().addAll("CoalCompany", "OilCompany");
        typeComboBox.setValue("CoalCompany");
        updateVisibleFields();
        typeComboBox.setOnAction(e -> updateVisibleFields());
    }

    private void setupSelectionListener() {
        companiesTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        statusLabel.setText("Выбрана: " + newSel.getName());
                    }
                });
    }

    private void updateVisibleFields() {
        boolean isCoal = "CoalCompany".equals(typeComboBox.getValue());

        coalVolumeField.setVisible(isCoal); coalVolumeField.setManaged(isCoal);
        minesField.setVisible(isCoal); minesField.setManaged(isCoal);
        addMinesField.setVisible(isCoal); addMinesField.setManaged(isCoal);

        oilVolumeField.setVisible(!isCoal); oilVolumeField.setManaged(!isCoal);
        wellsField.setVisible(!isCoal); wellsField.setManaged(!isCoal);
        addWellsField.setVisible(!isCoal); addWellsField.setManaged(!isCoal);
    }

    private void updateHoldingComboBoxes() {
        assignHoldingComboBox.getItems().clear();
        assignHoldingComboBox.getItems().add("Без холдинга");

        for (HoldingCompanies h : holdingRepository.findAll()) {
            assignHoldingComboBox.getItems().add(h.getHoldingName());
        }
    }

    private void loadCompanies() {
        companiesData.clear();
        for (IndustrialCompanies company : companyCRUD.findAll()) {
            String details = "";
            if (company instanceof CoalCompany coal) {
                details = "Угля: " + coal.getCoalVolume() + ", Шахт: " + coal.getMineCount();
                if (coal.getCoalAction() != null) details += " | " + coal.getCoalAction();
            } else if (company instanceof OilCompany oil) {
                details = "Нефти: " + oil.getOilVolume() + ", Скважин: " + oil.getHoleCount();
                if (oil.getOilAction() != null) details += " | " + oil.getOilAction();
            }

            String holdingName = (company.getHoldingCompany() != null)
                    ? company.getHoldingCompany().getHoldingName()
                    : "—";

            companiesData.add(new DisplayItem(
                    company.getId(),
                    company.getCompanyName(),
                    company.getType(),
                    company.getAnnualTurnover(),
                    holdingName,
                    details
            ));
        }
    }

    @FXML
    private void handleAddCompany() {
        try {
            IndustrialCompanies company;

            if ("CoalCompany".equals(typeComboBox.getValue())) {
                CoalCompany coal = new CoalCompany();
                coal.setCoalVolume(Long.parseLong(coalVolumeField.getText()));
                coal.setMineCount(Long.parseLong(minesField.getText()));
                company = coal;
            } else {
                OilCompany oil = new OilCompany();
                oil.setOilVolume(Long.parseLong(oilVolumeField.getText()));
                oil.setHoleCount(Long.parseLong(wellsField.getText()));
                company = oil;
            }

            company.setCompanyName(nameField.getText());
            company.setAnnualTurnover(Long.parseLong(turnoverField.getText()));

            String selectedHolding = assignHoldingComboBox.getValue();
            if (selectedHolding != null && !selectedHolding.equals("Без холдинга")) {
                holdingRepository.findByHoldingName(selectedHolding).ifPresent(company::setHoldingCompany);
            }

            companyCRUD.save(company);
            showStatus("✅ Компания добавлена", true);
            clearFields();
            loadCompanies();

        } catch (NumberFormatException e) {
            showStatus("❌ Ошибка: проверьте числовые поля", false);
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuickAssign() {
        DisplayItem selected = companiesTable.getSelectionModel().getSelectedItem();
        String holdingName = assignHoldingComboBox.getValue();

        if (selected == null) {
            showStatus("⚠️ Выберите компанию", false);
            return;
        }
        if (holdingName == null || holdingName.equals("Без холдинга")) {
            showStatus("⚠️ Выберите холдинг", false);
            return;
        }

        try {
            var companyOpt = companyCRUD.findById(selected.getId());
            var holdingOpt = holdingRepository.findByHoldingName(holdingName);

            if (companyOpt.isPresent() && holdingOpt.isPresent()) {
                IndustrialCompanies company = companyOpt.get();
                HoldingCompanies holding = holdingOpt.get();

                if (company.getHoldingCompany() != null) {
                    company.getHoldingCompany().getIndustrialCompanies().remove(company);
                }

                holding.getIndustrialCompanies().add(company);
                company.setHoldingCompany(holding);

                companyCRUD.save(company);
                loadCompanies();
                showStatus("✅ Привязано к \"" + holdingName + "\"", true);
            }
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveFromHolding() {
        DisplayItem selected = companiesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showStatus("⚠️ Выберите компанию", false);
            return;
        }

        try {
            Optional<IndustrialCompanies> companyOpt = companyCRUD.findById(selected.getId());

            if (companyOpt.isPresent()) {
                IndustrialCompanies company = companyOpt.get();

                if (company.getHoldingCompany() == null) {
                    showStatus("⚠️ Компания не в холдинге", false);
                    return;
                }

                String holdingName = company.getHoldingCompany().getHoldingName();

                company.getHoldingCompany().getIndustrialCompanies().remove(company);
                company.setHoldingCompany(null);

                companyCRUD.save(company);
                loadCompanies();
                updateHoldingComboBoxes();
                showStatus("✅ Компания удалена из \"" + holdingName + "\"", true);
            }
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadCompanies();
        updateHoldingComboBoxes();
        showStatus("🔄 Список обновлён", true);
    }

    @FXML
    private void handleDelete() {
        DisplayItem selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                companyCRUD.findById(selected.getId()).ifPresent(companyCRUD::delete);
                loadCompanies();
                showStatus("✅ Компания удалена", true);
            } catch (Exception e) {
                showStatus("❌ Ошибка при удалении", false);
                e.printStackTrace();
            }
        } else {
            showStatus("⚠️ Выберите компанию для удаления", false);
        }
    }

    @FXML
    private void handleCalculate() {
        DisplayItem selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                IndustrialCompanies company = companyCRUD.findById(selected.getId()).orElse(null);
                if (company != null) {
                    company.CostOfTimeProduction();
                    companyCRUD.save(company);
                    loadCompanies();
                    showStatus("✅ Расчёт выполнен", true);
                }
            } catch (Exception e) {
                showStatus("❌ Ошибка при расчёте", false);
                e.printStackTrace();
            }
        } else {
            showStatus("⚠️ Выберите компанию", false);
        }
    }

    @FXML
    private void handleAddMines() {
        processCoalAction((coal, count) -> {
            coal.addCoalMines(count);
            return "✅ Шахты добавлены: +" + count;
        }, addMinesField, "❌ Введите число шахт");
    }

    @FXML
    private void handleStopExpanding() {
        processCoalAction((coal, count) -> {
            coal.stopExpanding();
            return "✅ Производство остановлено";
        }, null, null);
    }

    @FXML
    private void handleAddWells() {
        processOilAction((oil, count) -> {
            oil.addOilWells(count);
            return "✅ Скважины добавлены: +" + count;
        }, addWellsField, "❌ Введите число скважин");
    }

    @FXML
    private void handleCheckResources() {
        processOilAction((oil, count) -> {
            oil.checkResources();
            return "✅ Ресурсы проверены";
        }, null, null);
    }

    @FunctionalInterface
    private interface CoalAction { String apply(CoalCompany coal, Long count) throws Exception; }
    @FunctionalInterface
    private interface OilAction { String apply(OilCompany oil, Long count) throws Exception; }

    private void processCoalAction(CoalAction action, TextField inputField, String errorMsg) {
        DisplayItem selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("⚠️ Выберите компанию", false); return; }

        try {
            Long count = (inputField != null) ? Long.parseLong(inputField.getText()) : null;
            IndustrialCompanies company = companyCRUD.findById(selected.getId()).orElse(null);

            if (company instanceof CoalCompany coal) {
                String result = action.apply(coal, count);
                companyCRUD.save(coal);
                if (inputField != null) inputField.clear();
                loadCompanies();
                showStatus(result, true);
            } else {
                showStatus("⚠️ Не угольная компания", false);
            }
        } catch (NumberFormatException e) {
            showStatus(errorMsg != null ? errorMsg : "❌ Ошибка числа", false);
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void processOilAction(OilAction action, TextField inputField, String errorMsg) {
        DisplayItem selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("⚠️ Выберите компанию", false); return; }

        try {
            Long count = (inputField != null) ? Long.parseLong(inputField.getText()) : null;
            IndustrialCompanies company = companyCRUD.findById(selected.getId()).orElse(null);

            if (company instanceof OilCompany oil) {
                String result = action.apply(oil, count);
                companyCRUD.save(oil);
                if (inputField != null) inputField.clear();
                loadCompanies();
                showStatus(result, true);
            } else {
                showStatus("⚠️ Не нефтяная компания", false);
            }
        } catch (NumberFormatException e) {
            showStatus(errorMsg != null ? errorMsg : "❌ Ошибка числа", false);
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateHolding() {
        String name = newHoldingNameField.getText().trim();
        if (name.isEmpty()) {
            showStatus("⚠️ Введите название холдинга", false);
            return;
        }
        try {
            if (holdingRepository.findByHoldingName(name).isPresent()) {
                showStatus("❌ Холдинг с таким именем уже существует", false);
                return;
            }
            HoldingCompanies holding = new HoldingCompanies();
            holding.setHoldingName(name);
            holdingRepository.save(holding);

            newHoldingNameField.clear();
            updateHoldingComboBoxes();
            showStatus("✅ Холдинг \"" + name + "\" создан", true);
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchHolding() {
        String name = searchHoldingField.getText().trim();
        if (name.isEmpty()) {
            showStatus("⚠️ Введите название для поиска", false);
            return;
        }
        try {
            Optional<HoldingCompanies> holdingOpt = holdingRepository.findByHoldingName(name);
            if (holdingOpt.isPresent()) {
                displayHoldingCompanies(holdingOpt.get());
                showStatus("📋 Найдено компаний: " + holdingOpt.get().getIndustrialCompanies().size(), true);
            } else {
                holdingCompaniesList.getItems().clear();
                showStatus("❌ Холдинг \"" + name + "\" не найден", false);
            }
        } catch (Exception e) {
            showStatus("❌ Ошибка поиска: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUnassignFromHolding() {
        String selected = holdingCompaniesList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("⚠️ Выберите компанию в списке холдинга", false);
            return;
        }
        try {
            // Формат строки: "ID:123: CompanyName [Type]"
            String[] parts = selected.split(":", 3);
            if (parts.length < 2) {
                showStatus("❌ Ошибка формата данных", false);
                return;
            }

            Long companyId = Long.parseLong(parts[1].trim());
            Optional<IndustrialCompanies> companyOpt = companyCRUD.findById(companyId);

            if (companyOpt.isPresent()) {
                IndustrialCompanies company = companyOpt.get();
                if (company.getHoldingCompany() != null) {
                    String holdingName = company.getHoldingCompany().getHoldingName();
                    company.getHoldingCompany().getIndustrialCompanies().remove(company);
                    company.setHoldingCompany(null);
                    companyCRUD.save(company);
                    loadCompanies();
                    updateHoldingComboBoxes();
                    if (!searchHoldingField.getText().isEmpty()) {
                        handleSearchHolding();
                    }
                    showStatus("✅ Компания отвязана от \"" + holdingName + "\"", true);
                }
            } else {
                showStatus("❌ Компания не найдена", false);
            }
        } catch (NumberFormatException e) {
            showStatus("❌ Ошибка: неверный формат ID", false);
            e.printStackTrace();
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteHolding() {
        String name = searchHoldingField.getText().trim();
        if (name.isEmpty()) {
            showStatus("⚠️ Введите название холдинга для удаления", false);
            return;
        }
        try {
            Optional<HoldingCompanies> holdingOpt = holdingRepository.findByHoldingName(name);
            if (holdingOpt.isPresent()) {
                HoldingCompanies holding = holdingOpt.get();
                for (IndustrialCompanies company : holding.getIndustrialCompanies()) {
                    company.setHoldingCompany(null);
                    companyCRUD.save(company);
                }
                holdingRepository.delete(holding);
                holdingCompaniesList.getItems().clear();
                updateHoldingComboBoxes();
                loadCompanies();
                showStatus("✅ Холдинг \"" + name + "\" удалён", true);
            } else {
                showStatus("❌ Холдинг не найден", false);
            }
        } catch (Exception e) {
            showStatus("❌ Ошибка: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }


    private void displayHoldingCompanies(HoldingCompanies holding) {
        holdingCompaniesList.getItems().clear();
        if (holding.getIndustrialCompanies() == null || holding.getIndustrialCompanies().isEmpty()) {
            holdingCompaniesList.getItems().add("📭 Нет компаний в холдинге");
            return;
        }
        for (IndustrialCompanies c : holding.getIndustrialCompanies()) {
           holdingCompaniesList.getItems().add(c.toString());
        }
        showStatus("📋 Загружено компаний: " + holding.getIndustrialCompanies().size(), true);
    }

    private void clearFields() {
        nameField.clear(); turnoverField.clear();
        coalVolumeField.clear(); minesField.clear();
        oilVolumeField.clear(); wellsField.clear();
        addMinesField.clear(); addWellsField.clear();
        assignHoldingComboBox.setValue("Без холдинга");
    }

    private void showStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success
                ? "-fx-text-fill: #28a745; -fx-font-weight: bold;"
                : "-fx-text-fill: #dc3545; -fx-font-weight: bold;");
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class DisplayItem {
        private Long id;
        private String name;
        private String type;
        private Long turnover;
        private String holdingName;
        private String details;
    }
}