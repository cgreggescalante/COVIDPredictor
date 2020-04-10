package com.covidpredictor;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CaseData {
    private Map<String, Country> countries;

    private List<String[]> rawData;

    public CaseData() {
        countries = new TreeMap<>();

        try {
            FileReader reader = new FileReader("src/main/resources/data/train.csv");

            CSVReader csvReader = new CSVReader(reader, ',', '"', 1);

            rawData = csvReader.readAll();

            for (String[] entry : rawData) {
                if (countries.containsKey(entry[2])) {
                    countries.get(entry[2]).addDay(entry);
                } else {
                    countries.put(
                            entry[2],
                            new Country(entry[2])
                    );
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        saveData();

    }

    public void saveData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/covid", "PotatoTax", "PotatoTax1707")) {
                conn.setAutoCommit(false);
                PreparedStatement addCumulative = conn.prepareStatement("INSERT INTO cumulative(country, region, date, cases, fatalities) VALUES (?,?,?,?,?);");
                PreparedStatement addDaily = conn.prepareStatement("INSERT INTO daily(country, region, date, cases, fatalities) VALUES (?,?,?,?,?);");

                int i = 0;

                for (String[] entry : rawData) {
                    addCumulative.setString(1, entry[2]);
                    addCumulative.setString(2, entry[1]);
                    addCumulative.setDate(3, Date.valueOf(entry[3]));
                    addCumulative.setInt(4, (int) Float.parseFloat(entry[4]));
                    addCumulative.setInt(5, (int) Float.parseFloat(entry[5]));
                    addCumulative.addBatch();

                    i++;

                    if (i % 1000 == 0) {
                        addCumulative.executeBatch();
                    }
                }

                addCumulative.executeBatch();

                i = 0;

                for (Country country : countries.values()) {
                    for (LocalDate date : country.getDailyCases().keySet()) {
                        addDaily.setString(1, country.getName());
                        addDaily.setString(2, null);
                        addDaily.setDate(3, Date.valueOf(date));
                        addDaily.setInt(4, country.getDailyCases().get(date));
                        addDaily.setInt(5, country.getDailyFatalities().get(date));
                        addDaily.addBatch();

                        i++;

                        if (i % 1000 == 0) {
                            addDaily.executeBatch();
                        }
                    }

                    for (Region region : country.getRegions().values()) {
                        for (LocalDate date : region.getDailyCases().keySet()) {
                            addDaily.setString(1, country.getName());
                            addDaily.setString(2, region.getName());
                            addDaily.setDate(3, Date.valueOf(date));
                            addDaily.setInt(4, region.getDailyCases().get(date));
                            addDaily.setInt(5, region.getDailyFatalities().get(date));
                            addDaily.addBatch();

                            i++;

                            if (i % 1000 == 0) {
                                addDaily.executeBatch();
                            }
                        }
                    }
                }

                addDaily.executeBatch();

                conn.commit();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CaseData();
    }
}
