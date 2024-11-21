package com.epam.edp.demo.controller;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportsController {

    @Autowired
    private DynamoDB dynamoDB;

    @GetMapping("/reports")
    public String getReports(Model model) {
        Table table = dynamoDB.getTable("reports_db");

        // Scan the table and retrieve all items
        Iterator<Item> items = table.scan().iterator();
        List<Item> reportItems = new ArrayList<>();

        while (items.hasNext()) {
            reportItems.add(items.next());
        }

        // Add the items to the model, accessible in the Thymeleaf template
        model.addAttribute("reports", reportItems);

        // Return the Thymeleaf template name (e.g., reports.html)
        return "reports";
    }

}
