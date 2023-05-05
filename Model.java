package com.example.lez5_scene_builder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;


public class Model {

    public void log(Object o){
        System.out.println(o);
    }

    private static Model single_instance = null;
    private Connection conn;
    public ObservableList<Person> people;

    public ObservableList<SimpleStringProperty> occupations = FXCollections.observableArrayList();

    public ObservableList<SimpleStringProperty> cities = FXCollections.observableArrayList();


    public void connect() throws SQLException {

        String url = "jdbc:sqlite:table.db";
        conn = DriverManager.getConnection(url);
        System.out.println("Connection to SQLite has been established.");

    }

    public ResultSet runQuery(String q) throws SQLException {

        ResultSet rs = null;
        Statement stmt  = conn.createStatement();
        rs = stmt.executeQuery(q);
        return rs;

    }

    public void runStatement(String s) throws SQLException {

        Statement stmt  = conn.createStatement();
        stmt.executeUpdate(s);

    }

    public int runStatementWithOutput(String s) throws SQLException {

        int r;
        Statement stmt  = conn.createStatement();
        r = stmt.executeUpdate(s);
        return r;
    }

    private Model() throws SQLException
    {
        connect();

        if (tableExists("people")) {
            log("people table exists");
        } else {
            log("people table DO NOT exists");
            resetPeopleTable();
        };

        if (tableExists("occupations")) {
            log("occupations table exists");
        } else {
            log("occupations table DO NOT exists");
            resetOccupationsTable();
        };

        if (tableExists("cities")){
            log("people table exists");
        } else {
            log("people table DO NOT exists");
            resetCitiesTable();
        };

        loadPeople();
        loadOccupations();
        loadCities();
    }

    public static synchronized Model getInstance() throws SQLException
    {
        if (single_instance == null)
            single_instance = new Model();

        return single_instance;
    }

    public boolean tableExists(String table_name) throws SQLException {
        String q = "SELECT * FROM sqlite_master WHERE tbl_name = '" + table_name + "'";
        log(q);
        ResultSet rs = runQuery(q);
        return rs.next();
    }

    public void resetPeopleTable() throws SQLException{
        String s = "DROP TABLE IF EXISTS people;" +
                "CREATE TABLE people( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(255), " +
                "birthdate DATE, " +
                "occupation VARCHAR(255), " +
                "city VARCHAR(255)" +
                ");";
        log(s);
        runStatement(s);
    }

    public void resetOccupationsTable() throws SQLException{
        String s = "DROP TABLE IF EXISTS occupations;" +
                "CREATE TABLE occupations( " +
                "name VARCHAR(255) PRIMARY KEY" +
                ");";
        log(s);
        runStatement(s);
    }

    public void resetCitiesTable() throws SQLException{
        String s = "DROP TABLE IF EXISTS cities;" +
                "CREATE TABLE cities( " +
                "name VARCHAR(255) PRIMARY KEY" +
                ");";
        log(s);
        runStatement(s);
    }

    public void setPersonName(int id, String name) throws SQLException{
        String s = "UPDATE people SET name = '" + name + "' WHERE id = " + id;
        log(s);
        runStatement(s);
    }

    public void loadPeople() throws SQLException{
        people = FXCollections.<Person>observableArrayList(
                person -> new Observable[] {
                        person.nameProperty(), person.birthdateProperty()  }
        );
        String q = "SELECT * FROM people ORDER BY id";
        log(q);
        ResultSet rs = runQuery(q);
        while (rs.next()){
            people.add(new Person(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDate("birthdate") == null ? null :  rs.getDate("birthdate").toLocalDate()));
        }
    }

    public void loadOccupations() throws SQLException{
        String q = "SELECT name FROM occupations ORDER BY name";
        log(q);
        ResultSet rs = runQuery(q);
        while (rs.next()){
            String r = rs.getString("name");
            if(occupations.filtered(p -> p.getValue().equals(r)).isEmpty())
                occupations.add(new SimpleStringProperty(r));
        }
    }

    public void loadCities() throws SQLException{
        String q = "SELECT name FROM cities ORDER BY name";
        log(q);
        ResultSet rs = runQuery(q);
        while (rs.next()){
            String r = rs.getString("name");
            if(cities.filtered(p -> p.getValue().equals(r)).isEmpty())
                cities.add(new SimpleStringProperty(r));
        }
    }

    public TableColumn<Person, String> getPeopleNames() {
        TableColumn<Person, String> r = new TableColumn<>("name");
        r.setCellValueFactory(new PropertyValueFactory<>("name"));
        r.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        r.setOnEditCommit(e -> {
            try{
                setPersonName(e.getRowValue().getId(), e.getNewValue());
            }
            catch( SQLException sql_e ){
                log("error updating person" + e.getRowValue().getId());
            }
            e.getRowValue().setName(e.getNewValue());
        });
        return r;
    }

    public TableColumn<Person, LocalDate> getPeopleBirthdates(){
        TableColumn<Person, LocalDate> r = new TableColumn<>("birthdate");
        r.setCellValueFactory(new PropertyValueFactory<>("birthdate"));
        return r;
    }

    public void create_occupation(String name) throws SQLException{
        String q = "INSERT INTO Occupations(name)\n" +
                "VALUES ('"+ name +"');";
        log(q);
        runStatement(q);
    }

    public void create_city(String name) throws SQLException{
        String q = "INSERT INTO Cities(name)\n" +
                "VALUES ('"+ name +"');";
        log(q);
        runStatement(q);
    }

    public void create_person(String name, LocalDate birthdate) throws SQLException, ParseException {

        log(birthdate.toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(((Date) df.parse(birthdate.toString())).getTime());
        Long bdate = df.parse(birthdate.toString()).getTime();
        String q = "INSERT INTO People(name, birthdate)\n" +
                "VALUES ('"+ name +"', "+ bdate +")\n" +
                "RETURNING id;";
        log(q);
        runStatement(q);

    }


}
