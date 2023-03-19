package orgStructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class OrgStructureParserImpl implements OrgStructureParser {

    @Override
    public Employee parseStructure(File csvFile) throws IOException {
        Set<Employee> allEmpoyees = new HashSet<>();
        try (FileInputStream fileInputStream = new FileInputStream(csvFile);
             Scanner scanner = new Scanner(fileInputStream)) {
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                Employee e = employeeMapper(scanner.nextLine());
                allEmpoyees.add(e);
            }
        }
        return updateSubordinates(allEmpoyees);
    }

    private Employee employeeMapper(String stringFromFile) {
        String[] strings = stringFromFile.split(";");
        Employee employee = new Employee();
        try {
            employee.setId(Long.parseLong(strings[0]));
            employee.setBossid(strings[1].isBlank() ? null : Long.parseLong(strings[1]));
            employee.setName(strings[2]);
            employee.setPosition(strings[3]);
        } catch (NumberFormatException e) {
            throw new IncorrectFileException("Некорректный файл: Ошибка преобразования в число для строки \"" +
                    stringFromFile + "\"");
        }
        return employee;
    }

    private Employee updateSubordinates(Set<Employee> allEmployees) {
        checkForDuplicatedId(allEmployees);
        Employee head = null;
        for (Employee emp : allEmployees) {
            if (emp.getBossid() == null) {
                if (head == null) {
                    head = emp;
                } else {
                    throw new IncorrectFileException("Некорректный файл: более 1 Генерального директора найдено. Id сотрудников: "
                            + head.getId() + ", " + emp.getId());
                }
            } else {
                Employee bossForEmp = allEmployees.stream()
                        .filter(e -> e.getId().equals(emp.getBossid()))
                        .findFirst()
                        .orElseThrow(() -> new IncorrectFileException("Некорректный файл: Не найден босс с id = " + emp.getBossid()
                                + " для сотруднка с id = " + emp.getId()));
                emp.setBoss(bossForEmp);
                bossForEmp.getSubordinate().add(emp);

            }
            if (head == null) {
                throw new IncorrectFileException("Некорректный файл: Не найден Генеральный Директор");
            }
        }
        return head;
    }

    private void checkForDuplicatedId(Set<Employee> allEmployees) {
        long uniqueIdCount = allEmployees.stream()
                .map(Employee::getId)
                .distinct()
                .count();
        if (uniqueIdCount < allEmployees.size()) {
            throw new IncorrectFileException("Некорректный файл: найдены сотрудники с одинаковым id");
        }
    }
}
