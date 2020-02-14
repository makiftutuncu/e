package dev.akif.espringexample.people;

public class PersonDTOWithId extends PersonDTO {
    private Long id;

    public PersonDTOWithId(Long id, String name, Integer age) {
        super(name, age);
        this.id = id;
    }

    public PersonDTOWithId(Person person) {
        this(person.getId(), person.getName(), person.getAge());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
