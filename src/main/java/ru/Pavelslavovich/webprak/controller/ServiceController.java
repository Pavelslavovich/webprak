package ru.Pavelslavovich.webprak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.Pavelslavovich.webprak.dao.ServiceDao;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceDao serviceDao;

    @GetMapping
    public String list(@RequestParam(value = "nameQuery", required = false) String nameQuery,
                       @RequestParam(value = "minCost", required = false) String minCost,
                       @RequestParam(value = "maxCost", required = false) String maxCost,
                       Model model) {
        List<ServiceEntity> services;
        if ((nameQuery != null && !nameQuery.isEmpty())
                || (minCost != null && !minCost.isEmpty())
                || (maxCost != null && !maxCost.isEmpty())) {
            String nq = (nameQuery != null && !nameQuery.isEmpty()) ? nameQuery : null;
            BigDecimal min = (minCost != null && !minCost.isEmpty()) ? new BigDecimal(minCost) : null;
            BigDecimal max = (maxCost != null && !maxCost.isEmpty()) ? new BigDecimal(maxCost) : null;
            services = serviceDao.findByNameLikeAndCostRange(nq, min, max);
        } else {
            services = serviceDao.findAll();
        }
        model.addAttribute("services", services);
        return "services";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Optional<ServiceEntity> opt = serviceDao.findById(id);
        if (opt.isEmpty()) return "redirect:/services";
        model.addAttribute("service", opt.get());
        return "service";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("isNew", true);
        return "serviceEdit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Optional<ServiceEntity> opt = serviceDao.findById(id);
        if (opt.isEmpty()) return "redirect:/services";
        model.addAttribute("service", opt.get());
        model.addAttribute("isNew", false);
        return "serviceEdit";
    }

    @PostMapping
    public String create(@RequestParam("name") String name, @RequestParam("baseCost") String baseCost) {
        ServiceEntity service = new ServiceEntity(name, new BigDecimal(baseCost));
        serviceDao.save(service);
        return "redirect:/services/" + service.getId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @RequestParam("name") String name,
                         @RequestParam("baseCost") String baseCost) {
        ServiceEntity updated = serviceDao.updateById(id, name, new BigDecimal(baseCost));
        if (updated == null) return "redirect:/services";
        return "redirect:/services/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes attrs) {
        if (serviceDao.hasContracts(id)) {
            attrs.addFlashAttribute("error", "Невозможно удалить услугу: есть связанные договоры");
            return "redirect:/services/" + id;
        }
        serviceDao.deleteById(id);
        return "redirect:/services";
    }
}
