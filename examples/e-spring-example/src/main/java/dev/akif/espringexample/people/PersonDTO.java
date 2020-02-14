package dev.akif.espringexample.people;

public class PersonDTO {
    private String name;
    private Integer age;

    public PersonDTO() {}

    public PersonDTO(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public PersonDTO(Person person) {
        this(person.getName(), person.getAge());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Person toPerson(long id) {
        return new Person(id, name, age);
    }

    public Person updated(Person person) {
        return new Person(person.getId(), name == null ? person.getName() : name, age == null ? person.getAge() : age);
    }
}
