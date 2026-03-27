package ru.Pavelslavovich.webprak.dao;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ServiceDaoTests extends DaoTestSupport {
    private ServiceDao serviceDao;

    @BeforeMethod
    public void initDao() {
        serviceDao = new ServiceDao(sessionFactory);
    }

    @Test
    public void testSaveFindUpdateDeleteById() {
        ServiceEntity service = new ServiceEntity("Тестовая услуга", new BigDecimal("1500.00"));

        ServiceEntity saved = serviceDao.save(service);
        Assert.assertNotNull(saved.getId());

        Optional<ServiceEntity> found = serviceDao.findById(saved.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get().getName(), "Тестовая услуга");

        found.get().setName("Обновлённая услуга");
        serviceDao.update(found.get());
        Assert.assertEquals(serviceDao.findById(saved.getId()).orElseThrow().getName(), "Обновлённая услуга");

        List<ServiceEntity> all = serviceDao.findAll();
        Assert.assertFalse(all.isEmpty());

        Assert.assertTrue(serviceDao.deleteById(saved.getId()));
        Assert.assertFalse(serviceDao.findById(saved.getId()).isPresent());
        Assert.assertFalse(serviceDao.deleteById(saved.getId()));
    }

    @Test
    public void testFindByNameLikeAndCostRangeWithMatches() {
        seedBasicData();

        List<ServiceEntity> result = serviceDao.findByNameLikeAndCostRange(
                "сульта",
                new BigDecimal("1000"),
                new BigDecimal("10000")
        );

        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).getName(), "Консультации");
    }

    @Test
    public void testFindByNameLikeAndCostRangeWithoutMatches() {
        seedBasicData();

        List<ServiceEntity> result = serviceDao.findByNameLikeAndCostRange(
                "эмиссия",
                null,
                null
        );

        Assert.assertTrue(result.isEmpty());
    }
}
