package org.example;

import java.util.Scanner;
import org.example.repository.crudRealization;
import org.example.model.CoalCompany;
import org.example.model.IndustrialCompanies;
import org.example.model.OilCompany;


public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static crudRealization CompanyCRUD;
    public static void main(String[] args) {

        boolean running = true;


        while (running) {
            showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addCompany(CompanyCRUD);
                case "2" -> updateCompany(CompanyCRUD);
                case "3" -> deleteCompany(CompanyCRUD);
                case "4" -> getInfo(CompanyCRUD);
                case "5" -> madeAction(CompanyCRUD);
                case "0" -> {
                    return;
                }

                default -> System.out.println("Unknown input");
            }
        }
        scanner.close();
    }

    private static void addCompany(crudRealization CompanyCRUD) {
        System.out.println("Выберите тип компании:");
        System.out.println("1. Угольная (Coal)");
        System.out.println("2. Нефтяная (Oil)");
        System.out.println("Выберите: ");

        String companyTypeChoice = scanner.nextLine();

        if ("1".equals(companyTypeChoice)) {
            CoalCompany coalCompany = new CoalCompany();

            System.out.println("Введите название компании");
            coalCompany.setCompanyName(scanner.nextLine());

            System.out.println("Введите годовой оборот");
            coalCompany.setAnnualTurnover(Long.parseLong(scanner.nextLine()));

            System.out.println("Тип компании");
            System.out.println("Угольная -> Coal");
            coalCompany.setType("CoalCompany");


            System.out.println("Введите объём угля");
            coalCompany.setCoalVolume(Long.parseLong(scanner.nextLine()));

            System.out.println("Введите кол-во шахт:");
            coalCompany.setMineCount(Long.parseLong(scanner.nextLine()));

            CompanyCRUD.save(coalCompany);
            System.out.println("Компания успешно сохранена");
        } else if ("2".equals(companyTypeChoice)) {
            OilCompany oilCompany = new OilCompany();

            System.out.println("Введите название компании");
            oilCompany.setCompanyName(scanner.nextLine());

            System.out.println("Введите годовой оборот");
            oilCompany.setAnnualTurnover(Long.parseLong(scanner.nextLine()));

            System.out.println("Тип компании");
            System.out.println("Нефтяная -> Oil");
            oilCompany.setType("OilCompany");

            System.out.println("Введите объём нефти");
            oilCompany.setOilVolume(Long.parseLong(scanner.nextLine()));

            System.out.println("Введите кол-во скважин:");
            oilCompany.setHoleCount(Long.parseLong(scanner.nextLine()));

            CompanyCRUD.save(oilCompany);
            System.out.println("Компания успешно сохранена");
        } else {
            System.out.println("Unknown input");
        }
    }

    private static void updateCompany(crudRealization CompanyCRUD) {
        System.out.println("Введите ID: ");
        Long id = Long.parseLong(scanner.nextLine());

        IndustrialCompanies company = CompanyCRUD.findById(id).orElse(null);
        if (company == null) {
            System.out.println("Компания не найдена");
            return;
        }

        System.out.println("Выберите поле для изменения:");
        System.out.println("1. Название");
        System.out.println("2. Годовой оборот");
        System.out.println("3. Тип");

        if (company instanceof CoalCompany) {
            System.out.println("4. Объём угля");
            System.out.println("5. Кол-во шахт");
        } else if (company instanceof OilCompany) {
            System.out.println("4. Объём нефти");
            System.out.println("5. Кол-во скважин");
        }
        System.out.print("Выберите: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> {
                System.out.print("Новое название: ");
                company.setCompanyName(scanner.nextLine());
            }
            case "2" -> {
                System.out.print("Новый оборот: ");
                company.setAnnualTurnover(Long.parseLong(scanner.nextLine()));
            }
            case "3" -> {
                System.out.print("Новый тип: ");

            }
            case "4" -> {
                System.out.print("Новый объём: ");
                Long val = Long.parseLong(scanner.nextLine());
                if (company instanceof CoalCompany c) c.setCoalVolume(val);
                else if (company instanceof OilCompany o) o.setOilVolume(val);
            }
            case "5" -> {
                System.out.print("Новое количество: ");
                Long val = Long.parseLong(scanner.nextLine());
                if (company instanceof CoalCompany c) c.setMineCount(val);
                else if (company instanceof OilCompany o) o.setHoleCount(val);
            }
            default -> System.out.println("Неверный выбор");
        }

        CompanyCRUD.save(company);
        System.out.println("Компания обновлена");
    }

    private static void deleteCompany(crudRealization CompanyCRUD) {
        Long input;

        System.out.println("Введите ID: ");
        input = Long.parseLong(scanner.nextLine());

        IndustrialCompanies company = CompanyCRUD.findById(input).orElse(null);
        if (company == null) {
            System.out.println("Компания не найдена");
            return;
        }

        CompanyCRUD.deleteById(input);
        System.out.println("Компания успешно удалена");
    }

    private static void getInfo(crudRealization CompanyCRUD) {
        System.out.println("1. По ID");
        System.out.println("2. Показать всех");
        System.out.println("Выберите: ");

        String input = scanner.nextLine();

        if ("1".equals(input)) {
            Long inputID = Long.parseLong(scanner.nextLine());
            IndustrialCompanies company = CompanyCRUD.findById(inputID).orElse(null);

            if (company == null) {
                System.out.println("Не удалось найти компанию");
            } else {
                company.showInfo();
            }
        } else if ("2".equals(input)) {
            for (IndustrialCompanies industrialCompanies : CompanyCRUD.findAll()) {
                industrialCompanies.showInfo();
                System.out.println("--------------------------");
            }
        }
    }

    private static void madeAction(crudRealization CompanyCRUD) {
        System.out.println("Выберите тип компании:");
        System.out.println("1. Угольная (Coal)");
        System.out.println("2. Нефтяная (Oil)");
        System.out.print("Выберите: ");

        String input = scanner.nextLine();
        switch (input) {
            case "1" -> madeCoalAction(CompanyCRUD);
            case "2" -> madeOilAction(CompanyCRUD);
            default -> System.out.println("Неверный выбор");
        }
    }

    private static void madeCoalAction(crudRealization CompanyCRUD) {
        System.out.println("\n>>> Угольная компания");
        System.out.print("Введите ID компании: ");

        Long inputID;
        try {
            inputID = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
            return;
        }

        IndustrialCompanies company = CompanyCRUD.findById(inputID).orElse(null);

        // Проверка: найдена ли компания и является ли она угольной
        if (company instanceof CoalCompany coalCompany) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Рассчитать стоимость продукции");
            System.out.println("2. Добавить шахты");
            System.out.println("3. Остановить производство");
            System.out.print("Выберите: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    coalCompany.CostOfTimeProduction();
                    CompanyCRUD.save(coalCompany);
                    System.out.println("Расчет выполнен");
                }
                case "2" -> {
                    System.out.print("Введите кол-во добавляемых шахт: ");
                    try {
                        Long count = Long.parseLong(scanner.nextLine());
                        coalCompany.addCoalMines(count);
                        CompanyCRUD.save(coalCompany);
                        System.out.println("Шахты добавлены");
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка ввода числа");
                    }
                }
                case "3" -> {
                    coalCompany.stopExpanding();
                    CompanyCRUD.save(coalCompany);
                    System.out.println("Производство остановлено");
                }
                default -> System.out.println("Неверный выбор");
            }
        } else if (company == null) {
            System.out.println("Компания с таким ID не найдена");
        } else {
            System.out.println("Ошибка: Компания с этим ID не является угольной");
        }
    }

    private static void madeOilAction(crudRealization CompanyCRUD) {
        System.out.println("\n>>> Нефтяная компания");
        System.out.print("Введите ID компании: ");

        Long inputID;
        try {
            inputID = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
            return;
        }

        IndustrialCompanies company = CompanyCRUD.findById(inputID).orElse(null);

        if (company instanceof OilCompany oilCompany) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Рассчитать стоимость продукции");
            System.out.println("2. Добавить скважины");
            System.out.println("3. Проверить ресурсы");
            System.out.print("Выберите: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    oilCompany.CostOfTimeProduction();
                    CompanyCRUD.save(oilCompany);
                    System.out.println("Расчет выполнен");
                }
                case "2" -> {
                    System.out.print("Введите кол-во добавляемых скважин: ");
                    try {
                        Long count = Long.parseLong(scanner.nextLine());
                        oilCompany.addOilWells(count);
                        CompanyCRUD.save(oilCompany);
                        System.out.println("Скважины добавлены");
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка ввода числа");
                    }
                }
                case "3" -> {
                    oilCompany.checkResources();
                    CompanyCRUD.save(oilCompany);
                    System.out.println("Ресурсы проверены");
                }
                default -> System.out.println("Неверный выбор");
            }
        } else if (company == null) {
            System.out.println("Компания с таким ID не найдена");
        } else {
            System.out.println("Ошибка: Компания с этим ID не является нефтяной");
        }
    }

    private static void showMenu() {
        System.out.println("Menu:");
        System.out.println("1. Add company");
        System.out.println("2. Change company");
        System.out.println("3. Delete company");
        System.out.println("4. Get one or all companies");
        System.out.println("5. Methods");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

}