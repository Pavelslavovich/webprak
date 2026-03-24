package ru.Pavelslavovich.webprak.dao;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.Pavelslavovich.webprak.model.ContractStatus;
import ru.Pavelslavovich.webprak.model.ServiceContract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServiceContractDaoTests extends DaoTestSupport {
    private ServiceContractDao serviceContractDao;

    @BeforeMethod
    public void initDao() {
        serviceContractDao = new ServiceContractDao(sessionFactory);
    }

    @Test
    public void testFindByContractNumberFoundAndNotFound() {
        seedBasicData();

        Optional<ServiceContract> found = serviceContractDao.findByContractNumber("LF-T-001");
        Optional<ServiceContract> missing = serviceContractDao.findByContractNumber("NOPE");

        Assert.assertTrue(found.isPresent());
        Assert.assertFalse(missing.isPresent());
    }

    @Test
    public void testFindByStatusVariants() {
        seedBasicData();

        List<ServiceContract> active = serviceContractDao.findByStatus(ContractStatus.ACTIVE);
        List<ServiceContract> cancelled = serviceContractDao.findByStatus(ContractStatus.CANCELLED);

        Assert.assertEquals(active.size(), 1);
        Assert.assertTrue(cancelled.isEmpty());
    }

    @Test
    public void testRegisterContractWithEmployees() {
        SeedData data = seedBasicData();

        ServiceContract newContract = new ServiceContract(
                "LF-T-003",
                data.clients().get(0),
                data.services().get(1),
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 5),
                null,
                ContractStatus.DRAFT,
                new BigDecimal("7000.00"),
                "новый"
        );

        ServiceContract saved = serviceContractDao.registerContractWithEmployees(
                newContract,
                List.of(
                        new ServiceContractDao.EmployeeRole(data.employees().get(0).getId(), "исполнитель"),
                        new ServiceContractDao.EmployeeRole(data.employees().get(2).getId(), "ассистент")
                )
        );

        Assert.assertNotNull(saved.getId());

        ServiceContract loaded = serviceContractDao.findById(saved.getId()).orElseThrow();
        Assert.assertEquals(loaded.getContractNumber(), "LF-T-003");

        Integer employeesCount = inTransaction(session ->
                session.createQuery(
                                "select count(ce) from ContractEmployee ce where ce.contract.id = :contractId",
                                Long.class
                        )
                        .setParameter("contractId", saved.getId())
                        .uniqueResult()
                        .intValue()
        );
        Assert.assertEquals(employeesCount.intValue(), 2);
    }
}
