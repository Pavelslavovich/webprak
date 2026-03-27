package ru.Pavelslavovich.webprak.dao;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.Pavelslavovich.webprak.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoTests extends DaoTestSupport {
    private EmployeeDao employeeDao;

    @BeforeMethod
    public void initDao() {
        employeeDao = new EmployeeDao(sessionFactory);
    }

    @Test
    public void testSaveFindUpdateDeleteById() {
        Employee employee = new Employee("Тестов Тестович", "юрист", "высшее", "ул. Тестовая, 1", "");

        Employee saved = employeeDao.save(employee);
        Assert.assertNotNull(saved.getId());

        Optional<Employee> found = employeeDao.findById(saved.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get().getFullName(), "Тестов Тестович");

        found.get().setFullName("Тестов Тест Обновлён");
        employeeDao.update(found.get());
        Assert.assertEquals(employeeDao.findById(saved.getId()).orElseThrow().getFullName(), "Тестов Тест Обновлён");

        List<Employee> all = employeeDao.findAll();
        Assert.assertFalse(all.isEmpty());

        Assert.assertTrue(employeeDao.deleteById(saved.getId()));
        Assert.assertFalse(employeeDao.findById(saved.getId()).isPresent());
        Assert.assertFalse(employeeDao.deleteById(saved.getId()));
    }

    @Test
    public void testFindByClientAndPeriodWithMatches() {
        SeedData data = seedBasicData();
        Long clientId = data.clients().get(0).getId();

        List<Employee> employees = employeeDao.findByClientAndPeriod(
                clientId,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );

        Assert.assertEquals(employees.size(), 2);
    }

    @Test
    public void testFindByClientAndPeriodWithoutMatches() {
        SeedData data = seedBasicData();
        Long clientId = data.clients().get(1).getId();

        List<Employee> employees = employeeDao.findByClientAndPeriod(
                clientId,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)
        );

        Assert.assertTrue(employees.isEmpty());
    }
}
