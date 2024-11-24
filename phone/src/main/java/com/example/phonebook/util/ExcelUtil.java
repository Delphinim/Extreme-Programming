package com.example.phonebook.util;

import com.example.phonebook.entity.Contact;
import com.example.phonebook.entity.ContactPhone;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {
    
    public static byte[] exportToExcel(List<Contact> contacts) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("联系人列表");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("姓名");
            headerRow.createCell(1).setCellValue("电话号码");
            headerRow.createCell(2).setCellValue("电话类型");
            headerRow.createCell(3).setCellValue("电话描述");
            headerRow.createCell(4).setCellValue("邮箱");
            headerRow.createCell(5).setCellValue("地址");
            headerRow.createCell(6).setCellValue("微信");
            headerRow.createCell(7).setCellValue("QQ");
            headerRow.createCell(8).setCellValue("微博");
            headerRow.createCell(9).setCellValue("是否收藏");
            
            int rowNum = 1;
            for (Contact contact : contacts) {
                if (contact.getPhones() == null || contact.getPhones().isEmpty()) {
                    Row row = sheet.createRow(rowNum++);
                    fillContactData(row, contact, null);
                } else {
                    for (ContactPhone phone : contact.getPhones()) {
                        Row row = sheet.createRow(rowNum++);
                        fillContactData(row, contact, phone);
                    }
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private static void fillContactData(Row row, Contact contact, ContactPhone phone) {
        row.createCell(0).setCellValue(contact.getName());
        row.createCell(1).setCellValue(phone != null ? phone.getPhone() : "");
        row.createCell(2).setCellValue(phone != null ? phone.getType() : "");
        row.createCell(3).setCellValue(phone != null ? phone.getDescription() : "");
        row.createCell(4).setCellValue(contact.getEmail() != null ? contact.getEmail() : "");
        row.createCell(5).setCellValue(contact.getAddress() != null ? contact.getAddress() : "");
        row.createCell(6).setCellValue(contact.getWechat() != null ? contact.getWechat() : "");
        row.createCell(7).setCellValue(contact.getQq() != null ? contact.getQq() : "");
        row.createCell(8).setCellValue(contact.getWeibo() != null ? contact.getWeibo() : "");
        row.createCell(9).setCellValue(contact.getIsFavorite() != null && contact.getIsFavorite() ? "是" : "否");
    }
    
    public static List<Contact> importFromExcel(MultipartFile file) throws IOException {
        List<Contact> contacts = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            // 跳过标题行
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            Contact currentContact = null;
            String currentName = null;
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String name = getCellValue(row.getCell(0));
                
                if (!name.equals(currentName)) {
                    currentContact = new Contact();
                    currentContact.setName(name);
                    currentContact.setEmail(getCellValue(row.getCell(4)));
                    currentContact.setAddress(getCellValue(row.getCell(5)));
                    currentContact.setWechat(getCellValue(row.getCell(6)));
                    currentContact.setQq(getCellValue(row.getCell(7)));
                    currentContact.setWeibo(getCellValue(row.getCell(8)));
                    currentContact.setIsFavorite("是".equals(getCellValue(row.getCell(9))));
                    contacts.add(currentContact);
                    currentName = name;
                }
                
                String phone = getCellValue(row.getCell(1));
                if (!phone.isEmpty()) {
                    ContactPhone contactPhone = new ContactPhone();
                    contactPhone.setPhone(phone);
                    contactPhone.setType(getCellValue(row.getCell(2)));
                    contactPhone.setDescription(getCellValue(row.getCell(3)));
                    contactPhone.setContact(currentContact);
                    currentContact.getPhones().add(contactPhone);
                }
            }
        }
        
        return contacts;
    }
    
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }
} 