package orgStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employee {
    private Long id;
    private Long bossid;
    private String name;
    private String position;
    private Employee boss;
    private List<Employee> subordinate = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBossid() {
        return bossid;
    }

    public void setBossid(Long bossid) {
        this.bossid = bossid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Employee getBoss() {
        return boss;
    }

    public void setBoss(Employee boss) {
        this.boss = boss;
    }

    public List<Employee> getSubordinate() {
        return subordinate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", bossid=" + bossid +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", subordinate=" + subordinate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(bossid, employee.bossid) && Objects.equals(name, employee.name) && Objects.equals(position, employee.position) && Objects.equals(boss, employee.boss) && Objects.equals(subordinate, employee.subordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bossid, name, position, boss, subordinate);
    }
}
