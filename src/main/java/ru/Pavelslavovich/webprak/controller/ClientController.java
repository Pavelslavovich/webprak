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
import ru.Pavelslavovich.webprak.model.Client;
import ru.Pavelslavovich.webprak.model.ClientContact;
import ru.Pavelslavovich.webprak.model.ClientContactMethod;
import ru.Pavelslavovich.webprak.model.ClientType;
import ru.Pavelslavovich.webprak.model.ContactMethodType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientDao clientDao;

    @GetMapping
    public String list(@RequestParam(value = "nameQuery", required = false) String nameQuery,
                       @RequestParam(value = "type", required = false) String type,
                       @RequestParam(value = "serviceId", required = false) Long serviceId,
                       @RequestParam(value = "fromDate", required = false) String fromDate,
                       @RequestParam(value = "toDate", required = false) String toDate,
                       @RequestParam(value = "employeeId", required = false) Long employeeId,
                       Model model) {
        List<Client> clients;
        boolean hasContractFilter = serviceId != null || employeeId != null
                || (fromDate != null && !fromDate.isEmpty())
                || (toDate != null && !toDate.isEmpty());
        if (hasContractFilter) {
            LocalDate from = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate) : LocalDate.of(1900, 1, 1);
            LocalDate to = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate) : LocalDate.of(2100, 1, 1);
            clients = clientDao.findByServiceInPeriodAndEmployee(serviceId, from, to, employeeId);
        } else if ((nameQuery != null && !nameQuery.isEmpty()) || (type != null && !type.isEmpty())) {
            ClientType ct = (type != null && !type.isEmpty()) ? ClientType.valueOf(type) : null;
            String nq = (nameQuery != null && !nameQuery.isEmpty()) ? nameQuery : null;
            clients = clientDao.search(nq, ct);
        } else {
            clients = clientDao.findAll();
        }
        model.addAttribute("clients", clients);
        return "clients";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Optional<Client> opt = clientDao.findByIdFull(id);
        if (opt.isEmpty()) return "redirect:/clients";
        model.addAttribute("client", opt.get());
        return "client";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("isNew", true);
        return "clientEdit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Optional<Client> opt = clientDao.findByIdFull(id);
        if (opt.isEmpty()) return "redirect:/clients";
        model.addAttribute("client", opt.get());
        model.addAttribute("isNew", false);
        return "clientEdit";
    }

    @PostMapping
    public String create(HttpServletRequest request) {
        Client client = buildClientFromRequest(request);
        clientDao.save(client);
        return "redirect:/clients/" + client.getId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, HttpServletRequest request) {
        List<ClientDao.ContactData> contacts = parseContacts(request);
        String typeStr = request.getParameter("clientType");
        String displayName = request.getParameter("displayName");
        String note = request.getParameter("note");
        Client updated = clientDao.updateFull(id, ClientType.valueOf(typeStr), displayName, note, contacts);
        if (updated == null) return "redirect:/clients";
        return "redirect:/clients/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes attrs) {
        if (clientDao.hasContracts(id)) {
            attrs.addFlashAttribute("error", "Невозможно удалить клиента: есть связанные договоры");
            return "redirect:/clients/" + id;
        }
        clientDao.deleteById(id);
        return "redirect:/clients";
    }

    private Client buildClientFromRequest(HttpServletRequest request) {
        ClientType type = ClientType.valueOf(request.getParameter("clientType"));
        String name = request.getParameter("displayName");
        String note = request.getParameter("note");
        Client client = new Client(type, name, note);
        List<ClientDao.ContactData> contacts = parseContacts(request);
        for (ClientDao.ContactData cd : contacts) {
            ClientContact cc = new ClientContact(cd.fullName(), cd.role(), cd.comment());
            for (ClientDao.MethodData md : cd.methods()) {
                cc.addMethod(new ClientContactMethod(md.type(), md.value(), md.primary()));
            }
            client.addContact(cc);
        }
        return client;
    }

    private List<ClientDao.ContactData> parseContacts(HttpServletRequest request) {
        List<ClientDao.ContactData> contacts = new ArrayList<>();
        String countStr = request.getParameter("contactCount");
        int contactCount = (countStr != null && !countStr.isEmpty()) ? Integer.parseInt(countStr) : 0;
        for (int i = 0; i < contactCount; i++) {
            String cName = request.getParameter("contact_name_" + i);
            if (cName == null || cName.isBlank()) continue;
            String cRole = request.getParameter("contact_role_" + i);
            String cComment = request.getParameter("contact_comment_" + i);
            List<ClientDao.MethodData> methods = new ArrayList<>();
            String mcStr = request.getParameter("contact_" + i + "_methodCount");
            int methodCount = (mcStr != null && !mcStr.isEmpty()) ? Integer.parseInt(mcStr) : 0;
            for (int j = 0; j < methodCount; j++) {
                String mType = request.getParameter("contact_" + i + "_mtype_" + j);
                String mValue = request.getParameter("contact_" + i + "_mvalue_" + j);
                if (mValue == null || mValue.isBlank()) continue;
                String mPrimary = request.getParameter("contact_" + i + "_mprimary_" + j);
                methods.add(new ClientDao.MethodData(
                        ContactMethodType.valueOf(mType), mValue, "on".equals(mPrimary)));
            }
            contacts.add(new ClientDao.ContactData(cName, cRole, cComment, methods));
        }
        return contacts;
    }
}
