package ru.Pavelslavovich.webprak.dao;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.Pavelslavovich.webprak.model.Client;
import ru.Pavelslavovich.webprak.model.ClientType;

import java.time.LocalDate;
import java.util.List;

public class ClientDaoTests extends DaoTestSupport {
    private ClientDao clientDao;

    @BeforeMethod
    public void initDao() {
        clientDao = new ClientDao(sessionFactory);
    }

    @Test
    public void testSaveFindUpdateDelete() {
        Client client = new Client(ClientType.ORGANIZATION, "ООО Тест", "заметка");

        Client saved = clientDao.save(client);
        Assert.assertNotNull(saved.getId());

        Client found = clientDao.findById(saved.getId()).orElseThrow();
        Assert.assertEquals(found.getDisplayName(), "ООО Тест");

        found.setDisplayName("ООО Тест Обновлен");
        clientDao.update(found);

        Client updated = clientDao.findById(saved.getId()).orElseThrow();
        Assert.assertEquals(updated.getDisplayName(), "ООО Тест Обновлен");

        Assert.assertTrue(clientDao.deleteById(saved.getId()));
        Assert.assertFalse(clientDao.findById(saved.getId()).isPresent());
        Assert.assertFalse(clientDao.deleteById(saved.getId()));
    }

    @Test
    public void testFindByServiceInPeriodAndEmployeeWithEmployeeFilter() {
        SeedData data = seedBasicData();

        Long serviceId = data.services().get(0).getId();
        Long employeeId = data.employees().get(0).getId();

        List<Client> clients = clientDao.findByServiceInPeriodAndEmployee(
                serviceId,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                employeeId
        );

        Assert.assertEquals(clients.size(), 1);
        Assert.assertEquals(clients.get(0).getDisplayName(), "ООО Альфа");
    }

    @Test
    public void testFindByServiceInPeriodAndEmployeeWhenNoMatches() {
        SeedData data = seedBasicData();

        Long serviceId = data.services().get(0).getId();

        List<Client> clients = clientDao.findByServiceInPeriodAndEmployee(
                serviceId,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 2, 1),
                null
        );

        Assert.assertTrue(clients.isEmpty());
    }
}
