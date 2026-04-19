package ru.Pavelslavovich.webprak.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.Pavelslavovich.webprak.dao.ClientDao;
import ru.Pavelslavovich.webprak.dao.EmployeeDao;
import ru.Pavelslavovich.webprak.dao.ServiceContractDao;
import ru.Pavelslavovich.webprak.dao.ServiceDao;
import ru.Pavelslavovich.webprak.model.Client;
import ru.Pavelslavovich.webprak.model.ContractStatus;
import ru.Pavelslavovich.webprak.model.ServiceContract;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ServiceContractDao contractDao;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ServiceDao serviceDao;
    @Autowired
    private EmployeeDao employeeDao;

    @GetMapping
    public String list(@RequestParam(value = "clientId", required = false) Long clientId,
                       @RequestParam(value = "serviceId", required = false) Long serviceId,
                       @RequestParam(value = "employeeId", required = false) Long employeeId,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "fromDate", required = false) String fromDate,
                       @RequestParam(value = "toDate", required = false) String toDate,
                       Model model) {
        List<ServiceContract> contracts;
        boolean hasFilter = clientId != null || serviceId != null || employeeId != null
                || (status != null && !status.isEmpty())
                || (fromDate != null && !fromDate.isEmpty())
                || (toDate != null && !toDate.isEmpty());
        if (hasFilter) {
            ContractStatus cs = (status != null && !status.isEmpty()) ? ContractStatus.valueOf(status) : null;
            LocalDate from = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate) : null;
            LocalDate to = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate) : null;
            contracts = contractDao.findByFilters(clientId, serviceId, employeeId, cs, from, to);
        } else {
            contracts = contractDao.findAllWithDetails();
        }
        model.addAttribute("contracts", contracts);
        return "contracts";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Optional<ServiceContract> opt = contractDao.findByIdFull(id);
        if (opt.isEmpty()) return "redirect:/contracts";
        model.addAttribute("contract", opt.get());
        return "contract";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("isNew", true);
        model.addAttribute("clients", clientDao.findAll());
        model.addAttribute("services", serviceDao.findAll());
        model.addAttribute("employees", employeeDao.findAll());
        model.addAttribute("statuses", ContractStatus.values());
        return "contractEdit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Optional<ServiceContract> opt = contractDao.findByIdFull(id);
        if (opt.isEmpty()) return "redirect:/contracts";
        model.addAttribute("contract", opt.get());
        model.addAttribute("isNew", false);
        model.addAttribute("clients", clientDao.findAll());
        model.addAttribute("services", serviceDao.findAll());
        model.addAttribute("employees", employeeDao.findAll());
        model.addAttribute("statuses", ContractStatus.values());
        return "contractEdit";
    }

    @PostMapping
    public String create(HttpServletRequest request) {
        String contractNumber = request.getParameter("contractNumber");
        Long clientId = Long.parseLong(request.getParameter("clientId"));
        Long serviceId = Long.parseLong(request.getParameter("serviceId"));
        LocalDate signedOn = LocalDate.parse(request.getParameter("signedOn"));
        LocalDate serviceStart = LocalDate.parse(request.getParameter("serviceStart"));
        String endStr = request.getParameter("serviceEnd");
        LocalDate serviceEnd = (endStr != null && !endStr.isEmpty()) ? LocalDate.parse(endStr) : null;
        ContractStatus status = ContractStatus.valueOf(request.getParameter("status"));
        BigDecimal agreedCost = new BigDecimal(request.getParameter("agreedCost"));
        String comment = request.getParameter("comment");

        Client client = clientDao.findById(clientId).orElse(null);
        ServiceEntity service = serviceDao.findById(serviceId).orElse(null);
        if (client == null || service == null) return "redirect:/contracts";

        ServiceContract contract = new ServiceContract(contractNumber, client, service,
                signedOn, serviceStart, serviceEnd, status, agreedCost, comment);

        List<ServiceContractDao.EmployeeRole> employees = parseEmployees(request);
        contractDao.registerContractWithEmployees(contract, employees);
        return "redirect:/contracts/" + contract.getId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, HttpServletRequest request) {
        String contractNumber = request.getParameter("contractNumber");
        Long clientId = Long.parseLong(request.getParameter("clientId"));
        Long serviceId = Long.parseLong(request.getParameter("serviceId"));
        LocalDate signedOn = LocalDate.parse(request.getParameter("signedOn"));
        LocalDate serviceStart = LocalDate.parse(request.getParameter("serviceStart"));
        String endStr = request.getParameter("serviceEnd");
        LocalDate serviceEnd = (endStr != null && !endStr.isEmpty()) ? LocalDate.parse(endStr) : null;
        ContractStatus status = ContractStatus.valueOf(request.getParameter("status"));
        BigDecimal agreedCost = new BigDecimal(request.getParameter("agreedCost"));
        String comment = request.getParameter("comment");

        List<ServiceContractDao.EmployeeRole> employees = parseEmployees(request);
        ServiceContract updated = contractDao.updateFull(id, contractNumber, clientId, serviceId,
                signedOn, serviceStart, serviceEnd, status, agreedCost, comment, employees);
        if (updated == null) return "redirect:/contracts";
        return "redirect:/contracts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        contractDao.deleteById(id);
        return "redirect:/contracts";
    }

    private List<ServiceContractDao.EmployeeRole> parseEmployees(HttpServletRequest request) {
        List<ServiceContractDao.EmployeeRole> employees = new ArrayList<>();
        String countStr = request.getParameter("employeeCount");
        int count = (countStr != null && !countStr.isEmpty()) ? Integer.parseInt(countStr) : 0;
        for (int i = 0; i < count; i++) {
            String empIdStr = request.getParameter("employee_id_" + i);
            if (empIdStr == null || empIdStr.isBlank()) continue;
            String role = request.getParameter("employee_role_" + i);
            employees.add(new ServiceContractDao.EmployeeRole(Long.parseLong(empIdStr), role));
        }
        return employees;
    }
}
