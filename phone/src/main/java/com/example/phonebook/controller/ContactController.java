package com.example.phonebook.controller;

import com.example.phonebook.entity.Contact;
import com.example.phonebook.repository.ContactRepository;
import com.example.phonebook.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/contact")
public class ContactController {
    
    @Autowired
    private ContactRepository contactRepository;
    
    @GetMapping("/list")
    public String index(Model model) {
        List<Contact> contacts = contactRepository.findAllOrderByFavoriteAndCreateTime();
        model.addAttribute("contacts", contacts);
        return "index";
    }
    
    @PostMapping
    @ResponseBody
    public void save(@RequestBody Contact contact) {
        if (contact.getPhones() != null) {
            contact.getPhones().forEach(phone -> {
                phone.setContact(contact);
            });
        }
        contactRepository.save(contact);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public void delete(@PathVariable Long id) {
        contactRepository.deleteById(id);
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Contact getById(@PathVariable Long id) {
        return contactRepository.findById(id).orElse(null);
    }
    
    @PostMapping("/{id}/favorite")
    @ResponseBody
    public void toggleFavorite(@PathVariable Long id) {
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null) {
            contactRepository.updateFavoriteStatus(id, !contact.getIsFavorite());
        }
    }
    
    @PostMapping("/{id}/update")
    @ResponseBody
    public void update(@PathVariable Long id, @RequestBody Contact contact) {
        contact.setId(id);
        
        // 获取原有联系人信息
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("联系人不存在"));
        
        // 更新基本信息
        existingContact.setName(contact.getName());
        existingContact.setEmail(contact.getEmail());
        existingContact.setAddress(contact.getAddress());
        existingContact.setWechat(contact.getWechat());
        existingContact.setQq(contact.getQq());
        existingContact.setWeibo(contact.getWeibo());
        
        // 清除原有电话
        existingContact.getPhones().clear();
        
        // 添加新电话
        if (contact.getPhones() != null) {
            contact.getPhones().forEach(phone -> {
                phone.setContact(existingContact);
                existingContact.getPhones().add(phone);
            });
        }
        
        contactRepository.save(existingContact);
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts() {
        try {
            List<Contact> contacts = contactRepository.findAllOrderByFavoriteAndCreateTime();
            byte[] excelContent = ExcelUtil.exportToExcel(contacts);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "contacts.xlsx");
            
            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/import")
    @ResponseBody
    public String importContacts(@RequestParam("file") MultipartFile file) {
        try {
            List<Contact> contacts = ExcelUtil.importFromExcel(file);
            contacts.forEach(contact -> {
                if (contact.getPhones() != null) {
                    contact.getPhones().forEach(phone -> phone.setContact(contact));
                }
            });
            contactRepository.saveAll(contacts);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<Contact> search(@RequestParam String keyword) {
        return contactRepository.searchContacts(keyword);
    }
    
    @DeleteMapping("/batch")
    @ResponseBody
    public void batchDelete(@RequestBody List<Long> ids) {
        contactRepository.deleteByIdIn(ids);
    }
} 