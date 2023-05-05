package com.example.lez5_scene_builder;

import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

public class Person {

    private final ReadOnlyIntegerWrapper id =
            new ReadOnlyIntegerWrapper(this, "id");

    private final StringProperty name =
            new SimpleStringProperty(this,"name");
    private final ObjectProperty<LocalDate> birthdate =
            new SimpleObjectProperty<>(this, "birthdate", null);

    public Person(int id, String name, LocalDate birthdate) {
        this.id.set(id);
        this.name.set(name);
        this.birthdate.set(birthdate);
    }

    public final int getId() {
        return id.get();
    }

    public final ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    public final String getName() {
        return name.get();
    }

    public final void setName(String name) {
        nameProperty().set(name);
    }

    public final StringProperty nameProperty() {
        return name;
    }


    public final LocalDate getBirthdate() {
        return birthdate.get();
    }

    public final void setBirthdate(LocalDate birthdate) {
        birthdateProperty().set(birthdate);
    }

    public final ObjectProperty<LocalDate> birthdateProperty() {
        return birthdate;
    }

}



