package ru.Pavelslavovich.webprak.dao;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.Pavelslavovich.webprak.model.Employee;

import java.time.LocalDate;
import java.util.List;

public class EmployeeDaoTests extends DaoTestSupport {
    private EmployeeDao employeeDao;

    @BeforeMethod
    public void initDao() {
        employeeDao = new EmployeeDao(sessionFactory);
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
