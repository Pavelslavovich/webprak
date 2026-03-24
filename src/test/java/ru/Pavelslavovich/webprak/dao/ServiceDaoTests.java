package ru.Pavelslavovich.webprak.dao;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.math.BigDecimal;
import java.util.List;

public class ServiceDaoTests extends DaoTestSupport {
    private ServiceDao serviceDao;

    @BeforeMethod
    public void initDao() {
        serviceDao = new ServiceDao(sessionFactory);
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
