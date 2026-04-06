package org.example;

public class CompanyItem {
    private final Long id;
    private final String name;
    private final String type;
    private final Long turnover;
    private final String details; // ← добавили поле

    public CompanyItem(Long id, String name, String type, Long turnover, String details) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.turnover = turnover;
        this.details = details;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Long getTurnover() { return turnover; }
    public String getDetails() { return details; }
}