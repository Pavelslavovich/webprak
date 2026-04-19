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
import ru.Pavelslavovich.webprak.dao.EmployeeDao;
import ru.Pavelslavovich.webprak.model.ContactMethodType;
import ru.Pavelslavovich.webprak.model.Employee;
import ru.Pavelslavovich.webprak.model.EmployeeContactMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;

    @GetMapping
    public String list(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Employee> employees;
        if (query != null && !query.isEmpty()) {
            employees = employeeDao.search(query);
        } else {
            employees = employeeDao.findAll();
        }
        model.addAttribute("employees", employees);
        return "employees";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Optional<Employee> opt = employeeDao.findByIdFull(id);
        if (opt.isEmpty()) return "redirect:/employees";
        model.addAttribute("employee", opt.get());
        return "employee";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("isNew", true);
        return "employeeEdit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Optional<Employee> opt = employeeDao.findByIdFull(id);
        if (opt.isEmpty()) return "redirect:/employees";
        model.addAttribute("employee", opt.get());
        model.addAttribute("isNew", false);
        return "employeeEdit";
    }

    @PostMapping
    public String create(HttpServletRequest request) {
        String fullName = request.getParameter("fullName");
        String position = request.getParameter("position");
        String education = request.getParameter("education");
        String homeAddress = request.getParameter("homeAddress");
        String note = request.getParameter("note");
        Employee employee = new Employee(fullName, position, education, homeAddress, note);
        List<EmployeeDao.ContactMethodData> methods = parseMethods(request);
        for (EmployeeDao.ContactMethodData md : methods) {
            employee.addContactMethod(new EmployeeContactMethod(md.type(), md.value(), md.primary()));
        }
        employeeDao.save(employee);
        return "redirect:/employees/" + employee.getId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, HttpServletRequest request) {
        String fullName = request.getParameter("fullName");
        String position = request.getParameter("position");
        String education = request.getParameter("education");
        String homeAddress = request.getParameter("homeAddress");
        String note = request.getParameter("note");
        List<EmployeeDao.ContactMethodData> methods = parseMethods(request);
        Employee updated = employeeDao.updateFull(id, fullName, position, education, homeAddress, note, methods);
        if (updated == null) return "redirect:/employees";
        return "redirect:/employees/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes attrs) {
        if (employeeDao.hasContracts(id)) {
            attrs.addFlashAttribute("error", "Невозможно удалить служащего: есть связанные договоры");
            return "redirect:/employees/" + id;
        }
        employeeDao.deleteById(id);
        return "redirect:/employees";
    }

    private List<EmployeeDao.ContactMethodData> parseMethods(HttpServletRequest request) {
        List<EmployeeDao.ContactMethodData> methods = new ArrayList<>();
        String countStr = request.getParameter("methodCount");
        int methodCount = (countStr != null && !countStr.isEmpty()) ? Integer.parseInt(countStr) : 0;
        for (int i = 0; i < methodCount; i++) {
            String mType = request.getParameter("method_type_" + i);
            String mValue = request.getParameter("method_value_" + i);
            if (mValue == null || mValue.isBlank()) continue;
            String mPrimary = request.getParameter("method_primary_" + i);
            methods.add(new EmployeeDao.ContactMethodData(
                    ContactMethodType.valueOf(mType), mValue, "on".equals(mPrimary)));
        }
        return methods;
    }
}
